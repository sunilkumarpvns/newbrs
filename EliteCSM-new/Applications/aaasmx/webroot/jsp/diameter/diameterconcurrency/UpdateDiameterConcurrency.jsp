<%@page import="com.elitecore.core.serverx.sessionx.FieldMapping"%>
<%@page import="com.elitecore.elitesm.datamanager.datasource.database.data.IDatabaseDSData"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page import="com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable.DBFailureActions" %>
<%@page import="com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable.MandateryFieldConstants"%>
<%@page import="com.elitecore.aaa.diameter.conf.DiameterConcurrencyConfigurable.SessionOverrideActions"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<% DiameterConcurrencyData diameterConcurrencyDataObj = (DiameterConcurrencyData)request.getAttribute("diameterConcurrencyData");
   List<IDatabaseDSData> lstdatasource = (List<IDatabaseDSData>) request.getAttribute("lstDatasource");
%>
<bean:define id="diameterConcurrencyDataBean" name="diameterConcurrencyData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyData" />
<script language="javascript" src="<%=request.getContextPath()%>/js/diameter/diameter-concurrency.js"></script>

<script>

var isValidName;

function validateName(val)
{
	var test1 = /(^[A-Za-z0-9-]*$)/;
	var regexp =new RegExp(test1);
	if(regexp.test(val)){
		return true;
	}
	return false;
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIAMETER_CONCURRENCY%>',searchName,'update','<%=diameterConcurrencyDataObj.getDiaConConfigId()%>','verifyNameDiv');
}

$(document).ready(function(){
	$('.additionalMappingTable td img.delete').live('click',function() {
		$(this).parent().parent().remove();
	});	
	
	$('.includeInASR').each(function(){
		if($(this).val() == 'true'){
			$(this).attr('checked',true);
		}else{
			$(this).attr('checked',false);
		}
	});
	setColumnsOnTextFields();
});

</script>

<html:form action="/updateDiameterConcurrency">
	<html:hidden name="diameterConcurrencyForm" styleId="action" property="action" value="update"/>
	<html:hidden name="diameterConcurrencyForm" styleId="diaConConfigId" property="diaConConfigId" />
	<html:hidden name="diameterConcurrencyForm" styleId="auditUId" property="auditUId" />
	<html:hidden name="diameterConcurrencyForm" styleId="lstFieldMapping" property="lstFieldMapping" />

	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%">
		<tr>
			<td height="30%" class="captiontext" valign="top">&nbsp;</td>
			<td height="70%" class="labeltext" valign="top">&nbsp;</td>
		</tr>
		<tr>
			<td width="30%" height="20%" class="captiontext" valign="top">
				<bean:message bundle="diameterResources"  key="diameter.diameterconcurrency.name" /> 
				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.name" header="diameter.diameterconcurrency.name"/>
			</td>
			<td width="70%" height="20%" class="labeltext" valign="top">
				<html:text styleId="name" tabindex="1" name="diameterConcurrencyForm" property="name" onblur="verifyName();" size="30" styleClass="flatfields" style="font-family: Verdana; width:250px " maxlength="30"  />
				<font color="#FF0000"> *</font>	
				<div id="verifyNameDiv" class="labeltext"></div>
			</td>
		</tr>
		<tr>
			<td height="30%" class="captiontext" valign="top">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.description" />
				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.description" header="diameter.diameterconcurrency.description"/>
			</td>
			<td height="70%" class="labeltext" valign="top">
				<html:textarea styleId="description" name="diameterConcurrencyForm" property="description" cols="50" rows="4" style="width:250px" tabindex="2" />
			</td>
		</tr>
		<tr>
			<td height="30%" align="left" class="captiontext" valign="top">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.datasource" />
				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.datasource" header="diameter.diameterconcurrency.datasource"/>
			</td>
			<td height="70%" align="left" class="labeltext" valign="top">
				<%if (lstdatasource == null) {%> 
					<bean:define id="lstDatasource"	name="diameterConcurrencyForm" property="lstDatasource"></bean:define> 
				<%} %> 
					<html:select name="diameterConcurrencyForm" styleId="databaseDsId" property="databaseDsId" size="1" style="width:250px" tabindex="4" onchange="setColumnsOnTextFields();">
						<html:option value="0">
							<bean:message bundle="sessionmanagerResources" key="sessionmanager.select" />
						</html:option>
						<html:options collection="lstDatasource" property="databaseId" labelProperty="name" /> 
					</html:select>
					<font color="#FF0000"> *</font>	
			</td>
		</tr>
		<tr>
			<td height="30%" class="captiontext" valign="top">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.tablename" />
				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.tablename" header="diameter.diameterconcurrency.tablename"/>
			</td>
			<td height="70%" class="labeltext" valign="top">
				<html:text styleId="tableName" name="diameterConcurrencyForm" property="tableName" style="width:250px" tabindex="5" maxlength="30" />
				<font color="#FF0000"> *</font>	
			</td>
		</tr>
		<tr>
			<td height="30%" class="captiontext" valign="top">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.starttime" />
				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.starttime" header="diameter.diameterconcurrency.starttime"/>
			</td>
			<td height="70%" class="labeltext" valign="top">
				<html:text styleId="startTimeField" name="diameterConcurrencyForm" property="startTimeField" styleClass="startTimeField" style="width:250px" tabindex="5" maxlength="30" />
				<font color="#FF0000"> *</font>	
			</td>
		</tr>
		<tr>
			<td height="30%" class="captiontext" valign="top">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.lastupdatetime" />
				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.lastupdatetime" header="diameter.diameterconcurrency.lastupdatetime"/>
			</td>
			<td height="70%" class="labeltext" valign="top">
				<html:text styleId="lastUpdateTimeField" name="diameterConcurrencyForm" property="lastUpdateTimeField" styleClass="lastUpdateTimeField" style="width:250px" tabindex="5" maxlength="30" />
				<font color="#FF0000"> *</font>	
			</td>
		</tr>
		<tr>
			<td height="30%" class="captiontext" valign="top">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.concurrencyidentityfield" />
				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.concurrencyidentityfield" header="diameter.diameterconcurrency.concurrencyidentityfield"/>
			</td>
			<td height="70%" class="labeltext" valign="top">
				<html:text styleId="concurrencyIdentityField" name="diameterConcurrencyForm" property="concurrencyIdentityField"  styleClass="concurrencyIdentityField" style="width:250px" tabindex="5" maxlength="30" />
				<font color="#FF0000"> *</font>	
			</td>
		</tr>
		<tr>
			<td height="30%" class="captiontext" valign="top">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.dbfailureaction" />
				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.dbfailureaction" header="diameter.diameterconcurrency.dbfailureaction"/>
			</td>
			<td height="70%" class="labeltext" valign="top">
				<html:select name="diameterConcurrencyForm" styleId="dbFailureAction" property="dbFailureAction" style="width:250px" tabindex="6" >
					<logic:iterate id="dbFailureActionInst"  collection="<%=DBFailureActions.values() %>"> 
						<%String displayText=((DBFailureActions)dbFailureActionInst).name(); %>
						<html:option value="<%=displayText%>"><%=displayText%></html:option>
					</logic:iterate>
				</html:select>
			</td>
		</tr>
		<tr>
			<td class="tblheader-bold" colspan="2">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.sessionoverideproperties" />
			</td>
		</tr>
		<tr>
			<td height="30%" class="captiontext" valign="top">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.action" />
				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.action" header="diameter.diameterconcurrency.action"/>
			</td>
			<td height="70%" class="labeltext" valign="top">
				<html:select name="diameterConcurrencyForm" styleId="sessionOverrideAction" property="sessionOverrideAction" style="width:250px" tabindex="6" >
					<logic:iterate id="sessionOverideActionInst"  collection="<%=SessionOverrideActions.values() %>"> 
						<%String valueText=((SessionOverrideActions)sessionOverideActionInst).value; %>
						<html:option value="<%=valueText%>"><%=((SessionOverrideActions)sessionOverideActionInst).value%></html:option>
					</logic:iterate>
				</html:select>
			</td>
		</tr>
		<tr>
			<td align="left" class="captiontext" valign="top">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.sessionoverridefields" />
				<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.sessionoverridefields" header="diameter.diameterconcurrency.sessionoverridefields"/>
			</td>
			<td align="left" class="labeltext" valign="top" colspan="2">
				<html:text styleId="sessionOverrideFields" property="sessionOverrideFields" maxlength="300" style="width:250px;"/>
			</td>
		</tr>
		<tr>
			<td class="tblheader-bold" colspan="2">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.mandatorydbfieldmapping" />
			</td>
		</tr>
		<tr>
			<td colspan="2" class="captiontext concurrency-mapping-tbl">
				<table cellspacing="0" cellpadding="0" border="0" width="97%" class="madatoryMappingsTable">
					<tr>
						<td class="tblheader" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.fields" />
							<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.fields" header="diameter.diameterconcurrency.fields"/>
						</td>
						<td class="tblheader" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.dbfieldname" />
							<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.dbfieldname" header="diameter.diameterconcurrency.dbfieldname"/>
						</td>
						<td class="tblheader" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconurrency.referringattribute" />
							<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconurrency.referringattribute" header="diameter.diameterconurrency.referringattribute"/>
						</td>
						<td class="tblheader" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.datatype" />
							<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.datatype" header="diameter.diameterconcurrency.datatype"/>
						</td>
						<td class="tblheader" width="17%">
							<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.defaultvalue" />
							<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.defaultvalue" header="diameter.diameterconcurrency.defaultvalue"/>
						</td>
						<td class="tblheader" width="12%" align="center">
							<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.includeinasr"/>
							<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.includeinasr" header="diameter.diameterconcurrency.includeinasr"/>
						</td>
					</tr>
					<logic:iterate id="obj" name="mandatoryFieldMappingsList" scope="request"  type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyFieldMapping">
						<tr>
							<td class="labeltext allborder" width="18%">
								<bean:write property="logicalField" name="obj" /> 
								<input type="hidden" value="<bean:write property="logicalField" name="obj" />" name="field"/>
							</td>
							<td class="tblrows" width="17%">
								<input name="dbFieldName"  class="dbFieldName concurrency-textbox" style="width:100%;" value="<bean:write property="dbFieldName" name="obj" />"/>
							</td>
							<td class="tblrows" width="17%">
								<input name="referringAttribute" class="referringAttribute concurrency-textbox" style="width:100%;" value="<bean:write property="referringAttribute" name="obj" />"/>
							</td>
							<td class="tblrows" width="17%">
								<input type="hidden" name="dataType" class="dataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" />
								<select disabled="disabled" class="concurrency-textbox" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width:100%;">
									<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
								</select>	
							</td>
							<td class="tblrows" width="16%">
								<input name="defaultValue" class="defaultValue concurrency-textbox" style="width:100%;" value="<bean:write property="defaultValue" name="obj" />"/>
							</td>
							<td class="labeltext tblrows" width="10%" align="center">
								<input type="checkbox" name="includeInASR" class="includeInASR" onclick="changeIncludeInASRValue(this);" value="<bean:write property="includeInASR" name="obj" />">
							</td>
						</tr> 	
					</logic:iterate> 
					</table>
			</td>
		</tr>
		<tr>
			<td class="tblheader-bold" colspan="2">
				<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.additionaldbfieldmapping" />
			</td>
		</tr>
		<tr>
			<td colspan="2" class="captiontext concurrency-mapping-tbl">
				<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td>
							<input type="button" onclick="addAdditionalMappingRow('additionalMappingTable','templateMappingTable');" value=" Add " class="light-btn broadcast-com-btn" style="size: 140px" tabindex="3"> 
						</td>
					</tr>
					<tr>
						<td>
							<table cellspacing="0" cellpadding="0" border="0" width="100%" id="additionalMappingTable" class="additionalMappingTable">
								<tr>
									<td class="tblheader" width="19%">
										<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.dbfieldname" />
										<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.dbfieldname" header="diameter.diameterconcurrency.dbfieldname"/>
									</td>
									<td class="tblheader" width="20%">
										<bean:message bundle="diameterResources" key="diameter.diameterconurrency.referringattribute" />
										<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconurrency.referringattribute" header="diameter.diameterconurrency.referringattribute"/>
									</td>
									<td class="tblheader" width="20%">
										<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.datatype" />
										<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.datatype" header="diameter.diameterconcurrency.datatype"/>
									</td>
									<td class="tblheader" width="20%">
										<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.defaultvalue" />
										<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.defaultvalue" header="diameter.diameterconcurrency.defaultvalue"/>
									</td>
									<td class="tblheader" width="15%" align="center">
										<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.includeinasr"/>
										<ec:elitehelp headerBundle="diameterResources" text="diameter.diameterconcurrency.includeinasr" header="diameter.diameterconcurrency.includeinasr"/>
									</td>
									<td class="tblheader" width="8%" align="center">
										<bean:message bundle="diameterResources" key="diameter.diameterconcurrency.remove"/>
									</td>
								</tr>
								<logic:iterate id="obj" name="additionalFieldMappingsList" scope="request"  type="com.elitecore.elitesm.datamanager.diameter.diameterconcurrency.data.DiameterConcurrencyFieldMapping">
								<tr>
									<td class="allborder" width="19%">
										<input name="dbFieldName"  class="dbFieldName concurrency-textbox" style="width:100%;" value="<bean:write property="dbFieldName" name="obj" />"/>
									</td>
									<td class="tblrows" width="20%">
										<input name="referringAttribute" class="referringAttribute concurrency-textbox" style="width:100%;" value="<bean:write property="referringAttribute" name="obj" />"/>
									</td>
									<td class="tblrows" width="20%">
										<input type="hidden" name="dataType" class="dataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" />
										<select disabled="disabled" class="concurrency-textbox" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width:100%;">
											<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
										</select>	
									</td>
									<td class="tblrows" width="20%">
										<input name="defaultValue" class="defaultValue concurrency-textbox" style="width:100%;" value="<bean:write property="defaultValue" name="obj" />"/>
									</td>
									<td class="labeltext tblrows" width="15%" align="center">
										<input type="checkbox" name="includeInASR" class="includeInASR" onclick="changeIncludeInASRValue(this);" value="<bean:write property="includeInASR" name="obj" />">
									</td>
									<td class="labeltext tblrows" width="15%" align="center">
										<img src='<%=request.getContextPath()%>/images/minus.jpg' class='delete' height='15' />&nbsp;
									</td>
								</tr>
								</logic:iterate>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td height="30%" class="labeltext">&nbsp;</td>
			<td height="70%" class="labeltext">
				<input type="button" tabindex="12" onclick="customValidate();" value="   Update   " class="light-btn">&nbsp;&nbsp; 
				<input type="reset" tabindex="13"  onclick="javascript:location.href='<%=basePath%>/viewDiameterConcurrency.do?diaConConfigId=<bean:write name="diameterConcurrencyDataBean" property="diaConConfigId"/>'" value=" Cancel " class="light-btn">
			</td>
		</tr>
	</table>
</html:form>
<table style="display: none;" id="templateMappingTable">
	<tr>
		<td class="allborder" width="19%">
			<input name="dbFieldName"  class="dbFieldName concurrency-textbox" style="width:100%;"/>
		</td>
		<td class="tblrows" width="20%">
			<input name="referringAttribute" class="referringAttribute concurrency-textbox" style="width:100%;"/>
		</td>
		<td class="tblrows" width="20%">
			<input type="hidden" name="dataType" class="dataType" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" />
			<select disabled="disabled" class="concurrency-textbox" value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>" style="width:100%;">
				<option value="<%=Integer.toString(FieldMapping.STRING_TYPE)%>">String</option>
			</select>	
		</td>
		<td class="tblrows" width="20%">
			<input name="defaultValue" class="defaultValue concurrency-textbox" style="width:100%;"/>
		</td>
		<td class="labeltext tblrows" width="15%" align="center">
			<input type="checkbox" name="includeInASR" class="includeInASR" value="false" onclick="changeIncludeInASRValue(this);">
		</td>
		<td class="labeltext tblrows" width="15%" align="center">
			<img src='<%=request.getContextPath()%>/images/minus.jpg' class='delete' height='15' />&nbsp;
		</td>
		
	</tr>
</table>