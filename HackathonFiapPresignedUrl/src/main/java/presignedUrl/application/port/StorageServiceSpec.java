package presignedUrl.application.port;

import java.net.URL;

public interface StorageServiceSpec {
    URL generatePresignedUrl(String fileKey, int expirationMinutes);
}