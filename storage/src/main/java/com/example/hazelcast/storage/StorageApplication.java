package com.example.hazelcast.storage;

import com.example.hazelcast.shared.map.MapNames;
import com.example.hazelcast.storage.listener.StorageEntryListener;
import com.example.hazelcast.storage.storage.VehiclesMapStore;
import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@SpringBootApplication
@EntityScan("com.example.hazelcast.shared.model")// it will scan your entity shared package and will be managed by Spring
@Configuration
public class StorageApplication {

    final static Logger logger = LoggerFactory.getLogger(StorageApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(StorageApplication.class, args);
	}

    @Bean(destroyMethod = "shutdown")
    public HazelcastInstance createStorageInstance(@Qualifier("StorageConfig") Config config) throws Exception{
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);

        instance.getMap(MapNames.VEHICLES_MAP);

        return  instance;
    }

	@Bean(name = "StorageConfig")
	public Config config(VehiclesMapStore vehiclesMapStore) throws Exception {
		Config config = new Config();

		MapStoreConfig vehicleMapStoreConfig = new MapStoreConfig();

		vehicleMapStoreConfig.setImplementation(vehiclesMapStore);

        MapConfig vehicleMapConfig = new MapConfig();

        vehicleMapConfig.setMapStoreConfig(vehicleMapStoreConfig);
		vehicleMapConfig.setName(MapNames.VEHICLES_MAP);
		vehicleMapConfig.addMapIndexConfig(
		        new MapIndexConfig("registrationDate",true)
        );
		vehicleMapConfig.addEntryListenerConfig(
		        new EntryListenerConfig(StorageEntryListener.class.getName(), false, false)
        );

		vehicleMapConfig.setBackupCount(2);

		config.addMapConfig(vehicleMapConfig);

		return config;
	}
}
