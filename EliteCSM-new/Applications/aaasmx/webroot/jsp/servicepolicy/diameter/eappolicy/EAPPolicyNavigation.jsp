<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.eappolicy.data.EAPPolicyData"%><table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr> 
<td colspan="2" valign="top"> 


<%
    EAPPolicyData eapPolicyInstData = (EAPPolicyData) request.getAttribute("eapPolicyData");
	
	String navigationBasePath = request.getContextPath();	
	
	String basicDetails = navigationBasePath+"/updateEAPPolicyBasicDetail.do?policyId="+eapPolicyInstData.getEapPolicyId();
	
	String authenticationDetails = navigationBasePath+"/updateEAPPolicyAuthenticationDetail.do?policyId="+eapPolicyInstData.getEapPolicyId();
	
	String authorizationDetails = navigationBasePath+"/updateEAPPolicyAuthorizationDetail.do?policyId="+eapPolicyInstData.getEapPolicyId();
	
	String rfcDetails = navigationBasePath+"/updateRFC4372CUIDetails.do?policyId="+eapPolicyInstData.getEapPolicyId();

	String responseAttributes = navigationBasePath+"/updateEAPPolicyResponseAttributesDetails.do?policyId="+eapPolicyInstData.getEapPolicyId();
	
	String driverDetails = navigationBasePath+"/updateEAPPolicyDriverProfile.do?policyId="+eapPolicyInstData.getEapPolicyId();
	
	String viewSummary = navigationBasePath+"/initViewEAPPolicy.do?policyId="+eapPolicyInstData.getEapPolicyId();
	
	String viewAdvanceDetails = navigationBasePath+"/viewEAPDetails.do?policyId="+eapPolicyInstData.getEapPolicyId();
	
	String viewHistory = navigationBasePath+"/viewEAPDetailsHistory.do?policyId="+eapPolicyInstData.getEapPolicyId()+"&auditUid="+eapPolicyInstData.getAuditUId()+"&name="+eapPolicyInstData.getName();	

%>
	  
	  <table border="0" width="100%" cellspacing="0" cellpadding="0">
	  
	  	 <tr id=header1> 
          <td class="subLinksHeader" width="87%"><bean:message key="general.action" /></td>
          <td class="subLinksHeader" width="13%"> <a href="javascript:void(0)" onClick="STB('UpdateRadiusPolicy');swapImages()"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a></td>
        </tr>
        <tr valign="top"> 
          <td colspan="2" id="backgr1"> 
            <div> 
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
				 <tr> 
                  <td class="subLinks"><a class="subLink" href="<%=basicDetails%>" ><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.update.basic.details" /></a></td>
                </tr>
             	<tr> 
                  <td class="subLinks"><a class="subLink" href="<%=authenticationDetails%>" > Update <bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.authenticationdetails" /></a></td>
                </tr>
                <tr> 
                  <td class="subLinks"><a class="subLink" href="<%=authorizationDetails%>" > Update <bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.authorizationdetails" /></a></td>
                </tr>
                <tr> 
                  <td class="subLinks"><a class="subLink" href="<%=rfcDetails%>" ><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.update.rfc4372cui.details" /></a></td>
                </tr>
                <tr>
               	  <td class="subLinks"><a class="subLink" href="<%=responseAttributes%>" > <bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.update.responseattribute" /></a></td>
                </tr>
                <tr> 
                  <td class="subLinks"><a class="subLink" href="<%=driverDetails%>" > Update <bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.driverdetails" /></a></td>
                </tr>
              </table>
          </div></td></tr>
	  
	     <tr id=header1> 
          <td class="subLinksHeader" width="87%"><bean:message key="general.view" /></td>
          <td class="subLinksHeader" width="13%"> <a href="javascript:void(0)" onClick="STB('ViewRadiusPolicy');swapImages()"><img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a></td>
        </tr>
        <tr valign="top"> 
          <td colspan="2" id="backgr1"> 
            <div> 
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
               	<tr> 
                  <td class="subLinks"><a href="<%=viewSummary%>" class="subLink"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.viewbasic" /></a></td>
                </tr>              
              </table>
          </div></td></tr>
         
        <tr valign="top"> 
          <td colspan="2" id="backgr1"> 
            <div> 
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
               	<tr> 
                  <td class="subLinks"><a href="<%=viewAdvanceDetails%>" class="subLink"><bean:message bundle="servicePolicyProperties" key="servicepolicy.eappolicy.details" /></a></td>
                </tr>              
              </table>
          </div></td></tr>
          	<tr>
				<td colspan="2" id="backgr1">
					 <div> 
	            		  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	             		  	<tr> 
	              			    <td class="subLinks">
									<a href="<%=viewHistory%>">
										<bean:message bundle="datasourceResources" key="database.datasource.viewDatabaseDatasourceHistory" />
									</a>
								</td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
      </table></td>
  </tr>

</table>
