<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<% String basePath = request.getContextPath(); %>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<%@page import="com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData"%>
<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>" border="0" cellspacing="0" cellpadding="0">
  <tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  			&nbsp;
  		</td>
		<td>
   			<table cellpadding="0" cellspacing="0" border="0" width="100%">
    			<tr>
    				<td  cellpadding="0" cellspacing="0" border="0" width="100%">
    				<table width="100%" border="0" cellspacing="0" cellpadding="0">				
					<tr>
						<td valign="top" align="right">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">	
							<tr>	
								<td>
									<%@ include file="ViewRadiusESIGroup.jsp" %>
				  				</td>
							</tr>
    					</table>
    					</td>	
						<td width="168" class="grey-bkgd" valign="top">
							 <%@ include file="RadiusESIGroupNavigation.jsp" %>
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
	setTitle('<bean:message bundle="radiusResources" key="radiusesigroup.title"/>');
</script>			