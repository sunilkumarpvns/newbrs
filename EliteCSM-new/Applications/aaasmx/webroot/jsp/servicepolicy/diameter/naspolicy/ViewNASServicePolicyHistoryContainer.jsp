<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page  import="com.elitecore.elitesm.datamanager.externalsystem.data.*" %> 
<%@ page  import="com.elitecore.elitesm.web.core.system.cache.*" %> 
<%@ page  import="com.elitecore.elitesm.util.constants.*" %> 
<%@ page  import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.*" %> 

<%
    String basePath = request.getContextPath(); 
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
setTitle('<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy"/>');
</script>
<bean:define id="nasPolicyInstDataBean" name="nasPolicyInstData" scope="request" type="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyInstData" />
<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>" border="0" cellspacing="0" cellpadding="0">
 <tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  			&nbsp;
  		</td>
			<td>
   				<table cellpadding="0" cellspacing="0" border="0" width="100%">
    			<tr>
					<td  width="100%" class="box" valign="top">
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
  							<tr>
 	  							<td  width="*" class="box"  valign="top">
    								<table width="100%" border="0" cellspacing="0" cellpadding="0">				
										<tr>
											<td valign="top" align="right">
												<%@ include file="ViewNASServicePolicyBasicDetail.jsp" %>
											</td>
										</tr>
										<tr>
											<td><%@ include
													file="/jsp/core/system/history/ViewHistoryJSON.jsp"%>
											</td>
										</tr>
									</table>
    							</td>	
								<td width="168" class="grey-bkgd" valign="top">
									<%@ include file="NASServicePolicyNavigation.jsp" %>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp" %>
			</table>
		</td>
	</tr>
</table>
	 


