
package com.storagenode.config;

import com.hazelcast.config.*;
import com.hazelcast.config.MaxSizeConfig.MaxSizePolicy;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.factory.PersonSerializationFactory;
import com.hazelcast.maps.MapNames;
import com.storagenode.feign.ConsulAPI;
import com.storagenode.mapstore.PersonMapStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

@Configuration
@Slf4j
public class HazelcastConfig {

    private final HazelcastBaseConfig hazelcastBaseConfig;
    private final PersonMapStore personMapStore;
    private final ConsulAPI consulAPI;

    @Autowired
    public HazelcastConfig(HazelcastBaseConfig hazelcastBaseConfig, PersonMapStore personMapStore, ConsulAPI consulAPI) {
        this.hazelcastBaseConfig = hazelcastBaseConfig;
        this.personMapStore = personMapStore;
        this.consulAPI = consulAPI;
    }

    @Bean(destroyMethod = "shutdown")
    public HazelcastInstance createStorageNode() {
        Config config = new Config();
        config.getGroupConfig().setName("storage_node");
        config.setInstanceName(hazelcastBaseConfig.getInstanceName());
        config.setProperty("hazelcast.rest.enabled", String.valueOf(hazelcastBaseConfig.getRestClientEnabled()));
        config.setProperty("hazelcast.logging.type", "slf4j");
        config.setProperty("hazelcast.socket.bind.any", "false");
        config.setProperty("hazelcast.operation.call.timeout.millis", String.valueOf(hazelcastBaseConfig.getOperationCallTimeout()));
        config.setProperty("hazelcast.initial.min.cluster.size", String.valueOf(hazelcastBaseConfig.getInitialMinClusterSize()));
        config.setProperty("hazelcast.partition.count", String.valueOf(hazelcastBaseConfig.getPartitionCount()));
        config.setProperty("hazelcast.operation.thread.count", String.valueOf(hazelcastBaseConfig.getOperationThreadCount()));
        config.setProperty("hazelcast.max.join.seconds", String.valueOf(hazelcastBaseConfig.getMaxJoinSeconds()));
        config.setProperty("hazelcast.heartbeat.interval.seconds", String.valueOf(hazelcastBaseConfig.getHeartBeatIntervalSeconds()));
        config.setProperty("hazelcast.max.no.heartbeat.seconds", String.valueOf(hazelcastBaseConfig.getMaxNoHeartBeatSeconds()));
        config.setProperty("hazelcast.operation.responsequeue.idlestrategy", String.valueOf(hazelcastBaseConfig.getOperationResponseQueueIdlestrategy()));
        config.setProperty("hazelcast.backpressure.enabled", String.valueOf(hazelcastBaseConfig.getBackPressureEnabled()));
        config.setProperty("hazelcast.backpressure.max.concurrent.invocations.per.partition",
                String.valueOf(hazelcastBaseConfig.getBackPressurePartition()));
        config.setProperty("hazelcast.clientengine.query.thread.count", String.valueOf(hazelcastBaseConfig.getClientEngineQueryCount()));
        config.setProperty("hazelcast.client.endpoint.remove.delay.seconds", String.valueOf(hazelcastBaseConfig.getClientEndpointRemoveDelay()));
        config.setProperty("hazelcast.diagnostics.enabled", String.valueOf(hazelcastBaseConfig.getDiagnosticsEnabled()));
        config.setProperty("hazelcast.diagnostics.filename.prefix", String.valueOf(hazelcastBaseConfig.getDiagnosticsFilename()));
        config.setProperty("hazelcast.event.queue.capacity", String.valueOf(hazelcastBaseConfig.getEventQueueCapacity()));
        config.setProperty("hazelcast.map.write.behind.queue.capacity", String.valueOf(hazelcastBaseConfig.getWriteBehindQueueCapacity()));
        config.setProperty("hazelcast.io.thread.count", String.valueOf(hazelcastBaseConfig.getIoThreadCount()));
        config.setProperty("hazelcast.shutdownhook.enabled", String.valueOf(hazelcastBaseConfig.getShutdownHookEnabled()));
        config.setProperty("hazelcast.member.list.publish.interval.seconds", String.valueOf(hazelcastBaseConfig.getMemberListPublishIntervalSeconds()));
        config.setProperty("hazelcast.master.confirmation.interval.seconds",
                String.valueOf(hazelcastBaseConfig.getMasterConfirmationIntervalSeconds()));

        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setPortAutoIncrement(false);
        networkConfig.setPort(hazelcastBaseConfig.getPort() == null || hazelcastBaseConfig.getPort() <= 0 ? NetworkConfig.DEFAULT_PORT : hazelcastBaseConfig.getPort());

        if (hazelcastBaseConfig.getConsulDiscovery().getEnabled()) {
            hazelcastBaseConfig.setClusterMembers(getClusterMembersFromConsul());
            log.info("Cluster members are discovered using consul discovery. {}", hazelcastBaseConfig.getClusterMembers());
        }

        JoinConfig joinConfig = new JoinConfig();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getAwsConfig().setEnabled(false);
        TcpIpConfig tcpIpConfig = new TcpIpConfig();
        tcpIpConfig.addMember(hazelcastBaseConfig.getClusterMembers());
        tcpIpConfig.setEnabled(true);
        joinConfig.setTcpIpConfig(tcpIpConfig);
        networkConfig.setJoin(joinConfig);

        if (hazelcastBaseConfig.getManCenter().getEnabled()) {
            ManagementCenterConfig manCenterCfg = new ManagementCenterConfig();
            manCenterCfg.setEnabled(true).setUrl(hazelcastBaseConfig.getManCenter().getUrl());
            config.setManagementCenterConfig(manCenterCfg);
        }

        config.getSerializationConfig().addDataSerializableFactory(PersonSerializationFactory.FACTORY_ID,
                new PersonSerializationFactory());
        config.addMapConfig(getPersonMapConfig());

        config.setNetworkConfig(networkConfig);
        log.info("Config Values {}", config);
        return Hazelcast.newHazelcastInstance(config);
    }

    private String getClusterMembersFromConsul() {
        return consulAPI.getClusterMembers()
                .stream()
                .map(m -> m.get("Address").toString())
                .collect(Collectors.joining(","));
    }

    private MapConfig getPersonMapConfig() {
        HazelcastBaseConfig.MapConfig mapConfig = hazelcastBaseConfig.getMapConfig().get(MapNames.PERSON_MAP);
        MapConfig personMapConfig = new MapConfig();
        personMapConfig.setName(MapNames.PERSON_MAP);
        personMapConfig.setBackupCount(mapConfig.getBackupCount());
        personMapConfig.setEvictionPolicy(EvictionPolicy.valueOf(mapConfig.getEvictionPolicy()));
        personMapConfig.setTimeToLiveSeconds(mapConfig.getTimeToLive());
        MapStoreConfig omniIdSearchFlagConfig = new MapStoreConfig();
        omniIdSearchFlagConfig.setImplementation(personMapStore);
        omniIdSearchFlagConfig.setWriteDelaySeconds(mapConfig.getWriteDelaySeconds());
        personMapConfig.setMapStoreConfig(omniIdSearchFlagConfig);
        personMapConfig.setMaxSizeConfig(new MaxSizeConfig(mapConfig.getMaxEntriesPerNode(), MaxSizePolicy.PER_NODE));
        return personMapConfig;
    }

}
