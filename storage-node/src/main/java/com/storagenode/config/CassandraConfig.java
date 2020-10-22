package com.storagenode.config;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CassandraConfig {

    private Session session;
    private final CassandraBaseConfig cassandraBaseConfig;

    @Autowired
    public CassandraConfig(CassandraBaseConfig cassandraBaseConfig) {
        this.cassandraBaseConfig = cassandraBaseConfig;
    }

    @Bean(name = "cassandraSessionBean")
    public Session getSession() {
        connect();
        return session;
    }

    private void connect() {
        PoolingOptions poolingOptionsDetails = new PoolingOptions();
        poolingOptionsDetails.setMaxConnectionsPerHost(HostDistance.LOCAL, 3)
                .setMaxConnectionsPerHost(HostDistance.REMOTE, 2);

        SocketOptions socketOptions = new SocketOptions();
        socketOptions.setReadTimeoutMillis(5000);
        socketOptions.setConnectTimeoutMillis(5000);

        Cluster cluster = Cluster.builder().addContactPoints(cassandraBaseConfig.getContactPoints()).withPort(cassandraBaseConfig.getPort()).withoutJMXReporting()
                .withPoolingOptions(poolingOptionsDetails)
                .withLoadBalancingPolicy(DCAwareRoundRobinPolicy.builder().withLocalDc(cassandraBaseConfig.getDataCenter()).build())
                .withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM))
                .withSocketOptions(socketOptions).withAuthProvider(new PlainTextAuthProvider(cassandraBaseConfig.getUserName(), cassandraBaseConfig.getPassword()))
                .build();

        Metadata metadata = cluster.getMetadata();
        log.info("Cluster name: {}", metadata.getClusterName());
        log.info("PROTOCOL VERSION {}", cluster.getConfiguration().getProtocolOptions().getProtocolVersion());

        session = cluster.connect(cassandraBaseConfig.getKeyspaceName());

        for (Host host : metadata.getAllHosts()) {
            log.info("Datacenter: {}, Host: {}, Rack: {}", host.getDatacenter(), host.getAddress(), host.getRack());
        }
    }
}
