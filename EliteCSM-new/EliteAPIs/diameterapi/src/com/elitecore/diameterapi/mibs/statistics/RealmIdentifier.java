package com.elitecore.diameterapi.mibs.statistics;

import com.elitecore.diameterapi.diameter.common.util.constant.RoutingActions;
import com.elitecore.diameterapi.mibs.constants.ServiceTypes;

public class RealmIdentifier {
	
	private String dbpRealmMessageRouteRealm;
	private long  dbpRealmMessageRouteApp;
	private ServiceTypes dbpRealmMessageRouteType;
	private RoutingActions dbpRealmMessageRouteAction;
	private int hash = -1;
	
	public RealmIdentifier(String realmName, long realmApp, 
			ServiceTypes appType, RoutingActions routeAction){
		
		this.dbpRealmMessageRouteRealm = realmName;
		this.dbpRealmMessageRouteApp = realmApp;
		this.dbpRealmMessageRouteType = appType;
		this.dbpRealmMessageRouteAction = routeAction;
	}

	public String getDbpRealmMessageRouteRealm() {
		return dbpRealmMessageRouteRealm;
	}

	public long getDbpRealmMessageRouteApp() {
		return dbpRealmMessageRouteApp;
	}

	public ServiceTypes getDbpRealmMessageRouteType() {
		return dbpRealmMessageRouteType;
	}

	public RoutingActions getDbpRealmMessageRouteAction() {
		return dbpRealmMessageRouteAction;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null)
			return false;
		
		if(obj == this)
			return true;
		try{
			RealmIdentifier realmIdentifier = (RealmIdentifier) obj;
			
			if(this.dbpRealmMessageRouteAction == realmIdentifier.dbpRealmMessageRouteAction &&
					this.dbpRealmMessageRouteApp == realmIdentifier.dbpRealmMessageRouteApp &&
					this.dbpRealmMessageRouteType == realmIdentifier.dbpRealmMessageRouteType &&
					this.dbpRealmMessageRouteRealm.equalsIgnoreCase(realmIdentifier.dbpRealmMessageRouteRealm))
					return true;
		}catch(ClassCastException e){}
		return false;
	}
	
	@Override
	public int hashCode() {

		if(hash == -1){
			int hash = this.dbpRealmMessageRouteRealm.hashCode();
			hash = 31*hash + this.dbpRealmMessageRouteAction.hashCode();
			hash = 31*hash + (int)this.dbpRealmMessageRouteApp;
			hash = 31*hash + this.dbpRealmMessageRouteType.hashCode();
			if(hash < 0){
				return hash + Integer.MAX_VALUE;
			}
		}
		return hash;
	}
	
	/***
	 * Method will return the name,
	 * used for realm registration
	 * in jmx.
	 * name will be composed using four unique property.
	 * name = routeRealm(RouteAction)AppID(ServiceType)
	 * @return realname
	 */
	public String getRealmName() {
		return dbpRealmMessageRouteRealm + "(" + dbpRealmMessageRouteAction.routingActionStr + ") " + dbpRealmMessageRouteApp  +" (" + dbpRealmMessageRouteType.serviceTypeStr + ")";  
	}
}
