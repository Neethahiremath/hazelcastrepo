package com.storagenode.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties("hazelcast")
@Getter
@Setter
public class HazelcastBaseConfig {

    private Integer port;
    private String instanceName;
    private String clusterMembers;
    private Boolean restClientEnabled = false;
    private Integer operationCallTimeout = 30000;
    private Integer initialMinClusterSize = 0;
    private Integer partitionCount = 271;
    private Integer operationThreadCount = 8;
    private Integer maxJoinSeconds = 300;
    private Integer heartBeatIntervalSeconds = 15;
    private Integer maxNoHeartBeatSeconds = 240;
    private Integer memberListPublishIntervalSeconds = 60;
    private Integer masterConfirmationIntervalSeconds = 30;
    private String operationResponseQueueIdlestrategy;
    private Boolean backPressureEnabled = true;
    private Integer backPressurePartition = 200;
    private Integer clientEngineQueryCount = 20;
    private Integer clientEndpointRemoveDelay = 20;
    private Boolean diagnosticsEnabled = true;
    private String diagnosticsDirectory;
    private String diagnosticsFilename;
    private Integer eventQueueCapacity = 3000000;
    private Integer writeBehindQueueCapacity = 15000;
    private Integer ioThreadCount = 3;
    private Boolean shutdownHookEnabled = false;
    private Integer statisticsPrintDuration;
    private ManCenter manCenter;
    private Map<String, MapConfig> mapConfig;
    private ManCenter consulDiscovery;

    @Getter
    @Setter
    public static class ManCenter {
        private Boolean enabled = false;
        private String url;
    }

    @Getter
    @Setter
    public static class MapConfig {

        private Integer backupCount = 0;
        private String evictionPolicy = "LFU";
        private Integer timeToLive = 0;
        private Integer writeDelaySeconds = 5;
        private Integer maxEntriesPerNode = 500000;
        private Boolean writeToCassandra = false;

    }
}
