package com.hazelcast.factory;

import com.hazelcast.model.Person;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class PersonSerializationFactory implements DataSerializableFactory {
	public static final int FACTORY_ID = 1;

	@Override
	public IdentifiedDataSerializable create(int classId) {
		if (classId == Person.CLASS_ID) {
			return new Person();
		}
		return null;
	}

}

