package com.elitecore.aaa.diameter.service.application.handlers;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.DefaultValueProvider;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public abstract class LogicalExpressionFilter {
private static final String MODULE = "LOGIC-EXPR-FILTER";
	
	/**
	 * Returns true if the logical expression evaluates to true, false otherwise.
	 * 
	 * @param request request on which the filter is to be applied
	 * @param response response on which the filter is to be applied
	 * @return true if the logical expression evaluates to true, false otherwise
	 */
	public abstract boolean apply(ApplicationRequest request, ApplicationResponse response);
	
	/**
	 * Creates the instance of filter using the expression library. The filter
	 * always evaluates to true if {@code expression} is {@code null} or empty.
	 * 
	 * @param <T> type of request packet
	 * @param <V> type of response packet
	 * @param expression expression string that can be null
	 * @return a newly created filter
	 * @throws InitializationFailedException if filter expression is invalid
	 */
	public static LogicalExpressionFilter create(@Nullable String expression) 
			throws InitializationFailedException {
		
		if (Strings.isNullOrBlank(expression)) {
			return new FilterAbsent();
		}
		return new FilterPresent(expression).init();
	}
	
	static class FilterPresent extends LogicalExpressionFilter {
		private final String rulesetString;
		private LogicalExpression ruleset;

		public FilterPresent(@Nonnull String rulesetString) {
			this.rulesetString = checkNotNull(rulesetString, "ruleset is null");
		}

		public FilterPresent init() throws InitializationFailedException {
			try {
				initRuleSet();
			} catch (InvalidExpressionException e) {
				throw new InitializationFailedException(e);
			}
			return this;
		}

		private void initRuleSet() throws InvalidExpressionException {
			ruleset = Compiler.getDefaultCompiler()
			.parseLogicalExpression(rulesetString);
		}

		@Override
		public boolean apply(ApplicationRequest request, ApplicationResponse response) {
			try { 
				return ruleset.evaluate(new DefaultValueProvider(request, response));
			} catch (Exception ex) {
				LogManager.getLogger().trace(MODULE, ex);
				return false;
			}
		}
	}
	
	static class FilterAbsent extends LogicalExpressionFilter {

		@Override
		public boolean apply(ApplicationRequest request, ApplicationResponse response) {
			return true;
		}
	}
}
