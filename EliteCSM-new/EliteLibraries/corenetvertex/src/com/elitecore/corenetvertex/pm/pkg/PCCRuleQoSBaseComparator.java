package com.elitecore.corenetvertex.pm.pkg;

import java.util.Comparator;

import com.elitecore.commons.logging.LogManager;

public class PCCRuleQoSBaseComparator implements Comparator<PCCRule> {
	private static final String MODULE = "PCC-QoS-COMPARATOR";

	@Override
	public int compare(PCCRule pccRule, PCCRule selectedPCCRule) {
		
		if(pccRule == selectedPCCRule){
			if(LogManager.getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, pccRule.getName() + " and " + selectedPCCRule.getName() + " are same. Reason:policy"
						+ " " + pccRule.getName() + " and " + selectedPCCRule.getName() + " refers to same object");
			return 0;
		}
		
		if(selectedPCCRule == null){
			if(LogManager.getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, pccRule.getName() +" is greater. Reason: Comparison argument is null");
			return 1;
		}
		
		/**
		 * do not directly use below parameter, 
		 * Instead use method as method is overriden in case of multiplier.
		 */
		long gbrdl = pccRule.getGBRDL();
		long gbrul = pccRule.getGBRUL();
		long mbrdl = pccRule.getMBRDL();
		long mbrul = pccRule.getMBRUL();
		
		if(gbrdl == selectedPCCRule.getGBRDL() && 
			gbrul == selectedPCCRule.getGBRUL() && 
			mbrdl == selectedPCCRule.getMBRDL() && 
			mbrul == selectedPCCRule.getMBRUL()){
			if(LogManager.getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, pccRule.getName() +" and " + selectedPCCRule.getName() + " are same. Reason: All comparison parameters(QCI, GBRDL, GBRUL, MBRDL, MBRUL) of "+ pccRule.getName() +" and "+ selectedPCCRule.getName() +" are same");
			return 0;
		}
		
		
		
		

		if(pccRule.getQCI().isGBRQCI()){
			if(LogManager.getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, "Comparing GBRDL...");
			if(gbrdl > selectedPCCRule.getGBRDL()){
				if(LogManager.getLogger().isDebugLogLevel())
					LogManager.getLogger().debug(MODULE, pccRule.getName() +" is greater. Reason: GBRDL of "+ pccRule.getName() + " higher " + " than GBRDL of "+ selectedPCCRule.getName());
				return 1;
			} else if(gbrdl < selectedPCCRule.getGBRDL()) {
				if(LogManager.getLogger().isDebugLogLevel())
					LogManager.getLogger().debug(MODULE, selectedPCCRule.getName() +" is greater. Reason: GBRDL of "+ pccRule.getName() + " lower " + " than GBRDL of "+ selectedPCCRule.getName());
				return -1;
			}
			
			
			if(LogManager.getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, "Comparing GBRUL...");
			if(gbrul > selectedPCCRule.getGBRUL()){
				if(LogManager.getLogger().isDebugLogLevel())
					LogManager.getLogger().debug(MODULE, pccRule.getName() +" is greater. Reason: GBRUL of "+ pccRule.getName() + " higher " + " than GBRUL of "+ selectedPCCRule.getName());
				return 1;
			} else if(gbrul < selectedPCCRule.getGBRUL()) {
				if(LogManager.getLogger().isDebugLogLevel())
					LogManager.getLogger().debug(MODULE, selectedPCCRule.getName() +" is greater. Reason: GBRUL of "+ pccRule.getName() + " lower " + " than GBRUL of "+ selectedPCCRule.getName());
				return -1;
			}
		} else {
			if(LogManager.getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, "Skipped Comparing of GBRDL and GBRUL. Reason: QCI(" + pccRule.getQCI() + ") is greater than 5");
		}
			
		
		
		if(LogManager.getLogger().isDebugLogLevel())
			LogManager.getLogger().debug(MODULE, "Comparing MBRDL...");
		if(mbrdl > selectedPCCRule.getMBRDL()){
			if(LogManager.getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, pccRule.getName() +" is greater. Reason: MBRDL of "+ pccRule.getName() + " higher " + " than MBRDL of "+ selectedPCCRule.getName());
			return 1;
		} else if(mbrdl < selectedPCCRule.getMBRDL()) {
			if(LogManager.getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, selectedPCCRule.getName() +" is greater. Reason: MBRDL of "+ pccRule.getName() + " lower " + " than MBRDL of "+ selectedPCCRule.getName());
			return -1; 
		}
		
		
		if(LogManager.getLogger().isDebugLogLevel())
			LogManager.getLogger().debug(MODULE, "Comparing MBRUL...");
		if(mbrul > selectedPCCRule.getMBRUL()){
			if(LogManager.getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, pccRule.getName() +" is greater. Reason:MBRUL of "+ pccRule.getName() + " higher " + " than MBRUL of "+ selectedPCCRule.getName());
			return 1;
		} else if(mbrul < selectedPCCRule.getMBRUL()) {
			if(LogManager.getLogger().isDebugLogLevel())
				LogManager.getLogger().debug(MODULE, selectedPCCRule.getName() +" is greater. Reason: MBRUL of "+ pccRule.getName() + " lower " + " than MBRUL of "+ selectedPCCRule.getName());
			return -1; 
		}
		 
		return 0;
	}

}
