<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page
	import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page
	import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>

<%
		
		DriverInstanceData driverInstanceData = (DriverInstanceData)request.getSession().getAttribute("driverInstanceData");
%>



<script>
var isValidName;

function validateForm(){	

		if(isNull(document.forms[0].driverinstname.value)){
        	alert('Driver Name must be specified.');	        	 
    	}else if(!isValidName) {
 			alert('Enter Valid Driver Name');
 			document.forms[0].driverinstname.focus();
 			return;
 		}else if(isNull(document.forms[0].fileLocations.value)){
	        alert('Driver File Location must be specified.');	        	 
	    }else if(isNull(document.forms[0].driverinstname.value)){  
	        alert('Driver Instance name must be specified.');
		}else if(isNull(document.forms[0].expirydate.value)){  
	        alert('Driver expiry date format must be specified.');
		}else{			
 			document.forms[0].action.value = 'Update';
 	 		document.forms[0].submit();	    
		}  
	}		
	
function verifyName() {
	var searchName = document.getElementById("driverinstname").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DRIVER%>',searchName,'update','<%=driverInstanceData.getDriverInstanceId()%>','verifyNameDiv');
}
</script>
<html:form action="/updateRadiusUserFileAuthDriver">

	<html:hidden property="action" />
	<html:hidden property="driverInstanceId" />
	<html:hidden property="auditUId" styleId="auditUId"/>

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" width="27%"
							colspan="4"><bean:message bundle="driverResources"
								key="driver.driverinstancedetails" /></td>
					</tr>
					<tr>
						<td width="10" class="small-gap" colspan="4">&nbsp;</td>
					</tr>
					<tr>
						<td width="10" class="small-gap" colspan="4">&nbsp;</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="27%">
							<bean:message bundle="driverResources" key="driver.instname" />
								<ec:elitehelp headerBundle="driverResources" 
									text="createdriver.name" header="driver.instname"/>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text styleId="driverinstname" onkeyup="verifyName();"
								property="driverInstanceName" size="30" maxlength="60"
								tabindex="1" style="width:250px" /><font color="#FF0000">
								*</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
					</tr>


					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.instdesc" />
									<ec:elitehelp headerBundle="driverResources" 
										text="createdriver.description" header="driver.instdesc"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
							<html:text styleId="driverinstdesc" property="driverInstanceDesc"
								tabindex="2" size="30" maxlength="60" style="width:250px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.fileLocations" /> 
								<ec:elitehelp headerBundle="driverResources" 
									text="ufauthdriver.filelocation" header="driver.fileLocations"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
							<html:text styleId="fileLocations" tabindex="3"
								property="fileLocations" size="60" maxlength="255"
								style="width:250px" /><font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="driverResources" key="driver.userfileauthdriver.expirydate" /> 
								<ec:elitehelp headerBundle="driverResources" 
									text="ufauthdriver.expirydate" header="driver.userfileauthdriver.expirydate"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="76%">
							<html:text styleId="expirydate" property="expiryDateFormat"
								tabindex="4" size="30" maxlength="60" style="width:250px" /><font
							color="#FF0000"> *</font>
						</td>
					</tr>

					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="3"><input
							type="button" name="c_btnCreate" id="c_btnCreate2"
							value=" Update " class="light-btn" onclick="validateForm()"
							tabindex="5"> <input type="reset"
							name="c_btnDeletePolicy"
							onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'"
							value="Cancel" class="light-btn" tabindex="6"></td>
					</tr>
				</table>
			</td>
		</tr>

	</table>

</html:form>

