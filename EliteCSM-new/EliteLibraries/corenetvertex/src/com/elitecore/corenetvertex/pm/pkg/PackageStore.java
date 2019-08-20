package com.elitecore.corenetvertex.pm.pkg;

import java.util.Iterator;

import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;

public abstract class PackageStore<T extends UserPackage> {

	public abstract void add(T t);
	
	public abstract T getById(String id);

	public abstract T getByName(String name);
	
	public abstract void removeById(String id);
	
	public void merge(PackageStore<T> existingStore, Predicate<UserPackage> filter) {
		Iterator<T> existingStoreIterator = existingStore.iterator();
		
		while (existingStoreIterator.hasNext()) {
			T userPackage = existingStoreIterator.next();
			if (getById(userPackage.getId()) == null && filter.apply(userPackage)) {
				add(userPackage);
			}
		}
	}
	
	public abstract Iterator<T> iterator();
}