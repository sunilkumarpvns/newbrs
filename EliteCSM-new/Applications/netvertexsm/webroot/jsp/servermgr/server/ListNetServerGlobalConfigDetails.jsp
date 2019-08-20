<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData"%>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.ListNetServerGlobalConfigurationForm" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerConfigMapTypeData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerConfigMapTypeData" %>






<% 
	String localBasePath1 = request.getContextPath();
%>

<%
	ListNetServerGlobalConfigurationForm listNetServerGlobalConfigurationForm = (ListNetServerGlobalConfigurationForm)request.getAttribute("listNetServerGlobalConfigurationForm");
	INetServerConfigMapTypeData netServerConfigMapTypeData = (NetServerConfigMapTypeData)request.getAttribute("netServerConfigMapTypeData");
	List configInstanceList = ((ListNetServerGlobalConfigurationForm)request.getAttribute("listNetServerGlobalConfigurationForm")).getConfigInstanceList();
	int iIndex = 0;
%>

<html:form action="/listNetServerConfiguration">
<html:hidden name="listNetServerGlobalConfigurationForm" styleId="action" property="action"/>
<html:hidden name="listNetServerGlobalConfigurationForm" styleId="netServerId" property="netServerId"/>

<table width="100%" border="0" cellspacing="0" cellpadding="0"> 
    <tr>
     <td valign="top" align="right"> 
       <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
			<td class="tblheader-bold" colspan="3"><bean:message bundle="servermgrResources" key="servermgr.view.updateserverglobalconfiguration"/></td>
		  </tr>
		  <tr>
            <td colspan="3">
              <table width="100%" cols="8" id = "listTable" type="tbl-list" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="right" class="tblheader" valign="top" width="5%" ><bean:message bundle="servermgrResources" key="servermgr.serialnumber"/></td>
                  <td align="left" class="tblheader" valign="top" width="20%" ><bean:message bundle="servermgrResources" key="servermgr.name"/></td>
                  <td align="left" class="tblheader" valign="top" width="25%" ><bean:message bundle="servermgrResources" key="servermgr.alias"/></td>
                  <td align="center" class="tblheader" valign="top" width="5%" ><bean:message key="general.edit" /></td>                          
                </tr>
<%
    if(configInstanceList != null && configInstanceList.size() > 0){
%>
  			<logic:iterate id="netConfigInstanceData" name="listNetServerGlobalConfigurationForm" property="configInstanceList" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationInstanceData">                
			    <bean:define id="netConfigurationData" name="netConfigInstanceData" property="netConfiguration" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetConfigurationData"></bean:define>
                <tr>
			      <td align="right" class="tblfirstcol" valign="top" ><%=(iIndex+1) %></td>
                  <td align="left" class="tblrows" valign="top" ><bean:write name="netConfigurationData" property="name"/></td>
   				  <td align="left" class="tblrows"><bean:write name="netConfigurationData" property="displayName"/></td>
			      <td align="center" class="tblrows">
					<a href="<%=localBasePath1%>/updateNetServerConfiguration.do?confInstanceId=<bean:write name="netConfigInstanceData" property="configInstanceId"/>&netServerId=<%=listNetServerGlobalConfigurationForm.getNetServerId()%>"><img src="<%=localBasePath%>/images/edit.jpg" border="0"/></a>
                  </td>     
                </tr>         
               <% iIndex += 1; %>
			   </logic:iterate>
<%
    }else{
%>                       
			    <tr>
                  <td align="center" class="tblfirstcol" colspan="8"><bean:message bundle="servermgrResources" key="servermgr.norecordsfound"/></td>
                </tr>
<%
    }
%>		
			   </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
</table>
</html:form>
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	


