<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.servermgr.eap.forms.UpdateEAPBasicDetailsForm"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.eap.data.EAPConfigData"%>
<%
	UpdateEAPBasicDetailsForm updateEAPBasicDetailsConfigForm = (UpdateEAPBasicDetailsForm)request.getAttribute("updateEAPBasicDetailsForm");    
	EAPConfigData eapConfigInstanceData = (EAPConfigData)request.getAttribute("eapConfigData");
%>

<script>
var isValidName;

function validateUpdate()
{
 	    
  if(isNull(document.forms[0].name.value)){
		document.forms[0].name.focus();
		alert('Name must be specified');
  }else if(!isValidName) {
		alert('Enter Valid Name');
		document.forms[0].name.focus();
		return;
  }else if(isNull(document.forms[0].sessionCleanupInterval.value)){
	  document.forms[0].sessionCleanupInterval.focus();
	  alert('Session CleanUp Interval must be specified');
			
  }else if(!isNumber(document.forms[0].sessionCleanupInterval.value)){
	  document.forms[0].sessionCleanupInterval.focus();
	  alert('Session CleanUp Interval must be Numeric.');
			
  }else if(isNull(document.forms[0].sessionDurationForCleanup.value)){
	  document.forms[0].sessionDurationForCleanup.focus();
	  alert('Session Duration For Cleanup must be specified');
			
  }else if(!isNumber(document.forms[0].sessionDurationForCleanup.value)){
	  document.forms[0].sessionDurationForCleanup.focus();
	  alert('Session Duration for Cleanup must be Numeric.');
			
  }else if(isNull(document.forms[0].sessionTimeout.value)){
	  document.forms[0].sessionTimeout.focus();
	  alert('Session Timeout must be specified');
			
  }else if(!isNumber(document.forms[0].sessionTimeout.value)){
	  document.forms[0].sessionTimeout.focus();
	  alert('Session Timeout must be Numeric.');
			
  }else if(isNull(document.forms[0].treatInvalidPacketAsFatal.value)){
	  document.forms[0].treatInvalidPacketAsFatal.focus();
	  alert('Treat Invalid Packet As Fatal must be specified');
			
  }else if(isNull(document.forms[0].notificationSuccess.value)){
	  document.forms[0].notificationSuccess.focus();
	  alert('Notification Success must be specified');
			
  }else if(isNull(document.forms[0].notificationFailure.value)){
	  document.forms[0].notificationFailure.focus();
	  alert('Notification Failure must be specified');
			
  }else if(isNull(document.forms[0].maxEapPacketSize.value)){
	  document.forms[0].maxEapPacketSize.focus();
	  alert('Max EAP Packet Size must be specified');
			
  }else if(!isNumber(document.forms[0].maxEapPacketSize.value)){
	  document.forms[0].maxEapPacketSize.focus();
	  alert('Max EAP Packet Size must be Numeric.');
			
  }else{

	   document.forms[0].action.value="update";   
       document.forms[0].submit();	  
  }  
}
	
function isNumber(val){
	nre= /^\d+$/;
	var regexp = new RegExp(nre);
	if(!regexp.test(val))
	{
		return false;
	}
	return true;
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.EAP_CONFIG%>',searchName,'update','<%=eapConfigInstanceData.getEapId()%>','verifyNameDiv');
}
</script>

<html:form action="/updateEAPBasicDetails">

<html:hidden name="updateEAPBasicDetailsForm" styleId="action" property="action" value="update"/>
<html:hidden name="updateEAPBasicDetailsForm" styleId="eapId" property="eapId"/>
<html:hidden name="updateEAPBasicDetailsForm" styleId="peapBool" property="peapBool" value="false"/>
<html:hidden name="updateEAPBasicDetailsForm" styleId="ttlsConfigurationExist" property="ttlsConfigurationExist"/>
<html:hidden name="updateEAPBasicDetailsForm" styleId="auditUId" property="auditUId"/>

<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
						        <tr>
									<td class="tblheader-bold" colspan="2">
										<bean:message bundle="servermgrResources" key="servermgr.eapconfig.updateeapconfiguration"/>
									</td>
						       </tr>
						       <tr > 
						<td class="small-gap" colspan="3" >&nbsp;</td>
			    </tr>
			    <tr > 
					   <td align="left" class="captiontext" valign="top" width="28%" >
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.name"/>
					   				<ec:elitehelp headerBundle="servermgrResources" 
					   					text="eapconfig.name" 
					   						header="servermgr.eapconfig.name"/>
					   </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:text styleId="name" property="name" onkeyup="verifyName();" size="25" maxlength="50" style="width:250px" tabindex="1"/><font color="#FF0000"> *</font>
						<div id="verifyNameDiv" class="labeltext"></div>
					  </td>
		  		</tr>
		  		<tr > 
					  <td align="left" class="captiontext" valign="top" valign="top" >
					  		<bean:message bundle="servermgrResources" 
					  			key="servermgr.eapconfig.description"/>
					  				<ec:elitehelp headerBundle="servermgrResources" 
					  					text="eapconfig.desc" 
					  						header="servermgr.eapconfig.description"/>
					  </td>
					  <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:textarea styleId="description" property="description" cols="30" rows="2" style="width:250px" tabindex="2"/>
					  </td>
		        </tr>
		        <tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.sessioncleanupinterval"/>
					   				<bean:message key="general.seconds" />
					   					<ec:elitehelp headerBundle="servermgrResources" 
					   						text="eapconfig.cleanupinterval" 
					   							header="servermgr.eapconfig.sessioncleanupinterval"/>
					   	</td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:text styleId="sessionCleanupInterval" property="sessionCleanupInterval" size="15" maxlength="50" tabindex="3" style="width:250px"/><font color="#FF0000"> *</font>
					  </td>
		  		</tr>
		  		
		  		<tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.sessiondurationforcleanup"/>
					   				<bean:message key="general.seconds" />
					   				    <ec:elitehelp headerBundle="servermgrResources" 
					   				    	text="eapconfig.cleanupduraiton" 
					   				    		header="servermgr.eapconfig.sessiondurationforcleanup"/>
					   </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:text styleId="sessionDurationForCleanup" property="sessionDurationForCleanup" tabindex="4" size="15" maxlength="50" style="width:250px"/><font color="#FF0000"> *</font>
					  </td>
		  		</tr>
		  		<tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.sessiontimeout"/>
					   				<bean:message key="general.seconds" />
					    				<ec:elitehelp headerBundle="servermgrResources" 
					    					text="eapconfig.sessiontimeout" 
					    						header="servermgr.eapconfig.sessiontimeout"/>
					   </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:text styleId="sessionTimeout" property="sessionTimeout" size="15" tabindex="5" maxlength="50" style="width:250px"/><font color="#FF0000"> *</font>
					  </td>
		  		</tr>
		  			<tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" key="servermgr.eapconfig.mskrevalidationtime"/>
					   		<bean:message key="general.seconds" />
					    	<ec:elitehelp headerBundle="servermgrResources" text="servermgr.eapconfig.mskrevalidationtime" header="servermgr.eapconfig.mskrevalidationtime"/>
				  	  </td>
					  <td align="left" class="labeltext" valign="top" colspan="2" > 
					  		<html:text styleId="mskRevalidationTime" property="mskRevalidationTime" size="15" maxlength="50" style="width:250px" tabindex="5"/>
					  </td>
		  		</tr>
		        <tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.treatinvalidpacketasfatal"/>
					   				<ec:elitehelp headerBundle="servermgrResources" 
					   					text="eapconfig.invalidpacket" 
					   						header="servermgr.eapconfig.treatinvalidpacketasfatal"/>
					   </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:select name="updateEAPBasicDetailsForm" styleId="treatInvalidPacketAsFatal" tabindex="6" property="treatInvalidPacketAsFatal" size="1" style="width:130px">
						   <html:option value="true" >True</html:option>
						   <html:option value="false" >False</html:option> 
						</html:select>
					  </td>
		  		</tr>
		        
		        
		  		
		  		<tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.notificationsuccess"/>
					  				<ec:elitehelp headerBundle="servermgrResources" 
					   					text="eapconfig.notificationsuccess" 
					   						header="servermgr.eapconfig.notificationsuccess"/>
					   </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:select name="updateEAPBasicDetailsForm" styleId="notificationSuccess" tabindex="7" property="notificationSuccess" size="1" style="width:130px">
						   <html:option value="true" >True</html:option>
						   <html:option value="false" >False</html:option> 
						</html:select>
					  </td>
		  		</tr>                     
		  		<tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.notificationfailure"/>
					   				<ec:elitehelp headerBundle="servermgrResources" 
					   					text="eapconfig.notificationfailure" 
					   						header="servermgr.eapconfig.notificationfailure"/>
					   </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:select name="updateEAPBasicDetailsForm" styleId="notificationFailure" tabindex="8" property="notificationFailure" size="1" style="width:130px">
						   <html:option value="true" >True</html:option>
						   <html:option value="false" >False</html:option> 
						</html:select>
					  </td>
		  		</tr>
		  		
		  		
		  		<tr> 
					   <td align="left" class="captiontext" valign="top">
					   		<bean:message bundle="servermgrResources" 
					   			key="servermgr.eapconfig.maxeappacketsize"/>
					   				<ec:elitehelp headerBundle="servermgrResources" 
					   					text="eapconfig.maxpacketsize" 
					   						header="servermgr.eapconfig.maxeappacketsize"/>
					 </td>
					   <td align="left" class="labeltext" valign="top" colspan="2" > 
						<html:text styleId="maxEapPacketSize" property="maxEapPacketSize" tabindex="9" size="15" maxlength="50" style="width:250px"/><font color="#FF0000"> *</font>
					  </td>
		  		</tr>
		  		
		  		
				 <tr>
			   		  <td align="left" class="labeltext" valign="top" >&nbsp;</td>
					  <td align="left" class="labeltext" valign="top" colspan="2" >&nbsp;</td>
			    </tr>
			    <tr>
			     	  <td class="small-gap" width="16%" >&nbsp;</td>
		     	      <td align="left" class="labeltext" valign="top" >
		     	        <input type="button" name="c_btnUpdate"  tabindex="10"  onclick="validateUpdate()"  value=" Update "  class="light-btn" />
		     	        <input type="button" name="c_btnCancel" tabindex="11" onclick="javascript:location.href='<%=basePath%>/viewEAPConfig.do?viewType=basic&eapId=<%=updateEAPBasicDetailsConfigForm.getEapId()%>'" value="   Cancel   "	class="light-btn" /></td>
					  
			    </tr>
			    
			    <tr>
			   		  <td align="left" class="labeltext" valign="top" >&nbsp;</td>
					  <td align="left" class="labeltext" valign="top" colspan="2" >&nbsp;</td>
			    </tr>
			</table>
		
						
</html:form>

<script>

</script>