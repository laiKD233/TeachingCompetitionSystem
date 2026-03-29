package com.teaching.competition;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.teaching.competition.mapper")
@EnableScheduling
public class TeachingCompetitionApplication {
    public static void main(String[] args) {
        SpringApplication.run(TeachingCompetitionApplication.class, args);
    }
}
