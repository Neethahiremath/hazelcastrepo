package com.storagenode.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
@ConfigurationProperties("cassandra-config")
class CassandraBaseConfig {

    private String[] contactPoints;
    private Integer port;
    private String keyspaceName;
    private String userName;
    private String password;
    private String dataCenter;

}
