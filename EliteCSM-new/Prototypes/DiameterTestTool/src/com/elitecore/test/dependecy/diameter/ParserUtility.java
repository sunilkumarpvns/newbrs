package com.elitecore.test.dependecy.diameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ParserUtility {
	private final static int S = 1; // Shift Operation 
	private final static int R = 2; // Reduce Operation
	private final static int A = 3; // Accept Operation
	private final static int E1 = 4;//Error : missing right parenthesis
	private final static int E2 = 5;//Error : missing operator
	private final static int E3 = 6;//Error : unbalanced right parenthesis
	private final static String E1_Message = "missing right parenthesis";
	private final static String E2_Message = "missing operator";
	private final static String E3_Message = "unbalanced right parenthesis";
	
	private final static int AND = 0; // '&&'
	private final static int OR = 1;  // '||'
	private final static int EITHER_OR = 2; // '^'
	private final static int LEFT_PARENTHESIS = 3; // '('
	private final static int RIGHT_PARENTHESIS = 4;// ')'
	private final static int $ = 5; // '$'	

	/**
	 * operatorTable contains operation for operator
	 * operatorTable[0][]/[][0] contains operation for &&
	 * operatorTable[1][]/[][1] contains operation for ||
	 * operatorTable[2][]/[][2] contains operation for (
	 * operatorTable[3][]/[][3] contains operation for )
	 * operatorTable[4][]/[][4] contains operation for $
	 */
	private final static int[][] operatorTable = {{R,R,R,S,R,R},
												  {R,R,R,S,R,R},
												  {R,R,R,S,R,R},
												  {S,S,S,S,R,E1},
												  {R,R,R,E2,R,R},
												  {S,S,S,S,E3,A}
												 };	
	
	public static List<String> convertToPostFixNotation(String inStr) throws ParserException{
		List<String> tokens = getOperatorParsingTokens(inStr);
		//System.out.println("String Tokens : " + tokens);
		//Iterator<String> inStrIterator  = tokens.iterator();
		List<String> postFixNotation = parseTokens(tokens);
		return postFixNotation;
	}
	public static List<String> getOperatorParsingTokens(String inStr){
		int pos=0;
		ArrayList<String> arrList = new ArrayList<String>();
		arrList.add("$");
		StringBuilder strBuilder = new StringBuilder();
		char[] charString = inStr.toCharArray();
		final int len = charString.length;
		while(pos < len){
			char ch = charString[pos];
			if(ch == '"'){
				pos++;
				while(pos < len){
					ch = charString[pos];
					if(ch == '\\'){
						strBuilder.append(charString[pos+1]);
						pos=pos+2;
						continue;
					}
					if(ch == '"'){
						//pos++;
						break;
					}
					strBuilder.append(ch);
					pos++;
				}
				if(strBuilder.toString().length() > 0)
					arrList.add(strBuilder.toString());
				strBuilder = new StringBuilder();
			}else if(ch == '&'){
				if(charString[pos+1]== '&'){
					if(strBuilder.toString().length()>0)
						arrList.add(strBuilder.toString());
					arrList.add("&&");
					pos++;
					strBuilder = new StringBuilder();
				}else{
					strBuilder.append(ch);	
				}
			}else if(ch == '|'){				
				if(charString[pos+1]== '|'){
					if(strBuilder.toString().length()>0)
						arrList.add(strBuilder.toString());
					arrList.add("||");
					pos++;
					strBuilder = new StringBuilder();
				}else{
					strBuilder.append(ch);	
				}
			}else if(ch == '^'){
				if(strBuilder.toString().length()>0)
					arrList.add(strBuilder.toString());
				arrList.add("^");
				strBuilder = new StringBuilder();
			}else if(ch == '('){
				if(strBuilder.toString().length()>0)
				arrList.add(strBuilder.toString());
				arrList.add("(");
				strBuilder = new StringBuilder();
			}else if(ch == ')'){
				if(strBuilder.toString().length()>0)
				arrList.add(strBuilder.toString());
				arrList.add(")");
				strBuilder = new StringBuilder();
			}else if(ch== ' ' || ch=='\r' || ch == '\n'){
				//do nothing
			}else {
				strBuilder.append(ch);				
			}				
			pos++;
		}
		if(strBuilder.toString().length()>0)
		arrList.add(strBuilder.toString());
		arrList.add("$");
		return arrList;
	}
	
	public static List<String> parseTokens(List<String> tokens) throws ParserException{
		Stack<String> oprStack = new Stack<String>();
		oprStack.push("$");//initialize the oprStack with $
		ArrayList<String> postFixExp = new ArrayList<String>();
		final int listSize = tokens.size(); 
		for(int i=0;i<listSize;i++){
			String token = tokens.get(i);
			int inTokenCode = getOperatorCode(token.charAt(0));
			if(inTokenCode == -1){ //not a operator, push into to postFixExp
				postFixExp.add(token);
			}else{ 
				int operationCode;
				int stackTopOperatorCode = getOperatorCode(oprStack.peek().charAt(0)); 
				operationCode = getOperation(stackTopOperatorCode, inTokenCode);		
				if(operationCode == S){
					oprStack.push(token);
				}else if(operationCode == R){
					if(inTokenCode == RIGHT_PARENTHESIS){ // operator is )
						String operator = oprStack.pop();
						int code = getOperatorCode(operator.charAt(0));
						while(code != LEFT_PARENTHESIS){ //while operator is not (
							postFixExp.add(operator);
							operator = oprStack.pop();
							code = getOperatorCode(operator.charAt(0));
						}				
					}else{
						while(operationCode == R){
							String op = oprStack.pop();
							postFixExp.add(op);
							stackTopOperatorCode = getOperatorCode(oprStack.peek().charAt(0)); 
							operationCode = getOperation(stackTopOperatorCode, inTokenCode);
						}						
						oprStack.push(token);
					}
				}else if(operationCode == A){
//					oprStack.push(token);
//					postFixExp.add(token);
				}else if(operationCode == E1){
					throw new ParserException(E1_Message);
				}else if(operationCode == E2){
					throw new ParserException(E2_Message);
				}else if(operationCode == E3){
					throw new ParserException(E3_Message);
				}				
			}
//			System.out.println("Token : " + token);
//			System.out.println("OprStack : " + oprStack);
//			System.out.println("PostFixStack : " + postFixExp);
		}
		return postFixExp;
	}
	public static int getOperation(int operator1,int Operator2){
		return operatorTable[operator1][Operator2];
	}
	public static int getOperatorCode(char operator){
		if(operator =='&'){
			return AND;
		}else if(operator =='|'){
			return OR;
		}else if(operator =='^'){
			return EITHER_OR;
		}else if(operator =='('){
			return LEFT_PARENTHESIS;
		}else if(operator ==')'){
			return RIGHT_PARENTHESIS;
		}else if(operator == '$'){
			return $;
		}
		return -1;// -1 = not a operator		
	}
	public static Map<String, Map<String, String>> convertToCustomerLevelPolicyMap(String inStr){
		Map<String, Map<String, String>> policyMap = new HashMap<String, Map<String, String>>();
		Map<String, String> attrMap = new HashMap<String, String>();
		List<String> tokens = getPolicyParsingTokens(inStr);
		//System.out.println("Tokens : " + tokens );
		Iterator<String> strIterator = tokens.iterator();		
		while(strIterator.hasNext()){
			String token = strIterator.next();
			if("$".equals(token)){
				token = strIterator.next();
				policyMap.put(token, getAllAttributes(strIterator));
			}else if(",".equals(token)){
				continue;
			}else if(token.contains("=")){
				String[] attr = splitKeyAndValue(token);
				attrMap.put(attr[0], attr[2]);
			}else{ 
				attrMap.putAll(getAllAttributes(strIterator));
			}			
		}
		policyMap.put("*", attrMap);
		return policyMap;
	}
	
	private static Map<String, String> getAllAttributes(Iterator<String> strIterator){
		Map<String, String> attrMap = new HashMap<String, String>();
		while(strIterator.hasNext()){
			String token = strIterator.next();
			if("$".equals(token) || ")".equals(token)){
				return attrMap;
			}
			if(",".equals(token) || "(".equals(token)){
				continue;
			}
			String[] attr = splitKeyAndValue(token);			
			attrMap.put(attr[0], attr[2]);
		}
		return attrMap;
	}
	public static Map<String, Map<String, ArrayList<String>>> convertToReplyPolicyMap(String inStr){
		Map<String, Map<String, ArrayList<String>>> policyMap = new HashMap<String, Map<String, ArrayList<String>>>();
		Map<String, ArrayList<String>> attrMap = new HashMap<String, ArrayList<String>>();
		List<String> tokens = getPolicyParsingTokens(inStr);
		//System.out.println("Tokens : " + tokens );
		Iterator<String> strIterator = tokens.iterator();		
		while(strIterator.hasNext()){
			String token = strIterator.next();
			if("$".equals(token)){
				token = strIterator.next();
				policyMap.put(token,getAllAttributesList(strIterator)); 
			}else if(",".equals(token)){
				continue;
			}else if("=".equals(token)){
				String[] attr = splitKeyAndValue(token);
				ArrayList<String> valueList = attrMap.get(attr[0]);
				if(valueList==null){
					valueList = new ArrayList<String>();
					valueList.add(attr[2]);
					attrMap.put(attr[0], valueList);
				}else{
					valueList.add(attr[2]);
				}
			}else if(" ".equals(token)){
				 continue;
			}else if(token.startsWith("\n") || token.startsWith("\r")){
				continue;
			}else{
				attrMap.putAll(getAllAttributesList(strIterator));
			}			
		}
		policyMap.put("*", attrMap);
		return policyMap;
	}

	public static List<String> getReplyItemTokens(String inStr){
		StringBuilder currentToken = new StringBuilder();
		ArrayList<String> replyItemList = new ArrayList<String>();
		int pos=0;
		char[] charString = inStr.toCharArray();
		final int len = charString.length;
		while(pos<len){
			if(charString[pos]=='"'){
				pos++;
				char ch;
				while(pos < len){
					ch = charString[pos];
					if(ch == '\\'){
						currentToken.append(charString[pos+1]);
						pos=pos+2;
						continue;
					}
					if(ch == '"'){
						pos++;
						break;
					}
					currentToken.append(ch);
					pos++;
				}
				continue;
			}else if(charString[pos]=='(' || charString[pos] ==')'||charString[pos]==','){
				if(currentToken.length()>0)
					replyItemList.add(currentToken.toString());
				replyItemList.add(String.valueOf(charString[pos]));
				currentToken = new StringBuilder();
				pos++;
				continue;
			}else if(charString[pos]=='\r'|| charString[pos]=='\n'|| charString[pos]==' '){
				pos++;
				continue;
			}
			currentToken.append(charString[pos]);
			pos++;
		}
		if(currentToken.length()>0)
			replyItemList.add(currentToken.toString());
		return replyItemList;
	}
	
	public static Map<String,Map<String,Map<String,ArrayList<String>>>> parseCustomerReplyItems(String strReplyItems) throws ParserException{
		List<String> replyItemList = getReplyItemTokens(strReplyItems);
		
		int policyDepthLevel=0;
		if(replyItemList==null)			
			return null;
		Map<String,Map<String,Map<String,ArrayList<String>>>> policyOverrideDataMap = new HashMap<String, Map<String,Map<String,ArrayList<String>>>>();
		Map<String, Map<String, ArrayList<String>>> defaultReplyItemMap = new HashMap<String, Map<String,ArrayList<String>>>();
		final int listSize = replyItemList.size();
		for(int i=0;i<listSize;i++){
			String currentToken = replyItemList.get(i);
				
			if(currentToken.charAt(0) =='$' && !currentToken.startsWith("$REQ:") && !currentToken.startsWith("$RES:")){
				Map<String,Map<String,ArrayList<String>>> policyWiseReplyItemMap = new HashMap<String, Map<String,ArrayList<String>>>();
				if(!replyItemList.get(++i).equals("(")){
					throw new ParserException("Invalid Token : " + currentToken + " in reply item : " + strReplyItems);
				}
				policyDepthLevel=0;
				while(++i<listSize){
					String tmpString = replyItemList.get(i);
					if(")".equals(tmpString) && policyDepthLevel==0)
						break;
					if("(".equals(tmpString)){
						policyDepthLevel++;
					}else if(")".equals(tmpString)){
						policyDepthLevel--;
					}else{
						if(tmpString.startsWith("$REQ:")|| tmpString.startsWith("$RES:")){
							Map<String,ArrayList<String>> replyItemBasedOnResMap = new LinkedHashMap<String, ArrayList<String>>();
							if(!replyItemList.get(++i).equals("(")){
								throw new ParserException("Invalid Token : " + replyItemList.get(i)+ " in reply item : " + strReplyItems);
							}
							while(++i<listSize){
								String replyItems = replyItemList.get(i);
								if(")".equals(replyItems)){
									i++;
									break;
								}else if(",".equals(replyItems))
									continue;
								String[] tmp = splitKeyAndValue(replyItems);
								if(tmp.length!=3){
									throw new ParserException("Invalid Token : " + replyItems+ " in reply item : " + strReplyItems);
								}
								ArrayList<String> valueList = replyItemBasedOnResMap.get(tmp[0]);
								if(valueList == null){
									valueList = new ArrayList<String>();
									replyItemBasedOnResMap.put(tmp[0], valueList);
								}
								valueList.add(tmp[2]);
							}
							policyWiseReplyItemMap.put(tmpString, replyItemBasedOnResMap);
						}else if(",".equals(tmpString)){
							continue;
						}else{
							Map<String,ArrayList<String>> replyItem = policyWiseReplyItemMap.get("*");
							if(replyItem == null){
								replyItem = new HashMap<String, ArrayList<String>>();
								policyWiseReplyItemMap.put("*", replyItem);
							}
							String[] tmp = splitKeyAndValue(tmpString);
							ArrayList<String> valueList = replyItem.get(tmp[0]);
							if(valueList == null){
								valueList = new ArrayList<String>();
								replyItem.put(tmp[0], valueList);
							}
							valueList.add(tmp[2]);
						}
					}
				}
				policyOverrideDataMap.put(currentToken.substring(1), policyWiseReplyItemMap);
				continue;
			}else if(currentToken.startsWith("$REQ:")||currentToken.startsWith("$RES:")){
				Map<String,ArrayList<String>> replyItemBasedOnReqMap = new HashMap<String, ArrayList<String>>();
				if(!replyItemList.get(++i).equals("(")){
					throw new ParserException("Invalid Token : " + replyItemList.get(i)+ " in reply item : " + strReplyItems);
				}
				while(++i<listSize){
					String replyItems = replyItemList.get(i);
					if(")".equals(replyItems)){
						i++;
						break;
					}else if(",".equals(replyItems)){
						continue;
					}
					String[] tmp = splitKeyAndValue(replyItems);
					if(tmp.length!=3){
						throw new ParserException("Invalid Token : " + replyItems + " in reply item : " + strReplyItems);
					}
					ArrayList<String> valueList = replyItemBasedOnReqMap.get(tmp[0]);
					if(valueList == null){
						valueList = new ArrayList<String>();
						replyItemBasedOnReqMap.put(tmp[0], valueList);
					}
					valueList.add(tmp[2]);
				}
				defaultReplyItemMap.put(currentToken, replyItemBasedOnReqMap);
			}else{
				Map<String,ArrayList<String>> replyItems = defaultReplyItemMap.get("*");
				if(replyItems==null){
					replyItems = new HashMap<String, ArrayList<String>>();
					defaultReplyItemMap.put("*", replyItems);
				}
				if(",".equals(currentToken))
						continue;
				String[] tmp = splitKeyAndValue(currentToken);
				if(tmp.length!=3){
					throw new ParserException("Invalid Token : " + replyItems + " in reply item : " + strReplyItems);
				}
				ArrayList<String> valueList = replyItems.get(tmp[0]);
				if(valueList == null){
					valueList = new ArrayList<String>();
					replyItems.put(tmp[0], valueList);
				}
				valueList.add(tmp[2]);
			}
		}
		policyOverrideDataMap.put("*",defaultReplyItemMap);
		return policyOverrideDataMap;
	}

	
	public static Map<String,Map<String,ArrayList<String>>> parseReplyItems(String strReplyItems) throws ParserException{
		List<String> replyItemList = getReplyItemTokens(strReplyItems);
		
		if(replyItemList==null)			
			return null;
		Map<String, Map<String, ArrayList<String>>> replyItemMap = new HashMap<String, Map<String,ArrayList<String>>>();
		final int listSize = replyItemList.size();
		for(int i=0;i<listSize;i++){
			String currentToken = replyItemList.get(i);
				
			if(currentToken.startsWith("$REQ:")||currentToken.startsWith("$RES:")){
				Map<String,ArrayList<String>> replyItemBasedOnReqMap = new LinkedHashMap<String, ArrayList<String>>();
				if(!replyItemList.get(++i).equals("(")){
					throw new ParserException("Invalid Token : " + replyItemList.get(i)+ " in reply item : " + strReplyItems);
				}
				while(++i<listSize){
					String replyItems = replyItemList.get(i);
					if(")".equals(replyItems)){
						i++;
						break;
					}else if(",".equals(replyItems)){
						continue;
					}
					String[] tmp = splitKeyAndValue(replyItems);
					if(tmp.length!=3){
						throw new ParserException("Invalid Token : " + replyItems + " in reply item : " + strReplyItems);
					}
					ArrayList<String> valueList = replyItemBasedOnReqMap.get(tmp[0]);
					if(valueList == null){
						valueList = new ArrayList<String>();
						replyItemBasedOnReqMap.put(tmp[0], valueList);
					}
					valueList.add(tmp[2]);
				}
				replyItemMap.put(currentToken, replyItemBasedOnReqMap);
			}else{
				Map<String,ArrayList<String>> replyItems = replyItemMap.get("*");
				if(replyItems==null){
					replyItems = new LinkedHashMap<String, ArrayList<String>>();
					replyItemMap.put("*", replyItems);
				}
				if(",".equals(currentToken))
						continue;
				String[] tmp = splitKeyAndValue(currentToken);
				if(tmp.length!=3){
					throw new ParserException("Invalid Token : " + replyItems + " in reply item : " + strReplyItems);
				}
				ArrayList<String> valueList = replyItems.get(tmp[0]);
				if(valueList == null){
					valueList = new ArrayList<String>();
					replyItems.put(tmp[0], valueList);
				}
				valueList.add(tmp[2]);
			}
		}
		return replyItemMap;
	}

	public static Map<String, String> convertToPolicyWiseReplyItemMap(String inStr){
		Map<String, String> policyMap = new HashMap<String, String>();
		int pos=0;
		StringBuilder strBuilder =null;
		StringBuilder policyName = null;
		char[] charString = inStr.toCharArray();
		final int len = charString.length;
		while(pos < len){
			if(charString[pos]=='$'){
				policyName = new StringBuilder();
				pos++;
				while(pos < len && charString[pos]!='('){
					policyName.append(charString[pos]);
					pos++;
				}
			}
			if(charString[pos]=='('){
				int stop=0;
				pos++;
				strBuilder = new StringBuilder();
				while(pos < len){
					char currentChar = charString[pos];
					if(currentChar ==')'&& stop==0)
						break;
					strBuilder.append(currentChar);
					if(currentChar=='('){
						stop++;
					}else if(currentChar==')'){
						stop--;
					}
					pos++;
				}
				policyMap.put(policyName.toString(),strBuilder.toString());
				pos++;
			}
			pos++;
		}
		//System.out.println("Tokens : " + tokens );
		return policyMap;
	}
	private static Map<String, ArrayList<String>> getAllAttributesList(Iterator<String> strIterator){
		Map<String, ArrayList<String>> attrMap = new LinkedHashMap<String, ArrayList<String>>();
		while(strIterator.hasNext()){
			String token = strIterator.next();
			if("$".equals(token) || ")".equals(token)){
				return attrMap;
			}
			if(",".equals(token) || "(".equals(token)){
				continue;
			}
			String[] attr = splitKeyAndValue(token);
			ArrayList<String> attributeList = attrMap.get(attr[0]);
			if(attributeList==null){
				attributeList = new ArrayList<String>();
				attributeList.add(attr[2]);
				attrMap.put(attr[0], attributeList);
			}else{
				attributeList.add(attr[2]);
			}
		}
		return attrMap;
	}
	public static String[] splitKeyAndValue(String attr){
		/***
		 * string[0] = key
		 * string[1] = operator
		 * string[2] = value
		 */
		//System.out.println("Attr : " + attr);
		String[] splitedAttr = new String[3];
		StringBuilder strBuilder = new StringBuilder();
		int pos=0;
		char[] charString = attr.toCharArray();
		final int len = charString.length;
		while(pos < len){
			char ch = charString[pos];
			if(ch == '='){
				if(strBuilder.toString().length()>0)
					splitedAttr[0] = strBuilder.toString();
				splitedAttr[1] = "=";
				splitedAttr[2] = attr.substring(pos+1);
				return splitedAttr;
			}else if(ch == '!'){
				if(charString[pos+1] == '='){
					pos++;
					if(strBuilder.toString().length()>0)
						splitedAttr[0] = strBuilder.toString();
					splitedAttr[1] = "!=";
					splitedAttr[2] = attr.substring(pos+1);
					return splitedAttr;
				}				
			}else if(ch == '<'){
				if(charString[pos+1] == '='){
					pos++;
					if(strBuilder.toString().length()>0)
						splitedAttr[0] = strBuilder.toString();
					splitedAttr[1] = "<=";
					splitedAttr[2] = attr.substring(pos+1);					
				}else{						
					if(strBuilder.toString().length()>0)
						splitedAttr[0] = strBuilder.toString();
					splitedAttr[1] = "<";
					splitedAttr[2] = attr.substring(pos+1);								
				}
				return splitedAttr;
			}else if(ch == '>'){				
				if(charString[pos+1] == '='){
					pos++;
					if(strBuilder.toString().length()>0)
						splitedAttr[0] = strBuilder.toString();
					splitedAttr[1] = ">=";
					splitedAttr[2] = attr.substring(pos+1);					
				}else{						
					if(strBuilder.toString().length()>0)
						splitedAttr[0] = strBuilder.toString();
					splitedAttr[1] = ">";
					splitedAttr[2] = attr.substring(pos+1);								
				}
				return splitedAttr;
			}else {
				strBuilder.append(ch);
				pos++;
			}						
		}
		if(strBuilder.toString().length()>0)
			splitedAttr[0] = strBuilder.toString();
		return splitedAttr;
	}
	
	public static String[] splitString(String strAttribute,char... splitChar){
		ArrayList<String> splittedExpression = new ArrayList<String>();
		StringBuilder currentExpression = new StringBuilder();
		int pos=0;
		if(strAttribute == null)
			return null;
		char[] charString = strAttribute.toCharArray();
		final int len  = charString.length;
		while(pos<len){
			char currentChar = charString[pos];
			if(currentChar == '"'){
				pos++;
				while(pos < len){
					currentChar = charString[pos];
					if(currentChar == '\\'){
						currentExpression.append(charString[pos+1]);
						pos=pos+2;
						continue;
					}
					if(currentChar == '"'){
						pos++;
						break;
					}
					currentExpression.append(currentChar);
					pos++;
				}
			} else if(containsChar(splitChar, currentChar)){
				if(currentExpression.toString().length() > 0)
				splittedExpression.add(currentExpression.toString());
				currentExpression = new StringBuilder();
				pos++;
				continue;
			}else{
				currentExpression.append(currentChar);
				pos++;
			}
		}
		if(currentExpression!=null && currentExpression.length()!=0)
			splittedExpression.add(currentExpression.toString());
		return splittedExpression.toArray(new String[splittedExpression.size()]);
	}
	
	/**
	 *  Returns true if characters Array contains lookupChar
	 */
	public static boolean containsChar (char[] characters, char lookupChar) {
		if (characters != null) {
			for (int i=0 ; i<characters.length ; i++) { 
				if (characters[i] == lookupChar) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static List<String> getPolicyParsingTokens(String inStr){
		int pos=0;
		boolean bIgnore = false;
		ArrayList<String> arrList = new ArrayList<String>();		
		StringBuilder strBuilder = new StringBuilder();
		char[] charString = inStr.toCharArray();
		final int len = charString.length;
		while(pos < len){
			char ch = charString[pos];
			if(ch == '"'){
				pos++;
				while(pos < len){
					ch = charString[pos];
					if(ch == '\\'){
						strBuilder.append(charString[pos+1]);
						pos=pos+2;
						continue;
					}
					if(ch == '"'){
						pos++;
						break;
					}
					strBuilder.append(ch);
					pos++;
				}
				if(strBuilder.toString().length() > 0)
					arrList.add(strBuilder.toString());
				strBuilder = new StringBuilder();
			}else if(ch == '('){		
				bIgnore = true;
				arrList.add("(");		
				pos++;
				while(pos < len){					
					ch = charString[pos];
					if(ch == ',' || ch == ')' || ch == '"')
						break;
					strBuilder.append(ch);
					pos++;
				}
				if(strBuilder.toString().length()>0 && pos < len && charString[pos] != '"'){
					arrList.add(strBuilder.toString());
					strBuilder = new StringBuilder();		
				}
			}else if(ch == ')'){
				bIgnore = false;
				if(strBuilder.toString().length()>0)
					arrList.add(strBuilder.toString());
				arrList.add(")");							
				strBuilder = new StringBuilder();
				pos++;
			}else if(ch == '$' && !bIgnore){
				arrList.add("$");
				pos++;
				while(pos < len){
					ch = charString[pos];
					if(ch == '(' || ch == '"')
						break;
					strBuilder.append(ch);
					pos++;
				}				
				if(strBuilder.toString().length()>0 && pos < len && charString[pos] != '"'){
					arrList.add(strBuilder.toString());
					strBuilder = new StringBuilder();
				}
			}else if(ch == ','){
				if(strBuilder.toString().length()>0)
					arrList.add(strBuilder.toString());
				strBuilder = new StringBuilder();
				arrList.add(",");
				pos++;
				while( pos < len){
					ch = charString[pos];
					if(ch == ',' || ch == ')' || ch == '$' || ch == '"')
						break;
					strBuilder.append(ch);
					pos++;
				}
				if(strBuilder.toString().length()>0 && pos < len && charString[pos] != '"'){
					arrList.add(strBuilder.toString());
					strBuilder = new StringBuilder();
				}
			}else if(ch== ' '){
				pos++;
			}else {
				strBuilder.append(ch);
				pos++;
			}						
		}
		if(strBuilder.toString().length()>0)
			arrList.add(strBuilder.toString());		
		return arrList;
	}
	
	public static String getRegxPattern(String strPattern) {

    	char[] patternCharacters = strPattern.toCharArray();
    	StringBuilder newPattern = new StringBuilder();
    	String strRegx = null;
		for(int i=0;i<patternCharacters.length;i++){
			if(patternCharacters[i]=='\\'){
				i++;
				if(patternCharacters[i]=='*' || patternCharacters[i]=='?' || patternCharacters[i]=='\\')
					newPattern.append("\\" +patternCharacters[i]);
				else
					newPattern.append("\\\\" + patternCharacters[i]);
				
				continue;
			}else if(patternCharacters[i]=='*'){
				newPattern.append("[\\p{ASCII}]*");
			}else if(patternCharacters[i]=='?'){
				newPattern.append("[\\p{ASCII}]");
        }else
        	newPattern.append(patternCharacters[i]);
		}
		strRegx = newPattern.toString();
    	return strRegx;
    }
	public static void main(String args[]){
		//String strReplyItem = "$pn($REQ:0:1(0:9=$REQ:0:4,0:6=12),0:7=5),0:6=6";
		//String strReplyItem = "0:6=6";
		//ArrayList<String> replyItemList = parseCustomerReplyItems(strReplyItem);
//		try {
			//System.out.println(convertToCustomerLevelPolicyMap(strReplyItem));
//		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//System.out.println(replyItemList);
	}
}