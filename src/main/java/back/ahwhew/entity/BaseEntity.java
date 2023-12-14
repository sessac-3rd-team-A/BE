package back.ahwhew.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@MappedSuperclass // 테이블로 생성되지 않도록 해주는 어노테이션
@EntityListeners(value = {AuditingEntityListener.class}) // AuditingEntityListener : JPA 내부에서 엔티티 객체가 생성/변경 되는것을 감지하는 역할
@Getter
abstract class BaseEntity {
    @CreatedDate
    @Column(name = "regdate",updatable = false) // updatable = false : 객체를 DB에 반영할 때 regdate 컬럼값은 변경되지 않는다
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name = "moddate")
    private LocalDateTime modDate;
}