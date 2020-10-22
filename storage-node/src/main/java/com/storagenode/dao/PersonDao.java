package com.storagenode.dao;

import com.datastax.driver.core.*;
import com.hazelcast.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository()
public class PersonDao {

    @Autowired
    @Qualifier("cassandraSessionBean")
    private Session session;

    @Autowired
    @Qualifier("updatePerson")
    private PreparedStatement updatePerson;

    @Autowired
    @Qualifier("findByKey")
    private PreparedStatement findByKey;

    public Person findById(String personId) {
        BoundStatement boundStatement = findByKey.bind()
                .setString("personId", personId);
        try {
            ResultSet resultSet = session.execute(boundStatement);
            Row row = resultSet.one();
            if (row != null) {
                return Person.builder()
                        .personId(row.getString("personId"))
                        .name(row.getString("name"))
                        .build();
            }
        } catch (Exception ex) {
            log.error("unable to find id");
            return Person.builder().build();
        }
        return Person.builder().build();
    }

    public void update(String personId, Person person) {

        BoundStatement boundStatement = updatePerson.bind()
                .setString("personId", personId);

        if (Optional.ofNullable(person.getName()).isPresent()) {
            boundStatement.setString("name", person.getName());
        }

        try {
            session.execute(boundStatement);
        } catch (Exception ex) {
            log.error("Unable to save", ex);
        }
        log.info("Updated");
    }

}
