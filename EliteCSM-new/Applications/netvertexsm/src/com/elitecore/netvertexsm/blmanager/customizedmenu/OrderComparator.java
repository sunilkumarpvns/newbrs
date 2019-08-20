package com.elitecore.netvertexsm.blmanager.customizedmenu;

import java.util.Comparator;
/**
 * OrderComparator is used to compare menuItems order in customized menu
 */
public class OrderComparator implements Comparator<MenuItem> {

	@Override
	public int compare(MenuItem menuItem1, MenuItem menuItem2) {
		if(menuItem1.getOrder() > menuItem2.getOrder()){
			return 1;
		}else if(menuItem1.getOrder() < menuItem2.getOrder()){
			return -1;
		}else{
			return 0;
		}
	}
}
