package com.elitecore.aaa.diameter.service.application.handlers;

import static com.elitecore.aaa.util.constants.AAAServerConstants.ADVANCED;
import static com.elitecore.aaa.util.constants.AAAServerConstants.GROUP;
import static com.elitecore.aaa.util.constants.AAAServerConstants.NONE;
import static com.elitecore.aaa.util.constants.AAAServerConstants.PROFILE_CUI;

import java.util.List;

import javax.annotation.Nonnull;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.ChargeableUserIdentityConfiguration;
import com.elitecore.aaa.radius.util.exprlib.DefaultDiameterValueProvider;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;

public class CUIAdditionHandler<T extends ApplicationRequest, V extends ApplicationResponse>
implements DiameterApplicationHandler<T, V> {
	
	private static final String MODULE = "CUI-ADDITION-HNDLR";
	
	private final ChargeableUserIdentityConfiguration data;

	private Expression cuiExpression;

	public CUIAdditionHandler(@Nonnull ChargeableUserIdentityConfiguration data) {
		this.data = data;
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		// no-op
	}

	@Override
	public void init() throws InitializationFailedException {
		initCUIExpression();
	}
	
	private void initCUIExpression() {
		if (ADVANCED.equalsIgnoreCase(data.getCui())) {
			try {
				this.cuiExpression = Compiler.getDefaultCompiler().parseExpression(
						data.getExpression());
			} catch (InvalidExpressionException ex) {
				LogManager.getLogger().error(MODULE, "Error in compiling advanced CUI expression: " 
						+ data.getExpression() + ". Reason: " + ex.getMessage()
						+ ". Will use Authenticated-UserName as default value.");
			}
		}
	}

	@Override
	public boolean isEligible(T request, V response) {
		return true;
	}

	@Override
	public void handleRequest(T request, V response, ISession session) {
		IDiameterAVP cuiRequestAVP = request.getAVP(DiameterAVPConstants.CUI);
		String cui = (String) request.getParameter(AAAServerConstants.CUI_KEY);
		AccountData accountData = request.getAccountData();
		String strCUIConfiguration = data.getCui();
		
		if (NONE.equalsIgnoreCase(strCUIConfiguration)
				&& cuiRequestAVP != null) {
			if (accountData == null) {
				LogManager.getLogger().warn(MODULE, "Customer profile not available for "+ cui+ ", considering Authenticated-UserName as default");
			} else if (accountData.getCUI() != null) {
				cui = accountData.getCUI();
			} else if (accountData.getGroupName() != null) {
				cui = accountData.getGroupName();
			}
			
			IDiameterAVP cuiRespAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CUI);
			cuiRespAVP.setStringValue(cui);
			response.addAVP(cuiRespAVP);
			
			//FIXME NARENDRA - this else block should be inlined with inner conditions. Remove this after unit testing
		} else if (NONE.equalsIgnoreCase(strCUIConfiguration) == false) { 
			if (accountData == null) {
				LogManager.getLogger().warn(MODULE, "Customer profile not available for "+ cui+ ", considering Authenticated-UserName as default");
			} else if (PROFILE_CUI.equalsIgnoreCase(strCUIConfiguration)) {
				if (accountData.getCUI() == null) {
					LogManager.getLogger().warn(MODULE,"Profile-CUI not available for "+ cui+ ",considering Authenticated-UserName as default");
				} else {
					cui = accountData.getCUI();
				}
			} else if (GROUP.equalsIgnoreCase(strCUIConfiguration)) {
				if (accountData.getGroupName() == null) {
					LogManager.getLogger().warn(MODULE,"Group-Name not available for "+ cui+ ",considering Authenticated-UserName as default");
				} else {
					cui = accountData.getGroupName();
				}
			} else if (ADVANCED.equalsIgnoreCase(strCUIConfiguration)) {
				cui = evaluateCUIFromExpression(request, response, cui);
			}
			
			addCUIAttributesInResponse(response, cui);
		}
	}
	
	private void addCUIAttributesInResponse(V response, String cuiValue) {
		List<String> cuiRespAttrList =  data.getAuthenticationCuiAttributes();
		
		if (cuiRespAttrList.size() > 0) {
			for (String cuiRespAttr : cuiRespAttrList) {
				IDiameterAVP cuiRespAttribute = DiameterDictionary.getInstance().getKnownAttribute(cuiRespAttr);
				if (cuiRespAttribute != null) {
					cuiRespAttribute.setStringValue(cuiValue);
					response.addAVP(cuiRespAttribute);
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Added CUI AVP: " + cuiRespAttr + " with value: " + cuiValue + " in response.");
					}
				} else {
					if (LogManager.getLogger().isInfoLogLevel()) {
						LogManager.getLogger().info(MODULE, "Configured CUI AVP: " + cuiRespAttr + " is not found in dictionary. So will not be added in response.");
					}
				}
			}
		} else {
			IDiameterAVP cuiRespAVP = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.CUI);
			cuiRespAVP.setStringValue(cuiValue);
			response.addAVP(cuiRespAVP);
			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "No CUI response attribute configured, adding Standard-CUI (0:89) AVP with value: " + cuiValue + " in response.");
			}
		}
	}

	private String evaluateCUIFromExpression(T request, V response,
			String userIdentity) {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Evaluating advanced CUI expression for Identity: " 
					+ userIdentity);
		}
		
		if (cuiExpression == null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Advanced CUI expression did not initialize " 
						+ "so considering Authenticated-UserName as default for Identity: " 
						+ userIdentity);
			}
			return userIdentity;
		}
		
		String cui = userIdentity;
		try {
			cui = cuiExpression.getStringValue(new DefaultDiameterValueProvider(request.getDiameterRequest(), 
					response.getDiameterAnswer()));
			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Advanced CUI expression: " 
						+ data.getExpression() + " evaluated to: "
						+ cui + " for Identity: " + userIdentity);
			}
		} catch (InvalidTypeCastException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Error in evaluating CUI expression " 
						+ data.getExpression() + " for Identity: " 
						+ userIdentity + ". Reason: " + e.getMessage() + 
						". Considering Authenticated-UserName as default");
			}
		} catch (IllegalArgumentException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Error in evaluating CUI expression " 
						+ data.getExpression() + " for Identity: " 
						+ userIdentity + ". Reason: " + e.getMessage() + 
						". Considering Authenticated-UserName as default");
			}
		} catch (MissingIdentifierException e) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Error in evaluating CUI expression " 
						+ data.getExpression() + " for Identity: " 
						+ userIdentity + ". Reason: " + e.getMessage() + 
						". Considering Authenticated-UserName as default");
			}
		}
		
		return cui;
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}
}
