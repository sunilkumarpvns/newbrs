<%@ page import="java.util.*"%>
<%@page import="com.elitecore.elitesm.util.constants.ServicePolicyConstants"%>
<%@page import="com.elitecore.elitesm.web.sessionmanager.forms.UpdateSessionManagerDetailForm"%>
<%@page import="com.elitecore.elitesm.util.constants.ExternalSystemConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData"%>
<%@ page import="java.util.List"%>

<% 
UpdateSessionManagerDetailForm updateSessionManagerDetailForm = (UpdateSessionManagerDetailForm)request.getAttribute("updateSessionManagerDetailForm");
String esiUrl = basePath+"/initAddExtenalSystemPopup.do?externalSystemTypeId="+ExternalSystemConstants.SESSION_MANAGER;
String[] sessionManagerInstanceIds = (String[])request.getAttribute("sessionManagerInstanceIds");
String[][] sessionManagerInstanceNames =  (String[][])request.getAttribute("sessionManagerInstanceNames");
List<ExternalSystemInterfaceInstanceData> sessionManagerServerList = (List<ExternalSystemInterfaceInstanceData>)request.getAttribute("sessionManagerInstanceList");	
%>


<script>

function validateUpdate()
{

	var sessionmanagerservers = document.getElementById("sessionMangerServers");
	
	if(sessionmanagerservers !=null){
		var options = document.getElementById("sessionMangerServers");
		var strCGArray = [];
		for (var i = 0; i < options.length; i++) {
			strCGArray.push(options[i].text);
		}
		var flagZeroPrepaideServer=0,flagOtherPrepaidServer=0;
		var indexofElement=0;
		for(var indexArray=0;indexArray<=strCGArray.length-1;indexArray++){
		  var element=strCGArray[indexArray];
      		if(element != null && element != ""){
      		 	var indexofElement=element.lastIndexOf("-");
      			if(indexofElement > 0){
      				indexofElement=indexofElement+1;
          			var lastIndexOfOccurance=element.charAt(indexofElement);
          			if(lastIndexOfOccurance == 0){
          				flagZeroPrepaideServer++;
          			}else{
          				flagOtherPrepaidServer++;
          			}
      			}
      		}
		}
		if(flagZeroPrepaideServer == 1 && flagOtherPrepaidServer == 0){
			alert("Session Manager must have atleast one Radius ESI with Non Zero");
			document.forms[0].prepaidServers.focus();
			return;
		}
		selectAll(sessionmanagerservers);
	}
	
	
    if(sessionmanagerservers.options.length==0){
       alert("At least one Session Management Server must be selected.");
       return;
	}else{
	  document.forms[0].action.value='update'; 	
	  document.forms[0].submit();
	}
	
	
}

var currentList;
var serverListArray = new Array();
function popup(mylink, windowname,current)
{
	
	currentList = current;
	if (! window.focus)return true;
		var href;
	if (typeof(mylink) == 'string')
					href=mylink;
	else
					href=mylink.href;
					
	window.open(href, windowname, 'width=800,height=300,left=150,top=100,scrollbars=yes');
	return false;
} 
function addItemInCurrentList(esiServers,weightageArray, serverNameArray){
	//alert(esiServers.length);
	var existLength = currentList.length
	var index = existLength;
	for(var i = 0; i < esiServers.length; i++) {
		
		if(esiServers[i].checked == true){
		var nameValue  = serverNameArray[i] +"-W-"+weightageArray[i];
		var keyValue   = esiServers[i].value  +"-"+weightageArray[i];
		currentList.options[index] = new Option(nameValue, keyValue);
		index++;
		}
	 }
}
function removeFromList(externalSystemList){
	
var size = externalSystemList.options.length;
for(var i = size - 1; i >= 0; i--) {
if ((externalSystemList.options[i] != null) && (externalSystemList.options[i].selected == true)) {
	externalSystemList.options[i] = null;
      }
   }
}

function selectAll(selObj){
	
	for(var i=0;i<selObj.options.length;i++){
		selObj.options[i].selected = true;
	}
}

var jSessionManagerNames = new Array();
var jSessionManagerInstanceIds = new Array();

function sessionManagerPopup (){
	var count=0;

	jSessionManagerNames.length = <%=sessionManagerServerList.size()%>;				
	jSessionManagerInstanceIds.length= <%=sessionManagerServerList.size()%>;
				
		<%int l,m=0;
		for(l =0;l<sessionManagerServerList.size();l++){%>		
		jSessionManagerNames[<%=l%>] = new Array(4);		
				<%for(m=0;m<4;m++){%>												
				jSessionManagerNames[<%=l%>][<%=m%>] = '<%=sessionManagerInstanceNames[l][m]%>'				
				<%}%>
				jSessionManagerInstanceIds[<%=l%>] = '<%=sessionManagerInstanceIds[l]%>'	
			count ++;							
		<%}%>	 	 

		var headersArr = new Array(6);
		headersArr[0] = '';
		headersArr[1] = '<bean:message bundle="externalsystemResources" key="esi.name" />';
		headersArr[2] = '<bean:message bundle="externalsystemResources" key="esi.address" />';
		headersArr[3] = '<bean:message bundle="externalsystemResources" key="esi.minlocalport" />';
		headersArr[4] = '<bean:message bundle="externalsystemResources" key="esi.expiredrequestlimitcount" />';
		headersArr[5] = 'Weightage';
		
		initializeData(jSessionManagerInstanceIds,jSessionManagerNames,'sessionPopup','sessionMgrCheckboxId',headersArr,'true',count);
		hideSelectedData('sessionMangerServers','sessionMgrCheckboxId');
}

function sessionManagPopup(){
	openpopup('sessionManagerPopup','sessionMgrCheckboxId',jSessionManagerInstanceIds,jSessionManagerNames,'sessionMangerServers');
}


</script>

<html:form action="/updateSessionManagerDetail">
	<html:hidden name="updateSessionManagerDetailForm" styleId="action" property="action" />
	<html:hidden name="updateSessionManagerDetailForm" styleId="sminstanceid" property="sminstanceid" />
	<html:hidden name="updateSessionManagerDetailForm" styleId="smType" property="smType" />

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" height="15%">
					<tr>
						<td class="small-gap" width="7" colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td width="100%" colspan="2" valign="top">
							<table width="100%" cellpadding="0" cellspacing="0" border="0">
								<tr>
									<td class="tblheader-bold" colspan="3">
										<bean:message bundle="sessionmanagerResources" key="sessionmanager.updatesessionmanagerdetails" />
									</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td align="left" class="captiontext" valign="top" width="20%">
										Session Management Servers 
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="sessionmanager.servers"/>','Session Management Servers')" />
									</td>
									<td align="left" class="labeltext" valign="top" width="32%">
										<html:select name="updateSessionManagerDetailForm" property="sessionMangerServers" styleId="sessionMangerServers" multiple="true" size="5" style="width: 250;">
											<html:optionsCollection name="updateSessionManagerDetailForm" property="sessionManagerServerList" label="name" value="value" />
										</html:select>
									</td>
									<td align="left" class="labeltext" valign="top" width="32%">
										<input type="button" value="Add " onClick="sessionManagPopup();" class="light-btn" style="width: 75px" />
										<br /> <br /> 
										<input type="button" value="Remove " onclick="removeData('sessionMangerServers','sessionMgrCheckboxId');" class="light-btn" style="width: 75px" />
									</td>
								</tr>

								<tr>
									<td align="left" class="captiontext" valign="top">
										<bean:message bundle="sessionmanagerResources" key="sessionmanager.translationmapping" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="sessionmanager.translationmapping"/>','<bean:message bundle="sessionmanagerResources" key="sessionmanager.translationmapping" />')" />
									</td>
									<td align="left" class="labeltext" valign="top"  colspan="3"><html:select
											name="updateSessionManagerDetailForm" styleId="translationType"
											property="configId"  style="width: 250;">
											<html:option value="">--select--</html:option>
											<optgroup label="Translation Mapping"
												class="labeltext">
												<logic:iterate id="translationMapping"
													name="updateSessionManagerDetailForm"
													property="translationMappingConfDataList"
													type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData">
													<html:option
														value="<%=ConfigConstant.TRANSLATION_MAPPING +  translationMapping.getTranslationMapConfigId()%>"
														styleClass="<%=ConfigConstant.TRANSLATION_MAPPING%>"><%=translationMapping.getName()%></html:option>
												</logic:iterate>
											</optgroup>
											<optgroup label="Copy Packet Mapping"
												class="labeltext">
												<logic:iterate id="copyPacketMapping"
													name="updateSessionManagerDetailForm"
													property="copyPacketMappingConfDataList"
													type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData">
													<html:option
														value="<%=ConfigConstant.COPY_PACKET_MAPPING + copyPacketMapping.getCopyPacketTransConfId()%>"
														styleClass="<%=ConfigConstant.COPY_PACKET_MAPPING%>"><%=copyPacketMapping.getName()%></html:option>
												</logic:iterate>
											</optgroup>
										</html:select> <html:hidden property="configId" styleId="configId"
											style="configId" /> </td>
								</tr>
												
								<tr>
									<td align="left" class="captiontext" valign="top">
										<bean:message bundle="sessionmanagerResources" key="sessionmanager.script" />
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="sessionmanager.script"/>','<bean:message bundle="sessionmanagerResources" key="sessionmanager.script" />')" />
									</td>
									<td align="left" class="labeltext" valign="top" colspan="3">
										<html:text property="script" maxlength="255" style="width: 250;" ></html:text>
									</td>
								</tr>
								<tr id="acceptOnTimeOutID">
									<td align="left" class="labeltext" valign="top"></td>
									<td align="left" class="labeltext" valign="top" width="32%">
										<html:checkbox property="acceptOnTimeOut" styleId="acceptOnTimeOut"></html:checkbox> 
										<bean:message bundle="sessionmanagerResources" key="sessionmanager.acceptontimeout" />
									</td>
								</tr>

								<tr>
									<td align="left" class="labeltext" valign="top">&nbsp;</td>
									<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td>
								</tr>
								<tr>
									<td align="center" class="labeltext" valign="top" colspan="2">
										<input type="button" name="c_btnNext" onclick="validateUpdate()" value=" Update " class="light-btn" />
										<input type="button" name="c_btnCancel" onclick="javascript:location.href='<%=basePath%>/viewSessionManager.do?sminstanceid=<%=updateSessionManagerDetailForm.getSminstanceid()%>'" value="   Cancel   " class="light-btn" />
									</td>

								</tr>

							</table>
						</td>
					</tr>

				</table>
			</td>
		</tr>
	</table>
	<div id="sessionManagerPopup" style="display: none;" title="Add Session Manager Servers">
		<table id="sessionPopup" name="sessionPopup" cellpadding="0" cellspacing="0" width="100%" class="box">
		</table>
	</div>
</html:form>

<script>
sessionManagerPopup();
</script>

