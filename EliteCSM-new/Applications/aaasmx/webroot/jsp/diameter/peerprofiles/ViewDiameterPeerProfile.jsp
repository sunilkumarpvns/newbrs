<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <bean:define id="diameterPeerProfileDataBean" name="diameterPeerProfileData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterpeerprofile.data.DiameterPeerProfileData" />
    <tr> 
    
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" >
	          <tr> 
	            <td class="tblheader-bold" colspan="2" height="20%">
	              <bean:message bundle="diameterResources" key="diameterpeerprofile.viewpeerprofile"/>
	            </td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.name" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="profileName"/>&nbsp;</td>
	          </tr>
	           <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="general.description" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="description"/>&nbsp;</td>
	          </tr>
	           <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.exclusiveauthappid" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="exclusiveAuthAppIds"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.exclusiveacctappid" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="exclusiveAcctAppIds"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.ceravps" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="cerAvps"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.dpravps" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="dprAvps"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.dwravps" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="dwrAvps"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.sessioncleanupon" />&nbsp;<bean:message bundle="diameterResources" key="diameterpeerprofile.sessioncleanupcer" /></td>
	            <td  width="70%" class="tblcol"><bean:write name="diameterPeerProfileDataBean" property="sessionCleanUpCER"/></td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.sessioncleanupon" />&nbsp;<bean:message bundle="diameterResources" key="diameterpeerprofile.sessioncleanupdpr" /></td>
	            <td  width="70%" class="tblcol"><bean:write name="diameterPeerProfileDataBean" property="sessionCleanUpDPR"/></td>
	          </tr>     
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.diameterURI" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="redirectHostAvpFormat"/>&nbsp;</td>
	          </tr>      
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.followredirection" /></td>
	            <td class="tblcol" width="70%" >
		            <logic:equal value="true" property="followRedirection" name="diameterPeerProfileDataBean">Enabled</logic:equal>
		            <logic:equal value="false" property="followRedirection" name="diameterPeerProfileDataBean">Disabled</logic:equal>&nbsp;
	            </td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="diameterpeerprofile.hotlinepolicy" /></td>
	            <td class="tblcol" width="70%" ><bean:write name="diameterPeerProfileDataBean" property="hotlinePolicy"/>&nbsp;</td>
	          </tr>
		</table>
		</td>
    </tr>
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>   
   </tr>
</table>


