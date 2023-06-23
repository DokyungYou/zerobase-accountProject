package com.example.account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class LocalRedisConfig {
    @Value("${spring.redis.port}") //redis 를 띄워줄 port 를 이 경로에 넣어줄 것임
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis(){ 
        redisServer = new RedisServer(redisPort);
        redisServer.start(); //객체 하나를 만들어서 시작시키는것
    }

    //우리 어플리케이션이 죽을때 얘만 떠있으면 안되니까 같이 보내줘야함
    @PreDestroy
    public void stopRedis(){
        if(redisServer != null){
        redisServer.stop();
        }
    }
}
