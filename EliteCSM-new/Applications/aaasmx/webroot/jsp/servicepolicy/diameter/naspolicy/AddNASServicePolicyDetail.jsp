<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.AddNASServicePolicyForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>
<%@page import="com.elitecore.commons.base.Collectionz"%>

<%

	AddNASServicePolicyForm addNASServicePolicyForm = (AddNASServicePolicyForm)request.getSession().getAttribute("addNASServicePolicyForm");
	String basePath = request.getContextPath();
	String actionStr = "Create";
	
	List<PluginInstData> pluginInstDataList = (List<PluginInstData>) request.getAttribute("pluginInstDataList");
	
	String[] nasAuthDriverInstanceIds = (String[])request.getAttribute("nasAuthDriverInstanceIds");
	String[][] nasAuthDriverInstanceNames = (String[][])request.getAttribute("nasAuthDriverInstanceNames");
	List<DriverInstanceData> nasAuthDriverList = (List)request.getAttribute("nasAuthDriverList");
	
	String[] nasAcctDriverInstanceIds = (String[])request.getAttribute("nasAcctDriverInstanceIds");
	String[][] nasAcctDriverInstanceNames = (String[][])request.getAttribute("nasAcctDriverInstanceNames");
	List<DriverInstanceData> nasAcctDriverList = (List)request.getAttribute("nasAcctDriverList");
%>
<script language="javascript" src="<%=request.getContextPath()%>/js/diameter/diameter-policy.js"></script>
<script>
var jdriverNames = new Array();
var jdriverInstanceIds = new Array();
var count=0;
var driverScriptList = [];

<% 
	if( Collectionz.isNullOrEmpty(addNASServicePolicyForm.getDriverScriptList()) == false ){
		for( ScriptInstanceData scriptInstData : addNASServicePolicyForm.getDriverScriptList()){ %>
			driverScriptList.push({'value':'<%=scriptInstData.getName()%>','label':'<%=scriptInstData.getName()%>'});
		<%}
	}
%>

function nasAuthDriverPopup() {
	jdriverNames.length = <%=nasAuthDriverList.size()%>;				
	jdriverInstanceIds.length= <%=nasAuthDriverList.size()%>;
		
	
	<%int j,k=0;
	for(j =0;j<nasAuthDriverList.size();j++){%>		
		jdriverNames[<%=j%>] = new Array(2);		
			<%for(k=0;k<3;k++){%>												
				jdriverNames[<%=j%>][<%=k%>] = '<%=nasAuthDriverInstanceNames[j][k]%>'				
			<%}%>
		jdriverInstanceIds[<%=j%>] = '<%=nasAuthDriverInstanceIds[j]%>'	
		count ++;							
	<%}%>	 	 

	var headersArr = new Array(5);
	headersArr[0] = '';
	headersArr[1] = 'Driver Name';
	headersArr[2] = 'Driver Description';
	headersArr[3] = 'Driver Type';
	headersArr[4] = 'Weightage';
	
	initializeData(jdriverInstanceIds,jdriverNames,'authDriver','nasAuthCheckBoxId',headersArr,'true',count);
}


function nasAuthpopup(){
	openpopup('authDriverPopup','nasAuthCheckBoxId',jdriverInstanceIds,jdriverNames,'nasAuthDrivers');	
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

function additionalDriverSetUp(){
	var additionalHeadersArr = new Array(4);
	additionalHeadersArr[0] = '';
	additionalHeadersArr[1] = 'Additional Driver Name';
	additionalHeadersArr[2] = 'Driver Description';
	additionalHeadersArr[3] = 'Driver Type';
	initializeAdditionalData(jdriverInstanceIds,jdriverNames,'additionalDriver','additionalDriverCheckBoxId',additionalHeadersArr,jdriverInstanceIds.length);
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
                               		                                                        
                           	$("#row"+"nasAuthCheckBoxId"+listOfIds[0]).hide();
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

	                               	$("#row"+"nasAuthCheckBoxId"+listOfIds[i]).hide();
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
	     $("#row"+"nasAuthCheckBoxId"+mainValue).show();
	     $("#additionalrow"+checkboxid+mainValue).show();
	     $(this).remove();				      
 });
} 

var jacctDriverNames = new Array();
var jacctDriverInstanceIds = new Array();
var count1=0;

function nasAcctDriverPopup() {
	jacctDriverNames.length = <%=nasAcctDriverList.size()%>;				
	jacctDriverInstanceIds.length= <%=nasAcctDriverList.size()%>;
		
	
	<%int l,m=0;
	for(l =0;l<nasAcctDriverList.size();l++){%>		
	jacctDriverNames[<%=l%>] = new Array(3);		
			<%for(m=0;m<3;m++){%>												
			jacctDriverNames[<%=l%>][<%=m%>] = '<%=nasAcctDriverInstanceNames[l][m]%>'				
			<%}%>
			jacctDriverInstanceIds[<%=l%>] = '<%=nasAcctDriverInstanceIds[l]%>'	
		count1 ++;							
	<%}%>	 	 

	var headersArr = new Array(5);
	headersArr[0] = '';
	headersArr[1] = 'Driver Name';
	headersArr[2] = 'Driver Description';
	headersArr[3] = 'Driver Type';
	headersArr[4] = 'Weightage';
	
	initializeData(jacctDriverInstanceIds,jacctDriverNames,'acctDriver','nasAcctCheckBoxId',headersArr,'true',count1);
}

function nasAcctpopup(){
	openpopup('acctDriverPopup','nasAcctCheckBoxId',jacctDriverInstanceIds,jacctDriverNames,'nasAcctDrivers');	
}

var currentList;
var nasAuthDriverList;
var nasAcctDriverList;

var mypopupwindow;
function popup(mylink, windowname,current)
{
	currentList = current;
	if (! window.focus)return true;
		var href;
	if (typeof(mylink) == 'string')
		href=mylink;
	else
		href=mylink.href;
					
	mypopupwindow = window.open(href, windowname, 'width=900,height=300,left=150,top=100,scrollbars=yes');
	return false;
} 
function addItemInDriverCurrentList(drivers, driverNameArray,wieghtArray){
	
	driverLocalList =currentList;
	var existLength = driverLocalList.length
	var currentLocation=existLength;
	for(var i = 0; i < drivers.length; i++) {
		
		if(drivers[i].checked == true){
			var nameValue  = driverNameArray[i] + "-W-" + wieghtArray[i];
			var keyValue =  drivers[i].value + "-" +  wieghtArray[i];			
			driverLocalList.options[currentLocation] = new Option(nameValue,keyValue);
			currentLocation++;
		}
	}
	
}
function removeFromList(driverList){
var size = driverList.options.length;
for(var i = size - 1; i >= 0; i--) {
if ((driverList.options[i] != null) && (driverList.options[i].selected == true)) {
	driverList.options[i] = null;
      }
   }
}
function selectAll(selObj){
	for(var i=0;i<selObj.options.length;i++){
		selObj.options[i].selected = true;
	}
}
function validate(){
	//selectAll(mainDriverList);
	selectAll(nasAuthDriverList);
	selectAll(nasAcctDriverList);
	selectAll(nasAdditionalDriverList);
		
	if(nasAuthDriverList.options.length==0){
		alert('Authentication Driver must be specified');
		return;
	}
	if(nasAcctDriverList.options.length==0){
		alert('Accounting Driver must be specified');
		return;
	}
	
	/* Validating Auth Plugins if configured */
	if(!validatePlugins('pre-plugin-mapping-table','post-plugin-mapping-table','authPrePluginJson','authPostPluginJson')){
		return;
	}
	
	/* Validating Acct Plugins if configured */
	if(!validatePlugins('acct-pre-plugin-mapping-table','acct-post-plugin-mapping-table','acctPrePluginJson','acctPostPluginJson')){
		return;
	}
	
	document.forms[0].submit();
}

function uncheckAllCheckBoxes(pluginNames){
	$("input:checkbox[name='"+pluginNames+"']").each(function(){
		if($(this).attr('checked')){
			$(this).attr('checked',false);
		}
	});
}

var pluginList = [];

$(document).ready(function(){
	/* Get plugin list*/
	<%for(PluginInstData pluginInstData : pluginInstDataList){%>
		pluginList.push({"id":"<%=pluginInstData.getPluginInstanceId()%>","value":"<%=pluginInstData.getPluginTypeName()%>","label":"<%=pluginInstData.getName()%>"});
	<%}%>
	
	 /* Script Autocomplete */
	 setSuggestionForScript(driverScriptList, "scriptInstAutocomplete");
});
</script>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<html:form action="/addNASServicePolicyDetail">

	<html:hidden property="authPrePluginJson"  styleId="authPrePluginJson" />
	<html:hidden property="authPostPluginJson" styleId="authPostPluginJson" />
	<html:hidden property="acctPrePluginJson"  styleId="acctPrePluginJson" />
	<html:hidden property="acctPostPluginJson" styleId="acctPostPluginJson" />
	
	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="box" cellpadding="0" cellspacing="0" border="0"
							width="100%">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header" colspan="6">Authentication Detail</td>

								</tr>

								<tr>
									<td width="20%" align="left" class="captiontext padding_top" valign="top" >
										Driver Group 
										<ec:elitehelp headerBundle="servicePolicyProperties" text="naspolicy.drivers" header="Driver Group"/>
									</td>
									<td align="left" class="labeltext padding_top" valign="top"
										style="width: 270;"><select class="labeltext"
										name="nasAuthDrivers" id="nasAuthDrivers" multiple="true"
										size="5" style="width: 250;" tabindex="1">
									</select><font color="#FF0000"> *</font></td>

									<td align="left" class="labeltext padding_top" valign="top"><input
										type="button" value="Add " onClick="nasAuthpopup();"
										class="light-btn" style="width: 75px" tabindex="2" /><br />
										<br /> <input type="button" value="Remove "
										onclick="removeData('nasAuthDrivers','nasAuthCheckBoxId')"
										class="light-btn" style="width: 75px" tabindex="3" /></td>
								</tr>
								

								<tr>
									<td width="20%" align="left" class="captiontext" valign="top">
										<bean:message bundle="servicePolicyProperties"	key="servicepolicy.additionalgroup" />
										<ec:elitehelp headerBundle="servicePolicyProperties" text="naspolicy.drivers" header="Additional Group"/>
									</td>
									<td align="left" class="labeltext" valign="top"
										style="width: 270;"><select class="labeltext"
										name="selectedAdditionalDriverIds"
										id="selectedAdditionalDriverIds" multiple="true" size="5"
										style="width: 250;" tabindex="4">
									</select></td>


									<td align="left" class="labeltext" valign="top"><input
										type="button" value="Add " onClick="additionalDriverpopup();"
										class="light-btn" style="width: 75px" tabindex="5" /><br />
										<br /> <input type="button" value="Remove "
										onclick="removeAdditionalDriverData('selectedAdditionalDriverIds','additionalDriverCheckBoxId')"
										class="light-btn" style="width: 75px" tabindex="6" /></td>
								</tr>
								
								<tr>
									<td class="captiontext" valign="top" width="25%">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.script" />
										<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.script" header="servicepolicy.naspolicy.script"/>
									</td>
									<td class="labeltext" valign="top" nowrap="nowrap" colspan="2">
										<html:text property="authScript" size="30" style="width:250px" maxlength="255" tabindex="7" styleClass="scriptInstAutocomplete"></html:text>
									</td>
								</tr>
								<tr>
									<td class="tblheader-bold captiontext" colspan="3">
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
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td class="tblheader-bold captiontext" colspan="3" style="padding-left: 20px;padding-right: 20px;">
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
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td class="table-header" colspan="5" style="border-top: 1px solid #CCC;">Accounting Detail</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td width="20%" align="left" class="captiontext" valign="top">
										Driver Group 
										<ec:elitehelp headerBundle="servicePolicyProperties" text="naspolicyacct.drivers" header="Driver Group"/>
									</td>
									<td align="left" class="labeltext" valign="top"><select
										class="labeltext" name="nasAcctDrivers" id="nasAcctDrivers"
										multiple="true" size="5" style="width: 250;" tabindex="12">
									</select><font color="#FF0000"> *</font></td>

									<td align="left" class="labeltext" valign="top"><input
										type="button" value="Add " onClick="nasAcctpopup();"
										class="light-btn" style="width: 75px" tabindex="13" /><br />
										<br /> <input type="button" value="Remove "
										onclick="removeData('nasAcctDrivers','nasAcctCheckBoxId')"
										class="light-btn" style="width: 75px" tabindex="14" /></td>
								</tr>
								
								
								<tr>
									<td class="captiontext" valign="top" width="25%">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.script" />
										<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.script" header="servicepolicy.naspolicy.script"/>
									</td>
									<td class="labeltext" valign="top" nowrap="nowrap" colspan="2">
										<html:text property="acctScript" size="30" style="width:250px" maxlength="255" tabindex="15" styleClass="scriptInstAutocomplete"></html:text>
									</td>
								</tr>
								<tr>
									<td class="tblheader-bold captiontext" colspan="3">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.preplugins" /> 
									</td>
								</tr>
								<tr>
									<td colspan="3" class="captiontext" style="padding: 10px;" align="left">
										<table id="postPluginTbl" class="postPluginTbl" cellspacing="0" cellpadding="0" width="70%">
											<tr>
												<td class="captiontext" valign="top" colspan="2">
													<input type="button" value="Add Plugin" onClick="addPluginMapping('acct-pre-plugin-mapping-table','acct-pre-plugin-mapping-template');" class="light-btn" tabindex="2" /><br />
												</td>
											</tr>
											<tr>
												<td  class="captiontext" valign="top">
													<table cellspacing="0" cellpadding="0" border="0" width="100%" id="acct-pre-plugin-mapping-table" class="acct-pre-plugin-mapping-table">
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
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td class="tblheader-bold captiontext" colspan="3">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.postplugins" /> 
									</td>
								</tr>
								<tr>
									<td colspan="3" class="captiontext" style="padding: 10px;" align="left">
										<table id="postPluginTbl" class="postPluginTbl" cellspacing="0" cellpadding="0" width="70%">
											<tr>
												<td class="captiontext" valign="top" colspan="2">
													<input type="button" value="Add Plugin" onClick="addPluginMapping('acct-post-plugin-mapping-table','acct-post-plugin-mapping-template');" class="light-btn" tabindex="2" /><br />
												</td>
											</tr>
											<tr>
												<td  class="captiontext" valign="top">
													<table cellspacing="0" cellpadding="0" border="0" width="100%" id="acct-post-plugin-mapping-table" class="acct-post-plugin-mapping-table">
														<tr>
															<td class="tbl-header-bold tbl-header-bg" width="47.5%">
																<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.pluginname" />
																<ec:elitehelp   header="radiusservicepolicy.pluginname" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginname" ></ec:elitehelp>
															</td>	
															<td class="tbl-header-bold tbl-header-bg" width="47.5%">
																<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.plugin.pluginarguments" />
																<ec:elitehelp header="radiusservicepolicy.pluginArg" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginArg" ></ec:elitehelp>
															</td>	
															<td class="tbl-header-bold tbl-header-bg" width="5%">
																Remove
															</td>	
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>

								<tr>

									<td class="btns-td" valign="middle">&nbsp;</td>
									<td class="btns-td" valign="middle" colspan="2"><input
										type="button" value="Previous  "
										onclick="history.go(-1);return false;" class="light-btn"
										tabindex="20" /> <input type="button"
										value=" <%=actionStr%> " onclick="javascript:validate();"
										class="light-btn" tabindex="21" /> <input type="reset"
										name="c_btnDeletePolicy"
										onclick="javascript:location.href='<%=basePath%>/initSearchNASServicePolicy.do?/>'"
										value="Cancel" class="light-btn" tabindex="22"></td>
								</tr>


							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
	</table>
</html:form>

<!-- Mapping Table of Pre Plugin -->
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

<!-- Mapping Table of Post Plugin -->
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

<!-- Mapping Table of Pre Acct Plugin -->
<table class="acct-pre-plugin-mapping-template" style="display:none">
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

<!-- Mapping Table of Post Acct Plugin -->
<table class="acct-post-plugin-mapping-template" style="display:none">
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

<div id="authDriverPopup" style="display: none;"
	title="Add Nas Auth Drivers">
	<table id="authDriver" name="authDriver" cellpadding="0"
		cellspacing="0" width="100%" class="box">
	</table>
</div>
<div id="acctDriverPopup" style="display: none;"
	title="Add Nas Acct Drivers">
	<table id="acctDriver" name="acctDriver" cellpadding="0"
		cellspacing="0" width="100%" class="box">
	</table>
</div>
<div id="additionalDriverPopup" style="display: none;"
	title="Add Additional Drivers">
	<table id="additionalDriver" name="additionalDriver" cellpadding="0"
		cellspacing="0" width="100%" class="box">
	</table>
</div>
<script>  
 nasAuthDriverPopup();
 nasAcctDriverPopup();
 additionalDriverSetUp();
 nasAuthDriverList=document.getElementById("nasAuthDrivers");
 nasAcctDriverList=document.getElementById("nasAcctDrivers");
 nasAdditionalDriverList=document.getElementById("selectedAdditionalDriverIds");
 setTitle('<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy"/>');
  </script>
