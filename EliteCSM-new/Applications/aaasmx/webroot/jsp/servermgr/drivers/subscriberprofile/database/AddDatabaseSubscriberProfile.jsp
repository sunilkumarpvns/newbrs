<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatasourceSchemaData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DBSubscriberProfileParamPoolValueData"%>
<%@page import="com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.SQLPoolValueData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatabaseSubscriberProfileRecordBean"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatabaseSubscriberProfileRecordBean"%>
<%@page import="com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.AddDatabaseSubscriberProfileDataForm"%>
<%@page import="java.util.*"%>

<%
		String localBasePath=request.getContextPath();
		AddDatabaseSubscriberProfileDataForm  addDatabaseSubscriberProfileDataForm = (AddDatabaseSubscriberProfileDataForm) request.getAttribute("addDatabaseSubscriberProfileDataForm");
		List listDatabaseDatasourceDataSchema = addDatabaseSubscriberProfileDataForm.getLstDatabaseDatasourceDataSchema();
		List valuePoolData = addDatabaseSubscriberProfileDataForm.getParamPoolValue();	
		int index = 0;
		int j = 0;			           
%>
<script>

function validateUpdate(){
	<%for(int i=1;i<listDatabaseDatasourceDataSchema.size();i++){ %>
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
    
    document.forms[0].action.value = 'addData';
   	document.forms[0].submit();
}	

</script>

<html:form action="/addDatabaseSubscriberProfileData" >
	<html:hidden name="addDatabaseSubscriberProfileDataForm"
		styleId="action" property="action" />
	<html:hidden name="addDatabaseSubscriberProfileDataForm"
		styleId="dbAuthId" property="dbAuthId" />
	<html:hidden name="addDatabaseSubscriberProfileDataForm"
		styleId="driverInstanceId" property="driverInstanceId" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td valign="top" align="right">
				<table cellpadding="0" cellspacing="0" border="0" width="100%"
					height="15%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="5">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.adddatabasedatasourcedatadetail" />
						</td>
					</tr>
					<tr>

						<td align="center" class="tblheader" valign="top" width="5%">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.serialnumber" />
						</td>
						<td align="left" class="tblheader" valign="top" width="30%">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.fieldname" />
						</td>

						<td align="left" class="tblheader" valign="top" width="67%">
							<bean:message bundle="driverResources"
								key="subscriberprofile.database.fieldvalue" />
						</td>


					</tr>

					<%	if(listDatabaseDatasourceDataSchema.size() >0 ) {%>

					<% 
								for (int k=0;k<listDatabaseDatasourceDataSchema.size();k++ ) { %>
					<%  
									    String strFieldId = "fieldId[" + k + "]";
										String strFieldValue = "fieldValue[" + k + "]";
										String strFieldName = "fieldName[" + k + "]";
										String strNullableValue = "nullableValue[" + k + "]";	
																		
									if(k==0){
									%>
					<html:hidden name="addDatabaseSubscriberProfileDataForm"
						styleId="<%=strFieldName%>" property="<%=strFieldName%>" />
					<html:hidden name="addDatabaseSubscriberProfileDataForm"
						styleId="<%=strFieldId%>" property="<%=strFieldId%>" />
					<html:hidden name="addDatabaseSubscriberProfileDataForm"
						styleId="<%=strNullableValue%>" property="<%=strNullableValue%>" />
					<%	
									}else{				
								    %>
					<tr>
						<html:hidden name="addDatabaseSubscriberProfileDataForm"
							styleId="<%=strFieldName%>" property="<%=strFieldName%>" />
						<html:hidden name="addDatabaseSubscriberProfileDataForm"
							styleId="<%=strFieldId%>" property="<%=strFieldId%>" />
						<html:hidden name="addDatabaseSubscriberProfileDataForm"
							styleId="<%=strNullableValue%>" property="<%=strNullableValue%>" />

						<td align="center" class="tblleftlinerows" width="5%"><%=(index + 1)%>
						</td>

						<td align="left" valign="top" width="30%" class="tblleftlinerows">
							<bean:write name="addDatabaseSubscriberProfileDataForm"
								property="<%=strFieldName %>" />
						</td>
						<td align="left" valign="top" width="67%" class="tblleftlinerows">
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <%
												IDatabaseSubscriberProfileRecordBean  toPrint1 = (DatabaseSubscriberProfileRecordBean)listDatabaseDatasourceDataSchema.get(k);
												System.out.println("Compare With The Other List"+toPrint1.getFieldName());
												
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
											%> <html:select name="addDatabaseSubscriberProfileDataForm"
								styleId="<%=strFieldValue%>" property="<%=strFieldValue%>">
								<% 
											 		Iterator it = lstPoolValueFromQuery.iterator();
											 		while (it.hasNext()) {
											 
											            		SQLPoolValueData sqlPoolValueData = (SQLPoolValueData)it.next();
											 		%>
								<html:option value="<%=sqlPoolValueData.getPoolValue()%>"><%=sqlPoolValueData.getName()%></html:option>

								<%
													 }
													%>
							</html:select> <%}else if(abc != null && abc.size()>0){
											 %> <html:select name="addDatabaseSubscriberProfileDataForm"
								styleId="<%=strFieldValue%>" property="<%=strFieldValue%>">
								<% 
											 		Iterator it = abc.iterator();
											 		while (it.hasNext()) {
											 
											            		DBSubscriberProfileParamPoolValueData dbdsParamPoolValueData = (DBSubscriberProfileParamPoolValueData)it.next();
											 		%>
								<html:option value="<%=dbdsParamPoolValueData.getValue()%>"><%=dbdsParamPoolValueData.getName()%></html:option>

								<%
													 }
													%>
							</html:select> <% 		
											 	}else{
											 %> <html:text name="addDatabaseSubscriberProfileDataForm"
								size="35" styleId="<%=strFieldValue%>"
								property="<%=strFieldValue%>" /> <%
											 }
											 %> <% 
												int nullableValue=addDatabaseSubscriberProfileDataForm.getNullableValue(k);
												String columnTypeName= addDatabaseSubscriberProfileDataForm.getColumnTypeName(k);
												if(nullableValue == 0){ %> <font color="#FF0000"> *</font> <% } 
											    if(columnTypeName.equalsIgnoreCase("DATE")  || columnTypeName.equalsIgnoreCase("TIMESTAMP")) {
											 %> <font color="#FF0000">[<%=addDatabaseSubscriberProfileDataForm.getDateFormat()%>]
						</font> <%
											 	}	      	
											 %>

						</td>
						<%index++;%>
					</tr>
					<%} } %>
					<% } %>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle"><input type="button"
							name="c_btnAddu" onclick="validateUpdate();" value=" Add "
							class="light-btn"> <input type="button" value=" Cancel "
							onclick="javascript:location.href='<%=basePath%>/searchDatabaseSubscriberProfileData.do?driverInstanceId=<%=addDatabaseSubscriberProfileDataForm.getDriverInstanceId()%>&dbAuthId=<%=addDatabaseSubscriberProfileDataForm.getDbAuthId()%>'"
							property="" onclick="" class="light-btn" /></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>