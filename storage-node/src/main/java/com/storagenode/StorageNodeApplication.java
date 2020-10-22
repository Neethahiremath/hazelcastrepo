package com.storagenode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;

@SpringBootApplication
@Configuration
@Slf4j
@EnableFeignClients
public class StorageNodeApplication {
    public static void main(String[] args) {
        try {
            log.info("/etc/environment content: {}",
                    new String(Files.readAllBytes(Paths.get("/etc/environment"))));
        } catch (IOException e) {
			log.error("Couldn't read /etc/environment", e);
        }

        log.info("System Environment: {}", System.getenv());

        Environment env = new SpringApplication(StorageNodeApplication.class)
                .run(args).getEnvironment();

        log.info("App Environment: {}", env);

        log.info(MessageFormat.format("\n" +
                        "****************\n" +
                        "\tHazelcast: \t\thttp://{1}:{2}\n" +
                        "***************\n",
                env.getProperty("spring.application.name"),
                env.getProperty("LOCAL_IP", "localhost"),
                env.getProperty("hazelcast.port")));
    }
}
