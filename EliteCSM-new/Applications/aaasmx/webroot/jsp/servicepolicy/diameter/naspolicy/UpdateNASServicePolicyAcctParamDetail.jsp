<%@page import="com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData"%>
<%@page import="com.elitecore.elitesm.util.constants.PolicyPluginConstants"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.elitesm.util.EliteUtility" %> 
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>
<%@page import="java.util.List"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ServiceTypeConstants"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData" %>
<%@page import="com.elitecore.commons.base.Collectionz"%>
<%

	UpdateNASServicePolicyAccountingParamsForm form = (UpdateNASServicePolicyAccountingParamsForm)request.getAttribute("acctParmaForm");

	DriverBLManager driverManager = new DriverBLManager();
	List<DriverInstanceData> listOfDriver = driverManager.getDriverInstanceList(ServiceTypeConstants.NAS_ACCT_APPLICATION);
	
	
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
<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.naspolicy.forms.UpdateNASServicePolicyAccountingParamsForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servicepolicy.diameter.naspolicy.data.NASPolicyAcctDriverRelData"%>
<script language="javascript" src="<%=request.getContextPath()%>/js/diameter/diameter-policy.js"></script>
<script>
function validate()
{	
	selectAll(selecteddriverIds);
	if(selecteddriverIds.options.length==0){
		alert('Accounting Driver must be specified');
		return;
	}
	
	if(!validatePlugins('acct-pre-plugin-mapping-table','acct-post-plugin-mapping-table','acctPrePluginJson','acctPostPluginJson')){
		return;
	}
	
	document.forms[0].action.value="update";
	document.forms[0].submit();
}

function selectAll(selObj){
	if(selObj!=null){
		for(var i=0;i<selObj.options.length;i++){
			selObj.options[i].selected = true;
		}
	}
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
	
	jdriverNames.length = <%=listOfDriver.size()%>;				
	jdriverInstanceIds.length= <%=listOfDriver.size()%>;
		
	/* Get plugin list*/
	<%for(PluginInstData pluginInstData : pluginInstDataList){%>
		pluginList.push({"id":"<%=pluginInstData.getPluginInstanceId()%>","value":"<%=pluginInstData.getPluginTypeName()%>","label":"<%=pluginInstData.getName()%>"});
	<%}%>
	
	/* Script Autocomplete */
	setSuggestionForScript(driverScriptList, "scriptInstAutocomplete");
	
	<%int j,k=0;
	for(j =0;j<listOfDriver.size();j++){%>		
		jdriverNames[<%=j%>] = new Array(3);		
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
	hideSelectedData('selecteddriverIds','driverDataCheckBoxId');
   }
);

function driverpopup(){
	openpopup('driverPopup','driverDataCheckBoxId',jdriverInstanceIds,jdriverNames,'selecteddriverIds');
 }
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<html:form action="/updateNASServicePolicyAccountingParams">
<html:hidden name="updateNASServicePolicyAcctParamForm" styleId="nasPolicyId" property="nasPolicyId" />
<html:hidden name="updateNASServicePolicyAcctParamForm" styleId="action" property="action" />  
<html:hidden name="updateNASServicePolicyAcctParamForm" styleId="auditUId" property="auditUId" />   
<html:hidden name="updateNASServicePolicyAcctParamForm" styleId="name" property="name" />    
<html:hidden name="updateNASServicePolicyAcctParamForm" property="acctPrePluginJson"  styleId="acctPrePluginJson" />
<html:hidden name="updateNASServicePolicyAcctParamForm" property="acctPostPluginJson" styleId="acctPostPluginJson" />
    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
									<tr>
										<td align="left" class="tblheader-bold" valign="top"  width="10%" colspan="3">
											Other Details
										</td>
									</tr>
									<tr>
										<td width="30%" align="left" class="captiontext" valign="top">
											Driver Details	
											<ec:elitehelp headerBundle="servicePolicyProperties" text="naspolicyacct.drivers" header="Driver Group"/>
										</td>
										<td  width="28%"  align="left" class="labeltext" valign="top">
										<html:select property="selecteddriverIds" multiple="true" styleId="selecteddriverIds" size="5" style="width: 250;">
											
												<%
													
												List<NASPolicyAcctDriverRelData> driverList = form.getDriversList();												
												if(driverList != null){
													for(int i = 0;i<driverList.size();i++){
																										
														NASPolicyAcctDriverRelData data = driverList.get(i);
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
										
										<td align="left" class="labeltext" valign="top" width="42%">
										   <input type="button" value="Add " onClick="driverpopup()"  class="light-btn"  style="width: 75px"/><br/>
										   <br/>
										   <input type="button" value="Remove "  onclick="removeData('selecteddriverIds','driverDataCheckBoxId');" class="light-btn" style="width: 75px"/>
										</td>
									</tr>
									
									<tr>
									<td class="captiontext" valign="top" width="25%">
										<bean:message bundle="servicePolicyProperties" key="servicepolicy.naspolicy.script" />
										<ec:elitehelp headerBundle="servicePolicyProperties" text="servicepolicy.naspolicy.script" header="servicepolicy.naspolicy.script"/>
									</td>
									<td class="labeltext" valign="top" nowrap="nowrap" colspan="2">
										<html:text property="acctScript" size="30" style="width:250px" maxlength="255" styleClass="scriptInstAutocomplete"></html:text>
									</td>
									</tr>
									<tr>
										<td class="tblheader-bold" colspan="3">
											<bean:message bundle="servicePolicyProperties" key="servicepolicy.preplugins" /> 
										</td>
									</tr>
									<tr>
										<td colspan="3" class="captiontext" style="padding: 10px;" align="left">
											<table id="prePluginTbl" class="prePluginTbl" cellspacing="0" cellpadding="0" width="70%">
												<tr>
													<td class="captiontext" valign="top" colspan="2">
														<input type="button" value="Add Plugin" onClick="addPluginMapping('acct-pre-plugin-mapping-table','acct-pre-plugin-mapping-template');" class="light-btn" tabindex="2" /><br />
													</td>
												</tr>
												<tr>
													<td  class="captiontext" valign="top">
														<table cellspacing="0" cellpadding="0" border="0" width="100%" id="acct-pre-plugin-mapping-table" class="acct-pre-plugin-mapping-table">
															<tr>
																<td class="tbl-header-bold tbl-header-bg" width="47.5%"">
																	<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.pluginname" />
																	<ec:elitehelp   header="radiusservicepolicy.pluginname" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginname" ></ec:elitehelp>
																</td>	
																<td class="tbl-header-bold tbl-header-bg" width="47.5%"">
																	<bean:message bundle="servicePolicyProperties" key="radiusservicepolicy.plugin.pluginarguments" />
																	<ec:elitehelp   header="radiusservicepolicy.pluginArg" headerBundle="servicePolicyProperties" text="radiusservicepolicy.pluginArg" ></ec:elitehelp>
																</td>	
																<td class="tbl-header-bold tbl-header-bg" width="5%"">
																	Remove
																</td>	
															</tr>
															<logic:iterate id="obj" name="updateNASServicePolicyAcctParamForm" property="nasPolicyAcctPluginConfigList">
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
															<logic:iterate id="obj" name="updateNASServicePolicyAcctParamForm" property="nasPolicyAcctPluginConfigList">
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
										<td class="btns-td" valign="middle" colspan="3">
											<input type="button" value="Update "  class="light-btn" onclick="validate()"/>
											<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/viewNASServicePolicyDetail.do?nasPolicyId=<%=form.getNasPolicyId()%>'" value="Cancel" class="light-btn"/>
												
										</td>
									</tr>
		</table>
		</td>
    </tr>
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
   </tr>
   <tr>
   	<td>
   		<div id="driverPopup" style="display: none;" title="Add Drivers">
		<table id="addDriver" name="addDriver" cellpadding="0" cellspacing="0" width="100%" class="box"> 	
		</table>
	</div>	
   	</td>
   </tr>
</table>

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

</html:form>


