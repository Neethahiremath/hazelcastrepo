package com.hazelcast.entryprocessor;

import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.model.Person;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class UpdatePersonProcessor implements Serializable, EntryProcessor<String, Person> {

    private static final long serialVersionUID = 1L;
    private final String name;

    public UpdatePersonProcessor(Person person) {
        this.name = person.getName();
    }

    @Override
    public Object process(Map.Entry<String, Person> entry) {
        Person person = entry.getValue();

        if (!Optional.ofNullable(person).isPresent()) {
            person = Person.builder().build();
        }
        Person updatedPerson = updatePerson(person);
        entry.setValue(updatedPerson);
        return person;
    }

    private Person updatePerson(Person person) {

        if (Optional.ofNullable(name).isPresent())
            person.setName(this.name);
        return person;
    }

    @Override
    public EntryBackupProcessor<String, Person> getBackupProcessor() {
        return null;
    }

}
