package com.elitecore.core.commons.util.url;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.core.util.url.MultiSocketURLParser;
import com.elitecore.core.util.url.SocketDetail;

@RunWith(JUnitParamsRunner.class)
public class MultiSocketURLParserTest {
	
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	public static Object[][] dataFor_testParse() {
		return new Object[][] {
				{"10.106.1.1:100", getSDList($("10.106.1.1", 100))},
				{"10.106.1.1:100, 10.106.1.2:200", getSDList($("10.106.1.1", 100),$("10.106.1.2", 200))},
				{"10.106.1.1::100, 10.106.1.2:200, 10.106.1.3::300", getSDList($("10.106.1.2", 200))},
				{"10.106.1.1:0, 10.106.1.2:200", getSDList($("10.106.1.2", 200))},
		};
	}
	
	private static List<SocketDetail> getSDList(SocketDetail... details) {
		List<SocketDetail> socketDetails = new ArrayList<SocketDetail>();
		for (SocketDetail socketDetail: details) {
			socketDetails.add(socketDetail);
		}
		return socketDetails;
	}
	
	private static SocketDetail $(String ip, int port) {
		return new SocketDetail(ip, port);
	}
	
	@Test
	@Parameters(method = "dataFor_testParse")
	public void testParse(String serviceAddress, List<SocketDetail> expectedSDList) throws MalformedURLException {
		assertEquals(expectedSDList, MultiSocketURLParser.parse(serviceAddress));
	}
	
	public static Object[][] dataFor_testParse_WillThrowMalformedException() {
		return new Object[][] {
				{"10.106.1.1"},
				{"10.106.1.1:, 10.106.1.2:a"},
				{"10.106.1.1::100, 10.106.1.2::200"},
				{"10.106.1.1:0"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testParse_WillThrowMalformedException")
	public void testParse_WillThrowMalformedException(String serviceAddress) throws MalformedURLException {
		expectedException.expect(MalformedURLException.class);
		MultiSocketURLParser.parse(serviceAddress);
	}
}
