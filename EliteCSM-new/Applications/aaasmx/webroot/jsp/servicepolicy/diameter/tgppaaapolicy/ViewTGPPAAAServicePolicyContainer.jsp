<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page  import="com.elitecore.elitesm.datamanager.externalsystem.data.*" %> 

<%
    String basePath = request.getContextPath();
%>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>" border="0" cellspacing="0" cellpadding="0">
  <tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  			&nbsp;
  		</td>
		<td>
   			<table cellpadding="0" cellspacing="0" border="0" width="100%">
    			<tr>
    				<td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
    				<table width="100%" border="0" cellspacing="0" cellpadding="0">				
					<tr>
						<td valign="top" align="right">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">	
							<tr>	
								<td>
									<%@ include file="ViewTGPPAAAServicePolicyBasicDetails.jsp" %>
				  				</td>
							</tr>
							<% if("true".equals(request.getAttribute("viewHistory").toString())){ %>
							<tr>
				  				<td>
									<%@ include file="/jsp/core/system/history/ViewHistoryJSON.jsp"%>
				  				</td>
				  			</tr>	
				  			<%}%>
    					</table>
    					</td>	
						<td width="168" class="grey-bkgd" valign="top">
							 <%@ include file="TGPPAAAPolicyNavigation.jsp" %> 
						</td>
					</tr>
					</table>
					</td>
				</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
			</table>		  
		</td>
	</tr>	   
</table>		
<script>
	setTitle('<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.policy"/>');
</script>			