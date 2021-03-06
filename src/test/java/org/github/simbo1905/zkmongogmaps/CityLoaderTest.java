package org.github.simbo1905.zkmongogmaps;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionContaining.hasItem;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.github.simbo1905.zkmongogmaps.app.Zipcode;
import org.github.simbo1905.zkmongogmaps.data.ZipcodeLoader;
import org.junit.Assert;
import org.junit.Test;

public class CityLoaderTest {

	//{"city": "ACMAR", "loc": [-86.51557, 33.584132], "pop": 6055, "state": "AL", "_id": "35004"}
	private final Zipcode ACMAR = new Zipcode("35004", "ACMAR", "AL", 6055, new double[]{-86.51557, 33.584132});
	
	//{"city": "NEW SITE", "loc": [-85.951086, 32.941445], "pop": 19942, "state": "AL", "_id": "35010"}
	private final Zipcode NEW_SITE = new Zipcode("35010", "NEW SITE", "AL", 19942, new double[]{-85.951086, 32.941445});
	
	ZipcodeLoader cityLoader = new ZipcodeLoader();
	
	@Test
	public void testCityLoader() throws Exception {
		// given 
		InputStream instream = new FileInputStream("src/test/resources/zips.json");
		// when
		List<Zipcode> cities = cityLoader.readJsonStream(instream);
		// then
		Assert.assertThat(cities.size(), is(5));
		Assert.assertThat(cities, hasItem(ACMAR));
		Assert.assertThat(cities, hasItem(NEW_SITE));
	}
	
}
