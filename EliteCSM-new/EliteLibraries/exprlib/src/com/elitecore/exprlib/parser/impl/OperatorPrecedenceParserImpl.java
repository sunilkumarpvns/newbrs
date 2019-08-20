package com.elitecore.exprlib.parser.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.elitecore.exprlib.parser.OperatorPrecedenceParser;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.FunctionExpression;
import com.elitecore.exprlib.parser.expression.IdentifierExpression;
import com.elitecore.exprlib.parser.expression.LiteralExpression;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.impl.ComparisionExpressionImpl;
import com.elitecore.exprlib.parser.expression.impl.IdentifierExpressionImpl;
import com.elitecore.exprlib.parser.expression.impl.LiteralExpressionImpl;
import com.elitecore.exprlib.parser.expression.impl.LogicalExpressionImpl;
import com.elitecore.exprlib.scanner.Symbol;
import com.elitecore.exprlib.symbol.impl.IdentifierSymbol;
import com.elitecore.exprlib.symbol.impl.LiteralSymbol;
import com.elitecore.exprlib.symbol.impl.Operator;
import com.elitecore.exprlib.symbol.impl.OperatorSymbol;
import com.elitecore.exprlib.symbol.impl.operator.ComparisionOperator;
import com.elitecore.exprlib.symbol.impl.operator.Equals;
import com.elitecore.exprlib.symbol.impl.operator.HashOperator;
import com.elitecore.exprlib.symbol.impl.operator.LogicalOperator;
import com.elitecore.exprlib.symbol.impl.operator.OROperator;



public class OperatorPrecedenceParserImpl implements OperatorPrecedenceParser {

	private static final int NUMBER_OF_OPERANDS_FOR_BINARY_OPERATOR = 2;
	private int precedence[][];
	private final static int S=-1;
	private final static int R=1;
	private final static int EQ=0;
	private final static int ERR=2;
	private final static HashOperator HASH_OPERATOR = new HashOperator(Operator.HASH.type);
	private Map<String, FunctionExpression> functionMap=new HashMap<String, FunctionExpression>();

	public void addFunction(String functionName,FunctionExpression functionObject){

		functionMap.put(functionName, functionObject);
	}

	/**
	 * @author milan
	 * The precedence table is used to get the precedence of one expression over other 
	 * S : SHIFT , The precedence of next expression is greater than the expression in the stack   
	 * R : REDUCE , The precedence of next expression is smaller than the expression in the stack
	 */
	public OperatorPrecedenceParserImpl() {
		precedence=new int[][]{
                   	/*				AND OR NOT IN <  =  >  (    )    ANY #   */ 
					/*     AND */  {R,  R, S,  S, S, S, S, S,   R,   S, R},
					/*      OR */  {R,  R, S,  S, S, S, S, S,   R,   S, R},
					/*		NOT */ {R,  R, R,  S, S, S, S, S,   R,   S, R},
					/*      IN  */ {R,  R, R,  R, R, R, R, S,   R,   S, R},
					/* 	       <*/ {R,  R, R,  R, R, R, R, S,   R,   S, R},
					/*		   =*/ {R,  R, R,  R, R, R, R, S,   R,   S, R},
					/*		   >*/ {R,  R, R,  R, R, R, R, S,   R,   S, R},
					/*		   (*/ {S,  S, S,  S, S, S, S, S,   EQ,  S, ERR},
					/*		   )*/ {R,  R, R,  R, R, R, R, ERR, R, 	 R, R},
					/*		 ANY*/ {R,  R, R,  R, R, R, R, S,   R,   R, R},
					/*		  # */ {S,  S, S,  S, S, S, S, S,   ERR, S, EQ}
		};
	}

	/**
	 * @author milan
	 * @param symbols : list of all the tokenized symbols
	 * The method parse the symbols in Operator Precedence parser
	 * @return finalExpresson   : Method return the final expression
	 * @throws InvalidExpressionException
	 */
	public Expression parse(List<Symbol> symbols)throws InvalidExpressionException {
		try{
			/*
			 * Hash operator is not allowed in expression because hash(#) is used in InfixToPostFix conversion
			 */
			if(symbols.contains(HASH_OPERATOR)){
				throw new InvalidExpressionException("Hash Operator is not allowed in expression");
			}

			List<Symbol> postfixList = infixToPostfix(symbols);
			
			/* only parenthesis without any symbol returns empty postfix list, this will be handle here
			 *  eg. ()
			 */
			if(postfixList == null || postfixList.isEmpty() == true){
				throw new InvalidExpressionException("No valid operand or operator found in expression");
			}
			
			return postfixEvaluation(postfixList);
		}catch(InvalidExpressionException invalidEx){
			throw invalidEx;
		}catch (Exception e) {
			throw new InvalidExpressionException("Error while parsing expression, Reason: " + e.getMessage(), e);
		}
	}

	/**
	 * infixToPostfix method converts infix expression into postfix notation
	 * 
	 *@author milan
	 *@return postfix list of symbols
	 * @throws InvalidExpressionException
	 */
	private List<Symbol> infixToPostfix(List<Symbol> symbols)throws InvalidExpressionException {

		Stack<Symbol> postStack=new Stack<Symbol>();
		List<Symbol> postfix=new ArrayList<Symbol>();

		symbols.add(HASH_OPERATOR);
		postStack.push(HASH_OPERATOR);
		Symbol popped;
		Symbol prev = new InitialSymbol();
		int row=0,column=0;
		for(Symbol next:symbols){
			column=getIndex(next);
			row=getIndex(postStack.lastElement());
			
			
			/*
			 * 'IN'/'NOT' operator should always be followed by '(' operator,
			 * 
			 * Condition: 
			 * 	IF previous symbol is IN/NOT operator && next is not OPENINGBRACKET '(' 
			 * 				throw InvalidExpressionException
			 * eg. 
			 *  a IN a,b,c 
			 *  a IN < a,b,c
			 */	
			Operator prevOperator = Operator.getOperatorType(prev.getName());
			if((prevOperator == Operator.IN ||
					prevOperator == Operator.NOT) && Operator.getOperatorType(next.getName()) != Operator.OPENINGBRACES){
				throw new InvalidExpressionException("'(' missing for '" + prev.getName() + "' operator, Syntax: " + prevOperator.syntax);
			}
			
			if(precedence[row][column]==ERR){
				
				/*
				 * Bracket is not closed properly then 'next' may be hash(#) symbol,
				 * so throw error
				 * eg. 
				 * (A < B,
				 * (A AND ()
				 * (A AND (B)	
				 *
				 *NOTE: this check is only for valid reason of failed expression,
				 */
				if(next.getType() == Symbol.operator 
						&& Operator.getOperatorType(next.getName()) == Operator.HASH){
					throw new InvalidExpressionException("Bracket may not closed properly, recheck expression");
				}
				
				throw new InvalidExpressionException("Error occurred at symbol: "+ next);
			}
			
			while(precedence[row][column]==R) {
				popped = postStack.pop();
				postfix.add(popped);
				row=getIndex(postStack.lastElement());
				
				/*
				 * Bracket is not closed properly then 'next' may be hash(#) symbol,
				 * so throw error
				 * eg. 
				 * A < (B<C,	
				 *
				 *NOTE : this check is only for valid reason of failed expression,
				 */
				if(precedence[row][column]==ERR){
					
					if(next.getType() == Symbol.operator 
							&& Operator.getOperatorType(next.getName()) == Operator.HASH){
						throw new InvalidExpressionException("Bracket may not closed properly, recheck expression");
					}
					throw new InvalidExpressionException("Error occurred at symbol: "+ next);
				}
			}
			if(precedence[row][column]==S) {
				postStack.push(next);
			}else {
				popped = postStack.pop();
			}

			
			/*
			 * when Function symbol or IN operator found then insert null in postfix list.
			 * This is used for maintaining parameters for Function or IN operator
			 * 
			 * eg.
			 * For expression : add(a,b,c,d)
			 * symbol list : [add, (, a, b, c, d, )]
			 * postfix list: [null, a, b, c, d, add]
			 * 
			 * For expression : A IN (A,B,C,D)
			 * symbol list : [A, IN, (, A, B, C, D, )]
			 * postfix list: [A, null, A, B, C, D, IN]
			 *  
			 */
			if(next.getType() == Symbol.function 
					|| Operator.getOperatorType(next.getName()) == Operator.IN 
					|| Operator.getOperatorType(next.getName()) == Operator.NOT){
				postfix.add(null);
			}
			prev = next;
		}
		return postfix;
	}

	/**
	 * @author milan
	 * @param postfixList 
	 * @return finalExpression the last expression generated i.e to be evaluated
	 * @throws InvalidExpressionException 
	 * 
	 */
	private Expression postfixEvaluation(List<Symbol> postfixList) throws InvalidExpressionException {
		Stack<Expression> evaluateStack=new Stack<Expression>();
		
		for(Symbol nextSymbol:postfixList){
			if(nextSymbol==null){
				evaluateStack.push(null);
				continue;
			}
			switch(nextSymbol.getType()){
				case Symbol.operator:
					evaluateStack.push(createExpression(nextSymbol, evaluateStack));
					break;
				case Symbol.identifier:
					IdentifierExpression identifier=new IdentifierExpressionImpl((IdentifierSymbol)nextSymbol);
					evaluateStack.push(identifier);
					break;
				case Symbol.literal:
					LiteralExpression literal=new LiteralExpressionImpl((LiteralSymbol)nextSymbol);
					evaluateStack.push(literal);
					break;
				case Symbol.function:
					evaluateStack.push(createFunctionExpression(nextSymbol, evaluateStack));
			}
		}
		
		/*
		 * stack size has more than 1 elements means expression is not equally balanced so throw InvalidExpressionException
		 */
		if(evaluateStack.size() != 1){
			throw new InvalidExpressionException("Operator missing or invalid symbol");
		}
		return evaluateStack.pop();
	}
	
	/**
	 * @author milan
	 * @param nextSymbol
	 * @param evaluateStack
	 * @return The Function expression with all its arguments 
	 */
	private Expression createFunctionExpression(Symbol nextSymbol,Stack<Expression> evaluateStack){
		FunctionExpression functionExpression=functionMap.get(nextSymbol.getName());

		FunctionExpression functionExpressionClone= (FunctionExpression)functionExpression.clone();
		ArrayList<Expression> argumentList = new ArrayList<Expression>();
		//The arguments are received in reverse order 
		Expression functionArgument;
		while((functionArgument=evaluateStack.pop())!=null)
			argumentList.add(functionArgument);
		
		for(int index=argumentList.size()-1;index>=0;index--)
			functionExpressionClone.addArgument(argumentList.get(index));
		
		return functionExpressionClone;
	}
	
	
	/**
	 * @author milan
	 * @param nextSymbol
	 * @param evaluateStack
	 * @return The Logical expression 
	 * @throws InvalidExpressionException 
	 */
	private Expression createExpression(Symbol nextSymbol,Stack<Expression> evaluateStack) throws InvalidExpressionException{
		OperatorSymbol operator=(OperatorSymbol)nextSymbol;
		
		// if Logical operator found but stack does not have any operands then throw exception
		if(evaluateStack.isEmpty() == true){
			throw new InvalidExpressionException("Argument missing for '" + operator.getName() + "' operator, Syntax: " + operator.getOperatorType().syntax);
		}
		
		switch (operator.getOperatorCategory()) {
		case OperatorSymbol.logicalOperator:
			return createLogicalExpression((LogicalOperator)operator, evaluateStack);
		case OperatorSymbol.comparisionOperator:
			return createComparisionExpression((ComparisionOperator)operator,evaluateStack);
		default:
			throw new InvalidExpressionException("Invalid Operation, other operator found");
		}
	}
	
	private Expression createComparisionExpression(ComparisionOperator operator,
			Stack<Expression> evaluateStack) throws InvalidExpressionException {
		if(operator.getOperatorType() == Operator.IN){
			return handleInOperator(evaluateStack);
		}
		
		/*
		 * <,>,= requires two operands so
		 * IF total elements left in Stack is less than 2 then
		 * 		throw exception
		 */
		if(evaluateStack.size() < NUMBER_OF_OPERANDS_FOR_BINARY_OPERATOR){
			throw new InvalidExpressionException("Argument missing for '" + operator.getName() + "' operator, Syntax: " + operator.getOperatorType().syntax);
		}
		
		/*
		 *taking two operand for <,>,= operator
		 */
		Expression operand2 = evaluateStack.pop();
		Expression operand1 = evaluateStack.pop();
		if(operand1 == null || operand2 == null){
			throw new InvalidExpressionException("Invalid argument for '" + operator.getName() + "' operator, Syntax: " + operator.getOperatorType().syntax);
		}
		
		if(operand1.getExpressionType() == Expression.LogicalExpression || operand1.getExpressionType() == Expression.ComparisionExpression){
			throw new InvalidExpressionException("LHS operand is logical expression");
		}
		if(operand2.getExpressionType() == Expression.LogicalExpression || operand2.getExpressionType() == Expression.ComparisionExpression){
			throw new InvalidExpressionException("RHS operand is logical expression");
		}
		
		return new ComparisionExpressionImpl(operand1, operand2, operator);
	}

	private Expression createLogicalExpression(
			LogicalOperator operator,
			Stack<Expression> evaluateStack) throws InvalidExpressionException {
		
		if(operator.getOperatorType() == Operator.NOT){
			return handleNotOperator(operator, evaluateStack);
		}

		/*
		 * AND, OR requires two operands so
		 * IF total elements left in Stack is less than 2 then
		 * 		throw exception
		 */
		if(evaluateStack.size() < NUMBER_OF_OPERANDS_FOR_BINARY_OPERATOR){
			throw new InvalidExpressionException("Argument missing for '" + operator.getName() + "' operator, Syntax: " + operator.getOperatorType().syntax);
		}
		
		/*
		 *taking two operand for AND, OR operator
		 */
		Expression operand2 = evaluateStack.pop();
		Expression operand1 = evaluateStack.pop();
		if(operand1 == null || operand2 == null){
			throw new InvalidExpressionException("Invalid argument for '" + operator.getName() + "' operator, Syntax: " + operator.getOperatorType().syntax);
		}
		
		if(operand1.getExpressionType() != Expression.LogicalExpression && operand1.getExpressionType() != Expression.ComparisionExpression){
			throw new InvalidExpressionException("LHS operand is not a logical expression");
		}
		
		if(operand2.getExpressionType() != Expression.LogicalExpression && operand2.getExpressionType() != Expression.ComparisionExpression){
			throw new InvalidExpressionException("RHS operand is not a logical expression");
		}
		
		
		return new LogicalExpressionImpl((LogicalExpression)operand1, (LogicalExpression)operand2, operator);
	}

	private Expression handleNotOperator(LogicalOperator operator,
			Stack<Expression> evaluateStack) throws InvalidExpressionException {
		Expression operand1=null;
		List<Expression> operandsList=new ArrayList<Expression>();
		while((operand1=evaluateStack.pop()) != null){
			operandsList.add(operand1);
		}
		
		/*
		 * NOT operator must have only one operand, 
		 * if 0 or more than 1 operand found then
		 * 		throw InvalidExpressionException
		 */
		if(operandsList.size() != 1 ){
			throw new InvalidExpressionException("Argument missing for '" + operator.getName() + "' operator, Syntax: " + operator.getOperatorType().syntax);
		}
		
		
		//taking first element as Operand
		operand1 = operandsList.get(0);
		//Only Logical expression is allowed in NOT operation
		if(operand1.getExpressionType() != Expression.LogicalExpression && operand1.getExpressionType() != Expression.ComparisionExpression){
			throw new InvalidExpressionException("Only logical expression should be used with 'NOT' operator");
		}
		return new LogicalExpressionImpl((LogicalExpression)operand1, null, operator);
	}

	/**
	 * @param evaluateStack
	 * @return logicalExpression
	 * The method will make a final logicalExpression 
	 * e.g. A IN (1,2,3) will be    A = 1 || A = 2 || A = 3 
	 * @throws InvalidExpressionException 
	 */
	private LogicalExpression handleInOperator(Stack<Expression> evaluateStack) throws InvalidExpressionException {
		
		Expression operand1=null,operand2=null;
		LogicalExpression finalExpression=null,comparisionExpression=null;
		OROperator orOperator=new OROperator("OR");
		Equals equalOperator= new Equals("=");
		Stack<Expression> operandsStack=new Stack<Expression>();
		
		/*
		 * adding operands in operandsStack  
		 * Example: evalutionStack [1,2,3] (bottom->top)
		 *  operandsStack [3,2,1] (bottom->top)
		 */
		while((operand2=evaluateStack.pop()) != null){
			
			//Do not allow Logical Expression with 'IN' operator
			if(operand2.getExpressionType() == Expression.LogicalExpression || operand2.getExpressionType() == Expression.ComparisionExpression){
				throw new InvalidExpressionException("Logical Expression should not be used with 'IN' operator");
			}
			operandsStack.push(operand2);
		}
		
		if(operandsStack.isEmpty() == true || evaluateStack.isEmpty() == true){
			throw new InvalidExpressionException("Argument missing for 'IN' operator, Syntax: " + Operator.getOperatorType("IN").syntax);
		}
		
		operand1=evaluateStack.pop();
		//Do not allow Logical Expression with 'IN' operator
		if(operand1.getExpressionType() == Expression.LogicalExpression || operand1.getExpressionType() == Expression.ComparisionExpression){
			throw new InvalidExpressionException("Logical Expression should not be used with 'IN' operator");
		}
		
		//getting second operand from operandStack
		operand2 = operandsStack.pop();
		//building first comparison expression
		finalExpression = new ComparisionExpressionImpl(operand1, operand2, equalOperator);
		
		/*
		 * Building remaining comparison expression and wrapping it in LogicalExpression with OR operator
		 */
		while(operandsStack.isEmpty() == false){
			operand2=operandsStack.pop();
			comparisionExpression=new ComparisionExpressionImpl(operand1, operand2, equalOperator);
			finalExpression = new LogicalExpressionImpl(finalExpression, comparisionExpression, orOperator);
		}
		
		return finalExpression;
	}
	/**
	 * @author milan
	 * @param nextOperator
	 * @return index 
	 * TO Get the index of the symbol relative to the PrecedenceTable declared at the Top of Class
	 */
	private int getIndex(Symbol nextOperator){
		int index=0;	//AND OR IN NOT < = > (  ) ANY #
		if(nextOperator.getType()==Symbol.operator){
			OperatorSymbol operator=(OperatorSymbol) nextOperator;
			switch (operator.getOperatorType()) {
			case AND:
				index=0;
				break;
			case OR:
				index=1;
				break;
			case NOT:
				index=2;
				break;
			case IN:
				index=3;
				break;
			case LESSTHAN:
				index=4;
				break;
			case EQUALS:
				index=5;
				break;
			case GREATERTHAN:
				index=6;
				break;
			case OPENINGBRACES:
				index=7;
				break;
			case CLOSINGBRACES:
				index=8;
				break;
			case HASH:
				index=10;
				break;
			}
		}else{
			index=9;
		}
		return index;
	}

	
	/**
	 * This class is just for initialize a Symbol
	 * 
	 * NOTE: Never be used for any expression evolution operation 
	 * @author Chetan.Sankhala
	 */
	private static class InitialSymbol implements Symbol{
		
		private static final long serialVersionUID = 1L;

		@Override
		public int getType() {
			return Symbol.literal;
		}

		@Override
		public String getName() {
			return "initial symbol";
		}
	}
}

