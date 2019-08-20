package com.elitecore.aaa.core.radius.data;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.aaa.radius.conf.impl.ClientsConfigurable;
import com.elitecore.aaa.radius.conf.impl.ClientsConfigurable.ClientDetail;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.data.RadiusClientDataImpl;
import com.elitecore.commons.net.FakeAddressResolver;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class RadClientDataImplTest {
	
	private FakeAddressResolver fakeAddressResolver;

	@Before
	public void setUp() {
		fakeAddressResolver = new FakeAddressResolver()
				.addHost("127.0.0.0", new byte[] {127, 0, 0, 0})
				.addHost("127.0.0.2", new byte[] {127, 0, 0, 2})
				.addHost("127.0.0.3", new byte[] {127, 0, 0, 3})
				.addHost("127.0.0.4", new byte[] {127, 0, 0, 4})
				.addHost("127.0.0.5", new byte[] {127, 0, 0, 5});
	}
	
	public Object[] data() {
		return new Object[][]{
				// configured ips		expected number of clients	expected sub net mask for each client			expect client configured ip list
				
				//positive cases without combination
				{"0.0.0.0", 							1,			new Integer[]{null},							new String[]{	"0.0.0.0"}},
				{"127.0.0.1,0.0.0.0", 					2,			new Integer[]{null, null},						new String[]{	"127.0.0.1", 
																																	"0.0.0.0"}},
				{"127.0.0.1-127.0.0.3",						3,			new Integer[]{null, null, null},				new String[]{	"127.0.0.1", 
																																	"127.0.0.2", 
																																	"127.0.0.3"}},
				{"127.0.0.1/24", 							1,			new Integer[]{24},								new String[]{	"127.0.0.1"}},
				
				
				
				//positive cases with combination of two different formats
				{"0.0.0.0,127.0.0.1-127.0.0.3",				4,			new Integer[]{null, null, null, null},			new String[]{	"0.0.0.0", 
																																	"127.0.0.1", 
																																	"127.0.0.2", 
																																	"127.0.0.3"}},
				{"0.0.0.0,127.0.0.1/24",					2,			new Integer[]{null, 24},						new String[]{	"0.0.0.0", 
																																	"127.0.0.1"}},
				{"127.0.0.0/24,127.0.0.2-127.0.0.4",			4,			new Integer[]{24, null, null, null},			new String[]{	"127.0.0.0", 
																																	"127.0.0.2", 
																																	"127.0.0.3", 
																																	"127.0.0.4"}},
																																	
				{"127.0.0.2-127.0.0.4,127.0.0.1",				4,			new Integer[]{null, null, null, null},			new String[]{"127.0.0.2", 
																																	"127.0.0.3", 
																																	"127.0.0.4", 
																																	"127.0.0.1"}},
				//positive exceptional cases
				{"0.0.0.0,   , 127.0.0.0/ 24",			2,			new Integer[]{null, 24},						new String[]{	"0.0.0.0",
																																	"127.0.0.0"}},

				//negative cases
				{"invalid",								0,			new Integer[]{},								new String[]{	}},
				{"0.0.0.0/-12",							0,			new Integer[]{},								new String[]{	}},
				{null,									0,			new Integer[]{},								new String[]{	}},
				{"0.0.0.0-0.0.0.0",						0,			new Integer[]{},								new String[]{	}},
				{"127.0.0.7-127.0.0.3",						0,			new Integer[]{},								new String[]{	}},
				{"127.0.0.1-127.0.0.3-127.0.0.7",				0,			new Integer[]{},								new String[]{	}},
				{"127.0.0.1--127.0.0.7",					0,			new Integer[]{},								new String[]{	}},
				{"127.0.0.0/20/2",						0,			new Integer[]{},								new String[]{	}},
				{"127.0.0.0/32",							0,			new Integer[]{},								new String[]{	}},
				{"127.0.0.0/a",							0,			new Integer[]{},								new String[]{	}},
				
		};
	}
	
	@Test
	@Parameters(method = "data")
	public void testParseClientIP(String ip, int expectedClientNo, Integer[] expectedSubnetMask, String[] ipArray) {
		ClientDetail clientDetail = new ClientsConfigurable.ClientDetail();
		clientDetail.setStrClientIp(ip);
		List<RadClientData> parseClientIP = RadiusClientDataImpl.parseClientIP(clientDetail, null, fakeAddressResolver);
		assertEquals("number of clients are wrong for case with configured ip: \"" + ip + "\"  ", expectedClientNo, parseClientIP.size());
		int i=0;
		for (RadClientData radClientData : parseClientIP) {
			assertEquals("client ip is wrong for case with configured ip: \"" + ip + "\"  ", ipArray[i], radClientData.getClientIp());
			assertEquals("client subnet mask is wrong for case with configured ip: \"" + ip + "\"  ", expectedSubnetMask[i++], radClientData.getSubnetMask());
		}
	}
	
}
