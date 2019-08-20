<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.Timestamp.*"%>
<%@ page
	import="com.elitecore.elitesm.web.radius.policies.accesspolicy.forms.SearchAccessPolicyForm"%>
<%@ page
	import="com.elitecore.elitesm.web.radius.policies.accesspolicy.forms.CreateAccessPolicyForm"%>
<%@ page
	import="com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyDetailData"%>
<%@ page import="org.apache.struts.taglib.tiles.ImportAttributeTag"%>
<%@ page import="org.apache.struts.taglib.tiles.GetAttributeTag"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page
	import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@page
	import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>

<%
	String basePath = request.getContextPath();
    String statusVal=(String)request.getParameter("status");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
var isValidName;

function addnode(){
	var accessStatus = document.getElementsByName('accessstatus');
	for(i=0;i<accessStatus.length;i++){
		accessStatus[i].disabled = false;
	}
	document.forms[0].action.value = 'addDetail';
	document.forms[0].submit();
}
function showTimeslapTitle(status)
{

	var allowedTimeslapTitle = '<bean:message bundle="radiusResources" key="accesspolicy.allowedtimeslaps" />';
	var deniedTimeslapTitle = '<bean:message bundle="radiusResources" key="accesspolicy.deniedtimeslaps" />';
	var element = document.getElementById("timeslapTitle");
	if(status == false){
		element.innerHTML=allowedTimeslapTitle;
	}else{
		element.innerHTML=deniedTimeslapTitle;
	}
	
}
$(document).ready(function() {
	var chkBoxVal='<%=statusVal%>';
	if(chkBoxVal=='Hide'){
		document.getElementById("status").checked=false;
	}else{
		document.getElementById("status").checked=true;
	}
});
function validateCreate()
{
   if(document.forms[0].name.value == ''){
		alert('Name is a compulsory field Please enter required data in the field');
	}else if(!isValidName) {
		alert('Enter Valid Policy Name');
		document.forms[0].name.focus();
		return;
	}else{
		var accessStatus = document.getElementsByName('accessstatus');
		for(i=0;i<accessStatus.length;i++){
			accessStatus[i].disabled = false;
		}
		document.searchAccessPolicyForm.action.value = 'update';
		document.forms[0].submit();
	}
}

function deleteSubmit(index)
{
	var accessStatus = document.getElementsByName('accessstatus');
	for(i=0;i<accessStatus.length;i++){
		accessStatus[i].disabled = false;
	}
	
	document.forms[0].action.value = 'Remove';
	document.forms[0].itemIndex.value = index;
	document.forms[0].submit();
}
function defaultSlapTilte(){
	var allowedTimeslapTitle = '<bean:message bundle="radiusResources" key="accesspolicy.allowedtimeslaps" />';
	var deniedTimeslapTitle = '<bean:message bundle="radiusResources" key="accesspolicy.deniedtimeslaps" />';
	var element = document.getElementById("timeslapTitle");
	for (var i=0; i < document.forms[0].accessstatus.length; i++) {
	   if (document.forms[0].accessstatus[i].checked) {
		    if(document.forms[0].accessstatus[i].value == 'D'){
				element.innerHTML=allowedTimeslapTitle;
			}else{
				element.innerHTML=deniedTimeslapTitle;
			}
	   }
    }
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.ACCESS_POLICY%>',searchName,'create','','verifyNameDiv');
}
setTitle('<bean:message bundle="radiusResources" key="accesspolicy.accesspolicy"/>');
</script>

<%
				int j = 0;
				int index = 0;
				SearchAccessPolicyForm searchAccessPolicyForm = (SearchAccessPolicyForm) request
				.getAttribute("searchAccessPolicyForm");
			%>


<html:form action="/createAccessPolicy">
	<html:hidden name="searchAccessPolicyForm" styleId="action"
		property="action" />
	<html:hidden name="searchAccessPolicyForm" styleId="itemIndex"
		property="itemIndex" />
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
									<td class="table-header"><bean:message
											bundle="radiusResources"
											key="accesspolicy.createaccesspolicy" /></td>
								</tr>
								<tr>
									<td colspan="3">
										<table name="c_tblCrossProductList" width="100%"
											cellpadding="0" cellspacing="0">
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="radiusResources" key="accesspolicy.name" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="accesspolicy.name" header="accesspolicy.name" />
												</td>
												<td align="left" class="labeltext" valign="top" width="32%">
													<html:text styleId="name" onkeyup="verifyName();"
														tabindex="1" property="name" size="30" maxlength="30"
														style="width:250px" /> <font color="#FF0000"> *</font>
													<div id="verifyNameDiv" class="labeltext"></div>
												</td>
												<td align="left" class="labeltext" valign="top"><html:checkbox
														styleId="status" property="status" tabindex="2" value="1"></html:checkbox>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top">
													<bean:message bundle="radiusResources" key="accesspolicy.defaultaccess" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="accesspolicy.defaultaccess" header="accesspolicy.defaultaccess"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:radio styleId="accessstatus" property="accessstatus"
														value="A" onclick="javascript: showTimeslapTitle(true);"
														tabindex="3">
														<bean:message bundle="radiusResources"
															key="accesspolicy.allowed" />
													</html:radio> <html:radio styleId="accessstatus" property="accessstatus"
														value="D" onclick="javascript: showTimeslapTitle(false);"
														tabindex="4">
														<bean:message bundle="radiusResources"
															key="accesspolicy.denied" />
													</html:radio>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top" width="16%">
													<bean:message bundle="radiusResources" key="accesspolicy.description" />
													<ec:elitehelp headerBundle="radiusResources" 
													text="accesspolicy.description" header="accesspolicy.description" />
												</td>
												<td align="left" class="labeltext" valign="top" colspan="2">
													<html:textarea styleId="description" property="description"
														cols="50" rows="4" style="width:250px" tabindex="5"></html:textarea>
												</td>
											</tr>
											<tr>
												<td align="left" class="captiontext" valign="top"><input
													type="button" value="Add" styleId="addNode"
													property="addNode" onclick="addnode()" class="light-btn"
													tabindex="6" /></td>
											</tr>
											<tr>
												<td class="small-gap" width="7">&nbsp;</td>
											</tr>
											<tr>
												<td align="left" colspan="3" class="tblheader-bold"
													valign="top" width="100%">
													<table cellpadding="0" cellspacing="0" border="0"
														width="100%">
														<tr>
															<td class="tblheader-bold" align="left" width="120px">
																<div title="" name="timeslapTitle" id="timeslapTitle">
																	<bean:message bundle="radiusResources"
																		key="accesspolicy.deniedtimeslaps" />
																</div>
															</td>
															<td class="tblheader-bold" class="captiontext">
																<ec:elitehelp headerBundle="radiusResources" 
																text="accesspolicy.timeslaps" header="accesspolicy.deniedtimeslaps"/>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td class="small-gap" width="7">&nbsp;</td>
											</tr>
											<tr>
												<td colspan="3">
													<table width="98%" cols="5" id="listTable" type="tbl-list"
														border="0" cellpadding="0" cellspacing="0">
														<tr>
															<td align="center" class="tblheader" valign="top"
																width="7%"><bean:message bundle="radiusResources"
																	key="accesspolicy.serialno" /></td>
															<td align="center" class="tblheader" valign="top"
																width="20%"><bean:message bundle="radiusResources"
																	key="accesspolicy.startday" /></td>
															<td align="center" class="tblheader" valign="top"
																width="7%"><bean:message bundle="radiusResources"
																	key="accesspolicy.starthours" /></td>
															<td align="center" class="tblheader" valign="top"
																width="7%"><bean:message bundle="radiusResources"
																	key="accesspolicy.startminutes" /></td>
															<td align="center" class="tblheader" valign="top"
																width="20%"><bean:message bundle="radiusResources"
																	key="accesspolicy.endday" /></td>
															<td align="center" class="tblheader" valign="top"
																width="7%"><bean:message bundle="radiusResources"
																	key="accesspolicy.stophours" /></td>
															<td align="center" class="tblheader" valign="top"
																width="7%"><bean:message bundle="radiusResources"
																	key="accesspolicy.stopminutes" /></td>
															<td align="center" class="tblheader" valign="top"
																width="10%"><bean:message bundle="radiusResources"
																	key="accesspolicy.remove" /></td>
														</tr>

														<%
														if (searchAccessPolicyForm.getLstWeeKDay() != null) {
												%>
														<logic:iterate id="accessPolicyBean"
															name="searchAccessPolicyForm" property="lstWeeKDay"
															type="com.elitecore.elitesm.datamanager.radius.policies.accesspolicy.data.IAccessPolicyDetailData">
															<tr>
																<%
															String strSerialNumber = "serialNumber[" + j + "]";
																			String strStartWeekDay = "startWeekDay[" + j + "]";
																			//String strStartTime = "strStartTime[" + j + "]";
																			String strStartHours = "startHours[" + j + "]";
																			String strStartMinutes = "startMinutes[" + j + "]";
																			String strStopHours = "stopHours[" + j + "]";
																			String strStopMinutes = "stopMinutes[" + j + "]";
																			String strEndWeekDay = "endWeekDay[" + j + "]";
																			//String strStopTime = "strStopTime[" + j + "]";
																		  	String strAccessStatus = "accessStatus[" + j + "]";  
														%>

																<html:hidden name="searchAccessPolicyForm"
																	styleId="<%=strSerialNumber%>"
																	property="serialNumber" />


																<td align="center" class="cell-leftline" width="7%">
																	<%=(index + 1)%>
																</td>

																<td align="center" class="cell-rightbottomline"
																	valign="top" width="20%"><html:select
																		name="searchAccessPolicyForm" tabindex="7"
																		styleId="<%=strStartWeekDay%>"
																		property="<%=strStartWeekDay%>" size="1">
																		<html:options collection="lstWeeKDay"
																			property="weekDayId" labelProperty="name" />
																	</html:select></td>


																<td align="center" class="cell-rightbottomline"
																	valign="top" width="7%"><html:select
																		name="searchAccessPolicyForm"
																		styleId="<%=strStartHours%>"
																		property="<%=strStartHours%>" size="1" tabindex="8">
																		<html:option value="0">00</html:option>
																		<html:option value="1">01</html:option>
																		<html:option value="2">02</html:option>
																		<html:option value="3">03</html:option>
																		<html:option value="4">04</html:option>
																		<html:option value="5">05</html:option>
																		<html:option value="6">06</html:option>
																		<html:option value="7">07</html:option>
																		<html:option value="8">08</html:option>
																		<html:option value="9">09</html:option>
																		<html:option value="10">10</html:option>
																		<html:option value="11">11</html:option>
																		<html:option value="12">12</html:option>
																		<html:option value="13">13</html:option>
																		<html:option value="14">14</html:option>
																		<html:option value="15">15</html:option>
																		<html:option value="16">16</html:option>
																		<html:option value="17">17</html:option>
																		<html:option value="18">18</html:option>
																		<html:option value="19">19</html:option>
																		<html:option value="20">20</html:option>
																		<html:option value="21">21</html:option>
																		<html:option value="22">22</html:option>
																		<html:option value="23">23</html:option>

																	</html:select></td>
																<td align="center" class="cell-rightbottomline"
																	valign="top" width="7%"><html:select
																		name="searchAccessPolicyForm"
																		styleId="<%=strStartMinutes%>"
																		property="<%=strStartMinutes%>" size="1" tabindex="9">
																		<html:option value="0">00</html:option>
																		<html:option value="1">01</html:option>
																		<html:option value="2">02</html:option>
																		<html:option value="3">03</html:option>
																		<html:option value="4">04</html:option>
																		<html:option value="5">05</html:option>
																		<html:option value="6">06</html:option>
																		<html:option value="7">07</html:option>
																		<html:option value="8">08</html:option>
																		<html:option value="9">09</html:option>
																		<html:option value="10">10</html:option>
																		<html:option value="11">11</html:option>
																		<html:option value="12">12</html:option>
																		<html:option value="13">13</html:option>
																		<html:option value="14">14</html:option>
																		<html:option value="15">15</html:option>
																		<html:option value="16">16</html:option>
																		<html:option value="17">17</html:option>
																		<html:option value="18">18</html:option>
																		<html:option value="19">19</html:option>
																		<html:option value="20">20</html:option>
																		<html:option value="21">21</html:option>
																		<html:option value="22">22</html:option>
																		<html:option value="23">23</html:option>
																		<html:option value="24">24</html:option>
																		<html:option value="25">25</html:option>
																		<html:option value="26">26</html:option>
																		<html:option value="27">27</html:option>
																		<html:option value="28">28</html:option>
																		<html:option value="29">29</html:option>
																		<html:option value="30">30</html:option>
																		<html:option value="31">31</html:option>
																		<html:option value="32">32</html:option>
																		<html:option value="33">33</html:option>
																		<html:option value="34">34</html:option>
																		<html:option value="35">35</html:option>
																		<html:option value="36">36</html:option>
																		<html:option value="37">37</html:option>
																		<html:option value="38">38</html:option>
																		<html:option value="39">39</html:option>
																		<html:option value="40">40</html:option>
																		<html:option value="41">41</html:option>
																		<html:option value="42">42</html:option>
																		<html:option value="43">43</html:option>
																		<html:option value="44">44</html:option>
																		<html:option value="45">45</html:option>
																		<html:option value="46">46</html:option>
																		<html:option value="47">47</html:option>
																		<html:option value="48">48</html:option>
																		<html:option value="49">49</html:option>
																		<html:option value="50">50</html:option>
																		<html:option value="51">51</html:option>
																		<html:option value="52">52</html:option>
																		<html:option value="53">53</html:option>
																		<html:option value="54">54</html:option>
																		<html:option value="55">55</html:option>
																		<html:option value="56">56</html:option>
																		<html:option value="57">57</html:option>
																		<html:option value="58">58</html:option>
																		<html:option value="59">59</html:option>

																	</html:select></td>

																<td align="center" class="cell-rightbottomline"
																	valign="top" width="20%"><html:select
																		name="searchAccessPolicyForm"
																		styleId="<%=strEndWeekDay%>"
																		property="<%=strEndWeekDay%>" size="1" tabindex="10">
																		<html:options collection="lstWeeKDay"
																			property="weekDayId" labelProperty="name" />
																	</html:select></td>

																<td align="center" class="cell-rightbottomline"
																	valign="top" width="7%"><html:select
																		name="searchAccessPolicyForm"
																		styleId="<%=strStopHours%>"
																		property="<%=strStopHours%>" size="1" tabindex="11">
																		<html:option value="0">00</html:option>
																		<html:option value="1">01</html:option>
																		<html:option value="2">02</html:option>
																		<html:option value="3">03</html:option>
																		<html:option value="4">04</html:option>
																		<html:option value="5">05</html:option>
																		<html:option value="6">06</html:option>
																		<html:option value="7">07</html:option>
																		<html:option value="8">08</html:option>
																		<html:option value="9">09</html:option>
																		<html:option value="10">10</html:option>
																		<html:option value="11">11</html:option>
																		<html:option value="12">12</html:option>
																		<html:option value="13">13</html:option>
																		<html:option value="14">14</html:option>
																		<html:option value="15">15</html:option>
																		<html:option value="16">16</html:option>
																		<html:option value="17">17</html:option>
																		<html:option value="18">18</html:option>
																		<html:option value="19">19</html:option>
																		<html:option value="20">20</html:option>
																		<html:option value="21">21</html:option>
																		<html:option value="22">22</html:option>
																		<html:option value="23">23</html:option>

																	</html:select></td>
																<td align="center" class="cell-rightbottomline"
																	valign="top" width="7%"><html:select
																		name="searchAccessPolicyForm"
																		styleId="<%=strStopMinutes%>"
																		property="<%=strStopMinutes%>" size="1" tabindex="12">
																		<html:option value="0">00</html:option>
																		<html:option value="1">01</html:option>
																		<html:option value="2">02</html:option>
																		<html:option value="3">03</html:option>
																		<html:option value="4">04</html:option>
																		<html:option value="5">05</html:option>
																		<html:option value="6">06</html:option>
																		<html:option value="7">07</html:option>
																		<html:option value="8">08</html:option>
																		<html:option value="9">09</html:option>
																		<html:option value="10">10</html:option>
																		<html:option value="11">11</html:option>
																		<html:option value="12">12</html:option>
																		<html:option value="13">13</html:option>
																		<html:option value="14">14</html:option>
																		<html:option value="15">15</html:option>
																		<html:option value="16">16</html:option>
																		<html:option value="17">17</html:option>
																		<html:option value="18">18</html:option>
																		<html:option value="19">19</html:option>
																		<html:option value="20">20</html:option>
																		<html:option value="21">21</html:option>
																		<html:option value="22">22</html:option>
																		<html:option value="23">23</html:option>
																		<html:option value="24">24</html:option>
																		<html:option value="25">25</html:option>
																		<html:option value="26">26</html:option>
																		<html:option value="27">27</html:option>
																		<html:option value="28">28</html:option>
																		<html:option value="29">29</html:option>
																		<html:option value="30">30</html:option>
																		<html:option value="31">31</html:option>
																		<html:option value="32">32</html:option>
																		<html:option value="33">33</html:option>
																		<html:option value="34">34</html:option>
																		<html:option value="35">35</html:option>
																		<html:option value="36">36</html:option>
																		<html:option value="37">37</html:option>
																		<html:option value="38">38</html:option>
																		<html:option value="39">39</html:option>
																		<html:option value="40">40</html:option>
																		<html:option value="41">41</html:option>
																		<html:option value="42">42</html:option>
																		<html:option value="43">43</html:option>
																		<html:option value="44">44</html:option>
																		<html:option value="45">45</html:option>
																		<html:option value="46">46</html:option>
																		<html:option value="47">47</html:option>
																		<html:option value="48">48</html:option>
																		<html:option value="49">49</html:option>
																		<html:option value="50">50</html:option>
																		<html:option value="51">51</html:option>
																		<html:option value="52">52</html:option>
																		<html:option value="53">53</html:option>
																		<html:option value="54">54</html:option>
																		<html:option value="55">55</html:option>
																		<html:option value="56">56</html:option>
																		<html:option value="57">57</html:option>
																		<html:option value="58">58</html:option>
																		<html:option value="59">59</html:option>

																	</html:select></td>

																<td align="center" class="cell-rightbottomline"
																	valign="top" width="10%"><img
																	src="<%=basePath%>/images/minus.jpg"
																	onclick="deleteSubmit('<%=index%>')" border="0" /></td>
															</tr>

															<%
														j++;
													%>
															<%
														index++;
													%>
														</logic:iterate>
														<%
													}
												%>
													</table>
												</td>
											</tr>

											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2"><input
													type="button" value="Create" property="update"
													onclick="validateCreate()" class="light-btn" tabindex="13" />
													<input type="reset" name="c_btnDeletePolicy"
													onclick="javascript:location.href='<%=basePath%>/initSearchAccessPolicy.do?/>'"
													value="Cancel" class="light-btn" tabindex="14"></td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<%@ include file="/jsp/core/includes/common/Footer.jsp"%>
				</table>
			</td>
		</tr>
	</table>
</html:form>
<script>
 defaultSlapTilte();
 </script>

