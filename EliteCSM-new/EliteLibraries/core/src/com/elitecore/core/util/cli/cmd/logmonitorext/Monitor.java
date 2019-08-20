package com.elitecore.core.util.cli.cmd.logmonitorext;

import java.util.Collection;

public interface Monitor<T,V> {

	public static final int NO_TIME_LIMIT = -1;
	void add(String condition, long time) throws Exception;
	String clear(String condition);
	String clearAll();
	String getHotkeyHelp();
	String list();
	String getType();
	Collection<MonitorExpression<T,V>> getExpressions();
}
