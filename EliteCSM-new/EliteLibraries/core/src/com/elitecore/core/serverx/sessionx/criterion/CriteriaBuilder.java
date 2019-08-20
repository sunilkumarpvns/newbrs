package com.elitecore.core.serverx.sessionx.criterion;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.Criteria;

public abstract class CriteriaBuilder {
	private static final String MODULE = "CRITERIA_BUILDER";
	public String buildCriteria(Criteria criteria){
		ArrayList<Criterion> criterions = criteria.getCriterions();
		String criteriaString = null;
		if(criterions != null && criterions.size() > 0){
			Criterion criterion = criterions.get(0);
			criteriaString = buildExpression(criteria.getSchemaName(),criterion);
			if(criteriaString == null)
				return null;
			for(int criterionIndex=1; criterionIndex < criterions.size();criterionIndex++){
				Criterion nextCriterion = criterions.get(criterionIndex);
				String nextCriteriaString = buildExpression(criteria.getSchemaName(),nextCriterion);
				criteriaString += " AND " + nextCriteriaString;
			}
		}
		return criteriaString;
	}
	public void setParameters(PreparedStatement ps, Criteria criteria){
		setParameters(ps, criteria, 1);
	}
	public void setParameters(PreparedStatement ps, Criteria criteria, int index){
		if(criteria.getCriterions() == null && criteria.getCriterions().size() == 0){
			LogManager.getLogger().warn(MODULE, "Criteria list is empty. No parameter is setted in Prepared Statement");
			return;
		}
		for(Criterion criterion:criteria.getCriterions()){
			index = setParameters(ps, criterion, index);
		}
	}
	
	private int setParameters(PreparedStatement ps, Criterion criterion, int index){
		switch(criterion.getExpressionType()){
		case  Criterion.SIMPLE_EXPRESSION:
			try {
				ps.setString(index++, ((SimpleExpression)criterion).getValue());
			} catch (SQLException e) {
				LogManager.getLogger().error(MODULE, "Error in setting parameters for Prepared Statement, Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
			break;
		case Criterion.LOGICAL_EXPRESSION:
			LogicalExpression logicalExpression = (LogicalExpression) criterion;
			index = setParameters(ps, logicalExpression.getLhs(), index);
			index = setParameters(ps, logicalExpression.getRhs(), index);
		case Criterion.NOT_EXPRESSION:
			//Pending
		}
		return index;
	}
	private String buildExpression(String tableName, Criterion criterion){
		String expression = null;
		switch(criterion.getExpressionType()){
		case Criterion.LOGICAL_EXPRESSION:
			expression = buildLogicalExpression(tableName,criterion);
			break;
		case Criterion.NOT_EXPRESSION:
			expression = buildNotExpression(tableName,criterion);
			break;
		case Criterion.SIMPLE_EXPRESSION:
			expression = buildSimpleExpression(tableName,criterion);
			break;
		}
		return expression;
	}
	protected String buildSimpleExpression(String tableName,Criterion criterion){
		SimpleExpression simpleExpression = (SimpleExpression)criterion;
		String expression = getColumnName(tableName,simpleExpression.getPropertyName()) + simpleExpression.getOperator() + "?";
		return expression;
	}
	
	protected String buildNotExpression(String tableName,Criterion criterion){
		return "";
	}
	
	protected String buildLogicalExpression(String tableName,Criterion criterion){
		LogicalExpression logicalExpression = (LogicalExpression) criterion;
		String expression = "(" + buildExpression(tableName,logicalExpression.getLhs()) + " "+logicalExpression.getOp() + " "+buildExpression(tableName,logicalExpression.getRhs()) + ")";
		return expression;
	}
	
	protected abstract String getColumnName(String tableName,String propertyName);
}
