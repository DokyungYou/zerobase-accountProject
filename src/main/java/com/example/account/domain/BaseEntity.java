package com.example.account.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @Id  //pk 지정 어노테이션
    @GeneratedValue
    // 주로 @Id과 함께 사용 (엔티티 클래스의 주요 식별자(Primary Key) 값을 자동으로 생성하는 데 사용)
    private Long id;


    //createdAt & updatedAt 은 모든 테이블에 공통적으로 갖고있으면 좋다 (테이블의 메타정보)
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

}
