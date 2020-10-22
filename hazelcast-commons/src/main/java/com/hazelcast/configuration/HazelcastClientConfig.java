package com.hazelcast.configuration;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientConnectionStrategyConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.client.spi.properties.ClientProperty;
import com.hazelcast.config.*;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.factory.PersonSerializationFactory;
import com.hazelcast.factory.YamlPropertySourceFactory;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.CollectionUtils;

@Configuration
@ConditionalOnExpression("${hazelcast.enabled:false}")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:hazelcast/${hazelcast.client.config:client}.yml")
@Slf4j
@ToString
public class HazelcastClientConfig {

    @Value("${hazelcast.client.heartbeat.timeout:30000}")
    private String heartBeatTimout;

    @Value("${hazelcast.client.heartbeat.interval:1000}")
    private String heartBeatinterval;

    @Value("${hazelcast.client.allow.invocations.when.disconnected:true}")
    private boolean isAllowInvocations;

    @Value("${hazelcast.client.statistics.enabled:true}")
    private boolean isStatisticsEnabled;

    @Value("${hazelcast.client.responsequeue.idlestrategy:backoff}")
    private String idleStrategy;

    @Value("${hazelcast.client.invocation.timeout.seconds:10}")
    private String invocationTimeout;

    @Value("${hazelcast.logging.type:slf4j}")
    private String loggingType;

    @Value("${hazelcast.client.max.concurrent.invocations:2147483647}")
    private String concurrentInvocations;

    @Value("${hazelcast.client.event.queue.capacity:1000000}")
    private String eventQueueCap;

    @Autowired
    private Config config;

    @Bean
    public ClientConfig getHazelcastClientConfig() throws Exception {
        try {
            ClientConfig clientConfig = new ClientConfig();

            clientConfig.setProperty(ClientProperty.HEARTBEAT_TIMEOUT.getName(), heartBeatTimout);
            clientConfig.setProperty(ClientProperty.HEARTBEAT_INTERVAL.getName(), heartBeatinterval);
            clientConfig.setProperty(ClientProperty.INVOCATION_TIMEOUT_SECONDS.getName(), invocationTimeout);
            clientConfig.getConnectionStrategyConfig().setReconnectMode(ClientConnectionStrategyConfig.ReconnectMode.ASYNC);

            GroupConfig groupConfig = new GroupConfig();
            groupConfig.setName("person_storage");
            clientConfig.setGroupConfig(groupConfig);

            ClientNetworkConfig networkConfig = new ClientNetworkConfig();

            networkConfig.setConnectionTimeout(config.getHazelcast().getConnectionTimeout());
            networkConfig.setConnectionAttemptPeriod(config.getHazelcast().connectionAttemptPeriod);
            networkConfig.setConnectionAttemptLimit(config.getHazelcast().connectionAttemptLimit);

            if (!CollectionUtils.isEmpty(config.getHazelcast().serverIps)) {
                networkConfig.setAddresses(config.getHazelcast().serverIps);
            }

            clientConfig.setNetworkConfig(networkConfig);

            clientConfig.getSerializationConfig().addDataSerializableFactory(
                    PersonSerializationFactory.FACTORY_ID, new PersonSerializationFactory());

            return clientConfig;
        } catch (Exception ex) {
            log.error("HazelcastClientConfig.getHazelcastClientConfig() failed", ex);
            throw new Exception("HazelcastClientConfig.getHazelcastClientConfig() failed", ex);
        }
    }

    @Bean(destroyMethod = "shutdown")
    public HazelcastInstance getHazelcastClientInstance() throws Exception {
        return HazelcastClient.newHazelcastClient(getHazelcastClientConfig());
    }
}
