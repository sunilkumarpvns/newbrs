<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>

<%
    String basePath = request.getContextPath();
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
setTitle('<bean:message bundle="diameterResources" key="routingconf.title"/>');
</script>

<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>" border="0" cellspacing="0" cellpadding="0">
 <tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  			&nbsp;
  		</td>
			<td>
   				<table cellpadding="0" cellspacing="0" border="0" width="100%">
    			<tr>
					<td>
						<table cellpadding="0" cellspacing="0" border="0" width="100%">
  							<tr>
 	  							<td>
    								<table width="100%" border="0" cellspacing="0" cellpadding="0">				
										<tr>
											<td valign="top" align="right">
												<%@ include file="ViewDiameterRoutingConfBasicDetail.jsp" %>														
				  							</td>
										</tr>
										<tr>
											<td valign="top" align="right">
												<%@ include file="ViewDiameterRoutingTable.jsp" %>
				  							</td>
										</tr>
    								</table>
    							</td>
								<td width="168" class="grey-bkgd" valign="top">		
									<%@ include file="DiameterRoutingConfNavigation.jsp" %>
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