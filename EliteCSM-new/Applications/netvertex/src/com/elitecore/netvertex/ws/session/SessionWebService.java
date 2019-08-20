package com.elitecore.netvertex.ws.session;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.corenetvertex.constants.JMXConstant;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.session.SessionInformation;
import com.elitecore.corenetvertex.session.SessionRuleData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.netvertex.core.session.SessionOperation;
import com.elitecore.netvertex.ws.ReAuthorizationController;
import com.elitecore.netvertex.ws.SessionDisconnectController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Created by aditya on 9/9/16.
 */

@Path("/session")
public class SessionWebService {

	private static final String MODULE = "SESSION-WS";
	private SessionOperation sessionOperation;
	private com.elitecore.netvertex.ws.ReAuthorizationController reAuthorizationController;
	private SessionDisconnectController sessionDisconnectController;
	private Gson gson;

	public SessionWebService(SessionOperation sessionOperation,
							 ReAuthorizationController reAuthorizationController,
							 SessionDisconnectController sessionDisconnectController) {
		this.sessionOperation = sessionOperation;
		this.reAuthorizationController = reAuthorizationController;
		this.sessionDisconnectController = sessionDisconnectController;
		this.gson = GsonFactory.defaultInstance();
	}

	@GET
	@Path("/subscriberIdentity/{subscriberIdentity}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getSessionBySubscriberIdentity(@PathParam("subscriberIdentity") String subscriberIdentity) {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Calling getSessionBySubscriberIdentity for subscriberIdentity: " + subscriberIdentity);
		}

		return toJson(convertSessionDataToSessionInformations(sessionOperation.getSessionLocator().getCoreSessionByUserIdentity(subscriberIdentity)));
	}

	private Iterator<SessionInformation> convertSessionDataToSessionInformations(Iterator<SessionData> coreSessionByUserIdentity) {
		Collection<SessionInformation> sessionInformations = new ArrayList<SessionInformation>();

		if(coreSessionByUserIdentity != null) {
			while (coreSessionByUserIdentity.hasNext()) {
				sessionInformations.add(convertSessionDataToSessionInformation(coreSessionByUserIdentity.next()));
			}
		}
		return sessionInformations.iterator();
	}

	private SessionInformation convertSessionDataToSessionInformation(SessionData sessionData) {
		if(sessionData == null) {
			return null;
		}
		SessionInformation sessionInformation = new SessionInformation(sessionData.getSchemaName(),sessionData.getSessionId(), sessionData.getCreationTime(),sessionData.getLastUpdateTime());
		sessionInformation.setSessionInfo(fromSessionDataToSessionInfoMap(sessionData));
		sessionInformation.setSessionRuleDatas(fetchSessionRules(sessionData));
		return sessionInformation;

	}

	private Map<String, String> fromSessionDataToSessionInfoMap(SessionData sessionData) {
		Map<String, String> sessionInfoMap = new HashMap<String, String>();
		for (String key : sessionData.getKeySet()) {
			String value = sessionData.getValue(key);
			sessionInfoMap.put(key, value);
		}
		return sessionInfoMap;
	}

	private @Nullable List<SessionRuleData> fetchSessionRules(SessionData sessionData) {

		Criteria sessionRuleCriteria = null;
		String sessionId = sessionData.getValue(PCRFKeyConstants.CS_SESSION_ID.getVal());

		if(Strings.isNullOrBlank(sessionId) == false) {
			try{
				sessionRuleCriteria = sessionOperation.getSessionLocator().getSessionRuleCriteria();
			} catch (SessionException e) {
				LogManager.getLogger().error(MODULE, "Unable to locate session rule. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
				return null;
			}
		}

		if(sessionRuleCriteria == null){
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Session rule criteria not found");
			}
			return null;
		}
		if(SessionTypeConstant.GX.getVal().equals(sessionData.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))) {
			sessionRuleCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), sessionId));
		} else if(SessionTypeConstant.RX.getVal().equals(sessionData.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()))) {
			sessionRuleCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_AF_SESSION_ID.getVal(), sessionId));
		} else {
			return null;
		}
		List<SessionData> sessionRules = sessionOperation.getSessionLocator().getSessionRules(sessionRuleCriteria);

		List<SessionRuleData> sessionRuleDatas = Collectionz.newArrayList();

		if(Collectionz.isNullOrEmpty(sessionRules) == false) {
			for(SessionData sessionData2 : sessionRules) {
				SessionRuleData sessionRuleData =  new SessionRuleData();
				sessionRuleData.setPccRule(sessionData2.getValue(PCRFKeyConstants.PCC_RULE_LIST.getVal()));
				sessionRuleData.setAfSessionId(sessionData2.getValue(PCRFKeyConstants.CS_AF_SESSION_ID.getVal()));
				sessionRuleData.setDownLinkFlow(sessionData2.getValue(PCRFKeyConstants.CS_DOWNLINK_FLOW.val));
				sessionRuleData.setUpLinkFlow(sessionData2.getValue(PCRFKeyConstants.CS_UPLINK_FLOW.val));
				sessionRuleData.setMediaType(sessionData2.getValue(PCRFKeyConstants.CS_MEDIA_TYPE.val));
				sessionRuleData.setAdditionalParameter(sessionData2.getValue(PCRFKeyConstants.PCC_ADDITIONAL_PARAMETER.val));
				sessionRuleDatas.add(sessionRuleData);
			}
		}
		return sessionRuleDatas;
	}


	@GET
	@Path("/subscriberIdentity/cache/{subscriberIdentity}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getSessionBySubscriberIdentityFromCache(@PathParam("subscriberIdentity") String subscriberIdentity) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Calling getSessionBySubscriberIdentityFromCache for subscriberIdentity: " + subscriberIdentity);
		}
		return toJson(convertSessionDataToSessionInformations(sessionOperation.getSessionLocator().getCoreSessionByUserIdentityFromCache(subscriberIdentity)));
	}

	@GET
	@Path("/IPv4/{ip}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getSessionBySessionIPv4(@PathParam("ip") String ip) {
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Calling getSessionBySessionIPv4 for ip: " + ip);
		}
		
		return toJson(convertSessionDataToSessionInformations(sessionOperation.getSessionLocator().getCoreSessionBySessionIPv4(ip)));
	}
	
	@GET
	@Path("/IPv4/cache/{ip}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getSessionBySessionIPv4FromCache(@PathParam("ip") String ip) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Calling getSessionBySessionIPv4FromCache for ip: " + ip);
		}

		return toJson(convertSessionDataToSessionInformations(sessionOperation.getSessionLocator().getCoreSessionBySessionIPv4FromCache(ip)));
	}

	@GET
	@Path("/IPv6/{ip}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getSessionBySessionIPv6(@PathParam("ip") String ip) {
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Calling getSessionBySessionIPv6 for ip: " + ip);
		}
		
		return toJson(convertSessionDataToSessionInformations(sessionOperation.getSessionLocator().getCoreSessionBySessionIPv6(ip)));
	}

	@GET
	@Path("/IPv6/cache/{ip}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getSessionBySessionIPv6FromCache(@PathParam("ip") String ip) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Calling getSessionBySessionIPv6FromCache for ip: " + ip);
		}
		
		return toJson(convertSessionDataToSessionInformations(sessionOperation.getSessionLocator().getCoreSessionBySessionIPv6FromCache(ip)));
	}

	@GET
	@Path("/coreSessionId/{coreSessionId}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getSessionByCoreSessionId(@PathParam("coreSessionId") String coreSessionId) {
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Calling getSessionByCoreSessionId for coreSessionId: " + coreSessionId);
		}
		
		return toJson(convertSessionDataToSessionInformation(sessionOperation.getSessionLocator().getCoreSessionByCoreSessionID(coreSessionId)));
	}

	@GET
	@Path("/coreSessionId/cache/{coreSessionId}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getSessionByCoreSessionIdFromCache(@PathParam("coreSessionId") String coreSessionId) {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Calling getSessionByCoreSessionIdFromCache for coreSessionId: " + coreSessionId);
		}

		return toJson(convertSessionDataToSessionInformation(sessionOperation.getSessionLocator().getCoreSessionByCoreSessionIDFromCache(coreSessionId)));
	}


	@GET
	@Path("/remove/coreSessionId/{coreSessionId}")
	@Produces({ MediaType.TEXT_PLAIN })
	public String removeSessionByCoreSessionId(@PathParam("coreSessionId") String coreSessionId) {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "calling removeSessionByCoreSessionId for coreSessionId: " + coreSessionId);
		}

		if (sessionOperation.deleteCoreSessionByCoreSessionId(coreSessionId) > 0) {
			return JMXConstant.SUCCESS;
		}
		return JMXConstant.SESSION_NOT_FOUND;
	}
	
	@GET
	@Path("/remove/cache/coreSessionId/{coreSessionId}")
	@Produces({ MediaType.TEXT_PLAIN })
	public String removeSessionByCoreSessionIdFromCache(@PathParam("coreSessionId") String coreSessionId) {

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "calling removeSessionByCoreSessionIdFromCache for coreSessionId: " + coreSessionId);
		}

		if (sessionOperation.deleteCoreSessionByCoreSessionIdFromCache(coreSessionId)) {
			return JMXConstant.SUCCESS;
		}
		return JMXConstant.SESSION_NOT_FOUND;
	}


	@GET
	@Path("/reAuth/coreSessionId/{coreSessionId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String reauthSessionByCoreSessionId(@PathParam("coreSessionId") String coreSessionId) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "calling reauthSessionByCoreSessionId for coresessionId: " + coreSessionId);
		}

		return reAuthorizationController.reAuthByCoreSessionId(coreSessionId);
	}

	@GET
	@Path("/reAuth/subscriberIdentity/{subscriberIdentity}")
	@Produces(MediaType.TEXT_PLAIN)
	public String reauthSessionBySubscriberIdentity(@PathParam("subscriberIdentity") String subscriberIdentity) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "calling reauthSessionBySubscriberIdentity for subscriberIdentity: " + subscriberIdentity);
		}

		return reAuthorizationController.reAuthBySubscriberIdentity(subscriberIdentity);
	}

	@GET
	@Path("/reAuth/IPv6/{ip}")
	@Produces({MediaType.TEXT_PLAIN})
	public String reauthSessionByIPv6(@PathParam("ip") String ip) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "calling reauthSessionByIPv6 for IPv6 address: " + ip);
		}

		return reAuthorizationController.reAuthBySessionIPv6(ip);
	}

	@GET
	@Path("/reAuth/IPv4/{ip}")
	@Produces({MediaType.TEXT_PLAIN})
	public String reauthSessionByIPv4(@PathParam("ip") String ip) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "calling reauthSessionByIPv4 for IPv4 address: " + ip);
		}

		return reAuthorizationController.reAuthBySessionIPv4(ip);
	}
	
	@GET
	@Path("/disconnect/coreSessionId/{coreSessionId}")
	@Produces({MediaType.TEXT_PLAIN})
	public String disconnectSessionbyCoreSessionId(@PathParam("coreSessionId") String coreSessionId) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "calling disconnectSessionbyCoreSessionId for coreSessionId: " + coreSessionId);
		}

		return sessionDisconnectController.disconnectbyCoreSessionId(coreSessionId);
	}
	
	@GET
	@Path("/disconnect/subscriberIdentity/{subscriberIdentity}")
	@Produces({MediaType.TEXT_PLAIN})
	public String disconnectSessionBySubscriberIdentity(@PathParam("subscriberIdentity") String subscriberIdentity) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "calling disconnectSessionBySubscriberIdentity for subscriberIdentity: " + subscriberIdentity);
		}

		return sessionDisconnectController.disconnectbySubscriberIdentity(subscriberIdentity);
	}

	private String toJson(Iterator<SessionInformation> coreSessionIterator) {

		if (coreSessionIterator == null || coreSessionIterator.hasNext() == false) {
			return null;
		}
		
		Collection<SessionInformation> sessionInformations = new ArrayList<SessionInformation>();
		while (coreSessionIterator.hasNext()) {
			sessionInformations.add(coreSessionIterator.next());
		}
		
		return gson.toJson(sessionInformations, new TypeToken<Collection<SessionInformation>>(){}.getType());
	}

	private String toJson(SessionInformation sessionInformation) {

		if (sessionInformation == null) {
			return null;
		}
		return gson.toJson(sessionInformation);
	}

}
