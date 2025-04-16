package presignedUrl;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import presignedUrl.application.port.StorageService;
import presignedUrl.application.port.DynamoDBService;
import presignedUrl.domain.exception.PresignedUrlException;
import presignedUrl.domain.model.PresignedUrlResponse;
import presignedUrl.domain.model.UploadRecord;
import presignedUrl.infrastructure.JwtService;
import presignedUrl.infrastructure.S3StorageService;
import presignedUrl.infrastructure.DynamoDBServiceImpl;
import presignedUrl.infrastructure.config.AppConfig;

import java.net.URL;
import java.time.Instant;
import java.util.*;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final StorageService storageService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final AppConfig config;
    private final DynamoDBService dynamoDBService;

    public LambdaHandler() {
        this.objectMapper = new ObjectMapper();
        this.storageService = new S3StorageService();
        this.jwtService = new JwtService(objectMapper);
        this.config = AppConfig.getInstance();
        this.dynamoDBService = new DynamoDBServiceImpl();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        LambdaLogger logger = context.getLogger();

        try {
            String authorization = Optional.ofNullable(request.getHeaders())
                .map(headers -> headers.get("Authorization"))
                .orElseThrow(() -> new PresignedUrlException("Missing Authorization header"));

            if (!authorization.startsWith("Bearer ")) {
                throw new PresignedUrlException("Invalid Authorization header format");
            }

            String username = jwtService.extractUsername(authorization.substring(7))
                .orElseThrow(() -> new PresignedUrlException("Invalid token"));

            UUID uuid = UUID.randomUUID();    
            String fileKey = generateFileKey(username, uuid);   
            URL presignedUrl = storageService.generatePresignedUrl(fileKey, config.getExpirationTimeInMinutes());
            PresignedUrlResponse response = new PresignedUrlResponse(presignedUrl, config.getMaxFileSize(), fileKey);

            UploadRecord record = new UploadRecord(
                uuid.toString(),
                username,
                "PENDING",
                Instant.now()
            );
            dynamoDBService.saveUploadRecord(record);

            return createSuccessResponse(response);

        } catch (PresignedUrlException e) {
            logger.log("Client error: " + e.getMessage());
            return createErrorResponse(401, "Unauthorized: " + e.getMessage());
        } catch (Exception e) {
            logger.log("Unexpected error: " + e.getMessage());
            return createErrorResponse(500, "Internal server error");
        }
    }

    private String generateFileKey(String username, UUID uuid) {
        return String.format("uploads/%s/%s.mp4", username, uuid.toString());
    }

    private APIGatewayProxyResponseEvent createSuccessResponse(PresignedUrlResponse response) {
        try {
            String jsonResponse = objectMapper.writeValueAsString(response);
            return createResponse(200, jsonResponse);
        } catch (Exception e) {
            throw new PresignedUrlException("Error creating response", e);
        }
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String message) {
        return createResponse(statusCode, String.format("{\"error\":\"%s\"}", message));
    }

    private APIGatewayProxyResponseEvent createResponse(int statusCode, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Headers", "Content-Type,Authorization");

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(body)
                .withHeaders(headers);
    }
}