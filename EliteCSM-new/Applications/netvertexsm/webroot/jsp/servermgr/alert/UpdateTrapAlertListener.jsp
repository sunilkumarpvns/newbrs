<%@ page import="java.util.*"%>
<%@page import="com.elitecore.netvertexsm.util.constants.ServicePolicyConstants"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.alert.forms.UpdateAlertListenerForm"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTypeData"%>

<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
<%@page import="java.lang.String"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.Writer"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData"%>
<%@page import="com.elitecore.core.serverx.alert.TrapVersion"%>
<%@page import="com.elitecore.core.serverx.alert.SnmpRequestType"%>

<script type="text/javascript">
jQuery(document).ready(function(){
		showHideRequestType(document.getElementById("trapVersion").value);
});
</script>

<% 
	String localBasePath = request.getContextPath();
    UpdateAlertListenerForm updateAlertListenerForm = (UpdateAlertListenerForm) request.getAttribute("updateAlertListenerForm");
    List<String> selectedAlertTypeList=updateAlertListenerForm.getSelectedAlertsTypeList();
    List<String> selectedFloodControl=updateAlertListenerForm.getSelectedFloodControl();
	List<AlertTypeData> alertTypeDataList = (List<AlertTypeData>) request.getAttribute("alertTypeDataList");
	AlertListenerData trapAlertListenerData = (AlertListenerData)request.getAttribute("alertListenerData");
%>

<script>
var isValidName;
function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.ALERT_CONFIGURATION%>',searchName:searchName,mode:'update',id:'<%=updateAlertListenerForm.getListenerId()%>'},'verifyNameDiv');
}

function validateUpdate()
{
	verifyName();
	var flag = false;
	if(isNull(document.forms[0].name.value)){
		document.forms[0].name.focus();
		alert('Alert Listener Name must be specified');
	}else if(!isValidName) {
		alert('Enter Valid Name');
		document.forms[0].name.focus();		
	}else if(isNull(document.forms[0].trapServer.value)){
		document.forms[0].trapServer.focus();
		alert('Trap Server must be specified');
	}else if(isNull(document.forms[0].trapVersion.value)){
		document.forms[0].trapVersion.focus();
		alert('Trap Version must be specified');
	}else if(document.forms[0].trapVersion.value==1 &&  document.forms[0].snmpRequestType.value==2 && isNull(document.forms[0].timeout.value)){
		document.forms[0].timeout.focus();
		alert('Timeout must be specified');
	}else if(document.forms[0].snmpRequestType.value==2 && !isNumber(document.forms[0].timeout.value)){		
		document.forms[0].timeout.focus();
		alert('Invalid Timeout');				
	}else if(document.forms[0].trapVersion.value==1 && document.forms[0].snmpRequestType.value==2 && isNull(document.forms[0].retryCount.value)){
		document.forms[0].retryCount.focus();
		alert('Retry Count must be specified');
	}else if(document.forms[0].snmpRequestType.value==2 && !isNumber(document.forms[0].retryCount.value)){		
		document.forms[0].retryCount.focus();
		alert('Invalid Retry Count');		
	}else if(isNull(document.forms[0].community.value)){
		document.forms[0].community.focus();
		alert('Community must be specified');		
     }else if(isAlertConfigured()==false){
         alert('Atleast one alert must be selected');
    }else{	
    	document.forms[0].retryCount.value = jQuery.trim(document.forms[0].retryCount.value);
    	document.forms[0].timeout.value = jQuery.trim(document.forms[0].timeout.value);    	 
     	document.forms[0].action.value="Update";
		flag = true;
     }		  
	 return flag;
}

function showHideRequestType(val){
 	if(parseInt(val)==0){		
		//$("#snmpRequestTypeRow").hide();
		document.getElementById("snmpRequestType").value=1;
		$('#snmpRequestType').prop('disabled','disabled');
		//document.getElementById("snmpRequestType").diabled=true;
		$("#timeoutRow").hide();
		$("#retryCountRow").hide();		 			 				
	}else{
		$("#snmpRequestTypeRow").show();		
		$('#snmpRequestType').removeAttr('disabled');
		
		//document.getElementById("snmpRequestType").value="1";
		showTimeOutAndRetryCount(document.getElementById("snmpRequestType").value);
	}
}

function isNumber(val){
	nre= /^\d+$/;
	val = jQuery.trim(val);
 
	var regexp = new RegExp(nre);
	if(!regexp.test(val))
	{
		return false;
	}
	return true;
}

function showTimeOutAndRetryCount(val){
	if(parseInt(val)==1){		
		$("#timeoutRow").hide();
		$("#retryCountRow").hide();		 			 		
	}else{		
		$("#timeoutRow").show();
		$("#retryCountRow").show();		 			 				
	}
}

</script>
<html:form action="/updateAlertListener" onsubmit="return validateUpdate();"  >
	<html:hidden name="updateAlertListenerForm" styleId="action"
		property="action" />
	<html:hidden name="updateAlertListenerForm" styleId="listenerId"
		property="listenerId"/>
	<html:hidden name="updateAlertListenerForm" styleId="typeId"
		property="typeId"/>
    	

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
			<table cellpadding="0" cellspacing="0" border="0" width="97%"
				height="15%">
				<tr>
					<td class="small-gap" width="7" colspan="3">&nbsp;</td>
				</tr>
				<tr>
					<td class="box" width="100%" colspan="2" valign="top">
					<table width="100%" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td class="tblheader-bold" colspan="3"><bean:message
								bundle="servermgrResources"
								key="servermgr.alert.updatealertlistener" /></td>
						</tr>
						<tr>
							<td class="small-gap" colspan="3">&nbsp;</td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="20%"><bean:message
								bundle="servermgrResources"
								key="servermgr.alert.name" /></td>
							<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="name" property="name" size="25" maxlength="60" onblur="verifyName();"  /><font color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div>
							</td>
						</tr>

						<tr>
							<td align="left" class="labeltext" valign="top" width="20%"><bean:message
								bundle="servermgrResources"
								key="servermgr.alert.trapserver" /></td>
							<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="trapServer"
								property="trapServer" size="25" maxlength="100" /><font color="#FF0000"> *</font></td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="20%"><bean:message
								bundle="servermgrResources"
								key="servermgr.alert.trapversion" /></td>
								 
								<td align="left" class="labeltext" valign="top" colspan="2">
								<html:select  styleId="trapVersion" property="trapVersion" size="1" onchange="showHideRequestType(this.value)" style="width:65px" >
								   <%for(TrapVersion trapVersion:TrapVersion.values()){%>	
								   		<html:option value="<%=trapVersion.getVal().toString()%>" ><%=trapVersion.getName()%></html:option>
								   <%}%>
						   					</html:select><font color="#FF0000"> *</font>
						   	     </td>
						</tr>
						<tr id="snmpRequestTypeRow" >
							<td align="left" class="labeltext" valign="top" width="18%">
								<bean:message bundle="servermgrResources" key="trap.snmp.request.type" />
							</td>
							<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
								<html:select  styleId="snmpRequestType" property="snmpRequestType" size="1" onchange="showTimeOutAndRetryCount(this.value)" style="width:65px"  >
									<%for(SnmpRequestType snmpRequestType:SnmpRequestType.values()){%>
								   	<html:option value="<%=String.valueOf(snmpRequestType.getId())%>" ><%=snmpRequestType.getType()%></html:option>
								   <%}%>
					   			</html:select><font color="#FF0000"> *</font>
							</td>
						</tr>
					   	        
	 					<tr id="timeoutRow" style="display:none;" >
							<td align="left" class="labeltext" valign="top" width="18%">
								<bean:message bundle="servermgrResources" key="trap.timeout" />
							</td>
							<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
								<html:text styleId="timeout" property="timeout" size="10" maxlength="8" />
								<font color="#FF0000"> *</font>
							</td>
						</tr> 
					   	        
						<tr id="retryCountRow" style="display:none;" >
							<td align="left" class="labeltext" valign="top" width="18%">
								<bean:message bundle="servermgrResources" key="trap.retrycount" />
							</td>
							<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
								<html:text styleId="retryCount" property="retryCount" size="10" maxlength="2" />
								<font color="#FF0000"> *</font>
							</td>
						</tr> 														
						<tr>
							<td align="left" class="labeltext" valign="top" width="20%"><bean:message
								bundle="servermgrResources"
								key="servermgr.alert.community" /></td>
							<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="community" property="community"
								size="25" maxlength="50" /><font color="#FF0000"> *</font></td>
						</tr>
						
						<tr>
								<td align="left" class="labeltext" valign="top" width="18%">									
									<bean:message bundle="servermgrResources" key="trap.advanced.trap" />									
								</td>
								<td align="left" class="labeltext" valign="top" width="70%">								
								<html:select property="advanceTrap" styleId="advanceTrap" style="width:65px"  >								
								<html:option value="true">True</html:option>
								<html:option value="false">False</html:option>								
								</html:select>	
								</td>							
							</tr>
						                         
                         <tr>
							<td align="left" class="labeltext" valign="top">&nbsp;</td>
							<td align="left" class="labeltext" valign="top">&nbsp;</td>
						</tr>
						
							<tr>
								<td class="tblheader-bold" align="left" colspan="7">								
									<bean:message bundle="servermgrResources" key="trap.alerts.list" />									
								</td>
							</tr>
						<tr>
						  <td align="left" colspan="3" class="labeltext">
						     <table width="100%" cellspacing="0" cellpadding="0" border="0" id="alerttable">
						     <thead>
								<tr>
									<td align="center" class="tblheader-bold" valign="top"><bean:message bundle="servermgrResources" key="servermgr.alert.alertname"/></td>
									<td align="center" class="tblheader-bold" valign="top" width="*"><bean:message bundle="servermgrResources" key="servermgr.alert.enable"/></td>
									<td align="center" class="tblheader-bold" valign="top" width="*"><bean:message bundle="servermgrResources" key="servermgr.alert.floodcontrol"/></td>
								</tr>
						     </thead>
						     <tbody>
                                  <% 
                                  	for(int i=0, size=alertTypeDataList.size(); i<size; i++) {
                                  		AlertTypeData data = alertTypeDataList.get(i);
                                  		if(data.getType().equalsIgnoreCase("P")){
                                  		  if(selectedAlertTypeList.contains(data.getAlertTypeId())){
                       				 	        out.write("<tr data-tt-id="+data.getAlertTypeId()+" data-tt-parent-id="+data.getParentId()+" class='dataRow'>"
                       					                   +"<td align='left' class='tblfirstcol'><b>"+data.getName()+"</b></td>"
                       					                   +"<td align='center' class='tblrows'><input type='checkbox' name='alertEnable' value="+data.getAlertTypeId()+" checked></td>");
                       					    if(selectedFloodControl.contains(data.getAlertTypeId())){
                       						    out.write("<td align='center' class='tblrows'><input type='checkbox' name='floodcontrolenable' value="+data.getAlertTypeId()+" checked></td></tr>"); 
                       					     }else{
                       					    	 out.write("<td align='center' class='tblrows'><input type='checkbox' name='floodcontrolenable' value="+data.getAlertTypeId()+"></td></tr>");	 
                       					     } 
                       					  
                       				    }else{
                       				        out.write("<tr data-tt-id="+data.getAlertTypeId()+" data-tt-parent-id="+data.getParentId()+" class='dataRow'>"
                       				                  +"<td align='left' class='tblfirstcol'><b>"+data.getName()+"</b></td>"
                       				                  +"<td align='center' class='tblrows'><input type='checkbox' name='alertEnable' value="+data.getAlertTypeId()+"></td>"
                       				                  +"<td align='center' class='tblrows'><input type='checkbox' name='floodcontrolenable' value="+data.getAlertTypeId()+"></td></tr>"); 
                       				 }	
                                   }
                                  		drawTree(out,data,selectedAlertTypeList,selectedFloodControl);
                                 }
                                %>
                             </tbody>  
                             </table>
						     </td>
						  </tr>
						
						<tr>
							<td align="center" class="labeltext" valign="top" colspan="2">							
							<html:submit styleClass="light-btn" value=" Update "  styleId="update"  />
							<input type="button" name="c_btnCancel" onclick="javascript:location.href='<%=basePath%>/initSearchAlertListener.do?/>'" value="   Cancel   "	class="light-btn" /></td>
							
						</tr>
						 <tr>
							<td align="left" class="labeltext" valign="top">&nbsp;</td>
							<td align="left" class="labeltext" valign="top">&nbsp;</td>
						</tr>
                         
					</table>
					</td>
				</tr>
				
			</table>
			</td>
		</tr>
	</table>
</html:form>

<script>
initialized();
</script>