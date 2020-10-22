package com.storagenode.config;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.maps.MapNames;
import com.hazelcast.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class StorageNodeConfig {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @PostConstruct
    public void logMapStatistics() {
        IMap<String, Person> personIMap = hazelcastInstance.getMap(MapNames.PERSON_MAP);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> log.info("STATS: personIMap {} ",
                personIMap.getLocalMapStats()), 1, 10, TimeUnit.SECONDS);
    }

}
