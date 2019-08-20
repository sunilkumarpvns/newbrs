<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.elitesm.web.servermgr.server.forms.ViewNetServerServicesForm" %>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.data.NetServiceInstanceData"%>

<%@ include file="/jsp/core/includes/common/Header.jsp"%>





<% 
	String localBasePath = request.getContextPath();
%>

<%
//    String basePath = request.getContextPath();
	  List netServiceInstanceList = (List)request.getAttribute("netServiceInstanceList");
	  List lstServiceData = ((ViewNetServerServicesForm)request.getAttribute("viewNetServerServiesForm")).getListServices();
	  List lstNetServiceType = (List)request.getAttribute("lstNetServiceType");
	  int iIndex = 0;
%>


<html:form action="/viewNetServerServices">
<html:hidden name="viewNetServerServiesForm" styleId="action" property="action"/>

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
	<tr>
      <td colspan="3">&nbsp;</td>
    </tr>

	<tr>
      <td class="table-header" colspan="3"><bean:message bundle="servermgrResources" key="servermgr.servicelist"/></td>
    </tr>
    <tr>
     <td width="100%" valign="right" colspan="3" >
        <table width="100%" cellpadding="0" cellspacing="0" border="0" align="right" >
          <tr>
            <td class="small-gap" colspan="3">&nbsp;</td>
          </tr>
          <tr>
            <td colspan="3">
              <table width="100%" cols="8" id = "listTable" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="right" class="tblheader" valign="top" width="5%" ><bean:message bundle="servermgrResources" key="servermgr.serialnumber"/></td>
                  <td align="left" class="tblheader" valign="top" width="25%" ><bean:message bundle="servermgrResources" key="servermgr.name"/></td>
                  <td align="left" class="tblheader" valign="top" width="20%" ><bean:message bundle="servermgrResources" key="servermgr.servicetype"/></td>                          
                  <td align="left" class="tblheader" valign="top" width="30%" ><bean:message bundle="servermgrResources" key="servermgr.description"/></td>
                </tr>
<%
    if(lstServiceData != null && lstServiceData.size() > 0){
%>
               <logic:iterate id="netServiceInstanceData" name="viewNetServerServiesForm" property="listServices" type="com.elitecore.elitesm.datamanager.servermgr.data.INetServiceInstanceData">
                <tr>
			      <td align="right" class="tblfirstcol" valign="top" ><%=(iIndex+1) %></td>
                  <td align="left" class="tblrows" valign="top" ><a href="<%=localBasePath%>/viewNetServiceInstance.do?netserviceid=<bean:write name="netServiceInstanceData" property="netServiceId"/>"><bean:write name="netServiceInstanceData" property="name"/></td>
				  <td align="left" class="tblrows">
				  	<logic:iterate id="netServiceTypeData" name="lstNetServiceType" type="com.elitecore.elitesm.datamanager.servermgr.data.INetServiceTypeData"> 				  
				  		<logic:equal name="netServiceTypeData" property="netServiceTypeId" value="<%=netServiceInstanceData.getNetServiceTypeId()%>">
							<bean:write name="netServiceTypeData" property="name"/>  
				  		</logic:equal>
 				  	</logic:iterate>
				  </td>
 				  <td align="left" class="tblcol"><bean:write name="netServiceInstanceData" property="description"/>&nbsp</td>
                </tr>
<% iIndex += 1; %>
				 </logic:iterate>
<%
    }else{
%>
			    <tr>
                  <td align="center" class="tblfirstcol" colspan="8">No Records Found.</td>
                </tr>
<%
    }
%>				
              </table>
            </td>
          </tr>
          <tr>
            <td class="small-gap" width="50%" colspan="2" >&nbsp;</td>
          </tr>
        </table>
      </td>
    </tr>
</table>
</html:form>