<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.*"%>
<%@page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.driver.radius.forms.CreateRadiusDBAuthDriverForm"%>

<%
	String basePath = request.getContextPath();
	List dbfeildMapList = (List)session.getAttribute("dbfeildMapList");
	
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/driver-mapping.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/driver/radius-dbauth-driver.js"></script>
<script src="<%=request.getContextPath()%>/js/servicepolicy/formToWizard.js" type="text/javascript"></script> 

  <style type="text/css">
        fieldset { border:none;margin:-10px;}
        legend { font-size:18px; margin:0px; padding:10px 0px; color:#b0232a; font-weight:bold;}
        label { display:block; margin:15px 0 5px;}
        .prev, .next { background-color:#015198; color:#fff; text-decoration:none;font-family: Arial;font-size: 12px;background-image: url('<%=basePath%>/images/light-btn-bkgd.jpg');font-weight: bold;padding: 2px;padding-left: 5px;padding-right: 5px;}
        .prev:hover, .next:hover { color:#fff; text-decoration:none;}
        .prev { float:left;}
        .next { float:right;}
        #steps { list-style:none; width:100%; overflow:hidden; margin:0px; padding:0px;}
        #steps li {font-size:24px; float:left; padding:10px; color:#b0b1b3;}
        #steps li span {font-size:11px; display:block;}
        #steps li.current { color:#000;}
        #makeWizard { background-color:#b0232a; color:#fff; padding:5px 10px; text-decoration:none; font-size:18px;}
        #makeWizard:hover { background-color:#000;}
    </style>
<script language="javascript">

 	$(document).ready(function() {
 		
 		var obj=top.frames["mainFrame"].document.getElementById('contentIframe');
 		
 		if(obj){
 			  if(obj.id === "contentIframe"){
 		 	    	$("#dbAuthDriver").formToWizard({ submitButton: 'SaveAccount' }); 
 		 	    	$('#cancelBtn').hide();
 		 	    }
 			  
 			 if(obj.id !== "contentIframe"){
 		 		setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 	
 		 	}
 		}
 	
		if(!document.getElementById("cacheable").checked) {
			document.getElementById("primaryKeyColumn").disabled = true;
			document.getElementById("sequenceName").disabled = true;
		}else{
			document.getElementById("primaryKeyColumn").disabled = false;
			document.getElementById("sequenceName").disabled = false;
		}
		
		var logicalNameData =eval(('<bean:write name="logicalNameData"/>').replace(new RegExp("&quot;", 'g'),"\""));
		/* set Logical Data JsonObject */
		setLogicalNameData(logicalNameData);
		
		/* set Logical Name drop down for default value */ 
		setLogicalnameDropDown("mappingtbl","logicalnmVal",true);
		
		/* bind click event of delete image */
		$('#mappingtbl td img.delete').live('click',function() {
			 $(this).parent().parent().remove(); 
			 setLogicalnameDropDown("mappingtbl","logicalnmVal",true); /* set Logical Name drop down after remove row */
		});
	});		
 	
</script>




<html:form action="/createRadiusDBAuthDriver" styleId="dbAuthDriver">
	<html:hidden name="createRadiusDBAuthDriverForm" property="action" value="create" />
	<html:hidden name="createRadiusDBAuthDriverForm" property="driverInstanceName" />
	<html:hidden name="createRadiusDBAuthDriverForm" property="driverDesp" />
	<html:hidden name="createRadiusDBAuthDriverForm" property="driverRelatedId" />

	<html:hidden property="itemIndex" />

	<table cellpadding="0" cellspacing="0" border="0"
		width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
							<div id="main">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header"><bean:message
											bundle="driverResources" key="driver.createdbauthDriver" />
									</td>
								</tr>
								<tr>
									<td  width="100%" colspan="2">
									 	 <fieldset>
										<table cellpadding="0" cellspacing="0" border="0" width="100%">
											<tr>
												<td align="left" class="tblheader-bold" valign="top"
													colspan="6"><bean:message bundle="driverResources"
														key="driver.dbauthdriver.details" /></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="27%">
													<bean:message bundle="driverResources" key="driver.dbauthdriver.ds" /> 
													<ec:elitehelp headerBundle="driverResources" text="dbauthdriver.ds" header="driver.dbauthdriver.ds"/>
												</td>
												<td align="left" class="labeltext" valign="top"><html:select
														property="databaseId" styleId="databaseId"
														onchange="setFieldSuggestion()" style="width:200px"
														tabindex="1">
														<html:option value="0">Select</html:option>
														<html:optionsCollection
															name="createRadiusDBAuthDriverForm"
															property="databaseDSList" label="name" value="databaseId" />
													</html:select><font color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="driverResources" key="driver.tablename" /> 
													<ec:elitehelp headerBundle="driverResources" text="dbauthdriver.tblname" header="driver.tablename"/>
												</td>
												<td align="left" class="labeltext" valign="top"><html:text
														styleId="tableName" property="tableName" size="30"
														maxlength="128" style="width:250px" tabindex="2" /><font
													color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="driverResources" key="driver.querytimeout" /> 
													<bean:message key="general.seconds" /> 
													<ec:elitehelp headerBundle="driverResources" text="dbauthdriver.maxquerytimeout" header="driver.querytimeout"/>
												</td>
												<td align="left" class="labeltext" valign="top"><html:text
														styleId="querytimeout" property="dbQueryTimeout" size="20"
														maxlength="10" style="width:250px" tabindex="3" /><font
													color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top"><bean:message
														bundle="driverResources" key="driver.querytimeoutcount" />
													<ec:elitehelp headerBundle="driverResources" text="dbauthdriver.maxquerytimeout" header="driver.querytimeoutcount"/>
												</td>
												<td align="left" class="labeltext" valign="top"><html:text
														styleId="timeoutcount" property="maxQueryTimeoutCount"
														size="20" maxlength="10" style="width:250px" tabindex="4" /><font
													color="#FF0000"> *</font></td>
											</tr>

											<tr>
												<td align="left" class="captiontext" valign="top"><bean:message
														bundle="driverResources" key="driver.profilelookupcolumn" />
													<ec:elitehelp headerBundle="driverResources" text="dbauthdriver.profilelookup" header="driver.profilelookupcolumn"/>
												</td>
												<td align="left" class="labeltext" valign="top"><html:text
														styleId="profileLookupColumn"
														property="profileLookupColumn" size="30" maxlength="30"
														style="width:250px" tabindex="5" /><font color="#FF0000">
														*</font></td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top"><bean:message
														bundle="driverResources"
														key="driver.useridentityattributes" />
													<ec:elitehelp headerBundle="driverResources" text="driver.usridentity" header="driver.useridentityattributes"/>
												</td>
												<td align="left" class="labeltext" valign="top"><input
													type="text" name="userIdentityAttributes"
													id="userIdentityAttributes" size="60" maxlength="256"
													autocomplete="off" onkeyup="setColumnsOnUserIdentity();"
													style="width: 250px" tabindex="6" /></td>
											</tr>
											</table>
											</fieldset>
										</td>
									</tr>
										<tr>
											<td  width="100%">
											<fieldset>
											<table cellpadding="0" cellspacing="0" border="0" width="100%">
												<tr>
													<td align="left" class="tblheader-bold" valign="top"
														colspan="6"><bean:message bundle="driverResources"
															key="driver.dbauthdriver.cacheparameterdetails" /></td>
												</tr>
	
												<tr>
													<td align="left" class="labeltext" valign="top"></td>
													<td align="left" valign="top" class="labeltext"><html:checkbox
															styleId="cacheable" property="cacheable" value="true"
															onclick="enableAll();" tabindex="7"></html:checkbox> <bean:message
															bundle="driverResources" key="driver.cacheable" /> 
														<ec:elitehelp headerBundle="driverResources" text="dbauthdriver.cacheable" header="driver.cacheable"/>
													</td>
												</tr>
	
												<tr>
													<td align="left" class="captiontext" valign="top" width="27%">
														<bean:message bundle="driverResources" key="driver.primarykeycolumn" />
														<ec:elitehelp headerBundle="driverResources" text="dbauthdriver.primarykeycolumn" header="driver.primarykeycolumn"/>
												</td>
													<td align="left" class="labeltext" valign="top"><html:text
															styleId="primaryKeyColumn" property="primaryKeyColumn"
															size="20" maxlength="30" style="width:250px" tabindex="8" />
													</td>
												</tr>
	
												<tr>
													<td align="left" class="captiontext" valign="top" width="27%">
														<bean:message bundle="driverResources" key="driver.sequencename" /> 
														<ec:elitehelp headerBundle="driverResources" text="dbauthdriver.sequencename" header="driver.sequencename"/>
													</td>
													<td align="left" class="labeltext" valign="top"><html:text
															styleId="sequenceName" property="sequenceName" size="20"
															maxlength="30" style="width:250px" tabindex="9" /></td>
												</tr>
											</table>
											</fieldset>
										</td>
									</tr>
									<tr>
										<td  width="100%">
											<fieldset>
											<table cellpadding="0" cellspacing="0" border="0" width="100%">
												<tr>
													<td align="left" class="tblheader-bold" valign="top"
														colspan="3"><bean:message bundle="driverResources"
															key="driver.fieldmap" /></td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top"
														colspan="2"><input type="button" onclick='addNewRow("dbMappingTable","mappingtbl","logicalnmVal",true);'
														value=" Add " class="light-btn" style="size: 140px"
														tabindex="10"></td>
												</tr>
	
												<tr>
													<td align="left" width="100%" class="captiontext"
														colspan="3" valign="top">
														<table width="97%" id="mappingtbl" cellpadding="0"
															cellspacing="0" border="0">
															<tr>
																<td align="left" class="tblheader" valign="top">
																	Logical Name 
																	<ec:elitehelp headerBundle="driverResources" text="dbauthdriver.logicalname" header="Logical Name"/>
																</td>
																<td align="left" class="tblheader" valign="top">
																	DB Field
																	<ec:elitehelp headerBundle="driverResources" text="dbauthdriver.dbattribute" header="DB Fields"/>
																</td>
																<td align="left" class="tblheader" valign="top" width="15%">
																	<bean:message bundle="driverResources" key="driver.defaultvalue" /> 
																	<ec:elitehelp headerBundle="driverResources" text="dbauthdriver.defaultvalue" header="Default Value"/>
																</td>
																<td align="left" class="tblheader" valign="top">
																	<bean:message bundle="driverResources" key="driver.valuemapping" />
																	<ec:elitehelp headerBundle="driverResources" text="dbauthdriver.valuemapping" header="Value Mapping"/>
																</td>
																<td align="left" class="tblheader" valign="top"width="5%">Remove</td>
															</tr>
															<logic:iterate id="obj" name="defaultMapping" type=" com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthFieldMapData">
															<tr>
																<td class="allborder">
																	<select class="noborder"  name="logicalnmVal"  id="logicalnmVal" style="width:100%" tabindex="10" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
																		<option value='<bean:write name="obj" property="nameValuePoolData.value"/>'><bean:write name="obj" property="nameValuePoolData.name"/> </option>
																	</select>
																</td>
																<td class="tblrows"><input class="noborder" type="text" name="dbfieldVal"  maxlength="1000" size="28" style="width:100%" tabindex="10" value='<bean:write name="obj" property="dbField"/>' onfocus="setAutoCompleteData(this);"/></td>
																<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width:100%" tabindex="10"/></td>
																<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width:100%" tabindex="10"/></td>
																<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="10"/></td>
															</tr>
															</logic:iterate>
														</table>
													</td>
												</tr>
												<tr>
												<td  width="100%">
												<p>
												<table cellpadding="0" cellspacing="0" border="0" width="100%">
													<tr>
														<td class="btns-td" valign="middle">&nbsp;</td>
														<td class="btns-td" valign="middle" colspan="2"><input
															type="button" name="c_btnCreate" id="c_btnCreate2"
															value=" Create " class="light-btn" onclick="validateForm()"
															tabindex="11"> <input type="reset" id="cancelBtn"
															name="c_btnDeletePolicy"
															onclick="javascript:location.href='<%=basePath%>/initSearchDriver.do?'"
															value="Cancel" class="light-btn" tabindex="12"></td>
													</tr>
												</table>
												</p>
												</td>
											</tr>
											</table>
											</fieldset>
											</td>
											</tr>
										</table>
										</div>
									</td>
								</tr>
								<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	
</html:form>
 <!-- Mapping Table Row template -->
	<table style="display:none;" id="dbMappingTable">
		<tr>
			<td class="allborder">
				<select class="noborder"  name="logicalnmVal"  id="logicalnmVal" style="width:100%" tabindex="10" onchange='setLogicalnameDropDown("mappingtbl","logicalnmVal",true);'>
				</select>
			</td>
			<td class="tblrows"><input class="noborder" type="text" name="dbfieldVal"  maxlength="1000" size="28" style="width:100%" tabindex="10" onfocus="setAutoCompleteData(this);"/></td>
			<td class="tblrows"><input class="noborder" type="text" name="defaultValue" maxlength="1000" size="28" style="width:100%" tabindex="10"/></td>
			<td class="tblrows"><input class="noborder" type="text" name="valueMapping" maxlength="1000" size="30" style="width:100%" tabindex="10"/></td>
			<td class="tblrows" align="center" colspan="3"><img value="top" src="<%=basePath%>/images/minus.jpg"  class="delete" style="padding-right: 5px; padding-top: 5px;" height="14"  tabindex="10"/></td>
  		</tr>
	</table>
	<script>
	setTitle('<bean:message bundle="driverResources" key="driver.driver"/>'); 
	</script>