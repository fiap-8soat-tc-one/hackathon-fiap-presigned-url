package presignedUrl.domain.model;

import java.net.URL;

public class PresignedUrlResponse {
    private final URL url;
    private final long maxFileSize;
    private final String fileKey;

    public PresignedUrlResponse(URL url, long maxFileSize, String fileKey) {
        this.url = url;
        this.maxFileSize = maxFileSize;
        this.fileKey = fileKey;
    }

    public URL getUrl() {
        return url;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public String getFileKey() {
        return fileKey;
    }
}