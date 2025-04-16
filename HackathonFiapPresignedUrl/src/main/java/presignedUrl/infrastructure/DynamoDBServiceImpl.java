package presignedUrl.infrastructure;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import presignedUrl.application.port.DynamoDBService;
import presignedUrl.domain.exception.PresignedUrlException;
import presignedUrl.domain.model.UploadRecord;

import java.util.HashMap;
import java.util.Map;

public class DynamoDBServiceImpl implements DynamoDBService {
    private final AmazonDynamoDB dynamoDBClient;
    private static final String TABLE_NAME = "fiap-hackaton-uploads";

    public DynamoDBServiceImpl() {
        this.dynamoDBClient = AmazonDynamoDBClientBuilder.standard().build();
    }

    @Override
    public void saveUploadRecord(UploadRecord record) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("id", new AttributeValue(record.getId()));
            item.put("email", new AttributeValue(record.getEmail()));
            item.put("status_upload", new AttributeValue(record.getStatus()));
            item.put("data_criacao", new AttributeValue(record.getCreatedAt().toString()));
            item.put("url_download", new AttributeValue("https://fake-url.com/download/"));
            PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName(TABLE_NAME)
                .withItem(item);

            dynamoDBClient.putItem(putItemRequest);
        } catch (Exception e) {
            throw new PresignedUrlException("Failed to save upload record to DynamoDB", e);
        }
    }
}