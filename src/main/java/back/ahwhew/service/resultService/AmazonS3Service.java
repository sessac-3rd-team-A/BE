package back.ahwhew.service.resultService;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadImageFromBase64(String base64EncodedImage) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.decodeBase64(base64EncodedImage))) {
            // 고유한 파일 이름 생성
            String uniqueFileName = UUID.randomUUID().toString() + ".jpeg"; // 확장자는 필요에 따라 변경 가능

            // S3에 업로드
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(inputStream.available());
            amazonS3.putObject(new PutObjectRequest(bucketName, uniqueFileName, inputStream, metadata));

            // 업로드된 파일의 URL 생성
            return amazonS3.getUrl(bucketName, uniqueFileName).toString();
        } catch (Exception e) {
            log.error("S3에 이미지 업로드 중 오류 발생", e);
            throw new IOException("S3에 이미지 업로드 중 오류 발생", e);
        }
    }
}