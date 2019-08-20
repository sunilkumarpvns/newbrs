package com.elitecore.core.serverx.sessionx.criterion;

import java.io.Serializable;

public interface Criterion extends Serializable {
	int SIMPLE_EXPRESSION=0;
	int LOGICAL_EXPRESSION=1;
	int NOT_EXPRESSION=2;
	
	public int getExpressionType();
}
