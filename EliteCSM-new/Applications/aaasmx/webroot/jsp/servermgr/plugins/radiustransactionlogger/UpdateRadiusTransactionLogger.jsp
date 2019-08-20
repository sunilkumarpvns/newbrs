 <!-- Import Parameters -->
 <%@ page import="com.elitecore.elitesm.web.plugins.forms.TransactionLoggerForm"%>
 <%@ page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
 <%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
 <%@ include file="/jsp/core/includes/common/Header.jsp"%>
 
<% 
		TransactionLoggerForm transactionLoggerForm = (TransactionLoggerForm)request.getSession().getAttribute("transactionLoggerForm");
%>

<html:form action="/updateRadiusTransactionLogger" styleId="radiusTransactionLogger" method="post" enctype="multipart/form-data">

	<html:hidden name="transactionLoggerForm" property="action" styleId="action" value="update" />
	<html:hidden name="transactionLoggerForm" property="pluginType" styleId="pluginType" />
	<html:hidden name="transactionLoggerForm" property="auditUId" styleId="auditUId" />
	<html:hidden name="transactionLoggerForm" property="pluginInstanceId" styleId="pluginInstanceId" />
	<html:hidden name="transactionLoggerForm" property="formatMappingsJson" styleId="formatMappingsJson" />
	<html:hidden name="transactionLoggerForm" property="groovyFileName" styleId="groovyFileName" />
							
	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >            						  	
		<tr>
			<td class="tblheader-bold" height="20%" colspan="3">
				<bean:message bundle="pluginResources" key="plugin.update.plugininstancedetails" />
			</td>
		</tr>
		<tr>
			<td class="captiontext padding-top" width="30%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.instname" />
				<ec:elitehelp headerBundle="pluginResources" text="plugin.name" header="plugin.name"/>
			</td>
			<td class="labeltext padding-top" width="22%" height="20%" >
				<html:text styleId="pluginName" onkeyup="verifyName();" property="pluginName" size="40" maxlength="70" style="width:250px" tabindex="1" />
				<font color="#FF0000"> *</font>
				<div id="verifyNameDiv" class="labeltext"></div>
			</td>
			<td class="labeltext padding-top" width="40%" valign="top">
				<html:checkbox styleId="status" property="status" value="1" tabindex="2" />Active
			</td>
		</tr>
		<tr>
			<td class="captiontext" width="30%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.description" />
				<ec:elitehelp headerBundle="pluginResources" text="plugin.description" header="plugin.description"/>
			</td>
			<td class="labeltext padding-bottom" width="*" height="20%">
				<html:textarea styleId="description" property="description" cols="40" rows="4" style="width:250px" tabindex="2" />
			</td>
		</tr>
		<tr>
			<td class="captiontext padding-top" width="30%" height="20%" style="padding-top: 5px;">
				<bean:message bundle="pluginResources" key="plugin.transactionlogger.timeboundry" />
				<ec:elitehelp headerBundle="pluginResources" text="plugin.transactionlogger.timeboundry" header="plugin.transactionlogger.timeboundry"/>
			</td>
			<td class="labeltext padding-top" width="*" height="20%" style="padding-top: 5px;">
				<html:select property="timeBoundry" styleId="timeBoundry" style="width: 130px" tabindex="12">
					<html:option value="1">1 Min</html:option>
					<html:option value="2">2 Min</html:option>
					<html:option value="3">3 Min</html:option>
					<html:option value="5">5 Min</html:option>
					<html:option value="10">10 Min</html:option>
					<html:option value="15">15 Min</html:option>
					<html:option value="20">20 Min</html:option>
					<html:option value="30">30 Min</html:option>
					<html:option value="60">Hourly</html:option>
					<html:option value="1440">Daily</html:option>
				</html:select>
				<font color="#FF0000"> *</font>
			</td>
		</tr>
		<tr>
			<td class="captiontext padding-top" width="30%" height="20%">
				<bean:message bundle="pluginResources" key="plugin.transactionlogger.logfile" />
				<ec:elitehelp headerBundle="pluginResources" text="plugin.transactionlogger.logfile" header="plugin.transactionlogger.logfile"/>
			</td>
			<td class="labeltext padding-top" width="*" height="20%">
				<html:text styleId="logFile" property="logFile" style="width:250px" tabindex="13" maxlength="1024" />
			</td>
		</tr>
		<tr>
			<td align="left" class="captiontext" valign="top">
				<bean:message bundle="pluginResources" key="plugin.range" />
				<ec:elitehelp headerBundle="pluginResources" text="plugin.transactionlogger.sequencerange" header="plugin.range" />
			</td>
			<td align="left" class="labeltext" valign="top" colspan="3">
				<html:text styleId="range" property="range" size="20" tabindex="14" maxlength="50" style="width:250px" />
			</td>
		</tr>
		<tr>
			<td align="left" class="captiontext" valign="top">
				<bean:message bundle="pluginResources" key="plugin.pos" />
				<ec:elitehelp headerBundle="pluginResources" text="plugin.transactionlogger.pattern" header="plugin.detail.pattern" />
		 	</td>
			<td align="left" class="labeltext" valign="top" colspan="3">
				<html:select property="pattern" styleId="pattern" style="width: 130px" tabindex="15">
						<html:option value="suffix">Suffix</html:option>
						<html:option value="prefix">Prefix</html:option>
				</html:select>
			</td>
		</tr>
		<tr>
			<td align="left" class="captiontext" valign="top">
			<bean:message bundle="pluginResources" key="plugin.global" /> 
			<ec:elitehelp headerBundle="pluginResources" text="plugin.transactionlogger.globalization" header="plugin.global" /></td>
			<td align="left" class="labeltext" valign="top" colspan="3">
				<html:select property="globalization" styleId="global" style="width: 130px" tabindex="16">
						<html:option value="false">False</html:option>
						<html:option value="true">True</html:option>
					</html:select>
			</td>
		</tr>
		<tr>
			<td class="tblheader-bold" colspan="3">
				<bean:message bundle="pluginResources" key="plugin.transactionlogger.formatmappings" />
			</td>
		</tr>
		<tr>
			<td class="captiontext" colspan="3" style="padding-top: 10px;">
				<input type="button" value="Add Format" class="light-btn" onclick="addTransactionLogger();"/> 
			</td>
		</tr>
		<tr>
			<td class="captiontext" colspan="3">
				<table cellspacing="0" cellpadding="0" border="0" width="80%" id="mappingTable">
					<tr>
						<td class="tblheader" width="20%">
							<bean:message bundle="pluginResources" key="plugin.transactionlogger.formatmappings.key" />
							<ec:elitehelp headerBundle="pluginResources" text="plugin.transactionlogger.formatmappings.key" header="plugin.transactionlogger.formatmappings.key"/>
						</td>
						<td class="tblheader">
							<bean:message bundle="pluginResources" key="plugin.transactionlogger.formatmappings.format" />
							<ec:elitehelp headerBundle="pluginResources" text="plugin.transactionlogger.formatmappings.format" header="plugin.transactionlogger.formatmappings.format"/>
						</td>
						<td class="tblheader" width="10%" style="border-right: 2px solid #D9E6F6;">		
							<bean:message bundle="pluginResources" key="plugin.transactionlogger.formatmappings.remove" />
						</td>
					</tr>
					<logic:iterate id="obj" name="transactionLoggerForm" property="formatMappingDataSet">
					<tr>
						<td class="tblfirstcol" width="30%">
							<input type="text" name="key" class="noborder" style="width: 100%" value="<bean:write name='obj' property='key' />"/>
						</td>
						<td class="tblrows">
							<textarea rows="1" cols="5" style="width: 100%;height:18px;" class="noborder" name="format"><bean:write name='obj' property='format'  /></textarea>
						</td>
						<td class="tblrows" width="10%" align="center">
							<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
						</td>
					</tr>
					</logic:iterate>
				</table>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>
				<input type="button" id="submitLogger" value="Update" class="light-btn" onclick="validateTransactionLogger()"/>
				<input type="button" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=basePath%>/searchPlugin.do?'" />
			</td>
		</tr>
	</table>
	
</html:form>

<table id="templateTable" style="display: none;" width="100%" cellspacing="0" cellpadding="0" border="0">
		<tr>
			<td class="tblfirstcol" width="20%">
				<input type="text" name="key" class="noborder" style="width: 100%" />
			</td>
			<td class="tblrows">
				<textarea rows="1" cols="5" style="width: 100%;height:18px;" class="noborder" name="format"></textarea>
			</td>
			<td class="tblrows" width="10%" align="center">
				<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
			</td>
		</tr>
</table>

<script type="text/javascript">
	var isValidName;
	/** Setting title in main header bar*/
	setTitle('Radius Transaction Logger');

	function verifyName() {
		var searchName = document.getElementById("pluginName").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.PLUGIN%>',searchName,'update','<%=transactionLoggerForm.getPluginInstanceId()%>','verifyNameDiv');
	}
	
	/** This function is used for adding new plugin in corresponding mapping table */
	function addTransactionLogger() {
		var tableRowStr = $("#templateTable").find("tr");
		$("#mappingTable").find(' > tbody:last-child').append( "<tr>" + $(tableRowStr).html() +"</tr>" );
	}
	
	/* This function is used for delete entry of format mappings */
	function deleteMe(spanObject){
		$(spanObject).parent().parent().remove();
	}
	
	/* This function is used for validating fields and submit form */
	function  validateTransactionLogger(){
		
		if($('#pluginName').val() == ""){
			alert('Name must be specified');
			$('#pluginName').focus();
		}else if(!isValidName) {
			alert('Enter Valid Plugin Name');
			document.forms['radiusTransactionLogger'].pluginName.focus();
			return;
		}else if( !isValidMappings() ){
			return false;	
		}else{
			var formatMappings = [];
			
			$('table#mappingTable > tbody > tr').each(function(){
				
				   var key,format;
				
				   if(typeof $(this).find("textarea[name='format']").val() !== 'undefined'){
					   format=  $(this).find("textarea[name='format']").val();
				   }
				   
				   if(typeof $(this).find("input[name='key']").val() !== 'undefined'){
					   key=  $(this).find("input[name='key']").val();
				   }
				   
				   if(  !isEmpty(format) || !isEmpty(key)){
					   formatMappings.push({'key':key,'format':format});
				   }
			});
			
			$('#formatMappingsJson').val(JSON.stringify(formatMappings));
			document.forms['radiusTransactionLogger'].submit();
		}
	}
	
	function isValidMappings(){
		
		var returnValue = true;
		
		var parameterTableObject = $('table#mappingTable');
		var totalRows = parameterTableObject.find('tr').length;
		var valueIsConfigured = false;
		
		if( totalRows == 1){
			valueIsConfigured= false;
		}else if(totalRows > 1){
			valueIsConfigured = true;
		}
		
		if(valueIsConfigured == false){
			alert('Atleast one mapping is required in Mappings');
			returnValue = false;
			return false;
		}else{
			
			$('table#mappingTable > tbody > tr').each(function(){
				
				   var format,key;
				
				   if(typeof $(this).find("input[name='key']").val() !== 'undefined'){
					   key =  $(this).find("input[name='key']").val();
							if( key.length == 0 ){
								 $(this).find("input[name='key']").focus();
								  alert('Please Enter Key');
								  returnValue = false;
								  return false;
					   }
				   }
				   
				   if(typeof $(this).find("textarea[name='format']").val() !== 'undefined'){
					   format =  $(this).find("textarea[name='format']").val();
					   if( format.length == 0 ){
						   $(this).find("textarea[name='format']").focus();
						   alert('Please Enter Format');
						   returnValue = false;
						   return false;  
					   }
				   }
			});
		}
		
		return returnValue;
	}
	
</script>