package com.hazelcast.model;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.factory.PersonSerializationFactory;
import lombok.*;

import java.io.IOException;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person implements IdentifiedDataSerializable {

    public static final int CLASS_ID = 1;
    private String personId;
    private String name;

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(personId);
        out.writeUTF(name);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        personId = in.readUTF();
        name = in.readUTF();
    }

    @Override
    public int getFactoryId() {
        return PersonSerializationFactory.FACTORY_ID;
    }

    @Override
    public int getId() {
        return CLASS_ID;
    }

}
