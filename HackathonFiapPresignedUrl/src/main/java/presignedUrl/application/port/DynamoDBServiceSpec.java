package presignedUrl.application.port;

import presignedUrl.domain.model.UploadRecord;

public interface DynamoDBServiceSpec {
    void saveUploadRecord(UploadRecord record);
}