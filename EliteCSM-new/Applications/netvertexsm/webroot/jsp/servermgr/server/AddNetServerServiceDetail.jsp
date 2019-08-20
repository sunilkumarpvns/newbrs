
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.web.servermgr.service.form.AddNetServerServiceInstanceForm" %>


<% 
	String localBasePath1 = request.getContextPath();
%>

<%
	//List lstNetServiceType = (List)request.getAttribute("lstNetServiceType");

	AddNetServerServiceInstanceForm addNetServerServiceInstanceForm = (AddNetServerServiceInstanceForm)request.getAttribute("addNetServerServiceInstanceForm");
	
	List lstServiceType = addNetServerServiceInstanceForm.getLstServiceType();
	
	List netServiceInstanceList = (List)request.getAttribute("netServiceInstanceList");
%>

<script >
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
function deleteSubmit(index){
	document.forms[0].action.value = 'remove';
	document.forms[0].itemIndex.value = index;
	document.forms[0].submit();
}

function customValidate(){
	var arrayCheck = document.getElementsByName('selectedServices');
	if(arrayCheck.length>0){
		var selected =false;
		for (i = 0; i < arrayCheck.length;i++){
			if(arrayCheck[i].checked == true){
				selected =true;
			} 
		}
		if(selected==false){
			alert('Select atleast one service');
			return;
		}
	}else{
		alert('No service type available to add.');
		return;
	}
	document.forms[0].action.value = 'update';
	document.forms[0].submit();
}
</script>

<html:form action="/addNetServerServiceInstance">
<html:hidden name="addNetServerServiceInstanceForm" styleId="action" property="action" />
<html:hidden name="addNetServerServiceInstanceForm" styleId="itemIndex" property="itemIndex"/>
<html:hidden name="addNetServerServiceInstanceForm" styleId="netFormServerId" property="netFormServerId"/>
<html:hidden name="addNetServerServiceInstanceForm" styleId="netFormServerTypeId" property="netFormServerTypeId"/>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" >
          <tr>
		  	<td>&nbsp;</td>
		  </tr>

		<tr> 
			<td class="tblheader-bold" colspan="4"><bean:message bundle="servermgrResources" key="servermgr.addedserviceinstances"/></td>
		</tr>    
		<tr>
			<td colspan="4">
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
				   	<tr>			   			
						<td align="left" class="tblheader-bold" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.serialnumber"/></td>
						<td align="left" class="tblheader-bold" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.servicetype"/></td>
						<td align="left" class="tblheader-bold" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.description"/></td>
			 		</tr>
				
			    	<% if(netServiceInstanceList!=null && !netServiceInstanceList.isEmpty()){%>
			 		<%int index=1; %>
				 	 <logic:iterate id="addedNetServiceType" type="com.elitecore.netvertexsm.datamanager.servermgr.data.NetServiceInstanceData" name="addNetServerServiceInstanceForm" property="netServiceInstanceList">
				   	  <tr>				   	  
				   	  <td align="left" class="tblfirstcol" valign="top" width="8%" >
				          <%=index++%>.
				      </td>
				      <td align="left" class="tblrows" valign="top"  width="25%">
				          <bean:write name="addedNetServiceType" property="name" />
				      </td>
				      <td align="left" class="tblrows" valign="top" colspan="2">
				      	<bean:write name="addedNetServiceType" property="description" />
				      </td>
				      </tr>
				   	</logic:iterate>
					 <%}else{%>
					  <tr>
					 	<td align="center" class="tblfirstcol" valign="top" colspan="3" ><bean:message bundle="servermgrResources" key="servermgr.norecordsfound" /></td>
					  </tr>
				   <%} %>
				
				</table>	
			</td>
		</tr>
			<tr> 
		      <td>&nbsp;</td>
		    </tr>
		    
		   <tr> 
			<td class="tblheader-bold" colspan="4"><bean:message bundle="servermgrResources" key="servermgr.addserviceinstances"/></td>
		  </tr>
		  		 <% if(lstServiceType!=null && !lstServiceType.isEmpty()){%>
				   	<tr>
			   			<td align="center" class="tblheader" valign="top" width="8%">
			   				<input type="checkbox" name="toggleAll" id="toggleAll" value="checkbox" onclick="checkAll()"/>
						</td>
						<td align="left" class="tblheader-bold" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.serialnumber"/></td>
						<td align="left" class="tblheader-bold" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.servicetype"/></td>
						<td align="left" class="tblheader-bold" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.description"/></td>
			 		</tr>
			 		<%int index=1; %>
				 	 <logic:iterate id="netServiceType" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData" name="addNetServerServiceInstanceForm" property="lstServiceType">
					<!-- SCR0001 is the code for PCRFService. The PCRFService will not be removed/added from ConfiguredService -->
					<logic:notEqual value="SCR0001" property="netServiceTypeId" name="netServiceType">				   	  
				   	  <tr>
				   	  <td align="center" class="tblfirstcol" valign="top" width="8%" >
				   	  <html:multibox name="addNetServerServiceInstanceForm" property="selectedServices" styleId="selected">  
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
				 	<td align="center" class="tblfirstcol" valign="top" ><bean:message bundle="servermgrResources" key="servermgr.norecordsfound" /></td>
				  </tr>
				   <%} %>
				   	
		<tr> 
	      <td>&nbsp;</td>
	    </tr>
    
    	<tr>
		    <td align="left" class="labeltext" valign="middle" width="5%" colspan="4">
				 <input type="button" name="Add" onclick="customValidate()"  value="  Add  " class="light-btn" tabindex="4" />
				 <input type="reset" name="c_btnDeletePolicy"  onclick="javascript:location.href='<%=localBasePath1%>/viewNetServerInstance.do?netserverid=<bean:write name="addNetServerServiceInstanceForm" property="netFormServerId"/>'" value="Cancel" class="light-btn" tabindex="7">
			</td>
		</tr>
    
     
    	<tr> 
	      <td>&nbsp;</td>
	    </tr>
    

        </table>
	  </td>
    </tr>
    <%--tr>
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
                  <td align="left" class="tblheader" valign="top" width="25%" ><bean:message bundle="servermgrResources" key="servermgr.name"/></td>
                  <td align="left" class="tblheader" valign="top" width="30%" ><bean:message bundle="servermgrResources" key="servermgr.description"/></td>
                  <td align="left" class="tblheader" valign="top" width="20%" ><bean:message bundle="servermgrResources" key="servermgr.servicetype"/></td>
                  <td align="center" class="tblheader" valign="top" width="4%"><bean:message bundle="servermgrResources" key="servermgr.remove"/></td>
                </tr>
                <%
                	int index = 0;
					String strNetServerId;
					String strNetServiceTypeId;
					String strDescription;
					String strName;					
					String strAlias;					
                	String strCommonStatusId;
                %>
              <logic:iterate id="netServiceInstaceData" name="addNetServerServiceInstanceForm" property="listInstanceServices" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceInstanceData">
              <%
					strNetServerId = "netServerId["+index+"]";
					strNetServiceTypeId = "netServiceTypeId["+index+"]";
					strDescription = "description["+index+"]";
					strName = "name["+index+"]";
					strAlias = "alias["+index+"]";
					strCommonStatusId = "commonStatusId["+index+"]";
              %>
              	<html:hidden name="addNetServerServiceInstanceForm" styleId="<%=strNetServerId%>" property="<%=strNetServerId%>"/>
				<html:hidden name="addNetServerServiceInstanceForm" styleId="<%=strNetServiceTypeId%>" property="<%=strNetServiceTypeId%>"/>
				<html:hidden name="addNetServerServiceInstanceForm" styleId="<%=strDescription%>" property="<%=strDescription%>"/>
				<html:hidden name="addNetServerServiceInstanceForm" styleId="<%=strName%>" property="<%=strName%>"/>
				<html:hidden name="addNetServerServiceInstanceForm" styleId="<%=strAlias%>" property="<%=strAlias%>"/>
				<html:hidden name="addNetServerServiceInstanceForm" styleId="<%=strCommonStatusId%>" property="<%=strCommonStatusId%>"/>
              
                <tr>
 				  <td align="left" class="tblfirstcol"><%=(index+1)%></td>
 				  <td align="left" class="tblrows"><bean:write name="netServiceInstaceData" property="name"/></td>
 				  <td align="left" class="tblrows"><bean:write name="netServiceInstaceData" property="description"/> &nbsp;</td>   
 				  <td align="left" class="tblrows">
				  	<logic:iterate id="netServiceTypeData" name="lstNetServiceType" type="com.elitecore.netvertexsm.datamanager.servermgr.data.INetServiceTypeData"> 				  
				  		<logic:equal name="netServiceTypeData" property="netServiceTypeId" value="<%=netServiceInstaceData.getNetServiceTypeId()%>">
							<bean:write name="netServiceTypeData" property="name"/>
				  		</logic:equal>
 				  	</logic:iterate>
				  </td>
 				  <td align="center" class="tblrows">
					 <img src="<%=localBasePath%>/images/minus.jpg" onclick="deleteSubmit('<%=index%>')" border="0"/>
 				  </td>
                </tr>
                <% index++; %>
			 </logic:iterate>
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
         <input type="button" name="c_btnUpdate"  onclick="customValidate()"  id="c_btnUPdate"  value="Update"  class="light-btn" tabindex="6">                   
         
      </td>
	</tr--%>
	<tr> 
      <td>&nbsp;</td>
    </tr>
</table>
</html:form>

<%@ include file="/jsp/core/includes/common/Footer.jsp" %>	
