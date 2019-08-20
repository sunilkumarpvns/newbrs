package com.elitecore.aaa.radius.conf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import javax.xml.bind.JAXBException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.core.commons.config.core.ConfigurationContext;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;

@RunWith(JUnitParamsRunner.class)
public class RadClientConfigurationImplTest {
	
	@Mock AAAConfigurationContext aaaConfigurationContext;
	@Mock AAAServerContext aaaServerContext;
	
	private RadClientConfigurationImpl radClientConfiguration;
	
	@ReadOrder(order = {})
	class RadClientConfigurationImplStub extends RadClientConfigurationImpl {
	
		private VendorConfigurable vendorConfigurable;
		private ClientsConfigurable clientsConfigurable;
		private ClientProfileConfigurable clientsProfileConfigurable;
		
		public RadClientConfigurationImplStub(VendorConfigurable vendorConfigurable, 
				ClientsConfigurable clientsConfigurable, ClientProfileConfigurable clientsProfileConfigurable) {
			this.vendorConfigurable = vendorConfigurable;
			this.clientsConfigurable = clientsConfigurable;
			this.clientsProfileConfigurable = clientsProfileConfigurable;
		}
		
		protected ConfigurationContext getConfigurationContext(){
			return aaaConfigurationContext;
		}
		
		@Override
		protected VendorConfigurable getVendorConfigurable() {
			return vendorConfigurable;
		}
		
		@Override
		protected ClientsConfigurable getClientsConfigurable() {
			return clientsConfigurable;
		}
		
		@Override
		protected ClientProfileConfigurable getClientProfileConfigurable() {
			return clientsProfileConfigurable;
		}
	}
	
	@Before
	public void before() throws JAXBException, FileNotFoundException, UnsupportedEncodingException {
		MockitoAnnotations.initMocks(this);
		Mockito.when(aaaConfigurationContext.getServerContext()).thenReturn(aaaServerContext);
		Mockito.when(aaaServerContext.isLicenseValid(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		
		URL url = RadClientConfigurationImplTest.class.getClassLoader().getResource("config");
		String decodedUrlPath = URLDecoder.decode(url.getPath(),"UTF-8");
		
		VendorConfigurable vendorConfigurable = ConfigUtil.deserialize(new File(decodedUrlPath + File.separator + "vendors.xml"), VendorConfigurable.class);
		vendorConfigurable.postReadProcessing();

		ClientsConfigurable clientsConfigurable = ConfigUtil.deserialize(new File(decodedUrlPath + File.separator + "clients.xml"), ClientsConfigurable.class);

		ClientProfileConfigurable clientsProfileConfigurable = ConfigUtil.deserialize(new File(decodedUrlPath + File.separator + "rad-clients-profiles.xml"), ClientProfileConfigurable.class);
		clientsProfileConfigurable.postReadProcessing();
		
		radClientConfiguration = new RadClientConfigurationImplStub(vendorConfigurable, clientsConfigurable, clientsProfileConfigurable);
		radClientConfiguration.postReadProcessing();
	}
	
	@Test
	@Parameters(method = "dataFor_testGetClientData_ShouldReturnMappedClientData_WhenKeyClientIpIsInDifferentCase")
	public void testGetClientData_ShouldReturnMappedClientData_WhenKeyClientIpIsInDifferentCase(String key, String expectedClientDataIp) {
		assertNotNull(radClientConfiguration.getClientData(key));
		assertEquals(expectedClientDataIp, radClientConfiguration.getClientData(key).getClientIp());
	}
	
	@Test
	@Parameters(method = "dataFor_testGetClientData_ShouldReturnMappedClientData_WhenKeyClientIpIsInDifferentCase")
	public void testGetClientData_ReturnsClientData_ThatContainsClientIpInLowerCase(String key, String expectedClientDataIp) {
		assertEquals(expectedClientDataIp, radClientConfiguration.getClientData(key).getClientIp());
	}
	
	public static Object[][] dataFor_testGetClientData_ShouldReturnMappedClientData_WhenKeyClientIpIsInDifferentCase() {
		return new Object[][] {
				// lookup key										// expected ip from client data
				{"0:0:0:0:0:0:0:0".toLowerCase(),					"0:0:0:0:0:0:0:0"},
				{"0:0:0:0:0:0:0:0".toUpperCase(),					"0:0:0:0:0:0:0:0"},
				{"0:0:0:0:0:0:0:0",									"0:0:0:0:0:0:0:0"},

				{"0:0:0:0:0:0:0:1".toLowerCase(), 					"0:0:0:0:0:0:0:1"},
				{"0:0:0:0:0:0:0:1".toUpperCase(), 					"0:0:0:0:0:0:0:1"},
				{"0:0:0:0:0:0:0:1",									"0:0:0:0:0:0:0:1"},

				{"Fe80:0:0:0:eA40:f2Ff:Fe47:986c".toLowerCase(),	"fe80:0:0:0:ea40:f2ff:fe47:986c"},
				{"Fe80:0:0:0:eA40:f2Ff:Fe47:986c".toUpperCase(), 	"fe80:0:0:0:ea40:f2ff:fe47:986c"},
				{"Fe80:0:0:0:eA40:f2Ff:Fe47:986e",					"fe80:0:0:0:ea40:f2ff:fe47:986e"},
		};
	}
}
