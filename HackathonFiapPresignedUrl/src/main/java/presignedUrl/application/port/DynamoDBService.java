package presignedUrl.application.port;

import presignedUrl.domain.model.UploadRecord;

public interface DynamoDBService {
    void saveUploadRecord(UploadRecord record);
}