package com.elitecore.core.serverx.sessionx.criterion;

/**
 * The <tt>criterion</tt> package may be used by applications as a framework for building
 * new kinds of <tt>Criterion</tt>. However, it is intended that most applications will
 * simply use the built-in criterion types via the static factory methods of this class.
 *
 * @see com.elitecore.core.serverx.sessionx.Criteria
 * @author Subhash Punani
 */

public class Restrictions {
	
	Restrictions() {
		//cannot be instantiated
	}
	
	/**
	 * Apply an "equal" constraint to the named property
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression eq(String propertyName, String value) {
		return new SimpleExpression(propertyName, value, "=");
	}
	
	/**
	 * Apply a "not equal" constraint to the named property
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression ne(String propertyName, String value) {
		return new SimpleExpression(propertyName, value, "<>");
	}
	
	/**
	 * Apply a "like" constraint to the named property
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression like(String propertyName, String value) {
		return null;
	}
	
	/**
	 * Apply a "greater than" constraint to the named property
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression gt(String propertyName, String value) {
		return new SimpleExpression(propertyName, value, ">");
	}
	
	/**
	 * Apply a "less than" constraint to the named property
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression lt(String propertyName, String value) {
		return new SimpleExpression(propertyName, value, "<");
	}
		
	/**
	 * Apply a "less than or equal" constraint to the named property
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression le(String propertyName, String value) {
		return new SimpleExpression(propertyName, value, "<=");
	}
	
	/**
	 * Apply a "greater than or equal" constraint to the named property
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression ge(String propertyName, String value) {
		return new SimpleExpression(propertyName, value, ">=");
	}
	
	/**
	 * Return the conjuction of two expressions
	 *
	 * @param lhs
	 * @param rhs
	 * @return Criterion
	 */
	public static LogicalExpression and(Criterion lhs, Criterion rhs) {
		return new LogicalExpression(lhs, rhs, "and");
	}
	
	/**
	 * Return the disjuction of two expressions
	 *
	 * @param lhs
	 * @param rhs
	 * @return Criterion
	 */
	public static LogicalExpression or(Criterion lhs, Criterion rhs) {
		return new LogicalExpression(lhs, rhs, "or");
	}

	/**
	 * Return the negation of an expression
	 *
	 * @param expression
	 * @return Criterion
	 */
	public static Criterion not(Criterion expression) {
		return new NotExpression(expression);
	}

}
