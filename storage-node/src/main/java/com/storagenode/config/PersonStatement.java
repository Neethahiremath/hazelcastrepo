package com.storagenode.config;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PersonStatement {

    @Autowired
    private CassandraBaseConfig cassandraBaseConfig;

    @Autowired
    @Qualifier("cassandraSessionBean")
    private Session session;

    @Bean
    public PreparedStatement updatePerson() {

        String sb = "UPDATE " + cassandraBaseConfig.getKeyspaceName() + ".person " +
                "SET name=:name" +
                " WHERE personId = :personId;";
        return session.prepare(sb);
    }

    @Bean
    public PreparedStatement findByKey() {
        String sb = "SELECT * FROM " + cassandraBaseConfig.getKeyspaceName() + ".person " +
                "WHERE " + "personId=:personId;";
        return session.prepare(sb);
    }
}
