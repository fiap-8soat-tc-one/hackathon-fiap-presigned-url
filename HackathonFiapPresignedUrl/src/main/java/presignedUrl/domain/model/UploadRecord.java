package presignedUrl.domain.model;

import java.time.Instant;

public class UploadRecord {
    private final String id;
    private final String email;
    private final String status;
    private final Instant createdAt;

    public UploadRecord(String id, String email, String status, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}