<%@ page import="com.elitecore.netvertexsm.web.servermgr.drivers.csvdriver.form.CSVDriverForm" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Map.Entry" %>

<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" %>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.corenetvertex.spr.data.SPRFields" %>

<%
		String[] logicalNames 	= (String[])request.getAttribute("logicalNameArray");
		String[] fieldNames 	= (String[])request.getAttribute("fieldNameArray");	
%>
<script type="text/javascript">
var isValidName;
function validate() {
	if(isNull(document.forms[0].name.value)){
		alert('Name must be specified.');
		document.forms[0].name.focus();
		return false;
	}else if(!isValidName){
		alert('Enter Valid DB Sp Interface Name.');
		document.forms[0].name.focus();
		return false;
	}else if(document.forms[0].databaseDSId.value == 0){
		alert('Select atleast one datasource.');
		document.forms[0].databaseDSId.focus();
		return false;
	}else if(isNull(document.forms[0].tableName.value)){
		alert('Table Name must be specified.');
		document.forms[0].tableName.focus();
		return false;
	}else if(isNull(document.forms[0].identityField.value)){
		alert('Profile Lookup Column must be specified.');
		document.forms[0].identityField.focus();
		return false;
	}else if(isNull(document.forms[0].dbQueryTimeout.value)){
		alert('DB Query Timeout must be specified.');
		document.forms[0].dbQueryTimeout.focus();
		return false;
	}else if(isEcNaN(document.forms[0].dbQueryTimeout.value)){
		alert('DB Query Timeout must be numeric.');
		document.forms[0].dbQueryTimeout.focus();
		return false;
	}else if(isNull(document.forms[0].maxQueryTimeoutCnt.value)){
		alert('Max Query Timeout Count must be specified.');
		document.forms[0].maxQueryTimeoutCnt.focus();
		return false;
	}else if(isEcNaN(document.forms[0].maxQueryTimeoutCnt.value)){
		alert('Max Query Timeout Count must be numeric.');
		document.forms[0].maxQueryTimeoutCnt.focus();
		return false;
	}else if(!isMappingExist()){
		alert('Atleast one DB Field Mapping must be needed.');
		return false;
	}else if(!subscriberIdentityFieldCheck()){
		alert('SubscriberIdentity Field is mandatory,so add it in DB Field Mapping.');
		return false;
	}else if(!checkIDColumnWithMapping()){
		alert('Identity Column must be in DB Field Mapping.');
		return false;
	}
	return checkIfColumnExistsInTable();
}

$(document).ready(function() {
	$("#name").focus();
	verifyName();
	retriveTableFields($("#databaseDSId").val());
	//verifyName();
	var tempValue ;
	<%		
		 
		if(logicalNames!=null && fieldNames!=null){		
	%>  
	
	<%int j =0;	
		for(j =0;j<logicalNames.length;j++){
			
		if(logicalNames[j]!=null && logicalNames[j].trim().length()>0){
				System.out.println(logicalNames[j] + "--" + SPRFields.fromSPRFields(logicalNames[j]));
				if(SPRFields.fromSPRFields(logicalNames[j])!=null){
		
					%>								
		var logicalName  = '<%=SPRFields.fromSPRFields(logicalNames[j]).displayName%>';
					
		var logicalValue  = '<%=SPRFields.fromSPRFields(logicalNames[j]).name()%>';
					
		var fieldName = '<%=fieldNames[j]%>';								
		
		if(logicalName == 'null'){
			logicalName = '';
		}
		<%%>
		$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + logicalName + "&nbsp" + "<input type='hidden' name = 'logicalValues' value='" + logicalValue + "'/>" +"</td><td class='tblrows'>" + fieldName + "<input type='hidden' name = 'fieldNames' value='" + fieldName + "'/>" +"</td><td align='center' class='tblrows'><img  tabindex='9' src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");				
		$('#mappingtbl td img.delete').on('click',function() {
			 $(this).parent().parent().remove(); 
		});
		
	<%	 }
		}
	  }
	%>	
<%}%>

});

function setColumnsOnTextFields(){
	var dbId = document.getElementById("databaseDSId").value;
	retriveTableFields(dbId);
}

function setColumnsForTables(){
	setColumnsOnTextFields();
}
var dbFieldArray = new Array();

function retriveTableFields(dbId) {
	var dbFieldStr;
	var tableName = document.getElementById("tableName").value;
	//$("#identityField").flushCache();
	//$("#dbField").flushCache();
	$.post("FieldRetrievalServlet",
			{databaseId:dbId,tblName:tableName}
	).done(function(data){
		dbFieldStr = data.substring(1,data.length-3);
		dbFieldArray = dbFieldStr.split(", ");
		setFields("identityField",dbFieldArray);
		setFields("dbField",dbFieldArray);
		return dbFieldArray;
	});	
	
}
function verifyFormat (){
	var searchName = document.getElementById("name").value;
	callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.DRIVER%>',searchName:searchName,mode:'update',id:'<%=driverInstanceData.getDriverInstanceId()%>'},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.DRIVER%>',searchName:searchName,mode:'update',id:'<%=driverInstanceData.getDriverInstanceId()%>'},'verifyNameDiv');
}

function setTablesForDatabase(){

	var dbId = document.getElementById("databaseDSId").value;
	retriveTableNames(dbId);
}

function retriveTableNames(dbId) {
	var dbTableNamesStr;
	var tableName = "TABS";
	//$("#tableName").flushCache();
	$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
		dbTableNamesStr = data.substring(1,data.length-3);
		var dbTableNamesArray = new Array();
		dbTableNamesArray = dbTableNamesStr.split(", ");		
		setFields("tableName",dbTableNamesArray);
		return dbTableNamesArray;
	});		
}
function cuiFieldCheck(){
	var existingLogicalNames = document.getElementsByName("logicalValues");
	for(var i=0;i<existingLogicalNames.length;i++){
		if(existingLogicalNames[i].value == 'CUI'){
			return true;
		}
	}
	return false;
}

function subscriberIdentityFieldCheck(){
	var existingLogicalNames = document.getElementsByName("logicalValues");
	for(var i=0;i<existingLogicalNames.length;i++){	
		if(existingLogicalNames[i].value == 'SUBSCRIBER_IDENTITY'){
			return true;
		}
	}
	return false;
}

function checkIDColumnWithMapping(){
	var existingColumnNames = document.getElementsByName("fieldNames");
	var identityColumnName = document.getElementById("identityField").value;
	for(var i=0;i<existingColumnNames.length;i++){
		if(existingColumnNames[i].value == identityColumnName){
			return true;
		}
	}
	return false;
}

function isMappingExist(){
	var existingLogicalNames = document.getElementsByName("logicalValues");
	if(existingLogicalNames!=null && existingLogicalNames.length>0){
		return true;
	}
	return false;
}

function isFieldExistsInTable(value){
	return ($.inArray(value,dbFieldArray) >= 0);
}

function checkIfColumnExistsInTable(){
	var fields = "";
	if(document.getElementById('mappingtbl').getElementsByTagName('tr').length >= 2){
		var existingFieldNames = document.getElementsByName("fieldNames");
		for(var i=0;i<existingFieldNames.length;i++){
			var value = existingFieldNames[i].value;
			if(!isFieldExistsInTable(value)){
				if(i == (existingFieldNames.length -1 )){
					fields = fields + value;
				}else{
					fields = fields + value + ", ";
				}
			}
		}
	}
	if(fields != ""){
		if(!confirm("Field " + fields + " does not contains in Table " + document.forms[0].tableName.value +". Do you want to add field in mapping?")){
			return false;
		}
	} 
}

function openPopup() {	
	$.fx.speeds._default = 1000;
	document.getElementById("popupfieldMapping").style.visibility = "visible";		
	$( "#popupfieldMapping" ).dialog({
		modal: true,
		autoOpen: false,		
		height: 150,
		width: 500,		
		buttons:{					
            'Add': function() {
					
					var logicalName = $('#logicalName option:selected').text();
					var logicalNameVal = $('#logicalName').val();
					var actualLogicalValue = $('#logicalName option:selected').val();
					var fieldName = $('#dbField').val(); 
					if(isNull(logicalName) || logicalNameVal==0){
		     			alert('Logical Name must be specified.');
		     		}else if(isNull(fieldName)){
		     			alert('Field Name must be specified.');
		     		}else{	
		     			var i = 0;							
						var flag = true;												 	
						if(document.getElementById('mappingtbl').getElementsByTagName('tr').length >= 2){
							
							var existingLogicalNames = document.getElementsByName("logicalValues");
							for(i=0;i<existingLogicalNames.length;i++){
								
								var value = existingLogicalNames[i].value;
								if(value == actualLogicalValue){
									alert("Mapping with this Logical Name is already present so select another value for Logical Name");
									flag = false;
									break;
								}
							}
							
							var columnNames = document.getElementsByName("fieldNames");
							var dbField 	= document.getElementById("dbField").value;							
							for(i=0;i<columnNames.length;i++){							
								var value = columnNames[i].value;
								if(value == dbField){
									alert("Mapping with this Column Name is already present so enter another value for Column Name");
									flag = false;
									break;
								}
							}							
						}								
		         	
						if(flag){
							$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + logicalName + "<input type='hidden' name = 'logicalValues' value='" + actualLogicalValue + "'/>" +"</td><td class='tblrows'>" + fieldName + "<input type='hidden' name = 'fieldNames' value='" + fieldName + "'/>" +"</td><td align='center' class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");			         				          	
			          		$(this).dialog('close');
			         	}	         				    		         			   				         			          				          		
		         	}	
					$("#update").focus();
            },                
		    Cancel: function() {
            	$(this).dialog('close');
            	$("#update").focus();
        	}
        },
    	open: function() {
    		$('#dbField').val(""); 
    	},
    	close: function() {
    		$("#update").focus();
    	}				
	});
	$( "#popupfieldMapping" ).dialog("open");
	retriveTableFields($("#databaseDSId").val());
}	
setTitle('<bean:message bundle="driverResources" key="spinterface.title"/>');
	
</script>
<html:form action="/editDBSPInterface" onsubmit="return validate()">
 <html:hidden name="dbSPInterfaceForm" styleId="driverInstanceId" property="driverInstanceId"/>
 <html:hidden name="dbSPInterfaceForm" styleId="databaseSPInterfaceId" property="databaseSPInterfaceId"/>
 		<table cellSpacing="0" cellPadding="0" width="100%" border="0" >

	 	  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%" id="c_tblCrossProductList" align="right" border="0" >
						<tr>
							<td class="tblheader-bold" colspan="2">
								<bean:message key="general.update.basicdetails"/>
							</td>
		  				</tr> 
						<tr>
							<td align="left" class="labeltext" valign="top" width="30%">
							<bean:message  key="general.name" />
							</td>
							<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text styleId="name" onkeyup="verifyFormat()" onblur="verifyName()" property="name" size="30" tabindex="1"
								maxlength="60" style="width:250px"/><font color="#FF0000"> *</font> <div id="verifyNameDiv" class="labeltext"></div>
							</td>
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" >
							<bean:message  key="general.description" />
							<td align="left" class="labeltext" valign="top">
								<html:textarea styleId="description" property="description" style="width:250px" tabindex="2"/>
							</td>
						</tr>
			   </table>
			</td>
		</tr>

		 <tr>
    	  	 <td  colspan="3">
    	  	 	<table width="97%" id="c_tblCrossProductList" align="right" border="0" >
		 		<tr>
        	 <td class="tblheader-bold" height="20%"  colspan="2">
        		<bean:message bundle="driverResources" key="spinterface.db.title"/>
	          </td>
    	  </tr>
    	  <tr>
					<td align="left" class="labeltext" valign="top" width="30%">
						<bean:message bundle="driverResources" key="spinterface.db.dbdatasource"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spinterface.db.dbdatasource"/>','<bean:message bundle="driverResources" key="spinterface.db.dbdatasource" />')"/>
						</td> 
					<td align="left" class="labeltext" valign="top" > 
				 		<html:select name="dbSPInterfaceForm" styleId="databaseDSId" property="databaseDSId" size="1" style="width: 206;"  onchange="setColumnsOnTextFields();" tabindex="3">
							<html:option value="0" bundle="driverResources" key="driver.select" />
							<logic:iterate id="dbDatasources"  name="dbSPInterfaceForm" property="databaseDSList" 
								type="com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData">
								<html:option value="<%=Long.toString(dbDatasources.getDatabaseId())%>">
									<bean:write name="dbDatasources" property="name"/>
								</html:option>
							</logic:iterate>
						</html:select><font color="#FF0000"> *</font>	
					</td>
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="driverResources" key="spinterface.db.tablename"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spinterface.db.tablename"/>','<bean:message bundle="driverResources" key="spinterface.db.tablename" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:text property="tableName" styleId="tableName" onfocus="setTablesForDatabase();" maxlength="30" size="30" styleClass="c_deviceProfName" tabindex="4"/><font color="#FF0000"> *</font>
					</td> 
				  </tr>				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="driverResources" key="spinterface.db.identityfield"/>
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spinterface.db.identityfield"/>','<bean:message bundle="driverResources" key="spinterface.db.identityfield" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:text property="identityField" styleId="identityField" maxlength="50" onfocus="setColumnsForTables();" size="30" styleClass="c_deviceProfName" tabindex="5"/><font color="#FF0000"> *</font>
					</td> 
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="driverResources" key="spinterface.db.dbquerytimeout"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spinterface.db.dbquerytimeout"/>','<bean:message bundle="driverResources" key="spinterface.db.dbquerytimeout" />')"/>
						</td> 
					<td align="left" class="labeltext" valign="top" > 
					    <html:text property="dbQueryTimeout" styleId="dbQueryTimeout" maxlength="10" size="5" styleClass="c_deviceProfName" tabindex="6" /><font color="#FF0000"> *</font> 
					</td> 
				  </tr>			
				  <tr> 
					<td align="left" class="labeltext" valign="top" >
						<bean:message bundle="driverResources" key="spinterface.db.maxquerytimeoutcnt"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spinterface.db.maxquerytimeoutcnt"/>','<bean:message bundle="driverResources" key="spinterface.db.maxquerytimeoutcnt" />')"/>
						</td> 
					<td align="left" class="labeltext" valign="top" > 
					    <html:text property="maxQueryTimeoutCnt" maxlength="10" size="5" styleId="maxQueryTimeoutCnt" tabindex="7" /><font color="#FF0000"> *</font> 
					</td> 
				  </tr>						  				  				  					
			   </table>  
			</td> 
		  </tr>	 
		  <tr> 
			<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr>

		  
       <!--  display added DB Field Mapping list -->
		            
		  <tr> 
			<td class="btns-td" valign="middle" colspan="3"> 
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
			  <tr> 
          		<td class="tblheader-bold" colspan="4" height="20%" style="padding-left: 2em">
          			<bean:message bundle="driverResources" key="spinterface.db.fieldmapping"/>
          		</td>
          </tr>		
          <tr> 
				<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 		  			
		  <tr>				 
				<td >				
				<input type="button" id="button" value="   Add   " class="light-btn"  onclick="openPopup();" tabindex="8">
			</td>
		  </tr>
   		  <tr> 
					<td class="small-gap" colspan="3"> 
						&nbsp;
					</td> 
				</tr> 
				<tr> 
					<td colspan="3">
					<table width="70%" id="mappingtbl" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td align="center" class="tblheader" valign="top" width="15%">
								<bean:message bundle="driverResources" key="spinterface.db.logicalname" /></td>
							<td align="center" class="tblheader" valign="top" width="15%">
								<bean:message bundle="driverResources" key="spinterface.db.fieldname" /></td>
							<td align="center" class="tblheader" valign="top" width="4%">Remove</td>
						</tr>
						
					</table>
					</td> 
				</tr> 				
			</table> 			
			</td> 
		</tr>

          <tr> 
	        <td class="btns-td" valign="middle" >&nbsp;</td> 
            <td class="btns-td" valign="middle"  > 
		        <input type="submit"  id="update" value="   Update   "  class="light-btn" tabindex="10">
		        <input type="button" align="left" value=" Cancel " class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchSPInterface.do?/>'" tabindex="11"/>                  
	        </td> 
   		  </tr> 
		</table> 
		
	<div id="popupfieldMapping" title="DB Field Mapping" style="display: none;">	
	<table>	 	 
		<tr>
			<td align="left" class="labeltext" valign="top" >
				<bean:message bundle="driverResources" key="spinterface.db.logicalname"/></td>
			<td align="left" class="labeltext" valign="top" width="30%">
				<html:select name="dbSPInterfaceForm" styleId="logicalName" property="logicalName" size="1" style="width: 180;">
					<html:option value="0" bundle="driverResources" key="driver.select" />					
					<%
						for(Entry<String,SPRFields> entry  : SPRFields.getSPRFieldsEntrySet()){
					%>						
						<html:option value="<%=entry.getValue().name()%>">
							<%=entry.getValue().displayName%>
						</html:option>
					<% } %>
				</html:select>								      											
			</td>
		</tr>			  	
		<tr>
			<td align="left" class="labeltext" valign="top" width="20%">
				<bean:message bundle="driverResources" key="spinterface.db.fieldname"/></td>
			<td align="left" class="labeltext" valign="top" width="32%">
				<html:text name="dbSPInterfaceForm" styleId="dbField" property="dbField" size="23" maxlength="60"/>
			</td>
		</tr>
	</table>								
</div>
</html:form>
<script>
</script>


