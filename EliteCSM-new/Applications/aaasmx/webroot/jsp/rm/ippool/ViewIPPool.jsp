<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolData" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>


<%
    String basePath1 = request.getContextPath();
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0"  >
   <bean:define id="ipPoolBean" name="ipPoolData" scope="request" type="com.elitecore.elitesm.datamanager.rm.ippool.data.IPPoolData" />
    <tr> 
      <td valign="top" align="right" height="15%" > 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="ippoolResources" key="ippool.viewippool" /></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="ippoolResources" key="ippool.displayname" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="ipPoolBean" property="name"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="ippoolResources" key="ippool.status" /></td>
				<logic:equal name="ipPoolBean" property="commonStatusId" value="CST01">
				  <td class="tblcol" width="70%" height="20%"><img src="<%=basePath1%>/images/active.jpg"/></td>
				</logic:equal>
				<logic:notEqual name="ipPoolBean" property="commonStatusId" value="CST01">
	  			 <td class="tblcol" width="70%" height="20%"><img src="<%=basePath1%>/images/deactive.jpg"/></td>
				</logic:notEqual>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="ippoolResources" key="ippool.nas.ipaddress" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="ipPoolBean" property="nasIPAddress"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" valign="top" width="30%" height="20%"><bean:message bundle="ippoolResources" key="ippool.additionalattributes" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="ipPoolBean" property="additionalAttributes"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="ippoolResources" key="ippool.createdate" /></td>
			<td class="tblcol" width="705" height="20%"><%=EliteUtility.dateToString(ipPoolBean.getCreateDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="ippoolResources" key="ippool.lastmodifieddate" /></td>
			<td class="tblcol" width="705" height="20%"><%=EliteUtility.dateToString(ipPoolBean.getLastModifiedDate(), ConfigManager.get(ConfigConstant.DATE_FORMAT))%></td>
          </tr>            
        </table>
		</td>
    </tr>
</table>
