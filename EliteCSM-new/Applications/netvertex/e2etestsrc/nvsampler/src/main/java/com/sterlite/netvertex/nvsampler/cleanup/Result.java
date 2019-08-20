package com.sterlite.netvertex.nvsampler.cleanup;

import java.util.ArrayList;
import java.util.List;
import com.sterlite.netvertex.nvsampler.commons.IndentingToStringBuilder;
import com.sterlite.netvertex.nvsampler.commons.ToStringable;

public class Result implements ToStringable {
	private boolean isSuccess;
	private String failMessage;
	private String operationName;
	private List<Result> results;

	public Result(String operationName) {
		this(operationName, true, null);
	}

	public Result(String operationName, boolean isSuccess, String failMessage) {
		this.operationName = operationName;
		this.isSuccess = isSuccess;
		this.failMessage = failMessage;
		this.results = new ArrayList<>();
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public String getFailMessage() {
		return failMessage;
	}

	public void setFailMessage(String failMessage) {
		this.failMessage = failMessage;
	}

	public List<Result> getResults() {
		return results;
	}

	public void addResult(Result result) {
		if (result.isSuccess == false) {
			notSuccess();
		}
		this.results.add(result);
	}

	public void success() {
		this.isSuccess = true;
	}

	public void notSuccess() {
		this.isSuccess = false;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		toString(builder);
		return builder.toString();
	}

	public String getOperationName() {
		return operationName;
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.append("success", isSuccess);
		if (isSuccess == false) {
			builder.append("fail message", failMessage);
		}

		results.forEach(res->builder.appendChildObject(res.getOperationName(), res));
	}

	public void addResults(List<Result> results) {
		results.forEach(this::addResult);
	}
}
