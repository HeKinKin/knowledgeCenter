package org.sang;

import com.spring4all.mongodb.EnableMongoPlus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling//开启定时任务支持
@EnableMongoPlus
public class KnowledgeCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnowledgeCenterApplication.class, args);
    }
}
