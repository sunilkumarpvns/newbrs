package com.elitecore.core.serverx.sessionx.impl;

import java.util.ArrayList;

import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.criterion.Criterion;

public class CriteriaImpl implements Criteria {
	private String schemaName;
	private String hint;
	private ArrayList<Criterion> criteriaList;
	
	public CriteriaImpl(String schemaName) {
		this.schemaName = schemaName;
		this.criteriaList = new ArrayList<Criterion>();
	}
	
	@Override
	public void add(Criterion criterion) {
		criteriaList.add(criterion);
	}
	
	@Override
	public ArrayList<Criterion> getCriterions() {
		return criteriaList;
	}

	public String getSchemaName() {
		return schemaName;
	}
	
	public void setHint(String hint) {
		this.hint = hint;
	}

	@Override
	public String getHint() {
		return hint;
	}
}
