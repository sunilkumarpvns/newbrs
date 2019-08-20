package com.elitecore.aaa.core.util.cli;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.Configuration;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;

public class ConfigCommand extends EliteBaseCommand {

	AAAServerContext serverContext = null;
	AAAServerConfiguration serverConfiguration = null;

	private static final String CONFIG_HELP_START = "{'config':{";
	private static final String CONFIG_HELP_END = "}}";
	
	Map<String,Map<String,String>> moduleParameterMap = new HashMap<String, Map<String,String>>();
	Map<String,String> moduleMap = new HashMap<String,String>();
	
	public ConfigCommand(AAAServerContext aaaServerContext) {
		serverContext = aaaServerContext;
	}

	public void init(AAAServerConfiguration serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
		readConfigClassProperties();
	}

	public void readConfigClassProperties() {
		printDetail(serverConfiguration, 1);
	}

	public String formalName(String name) {
		StringBuilder strBuilder = new StringBuilder();
		if (name.startsWith("get")) {
			name = name.substring(3, name.length());
		}
		if( name.endsWith("Impl")){
			name = name.substring(0, name.indexOf("Impl"));
		}
		int length = name.length();
		for (int i = 0; i < length; i++) {
			char ch = name.charAt(i);
			if (ch >= 'A' && ch <= 'Z') {
				if (i > 0 && i < length - 1) {
					char previousChar = name.charAt(i - 1);
					char nextChar = name.charAt(i + 1);
					if ((previousChar < 'A' || previousChar > 'Z')
							|| (nextChar >= 'a' && nextChar <= 'z')) {
						strBuilder.append("-");
					}
				}
			}
			strBuilder.append(ch);
		}
		return strBuilder.toString();
	}

	public void printDetail(Object obj, int level) {
		Class<?> test = obj.getClass();
		Map<String,String> keyValueMap = new HashMap<String, String>();
		Method[] declaredMethods = test.getMethods();
		
		for (int i = 0; i < declaredMethods.length; i++) {
			try {
				if(isReadableProperty(declaredMethods[i])){
					if (declaredMethods[i].getName().startsWith("get")) {
						Type t = declaredMethods[i].getGenericReturnType();
						String methodName = formalName(declaredMethods[i].getName());
						Object returnObj;
						try {							
							returnObj = declaredMethods[i].invoke(obj, (Object[])null);
							
							if (isConfigurationObject(declaredMethods[i])) {
								if(returnObj != null){
									printDetail(returnObj, level + 1);
								}else{
									moduleParameterMap.put(formalName(((Class<?>)t).getSimpleName()), null);
								}
							}else{
								if(!methodName.contains("Class"))
								keyValueMap.put(methodName,getStringValue(returnObj, 1));
							}
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		moduleMap.put(formalName(obj.getClass().getSimpleName()), obj.toString());
		if(keyValueMap.size() > 0)
			moduleParameterMap.put(formalName(obj.getClass().getSimpleName()), keyValueMap);
	}

	private String getStringValue(Object returnObj, int j) {
		String resp="";
		StringBuilder strBuilder=new StringBuilder();
		if(returnObj instanceof String[]){				
			String[] stringArray=(String[]) returnObj;
			strBuilder.append("{");
			if(stringArray.length>0){
				for(int i=0;i<stringArray.length;i++){
					strBuilder.append(" "+stringArray[i]+((i+1)==stringArray.length?"":","));
				}
			}
			strBuilder.append(" }");
			resp=strBuilder.toString();
		}else if(returnObj instanceof String){
			String str=(String) returnObj;
			strBuilder.append(str);
			resp=strBuilder.toString();
		}else if(returnObj instanceof Boolean){
			boolean value=(Boolean) returnObj;
			strBuilder.append(String.valueOf(value));
			resp=strBuilder.toString();
		}else if(returnObj instanceof Integer){
			Integer value=(Integer) returnObj;
			strBuilder.append(String.valueOf(value));
			resp=strBuilder.toString();
		}else if(returnObj instanceof Integer[]){
			Integer[] value=(Integer[]) returnObj;
			strBuilder.append("{");
			for(int i=0;i<value.length;i++){
				strBuilder.append(" "+value[i].toString()+((i+1)==value.length?"":","));
			}
			strBuilder.append("}");
			resp=strBuilder.toString();
		}else if(returnObj instanceof List){
			List<?> list=(List<?>) returnObj;
			Iterator<?> itr = list.iterator();
			strBuilder.append("{"+getNewLine(j));
			int countthree=0;
			while(itr.hasNext()){				
				String value=getStringValue(itr.next(), j+1);
				if(countthree==3){
					strBuilder.append(getNewLine(j)+" "+value+(itr.hasNext()?" , ":""));
					countthree=0;
				}else{
					strBuilder.append(" "+value+(itr.hasNext()?" , ":""));
					countthree++;
				}
			}
			strBuilder.append(getNewLine(j)+"}");
			resp=strBuilder.toString();
		}else if(returnObj instanceof Set){
			Set<?> set=(Set<?>) returnObj;
			strBuilder.append("{");
			int countthree=0;
			Iterator<?> iter = set.iterator();
			while (iter.hasNext()) {
				String value=getStringValue(iter.next(), j+1);
				if(countthree==3){
					strBuilder.append(" "+value+(iter.hasNext()?" , ":""));
					countthree=0;
				}else{
					strBuilder.append(" "+value+(iter.hasNext()?" , ":""));
					countthree++;
				}
			}
			strBuilder.append(getNewLine(j)+"}");
			resp=strBuilder.toString();
		}else if(returnObj instanceof Map){
			Map<?, ?> map = (Map<?, ?>) returnObj;
			Iterator<?> entries = map.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entries.next();
				resp=getStringValue(entry.getKey(), j+1);
				String value = getStringValue(entry.getValue(), j+1);
				strBuilder.append(getNewLine(j)+"Key = " + resp + " : Value = " + value);
			}
			strBuilder.append(getNewLine(j));
			resp=strBuilder.toString();
		}else if(returnObj instanceof Byte){
			Byte b = (Byte) returnObj;
			strBuilder.append(" "+b.toString());
			resp=strBuilder.toString();
		}else if(returnObj instanceof Byte[]){
			Byte[] b = (Byte[]) returnObj;
			strBuilder.append("{");
			for(int i=0;i<b.length;i++){
				strBuilder.append(" "+b[i]+"");
			}
			strBuilder.append("}");
			resp=strBuilder.toString();
		}else if(returnObj instanceof Long[]){
			Long[] l = (Long[]) returnObj;
			strBuilder.append("{");
			for(int i=0;i<l.length;i++){
				strBuilder.append(" "+l[i]+"");
			}
			strBuilder.append("}");
			resp=strBuilder.toString();
		}else if(returnObj instanceof Double[]){
			Double[] db = (Double[]) returnObj;
			strBuilder.append("{");
			for(int i=0;i<db.length;i++){
				strBuilder.append(" "+db[i]+"");
			}
			strBuilder.append("}");
			resp=strBuilder.toString();
		}else if(returnObj instanceof Short[]){
			Short[] sh = (Short[]) returnObj;
			strBuilder.append("{");
			for(int i=0;i<sh.length;i++){
				strBuilder.append(" "+sh[i]+"");
			}
			strBuilder.append("}");
			resp=strBuilder.toString();
		}else if(returnObj instanceof Float[]){
			Float[] fl = (Float[]) returnObj;
			strBuilder.append("{");
			for(int i=0;i<fl.length;i++){
				strBuilder.append(" "+fl[i]+"");
			}
			strBuilder.append("}");
			resp=strBuilder.toString();
		}else{
			resp=getValueFromValueType(returnObj);
		}		
		return resp;
	}
	
	private String getValueFromValueType(Object returnObj) {
		String res;
		StringBuilder strBuilder=new StringBuilder();
		if(returnObj instanceof int[]){
			int[] i = (int[]) returnObj;
			strBuilder.append("{");
			for(int j=0;j<i.length;j++){
				strBuilder.append(" "+i[j]);
			}
			strBuilder.append(" }");
			res=strBuilder.toString();
		}else if(returnObj instanceof byte[]){
			byte[] b = (byte[]) returnObj;
			strBuilder.append("{");
			for(int j=0;j<b.length;j++){
				strBuilder.append(" "+b[j]);
			}
			strBuilder.append(" }");
			res=strBuilder.toString();
		}else if(returnObj instanceof char[]){
			char[] ch = (char[]) returnObj;
			strBuilder.append("{");
			for(int j=0;j<ch.length;j++){
				strBuilder.append(" "+ch[j]);
			}
			strBuilder.append(" }");
			res=strBuilder.toString();
		}else if(returnObj instanceof long[]){
			long[] l = (long[]) returnObj;
			strBuilder.append("{");
			for(int j=0;j<l.length;j++){
				strBuilder.append(" "+l[j]);
			}
			strBuilder.append(" }");
			res=strBuilder.toString();
		}else if(returnObj instanceof short[]){
			short[] sh = (short[]) returnObj;
			strBuilder.append("{");
			for(int j=0;j<sh.length;j++){
				strBuilder.append(" "+sh[j]);
			}
			strBuilder.append(" }");
			res=strBuilder.toString();
		}else if(returnObj instanceof float[]){
			float[] fl = (float[]) returnObj;
			strBuilder.append("{");
			for(int j=0;j<fl.length;j++){
				strBuilder.append(" "+fl[j]);
			}
			strBuilder.append(" }");
			res=strBuilder.toString();
		}else if(returnObj instanceof double[]){
			double[] db = (double[]) returnObj;
			strBuilder.append("{");
			for(int j=0;j<db.length;j++){
				strBuilder.append(" "+db[j]);
			}
			strBuilder.append(" }");
			res=strBuilder.toString();
		}else{
			res=(returnObj!= null?(returnObj.toString()):"NULL");
		}
		return res;
	}

	public String getNewLine(int i){
		StringBuilder strBuilder=new StringBuilder();
		if(i>0){
			strBuilder.append("\n"+fillChar("", 30, ' ')+fillChar("", 5*i, ' '));
		}
		return strBuilder.toString();
	}

	public boolean isReadableProperty(Method m){
		if (!m.getName().startsWith("get")) 
			return false;
		int modifier = m.getModifiers();
		if( Modifier.isPrivate(modifier) || Modifier.isStatic(modifier) || Modifier.isProtected(modifier) )
			return false;
		
		if(m.getParameterTypes().length > 0)
			return false;
		
		return true;
	}
	
	public boolean isConfigurationObject(Method m){
		Type t = m.getGenericReturnType();
		if(!( t instanceof ParameterizedType)){
			return isImplementConfiguration(((Class<?>) t));
		}
		return false;
	}

	public boolean isImplementConfiguration(Class<?> interfc){
		if (interfc == Configuration.class) {
			return true;
		}
		for (Class<?> cls : interfc.getInterfaces()) {
			if(isImplementConfiguration(cls))
				return true;
		}
		return false;
	}
	
	@Override
	public String execute(String parameter) {
		StringTokenizer stkTokenizer = new StringTokenizer(parameter," ");
		String parameter1="";
		if(stkTokenizer.countTokens()==0){
			return getHelpMsg();
		}else{
			parameter1 = stkTokenizer.nextToken();
		}
		
		if(!stkTokenizer.hasMoreTokens()){
			if(parameter1.equals("MODULE")){
				return getModules();			
			}else if(parameter1.equalsIgnoreCase("?") || parameter1.equalsIgnoreCase("help")){
				return getHelpMsg();
			}else{
				StringBuilder strBuilder = new StringBuilder();
				if (moduleParameterMap.get(parameter1)!=null){
					Map<String,String> keyValueMap = moduleParameterMap.get(parameter1);
					strBuilder.append(parameter1+" : \n");
					strBuilder.append(fillChar(" ", 25)+"CONFIGURATION"+" : \n\n");
					boolean hasSunModules=false;
					int srNO=1;
					if(keyValueMap != null){
						for(Entry<String, String> moduleEntry : keyValueMap.entrySet()){			
							String parameterName = moduleEntry.getKey();
							if(!parameterName.endsWith("Configuration")){
								strBuilder.append(fillChar("", 2,' ')+fillChar((srNO++)+". "+parameterName, 30,' ')+" : "+moduleEntry.getValue()+"\n\n");
							}else{
								hasSunModules=true;
							}
						}
					}
					strBuilder.append("\n");
										
					if(keyValueMap != null && hasSunModules==true){
						strBuilder.append(fillChar(" ", 25)+"SUB MODULES "+" : \n\n");
						for(Entry<String, String> moduleEntry : keyValueMap.entrySet()){			
							String moduleName = moduleEntry.getKey();
							if(moduleName.endsWith("Configuration")){
								strBuilder.append(fillChar("",2,' ')+moduleName+"\n");
							}
						}
					}
					strBuilder.append("\n");					
					return strBuilder.toString();
					
				}else{
					return parameter1 + " is not Configured";
				}
			}
		}
		
		//  config AAA-Server-Configuration MODULES   or   
		//	config AAA-Server-Configuration Key 	  or 
		//  config AAA-Server-Configuration Diameter-Config
		else if(stkTokenizer.hasMoreTokens()){
			String parameter2 = stkTokenizer.nextToken();
			StringBuilder strBuilder = new StringBuilder();
			if(parameter2.equalsIgnoreCase("MODULES")){
				//config AAA-Config MODULES
				Map<String,String> keyValueMap = moduleParameterMap.get(parameter1);
				if(keyValueMap != null){
					for(Entry<String, String> moduleEntry : keyValueMap.entrySet()){			
						String moduleName = moduleEntry.getKey();
						if(moduleName.endsWith("Configuration")){
							strBuilder.append(moduleName+"\n");
						}
					}
				}
				strBuilder.append("\n");
				return strBuilder.toString() ;
			}else if(parameter2.endsWith("Configuration")){
				//config AAA-Config Diameter-Config
				strBuilder.append(parameter2+" : \n");
				Map<String,String> keyValueMap2 = moduleParameterMap.get(parameter1);
				
				if(keyValueMap2.get(parameter2)!=null && !keyValueMap2.get(parameter2).equals("")){
					strBuilder.append(fillChar(" ", 25)+"CONFIGURATION "+" : \n");
					strBuilder.append(keyValueMap2.get(parameter2));
				}else{
					strBuilder.append(fillChar(" ", 25)+"Configuration found NULL "+" : \n");
				}				
				return strBuilder.toString();
			}else{
				// config AAA-Config Key
				Map<String,String> keyValueMap = moduleParameterMap.get(parameter1);
				if(keyValueMap != null){
					String moduleValue = keyValueMap.get(parameter2);
					if(moduleValue==null){
						return parameter1 + " has no Paramener with Name : "+parameter2;
					}
					strBuilder.append(parameter2+" : ");
					strBuilder.append(moduleValue);
					strBuilder.append("\n");
					return strBuilder.toString();
				}				
			}
		}
		return getHelpMsg();
	}

	private String getModules() {
		StringBuilder strBuilder = new StringBuilder();
		for(Entry<String, Map<String,String>> moduleEntry : moduleParameterMap.entrySet()){			
			String value = moduleEntry.getKey();
			strBuilder.append(value+"\n");
		}
		strBuilder.append("\n");
		return strBuilder.toString() ;
	}

	@Override
	public String getHotkeyHelp() {
		StringBuilder strBuilder = new StringBuilder(CONFIG_HELP_START);
		int iModule =1 , modulelength = moduleParameterMap.size();
		for(Entry<String, Map<String,String>> moduleEntry : moduleParameterMap.entrySet()){
			
			Map<String,String> keyValueMap = moduleEntry.getValue();

			strBuilder.append("'");
			strBuilder.append(moduleEntry.getKey());
			strBuilder.append("':{");
			if(keyValueMap != null){
				int i =1 , length = keyValueMap.size();
				for(Entry<String, String> entry : keyValueMap.entrySet()){			
					strBuilder.append("'");
					strBuilder.append(entry.getKey());
					strBuilder.append("':{}");			
					if(i < length)
						strBuilder.append(",");
					i = i + 1;
				}
			}
			strBuilder.append("}");
			if(iModule < modulelength)
				strBuilder.append(",");
			iModule = iModule +  1;
		}
		strBuilder.append(CONFIG_HELP_END);
		return strBuilder.toString() ;
	}

	@Override
	public String getCommandName() {
		return "config";
	}

	@Override
	public String getDescription() {
		return "Display config information";
	}

    @Override
    public String getHelpMsg(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("Usage : " + getCommandName() + " [<options>]" );
		out.println("Description : " + getDescription() );
		out.println("Possible options:\n");
		out.println(fillChar("   MODULE",45) + " Shows list of all the Modules");
		out.println(fillChar("   <config-module> <parameter>",45) + " Shows details of Parameter");
		out.println(fillChar("   <config-module> <config-sub-module>",45) + " Shows details of Sub-Module");
		out.println(fillChar("   <config-module> MODULES",45) + " Shows list of all Sub-Modules");
		out.println();
		out.close();
		return stringWriter.toString();
    }
}