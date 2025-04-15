package presignedUrl.infrastructure.config;

public class AppConfig {
    private static final AppConfig INSTANCE = new AppConfig();
    
    private final String bucketName;
    private final int expirationTimeInMinutes;
    private final long maxFileSize;

    private AppConfig() {
        this.bucketName = getRequiredEnv();
        this.expirationTimeInMinutes = Integer.parseInt(
            getEnvWithDefault("EXPIRATION_TIME_IN_MINUTES", "10")
        );
        this.maxFileSize = Long.parseLong(
            getEnvWithDefault("MAX_FILE_SIZE", String.valueOf(10 * 1024 * 1024))
        );
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    private String getRequiredEnv() {
        String value = System.getenv("BUCKET_NAME");
        if (value == null || value.trim().isEmpty()) {
            return "bucket-fiap-hackaton-t32-files";
        }
        return value;
    }

    private String getEnvWithDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }

    public String getBucketName() {
        return bucketName;
    }

    public int getExpirationTimeInMinutes() {
        return expirationTimeInMinutes;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }
}