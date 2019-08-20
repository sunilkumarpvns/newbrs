<%@ include file="/jsp/core/includes/common/Header.jsp"%> 
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%
    String basePath = request.getContextPath(); 
	String auditname=request.getParameter("name");
	String actionName=request.getParameter("actionName");
	
	System.out.println("audit name : "+auditname);
%>

<script>
setTitle('<bean:message bundle="datasourceResources" key="database.datasource.history"/>');
</script>

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
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											 <td class="tblheader-bold" colspan="2" height="20%">
											 	<bean:message  bundle="datasourceResources" key="database.datasource.auditdetails" />
											 </td>
										</tr>
										<tr>
											<td class="tblfirstcol" height="20%" width="20%">
												<bean:message  bundle="datasourceResources" key="database.datasource.name" />
											</td>
											 <td class="tblcol" height="20%" width="80%" >
											 	<%=auditname%>
											 </td>
										</tr>
										<tr>
											<td class="tblfirstcol" height="20%" width="20%">
												<bean:message bundle="datasourceResources" key="database.datasource.actionname" />
											</td>
											 <td class="tblcol" height="20%" width="80%">
											 	<%=actionName%>
											 </td>
										</tr>
									</table>
								</td>
							</tr>
							<tr>	
								<td>
									<%@ include file="ViewHistoryJSON.jsp"%>
								</td>
							</tr>
    					</table>
        				</td>	
						<td width="168" class="grey-bkgd" valign="top">
							<%@  include file="HistoryNavigation.jsp"%> 
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

