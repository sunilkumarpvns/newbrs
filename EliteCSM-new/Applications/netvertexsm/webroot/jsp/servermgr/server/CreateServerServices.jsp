<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.server.form.CreateServerForm"%>
<%@page import="java.util.List"%>
<jsp:directive.page import="com.elitecore.netvertexsm.util.constants.BaseConstant"/>





<%
    CreateServerForm createServerForm = (CreateServerForm) request.getAttribute("createServerForm");
    List lstServiceType = createServerForm.getLstServiceType();
%>

<style>
.light-btn {  border:medium none; font-family: Arial; font-size: 12px; color: #FFFFFF; background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg'); font-weight: bold}
</style>

<script>
function  checkAll(){
	var arrayCheck = document.getElementsByName('selectedServices');
	
 	if( document.forms[0].toggleAll.checked == true) {
	 	for (i = 0; i < arrayCheck.length;i++){
			arrayCheck[i].checked = true ;
		}
    } else if (document.forms[0].toggleAll.checked == false){
		for (i = 0; i < arrayCheck.length; i++){
			arrayCheck[i].checked = false ;
		}
	}
}
function validateCreate(){
	document.forms[0].submit();
}
function goPrevious(){
	history.go(-1);
}

$(document).ready(function(){
	setTitle('<bean:message bundle="servermgrResources" key="servermgr.service"/>');
});
</script>
<% String groupId = (String)request.getAttribute("groupId"); %>
<html:form action="/createServer">
<html:hidden styleId="isInSync" property="isInSync" value="<%=BaseConstant.HIDE_STATUS_ID%>"/>
<html:hidden styleId="action" property="action" value="create"/>
<html:hidden styleId="action" property="netServerType" value="<%=createServerForm.getNetServerType()%>"/>
<html:hidden styleId="action" property="javaHome" value="<%=createServerForm.getJavaHome()%>"/>
<html:hidden styleId="action" property="description" value="<%=createServerForm.getDescription()%>"/>
<html:hidden styleId="action" property="adminInterfaceIP" value="<%=createServerForm.getAdminInterfaceIP()%>"/>
<html:hidden styleId="action" property="adminInterfacePort" value="<%=Integer.toString(createServerForm.getAdminInterfacePort())%>"/>
<html:hidden styleId="action" property="name" value="<%=createServerForm.getName()%>"/>
<html:hidden styleId="action" property="serverHome" value="<%=createServerForm.getServerHome()%>"/>
<html:hidden styleId="action" property="status" value="<%=createServerForm.getStatus()%>"/>
<html:hidden property="groupId" value="<%=groupId %>"/>

<table cellpadding="0" cellspacing="0" border="0" width="100%" >
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr>
	  <td width="10">&nbsp;</td>
	  <td width="100%" colspan="2" valign="top" class="box">
		<table cellSpacing="0" cellPadding="0" width="100%" border="0">
	 	  <tr>
			<td class="table-header" colspan="5"><bean:message bundle="servermgrResources" key="servermgr.addservice"/><%--<img src="<%=basePath%>/images/open.jpg" border="0" name="closeopen"></td> --%>
		  </tr>
		  <tr>
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
		  </tr> 
		  <tr>
			<td colspan="3" style="padding-left:50px">
			   <table width="80%" name="c_tblCrossProductList" id="c_tblCrossProductList" align="left" border="0" cellpadding="0" cellspacing="0" >
				 <%
				
				   if(lstServiceType!=null){%>
				   	<tr>
			   			<td align="center" class="tblheader" valign="top" width="8%">
			   				<input type="checkbox" name="toggleAll" id="toggleAll" tabindex="1"  value="checkbox" onclick="checkAll()"/>
						</td>
						<td align="left" class="tblheader-bold" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.serialnumber"/></td>
						<td align="left" class="tblheader-bold" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.servicetype"/></td>
						<td align="left" class="tblheader-bold" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.description"/></td>
			 		</tr>
			 		<%int index=1; %>
				 	 <logic:iterate id="netServiceType" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData" name="createServerForm" property="lstServiceType">
				   	   <!-- SCR0001 is the code for PCRFService. The PCRFService will not be removed/added from ConfiguredService -->
				   	  <logic:notEqual value="SCR0001" property="netServiceTypeId" name="netServiceType">
				   	  <tr>
				   	  <td align="center" class="tblfirstcol" valign="top" width="8%" >
				   	  <html:multibox name="createServerForm" property="selectedServices" tabindex="2" styleId="selected">  
				   	      <bean:write name="netServiceType" property="netServiceTypeId" />
				   	   </html:multibox>
				   	  </td>
				   	  <td align="left" class="tblrows" valign="top" width="8%" >
				          <%=index++%>.
				      </td>
				      <td align="left" class="tblrows" valign="top"  >
				          <bean:write name="netServiceType" property="name" />
				      </td>
				      <td align="left" class="tblrows" valign="top"  >
				      	<bean:write name="netServiceType" property="description" />
				      </td>

				      </tr>
				   	</logic:notEqual>
				   	</logic:iterate>
				   	
				   	
				 <%}else{%>
				 <tr>
					<td align="center" class="tblheader-bold" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.servicetype"/></td>
    			</tr>
				  <tr>
				 	<td align="center" class="tblfirstcol" valign="top" colspan="2">No Records Found.</td>
				  </tr>
				   <%} %>
			   </table>  
			</td>
		  </tr>	 
		  <tr>
			<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
		  </tr> 
          <tr > 
	        <td class="btns-td" valign="middle" >&nbsp;</td>
            <td class="btns-td" valign="middle"  >
            	<input type="button" name="c_btnPrevious" onclick="goPrevious()" id="c_btnPrevious"  value=" Previous "  class="light-btn" tabindex="6">
				<input type="button" name="c_btnCreate" onclick="validateCreate()" id="c_btnCreate2"  value=" Create "  class="light-btn" tabindex="6">
                <input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/serverGroupManagement.do?method=initSearch'" value="Cancel" class="light-btn" tabindex="7"> 
	        </td>
   		  </tr>
		</table>
	  </td>
	</tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>
</html:form>

