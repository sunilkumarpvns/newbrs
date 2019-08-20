package com.elitecore.test.command;

import static com.elitecore.commons.logging.LogManager.getLogger;

import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.test.ExecutionContext;



public class StoreCommand implements Command {
	
	private static final String MODULE = "STORE-CMD";
	private final String key;
	private final ValueStorer valueProvider;
	private final Expression source;
	private final int index;
	private String name;

	public StoreCommand(String key, Expression source, int index,ValueStorer valueProvider,String name) {
		this.key = key;
		this.source = source;
		this.index = index;
		this.valueProvider = valueProvider;
		this.name = name;
	}

	@Override
	public void execute(ExecutionContext context) throws Exception {
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Executing store("+getName()+") command");
		
		valueProvider.storeValue(key, source,index,context);
		
		if(getLogger().isDebugLogLevel())
			getLogger().debug(MODULE, "Store("+getName()+") command");
	}

	@Override
	public String getName() {
		return name;
	}

}
