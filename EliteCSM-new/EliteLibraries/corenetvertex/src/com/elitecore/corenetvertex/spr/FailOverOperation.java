package com.elitecore.corenetvertex.spr;

import java.util.Collection;

public interface FailOverOperation<T> {

	void doFailover(Collection<T> t);
	void doFailover(T t);
}
