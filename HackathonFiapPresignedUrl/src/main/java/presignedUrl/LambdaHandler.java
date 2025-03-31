package presignedUrl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String BUCKET_NAME = "bucket-fiap-hackaton-t32-files";
    private static final int EXPIRATION_TIME_IN_MINUTES = 10;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            String authorization = request.getHeaders().get("Authorization");
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return createResponse(401, "{\"error\":\"Unauthorized\"}");
            }

            String username = extractUsername(authorization.substring(7));
            String fileKey = String.format("e-mail/%s/%s.mp4", username, UUID.randomUUID());

            URL url = s3Client.generatePresignedUrl(
                    BUCKET_NAME,
                    fileKey,
                    new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MINUTES * 60 * 1000),
                    HttpMethod.PUT
            );

            return createResponse(200, String.format(
                    "{\"url\":\"%s\",\"fileKey\":\"%s\",\"maxFileSize\":%d}",
                    url,
                    fileKey,
                    MAX_FILE_SIZE
            ));

        } catch (Exception e) {
            return createResponse(500, String.format("{\"error\":\"%s\"}", e.getMessage()));
        }
    }

    private String extractUsername(String token) {
        try {
            String[] chunks = token.split("\\.");
            JsonNode jsonNode = objectMapper.readTree(new String(Base64.getDecoder().decode(chunks[1])));
            return jsonNode.has("username") ? jsonNode.get("username").asText() : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    private APIGatewayProxyResponseEvent createResponse(int statusCode, String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withBody(body)
                .withHeaders(Map.of("Content-Type", "application/json"));
    }
}