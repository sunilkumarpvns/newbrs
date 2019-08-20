package com.elitecore.corenetvertex.pm.pkg;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;

public class GroupManageOrderStoreTest {
	
	private static final String PKG_ID2 = "PKG_ID2";
	private static final String GROUP2 = "GROUP2";
	private static final String ID2 = "ID2";
	private static final String ID1 = "ID1";
	private static final String GROUP1 = "GROUP1";
	private static final String PKG_ID1 = "PKG_ID1";

	@Test
	public void test_add_should_store_packages() {
		GroupManageOrder order1 = new GroupManageOrder(ID1, GROUP1, PkgType.EMERGENCY, 1, PKG_ID1); 
		
		GroupManageOrderStore store = new GroupManageOrderStore();
		store.add(order1);
		
		assertSame(order1, store.getById(ID1));
	}
	
	@Test
	public void test_removeById_should_remove_respective_package_from_store() {

		GroupManageOrder order1 = new GroupManageOrder(ID1, GROUP1, PkgType.EMERGENCY, 1, PKG_ID1); 
		
		GroupManageOrderStore store = new GroupManageOrderStore();
		store.add(order1);
		store.removeById(order1.getId());
		
		assertNull(store.getById(order1.getId()));
	}
	
	@Test
	public void test_merge_should_add_all_entry_not_exist_in_current_store() throws Exception {
		
		GroupManageOrder order1 = new GroupManageOrder(ID1, GROUP1, PkgType.EMERGENCY, 1, PKG_ID1);
		GroupManageOrder order2 = new GroupManageOrder(ID2, GROUP2, PkgType.EMERGENCY, 1, PKG_ID2);
		
		GroupManageOrderStore store1 = new GroupManageOrderStore();
		store1.add(order1);
		store1.add(order2);
		
		GroupManageOrder order3 = new GroupManageOrder(ID2, GROUP2, PkgType.EMERGENCY, 1, PKG_ID2);
		GroupManageOrderStore store2 = new GroupManageOrderStore();
		store2.add(order3);
		
		store2 = spy(store2);
		
		store2.merge(store1);
		
		verify(store2, times(1)).add(order1);
		verify(store2, times(0)).add(order2);
	}

}
