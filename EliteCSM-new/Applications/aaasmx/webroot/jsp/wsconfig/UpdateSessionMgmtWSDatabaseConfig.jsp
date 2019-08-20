<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page
	import="com.elitecore.elitesm.web.wsconfig.sessionmgmt.forms.SessionMgmtWSDatabaseConfigForm"%>
<%@ page import="java.util.List"%>
<%@page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	SessionMgmtWSDatabaseConfigForm sessionMgmtWSDatabaseConfigForm=(SessionMgmtWSDatabaseConfigForm)request.getAttribute("sessionMgmtWSDatabaseConfigForm");
	
    String[] keys = (String[])request.getAttribute("keys");
	String[] fields = (String[])request.getAttribute("fields");
	
	String[] attrKeys=(String[])request.getAttribute("attrKeys");
	String[] attrFields=(String[])request.getAttribute("attrFields");
%>

<script type="text/javascript">

var mainArray = new Array();
var count = 0;

var attrMainArray = new Array();
var attrCount = 0;



$(document).ready(function() 
{		
	
	<% if(keys != null && keys.length>0){ %>
	   //initDBFieldMapping();
			    var jkeys =  new Array(<%=keys.length%>)
				var jfields = new Array(<%=fields.length%>);	
							
					
				<%int j =0;				
				for(j =0;j<keys.length;j++){%>								
		
				jkeys[<%=j%>] = '<%=keys[j]%>';					
				jfields[<%=j%>] = '<%=fields[j]%>';
				
					
					var jkey = jkeys[<%=j%>];
					var jfield = jfields[<%=j%>];

					
					$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + jkey + "<input type='hidden' name = 'keyval' value='" + jkey + "'/>" +"</td><td class='tblfirstcol'>" + jfield + "<input type='hidden' name = 'fieldval' value='" + jfield + "'/></td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
					$('#mappingtbl td img.delete').live('click',function() {

	       				 //var $td= $(this).parents('tr').children('td');			
	       				 //var removalVal = $td.eq(0).text();

	       				 var removalVal = $(this).closest('tr').find('td:eq(0)').text();

	       				 for(var d=0;d<count;d++){
	       					var currentVal = mainArray[d];					
	       					if(currentVal == removalVal){
	       						mainArray[d] = '  ';
	       						break;
	       					}
	       				}
	       				 //mainArray[($(this).closest("tr").prevAll("tr").length) - 1] = '  ';
	       				 $(this).parent().parent().remove(); 
	       			  });					          		
					mainArray[count++] = jkey;	
				<%}%>	  
	   
	<%}%>
	
	<% if(attrKeys != null && attrKeys.length>0){ %>
        //initAttrFieldMapping();
				var attrkeys =  new Array(<%=attrKeys.length%>)
				var attrFields = new Array(<%=attrFields.length%>);	
							
					
				<% int j =0;				
				for(j =0;j<attrKeys.length;j++){%>								
			
				attrkeys[<%=j%>] = '<%=attrKeys[j]%>';					
				attrFields[<%=j%>] = '<%=attrFields[j]%>';
				
					
					var jattrKey = attrkeys[<%=j%>];
					var jattrField = attrFields[<%=j%>];
					
			
					//alert("vendoridentifier"+vendoridentifier+"serverCertName"+serverCertName+"serverPrivatekeyName"+serverPrivatekeyName+"caCertName"+caCertName);
					
					//alert($("#mappingtbl1 tr:last").after("<tr><td class='tblfirstcol'>" + vendoridentifier + "<input type='hidden' name = 'ouival' value='" + vendoridentifier + "'/>" +"</td><td class='tblfirstcol'>" + serverCertName + "<input type='hidden' name = 'serverCertificateNameval' value='" + serverCertName + "'/>" +"</td><td class='tblfirstcol'>" + serverPrivatekeyName + "<input type='hidden' name = 'serverPrivatekeyNameval' value='" + serverPrivatekeyName + "'/>" +"</td><td class='tblrows'>" + caCertName + "<input type='hidden' name = 'cacertificateNameval' value='" + caCertName + "'/>" + "<td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>"));
					$("#mappingtblattr tr:last").after("<tr><td class='tblfirstcol'>" + jattrKey + "<input type='hidden' name = 'attrkeyval' value='" + jattrKey + "'/>" +"</td><td class='tblfirstcol'>" + jattrField + "<input type='hidden' name = 'attrfieldval' value='" + jattrField + "'/></td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
					$('#mappingtblattr td img.delete').live('click',function() {

	       				 //var $td= $(this).parents('tr').children('td');			
	       				 //var removalVal = $td.eq(0).text();

	       				 var removalVal = $(this).closest('tr').find('td:eq(0)').text();

	       				 for(var d=0;d<count;d++){
	       					var currentVal = attrMainArray[d];					
	       					if(currentVal == removalVal){
	       						attrMainArray[d] = '  ';
	       						break;
	       					}
	       				}
	       				 //mainArray[($(this).closest("tr").prevAll("tr").length) - 1] = '  ';
	       				 $(this).parent().parent().remove(); 
	       			  });
	       			  
        			attrMainArray[count++] = jattrKey;
					
				<%}%>	  
      
	<%}%>
    
  }
);


function popupdb() {	
	$.fx.speeds._default = 1000;
	document.getElementById("popupdiv").style.visibility = "visible";		
	$( "#popupdiv" ).dialog({
		modal: true,
		autoOpen: false,		
		height: 200,
		width: 500,		
		buttons:{					
            'Add': function() {

						
                        var jkey=$('#key').val(); 			      		
						var jfield=$('#field').val();
						
						
			      				          		
			      		if(isNull(jkey)){
			     			alert('Key must be specified.');
			     		}else if(isNull(jfield)){
			     			alert('Field must be specified.');
			     		}else{	
			     			var i = 0;							
							var flag = true;												 	
							if(document.getElementById('mappingtbl').getElementsByTagName('tr').length >= 2){								
								for(i=0;i<count;i++){									
									var value = mainArray[i];																		
									if(value == jkey){
										alert("Key must be unique.");
										flag = false;
										break;
									}
								}
							}								
			         		if(flag){
			         			$("#mappingtbl tr:last").after("<tr><td class='tblfirstcol'>" + jkey + "<input type='hidden' name = 'keyval' value='" + jkey + "'/>" +"</td><td class='tblfirstcol'>" + jfield + "<input type='hidden' name = 'fieldval' value='" + jfield + "'/></td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
			         			$('#mappingtbl td img.delete').live('click',function() {

				       				 //var $td= $(this).parents('tr').children('td');			
				       				 //var removalVal = $td.eq(0).text();

				       				 var removalVal = $(this).closest('tr').find('td:eq(0)').text();

				       				 for(var d=0;d<count;d++){
				       					var currentVal = mainArray[d];					
				       					if(currentVal == removalVal){
				       						mainArray[d] = '  ';
				       						break;
				       					}
				       				}
				       				 //mainArray[($(this).closest("tr").prevAll("tr").length) - 1] = '  ';
				       				 $(this).parent().parent().remove(); 
				       			  });					          		
			         			mainArray[count++] = jkey;	
				          		$('#key').val(' ');
				          		$('#field').val(' ');				          		
				          		$(this).dialog('close');
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
    		document.getElementById("key").value = "";
    		document.getElementById("field").value = "";
     		document.getElementById("c_btnCreate2").focus();
    	}				
	});
	$( "#popupdiv" ).dialog("open");
	$(retriveTableFields(document.getElementById("databaseId").value));
	
}


function setColumnsOnTextFields(){
	var dbId = document.getElementById("databaseId").value;
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
		setFields("field",dbFieldArray);
		setFields("attrField",dbFieldArray);
		return dbFieldArray;
	});	
	
}

function popupAttr() {	
	$.fx.speeds._default = 1000;
	document.getElementById("popupdivattr").style.visibility = "visible";		
	$( "#popupdivattr" ).dialog({
		modal: true,
		autoOpen: false,		
		height: 200,
		width: 500,		
		buttons:{					
            'Add': function() {

                        var jattrKey=$('#attribute').val(); 			      		
						var jattrField=$('#attrField').val();
						
						
			      				          		
			      		if(isNull(jattrKey)){
			     			alert('Attribute must be specified.');
			     		}else if(isNull(jattrField)){
			     			alert('Field must be specified.');
			     		}else{	
			     			var i = 0;							
							var flag = true;												 	
							if(document.getElementById('mappingtblattr').getElementsByTagName('tr').length >= 2){								
								for(i=0;i<count;i++){									
									var value = attrMainArray[i];																		
									if(value == jattrKey){
										alert("Attribute must be unique.");
										flag = false;
										break;
									}
								}
							}								
			         		if(flag){
			         			$("#mappingtblattr tr:last").after("<tr><td class='tblfirstcol'>" + jattrKey + "<input type='hidden' name = 'attrkeyval' value='" + jattrKey + "'/>" +"</td><td class='tblfirstcol'>" + jattrField + "<input type='hidden' name = 'attrfieldval' value='" + jattrField + "'/></td><td class='tblrows'><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
											$('#mappingtblattr td img.delete')
													.live(
															'click',
															function() {

																//var $td= $(this).parents('tr').children('td');			
																//var removalVal = $td.eq(0).text();

																var removalVal = $(
																		this)
																		.closest(
																				'tr')
																		.find(
																				'td:eq(0)')
																		.text();

																for (var d = 0; d < count; d++) {
																	var currentVal = attrMainArray[d];
																	if (currentVal == removalVal) {
																		attrMainArray[d] = '  ';
																		break;
																	}
																}
																//mainArray[($(this).closest("tr").prevAll("tr").length) - 1] = '  ';
																$(this)
																		.parent()
																		.parent()
																		.remove();
															});

											attrMainArray[count++] = jattrKey;
											$('#attribute').val(' ');
											$('#attrField').val(' ');
											$(this).dialog('close');
										}
									}
								},
								Cancel : function() {
									$(this).dialog('close');
								}
							},
							open : function() {

							},
							close : function() {
								document.getElementById("attribute").value = "";
								document.getElementById("attrField").value = "";
								document.getElementById("c_btnCreate2").focus();
							}
						});
		$("#popupdivattr").dialog("open");
		$(retriveTableFields(document.getElementById("databaseId").value));

	}

	function validate() {

		if (document.forms[0].databaseId.value == '0') {

			document.forms[0].databaseId.focus();
			alert('Please Select Datasource');

		} else if (isNull(document.forms[0].tableName.value)) {
			document.forms[0].tableName.focus();
			alert('TableName must be specified');
		} else if (isNull(document.forms[0].recordFetchLimit.value)) {
			alert('Record Fetch Limit must be specified');
			document.forms[0].recordFetchLimit.focus();
		} else if (!isNumber(document.forms[0].recordFetchLimit.value)) {
			alert('Record Fetch Limit must be numeric');
			document.forms[0].recordFetchLimit.focus();
		} else {
			document.forms[0].checkAction.value = 'Update';
			document.forms[0].submit();
		}

	}
	function validateName(val) {
		var test1 = /(^[A-Za-z0-9-_]*$)/;
		var regexp = new RegExp(test1);
		if (regexp.test(val)) {
			return true;
		}
		return false;
	}
	function isNumber(val) {
		nre = /^\d+$/;
		var regexp = new RegExp(nre);
		if (!regexp.test(val)) {
			return false;
		}
		return true;
	}

	setTitle('<bean:message bundle="webServiceConfigResources" key="webservice.sessionmgmtconfig"/>');
</script>



<html:form action="/sessionMgmtWSDatabaseConfig">
	<html:hidden property="checkAction" />
	<html:hidden property="wsConfigId" />

	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%"
							class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">

								<tr>
									<td class="table-header"><bean:message
											bundle="webServiceConfigResources"
											key="webservice.sessionmgmtconfig.update" /></td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td colspan="3">
										<table width="100%" name="c_tblCrossProductList"
											id="c_tblCrossProductList" align="right" cellSpacing="0"
											cellPadding="0" border="0">

											<tr>
												<td align="left" class="captiontext" valign="top"
													width="25%"><bean:message
														bundle="webServiceConfigResources"
														key="webservice.dbconfig.datasource" /> <ec:elitehelp
														headerBundle="webServiceConfigResources"
														text="webservice.dbconfig.datasource"
														header="webservice.dbconfig.datasource" /></td>
												<td align="left" class="labeltext" valign="top" width="32%"
													colspan="3"><bean:define id="lstDatasource"
														name="sessionMgmtWSDatabaseConfigForm"
														property="lstDatasource"></bean:define> <html:select
														name="sessionMgmtWSDatabaseConfigForm"
														styleId="databaseId" tabindex="1" property="databaseId"
														size="1" onchange="setColumnsOnTextFields();">
														<html:option value="0">
															<bean:message bundle="webServiceConfigResources"
																key="webservice.dbconfig.select" />
														</html:option>
														<html:options collection="lstDatasource"
															property="databaseId" labelProperty="name" />
													</html:select><font color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top"
													width="18%"><bean:message
														bundle="webServiceConfigResources"
														key="webservice.dbconfig.tablename" /> <ec:elitehelp
														headerBundle="webServiceConfigResources"
														text="webservice.dbconfig.tablename"
														header="webservice.dbconfig.tablename"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2"
													width="82%"><html:text styleId="tableName"
														tabindex="2" property="tableName" size="40"
														maxlength="2000" onblur="setColumnsForTables();"
														style="width:250px" /> <font color="#FF0000"> *</font></td>
											</tr>
											<!-- 
						<tr>
							<td align="left" class="labeltext" valign="top" width="18%">
							<bean:message bundle="webServiceConfigResources" key="webservice.dbconfig.uidfieldname" /></td>
							<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
							<html:text styleId="userIdentityFieldName" property="userIdentityFieldName" size="40" maxlength="30" /> <font color="#FF0000"> *</font></td>
						</tr>
						 -->
											<tr>
												<td align="left" class="captiontext" valign="top"
													width="18%"><bean:message
														bundle="webServiceConfigResources"
														key="webservice.dbconfig.recordfetchlimit" /> <ec:elitehelp
														headerBundle="webServiceConfigResources"
														text="webservice.dbconfig.recordfetchlimit"
														header="webservice.dbconfig.recordfetchlimit" />
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2"
													width="82%"><html:text styleId="recordFetchLimit"
														tabindex="3" property="recordFetchLimit" size="15"
														maxlength="10" style="width:250px" /> <font
													color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="labeltext" colspan="3" valign="top">
													&nbsp;</td>
											</tr>






											<!-- DB Field Map Header -->
											<tr>
												<td align="left" class="tblheader-bold" valign="top"
													colspan="3"><bean:message
														bundle="webServiceConfigResources"
														key="webservice.dbconfig.fieldmap" /></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top"
													colspan="2"><input tabindex="4" type="button"
													onclick="popupdb()" value=" Add " class="light-btn"></td>
											</tr>

											<tr>
												<td colspan="3">
													<!--  dbfield table -->
													<table cellpadding="0" cellspacing="0" border="0"
														width="100%" align="right">
														<tr>
															<td align="left" class="labeltext" colspan="2"
																valign="top" width="98%">
																<table width="60%" id="mappingtbl" cellpadding="0"
																	cellspacing="0" border="0" class="captiontext">
																	<tr>
																		<td align="left" class="tblheader" valign="top"
																			width="50%"><bean:message
																				bundle="webServiceConfigResources"
																				key="webservice.dbconfig.key" /> <ec:elitehelp
																				headerBundle="webServiceConfigResources"
																				text="webservice.dbconfig.key"
																				header="webservice.dbconfig.key" /> 
																		</td>
																		<td align="left" class="tblheader" valign="top"
																			width="50%"><bean:message
																				bundle="webServiceConfigResources"
																				key="webservice.dbconfig.field" /> <ec:elitehelp
																				headerBundle="webServiceConfigResources"
																				text="webservice.dbconfig.field"
																				header="webservice.dbconfig.field" /> 
																		</td>
																		<td align="left" class="tblheader" valign="top"
																			width="12.5%">Remove</td>
																	</tr>

																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td>&nbsp;</td>
											</tr>

											<!-- Attribute Field Map Header -->
											<tr>
												<td align="left" class="tblheader-bold" valign="top"
													colspan="3"><bean:message
														bundle="webServiceConfigResources"
														key="webservice.dbconfig.attrfieldmap" /></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top"
													colspan="2"><input tabindex="5" type="button"
													onclick="popupAttr()" value=" Add " class="light-btn"></td>
											</tr>

											<tr>
												<td colspan="3">
													<!--  attribute table -->
													<table cellpadding="0" cellspacing="0" border="0"
														width="100%" align="right">
														<tr>
															<td class="labeltext" colspan="2" valign="top"
																width="100%">
																<table width="60%" id="mappingtblattr" cellpadding="0"
																	cellspacing="0" border="0" class="captiontext">
																	<tr>
																		<td align="left" class="tblheader" valign="top"
																			width="50%"><bean:message
																				bundle="webServiceConfigResources"
																				key="webservice.dbconfig.attribute" /> <ec:elitehelp
																				headerBundle="webServiceConfigResources"
																				text="webservice.dbconfig.attribute"
																				header="webservice.dbconfig.attribute" /> 
																		</td>
																		<td align="left" class="tblheader" valign="top"
																			width="50%"><bean:message
																				bundle="webServiceConfigResources"
																				key="webservice.dbconfig.field" /> <ec:elitehelp
																				headerBundle="webServiceConfigResources"
																				text="webservice.dbconfig.field"
																				header="webservice.dbconfig.field" /> 
																		</td>
																		<td align="left" class="tblheader" valign="top"
																			width="12.5%">Remove</td>
																	</tr>

																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td class="small-gap">&nbsp;</td>
											</tr>

											<tr>
												<td colspan="4" class="captiontext"><input
													type="button" name="c_btnUpdate" tabindex="6"
													onclick="validate();" id="c_btnCreate2"
													value="   Update   " class="light-btn">&nbsp;&nbsp;
													<input type="reset" name="c_btnCancel" tabindex="7"
													onclick="javascript:location.href='initSessionMgmtWSDatabaseConfig.do'"
													value=" Cancel " class="light-btn">&nbsp;&nbsp; <input
													type="button" name="c_btnRefresh" tabindex="8"
													onclick="javascript:location.href='initSessionMgmtWSDatabaseConfig.do?checkAction=refresh'"
													value=" Refresh " class="light-btn"></td>
											</tr>


										</table>

									</td>
								</tr>
							</table>

							<div id="popupdivattr" style="display: none;"
								title="Attribute Field Mapping">

								<table border="0">

									<tr>
										<td align="left" class="labeltext" valign="top" width="20%"><bean:message
												bundle="webServiceConfigResources"
												key="webservice.dbconfig.attribute" /></td>
										<td align="right" class="labeltext" valign="top"><html:text
												tabindex="9" styleId="attribute" property="attribute"
												size="25" maxlength="50" style="width:250px" /><font
											color="#FF0000"> *</font></td>
									</tr>

									<tr>
										<td align="left" class="labeltext" valign="top"><bean:message
												bundle="webServiceConfigResources"
												key="webservice.dbconfig.field" /></td>
										<td align="right" class="labeltext" valign="top"><html:text
												tabindex="10" styleId="attrField" property="attrField"
												size="25" maxlength="50" style="width:250px" /><font
											color="#FF0000"> *</font></td>
									</tr>



								</table>

							</div>


							<div id="popupdiv" style="display: none;"
								title="DB Field Mapping">

								<table border="0">

									<tr>
										<td align="left" class="labeltext" valign="top" width="10%"><bean:message
												bundle="webServiceConfigResources"
												key="webservice.dbconfig.key" /></td>
										<td align="right" class="labeltext" valign="top"><html:text
												tabindex="11" styleId="key" property="key" size="25"
												maxlength="50" style="width:225px" /><font color="#FF0000">
												*</font></td>
									</tr>

									<tr>
										<td align="left" class="labeltext" valign="top"><bean:message
												bundle="webServiceConfigResources"
												key="webservice.dbconfig.field" /></td>
										<td align="right" class="labeltext" valign="top"><html:text
												tabindex="12" styleId="field" property="field" size="25"
												maxlength="50" style="width:225px" /><font color="#FF0000">
												*</font></td>
									</tr>

								</table>

							</div>

						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
<script>
	setColumnsOnTextFields();
</script>


