<%@page import="java.net.URLDecoder"%>
<%@page import="java.net.URLEncoder"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>

<!-- This included because header js and plugin js is conflicted so used required css,js and taglib into specific module -->
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>
<%@ taglib prefix="ec" uri="/elitetags" %>

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
											 <%@ include file="/jsp/servermgr/plugins/quotamanagement/UpdateQuotaManagementPlugin.jsp"%>
		    							</td>	
									</tr>
								</table>
								</td>
								<td width="168" class="grey-bkgd" valign="top">
									<%@ include file="/jsp/servermgr/plugins/quotamanagement/QuotaManagementPluginNavigation.jsp"%>
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