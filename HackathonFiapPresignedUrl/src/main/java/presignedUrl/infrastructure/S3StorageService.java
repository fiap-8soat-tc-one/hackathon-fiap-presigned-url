package presignedUrl.infrastructure;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import presignedUrl.application.port.StorageService;
import presignedUrl.domain.exception.PresignedUrlException;
import presignedUrl.infrastructure.config.AppConfig;

import java.net.URL;
import java.util.Date;

public class S3StorageService implements StorageService {
    private final AmazonS3 s3Client;
    private final String bucketName;

    public S3StorageService() {
        try {
            this.s3Client = AmazonS3ClientBuilder.standard().build();
            this.bucketName = AppConfig.getInstance().getBucketName();
        } catch (Exception e) {
            throw new PresignedUrlException("Failed to initialize S3 client", e);
        }
    }

    @Override
    public URL generatePresignedUrl(String fileKey, int expirationMinutes) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000);
            return s3Client.generatePresignedUrl(bucketName, fileKey, expiration, HttpMethod.PUT);
        } catch (SdkClientException e) {
            throw new PresignedUrlException("Failed to generate presigned URL", e);
        }
    }

    // Package private for testing
    AmazonS3 getS3Client() {
        return s3Client;
    }
}