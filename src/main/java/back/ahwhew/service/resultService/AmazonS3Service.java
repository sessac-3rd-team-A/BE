package back.ahwhew.service.resultService;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadImageFromClient(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            // 고유한 파일 이름 생성
            String uniqueFileName = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());

            // S3에 업로드
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(new PutObjectRequest(bucketName, uniqueFileName, inputStream, metadata));

            // 업로드된 파일의 URL 생성
            return amazonS3.getUrl(bucketName, uniqueFileName).toString();
        } catch (Exception e) {
            log.error("Error uploading image to S3", e);
            throw new IOException("Error uploading image to S3", e);
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return fileName.substring(lastDotIndex);
        }
        return ""; // 파일 확장자가 없는 경우
    }


}