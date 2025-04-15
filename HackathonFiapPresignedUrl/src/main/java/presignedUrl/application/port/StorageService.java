package presignedUrl.application.port;

import java.net.URL;

public interface StorageService {
    URL generatePresignedUrl(String fileKey, int expirationMinutes);
}