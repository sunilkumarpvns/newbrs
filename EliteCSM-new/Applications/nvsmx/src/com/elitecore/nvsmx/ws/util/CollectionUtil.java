package com.elitecore.nvsmx.ws.util;

import java.util.ArrayList;


//FIXME Need to remove this class after test cases gets completed for the methods in this
public class CollectionUtil {


	public static <T> ArrayList<T> newArrayListWithElements(T... t) {
		ArrayList<T> list = new ArrayList<T>();
		
		for (T element : t) {
			list.add(element);
		}
		return list;
	}

}
