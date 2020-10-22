package com.hazelcast.listener;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryLoadedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapClearedListener;
import com.hazelcast.map.listener.MapEvictedListener;
import com.hazelcast.model.Person;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersonMapListener implements EntryAddedListener<String, Person>,EntryRemovedListener<String, Person>,
EntryUpdatedListener<String, Person>, EntryEvictedListener<String, Person>, EntryLoadedListener<String, Person>,
MapEvictedListener, MapClearedListener  {

	@Override
	public void mapCleared(MapEvent event) {
		log.debug("Entry Cleared: {}", event);
	}

	@Override
	public void mapEvicted(MapEvent event) {
		log.debug("Map Evicted: {}", event);
	}

	@Override
	public void entryLoaded(EntryEvent<String, Person> event) {
		log.debug("Entry Loaded: {}", event);
	}

	@Override
	public void entryEvicted(EntryEvent<String, Person> event) {
		log.debug("Entry Evicted: {}", event);
	}

	@Override
	public void entryUpdated(EntryEvent<String, Person> event) {
		log.debug("Entry Updated: {}", event);
	}

	@Override
	public void entryRemoved(EntryEvent<String, Person> event) {
		log.debug("Entry Removed: {}", event);
	}

	@Override
	public void entryAdded(EntryEvent<String, Person> event) {
		log.debug("Entry Added: {}", event);
	}

}
