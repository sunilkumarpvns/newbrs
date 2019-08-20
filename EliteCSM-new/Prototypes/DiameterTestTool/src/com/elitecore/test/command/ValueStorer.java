package com.elitecore.test.command;

import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.test.ExecutionContext;

public interface ValueStorer {
	void storeValue(String key, Expression source, int index, ExecutionContext context);
}
