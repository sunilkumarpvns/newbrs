package com.elitecore.elitesm.web.radius.utilities;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.util.chap.CHAPUtil;
import com.elitecore.coreradius.util.digest.DigestUtil;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;

import com.elitecore.elitesm.web.radius.utilities.forms.VerifyPasswordForm;

public class VerifyPasswordAction extends BaseWebAction{
	   private static final String SUCCESS_FORWARD = "success";               
       private static final String FAILURE_FORWARD = "failure";               
       private static final String MODULE ="ViewRadiusTestAction";
       private static final String VIEW_FORWARD = "initVerifyPassword";
       private static final String SUB_MODULE_ACTIONALIAS=ConfigConstant.VERIFY_PASSWORD_ACTION;
       
       private static final String PAP = "PAP";
       private static final String CHAP = "CHAP";
       private static final String DIGEST = "Digest";
       
       public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
    	   Logger.logInfo(MODULE, "Entered execute method of " + getClass().getName());
           VerifyPasswordForm verifyPasswordForm = (VerifyPasswordForm)form;
           ActionMessages messages = new ActionMessages();
           try {
        	   checkActionPermission(request, SUB_MODULE_ACTIONALIAS);
        	   String encryptType = verifyPasswordForm.getEncryptType();
        	   String chapChallenge = verifyPasswordForm.getChapChallenge();
        	   String reqAuth = verifyPasswordForm.getReqAuthenticator();
        	   String shareSecret = verifyPasswordForm.getSharedSecret();
        	   String userPassword = verifyPasswordForm.getUserPassword();
        	   String chapPassword = verifyPasswordForm.getChapPassword();
        	   IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
   			
        	   /* Digest parameters */

        	   Logger.logInfo(MODULE, " ::: Encrypt Type  ::: "+encryptType);
        	   Logger.logInfo(MODULE, " ::: CHAP Challenge  ::: "+chapChallenge);
        	   Logger.logInfo(MODULE, " ::: Request Authenticator  ::: "+reqAuth);
        	   Logger.logInfo(MODULE, " ::: Shared Secret  ::: "+shareSecret);
        	   Logger.logInfo(MODULE, " ::: userPassword  ::: "+userPassword);
        	   Logger.logInfo(MODULE, " ::: chapPassword  ::: "+chapPassword);

        	   if(encryptType.equals(PAP)) {

        		   byte[] userPasswordBytes = HexToBytes(userPassword);
        		   byte[] reqAuthBytes = HexToBytes(reqAuth);
        		   String decryptedPassword = RadiusUtility.decryptPasswordRFC2865(userPasswordBytes, reqAuthBytes, shareSecret);
        		   verifyPasswordForm.setMsgPassword(decryptedPassword);
        		   Logger.logInfo(MODULE, " ::: decryptedPassword  ::: "+decryptedPassword);
        		   request.setAttribute("verifyPasswordForm", verifyPasswordForm);
        	   }
        	   else if(encryptType.equals(CHAP)) {

        		   byte[] chapChallengeBytes= null;

        		   if(reqAuth!=null && !reqAuth.equals("")){
        			   reqAuth = reqAuth.trim();
        			   chapChallengeBytes = HexToBytes(reqAuth);
        		   }

        		   if(chapChallenge != null && !chapChallenge.equals("")){
        			   chapChallenge = chapChallenge.trim();
        			   chapChallengeBytes = HexToBytes(chapChallenge);
        		   }

        		   byte[] receivedCHAPPasswordBytes = HexToBytes(chapPassword);
        		   byte chapID = receivedCHAPPasswordBytes[0];
        		   byte[] calculatedChapPassword = CHAPUtil.generateCHAPPassword(chapID, userPassword, chapChallengeBytes);
        		   String calculatedChapPasswordHex = RadiusUtility.bytesToHex(calculatedChapPassword);

        		   byte[] receivedCHAPPassword = new byte[receivedCHAPPasswordBytes.length-1];
        		   System.arraycopy(receivedCHAPPasswordBytes, 1, receivedCHAPPassword, 0, receivedCHAPPassword.length);
        		   if(Arrays.equals(receivedCHAPPassword,calculatedChapPassword ))
        		   {
        			   verifyPasswordForm.setMsgPassword(calculatedChapPasswordHex + "        -  <i> Matched.</i>");
        		   }
        		   else
        		   {
        			   verifyPasswordForm.setMsgPassword(calculatedChapPasswordHex + "         -  <i> Not Matched. </i>");
        		   }
        		   Logger.logInfo(MODULE, " ::: calculated Chap Password in Hex  ::: "+calculatedChapPasswordHex);
        		   request.setAttribute("verifyPasswordForm", verifyPasswordForm);

        	   } else if(encryptType.equals(DIGEST)) {

        		   String digestRealm = verifyPasswordForm.getDigestRealm();
        		   String digestNonce = verifyPasswordForm.getDigestNonce();
        		   String digestMethod = verifyPasswordForm.getDigestMethod();
        		   String digestUri = verifyPasswordForm.getDigestUri();
        		   String digestQoP = verifyPasswordForm.getDigestQoP();
        		   String digestAlgorithm = verifyPasswordForm.getDigestAlgorithm();
        		   String digestBody = verifyPasswordForm.getDigestBody();
        		   String digestCNonce = verifyPasswordForm.getDigestCNonce();
        		   String digestNonceCount = verifyPasswordForm.getDigestNonceCount();
        		   String digestUserName = verifyPasswordForm.getDigestUserName();

        		   Logger.logInfo(MODULE, " ::: Realm  ::: "+digestRealm);
        		   Logger.logInfo(MODULE, " ::: Nonce  ::: "+digestNonce);
        		   Logger.logInfo(MODULE, " ::: Method  ::: "+digestMethod);
        		   Logger.logInfo(MODULE, " ::: Uri  ::: "+digestUri);
        		   Logger.logInfo(MODULE, " ::: QoP  ::: "+digestQoP);
        		   Logger.logInfo(MODULE, " ::: Algorithm  ::: "+digestAlgorithm);
        		   Logger.logInfo(MODULE, " ::: Body Digest  ::: "+digestBody);
        		   Logger.logInfo(MODULE, " ::: CNonce  ::: "+digestCNonce);
        		   Logger.logInfo(MODULE, " ::: Nonce count  ::: "+digestNonceCount);
        		   Logger.logInfo(MODULE, " ::: User Name  ::: "+digestUserName);

        		   byte[] HA1 = DigestUtil.getHA1(digestUserName, digestRealm, userPassword, digestAlgorithm, digestNonce, digestCNonce);
        		   String HA1Hex = DigestUtil.bytesToHex(HA1);
        		   byte[] HA2 = DigestUtil.getDigest(HA1Hex, digestQoP, digestMethod, digestUri, digestNonceCount, digestNonce, digestCNonce, digestBody);
        		   String HA2Hex = DigestUtil.bytesToHex(HA2);

        		   verifyPasswordForm.setMsgPassword(HA2Hex);
        		   Logger.logInfo(MODULE, " ::: calculated Digest ::: " + HA2Hex);
        		   request.setAttribute("verifyPasswordForm",verifyPasswordForm);


        	   } else {
        		   Logger.logError(MODULE, "Invalid Option for the Encrypt Type, reason : " + encryptType);
        		   ActionMessage message = new ActionMessage("utilities.verifypwd.invalidopt");;
        		   messages.add("information", message);   
        		   saveErrors(request, messages);
        		   return mapping.findForward(FAILURE_FORWARD);
        	   }
        	   doAuditing(staffData, SUB_MODULE_ACTIONALIAS);
        	   return mapping.findForward(VIEW_FORWARD);
           } 
           catch(ActionNotPermitedException e){
               Logger.logError(MODULE,"Error :-" + e.getMessage());
               printPermitedActionAlias(request);
               messages.add("information", new ActionMessage("general.user.restricted"));
               saveErrors(request, messages);
               return mapping.findForward(INVALID_ACCESS_FORWARD);
           }
           catch(StringIndexOutOfBoundsException ex)
           {
        	   Logger.logError(MODULE, "Error during password verification operation , reason : " + ex.getMessage());
        	   Logger.logTrace(MODULE, ex);
        	   Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(ex);
        	   request.setAttribute("errorDetails", errorElements);
        	   ActionMessage messageFail = new ActionMessage("utilities.verifypwd.fail");
        	   ActionMessage messageParaFail = new ActionMessage("utilities.verifypwd.parafail");
        	   messages.add("information", messageFail);   
        	   messages.add("information", messageParaFail);
        	   saveErrors(request, messages);
        	   return mapping.findForward(FAILURE_FORWARD);
           }catch(Exception e) {
        	   Logger.logError(MODULE, "Error during password verification operation , reason : " + e.getMessage());
        	   Logger.logTrace(MODULE, e);
        	   Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
        	   request.setAttribute("errorDetails", errorElements);
        	   ActionMessage message = new ActionMessage("utilities.verifypwd.fail");
        	   messages.add("information", message);   
        	   saveErrors(request, messages);
        	   return mapping.findForward(FAILURE_FORWARD);
           }
       }
       protected static final byte[] HexToBytes(String data) {
   		if (data.charAt(1) == 'x')
   			data = data.substring(2);
   		int len = data.length();
   		if (len % 2 != 0)
   			len++;
   		byte[] returnBytes = new byte[len / 2];
   		for (int i = 0; i < len - 1;) {
   			returnBytes[i / 2] = (byte) (HexToByte(data.substring(i, i + 2)) & 0xFF);

   			i += 2;
   		}
   		return returnBytes;
   	}

   	protected static final int HexToByte(String data) {
   		int byteVal = toByte(data.charAt(0)) & 0xFF;
   		byteVal = byteVal << 4;
   		byteVal = byteVal | toByte(data.charAt(1));
   		return byteVal;
   	}

   	protected static final int toByte(char ch) {
   		if ((ch >= '0') && (ch <= '9')) {
   			return ch - 48;
   		} else if ((ch >= 'A') && (ch <= 'F')) {
   			return ch - 65 + 10;
   		} else if ((ch >= 'a') && (ch <= 'f')) {
   			return ch - 97 + 10;
   		} else {
   			return 0;
   		}
   	}     
}
