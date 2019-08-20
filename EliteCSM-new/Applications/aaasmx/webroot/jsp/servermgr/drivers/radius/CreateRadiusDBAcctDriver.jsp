<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.*"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.List"%>

<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>

<%
	String basePath = request.getContextPath();
	List dbacctFeildMapList = (List)session.getAttribute("dbacctFeildMapList");	
%>
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<script language="javascript">
	$(document).ready(function(){

		$('#mappingtbl td img.delete').live('click',function() {	
				 $(this).parent().parent().remove(); 
			});	
	}); 
 	function validateForm(){		
 		if(document.getElementById('mappingtbl').getElementsByTagName('tr').length < 2){
 			alert('Atleast one field mapping must be present.');
 		}else if(document.forms[0].databaseId.value == 0){
 			alert('Select atleast one datasource value ');
 		}else if(isNull(document.forms[0].querytimeout.value)){
 			document.forms[0].querytimeout.focus();
 			alert('DB Query Timeout must be specified.');
 		}else if(!isPositiveNumber(document.forms[0].querytimeout.value)){
 			document.forms[0].querytimeout.focus();
 			alert('DB Query Timeout must be zero or positive number.');
 		}else if(isNull(document.forms[0].timeoutcount.value)){
 			document.forms[0].timeoutcount.focus();
 			alert('Maximum Query Timeout Count must be specified.');
 		}else if(!isPositiveNumber(document.forms[0].timeoutcount.value)){
 			document.forms[0].timeoutcount.focus();
 			alert('Maximum Query Timeout Count must be zero or positive number.');
 		}else if(document.forms[0].cdrTablename.value == 0){
 			document.forms[0].cdrTablename.focus();
 			alert('CDR Table Name must be specified');
 		}else if(isNull(document.forms[0].cdrIdDbField.value)){
 			document.forms[0].cdrIdDbField.focus();
 			alert('CDR Id DB field name must be specified');
 		}else if(isNull(document.forms[0].cdrIdSeqName.value)){
 			document.forms[0].cdrIdSeqName.focus();
 			alert('CDR Id Sequence name must be specified.');
 		}else if(isNull(document.forms[0].interimtblnm.value)){
 			document.forms[0].interimtblnm.focus();
 			alert('Interim CDR Table Name must be specified.');
 		}else if(isNull(document.forms[0].interimCdrIdDbField.value)){
 			document.forms[0].interimCdrIdDbField.focus();
 			alert('Interim CDR Id Db Field must be specified');
 		}else if(isNull(document.forms[0].interimCdrIdSeqName.value)){
 			document.forms[0].interimCdrIdSeqName.focus();
 			alert('Interim CDR Id Sequence name must be specified');
 		}else if(isNull(document.forms[0].callEndFieldName.value)){
 			document.forms[0].callEndFieldName.focus();
 			alert('Call End Field name must be specified.');
 		}else if(isNull(document.forms[0].callStartFieldName.value)){
 			document.forms[0].callStartFieldName.focus();
 			alert('Call Start Field name must be specified.');
 		}else if(isNull(document.forms[0].createDateFieldName.value)){
 			document.forms[0].createDateFieldName.focus();
 			alert('Create Date Field name must be specified.');
 		}else if(isNull(document.forms[0].lastModifiedDateFieldName.value)){
 			document.forms[0].lastModifiedDateFieldName.focus();
 			alert('Last modified date field name must be specified.');
 		}else{
 			if(isValidFieldMapping("mappingtbl","attributeids")){
 				if(isValidFieldMapping("mappingtbl","dbfields")){
 					document.forms[0].action.value='create';	
			 		document.forms[0].submit();
 				}	
			}
 	 	} 	 		
	}
	
	function setColumnsOnCDRTextFields(){
		var dbId = document.getElementById("databaseId").value;
		retriveCDRTableFields(dbId);
	}
	
	function setColumnsOnIntrimCDRTextFields(){
		var dbId = document.getElementById("databaseId").value;
		retriveIntrimTableFields(dbId);
	}
	
	function setColumnsForTables(){
		
		setColumnsOnCDRTextFields();
		setColumnsOnIntrimCDRTextFields();
	}

	function retriveCDRTableFields(dbId) {
		var dbFieldStr;
		var tableName = document.getElementById("cdrtablename").value;
		$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
			dbFieldStr = data.substring(1,data.length-3);
			var dbFieldArray = new Array();
			dbFieldArray = dbFieldStr.split(", ");
			setFields("cdrIdDbField",dbFieldArray);
			setFields("callStartFieldName",dbFieldArray);
			setFields("callEndFieldName",dbFieldArray);
			setFields("createDateFieldName",dbFieldArray);
			setFields("lastModifiedDateFieldName",dbFieldArray);
			setFields("dbDateField",dbFieldArray);
			setFields("dbfields",dbFieldArray);
			return dbFieldArray;
		});	
		
	}

	function retriveIntrimTableFields(dbId) {
		var dbFieldStr;
		var tableName = document.getElementById("interimtblnm").value;
		$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
			dbFieldStr = data.substring(1,data.length-3);
			var dbFieldArray = new Array();
			dbFieldArray = dbFieldStr.split(", ");
			
			setFields("interimCdrIdDbField",dbFieldArray);
			
			return dbFieldArray;
		});	
		
	}
	var idIndex=0;
	function setColumnsonDbFields(obj){
		var dbId = document.getElementById("databaseId").value;
		var dbFieldStr;
		var tableName = document.getElementById("cdrtablename").value;
		obj.id = "dbfields"+idIndex;
		$.post("FieldRetrievalServlet", {databaseId:dbId,tblName:tableName}, function(data){
			dbFieldStr = data.substring(1,data.length-3);
			var dbFieldArray = new Array();
			dbFieldArray = dbFieldStr.split(", ");
			setFields("cdrIdDbField",dbFieldArray);
			setFields("callStartFieldName",dbFieldArray);
			setFields("callEndFieldName",dbFieldArray);
			setFields("createDateFieldName",dbFieldArray);
			setFields("lastModifiedDateFieldName",dbFieldArray);
			setFields("dbDateField",dbFieldArray);
			setFields(obj.id,dbFieldArray);
			return dbFieldArray;
		});	
		idIndex++;
	}
	var indexOfId = 0 ;
	function setColumnsOnAttrIdFields(obj){
		obj.id = "attributeids"+indexOfId;
		var attrIdVal = document.getElementById("attributeids").value;
		retriveRadiusDictionaryAttributes(attrIdVal,"attributeids"+indexOfId);
		indexOfId++;
	}
	setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 	

	
</script>



<html:form action="/createRadiusDBAcctDriver" styleId="mainform">

	<html:hidden name="createRadiusDBAcctDriverForm" property="action" />
	<html:hidden name="createRadiusDBAcctDriverForm"
		property="driverInstanceName" />
	<html:hidden name="createRadiusDBAcctDriverForm" property="driverDesp" />
	<html:hidden name="createRadiusDBAcctDriverForm"
		property="driverRelatedId" />
	<html:hidden property="itemIndex" />

	<div id="backgroundPopup">
		<table cellpadding="0" cellspacing="0" border="0"
			width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
			<tr>
				<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
				<td>
					<table cellpadding="0" cellspacing="0" border="0" width="100%">
						<tr>
							<td cellpadding="0" cellspacing="0" border="0" width="100%"
								class="box">
								<table cellpadding="0" cellspacing="0" border="0" width="100%">

									<tr>
										<td class="table-header" colspan="3"><bean:message
												bundle="driverResources" key="driver.createdbacctDriver" />
										</td>
									</tr>
									<tr>
										<td class="small-gap" colspan="3">&nbsp;</td>
									</tr>
									<tr>
										<td colspan="3">
											<table width="100%" name="c_tblCrossProductList"
												id="c_tblCrossProductList" align="right" border="0"
												cellpadding="0" cellspacing="0">

												<tr>
													<td align="left" class="tblheader-bold" valign="top"
														colspan="3"><bean:message bundle="driverResources"
															key="driver.dbacctdetails" /></td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources" key="driver.dbauthdriver.ds" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="dbacctdriver.ds" header="driver.dbauthdriver.ds"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="75%">
														<html:select property="databaseId" styleId="databaseId"
															style="width:130px" tabindex="1"
															onchange="setColumnsForTables()">
															<html:option value="0">Select</html:option>
															<html:optionsCollection
																name="createRadiusDBAcctDriverForm"
																property="databaseDSList" label="name"
																value="databaseId" />
														</html:select><font color="#FF0000"> *</font>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources" key="driver.dsType" />
															<ec:elitehelp headerBundle="driverResources" 
																text="dbacctdriver.dstype" header="driver.dsType"/> 
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:select property="datasourceType" styleId="dstype"
															style="width:130px" tabindex="2">
															<html:option value="Oracle">Oracle</html:option>
														</html:select>
													</td>
												</tr>



												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources" key="driver.querytimeout" /> 
															<bean:message key="general.seconds" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.querytimeout" header="driver.querytimeout"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text styleId="querytimeout"
															property="dbQueryTimeout" size="20" tabindex="3"
															maxlength="50" style="width:250px" />
															<font color="#FF0000"> *</font>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="35%">
														<bean:message bundle="driverResources" key="driver.querytimeoutcount" /> 
															<ec:elitehelp headerBundle="driverResources" 
																text="dbacctdriver.maxquerytimeout" header="driver.querytimeoutcount"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text styleId="timeoutcount"
															property="maxQueryTimeoutCount" size="20" tabindex="4"
															maxlength="50" style="width:250px" />
															<font color="#FF0000"> *</font>
													</td>
												</tr>


												<!-- 
						<tr>
							<td align="left" class="labeltext" valign="top" width="25%">
							<bean:message bundle="driverResources" key="driver.storeTunnelStartRec" /></td>
							<td align="left" class="labeltext" valign="top" width="75%">
								<html:text styleId="storeTunnelStartRec" property="storeTunnelStartRec" size="20"
								maxlength="50" />
							</td>							
						</tr>
						
						<tr>
							<td align="left" class="labeltext" valign="top" width="25%">
							<bean:message bundle="driverResources" key="driver.storeTunnelStopRec" /></td>
							<td align="left" class="labeltext" valign="top" width="75%">
								<html:text styleId="storeTunnelStopRec" property="storeTunnelStopRec" size="20"
								maxlength="50" />
							</td>							
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="25%">
							<bean:message bundle="driverResources" key="driver.removeTunnelStopRec" /></td>
							<td align="left" class="labeltext" valign="top" width="75%">
								<html:text styleId="removeTunnelStopRec" property="removeTunnelStopRec" size="20"
								maxlength="50" />
							</td>							
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="25%">
							<bean:message bundle="driverResources" key="driver.storeTunnelLinkStartRec" /></td>
							<td align="left" class="labeltext" valign="top" width="75%">
								<html:text styleId="storeTunnelLinkStartRec" property="storeTunnelLinkStartRec" size="20"
								maxlength="50" />
							</td>							
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="25%">
							<bean:message bundle="driverResources" key="driver.storeTunnelLinkStopRec" /></td>
							<td align="left" class="labeltext" valign="top" width="75%">
								<html:text styleId="storeTunnelLinkStopRec" property="storeTunnelLinkStopRec" size="20"
								maxlength="50" />
							</td>							
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="25%">
							<bean:message bundle="driverResources" key="driver.removeTunnelLinkStopRec" /></td>
							<td align="left" class="labeltext" valign="top" width="75%">
								<html:text styleId="removeTunnelLinkStopRec" property="removeTunnelLinkStopRec" size="20"
								maxlength="50" />
							</td>							
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="25%">
							<bean:message bundle="driverResources" key="driver.storeTunnelRejectRec" /></td>
							<td align="left" class="labeltext" valign="top" width="75%">
								<html:text styleId="storeTunnelRejectRec" property="storeTunnelRejectRec" size="20"
								maxlength="50" />
							</td>							
						</tr>
						<tr>
							<td align="left" class="labeltext" valign="top" width="25%">
							<bean:message bundle="driverResources" key="driver.storeTunnelLinkRejectRec" /></td>
							<td align="left" class="labeltext" valign="top" width="75%">
								<html:text styleId="storeTunnelLinkRejectRec" property="storeTunnelLinkRejectRec" size="20"
								maxlength="50" />
							</td>							
						</tr-->
												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.multivalDelimeter" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.multivalDelimeter" 
																		header="driver.multivalDelimeter"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text styleId="multivalDelimeter"
															property="multivalDelimeter" size="20" tabindex="5"
															maxlength="50" style="width:250px" />
													</td>
												</tr>

												<tr>
													<td align="left" class="tblheader-bold" valign="top"
														colspan="3"><bean:message bundle="driverResources"
															key="driver.cdrconf" /></td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.cdrTablename" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.maxquerytimeout" 
																		header="driver.cdrTablename"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text styleId="cdrtablename" property="cdrTablename"
															size="20" tabindex="6" maxlength="128"
															onblur="setColumnsOnCDRTextFields();" style="width:250px" /><font
														color="#FF0000"> *</font>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.cdrIdDbField" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.cdrIdDbField" header="driver.cdrIdDbField"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text styleId="cdrIdDbField" property="cdrIdDbField"
															size="20" tabindex="7" maxlength="50" style="width:250px" /><font
														color="#FF0000"> *</font>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.cdrIdSeqName" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.cdrIdSeqName" header="driver.cdrIdSeqName"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text styleId="cdrIdSeqName" property="cdrIdSeqName"
															size="20" tabindex="8" maxlength="50" style="width:250px" /><font
														color="#FF0000"> *</font>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.storeallcdr" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.storestprec" header="driver.storeallcdr"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:select property="storeAllCdr" styleId="storeAllCdr"
															tabindex="9" value="true" style="width:130px">
															<html:option value="true">True</html:option>
															<html:option value="false">False</html:option>
														</html:select>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.storestprec" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.storestprec" header="driver.storestprec"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:select property="storeStopRec" styleId="storestprec"
															tabindex="10" value="true" style="width:130px">
															<html:option value="true">True</html:option>
															<html:option value="false">False</html:option>
														</html:select>
													</td>
												</tr>

												<tr>
													<td align="left" class="tblheader-bold" valign="top"
														colspan="3"><bean:message bundle="driverResources"
															key="driver.interimcdrconf" /></td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.interimTblnm" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.interimTblnm" header="driver.interimTblnm"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text styleId="interimtblnm"
															property="interimTablename" size="20" tabindex="11"
															maxlength="2000" onblur="setColumnsOnIntrimCDRTextFields()"
															style="width:250px" />
															<font color="#FF0000"> *</font>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.interimCdrIdDbField" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.interimCdrIdDbField" 
																		header="driver.interimCdrIdDbField"/> 
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text styleId="interimCdrIdDbField"
															property="interimCdrIdDbField" size="20" tabindex="12"
															maxlength="50" style="width:250px" />
															<font color="#FF0000"> *</font>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.interimCdrIdSeqName" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.interimCdrIdSeqName" 
																		header="driver.interimCdrIdSeqName"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text styleId="interimCdrIdSeqName"
															property="interimCdrIdSeqName" size="20" tabindex="13"
															maxlength="50" style="width:250px" />
															<font color="#FF0000"> *</font>
													</td>
												</tr>


												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.storeInterimRec" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.storeInterimRec" header="driver.storeInterimRec"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:select tabindex="14" property="storeInterimRec"
															styleId="storeInterimRec" value="true"
															style="width:130px">
															<html:option value="true">True</html:option>
															<html:option value="false">False</html:option>
														</html:select>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.removeInterimOnStop" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.removeInterimOnStop" 
																		header="driver.removeInterimOnStop"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:select tabindex="15" property="removeInterimOnStop"
															styleId="removeInterimOnStop" value="false"
															style="width:130px">
															<html:option value="true">True</html:option>
															<html:option value="false">False</html:option>
														</html:select>
													</td>
												</tr>

												<tr>
													<td align="left" class="tblheader-bold" valign="top"
														colspan="3"><bean:message bundle="driverResources"
															key="driver.fielddetails" /></td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.callStartFieldName" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.callStartFieldName" 
																		header="driver.callStartFieldName"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text tabindex="16" styleId="callStartFieldName"
															property="callStartFieldName" size="20" maxlength="50"
															style="width:250px" /><font color="#FF0000"> *</font>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.callEndFieldName" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.callEndFieldName" 
																		header="driver.callEndFieldName"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text tabindex="17" styleId="callEndFieldName"
															property="callEndFieldName" size="20" maxlength="50"
															style="width:250px" /><font color="#FF0000"> *</font>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.createDateFieldName" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.createDateFieldName" 
																		header="driver.createDateFieldName"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text tabindex="18" styleId="createDateFieldName"
															property="createDateFieldName" size="20" maxlength="50"
															style="width:250px" /><font color="#FF0000"> *</font>
													</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.lastModifiedDateFieldName" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.lastModifiedDateFieldName" 
																		header="driver.lastModifiedDateFieldName"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text tabindex="19"
															styleId="lastModifiedDateFieldName"
															property="lastModifiedDateFieldName" size="20"
															maxlength="50" style="width:250px" /><font
														color="#FF0000"> *</font>
													</td>
												</tr>

												<tr>
													<td align="left" class="tblheader-bold" valign="top"
														colspan="3"><bean:message bundle="driverResources"
															key="driver.cdrtimestampdetails" /></td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources"
															key="driver.dbDateField" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="" header="driver.dbDateField"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:text tabindex="20" styleId="dbDateField"
															property="dbDateField" size="20" maxlength="50"
															style="width:250px" />
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="30%">
														<bean:message bundle="driverResources" key="driver.enabled" /> 
																<ec:elitehelp headerBundle="driverResources" 
																	text="dbacctdriver.enabled" header="driver.enabled"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="70%">
														<html:select tabindex="21" property="enabled"
															styleId="enabled" value="true" style="width:130px">
															<html:option value="true">True</html:option>
															<html:option value="false">False</html:option>
														</html:select>

													</td>
												</tr>

												<tr>
													<td align="left" class="tblheader-bold" valign="top"
														colspan="3"><bean:message bundle="driverResources"
															key="driver.dbacctmapdetails" /></td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top"
														colspan="2" id="button"><input type="button"
														tabindex="22" onclick='addRow("dbMappingTable","mappingtbl");' value=" Add Mapping "
														class="light-btn" style="size: 140px"></td>
												</tr>
											</table>
										</td>
									</tr>

									<tr>
										<td width="98%" colspan="2" valign="top" class="captiontext">
											<table cellSpacing="0" cellPadding="0" width="97%" border="0"
												id="mappingtbl" class="box">
												<tr>
													<td align="left" class="tblheader" valign="top" width="20%" id="tbl_attrid">
														<bean:message bundle="driverResources" key="driver.attributeids" />
															<ec:elitehelp headerBundle="driverResources" 
																text="dbacctdriver.attid" header="driver.attributeids"/>
													</td>
													<td align="left" class="tblheader" valign="top" width="20%">
														<bean:message bundle="driverResources" key="driver.dbfield" />
															<ec:elitehelp headerBundle="driverResources" 
																text="dbacctdriver.dbfield" header="driver.dbfield"/>
													</td>
													<td align="left" class="tblheader" valign="top" width="20%">
														<bean:message bundle="driverResources" key="driver.datatype" />
															<ec:elitehelp headerBundle="driverResources" 
																text="dbacctdriver.datatype" header="driver.datatype"/>
													</td>
													<td align="left" class="tblheader" valign="top" width="15%">
														<bean:message bundle="driverResources" key="driver.defaultvalue"/> 
															<ec:elitehelp headerBundle="driverResources" 
																text="dbacctdriver.defaultvalue" header="driver.defaultvalue"/>
													</td>
													<td align="left" class="tblheader" valign="top" width="20%">
														<bean:message bundle="driverResources" key="driver.useDictionaryValue" />
															<ec:elitehelp headerBundle="driverResources" 
																text="dbacctdriver.dicvalue" header="driver.useDictionaryValue"/>
													</td>
													<td align="left" class="tblheader" valign="top" width="5%">Remove</td>
												</tr>
												
												<logic:iterate id="obj" name="defaultMapping" type="com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAcctFeildMapData">
															<tr>
																<td class="tblrows">
																	 <input class="noborder" onfocus="setColumnsOnAttrIdFields(this);" type="text" name="attributeids" maxlength="1000" size="28" style="width: 100%" tabindex="10" value='<bean:write name="obj" property="attributeids"/>' /></td>
																<td class="tblrows">
																	<input type="text" name="dbfields" id="dbfields"  maxlength="1000" class="noborder" size="28" style="width: 100%" value='<bean:write name="obj" property="dbfield"/>' onfocus="setColumnsonDbFields(this);" onkeyup="setColumnValue(this.value,this);"/>
																</td>
																<td class="tblrows">
																	<select style="width: 100%" class="noborder" id="datatype" name="datatype" class="noborder">
																			<option value='String' <logic:equal value="String" name="obj" property="datatype">selected</logic:equal>>String</option>
																			<option value='Date' <logic:equal value="Date" name="obj" property="datatype">selected</logic:equal>>Date</option>	
																	</select>
																</td>												
																<td class="tblrows">
																	<input class="noborder" type="text" name="defaultvalue" maxlength="1000" size="28" style="width: 100%" tabindex="10" value='<bean:write name="obj" property="defaultvalue"/>'/></td>
																<td class="tblrows">
																	<select class="noborder" name="useDictionaryValue" id="useDictionaryValue" style="width: 100%" tabindex="10">
																		<option value='true' <logic:equal value="true" name="obj" property="useDictionaryValue">selected</logic:equal>>true</option>
																		<option value='false' <logic:equal value="false" name="obj" property="useDictionaryValue">selected</logic:equal>>false</option>		
																	</select> 
																		
																	</td>
																<td class="tblrows" align="center" colspan="3"><img
																	value="top" src="<%=basePath%>/images/minus.jpg"
																	class="delete"
																	style="padding-right: 5px; padding-top: 5px;"
																	height="14" tabindex="10" /></td>
															</tr>
														</logic:iterate>
														
											</table>
										</td>
									</tr>




									<tr>
										<td align="left" class="labeltext" colspan="3" valign="top">
											&nbsp;</td>
									</tr>

									<tr>
										<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
									</tr>
									<tr>
										<td class="btns-td" valign="middle">&nbsp;</td>
										<td class="btns-td" valign="middle"><input type="button"
											value="Previous " onclick="history.go(-1)" class="light-btn"
											tabindex="23" /> <input type="button" name="c_btnCreate"
											id="c_btnCreate2" tabindex="24" value="Create"
											class="light-btn" tabindex="6" onclick="validateForm()"/>
											<input type="reset" name="c_btnDeletePolicy" tabindex="25"
											onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'"
											value="Cancel" class="light-btn" tabindex="7" /></td>
									</tr>
								</table>
							</td>
						</tr>
						<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
					</table>
				</td>
			</tr>
		</table>
	</div>
</html:form>

<!-- Mapping Table Row template -->
<table style="display: none;" id="dbMappingTable">
	<tr>
		<td class="allborder">
			<input type="text" name="attributeids" id="attributeids" class="noborder" maxlength="1000" size="28" style="width: 100%" onfocus="setColumnsOnAttrIdFields(this);" />
		</td>
		<td class="tblrows">
			<input type="text" name="dbfields" id="dbfields"  maxlength="1000" class="noborder" size="28" style="width: 100%" onfocus="setColumnsonDbFields(this);"/>
			
		</td>
		<td class="tblrows">	
			<select style="width: 100%" class="noborder" id="datatype" name="datatype" class="noborder">
						<option value="String">String</option>
						<option value="Date">Date</option>
			</select>
		</td>
		<td class="tblrows">
			<input type="text" name="defaultvalue" id="defaultvalue"  maxlength="1000" class="noborder" size="28" style="width: 100%" />
			
		</td>
		<td class="tblrows">
	 		<select style="width: 100%" class="noborder" id="useDictionaryValue" name="useDictionaryValue">
				<option value="True"> true</option>
				<option value="False">false</option>
			</select> 
		</td> 
		<td class="tblrows" align="center" colspan="3"><img value="top"
			src="<%=basePath%>/images/minus.jpg" class="delete"
			style="padding-right: 5px; padding-top: 5px;" height="14"
			tabindex="10" /></td>
	</tr>
</table>
</html>