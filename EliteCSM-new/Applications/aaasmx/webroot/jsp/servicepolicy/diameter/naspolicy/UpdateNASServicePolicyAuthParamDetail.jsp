<%@page import="com.elitecore.elitesm.util.constants.PolicyPluginConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData"%>
<%@page import="com.elitecore.elitesm.util.constants.ExternalSystemConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthPolicyInstData"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ServiceTypeConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAdditionalDriverRelData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>
<%@page import="com.elitecore.commons.base.Collectionz"%>

<%
	UpdateNASServicePolicyAuthenticationParamsForm form = (UpdateNASServicePolicyAuthenticationParamsForm)request.getAttribute("nasPolicyForm");
	
	DriverBLManager driverManager = new DriverBLManager();
	List<DriverInstanceData> listOfDriver = driverManager.getDriverInstanceList(ServiceTypeConstants.NAS_AUTH_APPLICATION);
	
	
	String[] driverInstanceIds = new String [listOfDriver.size()];
	String[][] driverInstanceNames = new String[listOfDriver.size()][3]; 
	
	for(int i=0;i<listOfDriver.size();i++){
		DriverInstanceData data = listOfDriver.get(i);				
			driverInstanceNames[i][0] = String.valueOf(data.getName());
			driverInstanceNames[i][1] = String.valueOf(data.getDescription());
			driverInstanceNames[i][2] = String.valueOf(data.getDriverTypeData().getDisplayName());
		driverInstanceIds[i] = String.valueOf(data.getDriverInstanceId());
	}
	
	List<PluginInstData> pluginInstDataList = (List<PluginInstData>)request.getAttribute("pluginInstDataList");
%>

<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.UpdateNASServicePolicyAuthenticationParamsForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAuthDriverRelData"%><script language="javascript1.2" src="<%=basePath%>/js/checkpwdstrength.js" type="text/javascript"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/diameter/diameter-policy.js"></script>
<script>
function validate()
{	
	if(isNull(document.forms[0].multipleUserIdentity.value)){
		alert('User Identity Attribute must be specified');
		document.forms[0].multipleUserIdentity.focus();
		return;
	}
	selectAll(selecteddriverIds);
	var additionalDriverList=document.getElementById("selectedAdditionalDriverIds");
	selectAll(additionalDriverList);
	if(selecteddriverIds.options.length==0){
		alert('Authentication Driver must be specified');
		return;
	}
	
	if(!validatePlugins('pre-plugin-mapping-table','post-plugin-mapping-table','authPrePluginJson','authPostPluginJson')){
		return;
	}
	
	document.forms[0].action.value="update";
	document.forms[0].submit();
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
	if(selObj!=null){
		for(var i=0;i<selObj.options.length;i++){
			selObj.options[i].selected = true;
		}
	}
}

function setStripUserIdentity(){
	var stripUserCheckBox = document.getElementById("stripUserCheckBox");
	var realmPatternComp = document.getElementById("realmPattern");
	var separatorComp = document.getElementById("realmSeparator");
	if(stripUserCheckBox.checked ==true){
		realmPatternComp.disabled=false;
		separatorComp.disabled=false;
	}else{
		realmPatternComp.disabled=true;
		separatorComp.disabled=true;
	}
}

function validateAuthMethod(){	
		var selectedAuthMethodCheBoxes = document.getElementsByName("selectedAuthMethodTypes");
		if(selectedAuthMethodCheBoxes.length>0){
				for (i=0; i<selectedAuthMethodCheBoxes.length; i++){
				 		 if (selectedAuthMethodCheBoxes[i].checked == true){  	
								return true;				 			 
				 		 }
				}
		}
		return false;				
}


function setInit(){
	var stripUserCheckBox = document.getElementById("stripUserCheckBox");
	var trimUserIdentityCheckBox = document.getElementById("trimUserIdentityCheckBox");
	var trimPasswordCheckBox = document.getElementById("trimPasswordCheckBox");
	setStripUserIdentity();
	
		
}

function setColumnsOnMultipleUIdTextFields(){
	var multipleUserIdVal = document.getElementById("multipleUserIdentity").value;
	retriveRadiusDictionaryAttributes(multipleUserIdVal,"multipleUserIdentity");
}

function setColumnsOnCuiRespAttrTextFields(){
	var cuiRespAttrVal = document.getElementById("cuiResponseAttributes").value;
	retriveRadiusDictionaryAttributes(cuiRespAttrVal,"cuiResponseAttributes");
}

function setColumnsOnUserNameRespAttrTextFields(){
	var userNameRespAttrVal = document.getElementById("userNameResonseAttributes").value;
	retriveRadiusDictionaryAttributes(userNameRespAttrVal,"userNameResonseAttributes");
}

var jdriverNames = new Array();
var jdriverInstanceIds = new Array();
var count=0;
var pluginList = [];

var driverScriptList = [];

<% 
if( Collectionz.isNullOrEmpty(form.getDriverScriptList()) == false ){
	for( ScriptInstanceData scriptInstData : form.getDriverScriptList()){ %>
		driverScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
	<%}
}
%>

$(document).ready(function() {		
	
	/* Get plugin list*/
	<%for(PluginInstData pluginInstData : pluginInstDataList){%>
		pluginList.push({"id":"<%=pluginInstData.getPluginInstanceId()%>","value":"<%=pluginInstData.getPluginTypeName()%>","label":"<%=pluginInstData.getName()%>"});
	<%}%>
	
	/* Script Autocomplete */
	setSuggestionForScript(driverScriptList, "scriptInstAutocomplete");
	
	jdriverNames.length = <%=listOfDriver.size()%>;				
	jdriverInstanceIds.length= <%=listOfDriver.size()%>;
		
	
	<%int j,k=0;
	for(j =0;j<listOfDriver.size();j++){%>		
		jdriverNames[<%=j%>] = new Array(2);		
			<%for(k=0;k<3;k++){%>												
				jdriverNames[<%=j%>][<%=k%>] = '<%=driverInstanceNames[j][k]%>'				
			<%}%>
		jdriverInstanceIds[<%=j%>] = '<%=driverInstanceIds[j]%>'	
		count ++;							
	<%}%>	 	 

	var headersArr = new Array(5);
	headersArr[0] = '';
	headersArr[1] = 'Driver Name';
	headersArr[2] = 'Driver Description';
	headersArr[3] = 'Driver Type';
	headersArr[4] = 'Weightage';
	
	initializeData(jdriverInstanceIds,jdriverNames,'addDriver','driverDataCheckBoxId',headersArr,'true',count);
	
	
	var additionalHeadersArr = new Array(3);
	additionalHeadersArr[0] = '';
	additionalHeadersArr[1] = 'Additional Driver Name';
	additionalHeadersArr[2] = 'Driver Description';
	additionalHeadersArr[3] = 'Driver Type';
	initializeAdditionalData(jdriverInstanceIds,jdriverNames,'additionalDriver','additionalDriverCheckBoxId',additionalHeadersArr,jdriverInstanceIds.length);

	hideSelectedData('selecteddriverIds','driverDataCheckBoxId');
	hideSelectedAdditionalDriverData('selectedAdditionalDriverIds','additionalDriverCheckBoxId');
   }
);

function hideSelectedData(componentId,checkboxid){
	
	var id = "#" + componentId + " " +"option";
	$(id).each(function(){
		 var mainValue = $(this).val();
		 var strippedVal = mainValue.split('-');
	     var rowid="#row"+checkboxid+strippedVal[0];
	     var addtionalId = "#additionalrow"+"additionalDriverCheckBoxId"+strippedVal[0];
	     $(rowid).hide();
	     $(addtionalId).hide();
    });
}

function hideSelectedAdditionalDriverData(componentId,checkboxid){
	var id = "#" + componentId + " " +"option"; 
	$(id).each(function(){
		 var mainValue = $(this).val();
	     var primaryId="#row"+"driverDataCheckBoxId"+mainValue;
	     $(primaryId).hide();
		 $("#additionalrow"+checkboxid+mainValue).hide();
    });
}
function driverpopup(){
	openpopup('driverPopup','driverDataCheckBoxId',jdriverInstanceIds,jdriverNames,'selecteddriverIds');
 }

function openpopup(divId,checkBxId,listOfIds,listOfNms,componentId,isWeightage){
	
	var id = "#" + divId;
	var comid = "#" + componentId;
	
	document.getElementById(divId).style.visibility = "visible";

       <%-- Reset Popup Window--%>	
	   if(isWeightage==null || isWeightage=='true')
		{
	       var jItems=$("*[name='"+checkBxId+"']"); //it will give element by name in jquery
			 for(var i=0;i<jItems.length;i++)
		     {
		         $("#"+checkBxId+listOfIds[i]+"w").val("1");
		     }

	    }

			
	$(id).dialog({
		modal: true,
		autoOpen: false,
		minHeight: 200,
		height: 250,
		width: 700,
		buttons:{					
			        'Add': function() {
						
                       //var selectedItems=document.getElementsByName(checkBxId);
                       var selectedItems=$("*[name='"+checkBxId+"']");
                       
                        if(selectedItems.length==1 && selectedItems[0].checked  == true){
                                
                        		if(isWeightage==null || isWeightage=='true'){	 
									var optionsval =$("#"+checkBxId+listOfIds[0]+"w").val();								
                                	var labelVal=$("#"+checkBxId+listOfIds[0]).val();       
                               		$(comid).append("<option id="+ listOfIds[0] +" value="+ listOfIds[0] + "-" + optionsval +" class=labeltext> "+labelVal+"-W-" + optionsval +" </option>");
                               		
								}else if(isWeightage=='false'){
	                                var labelVal=$("#"+checkBxId+listOfIds[0]).val();                                
	                               	$(comid).append("<option id="+ listOfIds[0] +" value="+ listOfIds[0]  +" class=labeltext> "+labelVal +" </option>");
								}
                        		$("#row"+checkBxId+listOfIds[0]).hide();
                               	$("#additionalrow"+"additionalDriverCheckBoxId"+listOfIds[0]).hide();
                            	selectedItems[0].checked=false;
								
								         
                        }else if(selectedItems.length>1 ){
	                        for(var i=0;i<selectedItems.length;i++)
	                        {
								if(selectedItems[i].checked == true)
	                            {   	                         
									if(isWeightage==null || isWeightage=='true'){	 
										var optionsval =$("#"+checkBxId+listOfIds[i]+"w").val();
	                                	var labelVal=$("#"+checkBxId+listOfIds[i]).val();     
	          							$(comid).append("<option id="+ listOfIds[i] +" value="+ listOfIds[i] + "-" + optionsval +" class=labeltext> "+labelVal+"-W-" + optionsval +" </option>");
									}else if(isWeightage=='false'){
		                                var labelVal=$("#"+checkBxId+listOfIds[i]).val();                                
		                               	$(comid).append("<option id="+ listOfIds[i] +" value="+ listOfIds[i]  +" class=labeltext> "+labelVal +" </option>");
									} 
	                               	$("#row"+checkBxId+listOfIds[i]).hide();
	                               	$("#additionalrow"+"additionalDriverCheckBoxId"+listOfIds[i]).hide();
									selectedItems[i].checked=false;         
	                            }                             
	                        }
                        }
                        $(this).dialog('close');
					},                
					Cancel: function() {
					$(this).dialog('close');
					}
				},
		open: function() {
		
		},
		close: function() {
			
		}				
		});
		$(id).dialog("open");
		
 }

function initializeAdditionalData(listOfAdditionalId,listOfAdditionaldrNm,additionaltableId,additionalcheckbxId,additionalheaderArr,additionalcount){		
	
	var additionaltable = document.getElementById(additionaltableId);		
	var additionalrowid = additionaltable.insertRow(0);
	for(var d=0;d<additionalheaderArr.length;d++){
		var cells = additionalrowid.insertCell(d);					
		cells.innerHTML = additionalheaderArr[d];			
		cells.className="tblheader-bold";						
	}
	var additionaltemp = 1;
	
	for(var a = 0;a<additionalcount;a++){
		
		var additionalrow = additionaltable.insertRow(additionaltemp);		
		additionalrow.id = "additionalrow"+ additionalcheckbxId + listOfAdditionalId[a];
		additionalrow.style.visibility = 'visible';
            
		    var additionalcell1 = additionalrow.insertCell(0);
		    var additionalelement1 = document.createElement("input");
		    additionalelement1.type = "checkbox";
		    additionalelement1.name = additionalcheckbxId;
		    additionalelement1.id =additionalcheckbxId+listOfAdditionalId[a];		    		    		    
		    additionalelement1.value = listOfAdditionaldrNm[a][0];
		    additionalcell1.appendChild(additionalelement1);
		    additionalcell1.className="tblfirstcol";
		    additionalcell1.width="5%";
		    
		    var additionalcellNumber = 1;
			
			for(var b=0;b<listOfAdditionaldrNm[a].length;b++){
				var cell = additionalrow.insertCell(additionalcellNumber);					
				
				if( additionalcellNumber == 1 ){
					cell.innerHTML = '<span class="view-details-css" onclick="openViewDetails(this,'+listOfAdditionalId[a]+',\''+ listOfAdditionaldrNm[a][b] +'\',\''+'DRIVERS\');">' + listOfAdditionaldrNm[a][b] +"</span>";
				}else{
					cell.innerHTML = listOfAdditionaldrNm[a][b];
				}
				
				cell.className="tblrows";
				additionalcellNumber++;
			}
			additionaltemp++;
		}
   }


function additionalDriverpopup(){
	openAdditionalPopup('additionalDriverPopup','additionalDriverCheckBoxId',jdriverInstanceIds,jdriverNames,'selectedAdditionalDriverIds');
}

function openAdditionalPopup(divId,checkBxId,listOfIds,listOfNms,componentId){
	var id = "#" + divId;
	var comid = "#" + componentId;
	
	document.getElementById(divId).style.visibility = "visible";
			
	$(id).dialog({
		modal: true,
		autoOpen: false,
		minHeight: 200,
		height: 250,
		width: 600,
		buttons:{					
			        'Add': function() {
                       	var selectedItems=$("*[name='"+checkBxId+"']");
                        if(selectedItems.length==1 && selectedItems[0].checked  == true){
                        	var supportedDriverList=document.getElementById("selectedAdditionalDriverIds");
                            var labelVal=$("#"+checkBxId+listOfIds[0]).val();       
                           	$(comid).append("<option id="+ listOfIds[0] +" value="+ listOfIds[0] +" class=labeltext> "+labelVal+" </option>");
                               		                                                        
                           	$("#row"+"driverDataCheckBoxId"+listOfIds[0]).hide();
                        	$("#additionalrow"+"additionalDriverCheckBoxId"+listOfIds[0]).hide();
							selectedItems[0].checked=false;	
								        
                        }else if(selectedItems.length>1 ){
                        	for(var i=0;i<selectedItems.length;i++)
	                        {
								if(selectedItems[i].checked == true)
	                            {   	
									var supportedDriverList=document.getElementById("selectedAdditionalDriverIds");	
									//var optionsval =$("#"+checkBxId+listOfIds[i]+"w").val();	
	                                var labelVal=$("#"+checkBxId+listOfIds[i]).val();       
	                               	$(comid).append("<option id="+ listOfIds[i] +" value="+ listOfIds[i] +" class=labeltext> "+labelVal+" </option>");	

	                               	$("#row"+"driverDataCheckBoxId"+listOfIds[i]).hide();
	                            	$("#additionalrow"+"additionalDriverCheckBoxId"+listOfIds[i]).hide();
	                                selectedItems[i].checked=false;         
	                            }                             
	                        }
                        }
                        $(this).dialog('close');
					},                
					Cancel: function() {
						$(this).dialog('close');
					}
				},
		open: function() {
		
		},
		close: function() {
			
		}				
		});
		$(id).dialog("open");	
 }
 
function removeAdditionalDriverData(componentId,checkboxid){
	 var id = "#" + componentId + " " +"option:selected";
	 $(id).each(function(){
		 var mainValue = $(this).val();
	     $("#row"+"driverDataCheckBoxId"+mainValue).show();
	     $("#additionalrow"+checkboxid+mainValue).show();
	     $(this).remove();				      
 });
} 
function removeData(componentId,checkboxid){
	 var id = "#" + componentId + " " +"option:selected";	
	 
	$(id).each(function(){
		 var mainValue = $(this).val();
		 var strippedVal = mainValue.split('-');
	     var rowid="#row"+checkboxid+strippedVal[0];
	     var addtionalId = "#additionalrow"+"additionalDriverCheckBoxId"+strippedVal[0];
	     $(rowid).show();
	     $(addtionalId).show();
	     $(this).remove();				      
  });
}

</script>
<div>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<html:form action="/updateNASServicePolicyAuthenticationParamDetail">
<html:hidden name="updateNASServicePolicyAuthParamForm" styleId="nasPolicyId" property="nasPolicyId"/>
<html:hidden name="updateNASServicePolicyAuthParamForm" styleId="action" property="action" />   
<html:hidden name="updateNASServicePolicyAuthParamForm" styleId="auditUId" property="auditUId" />   
<html:hidden name="updateNASServicePolicyAuthParamForm" styleId="name" property="name" />   
<html:hidden name="updateNASServicePolicyAuthParamForm" property="authPrePluginJson"  styleId="authPrePluginJson" />
<html:hidden name="updateNASServicePolicyAuthParamForm" property="authPostPluginJson" styleId="authPostPluginJson" />
 <tr> 
     <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
			<tr>
				<td align="left" class="tblheader-bold" valign="top"  width="30%" colspan="3">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.authenticationdetails"/>
				</td>
		   </tr>
		   <tr>	
			 <td class="small-gap" colspan="3">&nbsp;</td>
		   </tr>
			<tr>
				<td class="captiontext" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.authmethods" />
					<ec:elitehelp headerBundle="servicePolicyProperties" text="naspolicy.methods" header="servicepolicy.naspolicy.authmethods"/>
				</td>	
				<td class="labeltext" colspan="2" width="70%">
					<logic:iterate id="authMethodType" name="updateNASServicePolicyAuthParamForm"  property="methodTypeList" type="com.elitecore.elitesm.datamanager.servicepolicy.auth.data.AuthMethodTypeData">
						
						<logic:equal value="PAP" name="authMethodType" property="name" >
						 <html:multibox property="selectedAuthMethodTypes" styleClass="labeltext"  styleId="proxyCheckBox" >
						  <bean:write name="authMethodType" property="authMethodTypeId"/>
						  </html:multibox>
						  <bean:write name="authMethodType" property="name"/> 
						</logic:equal>
						
						<logic:equal value="CHAP" name="authMethodType" property="name" >
						 <html:multibox property="selectedAuthMethodTypes" styleClass="labeltext" styleId="httpDigestCheckBox" >
						  <bean:write name="authMethodType" property="authMethodTypeId"/>
						  </html:multibox>
						  <bean:write name="authMethodType" property="name"/> 
						</logic:equal>	
							
					</logic:iterate>																															
				</td>
			</tr>
			
			<tr>	
				<td class="small-gap" colspan="3">&nbsp;</td>
			</tr>
		
			<tr>	
				<td class="small-gap" colspan="3">&nbsp;</td>
			</tr>
			<tr>
				<td align="left" class="captiontext" valign="top" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.multipleuid"/>
					<ec:elitehelp headerBundle="servicePolicyProperties" text="naspolicy.idattribute" header="servicepolicy.naspolicy.multipleuid"/>
				</td>
				<td align="left" class="labeltext" valign="top" colspan="2" width="70%">
					<input type="text" name="multipleUserIdentity" id="multipleUserIdentity" size="30" value="<%=(form.getMultipleUserIdentity() != null) ?form.getMultipleUserIdentity() : ""%>" autocomplete="off" onkeyup="setColumnsOnMultipleUIdTextFields();" style="width:250px"/>
					<font color="#FF0000"> *</font>
				</td>
			</tr>
			<tr>
				<td align="left" class="captiontext" valign="top" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.casesensitiveuid"/>
					<ec:elitehelp headerBundle="servicePolicyProperties" text="naspolicy.casesensitiveuid" header="servicepolicy.naspolicy.casesensitiveuid"/>
				</td>
				<td align="left" class="labeltext" valign="top" colspan="2" width="70%">
					<html:select property="caseSensitiveUserIdentity"  styleClass="labeltext" styleId="caseSensitiveUserIdentity">
						<html:option value="1">No Change</html:option>
						<html:option value="2">Lower Case</html:option>											
						<html:option value="3">Upper Case</html:option>
					</html:select>
				</td>
			</tr>
			<tr>									  
				<td align="left" class="captiontext" valign="top" width="30%">
					<bean:message bundle="servicePolicyProperties" key="update.user.identity"/>
					<ec:elitehelp headerBundle="servicePolicyProperties" text="naspolicy.updateuserid" header="update.user.identity"/>
				</td>											
				<td class="labeltext"  colspan="2" valign="top" width="70%"> 		
				  <table width="97%" style="border-style: solid; border-width: 1px; border-color: #CCCCCC;">
					<tr>
						<td class="labeltext" >
							<html:checkbox property="stripUserIdentity" styleId="stripUserCheckBox" onclick="setStripUserIdentity()" ></html:checkbox>
                                       	<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.stripuid"/>
                            			 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;	
							
							<html:text property="realmSeparator" size="1" maxlength="1" styleId="realmSeparator" name="updateNASServicePolicyAuthParamForm"/>
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.realmseparator" />
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;													   
							
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.realmpattern" />
                                         <html:select property="realmPattern"  styleClass="labeltext" styleId="realmPattern">
                                                <html:option value="prefix">Prefix</html:option>
                                                <html:option value="suffix">Suffix</html:option>                                            
                                         </html:select>
						</td>
					</tr>
					<tr>
						<td class="labeltext" >
							<html:checkbox property="trimUserIdentity"  styleId="trimUserIdentityCheckBox"></html:checkbox>
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.trimuid"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;																							
							<html:checkbox property="trimPassword" styleId="trimPasswordCheckBox"></html:checkbox>
							<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.trimpassword"/>
						</td>
					</tr>
				</table>
				</td>								
			</tr>
			<tr>
				<td align="left" class="captiontext" valign="top" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.username"/>
					<ec:elitehelp headerBundle="servicePolicyProperties" text="naspolicy.username" header="servicepolicy.naspolicy.username"/>
				</td>
				<td align="left" class="labeltext" valign="top" colspan="2"  width="70%">											
					<html:select property="userNameAttribute"  styleClass="labeltext" styleId="uname" style="width: 200px">
							<html:option value="NONE">NONE</html:option>
							<html:option value="Authenticated-Username">Authenticated-Username</html:option>
<!-- 							<html:option value="CUI">CUI</html:option> -->
							<html:option value="Request">Request</html:option>											
					</html:select>
				</td>

			</tr>
			<tr>
				<td align="left" class="captiontext" valign="top" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.usernameresattrs"/>
					<ec:elitehelp headerBundle="servicePolicyProperties" text="naspolicy.usernameresattrs" header="servicepolicy.naspolicy.usernameresattrs"/>
				</td>                   
				<td align="left" class="labeltext" valign="top" colspan="2" width="70%">
					<input type="text" name="userNameResonseAttributes" id="userNameResonseAttributes" size="30" value="<%=(form.getUserNameResonseAttributes() != null) ? form.getUserNameResonseAttributes() : ""%>" autocomplete="off" onkeyup="setColumnsOnUserNameRespAttrTextFields();" style="width:250px"/>
				</td>
			</tr>
			<tr>
				<td align="left" class="captiontext" valign="top" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.anonymousidentity" /> 
					<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.anonymousidentity" header="servicepolicy.naspolicy.anonymousidentity"/>
				</td>                   
				<td align="left" class="labeltext" valign="top" colspan="2" width="70%">
					<html:text property="anonymousProfileIdentity" styleId="anonymousProfileIdentity" style="width:250px;"></html:text>
				</td>
			</tr>
			<tr>
				<td align="left" class="tblheader-bold" valign="top"  colspan="3">
					Other Details
				</td>
			</tr>
			<tr>
				<td width="30%" align="left" class="captiontext" valign="top">
					Driver Details	
				   	<ec:elitehelp headerBundle="servicePolicyProperties" text="naspolicy.drivers" header="Driver Group"/>
				</td>
				<td  width="70%"  align="left" class="labeltext" valign="top">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td width="45%">
								<html:select property="selecteddriverIds" multiple="true" styleId="selecteddriverIds" size="5" style="width: 250;">
									<%
										
									List<NASPolicyAuthDriverRelData> driverList = form.getDriversList();												
									if(driverList != null){
										for(int i = 0;i<driverList.size();i++){
																							
											NASPolicyAuthDriverRelData data = driverList.get(i);
											String nm = " ";
											if(data.getWeightage() != null)
												nm = data.getWeightage().toString();
											nm = data.getDriverData().getName() + "-W-" + nm;		
											String value = data.getDriverInstanceId() + "-" + data.getWeightage();
										%>																												
											<html:option value="<%=value%>" ><%=nm%></html:option>
										<%}
									}%>
								</html:select><font color="#FF0000"> *</font>	
							</td>
							<td width="55%">
								<input type="button" value="Add " onClick="driverpopup()"  class="light-btn"  style="width: 75px"/><br/>
				  				<br/>
				   				<input type="button" value="Remove "  onclick="removeData('selecteddriverIds','driverDataCheckBoxId');" class="light-btn" style="width: 75px"/>
							</td>
						</tr>
					</table>
				</td>
		</tr>
		<tr>
				<td width="30%" align="left" class="captiontext" valign="top">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.additionalgroup" />																						
					<ec:elitehelp headerBundle="servicePolicyProperties" text="authpolicy.additionalgroup" header="servicepolicy.additionalgroup"/>
				</td>
				
				<td  width="70%"  align="left" class="labeltext" valign="top">
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
						<tr>
							<td width="45%">
								<html:select  property="selectedAdditionalDriverIds" multiple="true" styleId="selectedAdditionalDriverIds" size="5" style="width: 250;">
									<logic:iterate id="nasPolicyAdditionalDriverRelData"  name="nasPolicyInstData"  property="nasPolicyAdditionalDriverRelDataList" >
                								<bean:define  id="driverInstanceData" name="nasPolicyAdditionalDriverRelData" property="driverInstanceData" type="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"/>
                								<option value="<bean:write name="driverInstanceData" property="driverInstanceId"/>"><bean:write name="driverInstanceData" property="name"/></option> 
                							</logic:iterate> 
								</html:select>	
							</td>
							<td width="55%">
							   <input type="button" value="Add " onClick="additionalDriverpopup();"  class="light-btn" style="width: 75px"/><br/>
							   <br/>
							   <input type="button" value="Remove "  onclick="removeAdditionalDriverData('selectedAdditionalDriverIds','additionalDriverCheckBoxId')" class="light-btn" style="width: 75px"/>
							</td>
						</tr>
					</table>
				</td>
		</tr>
			
		<tr>
				<td class="captiontext" valign="top" width="30%">
					<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.script" />
					<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.script" header="servicepolicy.naspolicy.script"/>
				</td>
				<td class="labeltext" valign="top" nowrap="nowrap" colspan="2" width="70%">
					<html:text property="authScript" size="30" style="width:250px" maxlength="255" styleClass="scriptInstAutocomplete"></html:text>
				</td>
		</tr>
		<tr>
			<td class="tblheader-bold" colspan="3">
				<bean:message bundle="servicePolicyProperties" key="servicepolicy.preplugins" /> 
			</td>
		</tr>
		<tr>
			<td colspan="3" class="captiontext" style="padding: 10px;" align="left">
				<table id="postPluginTbl" class="postPluginTbl" cellspacing="0" cellpadding="0" width="70%">
					<tr>
						<td class="captiontext" valign="top" colspan="2">
							<input type="button" value="Add Plugin" onClick="addPluginMapping('pre-plugin-mapping-table','pre-plugin-mapping-template');" class="light-btn" tabindex="2" /><br />
						</td>
					</tr>
					<tr>
						<td  class="captiontext" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%" id="pre-plugin-mapping-table" class="pre-plugin-mapping-table">
								<tr>
									<td class="tbl-header-bold tbl-header-bg" width="47.5%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.pluginname" />
										<ec:elitehelp   header="radiusservicepolicy.pluginname" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginname" ></ec:elitehelp>
									</td>	
									<td class="tbl-header-bold tbl-header-bg" width="47.5%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.plugin.pluginarguments" />
										<ec:elitehelp   header="radiusservicepolicy.pluginArg" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginArg" ></ec:elitehelp>
									</td>	
									<td class="tbl-header-bold tbl-header-bg" width="5%">
										Remove
									</td>	
								</tr>
								<logic:iterate id="obj" name="nasPolicyForm" property="nasPolicyAuthPluginConfigList">
									<logic:equal property="pluginType" name="obj" value="<%=PolicyPluginConstants.IN_PLUGIN%>">
										<tr>
											<td class="tblfirstcol" width="47.5%">
												<input type="text" name="prePluginName" class="noborder" style="width:100%;" onfocus="setAutocompletePluginData(this,pluginList);" value="<bean:write name="obj" property="pluginName"/>"/>
											</td>	
											<td class="tblrows" width="47.5%">
												<textarea name="prePluginArgument" rows="1" cols="1" style="width:100%;height: 19px;" class="noborder"><bean:write name='obj' property='pluginArgument'/></textarea>
											</td>	
											<td class="tblrows" width="5%" align="center">
												<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
											</td>	
										</tr>
									</logic:equal>
								</logic:iterate>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="tblheader-bold" colspan="3">
				<bean:message bundle="servicePolicyProperties" key="servicepolicy.postplugins" /> 
			</td>
		</tr>
		<tr>
			<td colspan="3" class="captiontext" style="padding: 10px;" align="left">
				<table id="postPluginTbl" class="postPluginTbl" cellspacing="0" cellpadding="0" width="70%">
					<tr>
						<td class="captiontext" valign="top" colspan="2">
							<input type="button" value="Add Plugin" onClick="addPluginMapping('post-plugin-mapping-table','post-plugin-mapping-template');" class="light-btn" tabindex="2" /><br />
						</td>
					</tr>
					<tr>
						<td  class="captiontext" valign="top">
							<table cellspacing="0" cellpadding="0" border="0" width="100%" id="post-plugin-mapping-table" class="post-plugin-mapping-table">
								<tr>
									<td class="tbl-header-bold tbl-header-bg" width="47.5%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.pluginname" />
										<ec:elitehelp   header="radiusservicepolicy.pluginname" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginname" ></ec:elitehelp>
									</td>	
									<td class="tbl-header-bold tbl-header-bg" width="47.5%">
										<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.plugin.pluginarguments" />
										<ec:elitehelp   header="radiusservicepolicy.pluginArg" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginArg" ></ec:elitehelp>
									</td>	
									<td class="tbl-header-bold tbl-header-bg" width="5%">
										Remove
									</td>	
								</tr>
								<logic:iterate id="obj" name="nasPolicyForm" property="nasPolicyAuthPluginConfigList">
									<logic:equal property="pluginType" name="obj" value="<%=PolicyPluginConstants.OUT_PLUGIN%>">
										<tr>
											<td class="tblfirstcol" width="47.5%">
												<input type="text" name="postPluginName" class="noborder" style="width:100%;" onfocus="setAutocompletePluginData(this,pluginList);" value="<bean:write name="obj" property="pluginName"/>"/>
											</td>	
											<td class="tblrows" width="47.5%">
												<textarea name="postPluginArgument" rows="1" cols="1" style="width:100%;height: 19px;" class="noborder"><bean:write name='obj' property='pluginArgument'/></textarea>
											</td>	
											<td class="tblrows" width="5%" align="center">
												<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
											</td>	
										</tr>
									</logic:equal>
								</logic:iterate>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
																																									
		<tr>
			<td class="btns-td" valign="middle">
				&nbsp;
			</td>
			<td class="btns-td" valign="middle" colspan="2">
				<input type="button" value="Update "  class="light-btn" onclick="validate()"/>
				<input type="reset" name="c_btnDeletePolicy" value="Cancel" class="light-btn" 
				onclick="javascript:location.href='<%=basePath%>/viewNASServicePolicyDetail.do?nasPolicyId=<%=form.getNasPolicyId()%>'"/>
			</td>
		</tr>
		</table>
		</td>
    </tr>
    <tr>
		<td class="small-gap" colspan="3">&nbsp;</td>
   </tr>
   </html:form>
   <tr>
   	<td>
   		<div id="driverPopup" style="display: none;" title="Add Drivers">
			<table id="addDriver" name="addDriver" cellpadding="0" cellspacing="0" width="100%" class="box"> 	
			</table>
		</div>
   	</td>
   </tr>
   <tr>
   <td>
   		</td>
   </tr>
</table>
  
</div>	
<div id="additionalDriverPopup" style="display: none;"  title="Add Additional Drivers">
	<table id="additionalDriver" name="additionalDriver" cellpadding="0" cellspacing="0" width="100%" class="box"> 	
	</table>
</div>

<!-- Pre Plugin Mapppings -->
<table class="pre-plugin-mapping-template" style="display:none">
	<tr>
		<td class="tblfirstcol" width="47.5%">
			<input type="text" name="prePluginName" class="noborder" style="width:100%;" onfocus="setAutocompletePluginData(this,pluginList);"/>
		</td>	
		<td class="tblrows" width="47.5%">
			<textarea name="prePluginArgument" rows="1" cols="1" style="width:100%;height: 19px;" class="noborder"></textarea>
		</td>	
		<td class="tblrows" width="5%" align="center">
			<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
		</td>	
	</tr>
</table>

<!-- Post Plugin Mapppings -->
<table class="post-plugin-mapping-template" style="display:none">
	<tr>
		<td class="tblfirstcol" width="47.5%">
			<input type="text" name="postPluginName" class="noborder" style="width:100%;" onfocus="setAutocompletePluginData(this,pluginList);"/>
		</td>	
		<td class="tblrows" width="47.5%">
			<textarea name="postPluginArgument" rows="1" cols="1" style="width:100%;height: 19px;" class="noborder"></textarea>
		</td>	
		<td class="tblrows" width="5%" align="center">
			<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
		</td>	
	</tr>
</table>