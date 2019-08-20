package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;

public class UMconfigurationImpl implements UMconfiguration{

	private final int batchSize;
	private final int batchQueryTimeout;
	
	
	
	public UMconfigurationImpl(int batchSize, int batchQueryTimeout){
		this.batchSize = batchSize;
		this.batchQueryTimeout = batchQueryTimeout;
	}
	

	@Override
	public int getBatchSize() {
		return batchSize;
	}

	@Override
	public int getBatchQueryTimeout() {
		return batchQueryTimeout;
	}


	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.append("Batch Size", batchSize);
		builder.append("Batch Query Timeout", batchQueryTimeout);
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- UM Configuration -- ");
		toString(builder);
		return builder.toString();
	}
}
