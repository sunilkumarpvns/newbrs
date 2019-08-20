package com.elitecore.netvertex.usagemetering.factory;

import com.elitecore.netvertex.usagemetering.ServiceUnit;
import com.elitecore.netvertex.usagemetering.UMLevel;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;

public abstract class UMInfoFactory {
	

	private static PCCRuleLevelUMFactory pccFactory = new PCCRuleLevelUMFactory();
	private static SessionLevelUMFactory sessionLevelFactory = new SessionLevelUMFactory();
	
	public static UMInfoFactory of(UMLevel usageMonitoringLevel){
		if(usageMonitoringLevel == UMLevel.PCC_RULE_LEVEL){
			return pccFactory;
		}else {
			return sessionLevelFactory;
		}
	}
	
	
	
	private static class PCCRuleLevelUMFactory extends UMInfoFactory{

		@Override
		public UsageMonitoringInfo gsuInstace(String monitoringKey,
				ServiceUnit serviceUnit) {
			UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();
			usageMonitoringInfo.setMonitoringKey(monitoringKey);
			usageMonitoringInfo.setUsageMonitoringLevel(UMLevel.PCC_RULE_LEVEL);
			usageMonitoringInfo.setGrantedServiceUnit(serviceUnit);
			return usageMonitoringInfo; 
		}

		@Override
		public UsageMonitoringInfo usuInstace(String monitoringKey,
				ServiceUnit serviceUnit) {
			UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();
			usageMonitoringInfo.setMonitoringKey(monitoringKey);
			usageMonitoringInfo.setUsageMonitoringLevel(UMLevel.PCC_RULE_LEVEL);
			usageMonitoringInfo.setUsedServiceUnit(serviceUnit);
			return usageMonitoringInfo; 
		}
	}
	
	private static class SessionLevelUMFactory extends UMInfoFactory{

		@Override
		public UsageMonitoringInfo gsuInstace(String monitoringKey,
				ServiceUnit serviceUnit) {
			UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();
			usageMonitoringInfo.setMonitoringKey(monitoringKey);
			usageMonitoringInfo.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);
			usageMonitoringInfo.setGrantedServiceUnit(serviceUnit);
			return usageMonitoringInfo; 
		}

		@Override
		public UsageMonitoringInfo usuInstace(String monitoringKey,
				ServiceUnit serviceUnit) {
			UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();
			usageMonitoringInfo.setMonitoringKey(monitoringKey);
			usageMonitoringInfo.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);
			usageMonitoringInfo.setUsedServiceUnit(serviceUnit);
			return usageMonitoringInfo; 
		}
	}
	
	
	public UsageMonitoringInfo gsuInstace(String monitoringKey, long inputOctets, long outputOctets, long totalOctets,long time){
		return gsuInstace(monitoringKey, new ServiceUnit.ServiceUnitBuilder().withInputOctets(inputOctets).withOutputOctets(outputOctets).withTotalOctets(totalOctets).withTime(time).build());
	}
	
	public  UsageMonitoringInfo usuInstace(String monitoringKey, long inputOctets, long outputOctets, long totalOctets, long time){
		return usuInstace(monitoringKey, new ServiceUnit.ServiceUnitBuilder().withInputOctets(inputOctets).withOutputOctets(outputOctets).withTotalOctets(totalOctets).withTime(time).build());
	}
	
	public abstract UsageMonitoringInfo gsuInstace(String monitoringKey, ServiceUnit serviceUnit);
	public abstract UsageMonitoringInfo usuInstace(String monitoringKey, ServiceUnit serviceUnit);
	
	
	
	
	
	@Deprecated
	public static UsageMonitoringInfo newSessionLevelInstace(String monitoringKey, long usedServiceUnit){
		UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();
		usageMonitoringInfo.setMonitoringKey(monitoringKey);
		usageMonitoringInfo.setUsageMonitoringLevel(UMLevel.SESSION_LEVEL);
		usageMonitoringInfo.setUsedServiceUnit(buildServiceUnit(0, 0, usedServiceUnit));
		return usageMonitoringInfo; 
	}
	
	@Deprecated
	public static UsageMonitoringInfo newPCCLevellInstace(String monitoringKey, long usedServiceUnit){
		UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();
		usageMonitoringInfo.setMonitoringKey(monitoringKey);
		usageMonitoringInfo.setUsageMonitoringLevel(UMLevel.PCC_RULE_LEVEL);
		usageMonitoringInfo.setUsedServiceUnit(buildServiceUnit(0, 0, usedServiceUnit));
		return usageMonitoringInfo; 
	}
	
	/*public static UsageMonitoringInfo ofSessionLevelGSUInstace(String monitoringKey, ServiceUnit serviceUnit){
		UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();
		usageMonitoringInfo.setMonitoringKey(monitoringKey);
		usageMonitoringInfo.setUsageMonitoringLevel(UsageMonitoringLevel.SESSION_LEVEL);
		usageMonitoringInfo.setGrantedServiceUnit(serviceUnit);
		return usageMonitoringInfo; 
	}
	
	public static UsageMonitoringInfo ofSessionLevelUSUInstace(String monitoringKey, ServiceUnit serviceUnit){
		UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();
		usageMonitoringInfo.setMonitoringKey(monitoringKey);
		usageMonitoringInfo.setUsageMonitoringLevel(UsageMonitoringLevel.SESSION_LEVEL);
		usageMonitoringInfo.setUsedServiceUnit(serviceUnit);
		return usageMonitoringInfo; 
	}
	
	public static UsageMonitoringInfo ofPCCLevelGSUInstace(String monitoringKey, ServiceUnit serviceUnit){
		UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();
		usageMonitoringInfo.setMonitoringKey(monitoringKey);
		usageMonitoringInfo.setUsageMonitoringLevel(UsageMonitoringLevel.PCC_RULE_LEVEL);
		usageMonitoringInfo.setGrantedServiceUnit(serviceUnit);
		return usageMonitoringInfo; 
	}
	
	public static UsageMonitoringInfo ofPCCLevelUSUInstace(String monitoringKey, ServiceUnit serviceUnit){
		UsageMonitoringInfo usageMonitoringInfo = new UsageMonitoringInfo();
		usageMonitoringInfo.setMonitoringKey(monitoringKey);
		usageMonitoringInfo.setUsageMonitoringLevel(UsageMonitoringLevel.PCC_RULE_LEVEL);
		usageMonitoringInfo.setUsedServiceUnit(serviceUnit);
		return usageMonitoringInfo; 
	}
*/
	
	
	
	
	
	private static ServiceUnit buildServiceUnit(long inputOctets, long outputOctets, long totalOctets){
		
		if(inputOctets <= 0 && outputOctets <= 0 && totalOctets <= 0){
			return null;
		}
		
		ServiceUnit serviceUnit = new ServiceUnit();
		
		if(inputOctets > 0){
			serviceUnit.setInputOctets(inputOctets);
		}
		
		if(outputOctets > 0){
			serviceUnit.setOutputOctets(outputOctets);
		}
		
		if(totalOctets > 0){
			serviceUnit.setTotalOctets(totalOctets);
		}
		
		return serviceUnit;
		
	}

}
