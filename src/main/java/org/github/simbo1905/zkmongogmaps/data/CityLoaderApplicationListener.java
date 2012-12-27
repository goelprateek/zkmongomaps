package org.github.simbo1905.zkmongogmaps.data;

import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;

import org.github.simbo1905.zkmongogmaps.app.City;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.mongodb.MongoCollectionUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.stereotype.Component;

import com.mongodb.DBCollection;

/**
 * A class to make sure that the database is preloaded
 */
@Component
public class CityLoaderApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final int TEST_DATASET_SIZE = 29469;

	@Inject
	MongoOperations mongoOperations;
	
	public void createIndexedCityCollectionIfNotExist() {
		mongoOperations.indexOps(City.class).ensureIndex( new GeospatialIndex("loc") );
		mongoOperations.indexOps(City.class).ensureIndex( new Index("state", Order.ASCENDING) );
	}

	public void loadReferenceDataIfNotfound() {
		String collectionName = MongoCollectionUtils.getPreferredCollectionName(City.class);
		DBCollection collection = mongoOperations.getCollection(collectionName);
		long collectionSize = collection.getCount();
		if( collectionSize < TEST_DATASET_SIZE ){
			InputStream classpathInputStream = CityLoaderApplicationListener.class.getClassLoader().getResourceAsStream("zips.json.gz");
			CityLoader loader = new CityLoader();
			try {
				GZIPInputStream zipInputStream = new GZIPInputStream(classpathInputStream);
				System.out.println(String.format("Doing one time load of a lot of cities..."));
				List<City> cities = loader.readJsonStream(zipInputStream);
				for( City c: cities ){
					mongoOperations.save(c);
				}
				System.out.println(String.format("One time load complete"));
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException("cannot laod test data from WEB-INF/dataload/zips.json.gz");
			}
		}
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		createIndexedCityCollectionIfNotExist();
		loadReferenceDataIfNotfound();
	}	
}
