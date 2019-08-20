<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" %>
<%@ page import="java.util.List" %> 
<%@page import=" com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>


<%
	DriverInstanceData driverInstanceData= (DriverInstanceData)request.getAttribute("driverInstanceData");
	long driverTypeId=driverInstanceData.getDriverTypeId();
%>
<script type="text/javascript">
var mainArray = new Array();
var refArray = new Array();
var count = 0;
var count1 = 0;

$(document).ready(function(){
	
	$('#mappingtbl td img.delete').on('click',function() {
			
			 //var $td= $(this).parents('tr').children('td');			
			var removalVal = $(this).closest('tr').find('td:eq(0)').text(); 							
			for(var d=0;d<mainArray.length;d++){
				
				var currentVal = mainArray[d];					
				if(currentVal == removalVal){
					mainArray[d] = '  ';
					break;
				}
			}
			var removalRefVal = $(this).closest('tr').find('td:eq(1)').text();
			for(var f=0;f<count1;f++){
			var currentRefVal = refArray[f];
			if(currentRefVal == removalRefVal){
				refArray[f] = "";
				break;
			}
			}
			 $(this).parent().parent().remove();
			 
	});

});        		

function openPopup(){
	if('<%=driverTypeId%>' == '1') {
	$.fx.speeds._default = 1000;
	document.getElementById("popupfieldMapping").style.visibility = "visible";		
	$( "#popupfieldMapping" ).dialog({
		modal: true,
		autoOpen: false,		
		height: 300,
		width: 400,				
		buttons:{					
            'Add': function() {	

					var name = $('#logicalName option:selected').text();					
					var name0 = $('#dbField').val(); 
					 
		          	refArray[count1++] = name0;
	          		if(isNull($('#logicalName').val())){
		     			alert('Logical Name must be specified.');			     		
		     		}else{	
		     			var i = 0;							
						var flag = true;												 	
						if(document.getElementById('mappingtbl').getElementsByTagName('tr').length >= 2){												
							for(i=0;i<mainArray.length;i++){									
								var value = mainArray[i];																						
								if(value == name){
									alert("Mapping with this value is already present so add another value for attribute");
									flag = false;
									break;
								}
							}
						}								
		         		if(flag){	
		         			$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + name + "<input type='hidden' name = 'logicalName' value='" + name + "'/>" +"</td><td class='tblfirstcol'>" + name0 + "<input type='hidden' name = 'dbField' id = 'ref' value='" + name0 + "'/>" + "</td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
		         			$('#mappingtbl td img.delete').on('click',function() {

		       				 //var $td= $(this).parents('tr').children('td');			
		       				var removalVal = $(this).closest('tr').find('td:eq(0)').text(); 							
		       				for(var d=0;d<count;d++){
		       					var currentVal = mainArray[d];					
		       					if(currentVal == removalVal){
		       						mainArray[d] = '  ';
		       						break;
		       					}
		       				}
		       				var removalRefVal = $(this).closest('tr').find('td:eq(1)').text();
		       				for(var f=0;f<count1;f++){
								var currentRefVal = refArray[f];
								if(currentRefVal == removalRefVal){
									refArray[f] = "";
									break;
								}
			       			}								
		       				 $(this).parent().parent().remove(); });		         						          					          	
			          		mainArray[count++] = name;						          					          		          					          							          					          		
			          		$(this).dialog('close');	
			          		init();		 			          		         		
			         	}	         				    		         			   				         			          				          		
		         	}		         				          		
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
	$( "#popupfieldMapping" ).dialog("open");
	$(retriveTableFields(document.getElementById("databaseDsId").value));
}else if('<%=driverTypeId%>' == '2') {
		$.fx.speeds._default = 1000;
		document.getElementById("popupfieldMapping").style.visibility = "visible";		
		$( "#popupfieldMapping" ).dialog({
			modal: true,
			autoOpen: false,		
			height: 300,
			width: 400,				
			buttons:{					
	            'Add': function() {	

						var name = $('#logicalName option:selected').text();					
						var name0 = $('#ldapAttribute').val(); 
						 
			          	refArray[count1++] = name0;
		          		if(isNull($('#logicalName').val())){
			     			alert('Logical Name must be specified.');			     		
			     		}else{	
			     			var i = 0;							
							var flag = true;												 	
							if(document.getElementById('mappingtbl').getElementsByTagName('tr').length >= 2){												
								for(i=0;i<mainArray.length;i++){									
									var value = mainArray[i];																						
									if(value == name){
										alert("Mapping with this value is already present so add another value for attribute");
										flag = false;
										break;
									}
								}
							}								
			         		if(flag){	
			         			$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + name + "<input type='hidden' name = 'logicalName' value='" + name + "'/>" +"</td><td class='tblfirstcol'>" + name0 + "<input type='hidden' name = 'ldapAttribute' id = 'ref' value='" + name0 + "'/>" + "&nbsp;</td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
			         			$('#mappingtbl td img.delete').on('click',function() {

			       				 //var $td= $(this).parents('tr').children('td');			
			       				var removalVal = $(this).closest('tr').find('td:eq(0)').text(); 							
			       				for(var d=0;d<count;d++){
			       					var currentVal = mainArray[d];					
			       					if(currentVal == removalVal){
			       						mainArray[d] = '  ';
			       						break;
			       					}
			       				}
			       				var removalRefVal = $(this).closest('tr').find('td:eq(1)').text();
			       				for(var f=0;f<count1;f++){
									var currentRefVal = refArray[f];
									if(currentRefVal == removalRefVal){
										refArray[f] = "";
										break;
									}
				       			}								
			       				 $(this).parent().parent().remove(); });		         						          					          	
				          		mainArray[count++] = name;						          					          		          					          							          					          		
				          		$(this).dialog('close');	
				          		init();		 			          		         		
				         	}	         				    		         			   				         			          				          		
			         	}		         				          		
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
		$( "#popupfieldMapping" ).dialog("open");
	}
		
}

function setColumnsOnTextFields(){
	var dbId = document.getElementById("databaseDsId").value;
	retriveTableFields(dbId);
}

function setColumnsForTables(){
	setColumnsOnTextFields();
}

function retriveTableFields(dbId) {
	var dbFieldStr;
	var tableName = document.getElementById("tableName").value;
	$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		setFields("identityField",dbFieldArray);
		setFields("dbField",dbFieldArray);
		return dbFieldArray;
	});	
	
}


function cuiFieldCheck(){
		
	for(var i=0;i<mainArray.length;i++){
		if(mainArray[i] == 'CUI'){
			return true;
		}
	}
	return false;
}

var isValidName;
function validate() {
	if('<%=driverTypeId%>' == '1'){
		if(isNull(document.forms[0].name.value)){
			alert('Name must be specified.');
		}else if(!isValidName) {
			alert('Enter Valid Driver Name.');
			document.forms[0].name.focus();
			return;
		}else if(document.getElementById("databaseDsId").value == 0){
			alert('Select atleast one datasource.');
		}else if(isNull(document.forms[0].tableName.value)){
			alert('Table Name must be specified.');
		}else if(isNull(document.forms[0].identityField.value)){
			alert('Profile Lookup Column must be specified.');
		}else if(isNull(document.forms[0].dbQueryTimeout.value)){
			alert('DB Query Timeout must be specified.');
		}else if(isEcNaN(document.forms[0].dbQueryTimeout.value)){
			alert('DB Query Timeout must be numeric.');
		}else if(isNull(document.forms[0].maxQueryTimeoutCnt.value)){
			alert('Max Query Timeout Count must be specified.');
		}else if(isEcNaN(document.forms[0].maxQueryTimeoutCnt.value)){
			alert('Max Query Timeout Count must be numeric.');
		}else if(!cuiFieldCheck()){
			alert('CUI Field is mandatory,so add it in DB Field Mapping.');
		}else{ 			 			
			document.forms[0].submit();
		}		
	}else if('<%=driverTypeId%>' == '2'){
		if(isNull(document.forms[0].name.value)){
			alert('Name must be specified.');
		}else if(!isValidName) {
			alert('Enter Valid Driver Name.');
			document.forms[0].name.focus();
			return;
		}else if(document.getElementById("ldapDsId").value == 0){
			alert('Select atleast one datasource.');
		}else if(isNull(document.forms[0].queryMaxExecTime.value)){
			alert('Max Query Execution Time must be specified.');
		}else if(isEcNaN(document.forms[0].queryMaxExecTime.value)){
			alert('Max Query Execution Time must be numeric.');
		}else{ 			 			
			document.forms[0].submit();
		}	
	}
}

$(document).ready(function(){
	verifyName();
	if('<%=driverTypeId%>' == '1') {
		$("#button").click(function(){					
			document.getElementById("logicalName").value="Select";
			document.getElementById("dbField").value=null;
		});
	}else if('<%=driverTypeId%>' == '2') {
		$("#button").click(function(){					
			document.getElementById("logicalName").value="Select";
			document.getElementById("ldapAttribute").value=null;
		});
	}
});

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.SUBSCRIBER_PROFILE_REPOSITORY%>',searchName,'update','<%=driverInstanceData.getDriverInstanceId()%>','verifyNameDiv');
}
	
</script>


<input type="hidden" value="<%=((DriverInstanceData)request.getAttribute("driverInstanceData")).getDriverInstanceId()%>" id="oldDriverTypeId" name="oldDriverTypeId" />

<table cellpadding="0" cellspacing="0" border="0" width="828" >
  <tr> 
    <td width="10">&nbsp;</td> 
    <td colspan="2"> 
      <table width="100%" border="0" cellspacing="0" cellpadding="0"> 
        <tr> 
          <td width="26" valign="top" rowspan="2"><img src="<%=basePath%>/images/left-curve.jpg"></td> 
          <td background="<%=basePath%>/images/header-gradient.jpg" width="133" rowspan="2" align="center" class="page-header">
          	<bean:message bundle="driverResources" key="driver.driverinstance"/></td> 
          <td width="32" rowspan="2"><img src="<%=basePath%>/images/right-curve.jpg"></td> 
          <td width="633"><a href="#"><img src="<%=basePath%>/images/csv.jpg" name="Image1" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image1','','<%=basePath%>/images/csv-hover.jpg',1)" border="0" alt="Save as CSV"></a><a href="#"><img src="<%=basePath%>/images/pdf.jpg" name="Image2" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image2','','<%=basePath%>/images/pdf-hover.jpg',1)" border="0" alt="Save as PDF"></a><a href="#"><img src="<%=basePath%>/images/html.jpg" name="Image3" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image3','','<%=basePath%>/images/html-hover.jpg',1)" border="0" alt="Save as HTML"></a><a href="#"><img src="<%=basePath%>/images/filter.jpg" name="Image4" onMouseOut="MM_swapImgRestore()" onMouseOver="MM_swapImage('Image4','','<%=basePath%>/images/filter-hover.jpg',1)" border="0" alt="Apply Filter"></a></td> 
        </tr> 
        <tr> 
          <td width="633" valign="bottom"><img src="<%=basePath%>/images/line.jpg"></td> 
        </tr> 
      </table> 
    </td> 
  </tr> 
  <tr> 
    <td width="10" class="small-gap">&nbsp;</td> 
    <td class="small-gap" colspan="2">&nbsp;</td> 
  </tr>
  <tr>
  <td width="10">&nbsp;</td> 
  <td>
<table width="828" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="10" class="small-gap">&nbsp;</td>
    <td class="small-gap" colspan="2">&nbsp;</td>
  </tr>
  <tr> 
    <td></td>
    <td width="773" valign="top" class="box">
    
    


<div  style="margin-left: 1.7em;" class="tblheader-bold"><bean:message bundle="driverResources" key="driver.driverinstance.viewDriverInstance" /></div>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right"> 
<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
	<bean:define id="driverInstanceBean" name="driverInstanceData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" />
		  <tr> 
      <td valign="top" align="right"> 
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
           <%if(driverTypeId==1) {%>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message  bundle="driverResources" key="driver.driverinstance.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="driverInstanceBean" property="name"/></td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.driverinstance.decription" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="description"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.dbdriver.databasedsid" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="databaseSprDriverData.databaseDs.name"/>&nbsp;</td>
          </tr>
 
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.driverinstance.drivertype" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="driverTypeData.name"/>&nbsp;</td>
          </tr>  
         
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.dbdriver.tablename" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="databaseSprDriverData.tableName"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.dbdriver.identityfield" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="databaseSprDriverData.identityField"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.dbdriver.dbquerytimeout" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="databaseSprDriverData.dbQueryTimeout"/>&nbsp;</td>
          </tr> 
         
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.dbdriver.maxquerytimeoutcnt" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="databaseSprDriverData.maxQueryTimeoutCnt"/>&nbsp;</td>
          </tr>
		   <%} else if(driverTypeId==2){%>
		    <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message  bundle="driverResources" key="driver.driverinstance.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="driverInstanceBean" property="name"/></td>
         	 </tr>
          
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.driverinstance.decription" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="description"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.ldapdriver.ldapdsid" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="ldapSPRDriverData.ldapDs.name"/>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.driverinstance.drivertype" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="driverTypeData.name"/>&nbsp;</td>
          </tr>  
		   <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.ldapdriver.expirydatapattern" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="ldapSPRDriverData.expiryDatePattern"/>&nbsp;</td>
          </tr>
          
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.ldapdriver.passdecrypttype" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="ldapSPRDriverData.passwordDecryptType"/>&nbsp;</td>
          </tr> 
            
           
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.ldapdriver.querymaxexectime" /></td>
             <td class="tblcol" width="70%" height="20%"><bean:write name="driverInstanceBean" property="ldapSPRDriverData.queryMaxExecTime"/>&nbsp;</td>
          </tr>
            <%} %>
		
		 </table>
		</td>
    </tr>
</table>
		</td>
    </tr>
</table>

<br>
<html:form action="/editDriverInstance">
<br>
<%if(driverTypeId==1) {%>
<bean:define id="driverInstanceBean" name="driverInstanceData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" />
<div style="margin-left: 2.0em;" class="tblheader-bold"><bean:message bundle="driverResources" key="driver.driverinstance.update" /></div>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right" colspan="8"> 
	<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right" >
	
	<tr>
		<td align="right" class="labeltext" valign="top" class="box" colspan="8"> 

 
  	 <table width="100%" id="c_tblCrossProductList" align="right" border="0" onclick="setColumnsForTables();"> 
 		 <bean:define id="driverInstanceBean" name="driverInstanceData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" />
		  	<tr> 
				<td align="left" class="labeltext" valign="top" width="20%">
					<bean:message bundle="driverResources" key="driver.driverinstance.name"/>
					<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="driverResources" key="driver.driverinstance.name"/>','<bean:message bundle="driverResources" key="driver.driverinstance.name" />')"/>	
				</td> 
				<sm:nvNameField maxLength="60" size="30" value="${driverInstanceBean.name}"/>
			</tr>
			<tr> 
				<td align="left" class="labeltext" valign="top" width="10%">
					<bean:message bundle="driverResources" key="driver.dbdriver.databasedsid"/></td> 
				<td align="left" class="labeltext" valign="top" width="32%" > 
			 		<html:select name="editDriverInstanceForm" styleId="databaseDsId" property="databaseDsId" size="1" style="width: 206;"  onchange="setColumnsOnTextFields();" tabindex="1">
						<html:option value="0" bundle="driverResources" key="driver.select" />
						<logic:iterate id="dbDatasources"  name="editDriverInstanceForm" property="databaseDsList" 
							type="com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData">
							<html:option value="<%=Long.toString(dbDatasources.getDatabaseId())%>">
								<bean:write name="dbDatasources" property="name"/>
							</html:option>
						</logic:iterate>
					</html:select><font color="#FF0000"> *</font>	
				</td>
			</tr>
			<tr> 
				<td align="left" class="labeltext" valign="top" width="20%">
					<bean:message bundle="driverResources" key="driver.driverinstance.decription"/></td> 
				<td align="left" class="labeltext" valign="top" width="32%" >
					<html:textarea property="description" cols="30" rows="3" tabindex="2" /></td> 
			</tr>	  
			<tr>
				<td align="left" class="labeltext" valign="top" width="20%">
					<bean:message bundle="driverResources" key="driver.dbdriver.tablename"/></td> 
				<td align="left" class="labeltext" valign="top" width="32%" > 
					<html:text property="tableName" styleId="tableName" onblur="setColumnsForTables();" tabindex="3" maxlength="60" size="30" styleClass="c_deviceProfName"/><font color="#FF0000"> *</font> </td>
	  		</tr>
	  		<tr> 
				<td align="left" class="labeltext" valign="top" width="10%">
					<bean:message bundle="driverResources" key="driver.dbdriver.identityfield"/></td> 
				<td align="left" class="labeltext" valign="top" width="32%" > 
					<html:text property="identityField" styleId="identityField" onfocus="setColumnsForTables();" onblur="setColumnsForTables();" maxlength="60" size="30" styleClass="c_deviceProfName" tabindex="3"/><font color="#FF0000"> *</font>
				</td> 
			</tr>
		  	<tr>
				<td align="left" class="labeltext" valign="top" width="20%">
					<bean:message bundle="driverResources" key="driver.dbdriver.dbquerytimeout"/></td> 
				<td align="left" class="labeltext" valign="top" width="32%" > 
				    <html:text property="dbQueryTimeout" maxlength="60" size="30" styleClass="c_deviceProfName" tabindex="4" /><font color="#FF0000"> *</font></td>
			</tr>
			<tr>  
				<td align="left" class="labeltext" valign="top" width="20%">
					<bean:message bundle="driverResources" key="driver.dbdriver.maxquerytimeoutcnt"/></td> 
				<td align="left" class="labeltext" valign="top" width="32%" > 
				    <html:text property="maxQueryTimeoutCnt" maxlength="60" size="30"  styleId="maxQueryTimeoutCnt" tabindex="5" /><font color="#FF0000"> *</font> </td>
			</tr>
			<tr>	
	        	<td class="btns-td" valign="middle" >&nbsp;
	        	<html:hidden property="driverInstanceId" styleId="driverInstanceId" name="editDriverInstanceForm" />
	        	<html:hidden property="oldDriverTypeId" value="<%=Long.toString(driverInstanceData.getDriverTypeId())%>" styleId="oldDriverTypeId" name="editDriverInstanceForm" />
	        	<html:hidden property="driverTypeId" value="<%=Long.toString(driverInstanceData.getDriverTypeId())%>" styleId="driverTypeId" name="editDriverInstanceForm" />
	        	<html:hidden property="databaseSpInterfaceId" value="<%=Long.toString(driverInstanceData.getDatabaseSpInterfaceDriverData().getDatabaseSpInterfaceId())%>" styleId="databaseSprId" name="editDriverInstanceForm" />
	        	<html:hidden property="databaseDsId" value="<%=Long.toString(driverInstanceData.getDatabaseSpInterfaceDriverData().getDatabaseDsId())%>" styleId="databaseDsId" name="editDriverInstanceForm" />
	        	</td>
	        </tr>	
	     	<tr>
				<td style="padding-left: 1.6em"><input type="button" id="button" value="Add Field Mapping" tabindex="6" class="light-btn" onclick="openPopup();"></td>
			</tr>   
	        <tr> 
  				<td width="10" class="small-gap">&nbsp;</td> 
  				<td class="small-gap" colspan="2">&nbsp;</td> 
  			</tr>
	        <tr>
			<td style="padding-left: 1.6em" colspan="4">
	
			<table width="70%" id="mappingtbl" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="center" class="tblheader" valign="top" width="15%">
						<bean:message bundle="driverResources" key="driver.dbfield.logicalname" /></td>
					<td align="center" class="tblheader" valign="top" width="15%">
						<bean:message bundle="driverResources" key="driver.dbfield" /></td>
					<td align="center" class="tblheader" valign="top" width="4%">Remove</td>
				</tr>
				<%int fieldIndex=0; %>
			<logic:iterate id="listBean" name="dbFieldMapList" type=" com.elitecore.netvertexsm.datamanager.servermgr.spinterface.dbinterface.data.DBFieldMapData">
			<script>
					mainArray[<%=fieldIndex%>] = '<%=listBean.getLogicalName()%>';
			</script>
				 <tr>
					<td class="tblfirstcol" width="70%" height="20%"><bean:write name="listBean" property="logicalName"/><input type='hidden' name = 'logicalName' value="<%=listBean.getLogicalName()%>"/></td>
				 	<td class="tblrows" width="70%" height="20%"><bean:write name="listBean" property="dbField"/><input type='hidden' name = 'dbField' value="<%=listBean.getDbField()%>"/></td>
				 	<td class='tblrows' onclick="remove()"><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' onclick="remove()" /></td>			 	
				 </tr>
				 <%fieldIndex++; %>
			</logic:iterate>
			</table>
			
			</td>
			</tr>
			<tr> 
	   			<td width="10" class="small-gap">&nbsp;</td> 
	   			<td class="small-gap" colspan="2">&nbsp;</td> 
		  	</tr>
			<tr>
				<td>&nbsp;</td>
				<td>
					<input type="button" onclick="validate();" value="  Update  " tabindex="7" class="light-btn">
					<input type="button" align="left" value=" Cancel " tabindex="8" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchDriverInstance.do?/>'"/>
				</td>				 	
		 	</tr>        
  	  </table>
		</td>
	</tr>

	</table>
		</td>
    </tr>
</table>

<%} %>	 
<%if(driverTypeId==2) {%>
<bean:define id="driverInstanceBean" name="driverInstanceData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" />
<div style="margin-left: 2.0em;" class="tblheader-bold"><bean:message bundle="driverResources" key="driver.driverinstance.update" /></div>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right" colspan="8"> 
	<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right" >
	
	<tr>
		<td align="right" class="labeltext" valign="top" class="box" colspan="8"> 

 
  	<table width="100%" id="c_tblCrossProductList" align="right" border="0" > 
 	  <bean:define id="driverInstanceBean" name="driverInstanceData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" />
	  	  <tr> 
			<td align="left" class="labeltext" valign="top" width="10%">
				<bean:message bundle="driverResources" key="driver.driverinstance.name"/></td> 
			<td align="left" class="labeltext" valign="top" width="32%" > 
				<html:text styleId="name" property="name" maxlength="60" size="30" tabindex="1" /><font color="#FF0000"> *</font></td> 
		  </tr>
		  <tr> 
			<td align="left" class="labeltext" valign="top" width="10%">
				<bean:message bundle="driverResources" key="driver.driverinstance.decription"/></td> 
			<td align="left" class="labeltext" valign="top" width="32%" >
				<html:textarea property="description" cols="30" rows="3" tabindex="2" /></td> 
		  </tr>
		  <tr> 
			<td align="left" class="labeltext" valign="top" width="10%">
				<bean:message bundle="driverResources" key="driver.ldapdriver.ldapdsid"/></td> 
			<td align="left" class="labeltext" valign="top" width="32%" > 
				<html:select name="editDriverInstanceForm" styleId="ldapDsId" property="ldapDsId" size="1" style="width: 206;" tabindex="1">
					<html:option value="0" bundle="driverResources" key="driver.select" />
						<logic:iterate id="ldapDatasources"  name="editDriverInstanceForm" property="ldapDsList" 
							type="com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData">
							<html:option value="<%=Long.toString(ldapDatasources.getLdapDsId())%>">
								<bean:write name="ldapDatasources" property="name"/>
							</html:option>
						</logic:iterate>
				</html:select><font color="#FF0000"> *</font>	
			</td> 
		  </tr>	  
  		  <tr>
			<td align="left" class="labeltext" valign="top" width="10%">
				<bean:message bundle="driverResources" key="driver.ldapdriver.expirydatapattern"/></td> 
			<td align="left" class="labeltext" valign="top" width="32%" > 
				<html:text property="expiryDatePattern"  maxlength="60" size="30" styleClass="c_deviceProfName" tabindex="3"/>
		  </tr>	
 		  <tr>
			<td align="left" class="labeltext" valign="top" width="10%">
				<bean:message bundle="driverResources" key="driver.ldapdriver.passdecrypttype"/></td> 
			<td align="left" class="labeltext" valign="top" width="32%" > 
			    <html:text property="passDecryptType" maxlength="60" size="30" styleClass="c_deviceProfName" tabindex="4" /><font color="#FF0000"> *</font></td> 
		  </tr>				
		  <tr>
			<td align="left" class="labeltext" valign="top" width="10%">
				<bean:message bundle="driverResources" key="driver.ldapdriver.querymaxexectime"/></td> 
			<td align="left" class="labeltext" valign="top" width="32%" > 
			    <html:text property="queryMaxExecTime" maxlength="60" size="30" styleId="queryMaxExecTime" tabindex="5" /><font color="#FF0000"> *</font> </td> 	
		  </tr>
		  <tr>	
	       	<td class="btns-td" valign="middle" >&nbsp;
	       	<html:hidden property="driverInstanceId" styleId="driverInstanceId" name="editDriverInstanceForm" />
	       	<html:hidden property="oldDriverTypeId" value="<%=Long.toString(driverInstanceData.getDriverTypeId())%>" styleId="oldDriverTypeId" name="editDriverInstanceForm" />
	       	<html:hidden property="driverTypeId" value="<%=Long.toString(driverInstanceData.getDriverTypeId())%>" styleId="driverTypeId" name="editDriverInstanceForm" />
	       	<html:hidden property="ldapSPInterfaceId" value="<%=Long.toString(driverInstanceData.getLdapSPInterfaceDriverData().getLdapSPInterfaceId())%>" styleId="ldapDriverId" name="editDriverInstanceForm" />
	       	<html:hidden property="ldapDsId" value="<%=Long.toString(driverInstanceData.getLdapSPInterfaceDriverData().getLdapDsId())%>" styleId="ldapDsId" name="editDriverInstanceForm" />
	       	</td>
	      </tr>	
	      <tr>				 
			<td><input type="button" id="button" value="Add Field Mapping" tabindex="6" class="light-btn"  onclick="openPopup();"></td>
 		  </tr>
		  <tr>
			<td colspan="4">
			<table width="70%" id="mappingtbl" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td align="left" class="tblheader" valign="top" width="5%"><bean:message bundle="driverResources" key="driver.ldapfield.logocalname" /></td>
					<td align="left" class="tblheader" valign="top" width="15%"><bean:message bundle="driverResources" key="driver.ldapfield.attributevalue" /></td>
					<td align="left" class="tblheader" valign="top" width="4%">Remove</td>
				</tr>
				<%int fieldIndex=0; %>
			com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPFieldMapData listBean = (com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPFieldMapData) pageContext.getAttribute("listBean");
			<script>
					mainArray[<%=fieldIndex%>] = '<%=listBean.getLogicalName()%>';
			</script>
				 <tr>
				 	<td class="tblfirstcol" width="70%" height="20%"><bean:write name="listBean" property="logicalName"/><input type='hidden' name = 'logicalName' value="<%=listBean.getLogicalName()%>"/></td>
				 	<td class="tblrows" width="70%" height="20%"><bean:write name="listBean" property="ldapAttribute"/><input type='hidden' name = 'ldapAttribute' value="<%=listBean.getLdapAttribute()%>"/></td>
				 	<td class='tblrows' onclick="remove()"><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' onclick="remove()" /></td>			 	
				 </tr>
				 <%fieldIndex++; %>
			</logic:iterate>
			</table>
			
			</td>
		  </tr>
		  <tr>
		  	<td>&nbsp;</td>
			<td>
				<input type="button" onclick="validate();" value="  Update  " tabindex="7" class="light-btn">
				<input type="button" align="left" value=" Cancel " tabindex="8" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchDriverInstance.do?/>'"/>
			</td>				 	
 		 </tr>
	</table>
	</td>
	</tr>		
	</table>
		</td>
    </tr>
</table>
<%} %>	  
</html:form> 
  	
	</td>
	<td width="168" class="grey-bkgd" valign="top">
      <%@  include file = "ViewDriverInstanceNavigation.jsp" %> 
    </td>
  </tr>
  <tr> 
    <td colspan="3" class="small-gap">&nbsp;</td>
  </tr>
  <tr> 
    <td width="10">&nbsp;</td>
    <td colspan="2" valign="top" align="right"> 
    
      <table width="99%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td width="82%" valign="top"><img src="<%=basePath%>/images/btm-line.jpg"></td>	
          <td align="right" rowspan="2" valign="top"><img src="<%=basePath%>/images/btm-gradient.jpg" width="140" height="23"></td>
        </tr>
        <tr> 
          <td width="82%" valign="top" align="right" class="small-text-grey">Copyright 
            &copy; <a href="http://www.elitecore.com" target="_blank">Elitecore 
            Technologies Pvt.Ltd</a> </td>
        </tr>
      </table>      
    </td>
  </tr>
</table>
</table>


<%if(driverTypeId==1) {%>
<div id="popupfieldMapping" title="Field Mapping" style="display: none;">	

	<table>	 	 
		<tr>
			<td align="left" class="labeltext" valign="top" >
				<bean:message bundle="driverResources" key="driver.dbfield.logicalname"/></td>
			<td align="left" class="labeltext" valign="top" width="30%">
				<html:select name="editDriverInstanceForm" styleId="logicalName" property="logicalName" size="1" style="width: 180;">
					<html:option value="0" bundle="driverResources" key="driver.select" />
					<logic:iterate id="logicalNames"  name="logicalNamePoolList" 
						type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData">
						<html:option value="<%=logicalNames.getName()%>">
							<bean:write name="logicalNames" property="name"/>
						</html:option>
					</logic:iterate>
				</html:select>								      											
			</td>
		</tr>			  	
		<tr>
			<td align="left" class="labeltext" valign="top" width="20%">
				<bean:message bundle="driverResources" key="driver.dbfield"/></td>
			<td align="left" class="labeltext" valign="top" width="32%">
				<html:text name="editDriverInstanceForm" styleId="dbField" property="dbField" size="23" maxlength="60"/>
			</td>
		</tr>
	</table>
	
		
</div>
<%} %>
<%if(driverTypeId==2) {%>
<div id="popupfieldMapping" title="LDAP Field Mapping" style="display: none;">	
	<table>	 	 
		<tr>
			<td align="left" class="labeltext" valign="top" >
				<bean:message bundle="driverResources" key="driver.ldapfield.logocalname"/></td>
			<td align="left" class="labeltext" valign="top" width="30%">
				<html:select name="editDriverInstanceForm" styleId="logicalName" property="logicalName" size="1" style="width: 180;">
					<html:option value="0" bundle="driverResources" key="driver.select" />
					<logic:iterate id="logicalNames"  name="editDriverInstanceForm" property="logicalNamePoolList" 
						type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData">
						<html:option value="<%=logicalNames.getName()%>">
							<bean:write name="logicalNames" property="name"/>
						</html:option>
					</logic:iterate>
				</html:select>								      											
			</td>
		</tr>			  	
		<tr>
			<td align="left" class="labeltext" valign="top" width="20%">
				<bean:message bundle="driverResources" key="driver.ldapfield.attributevalue"/></td>
			<td align="left" class="labeltext" valign="top" width="32%">
				<html:text name="editDriverInstanceForm" styleId="ldapAttribute" property="ldapAttribute" size="23" maxlength="60"/>
			</td>
		</tr>
	</table>								
</div>
<%} %>

<%@ include file="/jsp/core/includes/common/Footer.jsp" %>
			  	 