package com.elitecore.corenetvertex.pm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


import static org.junit.Assert.assertSame;

/**
 * Created by jaidiptrivedi on 17/5/17.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
		Reload.class,
		ReloadData.class,
		ReloadServiceData.class,
        ReloadDataPackages.class,
        ReloadDataIMSPackages.class,
		ReloadRnCPackage.class,
		ReloadDataSliceConfiguration.class,
		BoDPackagesTest.class
})
public class PolicyManagerTestSuite {

	public static Map<String, List<GroupManageOrder>> convertToMap(GroupManageOrder... groupManageOrders) {
		Map<String, List<GroupManageOrder>> manageOrderByGroupId = new HashMap<String, List<GroupManageOrder>>();
		
		for (GroupManageOrder groupManageOrder : Arrays.asList(groupManageOrders)) {
			if (manageOrderByGroupId.get(groupManageOrder.getGroupId()) == null) {
				manageOrderByGroupId.put(groupManageOrder.getGroupId(), new ArrayList<GroupManageOrder>());
			}
			manageOrderByGroupId.get(groupManageOrder.getGroupId()).add(groupManageOrder);
		}
		return manageOrderByGroupId;
	}
	
	public static <T> Matcher<? super Iterator<T>> hasSameElements(final Iterator<T> expectedIterator) {
		return new TypeSafeDiagnosingMatcher<Iterator<T>>() {

			@Override
			public void describeTo(Description arg0) {

			}

			@Override
			protected boolean matchesSafely(Iterator<T> actualIterator, Description arg1) {
				
				while (actualIterator.hasNext()) {
					assertSame(expectedIterator.next(), actualIterator.next());
				}

				return true;
			}
		};
	}
	
	public static class ManageOrderData {
		private int orderNo;
		private String groupId;
		private String pkgId;
		public ManageOrderData(int orderNo, String groupId, String pkgId) {
			this.orderNo = orderNo;
			this.groupId = groupId;
			this.pkgId = pkgId;
		}
		public int getOrderNo() {
			return orderNo;
		}
		public void setOrderNo(int orderNo) {
			this.orderNo = orderNo;
		}
		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}
		public String getPkgId() {
			return pkgId;
		}
		public void setPkgId(String pkgId) {
			this.pkgId = pkgId;
		}
	}
}
