package presignedUrl.infrastructure;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import presignedUrl.application.port.StorageServiceSpec;
import presignedUrl.domain.exception.PresignedUrlException;
import presignedUrl.infrastructure.config.AppConfig;

import java.net.URL;
import java.util.Date;

public class StorageService implements StorageServiceSpec {
    private final AmazonS3 s3Client;
    private final String bucketName;

    public StorageService() {
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
            Date expiration = getDate((long) expirationMinutes);
            GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(fileKey, expiration);
            generatePresignedUrlRequest.setContentType("video/mp4");
            return s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        } catch (SdkClientException e) {
            throw new PresignedUrlException("Failed to generate presigned URL", e);
        }
    }

    private static Date getDate(long expirationMinutes) {
        return new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000);
    }

    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String fileKey, Date expiration) {
        return new GeneratePresignedUrlRequest(bucketName, fileKey, HttpMethod.PUT).withExpiration(expiration);
    }
}