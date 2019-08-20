<%@ include file="/jsp/core/includes/common/Header.jsp" %>
 
<%@ page import="com.elitecore.netvertexsm.web.servermgr.spinterface.form.CreateSPInterfaceForm" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map.Entry" %>
<%@ page import="com.elitecore.corenetvertex.spr.data.SPRFields" %>
 

<%
String[] logicalNames = (String[])request.getAttribute("logicalNames");
String[] fieldNames = (String[])request.getAttribute("fieldNames");
String[] logicalValues = (String[])request.getAttribute("logicalValues");
%>

<script type="text/javascript">
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
					var fieldName = $('#dbField').val(); 
					var logicalValue = $('#logicalName option:selected').val();
					if(isNull(logicalName) || logicalNameVal==0){
		     			alert('Logical Name must be specified.');
		     		}else if(isNull(fieldName)){
		     			alert('Field Name must be specified.');
		     		}else{	
		     			var i = 0;							
						var flag = true;												 	
						if(document.getElementById('mappingtbl').getElementsByTagName('tr').length >= 2){
							
							var existingLogicalNames = document.getElementsByName("logicalNames");
							for(i=0;i<existingLogicalNames.length;i++){
								
								var value = existingLogicalNames[i].value;
								if(value == logicalName){
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
							$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + logicalName + "<input type='hidden' name = 'logicalNames' value='" + logicalName + "'/></td>"
															+"<input type='hidden' name = 'logicalValues' value='" + logicalValue + "'>" + logicalValue + "</input>"
														 +"<td class='tblrows'>" + fieldName + "<input type='hidden' name = 'fieldNames' value='" + fieldName + "'/></td>" 
														 +"<td align='center' class='tblrows'>"+"<img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' />"+"</td></tr>");			         				          	
			          		$(this).dialog('close');
			         	}	         				    		         			   				         			          				          		
		         	}		         								
            },                
		    Cancel: function() {
            	$(this).dialog('close');
        	}
        },
    	open: function() {
    		$('#dbField').val(""); 
    	},
    	close: function() {
    		
    	}				
	});
	$( "#popupfieldMapping" ).dialog("open");
	$(retriveTableFields(document.getElementById("databaseDSId").value));
}	

function setColumnsOnTextFields(){
	var dbId = document.getElementById("databaseDSId").value;
	retriveTableFields(dbId);
}

function setColumnsForTables(){
	setColumnsOnTextFields();
}

var dbFieldArray = new Array();
function retriveTableFields(dbId) {
	 tableName = document.getElementById("tableName").value;
	//$("#dbField").flushCache();
	//$("#identityField").flushCache();
	$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		dbFieldArray = dbFieldStr.split(", ");
		setFields("dbField",dbFieldArray);
		setFields("identityField",dbFieldArray);
		return dbFieldArray;
	});	
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
	var existingLogicalNames = document.getElementsByName("logicalNames");
	for(var i=0;i<existingLogicalNames.length;i++){
		if(existingLogicalNames[i].value == 'CUI'){
			return true;
		}
	}
	return false;
}

function subscriberIdentityFieldCheck(){
	var existingLogicalNames = document.getElementsByName("logicalNames");
	for(var i=0;i<existingLogicalNames.length;i++){
		if(existingLogicalNames[i].value == "<%=SPRFields.SUBSCRIBER_IDENTITY.displayName%>"){
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
	var existingLogicalNames = document.getElementsByName("logicalNames");
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

function validate() {
		if(document.forms[0].databaseDSId.value == 0){
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

function setTableNamesAndFieldNames(){
	setTablesForDatabase();
	setColumnsOnTextFields();
}

$(document).ready(function(){
	setTitle('<bean:message bundle="driverResources" key="spinterface.title"/>');
	<%int j =0;	
	if(logicalNames != null){
	for(j =0;j<logicalNames.length;j++){%>								
		
	var logicalName  = '<%=logicalNames[j]%>';
	var fieldName    = '<%=fieldNames[j]%>';								
	var logicalValue = '<%=logicalValues[j]%>';
		if(logicalName == 'null'){
			logicalName = '';
		}
		
		$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + logicalName + "<input type='hidden' name = 'logicalNames' value='" + logicalName + "'/></td>"  
										  +"<input type='hidden' name = 'logicalValues' value='" + logicalValue + "'/>"
										  +"<td class='tblrows'>" + fieldName +  "<input type='hidden' name = 'fieldNames' value='" + fieldName + "'/></td>" 
										  +"<td align='center' class='tblrows'>" + "<img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' />"+"</td></tr>");
		
		$('#mappingtbl td img.delete').on('click',function() {
			 $(this).parent().parent().remove(); 
		});
		
	<%}}%>	
});


</script> 	 
 
<html:form action="/createDBSPInterface" onsubmit="return validate()">
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
 <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
				<bean:message bundle="driverResources" key="spinterface.db.create"/>
			</td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="97%" id="c_tblCrossProductList" align="right" border="0" > 
				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="driverResources" key="spinterface.db.dbdatasource"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spinterface.db.dbdatasource"/>','<bean:message bundle="driverResources" key="spinterface.db.dbdatasource" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
				 		<html:select name="dbSPInterfaceForm" styleId="databaseDSId" property="databaseDSId" size="1" style="width: 206;"  onchange="setTableNamesAndFieldNames();" tabindex="1">
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
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="driverResources" key="spinterface.db.tablename"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spinterface.db.tablename"/>','<bean:message bundle="driverResources" key="spinterface.db.tablename" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="tableName" styleId="tableName" onchange="setColumnsOnTextFields();"  maxlength="30" size="30" tabindex="2"/><font color="#FF0000"> *</font>
					</td> 
				  </tr>				  
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="driverResources" key="spinterface.db.identityfield"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spinterface.db.identityfield"/>','<bean:message bundle="driverResources" key="spinterface.db.identityfield" />')"/>	
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:text property="identityField" styleId="identityField" maxlength="50" size="30"  onfocus="setColumnsForTables();"  tabindex="3"/><font color="#FF0000"> *</font>
					</td> 
				  </tr>
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="driverResources" key="spinterface.db.dbquerytimeout"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spinterface.db.dbquerytimeout"/>','<bean:message bundle="driverResources" key="spinterface.db.dbquerytimeout" />')"/>
						</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
					    <html:text property="dbQueryTimeout" styleId="dbQueryTimeout" maxlength="10" size="5" styleClass="c_deviceProfName" tabindex="4" /><font color="#FF0000"> *</font> 
					</td> 
				  </tr>			
				  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="driverResources" key="spinterface.db.maxquerytimeoutcnt"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="spinterface.db.maxquerytimeoutcnt"/>','<bean:message bundle="driverResources" key="spinterface.db.maxquerytimeoutcnt" />')"/>
						</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
					    <html:text property="maxQueryTimeoutCnt" maxlength="10" size="5" styleId="maxQueryTimeoutCnt" tabindex="5" /><font color="#FF0000"> *</font> 
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
          		<td class="tblheader-bold" colspan="4" height="20%" >
          			<bean:message bundle="driverResources" key="spinterface.db.fieldmapping"/>
          		</td>
          </tr>		
          <tr> 
				<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 		  			
				  		  			
	   <!-- add button  -->
		
		  <tr>				 
			<td >				
				<input type="button" id="button" value="   Add   " class="light-btn"  onclick="openPopup();" tabindex="5">
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
		        <input type="submit"  value="   Create   "  class="light-btn" tabindex="6">
		        <input type="button" align="left" value=" Cancel " class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchSPInterface.do?/>'" tabindex="7"/>                  
	        </td> 
   		  </tr> 
		</table> 
	  </td> 
	</tr> 
 <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table> 
</html:form> 

<div id="popupfieldMapping" title="DB Field Mapping" style="display: none;">	
	<table>	 	 
		<tr>
			<td align="left" class="labeltext" valign="top" >
				<bean:message bundle="driverResources" key="spinterface.db.logicalname"/></td>
			<td align="left" class="labeltext" valign="top" width="30%">
				<html:select name="dbSPInterfaceForm" styleId="logicalName" property="logicalName" size="1" style="width: 180;">
					<html:option value="0" bundle="driverResources" key="driver.select" />					
					<%
						for( Entry<String,SPRFields> entry  : SPRFields.getSPRFieldsEntrySet()){
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
<script>
$(retriveTableFields(document.getElementById("databaseDSId").value));
</script>

