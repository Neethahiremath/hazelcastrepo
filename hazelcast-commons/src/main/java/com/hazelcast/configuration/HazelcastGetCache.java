package com.hazelcast.configuration;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.listener.PersonMapListener;
import com.hazelcast.model.Person;
import com.hazelcast.maps.MapNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HazelcastGetCache {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Bean(name = "personMap")
    public IMap<String, Person> getPersonMap() {
        IMap<String, Person> personMap = hazelcastInstance.getMap(MapNames.PERSON_MAP);
            personMap.addEntryListener(new PersonMapListener(), true);
        return personMap;
    }

}
