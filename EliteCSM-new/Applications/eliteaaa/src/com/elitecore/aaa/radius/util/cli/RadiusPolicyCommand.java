package com.elitecore.aaa.radius.util.cli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.elitecore.aaa.radius.policies.radiuspolicy.RadiusPolicyManager;
import com.elitecore.core.commons.utilx.cli.cmd.EliteBaseCommand;
import com.elitecore.core.serverx.policies.data.IPolicyData;

public class RadiusPolicyCommand extends EliteBaseCommand {

	@Override
	public String execute(String parameter) {
		if(parameter != null && parameter.length() > 0 ){
			StringTokenizer tokenizer = new StringTokenizer(parameter, " ");			
			parameter= tokenizer.nextToken();
			
			if("?".equals(parameter.trim()))
				return getHelp().toString();
			
			if(tokenizer.countTokens()==0){
				return displayPolicyListFor(parameter); // policyManager				
			}else if(tokenizer.countTokens() == 1){
				return displayPolicy(RadiusPolicyManager.getRadiusPolicy(parameter, tokenizer.nextToken()));
			}else{
				return getErrorMsg();
			}			
		}else{
			return displayListOfPolicyManagerinstances();						
		}			
	}

	@Override
	public String getCommandName() {		
		return "radiuspolicy";
	}

	@Override
	public String getDescription() {
		return "Displays Radius Policy detail as per option.";
	}

	private String getHelp(){
		return "Usage : radiuspolicy [policy manager instance name] [policy name]";
	}
	private String getErrorMsg(){
		return "Invalid Parameter or Parameter format is not correct. \nTry 'radiuspolicy ?'.";
	}
	private String displayListOfPolicyManagerinstances(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);		
		List<String> strManagerInstancies = RadiusPolicyManager.getPolicyManagerInstances();
		if(strManagerInstancies != null && strManagerInstancies.size() > 0){
			int size = strManagerInstancies.size()+1;
			for(int srNo=0;srNo< size;srNo++){
				out.println();
				out.println("List Of Policy Manager Instances");
				out.println("--------------------------------");
				out.println(srNo + ") " + strManagerInstancies.get(srNo-1));
				out.println();
			}
		}else{
			return "Policy manager instances not found.";
		}		
		return stringBuffer.toString();
	}
	private String displayPolicyListFor(String policyManagerName){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		List<String> policyList = RadiusPolicyManager.getRadiusPolicies(policyManagerName);
		if(policyList!=null && !policyList.isEmpty()){
			Iterator <String> policyListIterator= policyList.iterator();
			int iSrNo=1;
			
			out.println("\nList Of Policies in Cache for " + policyManagerName);
			out.println("--------------------------------");
			while(policyListIterator.hasNext()){
				out.println(iSrNo++ + ") " + policyListIterator.next());
			}
			return stringBuffer.toString();
		}else{
			return "No Policy available in cache for " + policyManagerName + "!";
		}		
	}
	private String displayPolicy(IPolicyData policyData){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		if(policyData!=null){
			
			out.println("********************************************************************************************************");
			out.println("\t\t\t\t\tRADIUS POLICY DETAILS");
			out.println("********************************************************************************************************");
			out.println(policyData);
			out.println("********************************************************************************************************");
			return stringBuffer.toString();
		}else{
			return "No such Radius Policy exist!\nKindly verify the name and Retry.";
		}
		
	}
	
	@Override
	public String getHotkeyHelp() {
		return "{'radiuspolicy':{}}";
	}
}
