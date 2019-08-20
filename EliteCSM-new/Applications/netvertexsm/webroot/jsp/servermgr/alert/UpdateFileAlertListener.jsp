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


<% 
	String localBasePath = request.getContextPath();
    UpdateAlertListenerForm updateAlertListenerForm = (UpdateAlertListenerForm) request.getAttribute("updateAlertListenerForm");
    List<String> selectedAlertTypeList=updateAlertListenerForm.getSelectedAlertsTypeList();
    List<String> selectedFloodControl=updateAlertListenerForm.getSelectedFloodControl();
	List<AlertTypeData> alertTypeDataList = (List<AlertTypeData>) request.getAttribute("alertTypeDataList");
	AlertListenerData alertListenersData = (AlertListenerData)request.getAttribute("alertListenerData");
%>

<script>

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
			alert('Name must be specified');
     	}else if(!isValidName) {
    		alert('Enter Valid Name');
    		document.forms[0].name.focus();		
    	}else  if(isNull(document.forms[0].fileName.value)){
			document.forms[0].fileName.focus();
			alert('File name must be specified');
	     }else if(document.forms[0].rollingType[1].checked && !isNumber(document.forms[0].rollingUnitSizedBased.value)){
	  		 alert('Rolling Unit must be Numeric.');
	         
	     }else if(document.forms[0].rollingType[1].checked && !isNumber(document.forms[0].maxRollingUnitSizedBased.value)){

	    	 alert('Max Rolled Unit must be Numeric.');
	         
	     }else if(isAlertConfigured()==false){
	         alert('Atleast one alert must be selected');
	    }else{
	         if(document.forms[0].rollingType[0].checked){
	             document.forms[0].rollingUnit.value=document.forms[0].rollingUnitTimeBased.value;
	             document.forms[0].maxRollingUnit.value=document.forms[0].maxRollingUnitTimeBased.value;  
	         
	         }else if(document.forms[0].rollingType[1].checked){
	        	 document.forms[0].rollingUnit.value=document.forms[0].rollingUnitSizedBased.value;
	             document.forms[0].maxRollingUnit.value=document.forms[0].maxRollingUnitSizedBased.value;
	         }
	         document.forms[0].action.value="Update";         
		     flag = true;
	    }
		return flag;
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


</script>
<html:form action="/updateAlertListener" onsubmit="return validateUpdate();" >
	<html:hidden name="updateAlertListenerForm" styleId="action"
		property="action" />
	<html:hidden name="updateAlertListenerForm" styleId="listenerId"
		property="listenerId"/>
    <html:hidden name="updateAlertListenerForm" styleId="typeId"
		property="typeId"/>
    <html:hidden name="updateAlertListenerForm" styleId="rollingUnit"
		property="rollingUnit"/>
	<html:hidden name="updateAlertListenerForm" styleId="maxRollingUnit"
		property="maxRollingUnit"/>	
    
    
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
							<html:text styleId="name" property="name" size="25" onblur="verifyName();"
								maxlength="60" /><font color="#FF0000"> *</font>
								<div id="verifyNameDiv" class="labeltext"></div>
								</td>
						</tr>
		
						<tr>
							<td align="left" class="labeltext" valign="top" width="20%"><bean:message
								bundle="servermgrResources"
								key="servermgr.alert.filename" /></td>
							<td align="left" class="labeltext" valign="top" colspan="2">
							<html:text styleId="fileName"
								property="fileName" size="25" maxlength="100" /><font color="#FF0000"> *</font></td>
						</tr>
						
						<tr>
								<td class="small-gap">&nbsp;</td>
								</tr>
								
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">
											<bean:message bundle="servermgrResources" key="servermgr.alert.rollingtype"/>
									</td>
									<td align="left" class="labeltext" valign="top" >
				    					<html:radio  styleId="rollingType" property="rollingType" onclick="setTimeBased();" value="1"/>Time-Based
							            <html:radio  styleId="rollingType" property="rollingType" onclick="setSizeBased();" value="2"/>Size-Based
										      
									</td>
								</tr>
								
								<tr>
								<td class="small-gap">&nbsp;</td>
								</tr>
								
								
								
								<!-- Size Based selected ,display below portion -->
								<tr id="sizedBasedRollingUnit">
									<td align="left" class="labeltext" valign="top" width="18%"><bean:message bundle="servermgrResources" key="servermgr.alert.rollingunitkbs"/>&nbsp;</td>
									<td align="left" class="labeltext" valign="top" >
				    				    <html:text styleId="rollingUnitSizedBased" property="rollingUnitSizedBased" size="25" maxlength="100" />
										<font color="#FF0000"> *</font>      
									</td>
								</tr>
								<tr>
								<td class="small-gap">&nbsp;</td>
								</tr>
								<tr id="sizedBasedMaxRolledUnit">
									<td align="left" class="labeltext" valign="top" width="18%">
											<bean:message bundle="servermgrResources" key="servermgr.alert.maxrollingunit"/>
									</td>
									<td align="left" class="labeltext" valign="top" >
				    				    <html:text styleId="maxRollingUnitSizedBased" property="maxRollingUnitSizedBased" size="25" maxlength="100" />
										<font color="#FF0000"> *</font>      
									</td>
								</tr>
								<!-- end -->
								<!-- Time Based selected ,display below portion -->
								<tr id="timeBasedRollingUnit">
									<td align="left" class="labeltext" valign="top" width="18%">
											<bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit"/>
									</td>
								
									<td align="left" class="labeltext" valign="top" >
			    				 
				   					    <html:select  styleId="rollingUnitTimeBased" property="rollingUnitTimeBased" value="5" size="1">
										   <html:option value="3" ><bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit.minute"/></html:option>
										   <html:option value="4" ><bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit.hour"/></html:option>
										   <html:option value="5" ><bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit.daily"/></html:option>
					   					</html:select><font color="#FF0000"> *</font>      
							      </td>
								</tr>
								<tr>
								<td class="small-gap">&nbsp;</td>
								</tr>
								<tr id="timeBasedMaxRolledUnit">
									<td align="left" class="labeltext" valign="top" width="18%">
											<bean:message bundle="servermgrResources" key="servermgr.alert.maxrollingunit"/>
									</td>
									<td align="left" class="labeltext" valign="top" >
				    				    <html:text styleId="maxRollingUnitTimeBased" property="maxRollingUnitTimeBased" size="25" maxlength="100" value="0"/>
										<font color="#FF0000"> *</font>      
									</td>
								</tr>
								
								
								<tr>
								<td class="small-gap">&nbsp;</td>
								</tr>
								
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">
											<bean:message bundle="servermgrResources" key="servermgr.alert.comprolledunit"/>
									</td>
									<td align="left" class="labeltext" valign="top" >
				    				    <html:select  styleId="compRollingUnit" property="compRollingUnit" size="1" >
										   <html:option value="true" >True</html:option>
					   					   <html:option value="false" >False</html:option>
										</html:select><font color="#FF0000"> *</font>      
									</td>
								</tr>
						                         
                         <tr>
							<td align="left" class="labeltext" valign="top">&nbsp;</td>
							<td align="left" class="labeltext" valign="top">&nbsp;</td>
						</tr>
						<tr>
						<td class="tblheader-bold" align="left" colspan="3">								
									<bean:message bundle="servermgrResources" key="servermgr.alert.alertslist"/>
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
                                  	for(int i=0,size=alertTypeDataList.size();    i<size;   i++) {
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