package com.storagenode.mapstore;

import com.hazelcast.core.MapStore;
import com.hazelcast.maps.MapNames;
import com.hazelcast.model.Person;
import com.storagenode.config.HazelcastBaseConfig;
import com.storagenode.dao.PersonDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service()
public class PersonMapStore implements MapStore<String, Person> {

    private final TaskExecutor taskExecutor;
    private final PersonDao personDao;

    @Autowired
    public PersonMapStore(TaskExecutor taskExecutor, PersonDao personDao) {
        this.taskExecutor = taskExecutor;
        this.personDao = personDao;
    }

    @Override
    public void store(String personId, Person person) {
        personDao.update(personId, person);
    }

    @Override
    public void storeAll(Map<String, Person> personMap) {

        List<CompletableFuture<Object>> allTasks = new ArrayList<>();
        for (Map.Entry<String, Person> key : personMap.entrySet()) {
            CompletableFuture<Object> singleCassandraCall = CompletableFuture.runAsync(() -> store(key.getKey(),
                    key.getValue()), taskExecutor).handle((res, ex) -> {
                if (ex != null) {
                    log.error("Exception occurred while fetching from Entry {} stack {}", key, ex);
                    return "Unknown!";
                }
                return res;
            });
            allTasks.add(singleCassandraCall);
        }

        CompletableFuture.allOf(allTasks.toArray(new CompletableFuture[0])).join();

    }

    @Override
    public void delete(String personId) {
        log.info("delete called key :{}", personId);
    }

    @Override
    public void deleteAll(Collection<String> personIds) {
        log.info("DeleteAll called keys :{}", personIds);
    }

    @Override
    public Person load(String PersonId) {
        return personDao.findById(PersonId);

    }

    @Override
    public Map<String, Person> loadAll(Collection<String> keys) {
        long start = System.currentTimeMillis();
        Map<String, Person> personMap = new ConcurrentHashMap<>();
        List<CompletableFuture<Object>> allTasks = new ArrayList<>();
        keys.forEach(key -> {
            CompletableFuture<Object> singleCassandraCall = CompletableFuture.runAsync(() -> personMap.put(key, load(key)), taskExecutor).handle((res, ex) -> {
                if (ex != null) {
                    log.error("Exception occured while fetching from Key {} stack {}", key, ex);
                    return "Unknown!";
                }
                return res;
            });
            allTasks.add(singleCassandraCall);
        });

        // wait to get results until all futures and completed
        CompletableFuture.allOf(allTasks.toArray(new CompletableFuture[0])).join();

        return personMap;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        return null;
    }
}
