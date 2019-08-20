<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.elitesm.web.plugins.forms.CreateUniversalAuthPluginForm"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.List"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>

<% String basePath = request.getContextPath(); %>

	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<div id="main">
								<html:form action="createDiameterTransactionLoggerPlugin" styleId="diameterTransactionLogger" method="post">
					
								<html:hidden name="transactionLoggerForm" property="action" value="create" /> 
								<html:hidden name="transactionLoggerForm" property="pluginName" styleId="pluginName" />
								<html:hidden name="transactionLoggerForm" property="description" styleId="description" />
								<html:hidden name="transactionLoggerForm" property="pluginType" styleId="pluginType" />
								<html:hidden name="transactionLoggerForm" property="formatMappingsJson" styleId="formatMappingsJson" />
								<html:hidden name="transactionLoggerForm" property="status" styleId="status" />
					
								<table cellpadding="0" cellspacing="0" border="0" width="100%">
									<tr>
										<td class="table-header" colspan="2">
											<bean:message bundle="pluginResources" key="plugin.diameter.transactionlogger.title" />
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
											<html:text styleId="logFile" property="logFile" style="width:250px" tabindex="2" maxlength="1024"/>
										</td>
									</tr>
									<tr>
										<td align="left" class="captiontext" valign="top">
			                                 <bean:message bundle="pluginResources" key="plugin.range" />
											 <ec:elitehelp headerBundle="pluginResources" text="plugin.transactionlogger.sequencerange" header="plugin.range" />
										</td>
										<td align="left" class="labeltext" valign="top" colspan="3">
											<html:text styleId="range" property="range" size="20" tabindex="16" maxlength="50" style="width:250px" />
										</td>
									</tr>
									<tr>
										<td align="left" class="captiontext" valign="top">
											<bean:message bundle="pluginResources" key="plugin.pos" />
											<ec:elitehelp headerBundle="pluginResources" text="plugin.transactionlogger.pattern" header="plugin.detail.pattern" />
										</td>
										<td align="left" class="labeltext" valign="top" colspan="3">
											<html:select property="pattern" styleId="pattern" style="width: 130px" tabindex="17">
												<html:option value="suffix">Suffix</html:option>
												<html:option value="prefix">Prefix</html:option>
											</html:select>
										</td>
									</tr>
									<tr>
										<td align="left" class="captiontext" valign="top">
											<bean:message bundle="pluginResources" key="plugin.global" />
											<ec:elitehelp headerBundle="pluginResources" text="plugin.transactionlogger.globalization" header="plugin.global" />
										</td>
										<td align="left" class="labeltext" valign="top" colspan="3">
											<html:select property="globalization" styleId="global" style="width: 130px" tabindex="18">
												<html:option value="false">False</html:option>
												<html:option value="true">True</html:option>
											</html:select>
										</td>
									</tr>
									
									<tr>
										<td class="tblheader-bold" colspan="2">
											<bean:message bundle="pluginResources" key="plugin.transactionlogger.formatmappings" />
										</td>
									</tr>
									<tr>
										<td class="captiontext" colspan="2" style="padding-top: 10px;">
											<input type="button" value="Add Format" class="light-btn" onclick="addTransactionLogger();"/> 
										</td>
									</tr>
									<tr>
										<td class="captiontext" colspan="2">
											<table cellspacing="0" cellpadding="0" border="0" width="80%" id="mappingTable">
												<tr>
													<td class="tblheader" width="30%">
														<bean:message bundle="pluginResources" key="plugin.transactionlogger.formatmappings.key" />
														<ec:elitehelp headerBundle="pluginResources" text="plugin.transactionlogger.formatmappings.key" header="plugin.transactionlogger.formatmappings.key"/>
													</td>
													<td class="tblheader">
														<bean:message bundle="pluginResources" key="plugin.transactionlogger.formatmappings.format" />
														<ec:elitehelp headerBundle="pluginResources" text="plugin.transactionlogger.formatmappings.format" header="plugin.transactionlogger.formatmappings.format"/>
													</td>
													<td class="tblheader" width="10%">		
														<bean:message bundle="pluginResources" key="plugin.transactionlogger.formatmappings.remove" />
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
									</tr>
									<tr>
										<td>&nbsp;</td>
										<td align="left" style="padding-left: 50px;">
											<input type="button" id="submitLogger" value="Create" class="light-btn" onclick="validateTransactionLogger()"/>
											<input type="button" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=basePath%>/searchPlugin.do?'" />
										</td>
									</tr>
									<tr>
										<td>
											&nbsp;
										</td>
									</tr>
								</table>
								</html:form>
							</div>
						</td>
					</tr>
				<%@ include file="/jsp/core/includes/common/Footer.jsp"%> 
				</table>
			</td>
		</tr>
	</table>
	
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
		/** Setting title in main header bar*/
		setTitle('Diameter Transaction Logger');

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
			
			if( !isValidMappings() ){
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
				document.forms['diameterTransactionLogger'].submit();
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
