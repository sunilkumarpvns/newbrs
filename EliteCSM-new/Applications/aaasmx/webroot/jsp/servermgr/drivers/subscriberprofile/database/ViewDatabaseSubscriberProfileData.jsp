<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatasourceSchemaData"%>
<%@page import="com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.ViewDatabaseSubscriberProfileDataForm"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DBSubscriberProfileParamPoolValueData"%>
<%@page import="com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.SQLPoolValueData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatabaseSubscriberProfileRecordBean"%>
<%@ page import="java.util.*"%>

<% 
	String localBasePath=request.getContextPath();
	ViewDatabaseSubscriberProfileDataForm  viewDatabaseSubscriberProfileDataForm = (ViewDatabaseSubscriberProfileDataForm) request.getAttribute("viewDatabaseSubscriberProfileDataForm");
	List listDatabaseDatasourceRecord = (List)request.getAttribute("lstDataRecord");
	List lstEqualFieldName = (List)request.getAttribute("lstEqualFieldName");
	List valuePoolData = viewDatabaseSubscriberProfileDataForm.getParamPoolValue();	
	List<String> uniqueKeyList = viewDatabaseSubscriberProfileDataForm.getUniqueKeyList();
	int index = 0;
	int j = 0;		
%>
<script>

function duplicate(){

	  $.fx.speeds._default = 1000;
		document.getElementById("popupContact").style.visibility = "visible";
		//set value
				
		$( "#popupContact" ).dialog({
			modal: true,
			autoOpen: false,		
			height: "auto",
			width: 500,		
			buttons:{					
            'Duplicate': function() {
					var userId = "feee";
					if(isNull(userId)){
                        alert('Please Enter User Identity');
					}else{
						document.viewDatabaseSubscriberProfileDataForm.action.value = "duplicate";
						var form = document.forms[0];
						<%
							for(int i=0;i<uniqueKeyList.size();i++) {
						%>	
							var attridelement = document.createElement("input");
							attridelement.type = "hidden";
							attridelement.name = '<%=uniqueKeyList.get(i)%>';
							attridelement.value = document.getElementById('<%=uniqueKeyList.get(i)%>').value;
							form.appendChild(attridelement);
						<%}%>
						
						document.viewDatabaseSubscriberProfileDataForm.submit();
					}
            },                
		    Cancel: function() {
            	$("#uid").val('');
            	$(this).dialog('close');
        	}
	        },
    	open: function() {
	        	
    	},
    	close: function() {
    		$("#uid").val('');
    	}				
		});
		$( "#popupContact" ).dialog("open");
}


function validateUpdate(actionType){
	<%for(int i=1;i<listDatabaseDatasourceRecord.size();i++){ %>
		var isNulleble=document.getElementById('nullableValue[<%=i%>]');
		if(isNulleble.value==0){
			var field=document.getElementById('fieldValue[<%=i%>]');
			if(isNull(field.value)){	
				alert("Enter Mandatory Field");
				field.focus();
				return false; 
			}	
		}
	<%}%>

	$(document).ready(function() {
    	$('input[type=text]').each(function() {
              $(this).val(jQuery.trim($(this).val()));
        });
    });
	
	if(actionType == "edit") {	  
    	document.forms[0].action.value = 'update';
	}else if(actionType == "duplicate") {
		document.forms[0].action.value = 'duplicate';
	}
   	document.forms[0].submit();
}	
</script>
<html:form action="/updateDatabaseSubscriberProfileData">
	<html:hidden name="viewDatabaseSubscriberProfileDataForm" styleId="action" property="action" />
	<html:hidden name="viewDatabaseSubscriberProfileDataForm" styleId="dbAuthId" property="dbAuthId" />
	<html:hidden name="viewDatabaseSubscriberProfileDataForm" styleId="driverInstanceId" property="driverInstanceId" />
	<html:hidden name="viewDatabaseSubscriberProfileDataForm" styleId="strFieldName" property="strFieldName" />
	<html:hidden name="viewDatabaseSubscriberProfileDataForm" styleId="strFieldId" property="strFieldId" />

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" height="15%">
					<tr>
						<% 
							if(viewDatabaseSubscriberProfileDataForm.getStrType().equalsIgnoreCase("edit")) {
						%>
							<td align="left" class="tblheader-bold" valign="top" colspan="5">
								<bean:message bundle="driverResources" key="subscriberprofile.database.viewdatabasedatadetail" />
							</td>
						<% 
							} else {
						%>
							<td align="left" class="tblheader-bold" valign="top" colspan="5">
								<bean:message bundle="driverResources"
									key="subscriberprofile.database.duplicatedatabasedatadetail" />
							</td>
						<%}%>
					</tr>
					<tr>
						<td align="center" class="tblheader" valign="top" width="5%">
							<bean:message bundle="driverResources" key="subscriberprofile.database.serialnumber" />
						</td>
						<td align="left" class="tblheader" valign="top" width="30%">
							<bean:message bundle="driverResources" key="subscriberprofile.database.fieldname" />
						</td>
						<td align="left" class="tblheader" valign="top" width="67%">
							&nbsp;<bean:message bundle="driverResources" key="subscriberprofile.database.fieldvalue" />
						</td>
					</tr>


					<%	if(listDatabaseDatasourceRecord.size() >0 ) {
							for (int k=0;k<listDatabaseDatasourceRecord.size();k++ ) { 
								String strFieldId = "fieldId[" + k + "]";
								String strFieldValue = "fieldValue[" + k + "]";
								String strFieldName = "fieldName[" + k + "]";										
								String strFieldType = "fieldType[" + k + "]";		
								String strNullableValue = "nullableValue[" + k + "]";	
								if(k==0){
					%>
									<html:hidden name="viewDatabaseSubscriberProfileDataForm" styleId="<%=strFieldName%>" property="<%=strFieldName%>" />
									<html:hidden name="viewDatabaseSubscriberProfileDataForm" styleId="<%=strFieldId%>" property="<%=strFieldId%>" />
									<html:hidden name="viewDatabaseSubscriberProfileDataForm" styleId="<%=strNullableValue%>" property="<%=strNullableValue%>" />
					<%	
								}else{				
					%>
									<tr>
										<html:hidden name="viewDatabaseSubscriberProfileDataForm" styleId="<%=strFieldName%>" property="<%=strFieldName%>" />
										<html:hidden name="viewDatabaseSubscriberProfileDataForm" styleId="<%=strFieldId%>" property="<%=strFieldId%>" />
										<html:hidden name="viewDatabaseSubscriberProfileDataForm" styleId="<%=strNullableValue%>" property="<%=strNullableValue%>" />
										
										<td align="center" class="tblleftlinerows" width="5%"><%=(index + 1)%></td>
				
										<td align="left" valign="top" width="30%" class="tblleftlinerows">
											<bean:write name="viewDatabaseSubscriberProfileDataForm" property="<%=strFieldName %>" />
										</td>
										<td align="left" valign="top" width="30%" class="tblleftlinerows">
											&nbsp; 
											<%
												IDatabaseSubscriberProfileRecordBean toPrint1 = (IDatabaseSubscriberProfileRecordBean)listDatabaseDatasourceRecord.get(k);
												Iterator i = valuePoolData.iterator();
												Set abc = null;
												List lstPoolValueFromQuery = null;
												while(i.hasNext()){
												   	IDatasourceSchemaData prin1 = (DatasourceSchemaData)i.next();
												   	if(prin1.getFieldName().equalsIgnoreCase(toPrint1.getFieldName())){
												   		lstPoolValueFromQuery = prin1.getLstSQLPoolValue();
												   		abc = prin1.getDbdsParamPoolValueSet();
												   	}
												  }
												 if(lstPoolValueFromQuery !=null && lstPoolValueFromQuery.size()>0){
											%> 
											<html:select name="viewDatabaseSubscriberProfileDataForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>">
												<% 
													Iterator it = lstPoolValueFromQuery.iterator();
													while (it.hasNext()) {
													   SQLPoolValueData sqlPoolValueData = (SQLPoolValueData)it.next();
												%>
												<html:option value="<%=sqlPoolValueData.getPoolValue()%>"><%=sqlPoolValueData.getName()%></html:option>
												<%   }   %>
											</html:select> 
											<%   }else if(abc != null && abc.size()>0){  %> 
											<html:select name="viewDatabaseSubscriberProfileDataForm" styleId="<%=strFieldValue%>" property="<%=strFieldValue%>">
												<% 
													Iterator it = abc.iterator();
													while (it.hasNext()) {
													   DBSubscriberProfileParamPoolValueData dbdsParamPoolValueData = (DBSubscriberProfileParamPoolValueData)it.next();
												%>
												<html:option value="<%=dbdsParamPoolValueData.getValue()%>"><%=dbdsParamPoolValueData.getName()%></html:option>
												<%  }   %>
											</html:select> 
											<% 	}else{    %> 
												<bean:write name="viewDatabaseSubscriberProfileDataForm" property="<%=strFieldValue%>" />
											<%  }    
												int nullableValue=viewDatabaseSubscriberProfileDataForm.getNullableValue(k);
												String columnTypeName= viewDatabaseSubscriberProfileDataForm.getColumnTypeName(k);
												if(nullableValue == 0){ 
											%> 		
											<%   }
												if(columnTypeName.equalsIgnoreCase("DATE")  || columnTypeName.equalsIgnoreCase("TIMESTAMP")) { 
											%> 
													<font >[<%=viewDatabaseSubscriberProfileDataForm.getDateFormat()%>]</font> 
											<%   }	 %>
										</td>
									</tr>
								<%
									index++;
									}
								} // End OF for loop
							}// end of if condition								
					%>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						 <td class="btns-td" valign="middle" colspan="3" style="padding-left: 260px;">
							<input type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=localBasePath%>/searchDatabaseSubscriberProfileData.do?action=search&driverInstanceId=<%=viewDatabaseSubscriberProfileDataForm.getDriverInstanceId()%>&dbAuthId=<%=viewDatabaseSubscriberProfileDataForm.getDbAuthId()%>'" value="Back" class="light-btn">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<div id="popupContact" style="display: none;"
		title="Add Duplicate Subscriber Profile Detail">
		<%if(uniqueKeyList != null && uniqueKeyList.size() >0){%>
		<table>
			<%for(int i=0;i<uniqueKeyList.size();i++){%>
			<tr>
				<td align="left" class="labeltext" valign="top" width="30%"><%=uniqueKeyList.get(i)%>
				</td>
				<td align="left" class="labeltext" valign="top" width="70%"><input
					type="text" id='<%=uniqueKeyList.get(i)%>' size="25" maxlength="50"
					style="width: 200px" name='<%=uniqueKeyList.get(i)%>' /> <font
					color="#FF0000">*</font></td>
			</tr>

			<%}%>
		</table>
		<%}%>
	</div>
</html:form>


