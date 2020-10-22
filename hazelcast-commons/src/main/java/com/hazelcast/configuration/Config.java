package com.hazelcast.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties("hzconfig")
public class Config {

    public HazelcastConfig hazelcast;

    @Getter
    @Setter
    public static class HazelcastConfig {

        public List<String> serverIps;
        public boolean reDoOperation = true;
        public int connectionTimeout = 5000;
        public int connectionAttemptPeriod = 5000;
        public int connectionAttemptLimit = 2147483647;

    }
}
