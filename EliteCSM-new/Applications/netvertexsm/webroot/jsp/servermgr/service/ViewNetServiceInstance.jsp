<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceTypeData"%>


 <%
//	String basePath = request.getContextPath();
	String localBasePath = request.getContextPath();
	List netServiceTypeList = (List)request.getAttribute("netServiceTypeList");
	List netServerInstanceList = (List)request.getAttribute("netServerInstanceList");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<bean:define id="netServiceInstanceBean" name="netServiceInstanceData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData"/>
    <tr> 
	  <td class="table-header" ><bean:message bundle="servermgrResources" key="servermgr.servicedetails"/></td>
    </tr>
    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="servermgrResources" key="servermgr.servicesummary"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" height="20%" valign="top"><bean:message bundle="servermgrResources" key="servermgr.servicename"/></td>
            <td class="tblcol" 	height="20%" ><bean:write name="netServiceInstanceBean" property="name"/></td>
          </tr>
          
         <tr> 
            <td class="tblfirstcol" height="20%" valign="top"><bean:message bundle="servermgrResources" key="servermgr.servicetype"/></td>
            <td class="tblcol" height="20%" >
            	<logic:iterate id="netServiceTypeData" name="netServiceTypeList" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData">
					<logic:equal name="netServiceTypeData" property="netServiceTypeId" value="<%=netServiceInstanceBean.getNetServiceTypeId()%>">
						<bean:write name="netServiceTypeData" property="name"/>
					</logic:equal>
				</logic:iterate>
            </td>
          </tr>
          <tr> 
            <td class="tblfirstcol" height="20%" valign="top"><bean:message bundle="servermgrResources" key="servermgr.servername"/></td>
			<td class="tblcol" height="20%">
				<logic:iterate id="netServerInstance" name="netServerInstanceList" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServerInstanceData">
				   <logic:equal name="netServerInstance" property="netServerId" value="<%=Long.toString(netServiceInstanceBean.getNetServerId())%>">
				   		<bean:write name="netServerInstance" property="name"/>
				   </logic:equal>
				</logic:iterate>
          </tr>
         <tr> 
            <td class="tblfirstcol" height="20%" valign="top"><bean:message bundle="servermgrResources" key="servermgr.alias"/></td>
            <td class="tblcol" height="20%"><bean:write name="netServiceInstanceBean" property="displayName"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" height="20%" valign="top"><bean:message bundle="servermgrResources" key="servermgr.description"/></td>
            <td class="tblcol" height="20%"><bean:write name="netServiceInstanceBean" property="description"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" height="20%" valign="top"><bean:message bundle="servermgrResources" key="servermgr.status"/></td>
			  <logic:equal name="netServiceInstanceBean" property="commonStatusId" value="CST01">               
				<td class="tblcol" height="20%"><img src="<%=localBasePath%>/images/active.jpg"/>&nbsp;&nbsp;&nbsp;Active</td>  
			  </logic:equal>
			  <logic:notEqual name="netServiceInstanceBean" property="commonStatusId" value="CST01">
	  			<td class="tblcol" height="20%"><img src="<%=localBasePath%>/images/deactive.jpg"/>&nbsp;&nbsp;&nbsp;DeActive</td>
			  </logic:notEqual>
          </tr>  
        </table>
	  </td>
    </tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
</table>

