package com.elitecore.corenetvertex.spr;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;

import com.elitecore.corenetvertex.core.ldap.LDAPDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.spr.data.ProfileFieldMapping;
import com.elitecore.corenetvertex.spr.data.impl.LDAPSPInterfaceConfigurationImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;

@RunWith(HierarchicalContextRunner.class)
public class ExternalLDAPSPInterfaceTest {

	
	private ExternalLDAPSPInterface externalLdapSPInterface;
	private LDAPSPInterfaceConfigurationImpl ldapSpInterfaceConfiguration;
	private AlertListener alertListener;
	private ArrayList<String> searchBaseDNList = new ArrayList<String>(); 
	private DummyLDAPConnectionProvider ldapConnectionProvider;
	private LDAPConnection ldapConnection;
	private LDAPDataSource ldapDataSource;
	
	
	@Before
	public void setUp() throws LDAPException {
        ldapDataSource = mock(LDAPDataSource.class);
        doReturn("dsName").when(ldapDataSource).getDataSourceName();
		createLDAPSPInterfaceConfiguration();
		this.ldapConnection = spy(new LDAPConnection());
		doReturn(null).when(ldapConnection).search(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), Mockito.<String[]>any(), Mockito.eq(false));
		this.alertListener = mock(AlertListener.class);
		this.ldapConnectionProvider = new DummyLDAPConnectionProvider();
		this.externalLdapSPInterface = new ExternalLDAPSPInterface(ldapSpInterfaceConfiguration, alertListener, ldapConnectionProvider);
	}

	private void createLDAPSPInterfaceConfiguration() {
		searchBaseDNList.add("search1");
		this.ldapSpInterfaceConfiguration = new LDAPSPInterfaceConfigurationImpl(10, 1,
				"dd/MM/YYYY",
                ldapDataSource, "driverName", new ProfileFieldMapping(),
				"userPrefix",
				searchBaseDNList);
	}
	
	public class ConnectionShouldBeClosedWhen {
		
		@Test
		public void profileSuccessFetched() throws OperationFailedException {
			externalLdapSPInterface.getProfile("userId");
			assertTrue(ldapConnectionProvider.isCloseCalled());
		}
		
		@Test(expected=OperationFailedException.class)
		public void anyExceptionThrown() throws LDAPException, OperationFailedException {
			doThrow(new LDAPException()).when(ldapConnection).search(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString(), Mockito.<String[]>any(), Mockito.eq(false));
			
			try {
				externalLdapSPInterface.getProfile("userId");
			} catch (OperationFailedException e) {	
				assertTrue(ldapConnectionProvider.isCloseCalled());
				throw e;
			}
			fail("exception should be thrown");
		}
		
	}
	
	private class DummyLDAPConnectionProvider implements LDAPConnectionProvider {
		
		private boolean isCloseCalled = false;

		@Override
		public LDAPConnection getConnection() throws LDAPException {
			return ldapConnection;
		}

		@Override
		public void close(LDAPConnection connection) {
			isCloseCalled = true;
		}
		
		public boolean isCloseCalled() {
			return isCloseCalled;
		}
	}
}
