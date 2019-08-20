package com.elitecore.core.serverx.sessionx;

import java.util.ArrayList;

import com.elitecore.core.serverx.sessionx.criterion.Criterion;

public interface Criteria {
	public void add(Criterion criterion);
	public ArrayList<Criterion> getCriterions();
	public String getSchemaName();
	public String getHint();
	public void setHint(String hint);
}
