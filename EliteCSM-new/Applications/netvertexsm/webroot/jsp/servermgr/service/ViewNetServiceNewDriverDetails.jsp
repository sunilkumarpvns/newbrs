<%@ page import="java.util.List" %>

<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.NetDriverInstanceData"%>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.service.form.ViewNetServiceDriversInstanceForm" %>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.service.form.ViewNetServiceDriverDetailForm" %>

<%@ include file="/jsp/core/includes/common/Header.jsp"%>





<% 
	String localBasePath = request.getContextPath();
%>

<%
    List lstDriverData = ((ViewNetServiceDriverDetailForm)request.getAttribute("viewNetServiceDriverDetailForm")).getNetServiceDriverDetailBean();
    int iIndex = 0;
%>

<html:form action="/viewNetServiceDriverDetails">
<html:hidden name="viewNetServiceDriverDetailForm" styleId="itemIndex" property="itemIndex"/>
<html:hidden name="viewNetServiceDriverDetailForm" styleId="netServiceId" property="netServiceId"/>


<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
          <tr>
            <td class="table-header" colspan="3"><bean:message bundle="servermgrResources" key="servermgr.servicedriversinformation"/></td>
          </tr>
           <tr>
            <td class="btns-td" valign="middle" colspan="3">
              <table cellpadding="0" cellspacing="0" border="0" width="100%" >
                <tr>
                  <td class="small-gap" colspan="3">&nbsp;</td>
                </tr>
                <tr>
                  <td colspan="3">
                    <table width="100%" cols="8" id = "listTable" type="tbl-list" border="0" cellpadding="0" cellspacing="0">
                      <tr>
                        <td align="right" class="tblheader" valign="top" width="5%" ><bean:message bundle="servermgrResources" key="servermgr.serialnumber"/></td>
                        <td align="left" class="tblheader" valign="top" width="20%" ><bean:message bundle="servermgrResources" key="servermgr.name"/></td>
<%--                         <td align="left" class="tblheader" valign="top" width="25%" ><bean:message bundle="servermgrResources" key="servermgr.alias"/></td>  --%>
                        <td align="left" class="tblheader" valign="top" width="25%" ><bean:message bundle="servermgrResources" key="servermgr.description"/></td>                          
                        <td align="left" class="tblheader" valign="top" width="20%" ><bean:message bundle="servermgrResources" key="servermgr.drivertype"/></td>
                        <td align="center" class="tblheader" valign="top" width="5%" ><bean:message key="general.edit" /></td>                          
                      </tr>
<%
    if(lstDriverData != null && lstDriverData.size() > 0){
%>
				<logic:iterate id="netDriverInstanceData" name="viewNetServiceDriverDetailForm" property="netServiceDriverDetailBean" type="com.elitecore.netvertexsm.web.servermgr.service.forms.NetServiceDriverDetailBean">
                      <tr>
            	        <td align="right" class="tblfirstcol" valign="top" ><%=(iIndex+1) %></td>
                        <td align="left" class="tblrows" valign="top" ><bean:write name="netDriverInstanceData" property="name"/></td>
<%--	   					<td align="left" class="tblrows"><bean:write name="netDriverInstanceData" property="displayName"/></td>  --%>
						<td align="left" class="tblrows"><bean:write name="netDriverInstanceData" property="description"/></td>     
						<td align="left" class="tblrows">
						  <logic:iterate id="netDriverTypeData" name="netDriverTypeList" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetDriverTypeData"> 				  
					  		<logic:equal name="netDriverTypeData" property="netDriverTypeId" value="<%=netDriverInstanceData.getNetDriverTypeId()%>">
							  <bean:write name="netDriverTypeData" property="name"/>
					  		</logic:equal>
	 				  	  </logic:iterate>&nbsp;
	                    </td>
	                  <td align="center" class="tblrows">
							<a href="<%=localBasePath%>/updateNetDriverConfiguration.do?confInstanceId=<bean:write name="netDriverInstanceData" property="netConfigInstanceId"/>&netDriverId=<bean:write name="netDriverInstanceData" property="netDriverId"/>&netServiceId=<bean:write name="netDriverInstanceData" property="netServiceId"/>">                            
                            	<img src="<%=localBasePath%>/images/edit.jpg" border="0"/>
		                    </a>
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
            <td class="small-gap" width="50%" colspan="2" >&nbsp;</td>
          </tr>
        </table>
      </td>
    </tr>
</table>
</html:form>
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	