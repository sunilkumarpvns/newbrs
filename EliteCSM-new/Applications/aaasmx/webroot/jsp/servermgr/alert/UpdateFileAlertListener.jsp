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
	AlertListenerData alertListenersData = (AlertListenerData)request.getAttribute("alertListenerData");
%>

<script>
var isValidName;

function validateUpdate()
{
	if(!isValidName) {
		alert('Enter Valid Name');
		document.forms[0].name.focus();
		return;
	}else if(isNull(document.forms[0].fileName.value)){
			document.forms[0].fileName.focus();
			alert('File name must be specified');
	     }else if(document.forms[0].rollingType[1].checked && !isNumber(document.forms[0].rollingUnitSizedBased.value)){
	  		 alert('Rolling Unit must be Numeric.');
	         
	     }else if(document.forms[0].rollingType[1].checked && !isNumber(document.forms[0].maxRollingUnitSizedBased.value)){

	    	 alert('Max Rolled Unit must be Numeric.');
	         
	     }else{

	         if(document.forms[0].rollingType[0].checked){

	             document.forms[0].rollingUnit.value=document.forms[0].rollingUnitTimeBased.value;
	             document.forms[0].maxRollingUnit.value=document.forms[0].maxRollingUnitTimeBased.value;  
	         
	         }else if(document.forms[0].rollingType[1].checked){
	             
	        	 document.forms[0].rollingUnit.value=document.forms[0].rollingUnitSizedBased.value;
	             document.forms[0].maxRollingUnit.value=document.forms[0].maxRollingUnitSizedBased.value;

	         }
	         document.forms[0].action.value="Update";         
		     document.forms[0].submit();
	    }
}
function initialized(){

	
	if(document.forms[0].rollingType[0].checked){

        
        document.forms[0].rollingUnitTimeBased.value=document.forms[0].rollingUnit.value;
        document.forms[0].maxRollingUnitTimeBased.value=document.forms[0].maxRollingUnit.value;
        setTimeBased();
        
    }else if(document.forms[0].rollingType[1].checked){

    	
   	    document.forms[0].rollingUnitSizedBased.value=document.forms[0].rollingUnit.value;
        document.forms[0].maxRollingUnitSizedBased.value=document.forms[0].maxRollingUnit.value;
        setSizeBased();
        

    }  		
	
}

function setTimeBased(){

	document.getElementById('sizedBasedRollingUnit').style.display='none';
	document.getElementById('sizedBasedMaxRolledUnit').style.display='none';
	document.getElementById('timeBasedRollingUnit').style.display='';
	document.getElementById('timeBasedMaxRolledUnit').style.display='none';
	
}

function setSizeBased(){

	document.getElementById('sizedBasedRollingUnit').style.display='';
	document.getElementById('sizedBasedMaxRolledUnit').style.display='';
	document.getElementById('timeBasedRollingUnit').style.display='none';
	document.getElementById('timeBasedMaxRolledUnit').style.display='none';
	
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.ALERT_LISTENER%>',searchName,'update','<%=alertListenersData.getListenerId()%>','verifyNameDiv');
}
</script>
<html:form action="/updateAlertListener">
	<html:hidden name="updateAlertListenerForm" styleId="action" property="action" />
	<html:hidden name="updateAlertListenerForm" styleId="listenerId" property="listenerId" />
	<html:hidden name="updateAlertListenerForm" styleId="typeId" property="typeId" />
	<html:hidden name="updateAlertListenerForm" styleId="rollingUnit" property="rollingUnit" />
	<html:hidden name="updateAlertListenerForm" styleId="maxRollingUnit" property="maxRollingUnit" />
	<html:hidden name="updateAlertListenerForm" styleId="auditUId" property="auditUId" />


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
											header="servermgr.alert.listenerName"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="name" property="name" onkeyup="verifyName();"
								size="25" tabindex="1" maxlength="60" style="width:250px" /><font
							color="#FF0000"> *</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
					</tr>
					<!-- 	
						<tr>
								<td align="left" class="labeltext" valign="top" width="10%">
								<bean:message bundle="servermgrResources" key="servermgr.alert.listenerType"/></td>
								<td align="left" class="labeltext" valign="top" width="32%">														
									<html:select property="typeId">
									<html:option value="0">Select</html:option>
									<html:optionsCollection name="updateAlertListenerForm" property="availableListenerTypes" label="typeName" value="typeId"/>
									</html:select><font color="#FF0000"> *</font>
								</td>							
					 	</tr>
				
				 -->
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servermgrResources" 
								key="servermgr.alert.filename" />
									<ec:elitehelp headerBundle="servermgrResources" 
										text="filealertlistener.filename" 
											header="servermgr.alert.filename"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="fileName" tabindex="2" property="fileName"
								size="25" maxlength="100" style="width:250px" /><font
							color="#FF0000"> *</font>
						</td>
					</tr>

					<tr>
						<td class="small-gap">&nbsp;</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servermgrResources" key="servermgr.alert.rollingtype" />
							<ec:elitehelp headerBundle="servermgrResources" 
							text="filealertlistener.rolling" header="servermgr.alert.rollingtype"/>
						</td>
						<td align="left" class="labeltext" valign="top"><html:radio
								tabindex="3" styleId="rollingType" property="rollingType"
								onclick="setTimeBased();" value="1" />Time-Based <html:radio
								tabindex="4" styleId="rollingType" property="rollingType"
								onclick="setSizeBased();" value="2" />Size-Based</td>
					</tr>

					<tr>
						<td class="small-gap">&nbsp;</td>
					</tr>



					<!-- Size Based selected ,display below portion -->
					<tr id="sizedBasedRollingUnit">
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servermgrResources" key="servermgr.alert.rollingunitkbs" />
							<ec:elitehelp headerBundle="servermgrResources" 
							text="filealertlistener.rollingunit" header="servermgr.alert.rollingunitkbs"/>
						</td>
						<td align="left" class="labeltext" valign="top"><html:text
								tabindex="5" styleId="rollingUnitSizedBased"
								property="rollingUnitSizedBased" size="25" maxlength="100"
								style="width:250px" /> <font color="#FF0000"> *</font></td>
					</tr>
					<tr>
						<td class="small-gap">&nbsp;</td>
					</tr>
					<tr id="sizedBasedMaxRolledUnit">
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servermgrResources" key="servermgr.alert.maxrollingunit" />
							<ec:elitehelp headerBundle="servermgrResources" 
							text="filealertlistener.maxrollunit" header="servermgr.alert.maxrollingunit"/>
						</td>
						<td align="left" class="labeltext" valign="top"><html:text
								styleId="maxRollingUnitSizedBased" tabindex="6"
								property="maxRollingUnitSizedBased" size="25" maxlength="100"
								style="width:250px" /> <font color="#FF0000"> *</font></td>
					</tr>
					<!-- end -->
					<!-- Time Based selected ,display below portion -->
					<tr id="timeBasedRollingUnit">
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit"/>
							<ec:elitehelp headerBundle="servermgrResources" 
							text="filealertlistener.rollingunit" header="servermgr.alert.rollingunit"/>						
						</td>

						<td align="left" class="labeltext" valign="top"><html:select
								styleId="rollingUnitTimeBased" tabindex="7"
								property="rollingUnitTimeBased" value="5" size="1"
								style="width:130px">
								<html:option value="3">Minute</html:option>
								<html:option value="4">Hour</html:option>
								<html:option value="5">Daily</html:option>
							</html:select><font color="#FF0000"> *</font></td>
					</tr>
					<tr>
						<td class="small-gap">&nbsp;</td>
					</tr>
					<tr id="timeBasedMaxRolledUnit">
						<td align="left" class="captiontext" valign="top" width="30%">
							Max Rolled Unit <img src="<%=basePath%>/images/help-hover.jpg"
							height="12" width="12" style="cursor: pointer"
							onclick="parameterDescription('<bean:message bundle="descriptionResources" key="filealertlistener.maxrollunit"/>','Max Rolled Unit')" />

						</td>
						<td align="left" class="labeltext" valign="top"><html:text
								styleId="maxRollingUnitTimeBased" tabindex="8"
								property="maxRollingUnitTimeBased" size="25" maxlength="100"
								value="0" style="width:250px" /> <font color="#FF0000">
								*</font></td>
					</tr>


					<tr>
						<td class="small-gap">&nbsp;</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servermgrResources" key="servermgr.alert.comprolledunit"/>
							<ec:elitehelp headerBundle="servermgrResources" 
							text="filealertlistener.compressrollunit" header="servermgr.alert.comprolledunit"/>
						</td>
						<td align="left" class="labeltext" valign="top"><html:select
								styleId="compRollingUnit" tabindex="9"
								property="compRollingUnit" size="1" value="false"
								style="width:130px">
								<html:option value="true">True</html:option>
								<html:option value="false">False</html:option>
							</html:select><font color="#FF0000"> *</font></td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="30%">
							<bean:message bundle="servermgrResources" key="servermgr.alert.repeatedmessagereduction" /> 
							<ec:elitehelp headerBundle="servermgrResources" 
							text="filealertlistener.repeatedmessagereduction" header="servermgr.alert.repeatedmessagereduction"/>
						</td>
						<td align="left" class="labeltext" valign="top">
							<html:select styleId="repeatedMessageReduction" tabindex="10" property="repeatedMessageReduction" size="1" style="width:130px">
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
							<bean:message bundle="servermgrResources" key="servermgr.alert.alertlist" />
							<ec:elitehelp headerBundle="servermgrResources" 
							text="filealertlistener.alertlist" header="servermgr.alert.alertlist"/>
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
						<td align="left" class="captiontext" valign="top" colspan="2"
							style="padding-left: 300px;"><input type="button"
							name="c_btnNext" tabindex="11" onclick="validateUpdate()"
							value=" Update " class="light-btn" /> <input type="button"
							name="c_btnCancel" tabindex="12"
							onclick="javascript:location.href='<%=basePath%>/initSearchAlertListener.do?/>'"
							value="   Cancel   " class="light-btn" /></td>
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
<script>
initialized();
</script>