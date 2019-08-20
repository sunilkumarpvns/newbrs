<%@page import="com.elitecore.commons.base.Collectionz"%>
<%@ page import="com.elitecore.corenetvertex.constants.ServicePolicyActions"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData" %>
<%@page import="com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicySyGatewayRelData"%>
<%@page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%
	PCRFServicePolicyData pcrfPolicyData = (PCRFServicePolicyData) request.getAttribute("pcrfPolicyData");
	boolean isInvalidWeightAge = false;
	if(pcrfPolicyData != null){
		List<PCRFServicePolicySyGatewayRelData> syGatewayRelDataList = pcrfPolicyData.getPcrfServicePolicySyGatewayRelDataList();
		if(Collectionz.isNullOrEmpty(syGatewayRelDataList) == false){
			for(PCRFServicePolicySyGatewayRelData pcrfServicePolicySyGatewayRelData:syGatewayRelDataList){
		        if(pcrfServicePolicySyGatewayRelData.getWeightage() == 0){
					isInvalidWeightAge = true;
					break;
				}
			}
		}
	}

	String name = pcrfPolicyData.getName();
	List<DriverInstanceData> cdrDriverList = (List<DriverInstanceData>)request.getAttribute("cdrDriverList");
	List<GatewayData> 	 syGatewayDataList = (List<GatewayData>)request.getAttribute("syGatewayDataList");
	String redirectURL = request.getContextPath()+"/initEditPCRFServicePolicy.do?pcrfPolicyId="+pcrfPolicyData.getPcrfPolicyId();
%>


<script type="text/javascript">
	$(document).ready(function(){
        var isInvalidWeightAge = '<%=isInvalidWeightAge%>';
        if(isInvalidWeightAge == 'true'){
            var redirect = confirm("Configured PCRF Service policy has older configuration of Sy gateways. Please update with valid configuration!!!");
            if(redirect){
                document.location.href = "<%=redirectURL%>";
            }
        }
	});
</script>

<%@page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page
		import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager" %>
<%@ page import="java.util.List" %>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
	<td valign="top" align="right">
	<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
		<bean:define id="pcrfPolicyBean" name="pcrfPolicyData" scope="request" type="com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData" />
		<tr>
			<td class="tblheader-bold"  valign="top" colspan="3"><bean:message bundle="servicePolicyProperties" key="servicepolicy.view" /></td>
		</tr>
		          <tr>
		            <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="servicePolicyProperties" key="servicepolicy.name" /></td>
		            <td class="tblcol" width="70%" height="20%" ><bean:write name="pcrfPolicyBean" property="name"/>&nbsp;</td>
		          </tr>
		          <tr>
		            <td class="tbllabelcol" width="30%" height="20%"><bean:message  bundle="servicePolicyProperties" key="servicepolicy.description" /></td>
		            <td class="tblcol" width="70%" height="20%" ><bean:write name="pcrfPolicyBean" property="description"/>&nbsp;</td>
		          </tr>
	              <tr>
		            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.ruleset" /></td>
		            <td class="tblcol" width="70%" height="20%"><bean:write name="pcrfPolicyBean" property="ruleset"/>&nbsp;</td>
		          </tr>
		          <tr>
		             <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.idattribute" /></td>
		             <td class="tblcol" width="70%" height="20%"><bean:write name="pcrfPolicyBean" property="identityAttribute"/>&nbsp;</td>
		          </tr>
		          <tr>
		             <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.sy.mode"/></td>
		             <td class="tblcol" width="70%" height="20%"><bean:write name="pcrfPolicyBean" property="syMode"/>&nbsp;</td>
		          </tr>	          
		          <logic:equal name="pcrfPolicyBean" property="unknownUserAction" value="1" >
			          <tr>
			           	<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.unknownuseraction" /></td>
			           	<td class="tblcol" width="70%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.allowunknownuser" />&nbsp;</td>
			          </tr>
		          </logic:equal>  
		          <logic:equal name="pcrfPolicyBean" property="unknownUserAction" value="2" >
			          <tr>
			           	<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.unknownuseraction" /></td>
			           	<td class="tblcol" width="70%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.rejectunknownuser" />&nbsp;</td>
			          </tr>
		          </logic:equal>
		          <logic:equal name="pcrfPolicyBean" property="unknownUserAction" value="3" >
			          <tr>
			           	<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.unknownuseraction" /></td>
			           	<td class="tblcol" width="70%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.droprequest" />&nbsp;</td>
			          </tr>
		          </logic:equal>	          	         
		          <logic:equal name="pcrfPolicyBean" property="unknownUserAction" value="1" >
		          	<tr>
		            	<td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="servicePolicyProperties" key="servicepolicy.pkgdata" /></td>
		            	<td class="tblcol" width="70%" height="20%">
		            	<bean:write name="pcrfPolicyBean" property="pkgName"/>&nbsp;
		            	</a>
		            	</td>
		          	</tr>
		          </logic:equal>	       
		       <tr>
				<td class="tbllabelcol" width="20%" height="20%" >
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.action" /></td>
						<td class="tblcol" width="30%" height="20%" >
							<%
								ServicePolicyActions action = ServicePolicyActions.fromValue((int)(long)(pcrfPolicyBean.getAction()));
							%>
							<%=action.getName()%>
							&nbsp;
						</td>
				</tr>
		       <tr> 
	            <td class="tbllabelcol" width="30%" height="20%"><bean:message key="general.status" /></td>
					<logic:equal name="pcrfPolicyBean" property="status" value="CST01">
				    	<td class="tblcol" width="70%" height="20%" colspan="3"><img style="vertical-align:middle;" src="<%=basePath%>/images/active.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.active" /></td>
					</logic:equal>
					<logic:equal name="pcrfPolicyBean" property="status" value="CST02">
				    	<td class="tblcol" width="70%" height="20%" colspan="3"><img style="vertical-align:middle;" src="<%=basePath%>/images/deactive.jpg"/>&nbsp;&nbsp;&nbsp;<bean:message bundle="servicePolicyProperties" key="servicepolicy.inactive" /></td>
					</logic:equal>
	          </tr>	  
	          <tr>
	          		<td class="tbllabelcol" width="30%" height="20%">
	          			<bean:message key="general.lastmodifieddate" />
	          		</td>
	  
	          		 <td class="tblcol" width="70%" height="20%" colspan="1">
	          			<%= EliteUtility.dateToString(pcrfPolicyBean.getModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%> &nbsp; 
	          		</td>
	          		
	          </tr>        
		</table>
	</td>
    </tr>
</table>

<br>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right">
	<bean:define id="pcrfPolicyBean" name="pcrfPolicyData" scope="request" type="com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData" />
	<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">	
	<tr>
			<td class="tblheader-bold"  valign="top" colspan="3"><bean:message  bundle="servicePolicyProperties" key="servicepolicy.sy.gateways"/></td>
	</tr>
	<%if(syGatewayDataList != null && syGatewayDataList.size() > 0){%>
	
		
		 		<tr>
		 			<td class="tblheader-bold" width="30%" height="20%"><bean:message key="general.name" /></td>
					<td class="tblheader-bold" width="70%" height="20%"><bean:message bundle="gatewayResources" key="gateway.profile"/></td>
				</tr>
				<logic:iterate id="syGatewayBean" name="syGatewayDataList" type="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData">
			        <tr>
			            <td class="tblfirstcol" width="30%" height="20%">
			            	<a href="<%=basePath%>/viewGateway.do?gatewayId=<bean:write name="syGatewayBean" property="gatewayId"/>&commProtocolId=<bean:write name="syGatewayBean" property="commProtocol" />" tabindex="12">
								<bean:write name="syGatewayBean" property="gatewayName" />&nbsp;												
							</a>											
			            </td>
			            <td class="tblcol" width="70%" height="20%">
			            	<bean:write name="syGatewayBean" property="gatewayProfileData.profileName"/>&nbsp;
			            </td>
			        </tr>
				</logic:iterate>	
				
	<%}else{%>
		<tr >
			<td align="center" class="tblfirstcol"  colspan="2">	<bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/></td>
		</tr>
	
	<%}%>
	</table>
		</td>
    </tr>
</table>
<br>


<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right">
	<bean:define id="pcrfPolicyBean" name="pcrfPolicyData" scope="request" type="com.elitecore.netvertexsm.datamanager.servicepolicy.pcrfservicepolicy.data.PCRFServicePolicyData" />
	<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right">
	<tr>
			<td class="tblheader-bold"  valign="top" colspan="3"><bean:message  bundle="servicePolicyProperties" key="servicepolicy.cdrdriver"/></td>
	</tr>
	<%if(cdrDriverList != null & cdrDriverList.size() > 0){%>
		
		 		<tr>
		 			<td class="tblheader-bold" width="30%" height="20%"><bean:message key="general.name" /></td>
					<td class="tblheader-bold" width="70%" height="20%"><bean:message  bundle="servicePolicyProperties" key="servicepolicy.driverinstance.type" /></td>
				</tr>
		<logic:iterate id="cdrDrivers" name="cdrDriverList" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData">
		        <tr>
		            <td class="tblfirstcol" width="30%" height="20%">
		            	<a href="<%=basePath%>/viewDriverInstance.do?driverInstanceId=<bean:write name="cdrDrivers" property="driverInstanceId"/>" tabindex="2">
		            		<bean:write name="cdrDrivers" property="name"/>&nbsp;
		            	</a>		            	
		            </td>
		            <td class="tblcol" width="70%" height="20%"><bean:write name="cdrDrivers" property="driverTypeData.name"/>&nbsp;</td>
		        </tr>
		</logic:iterate>	
				
	<%}else{%>
	
		<tr>
			<td align="center" class="tblfirstcol"  colspan="2">	<bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/></td>
		</tr>
	
	<%}%>
		</table>
		</td>
    </tr>
</table> 

<br>
