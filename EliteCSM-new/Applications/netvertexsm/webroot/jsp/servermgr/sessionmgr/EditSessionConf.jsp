<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionConfData"%>
<%@ page import="java.util.*" %> 
<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyType"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>

<%
	SessionConfData sessionConfData= (SessionConfData)request.getAttribute("sessionConfData");
%>

<script language = "javascript">
var mainArray = new Array();
var mainArray1 = new Array();
var refArray = new Array(); 
var count = 0;
var count1 = 0;
function openPopup(op){	
	policyKey();
	$.fx.speeds._default = 1000;
	document.getElementById("popupfieldMapping").style.visibility = "visible";		
	$( "#popupfieldMapping" ).dialog({
		modal: true,
		autoOpen: false,		
		height: 300,
		width: 500,				
		buttons:{					
            'Add': function() {
					var name = $('#fieldName').val();
					var name0 = $('#referEntity').val(); 
					var name1 = $('#datatype').val();
		          	refArray[count1++] = name0;
	          		if(isNull($('#fieldName').val())){
		     			alert('Field Name value must be specified.');
		     		}else if(isNull($('#referEntity').val())){
		     			alert('Referring Entity value must be specified.');
		     		}else if(isNull($('#datatype').val())){
		     			alert('Data type value must be specified.');
		     		}else{	

			     		if(op==1){
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
			         			var dataTypeLabel;	
				         		if(name1=='0'){
				         			dataTypeLabel="String";
				         		}else if(name1=='1'){
				         			dataTypeLabel="Timestamp";
				         		}
				         		
				         		$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + name + "<input type='hidden' name = 'coreDBField' value='" + name + "'/>" +"</td><td class='tblrows'>" + name0 + "<input type='hidden' name = 'corerefrngattr' id = 'ref' value='" + name0 + "'/>" +"</td><td class='tblrows'>" + dataTypeLabel + "<input type='hidden' name = 'coredatatype' value='" + name1 + "'/>" +"</td><td align='center' class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
				         		
				         				
			         			$('#mappingtbl td img.delete').on('click',function() {
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
				       				$(this).parent().parent().remove(); 
			       				});		
			         			
				          		mainArray[count++] = name;				          					          							          					          		
				          		$(this).dialog('close');
				         	}	         				    		         			   				         			          				          		
			         	}
			     		else
			     		{
			     			var i = 0;							
							var flag = true;												 	
							if(document.getElementById('mappingtbl1').getElementsByTagName('tr').length >= 2){												
								for(i=0;i<mainArray1.length;i++){									
									var value = mainArray1[i];																						
									if(value == name){
										alert("Mapping with this value is already present so add another value for attribute");
										flag = false;
										break;
									}
								}
							}								
			         		if(flag){	
			         			var dataTypeLabel;	
				         		if(name1=='0'){
				         			dataTypeLabel="String";
				         		}else if(name1=='1'){
				         			dataTypeLabel="Timestamp";
				         		}
				         		
				         		$("#mappingtbl1 tr:last").after("<tr><td class='tblfirstcol'>" + name + "<input type='hidden' name = 'subDBField' value='" + name + "'/>" +"</td><td class='tblrows'>" + name0 + "<input type='hidden' name = 'subrefrngattr' id = 'ref' value='" + name0 + "'/>" +"</td><td class='tblrows'>" + dataTypeLabel + "<input type='hidden' name = 'subdatatype' value='" + name1 + "'/>" +"</td><td align='center' class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
				         		
				         			
			         			$('#mappingtbl1 td img.delete').on('click',function() {
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
				       				$(this).parent().parent().remove(); 
			       				});		
			         			
				          		mainArray1[count++] = name;				          					          							          					          		
				          		$(this).dialog('close');
				         	}
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
			console.log("close function called");
    		document.getElementById("fieldName").value = "";
			document.getElementById("referEntity").value = "";
			document.getElementById("defval").value = "";  
    		document.getElementById("c_btnCreate2").focus();
    	}				
	});
	$( "#popupfieldMapping" ).dialog("open");
}

$(document).ready(function(){
	$('#mappingtbl td img.delete').on('click',function(){
		 var rowid=$(this).closest('tr').find('td:eq(0)').text();
	     $(rowid).removeAttr("style");
	     $(this).closest('tr').remove();	     			      
   });
	$('#mappingtbl1 td img.delete').on('click',function(){
		 var rowid=$(this).closest('tr').find('td:eq(0)').text();
	     $(rowid).removeAttr("style");
	     $(this).closest('tr').remove();	     			      
  });
	setBatchUpdate();
});

function setBatchUpdate() {
	var batchUpdate = document.getElementById("batchUpdate1").value;
	if(batchUpdate == 'false' || batchUpdate == '0') {
		document.getElementById("batchSize").disabled = true;
		document.getElementById("batchUpdateInterval").disabled = true;
		document.getElementById("dbQueryTimeout").disabled = true;
	}else{
		document.getElementById("batchSize").disabled = false;
		document.getElementById("batchUpdateInterval").disabled = false;
		document.getElementById("dbQueryTimeout").disabled = false;
	}
}

function enableAll(){
	document.getElementById("batchSize").disabled = false;
	document.getElementById("batchUpdateInterval").disabled = false;
	document.getElementById("dbQueryTimeout").disabled = false;
}


function remove(){

		$('#mappingtbl td img.delete').on('click',function(){
			 var rowid=$(this).closest('tr').find('td:eq(0)').text();
		     $(rowid).removeAttr("style");
		     $(this).closest('tr').remove();	     			      
	    });
		
}
function removeBearer(){

	$('#mappingtblB td img.delete').on('click',function(){
		 var rowid=$(this).closest('tr').find('td:eq(0)').text();
	     $(rowid).removeAttr("style");
	     $(this).closest('tr').remove();	     			      
    });
	
}

function policyKey() {
	var mappingTypeArray = new Array();
	mappingTypeArray[0] = "<%=PCRFKeyType.REQUEST.getVal()%>";
	mappingTypeArray[1] = "<%=PCRFKeyType.RESPONSE.getVal()%>";
	
	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.POLICY_KEY%>,propertyName:"serviceName",mappingTypeArray:mappingTypeArray}, function(data){
		dbFieldStr = data.substring(1,data.length-3);
		var dbFieldArray = new Array();
		dbFieldArray = dbFieldStr.split(", ");
		commonAutoComplete("referEntity",dbFieldArray);					 
		return dbFieldArray;
	});
}
function validateCreate(){
	if(document.forms[0].dataSourceID.value == '0'){
		alert('Primary DataSource must be specified.');
		return false;
	}else{
		
		if(document.forms[0].batchUpdate1.value == '1' || document.forms[0].batchUpdate1.value == '2'){
			if(!validateBatchUpdateParams()){
				return;
			}
		}
		enableAll();
		document.forms[0].submit();
	}  
}
function validateBatchUpdateParams(){
	if(isNull(document.forms[0].batchSize.value)){
		alert('Batch Size must be specified');
		document.forms[0].batchSize.focus();
		return false;
	}else if(isEcNaN(document.forms[0].batchSize.value)){
		alert('Batch Size must be Numeric');
		document.forms[0].batchSize.focus();
		return false;
	}else if(document.forms[0].batchSize.value < 10 || document.forms[0].batchSize.value > 2000){
		alert('Batch Size must be between 10 to 2000');
		document.forms[0].batchSize.focus();
		return false;
	}else if(isNull(document.forms[0].batchUpdateInterval.value)){		
		alert('Batch Update Interval must be specified.');
		document.forms[0].batchUpdateInterval.focus();
		return false;
	}else if(isEcNaN(document.forms[0].batchUpdateInterval.value)){
		alert('Batch Update Interval must be Numeric');
		document.forms[0].batchUpdateInterval.focus();
		return false;
	}else if(document.forms[0].batchUpdateInterval.value < 1 || document.forms[0].batchUpdateInterval.value > 100){
		alert('Batch Update Interval must be between 1 and 100');
		document.forms[0].batchUpdateInterval.focus();
		return false;
	}else if(isNull(document.forms[0].dbQueryTimeout.value)){
		alert('DB Query TimeOut must be specified.');
		document.forms[0].dbQueryTimeout.focus();
		return false;
	}else if(document.forms[0].dbQueryTimeout.value <= 0){
		alert('DB Query TimeOut must be positive.');
		document.forms[0].dbQueryTimeout.focus();
		return false;
	}else if(isEcNaN(document.forms[0].dbQueryTimeout.value)){
		alert('DB Query TimeOut must be Numeric');
		document.forms[0].dbQueryTimeout.focus();
		return false;
	}
	return true;
}

$(document).ready(function(){
	setTitle('<bean:message bundle="sessionMgrResources" key="session.sessionconfiguration" />');
	$("#dataSourceID").focus();
});

</script>

<style> 
.bottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.topbottomborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 1px; border-right-width: 0px; border-bottom-width: 1px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
.noborder { font-family: Arial; font-size: 12px; color: #000000; border: #CCCCCC; border-style: solid; border-top-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-left-width: 0px ; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 3px; line-height: 19px}
</style>
 
<html:form action="/editSessionConf">
<table cellpadding="0" cellspacing="0" border="0" width="100%" >
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr>
  <td></td>
  <td width="85%" valign="top" class="box">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	  <tr> 
	    <td >
	
		<bean:define id="sessionInstanceBean" name="sessionConfData" scope="request" type="com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data.SessionConfData"/>
			<table cellpadding="0" cellspacing="0" border="0" width="100%">
		    <tr>
				<td valign="top" align="right" colspan="4"> 
				
		<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
			<tr>
				<td align="right" class="labeltext" valign="top" class="" > 
				
		 
		  		<table width="97%" id="c_tblCrossProductList" align="right" border="0" > 
		  			<tr>
		  			  	<td class="tblheader-bold" colspan="8" height="20%" width="100%">
		  			  		<bean:message bundle="sessionMgrResources" key="session.updatesessionconf" />
		  			  	</td>
		  			  </tr>
		  			  <tr>
				       	 <td width="10" class="small-gap">&nbsp;</td>
				       	 <td class="small-gap" colspan="2">&nbsp;</td>
					  </tr>						  
					  <tr>
						<td align="left" class="labeltext" valign="top" width="35%">
							<bean:message bundle="sessionMgrResources" key="session.gx.datasource"/>
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="session.gx.datasource"/>','<bean:message bundle="sessionMgrResources" key="session.gx.datasource" />')"/>	
						</td>
						<td align="left" class="labeltext" valign="top" width="65%" colspan="0">
							<html:select name="createSessionConfForm" styleId="dataSourceID" tabindex="3" property="dataSourceID" size="1" style="width: 217;">
								<html:option value="0">Select</html:option>
								<logic:iterate id="datasources"  name="createSessionConfForm" property="databaseDSList" type="com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData">
									<html:option value="<%=Long.toString(datasources.getDatabaseId())%>"><bean:write name="datasources" property="name"/></html:option>
								</logic:iterate>
							</html:select><font color="#FF0000"> *</font>	      
						</td>
					  </tr>
					  <tr>
						<td align="left" class="labeltext" valign="top" width="35%">
							<bean:message bundle="sessionMgrResources" key="session.gx.secondary.datasource"/>
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="session.gx.secondary.datasource"/>','<bean:message bundle="sessionMgrResources" key="session.gx.secondary.datasource" />')"/>
						</td>
						<td align="left" class="labeltext" valign="top" width="65%" colspan="0">
							<html:select name="createSessionConfForm" styleId="secondaryDataSourceID" tabindex="4" property="secondaryDataSourceID" size="1" style="width: 217;">
								<html:option value="0">Select</html:option>
								<logic:iterate id="datasources"  name="createSessionConfForm" property="databaseDSList" type="com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData">
									<html:option value="<%=Long.toString(datasources.getDatabaseId())%>"><bean:write name="datasources" property="name"/></html:option>
								</logic:iterate>
							</html:select>	      
						</td>
					  </tr>
					<tr> 
                    	<td class="tblheader-bold"  height="20%" colspan="4"><bean:message bundle="sessionMgrResources" key="session.viewbatchupdateproperties"/></td>
                  	</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" >
							<bean:message bundle="sessionMgrResources" key="session.batchupdate"/>
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="session.batchupdate"/>','<bean:message bundle="sessionMgrResources" key="session.batchupdate" />')"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
					    	<html:select name="createSessionConfForm" styleId="batchUpdate1" tabindex="6" property="batchUpdate" size="1"  onchange="setBatchUpdate();">
						   		<html:option value="1"><bean:message bundle="sessionMgrResources" key="session.batchupdatevalue.true"/></html:option>
						   		<html:option value="0" ><bean:message bundle="sessionMgrResources" key="session.batchupdatevalue.false"/></html:option>
						   		<html:option value="2"><bean:message bundle="sessionMgrResources" key="session.batchupdatevalue.other"/></html:option> 
						   		
							</html:select>      
						</td>
				 	 </tr>
				  	<tr>
						<td align="left" class="labeltext" valign="top" >
							<bean:message bundle="sessionMgrResources" key="session.batchsize"/>
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="session.batchsize"/>','<bean:message bundle="sessionMgrResources" key="session.batchsize" />')"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
						    <html:text styleId="batchSize" property="batchSize" size="30" tabindex="7" maxlength="4" /><font color="#FF0000"> *</font>
						</td>
				 	 </tr>	
				  
					  <tr>
						<td align="left" class="labeltext" valign="top" >
							<bean:message bundle="sessionMgrResources" key="session.batchupdateinterval"/>
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="session.batchupdateinterval"/>','<bean:message bundle="sessionMgrResources" key="session.batchupdateinterval" />')"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
					    	<html:text styleId="batchUpdateInterval" property="batchUpdateInterval" tabindex="8" size="30" maxlength="8" /><font color="#FF0000"> *</font>
						</td>
				 	 </tr>	
				  
				  	<tr>
						<td align="left" class="labeltext" valign="top" >
							<bean:message bundle="sessionMgrResources" key="session.querytimeout"/>
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="session.querytimeout"/>','<bean:message bundle="sessionMgrResources" key="session.querytimeout" />')"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="3">
					    	<html:text styleId="dbQueryTimeout" property="dbQueryTimeout" size="30" tabindex="9" maxlength="8" /><font color="#FF0000"> *</font>
						</td>
				  	</tr>
					
					<tr>
				    	<td width="10" class="small-gap">&nbsp;</td>
				       	<td class="small-gap" colspan="2">&nbsp;</td>
					</tr>	
					  	  
		<!-- 		DB field mapping for core session 		 -->				  
					  <tr> 
	       				 <td class="tblheader-bold" colspan="4" height="20%" width="100%">
	       				 	<bean:message bundle="sessionMgrResources" key="session.coresessionfieldmapping"/>
	       				 	<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="session.coresessionfieldmapping"/>','<bean:message bundle="sessionMgrResources" key="session.coresessionfieldmapping" />')"/>
	       				 </td>
	    			  </tr>
					  	
	                  <tr>
				       	 <td width="10" class="small-gap">&nbsp;</td>
				       	 <td class="small-gap" colspan="2">&nbsp;</td>
					  </tr>			  			
					  <!-- add button  -->
					  <tr>				 
						  <td align="left" style="padding-left: 2.5em" ><input type="button" tabindex="14" value="   Add   " class="light-btn" onclick="openPopup(1);"></td>
					  </tr>
					  <!--  display added db field map list -->	 
					  <tr>
						<td width="100%" colspan="5" valign="top" style="padding-left: 2.6em">
							<table cellSpacing="0" cellPadding="0" width="90%" border="0" id="mappingtbl">
								<tr>								
									<td align="left" class="tblheaderfirstcol" valign="top" width="25%"><bean:message bundle="sessionMgrResources" key="session.gx.dbfieldname"/></td>								
									<td align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="sessionMgrResources" key="session.gx.referringattr"/></td>
				                    <td align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="sessionMgrResources" key="session.gx.datatype"/></td>    
				                    <td align="left" class="tblheaderlastcol" valign="top" width="5%">Remove</td>
				                </tr>
				                 <%for(int i=0;i<sessionConfData.getCoreSessionList().size();i++){ %>
				                			
									<tr>
							 			<td class="tblfirstcol" width="*" height="*"><%=sessionConfData.getCoreSessionList().get(i).getFieldName() %><input type='hidden' name = 'coreDBField' value="<%=sessionConfData.getCoreSessionList().get(i).getFieldName()%>"/></td>
				 						<td class="tblrows" width="*" height="*"><%=sessionConfData.getCoreSessionList().get(i).getReferringAttr() %><input type='hidden' name = 'corerefrngattr' value="<%=sessionConfData.getCoreSessionList().get(i).getReferringAttr() %>"/></td>
				 						<td class="tblrows" width="*" height="*"><%if(sessionConfData.getCoreSessionList().get(i).getDatatype() == 0){%>String<%	} else if(sessionConfData.getCoreSessionList().get(i).getDatatype() == 1){%>TimeStamp<%}%><input type='hidden' name = 'coredatatype' value="<%=sessionConfData.getCoreSessionList().get(i).getDatatype()%>"/></td>
				 						<td class='tblrows' align='center' ><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' onclick="removeMe()" /></td>
				 					</tr>
								<%} %>
			            	</table>
			            </td>            	
					  </tr>
					  <tr>
				       	 <td width="10" class="small-gap">&nbsp;</td>
				       	 <td class="small-gap" colspan="2">&nbsp;</td>
					  </tr>	
 					  <tr> 
					 	<td class="tblheader-bold" colspan="4" height="20%" width="100%">
					 		<bean:message bundle="sessionMgrResources" key="session.subsessionfieldmapping"/>
					 		<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="session.subsessionfieldmapping"/>','<bean:message bundle="sessionMgrResources" key="session.subsessionfieldmapping" />')"/>
					 	</td>
	                  </tr>	
	                  <tr>
				       	 <td width="10" class="small-gap">&nbsp;</td>
				       	 <td class="small-gap" colspan="2">&nbsp;</td>
					  </tr>			  			
					  <!-- add button  -->
					  <tr>				 
						  <td align="left" style="padding-left: 2.5em" ><input type="button" tabindex="15" value="   Add   " class="light-btn" onclick="openPopup(2);"></td>
					  </tr>
					  <!--  display added db field map list -->	 
					  <tr>
						<td width="100%" colspan="5" valign="top" style="padding-left: 2.6em">
							<table cellSpacing="0" cellPadding="0" width="90%" border="0" id="mappingtbl1">
								<tr>								
									<td align="left" class="tblheaderfirstcol" valign="top" width="25%"><bean:message bundle="sessionMgrResources" key="session.gx.dbfieldname"/></td>								
									<td align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="sessionMgrResources" key="session.gx.referringattr"/></td>
				                    <td align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="sessionMgrResources" key="session.gx.datatype"/></td>    
				                    <td align="left" class="tblheaderlastcol" valign="top" width="5%">Remove</td>
				                </tr>
				                 <%for(int i=0;i<sessionConfData.getSubSessionList().size();i++){ %>
									<tr>
							 			<td class="tblfirstcol" width="*" height="*"><%=sessionConfData.getSubSessionList().get(i).getFieldName() %><input type='hidden' name = 'subDBField' value="<%=sessionConfData.getSubSessionList().get(i).getFieldName()%>"/></td>
				 						<td class="tblrows" width="*" height="*"><%=sessionConfData.getSubSessionList().get(i).getReferringAttr() %><input type='hidden' name = 'subrefrngattr' value="<%=sessionConfData.getSubSessionList().get(i).getReferringAttr() %>"/></td>
				 						<td class="tblrows" width="*" height="*"><%if(sessionConfData.getSubSessionList().get(i).getDatatype() == 0){%>String<%	} else if(sessionConfData.getSubSessionList().get(i).getDatatype() == 1){%>TimeStamp<%}%><input type='hidden' name = 'subdatatype' value="<%=sessionConfData.getSubSessionList().get(i).getDatatype()%>"/></td>
				 						<td class='tblrows' align='center' ><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' onclick="removeMe()" /></td>
				 					</tr>
								<%} %>
			            	</table>
			            </td>            	
					</tr>
					<tr>
				       	 <td width="10" class="small-gap">&nbsp;</td>
				       	 <td class="small-gap" colspan="2">&nbsp;</td>
					</tr>	
	         		<tr> 
	        			<td><html:hidden property="sessionConfID" styleId="sessionConfID" name="createSessionConfForm" /></td>
		        		<td class="btns-td" valign="middle" align="left"> 
			        		<input type="button" align="left" onclick="validateCreate();" tabindex="16" value="  Update  " class="light-btn" />
			        		<input type="reset" tabindex="17" onclick="javascript:location.href='<%=basePath%>/viewSessionConf.do?/>'" value="Cancel" class="light-btn" >
			        	</td> 
	   		 		</tr>			
		 	   </table>
		 	   </td></tr>
		 	   </table>
		  	</td>
		  </tr>
		 	</table>
		  </td>
		</tr>
		</table> 
	</td>
	<td width="15%" class="grey-bkgd" valign="top">
      	<%@  include file = "SessionConfNavigation.jsp" %> 
    </td>
  </tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

</html:form>  

<div id="popupfieldMapping" title="Field Mapping" style="display: none;">	
		<table>	 	 
		  <tr>
			<td align="left" class="labeltext" valign="top" width="20%">
				<bean:message bundle="sessionMgrResources" key="session.gx.dbfieldname"/></td>
			<td align="left" class="labeltext" valign="top" width="32%">
				<input type="text" id="fieldName" name="gxDBFieldName" size="25" maxlength="60" style="width:250px"/>
				<font color="#FF0000"> *</font>
			</td>
		  </tr>
		 
		  <tr>
			<td align="left" class="labeltext" valign="top" width="20%">
				<bean:message bundle="sessionMgrResources" key="session.gx.referringattr"/></td>
			<td align="left" class="labeltext" valign="top" width="32%">
			<%-- 	<html:text styleId="referEntity" property="referringEntity" size="25" maxlength="60" style="width:200px"/>    --%>
				<input type="text" name="gxReferringAttr" id="referEntity" size="30" autocomplete="off" style="width:250px"/>
				<font color="#FF0000"> *</font>
			</td>
		  </tr>
		  
		  <tr>
			<td align="left" class="labeltext" valign="top" width="20%">
			<bean:message bundle="sessionMgrResources" key="session.gx.datatype"/></td>
			<td align="left" class="labeltext" valign="top" width="32%">
			    <html:select name="sessionConfData" styleId="datatype" property="dataType" size="1" value="Select">
				   <html:option value="0">String</html:option>
				   <html:option value="1">Timestamp</html:option>
				</html:select><font color="#FF0000"> *</font>	      
			</td>
		  </tr>	
		  
		</table>								
</div>

<%@ include file="/jsp/core/includes/common/Footer.jsp" %>