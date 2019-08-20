package com.elitecore.core.util.cli.cmd;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public abstract class DetailProvider {


	public static final String CSV = "-csv";
	public static final String HELP_OPTION = "-help";
	public static final String HELP = "?";
	public static final String EMPTY_STR="";

    public DetailProvider(){

    }

    public abstract String execute(String [] parameters);
    public abstract String getHelpMsg();
    public abstract String getKey();

	public abstract HashMap<String ,DetailProvider> getDetailProviderMap();

	public void registerDetailProvider(DetailProvider detailprovider) throws RegistrationFailedException{
		if(detailprovider.getKey() == null){
			throw new  RegistrationFailedException("Failed to register detail provider. Reason : key is not specified.");
		}

		if(getDetailProviderMap().containsKey(detailprovider.getKey())){
			throw new  RegistrationFailedException("Failed to register detail provider. Reason : Detail Provider already contains detail provider with Key : " + detailprovider.getKey());
		}

		getDetailProviderMap().put(detailprovider.getKey() , detailprovider);
	}

	public String getHotkeyHelp() {
		StringWriter writer =new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		printWriter.print("'" + getKey() +"':{");
		int size = getDetailProviderMap().size();
		int i =1;
		for(DetailProvider provider : getDetailProviderMap().values()){
			if(i != size){
				printWriter.print(provider.getHotkeyHelp() + ",");
			}else{
				printWriter.print(provider.getHotkeyHelp());
			}
			i++;
		}
		printWriter.print("}");
		return writer.toString();	
	}

	protected String displayList(String header,Set<String> list) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		int count=1;
		out.println();
		out.println(" --- " + header + " --- ");
		out.println();
		if(list.isEmpty() == false){
			Iterator<String> iterator=list.iterator();
			while(iterator.hasNext()){
				out.printf("%4d) " , count++);
				out.println(iterator.next());
			}
		}else{
			out.println("No " + header + " available.");
		}
		return stringWriter.toString();	
	}
	
	/**
	 * checks whether the symbol is help option or not.
	 * 
	 * NOTE: Case insensitive check
	 * @param symbol The String to compare for help option
	 * @return true if symbol is help option, false if symbol is null or not help option
	 */
	protected final boolean isHelpSymbol(String symbol) {
		return HELP.equals(symbol) || HELP_OPTION.equalsIgnoreCase(symbol);
	}
		
	protected  int findMaxLength(int ...values){
		if(values.length == 0){
			return 0;
		}

		int temp = values[0];
		for(int i = 1 ; i < values.length ; i++){
			if(temp < values[i]){
				temp = values[i];
			}
		}
		return temp;
	}

	
	public String getDescription(){
		return EMPTY_STR;
	}

}