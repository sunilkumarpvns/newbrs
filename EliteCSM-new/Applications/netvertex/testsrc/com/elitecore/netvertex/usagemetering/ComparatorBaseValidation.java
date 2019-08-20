package com.elitecore.netvertex.usagemetering;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;

import com.elitecore.netvertex.core.Validation;

public class ComparatorBaseValidation<T> implements Validation<List<T>> {
	
	private Comparator<T> comparator;
	private List<T> expectedData;
	
	public ComparatorBaseValidation(Comparator<T> comparator,List<T> t) {
		this.expectedData = t;
		this.comparator = comparator;
	}

	

	@Override
	public void validate(List<T> actualData) throws Exception {

			Assert.assertTrue("List size not match\n" + expectedData.size() + "\n" + actualData.size(),expectedData.size() == actualData.size());
			
			List<T> expectedList = new ArrayList<T>();
			expectedList.addAll(expectedData);
			
			List<T> actualList = new ArrayList<T>();
			actualList.addAll(actualData);
			
			
			Iterator<T> exptedListItr = expectedList.iterator();
			
			
			while(exptedListItr.hasNext()){
				T expected = exptedListItr.next();
				Iterator<T> actualListItr = actualList.iterator();
				while(actualListItr.hasNext()){
					T actual = actualListItr.next();
					if(comparator.compare(expected, actual) == 0){
						exptedListItr.remove();
						actualListItr.remove();
						break;
					}
				}
			}
			
			
			Assert.assertTrue("Insert list not match\n" + expectedList + "\n" + actualList,expectedList.isEmpty() && actualList.isEmpty());
		
	}

}
