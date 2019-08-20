<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServerTypeData"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%
	  List netServerTypeList = (List)request.getAttribute("netServerTypeList");
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <bean:define id="netServerInstanceBean" name="netServerInstanceData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData" />
    <tr> 
	    <td class="table-header" ><bean:message bundle="servermgrResources" key="servermgr.serversummary"/>
        </td>
    </tr>
    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="4" height="20%"><bean:message bundle="servermgrResources" key="servermgr.serverinformation"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="20%" height="20%" ><bean:message bundle="servermgrResources" key="servermgr.servername"/></B></td>
            <td class="tblcol" width="30%" height="20%" ><bean:write name="netServerInstanceBean" property="name"/></td>
            <td class="tblfirstcol" width="20%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.serveridentification"/></td>
            <td class="tblcol" width="30%" height="20%"><bean:write name="netServerInstanceBean" property="netServerId"/></td>
          </tr>
<!--           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="servermgr.serveridentification"/></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="netServerInstanceBean" property="netServerId"/></td>
          </tr>  -->
<!--           <tr>
            <td class="tblfirstcol" width="30%" height="20%">Version</td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="netServerInstanceBean" property="version"/>
          </tr>  -->
          <tr> 
            <td class="tblfirstcol" height="20%"><bean:message bundle="servermgrResources" key="servermgr.servertype"/></td>
            <td class="tblcol" height="20%">
				<logic:iterate id="netServerTypeData" name="netServerTypeList" type="com.elitecore.elitesm.datamanager.servermgr.data.INetServerTypeData">	
				 	<logic:equal name="netServerTypeData" property="netServerTypeId" value="<%=netServerInstanceBean.getNetServerTypeId()%>">
                    	<bean:write name="netServerTypeData" property="name"/>
					</logic:equal>
		 		</logic:iterate>	
		    </td>
		    <td class="tblfirstcol" height="20%">Version</td>
            <td class="tblcol" height="20%"><bean:write name="netServerInstanceBean" property="version"/>
          </tr>   
          <tr>  
            <td class="tblfirstcol" height="20%"><bean:message bundle="servermgrResources" key="servermgr.description"/></td>
            <td class="tblcol" height="20%"><bean:write name="netServerInstanceBean" property="description"/>&nbsp;</td>
             <td class="tblfirstcol" height="20%"><bean:message bundle="servermgrResources" key="servermgr.staffuser"/></td>
            <td class="tblcol" height="20%"><bean:write name="netServerInstanceBean" property="staff"/></td>  
          </tr>
           <tr>  
            <td class="tblfirstcol" height="20%" valign="top"><bean:message bundle="servermgrResources" key="servermgr.serverhome"/></td>
            <td class="tblcol" height="20%" colspan="3"><bean:write name="netServerInstanceBean" property="serverHome"/>&nbsp;</td>

          </tr>
           <tr>  
            <td class="tblfirstcol" height="20%" valign="top"><bean:message bundle="servermgrResources" key="servermgr.javahome"/></td>
            <td class="tblcol" height="20%" colspan="3" ><bean:write name="netServerInstanceBean" property="javaHome"/>&nbsp;</td>
          </tr>
        </table>
	  </td>
    </tr>
</table>
