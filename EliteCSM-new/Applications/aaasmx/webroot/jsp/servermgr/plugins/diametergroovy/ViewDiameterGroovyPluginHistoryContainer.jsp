<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>

<% String basePath = request.getContextPath(); %>

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
										<td class="tblheader-bold" height="20%" colspan="2">
											<bean:message bundle="pluginResources" key="plugin.view"/>
										</td>
									</tr>  
								     <tr>
										<td align="left" class="tblheader-bold" valign="top" colspan="2">
										<bean:message bundle="pluginResources" key="plugin.plugininstancedetails" /></td>
									 </tr>  					
								   	 <tr>
										<td class="tblfirstcol" width="20%" height="20%" >
											<bean:message bundle="pluginResources" key="plugin.instname" />
										</td>
										<td class="tblcol" width="30%" height="20%" >
											<bean:write name="pluginInstance" property="name" />
										</td>
									 </tr>   
								 	 <tr>
										<td class="tblfirstcol" width="20%" height="20%">
											<bean:message bundle="pluginResources" key="plugin.instdesc" />
										</td>
										<td class="tblcol" width="30%" height="20%" >
											<bean:write name="pluginInstance" property="description"/>
										</td>
									</tr>
									<tr>	
										<td colspan="2">		
											<%@ include file="ViewDiameterGroovyHistory.jsp"%>
		    							</td>	
									</tr>
								</table>
								</td>
								<td width="168" class="grey-bkgd" valign="top">
									<%@ include file="/jsp/servermgr/plugins/diametergroovy/DiameterGroovyPluginNavigation.jsp"%>
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

<script>
	setTitle('<bean:message bundle="pluginResources" key="plugin.diameter.groovyplugin.title"/>'); 
</script>