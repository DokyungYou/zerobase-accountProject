package com.example.account.domain;

import lombok.*;


import javax.persistence.Entity;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity  //클래스가 데이터베이스 테이블과 매핑되는 엔티티(Entity)임을 나타냄 (일종의 설정클래스, 자바 객체 X, 하나의 테이블을 만든 것) , 해당 클래스는 pk를 가지고있어야함
public class AccountUser extends BaseEntity {

    private String name;

}
