<%@page import="java.util.*"%>
<%@page import="com.elitecore.elitesm.util.constants.ServicePolicyConstants"%>
<%@page import="com.elitecore.elitesm.web.servermgr.alert.forms.UpdateAlertListenerForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTypeData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
<%@page import="java.lang.String"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.Writer"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData"%>

<script type="text/javascript">
jQuery(document).ready(function(){
		$('ul#example').collapsibleCheckboxTree('expanded');
		$("#expand").addClass("light-btn");
		$("#expand").after("&nbsp;");
		$("#collapse").addClass("light-btn");
});
</script>

<% 
	String localBasePath = request.getContextPath();
    UpdateAlertListenerForm updateAlertListenerForm = (UpdateAlertListenerForm) request.getAttribute("updateAlertListenerForm");
    String[] selectedAlertsType = updateAlertListenerForm.getSelectedAlertsType();
	List<AlertTypeData> alertTypeDataList = (List<AlertTypeData>) request.getAttribute("alertTypeDataList");
	AlertListenerData sysAlertListenerData = (AlertListenerData)request.getAttribute("alertListenerData");
%>



<script>
var isValidName;

function validateUpdate()
{
	if(isNull(document.forms[0].name.value)){
		document.forms[0].name.focus();
		alert('Alert Listener Name must be specified');
	}else if(!isValidName) {
		alert('Enter Valid Name');
		document.forms[0].name.focus();
		return;
	}else if(document.forms[0].facility.value == 0){
		document.forms[0].facility.focus();
		alert('Facility must be specified');		
     }else{	
		 var verifyAddress=false;
    	 verifyAddress=validateIP(document.forms[0].address.value);
    	 if(verifyAddress==true){
		  document.forms[0].action.value="Update";
		  document.forms[0].submit();
    	 }else{
    		 alert("Enter Address is not Valid");
    		 document.forms[0].address.focus();
     }		  
}
}
function validateIP(ipaddress){
	var ip=/((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))|(^\s*((?=.{1,255}$)(?=.*[A-Za-z].*)[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?(?:\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?)*)\s*$)/;
		if(ip.test(ipaddress)){
			   return true;
		   }else{
			   return false;
		   }
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.ALERT_LISTENER%>',searchName,'update','<%=sysAlertListenerData.getListenerId()%>','verifyNameDiv');
}
</script>
<html:form action="/updateAlertListener">
	<html:hidden name="updateAlertListenerForm" styleId="action"
		property="action" />
	<html:hidden name="updateAlertListenerForm" styleId="listenerId"
		property="listenerId" />
	<html:hidden name="updateAlertListenerForm" styleId="typeId"
		property="typeId" />
	<html:hidden name="updateAlertListenerForm" styleId="auditUId"
		property="auditUId" />


	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="100%"
					height="15%">
					<tr>
						<td class="small-gap" width="7" colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td class="tblheader-bold" colspan="3"><bean:message
								bundle="servermgrResources"
								key="servermgr.alert.updatealertlistener" /></td>
					</tr>
					<tr>
						<td class="small-gap" colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servermgrResources" 
								key="servermgr.alert.listenerName" />
									<ec:elitehelp headerBundle="servermgrResources" 
										text="servermgr.alert.listenerName" 
											header="servermgr.alert.listenerName" />
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="name" onkeyup="verifyName();" property="name"
								size="25" maxlength="60" style="width:200px" tabindex="1" /><font
							color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servermgrResources" key="servermgr.alert.address" /> 
							<ec:elitehelp headerBundle="servermgrResources" 
							text="syslogalertlistener.address" header="servermgr.alert.address"/>
						</td>

						<td align="left" class="labeltext" valign="top" colspan="2"
							width="82%"><html:text styleId="address" tabindex="2"
								property="address" size="25" maxlength="100" style="width:200px" />
							<font color="#FF0000"> *</font></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servermgrResources" key="servermgr.alert.facility" /> 
							<ec:elitehelp headerBundle="servermgrResources" 
							text="syslogalertlistener.facility" header="servermgr.alert.facility"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="75%">
							<html:select name="updateAlertListenerForm" tabindex="3"
								property="facility" styleId="facility" style="width:130px">
								<html:option value="0">Select</html:option>
								<html:optionsCollection name="updateAlertListenerForm"
									property="sysLogNameValuePoolDataList" label="name"
									value="value" />
							</html:select><font color="#FF0000"> *</font>
						</td>
					</tr>


					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servermgrResources" key="servermgr.alert.repeatedmessagereduction" /> 
							<ec:elitehelp headerBundle="servermgrResources" 
							text="syslogalertlistener.repeatedmessagereduction" header="servermgr.alert.repeatedmessagereduction"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:select styleId="repeatedMessageReduction" tabindex="4" property="repeatedMessageReduction" size="1" style="width:130px">
								<html:option value="true">True</html:option>
								<html:option value="false">False</html:option>
							</html:select>
						</td>
					</tr>
					
					<tr>
						<td align="left" class="labeltext" valign="top">&nbsp;</td>
						<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
					</tr>

					<tr>
						<td class="tblheader-bold" align="left" colspan="7">
							<bean:message bundle="servermgrResources" key="servermgr.alert.alertlist" />
							<ec:elitehelp headerBundle="servermgrResources" 
							text="syslogalertlistener.list" header="servermgr.alert.alertlist"/>
						</td>
					</tr>

					<tr>
						<td align="left" colspan="3" class="labeltext"><ul
								id="example">
								<% 
                                  	for(int i=0;i<alertTypeDataList.size();i++) {
                                  		AlertTypeData data = alertTypeDataList.get(i);
                                  		if(data.getType().equalsIgnoreCase("P")){
                                  			out.println("<li><input class=labeltext  type="+'"'+"checkbox" + '"'+ "name=" + '"' + data.getName() + "value=" + '"' + data.getAlertTypeId() + '"' + '/' + '>'+"<b>"+data.getName()+"</b>"+"<ul>");                                  		
                                  		}
                                  		drawTree(out,data,selectedAlertsType);
                                  		if(data.getType().equalsIgnoreCase("P")){
                                  		out.println("</ul></li>");
                                  		}                                  	
                                  	}
                                  %>
							</ul></td>
					</tr>

					<tr>
						<td align="left" class="labeltext" valign="top" colspan="2" style="padding-left: 300px;">
							<input type="button" name="c_btnNext" tabindex="5"
							onclick="validateUpdate()" value=" Update " class="light-btn" />
							<input type="button" name="c_btnCancel" tabindex="6"
							onclick="javascript:location.href='<%=basePath%>/initSearchAlertListener.do?/>'"
							value="   Cancel   " class="light-btn" />
						</td>

					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top">&nbsp;</td>
						<td align="left" class="labeltext" valign="top">&nbsp;</td>
					</tr>

				</table>
			</td>
		</tr>
	</table>
</html:form>
