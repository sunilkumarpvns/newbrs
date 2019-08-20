<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.server.form.RemoveNetServerServiceInstanceForm" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData"%>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.service.form.RemoveNetServiceDriverInstanceForm" %>

<%@ include file="/jsp/core/includes/common/Header.jsp"%>





<% 
	String localBasePath = request.getContextPath();
%>

<%
	List netDriverInstanceList = (List)request.getAttribute("netDriverInstanceList");
	List lstDriverData = ((RemoveNetServiceDriverInstanceForm)request.getAttribute("removeNetServiceDriverInstanceForm")).getListDrivers();
//	List lstNetDriverType = (List)request.getAttribute("lstNetDriverType");
	int iIndex = 0;
%>

<script>
function remove(){
//	alert('here press the remove button');
	document.forms[0].action.value = 'remove';
	document.forms[0].submit();
}
</script>

<html:form action="/removeNetServiceDriverInstance">
<html:hidden name="removeNetServiceDriverInstanceForm" styleId="action" property="action"/>
<html:hidden name="removeNetServiceDriverInstanceForm" styleId="itemIndex" property="itemIndex"/>
<html:hidden name="removeNetServiceDriverInstanceForm" styleId="netServiceId" property="netServiceId"/>

<table width="100%" border="0" cellspacing="0" cellpadding="0"> 
    <tr>
      <td valign="top" align="right"> 
       <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
			<td class="tblheader-bold" colspan="3"><bean:message bundle="servermgrResources" key="servermgr.removedriverinstances"/></td>
		  </tr>
	      <tr>
            <td colspan="3">
              <table width="100%" cols="8" id = "listTable" type="tbl-list" border="0" cellpadding="0" cellspacing="0">
                <tr>
                 <td align="center" class="tblheader" valign="top" width="4%">
                     <input type="checkbox" value="checkbox" />
                  </td>
                  <td align="right" class="tblheader" valign="top" width="5%" ><bean:message bundle="servermgrResources" key="servermgr.serialnumber"/></td>
                  <td align="left" class="tblheader" valign="top" width="20%" ><bean:message bundle="servermgrResources" key="servermgr.name"/></td>
<!--                   <td align="left" class="tblheader" valign="top" width="25%" ><bean:message bundle="servermgrResources" key="servermgr.alias"/></td>  -->
                  <td align="left" class="tblheader" valign="top" width="25%" ><bean:message bundle="servermgrResources" key="servermgr.description"/></td>                          
	              <td align="left" class="tblheader" valign="top" width="20%" ><bean:message bundle="servermgrResources" key="servermgr.drivertype"/></td>
                </tr>
<%
    if(lstDriverData != null && lstDriverData.size() > 0){
%>
           <logic:iterate id="netDriverInstanceData" name="removeNetServiceDriverInstanceForm" property="listDrivers" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetDriverInstanceData">
                <tr>
                  <td align="center" class="tblfirstcol" valign="top" >
                     <input type="checkbox" name="select" value="<bean:write name="netDriverInstanceData" property="netDriverId"/>"/>
                  </td>
			      <td align="right" class="tblfirstcol" valign="top" ><%=(iIndex+1) %></td>
                  <td align="left" class="tblrows" valign="top" ><bean:write name="netDriverInstanceData" property="name"/></td>
<!--    				  <td align="left" class="tblrows"><bean:write name="netDriverInstanceData" property="displayName"/></td>  -->
 				  <td align="left" class="tblrows"><bean:write name="netDriverInstanceData" property="description"/></td>
 				  <td align="left" class="tblrows">
					<logic:iterate id="netDriverTypeData" name="lstNetDriverType" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetDriverTypeData"> 				  
				  		<logic:equal name="netDriverTypeData" property="netDriverTypeId" value="<%=netDriverInstanceData.getNetDriverTypeId()%>">
							<bean:write name="netDriverTypeData" property="name"/>
				  		</logic:equal>
 				  	</logic:iterate>&nbsp;
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
   	<tr> 
      <td class="btns-td" valign="middle"  >
	     <html:button property="c_btnDelete" onclick="remove()" value="Delete" styleClass="light-btn" />
<!--          <input type="button" name="c_btnUpdate"  onclick="javascript:location.href='<%=localBasePath%>/jsp/servermgr/ServerContainer.jsp'"  id="c_btnUPdate"  value="Save"  class="light-btn">     -->
         <input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=localBasePath%>/viewNetServiceInstance.do?netserviceid=<bean:write name="netServiceInstanceBean" property="netServiceId"/>'" value="Cancel" class="light-btn"> 
      </td>
	</tr>
	<tr> 
      <td>&nbsp;</td>
    </tr>
</table>
</html:form>
<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	
