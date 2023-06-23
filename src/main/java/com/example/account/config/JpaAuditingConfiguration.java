package com.example.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@Configuration
@EnableJpaAuditing  //이 클래스 자체가 스프링 어플리케이션 쓸 때 오토 스캔이 되는 타입이 된다????
public class JpaAuditingConfiguration {
}
