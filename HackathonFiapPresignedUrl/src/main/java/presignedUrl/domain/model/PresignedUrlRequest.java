package presignedUrl.domain.model;

public class PresignedUrlRequest {
    private final String username;
    private final String fileType;

    public PresignedUrlRequest(String username, String fileType) {
        this.username = username;
        this.fileType = fileType;
    }

    public String getUsername() {
        return username;
    }

    public String getFileType() {
        return fileType;
    }
}