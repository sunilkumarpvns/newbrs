<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebMethodKeyMapRelData"%>
<%@page import="java.util.Set"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebServiceAuthDriverData"%>
<%@page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@ page import="com.elitecore.elitesm.web.driver.radius.forms.UpdateWebServiceAuthDriverForm"%>

<%
		
		DriverInstanceData driverInstanceData = (DriverInstanceData)request.getSession().getAttribute("driverInstanceData");
		WebServiceAuthDriverData webServiceAuthDriverData = (WebServiceAuthDriverData) request.getSession().getAttribute("webservicedata");
		
		
%>
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script>
var isValidName;
$(document).ready(function() {
	
	
	var logicalNameData =eval(('<bean:write name="logicalNameData"/>').replace(new RegExp("&quot;", 'g'),"\""));
	/* set Logical Data JsonObject */
	setLogicalNameData(logicalNameData);
	
	/* set Logical Name drop down for default value */ 
	setLogicalnameDropDown("mappingtbl","logicalnmVal",true);
	
	/* bind click event of delete image */
	$('#mappingtbl td img.delete').live('click',function() {
		 $(this).parent().parent().remove(); 
		 setLogicalnameDropDown("mappingtbl","logicalnmVal",true); /* set Logical Name drop down after remove row */
	});
	setAction("update");
	enableAll();
	setFieldSuggestion();
});	

var webMethodKeyArray = new Array();
var webMethodKeyArrayIndex = 0;
<logic:iterate id="obj1" name="updateWebServiceAuthDriverForm" property="webMethodKeyList">
	webMethodKeyArray[webMethodKeyArrayIndex++] = '<bean:write name="obj1" property="value"/>';
</logic:iterate>

function setWebMethodKey(webMethodKey){
	$(webMethodKey).autocomplete({
    source: webMethodKeyArray
});
} 
function validateForm(){	

		if(isNull(document.forms[0].driverinstname.value)){
        	alert('Driver Name must be specified.');	        	 
    	}else if(!isValidName) {
 			alert('Enter Valid Driver Name');
 			document.forms[0].driverinstname.focus();
 			return;        	 
	    }else if(isNull(document.forms[0].driverinstname.value)){  
	        alert('Driver Instance name must be specified.');
		}else if(isNull(document.forms[0].serviceAddress.value)){  
	        alert('Service address must be specified.');
		}else if(isNull(document.forms[0].imsiAttribute.value)){
           alert('IMSI Attribute must be specified.');        	 
        }else if(isNull(document.forms[0].maxQueryTimeoutCnt.value)){
           alert('Max Query Timeout Count must be specified.');        	 
        }else if(isNaN(document.forms[0].maxQueryTimeoutCnt.value)){
           alert('Max Query Timeout Count must be numeric.');        	 
        }else if(isNull(document.forms[0].statusChkDuration.value)){
           alert('Status Check Duration must be specified.');        	 
        }else if(isNaN(document.forms[0].statusChkDuration.value)){
           alert('Status Check Duration must be numeric.');
        }else if(document.getElementById('mappingtbl').getElementsByTagName('tr').length < 2){
	 	 	alert('At least one mapping must be there.');
        }else{			
        	if(isValidLogicalNameMapping("mappingtbl","logicalnmVal","webMethodKey")){
				document.forms[0].action.value = 'Update';
				document.forms[0].submit();
			}    
		}  
	}		
function setIMSIAttributes(){
	var imsiAttributeVal = document.getElementById("imsiAttribute").value;
	retriveRadiusDictionaryAttributes(imsiAttributeVal,"imsiAttribute");
}

function verifyName() {
	var searchName = document.getElementById("driverinstname").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DRIVER%>',searchName,'update','<%=driverInstanceData.getDriverInstanceId()%>','verifyNameDiv');
}
function setColumnsOnUserIdentity(){
	var userIdentityAttributes = document.getElementById("userIdentityAttributes").value;
	retriveRadiusDictionaryAttributes(userIdentityAttributes,"userIdentityAttributes");
}

</script>
<html:form action="/updateWebServiceAuthDriver">

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
							<bean:message bundle="driverResources" key="driver.name" />
								<ec:elitehelp headerBundle="driverResources" 
									text="createdriver.name" header="driver.name"/>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text styleId="driverinstname" onkeyup="verifyName();"
								tabindex="1" property="driverInstanceName" size="30"
								maxlength="60" style="width:250px" /><font color="#FF0000">
								*</font>
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
					</tr>


					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="driverResources" key="driver.description" />
								<ec:elitehelp headerBundle="driverResources" 
									text="createdriver.description" header="driver.description"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
							<html:text styleId="driverinstdesc" property="driverInstanceDesc"
								tabindex="2" size="30" maxlength="60" style="width:250px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="driverResources"
								key="driver.webserviceauthdriver.serviceaddress" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="webservicedriver.address" 
											header="driver.webserviceauthdriver.serviceaddress"/>	
						</td>
						<td align="left" class="labeltext" valign="top" width="76%">

							<html:text styleId="serviceAddress" tabindex="3"
								property="serviceAddress" size="30" maxlength="70"
								style="width:250px" /><font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="driverResources" 
								key="driver.webserviceauthdriver.imsiattribute" />
									<ec:elitehelp headerBundle="driverResources" 
										text="webservicedriver.imsi" 
											header="driver.webserviceauthdriver.imsiattribute"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="76%">

							<input type="text" name="imsiAttribute" tabindex="4"
							id="imsiAttribute" size="30" autocomplete="off" maxlength="128"
							onkeyup="setIMSIAttributes();" style="width: 250px"
							value="<bean:write name="updateWebServiceAuthDriverForm" property="imsiAttribute"/>" />
							<font color="#FF0000"> *</font>

						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="driverResources"
								key="driver.webserviceauthdriver.maxquerytimeoutcount" />
									<ec:elitehelp headerBundle="driverResources" 
										text="webservicedriver.maxquerytimeoutcount" 
											header="driver.webserviceauthdriver.maxquerytimeoutcount"/>							
						</td>
						<td align="left" class="labeltext" valign="top" width="76%">

							<html:text styleId="maxQueryTimeoutCnt" tabindex="5"
								property="maxQueryTimeoutCnt" size="60" maxlength="20"
								style="width:250px" /><font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="driverResources"
								key="driver.webserviceauthdriver.statuschkduration" />
									<ec:elitehelp headerBundle="driverResources" 
										text="webservicedriver.statuscheckduration" 
											header="driver.webserviceauthdriver.statuschkduration" />
						</td>
						<td align="left" class="labeltext" valign="top" width="76%">

							<html:text styleId="statusChkDuration" tabindex="6"
								property="statusChkDuration" size="60" maxlength="20"
								style="width:250px" /><font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="18%">
							<bean:message bundle="driverResources"
								key="driver.useridentityattributes" /> 
									<ec:elitehelp headerBundle="driverResources" 
										text="driver.usridentity" header="driver.useridentityattributes"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="76%">
							<input type="text" name="userIdentityAttributes" tabindex="7"
							id="userIdentityAttributes" size="60" maxlength="256"
							autocomplete="off" onkeyup="setColumnsOnUserIdentity();"
							style="width: 250px"
							value="<bean:write name="updateWebServiceAuthDriverForm" property="userIdentityAttributes"/>" />
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="4">
							<bean:message bundle="driverResources"
								key="driver.webserviceauthdriver.wsmapping" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" colspan="3"
							id="button"><input type="button" onclick='addNewRow("dbMappingTable","mappingtbl","logicalnmVal",true);'
							tabindex="8" value=" Add Mapping" class="light-btn"
							style="size: 140px"></td>
					</tr>
					<tr>
						<td width="10" class="small-gap">&nbsp;</td>
						<td class="small-gap" colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td width="97%" colspan="4" valign="top" class="captiontext">
							<table cellSpacing="0" cellPadding="0" width="98%" border="0" id="mappingtbl" class="box">
								<tr>
									<td align="left" class="tblheader" valign="top" id="tbl_attrid">
										<bean:message bundle="driverResources" 
											key="driver.webserviceauthdriver.logicalname" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="webservicedriver.logicalname" 
														header="driver.webserviceauthdriver.logicalname"/>
									</td>
									<td align="left" class="tblheader" valign="top">
										<bean:message bundle="driverResources"
											key="driver.webserviceauthdriver.wsmethodkey" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="webservicedriver.webservicekey" 
														header="driver.webserviceauthdriver.wsmethodkey"/>									
									</td>
									<td align="left" class="tblheader" valign="top" width="15%">
										<bean:message bundle="driverResources"
											key="driver.webserviceauthdriver.defaultvalue" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="webservicedriver.defaultvalue" 
														header="driver.webserviceauthdriver.defaultvalue"/>
									</td>
									<td align="left" class="tblheader" valign="top">
										<bean:message bundle="driverResources"
											key="driver.webserviceauthdriver.valuemapping" /> 
												<ec:elitehelp headerBundle="driverResources" 
													text="webservicedriver.valuemapping" 
														header="driver.webserviceauthdriver.valuemapping"/>
									</td>
									<td align="left" class="tblheader" valign="top" width="5%">Remove</td>
								</tr>
								<logic:iterate id="obj" name="webServiceAuthDriverFieldMapList"
									type="com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebMethodKeyMapRelData">
									<tr>
										<td class="allborder"><select class="noborder"
											name="logicalnmVal" id="logicalnmVal" style="width: 100%"
											tabindex="10"
											onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
												<option
													value='<bean:write name="obj" property="logicalName"/>'>
													<bean:write name="obj" property="logicalName" />
												</option>
												
										</select></td>
										<td class="tblrows"><input class="noborder" type="text"
											name="webMethodKey" maxlength="1000" size="28"
											style="width: 100%" tabindex="10"
											value='<bean:write name="obj" property="webMethodKey"/>' onfocus="setWebMethodKey(this);"/></td>
										<td class="tblrows"><input class="noborder" type="text"
											name="defaultValue" maxlength="1000" size="28"
											style="width: 100%" tabindex="10"
											value='<bean:write name="obj" property="defaultValue"/>' /></td>
										<td class="tblrows"><input class="noborder" type="text"
											name="valueMapping" maxlength="1000" size="30"
											style="width: 100%" tabindex="10"
											value='<bean:write name="obj" property="valueMapping"/>' /></td>
										<td class="tblrows" align="center" colspan="3"><img
											value="top" src="<%=basePath%>/images/minus.jpg"
											class="delete" style="padding-right: 5px; padding-top: 5px;"
											height="14" tabindex="10" /></td>
									</tr>
								</logic:iterate>
							</table>
						</td>
					</tr>
					<tr>
						<td width="10" class="small-gap">&nbsp;</td>
						<td class="small-gap" colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="3"><input
							type="button" name="c_btnCreate" id="c_btnCreate2" tabindex="9"
							value=" Update " class="light-btn" onclick="validateForm()">
							<input type="reset" name="c_btnDeletePolicy" tabindex="10"
							onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'"
							value="Cancel" class="light-btn"></td>
					</tr>
				</table>
			</td>
		</tr>

	</table>
	</html:form>
<!-- Mapping Table Row template -->
<table style="display: none;" id="dbMappingTable">
	<tr>
		<td class="allborder"><select class="noborder"
			name="logicalnmVal" id="logicalnmVal" style="width: 100%"
			tabindex="10"
			onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
		</select></td>
		<td class="tblrows"><input class="noborder" type="text"
			name="webMethodKey" maxlength="1000" size="28" style="width: 100%" onfocus="setWebMethodKey(this);"
			tabindex="10" /></td>
		<td class="tblrows"><input class="noborder" type="text"
			name="defaultValue" maxlength="1000" size="28" style="width: 100%"
			tabindex="10" /></td>
		<td class="tblrows"><input class="noborder" type="text"
			name="valueMapping" maxlength="1000" size="30" style="width: 100%"
			tabindex="10" /></td>
		<td class="tblrows" align="center" colspan="3"><img value="top"
			src="<%=basePath%>/images/minus.jpg" class="delete"
			style="padding-right: 5px; padding-top: 5px;" height="14"
			tabindex="10" /></td>
	</tr>
</table>
