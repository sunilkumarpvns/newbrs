package com.elitecore.aaa.core.data;

import static junitparams.JUnitParamsRunner.$;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.NullLogger;

@RunWith(JUnitParamsRunner.class)
public class AdditionalResponseAttributesTest {

	@BeforeClass
	public static void init() {
		LogManager.setDefaultLogger(new NullLogger());
	}
	
	@Test
	@Parameters(method="dataFor_testParse")
	public void testParse(String additionalAttributeString, 
			AdditionalResponseAttributes expectedAdditionalResponseAttributes) {
		assertReflectionEquals(expectedAdditionalResponseAttributes, 
				new AdditionalResponseAttributes(additionalAttributeString), 
				ReflectionComparatorMode.LENIENT_ORDER);
	}

	public static Object[] dataFor_testParse(){
		return $(
				$("0:1 = xyz", 
						new AdditionalResponseAttributes(
							MapGenerator.fill(MapGenerator.createStringMap(), $("0:1", Arrays.asList("xyz"))), 
							MapGenerator.createStringMap(),
							MapGenerator.createStringMap(),
							MapGenerator.createAdditionalMap()
						)
				),
				$("0:25='poolname=IpPool', 0:25='poolid=1', 0:25='poolserialnumber=10'", 
						new AdditionalResponseAttributes(
						MapGenerator.fill(MapGenerator.createStringMap(), $("0:25", Arrays.asList("'poolname=IpPool'","'poolid=1'","'poolserialnumber=10'"))), 
						MapGenerator.createStringMap(),
						MapGenerator.createStringMap(),
						MapGenerator.createAdditionalMap()
						)
				),
				$("0:1 = xyz, 0:2 = pqr", 
						new AdditionalResponseAttributes(
							MapGenerator.fill(MapGenerator.createStringMap(), $("0:1", Arrays.asList("xyz")), $("0:2", Arrays.asList("pqr"))), 
							MapGenerator.createStringMap(),
							MapGenerator.createStringMap(),
							MapGenerator.createAdditionalMap()
						)
				),
				$("0:1 = $REQ:0:2", 
						new AdditionalResponseAttributes(
							MapGenerator.createStringMap(),
							MapGenerator.fill(MapGenerator.createStringMap(), $("0:1", Arrays.asList("0:2"))), 
							MapGenerator.createStringMap(),
							MapGenerator.createAdditionalMap()
						)
				),
				$("0:1 = $RES:0:2", 
						new AdditionalResponseAttributes(
							MapGenerator.createStringMap(),
							MapGenerator.createStringMap(),
							MapGenerator.fill(MapGenerator.createStringMap(), $("0:1", Arrays.asList("0:2"))), 
							MapGenerator.createAdditionalMap()
						)
				),
				$("0:456 = { '0:268' = '2001' }", 
						new AdditionalResponseAttributes(
							MapGenerator.createStringMap(),
							MapGenerator.createStringMap(),
							MapGenerator.createStringMap(),
							MapGenerator.fill(MapGenerator.createAdditionalMap(), 
									$("0:456", new AdditionalResponseAttributes(
											MapGenerator.fill(MapGenerator.createStringMap(), $("0:268", Arrays.asList("2001"))), 
											MapGenerator.createStringMap(),
											MapGenerator.createStringMap(),
											MapGenerator.createAdditionalMap())))
						)
				),
				$("0:456 = { '0:268' = '2001' ; '0:431' = { '0:421' = '123' ; '0:412' = '456' } } , 0:1 = xyz , 0:2 = $REQ:0:2 ", 
						new AdditionalResponseAttributes(
								MapGenerator.fill(MapGenerator.createStringMap(), $("0:1", Arrays.asList("xyz"))), 
								MapGenerator.fill(MapGenerator.createStringMap(), $("0:2", Arrays.asList("0:2"))), 
								MapGenerator.createStringMap(),
							MapGenerator.fill(MapGenerator.createAdditionalMap(), 
									$("0:456", new AdditionalResponseAttributes(
											MapGenerator.fill(MapGenerator.createStringMap(), $("0:268", Arrays.asList("2001"))), 
											MapGenerator.createStringMap(),
											MapGenerator.createStringMap(),
											MapGenerator.fill(MapGenerator.createAdditionalMap(), 
													$("0:431", new AdditionalResponseAttributes(
															MapGenerator.fill(MapGenerator.createStringMap(), $("0:421", Arrays.asList("123")), $("0:412", Arrays.asList("456"))), 
															MapGenerator.createStringMap(),
															MapGenerator.createStringMap(),
															MapGenerator.createAdditionalMap()))) 
											)))
						)
				),
				
				// Invalid Data
				$("", 
						new AdditionalResponseAttributes(
								MapGenerator.createStringMap(), 
								MapGenerator.createStringMap(),
								MapGenerator.createStringMap(),
								MapGenerator.createAdditionalMap()
						)
				),
				$(",", 
						new AdditionalResponseAttributes(
								MapGenerator.createStringMap(), 
								MapGenerator.createStringMap(),
								MapGenerator.createStringMap(),
								MapGenerator.createAdditionalMap()
						)
				),
				$("=", 
						new AdditionalResponseAttributes(
								MapGenerator.createStringMap(), 
								MapGenerator.createStringMap(),
								MapGenerator.createStringMap(),
								MapGenerator.createAdditionalMap()
						)
				),
				$("0:1=", 
						new AdditionalResponseAttributes(
								MapGenerator.createStringMap(), 
								MapGenerator.createStringMap(),
								MapGenerator.createStringMap(),
								MapGenerator.createAdditionalMap()
						)
				),
				
				$("0:1 < value", 
						new AdditionalResponseAttributes(
								MapGenerator.createStringMap(), 
								MapGenerator.createStringMap(),
								MapGenerator.createStringMap(),
								MapGenerator.createAdditionalMap()
						)
				),
				
				$("0:1= , 0:2 = xyz", 
						new AdditionalResponseAttributes(
								MapGenerator.fill(MapGenerator.createStringMap(), $("0:2", Arrays.asList("xyz"))), 
								MapGenerator.createStringMap(),
								MapGenerator.createStringMap(),
								MapGenerator.createAdditionalMap()
						)
				)
			);
	}
	
}

class MapGenerator {
	
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> fill(Map<K, V> map, Object[]... entries){
		for(Object[] entry : entries){
			map.put((K)entry[0], (V)entry[1]);
		}
		return map;
	}
	
	public static Map<String, List<String>> createStringMap(){
		return new HashMap<String,  List<String>>();
	}
	
	public static Map<String, AdditionalResponseAttributes> createAdditionalMap(){
		return new HashMap<String, AdditionalResponseAttributes>();
	}
}
