<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.radius.policies.radiuspolicy.forms.RadiusPolicyForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="java.util.List"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>


<%
	RadiusPolicyForm radiusPolicyForm = (RadiusPolicyForm) request.getAttribute("radiusPolicyForm");
	%>
<%
    String basePath = request.getContextPath();
    String statusVal=(String)request.getParameter("status");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script type="text/javascript" language="javascript"
	src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript"
	src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>
setExpressionData("Radius");
var isValidName;
$(document).ready(function() {
	var chkBoxVal='<%=statusVal%>';
	if(chkBoxVal=='Hide'){
		document.getElementById("strStatus").checked=false;
	}else{
		document.getElementById("strStatus").checked=true;
	}
});
function customValidate()
{
	var itemRetVar;
	var dicRetVar;	
	if(isNull(document.forms[0].name.value)){
		alert('Radius Policy Name must be specified');
		return false;
	}

	if(!isValidName) {
		alert('Enter Valid Policy Name');
		document.forms[0].name.focus();
		return false;
	}
	
	if(!validateName(document.forms[0].name.value)){
		alert('Policy Name should have following characters. A-Z, a-z, 0-9, _ and - ');
		return false;
	}
	
	if(isEmptyById("checkItem") && isEmptyById("rejectItem") && isEmptyById("replyItem") && isEmptyById("addItem")) { 
		alert('configure atleast one field among Check Item Expressions, Add Item Expressions, Reject Item Expressions and Reply Item Expressions.');
		return false;
	}
	$("#checkItem").closest("form").submit();
	
	
}
function validateName(val)
{
	var test1 = /(^[A-Za-z0-9-_]*$)/;
	var regexp =new RegExp(test1);
	if(regexp.test(val)){
		return true;
	}//04/19/2011
	return false;
}

function initialize(){
	document.getElementById('checkItemForward').checked = false;
	document.getElementById('rejectItemForward').checked = false;
	document.getElementById('replyItemForward').checked = false;
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.RADIUS_POLICY%>',searchName,'create','','verifyNameDiv');
}


function openTimePeriod(){	
	$.fx.speeds._default = 1000;
	document.getElementById("popupTimePeriod").style.visibility = "visible";
	$( "#popupTimePeriod" ).dialog({
		modal: true,
		autoOpen: false,		
		height: "auto",
		width: 500,				
		buttons:{					
            'Add': function() {	
            		emptyTimePeriodErrorDiv(); // Remove Error Msg From Div
					var monthOfYear = $('#moy').val(); 
					var dayOfMonth = $('#dom').val();
					var dayOfWeek = $('#dow').val();
					var timePeriod = $('#timeOfPeriod').val(); 
					if(isEmpty(monthOfYear) && isEmpty(dayOfMonth) && isEmpty(dayOfWeek) && isEmpty(timePeriod) ){
						$("#validationError").html("At least value for one field must be specified.");//alert('At least value for one field must be specified.');
		     		}else{
		     			if(!isValidTimePeriod($.trim(monthOfYear), $.trim(dayOfMonth), $.trim(dayOfWeek), $.trim(timePeriod)))
		     				return ;
		         		if(!isDuplicateTimeRestriction($.trim(monthOfYear), $.trim(dayOfMonth), $.trim(dayOfWeek), $.trim(timePeriod))){
		         			monthOfYear = isEmpty(monthOfYear) ? "" : $.trim(monthOfYear) ;
		         			dayOfMonth = isEmpty(dayOfMonth) ? "" : $.trim(dayOfMonth) ;
		         			dayOfWeek = isEmpty(dayOfWeek) ? "" : $.trim(dayOfWeek) ;
		         			timePeriod = isEmpty(timePeriod) ? "" : $.trim(timePeriod) ;
		         			imgPath = "<%=basePath%>"+ "/images/minus.jpg" ;
		         			$("#timePeriodtbl tr:last").after("<tr>"+
		         											  "<td class='tblfirstcol' align ='right'>" + monthOfYear + "<input type='hidden' name = 'monthOfYear' id='monthOfYear' value='" + monthOfYear + "'/>&nbsp;</td>"+
		         											  "<td class='tblrows' align ='right'>" + dayOfMonth + "<input type='hidden' name = 'dayOfMonth' id = 'dayOfMonth' value='" + dayOfMonth + "'/>&nbsp;</td>"+
		         											  "<td class='tblrows' align ='right'>" + dayOfWeek + "<input type='hidden' name = 'dayOfWeek' id = 'dayOfWeek' value='" + dayOfWeek + "'/>&nbsp;</td>"+
		         											  "<td class='tblrows' align ='right'>" + timePeriod + "<input type='hidden' name = 'timePeriod'  id = 'timePeriod' value='" + timePeriod + "'/>&nbsp;</td>"+
		         											  "<td class='tblrows' align ='center'><img src='"+imgPath+"' class='delete' height='15' /></td>"+
		         											  "</tr>");
		         			
		         			$('#timePeriodtbl td img.delete').live('click',function() {
		       				 				$(this).parent().parent().remove(); 
		       				 });		         						          					          	
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
    		document.getElementById("timeOfPeriod").value = "";
			document.getElementById("moy").value = "";
			document.getElementById("dom").value = "";
			document.getElementById("dow").value = "";
			emptyTimePeriodErrorDiv();
    	}				
	});
	$( "#popupTimePeriod" ).dialog("open");
	
}


function isValidTimePeriod(monthOfYear,dayOfMonth,dayOfWeek,timePeriod){
	var timePeriodRegex = /[^\d;,-]+/g ;
	var splitRegx =  /[;,-]/;;
	var timeRegex = /[^\d;,-:]+/g ;
	var isValidTimePeriod = true; 
	//Month Of Year Validation
	if(!isValid(timePeriodRegex, monthOfYear) || !isAllow(monthOfYear.split(splitRegx),12)) {  
		$("#monthOfYearError").html("<br>Month Of Year is not valid.");
		isValidTimePeriod = false;;
	}
	//Day Of Month
	if(!isValid(timePeriodRegex, dayOfMonth) || !isAllow(dayOfMonth.split(splitRegx),31)) {  
		$("#dayOfMonthError").html("<br>Day of Month is not valid.");
		isValidTimePeriod = false;;
	}
	//Day Of Week
	if(!isValid(timePeriodRegex, dayOfWeek) || !isAllow(dayOfWeek.split(splitRegx),7)) {  
		$("#dayOfWeekError").html("<br>Day of Week is not valid.");
		isValidTimePeriod = false;;
	}
	
	//Time Period
	if(!isValid(timeRegex, timePeriod) || !isAllowTime(timePeriod.split(splitRegx),24,60)) {  
		$("#timePeriodError").html("<br>Time Period is not valid.");
		isValidTimePeriod = false;;
	}
	return isValidTimePeriod;
}

function isValid(regulrExpression, str) {
	var isValid = true;
	var matches = (str.match(regulrExpression));
	if(matches == null) {
		return true;
	} else  {
		$(matches).each( function(index,item){
			if(!isEmpty(item)) {
				isValid = false;
				return false;
			}
		});
	}
	return isValid;
}

function isAllowTime(paramArray,maxAllowHour,maxAllowMinute){
	var isAllow = true; 
	$(paramArray).each(function(index, item) {
		var timeArray = item.split(/[:]/);
		if(timeArray.length > 2){
			isAllow = false;
			return false;
		}else if (timeArray.length == 2) {
			if(timeArray[0] > maxAllowHour || timeArray[1] > maxAllowMinute) {
				isAllow = false;
				return false;
			} 
		}else {
			if(item > maxAllowHour) {
				isAllow = false;
				return false;		
			}
		}
		
	});
	return isAllow;
} 

function isAllow(paramArray,maxAllow) {
	var isAllow = true; 
	$(paramArray).each(function(index, item) {
		if(item > maxAllow) {
			isAllow = false;
			return false;		
		}
	});
	return isAllow;
}

function isDuplicateTimeRestriction(monthOfYear,dayOfMonth,dayOfWeek,timePeriod) {
	var isDuplicate = false;
	$('#timePeriodtbl tr').each(function(index) {
		if(index > 0) {
        	var tds = $(this).find('td');
        	var moy = $.trim($(tds).eq(0).text());
        	var dom = $.trim($(tds).eq(1).text());
        	var dow = $.trim($(tds).eq(2).text());
        	var timeOfPeriod = $.trim($(tds).eq(3).text());
            if(moy == monthOfYear && dom == dayOfMonth && dow == dayOfWeek && timeOfPeriod == timePeriod ) {
            	$("#validationError").append("Mapping with this value is already present so add another value for attribute");//alert("Mapping with this value is already present so add another value for attribute");
        	   isDuplicate = true;
           }
        }
      });
	return isDuplicate;
}

function emptyTimePeriodErrorDiv() {
	$("#validationError").empty();
	$("#monthOfYearError").empty();
	$("#dayOfMonthError").empty();
	$("#dayOfWeekError").empty();
	$("#timePeriodError").empty();
}
setTitle('<bean:message bundle="radiusResources" key="radiuspolicy.radiuspolicy"/>');
</script>
<html>
<body onload="document.radiusPolicyForm.name.focus();">



	<html:form action="/createRadiusPolicy">
		<html:hidden name="radiusPolicyForm" styleId="action"
			property="action" value="add" />
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
												key="radiuspolicy.createradiuspolicy" /></td>
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
													<td align="left" class="captiontext" valign="top">
														<bean:message bundle="radiusResources" 
															key="radiuspolicy.name" /> 
																<ec:elitehelp headerBundle="radiusResources" 
																	text="radiuspolicy.name" 
																		header="radiuspolicy.name"/>
													</td>
													<td align="left" class="labeltext" valign="top" width="50%">
														<table width="100%" border="0" cellpadding="0"
															cellspacing="0">
															<tr>
																<td width="30%"><html:text styleId="name"
																		tabindex="1" name="radiusPolicyForm" property="name"
																		onblur="verifyName();" size="30"
																		styleClass="flatfields" style="font-family: Verdana; width:250px "
																		maxlength="30"  />
																	<div id="verifyNameDiv" class="labeltext"></div></td>
																<td width="45%" class="labeltext">&nbsp;<html:checkbox
																		styleId="strStatus" name="radiusPolicyForm" tabindex="2"
																		property="strStatus" value="1" />&nbsp;Active
																</td>
															</tr>
														</table>
													</td>
												</tr>

												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="radiusResources" key="radiuspolicy.description" />
														<ec:elitehelp headerBundle="radiusResources" 
														text="radiuspolicy.description" header="radiuspolicy.description"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="2">
														<table width="100%" border="0" cellpadding="0"
															cellspacing="0">
															<tr>
																<td><html:textarea styleId="description"
																		tabindex="3" property="description" cols="30" rows="4"
																		style="width:250px" name="radiusPolicyForm" /></td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td colspan="3">&nbsp;</td>
												</tr>
												<tr>
													<td colspan="3" class="tblheader-bold"><bean:message
															bundle="radiusResources" key="radiuspolicy.expressions" /></td>
												</tr>
												<tr>
													<td colspan="3">&nbsp;</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="radiusResources"
														key="radiuspolicy.checkitems.expression" /> 
														<ec:elitehelp headerBundle="radiusResources" 
														text="radiuspolicy.checkitem" header="radiuspolicy.checkitems.expression"/>
													</td>

													<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;&nbsp;
														<html:textarea property="checkItem" tabindex="4"
															styleId="checkItem" cols="30" rows="4"
															style="width:250px"></html:textarea> <img
														alt="Expression" src="<%=basePath%>/images/lookup.jpg"
														onclick="popupExpression('checkItem');" tabindex="5" />
													</td>
												</tr>
												<tr>
													<td colspan="3">&nbsp;</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="radiusResources"
														key="radiuspolicy.additems.expression" /> 
														<ec:elitehelp headerBundle="radiusResources" 
														text="radiuspolicy.additem" header="radiuspolicy.additems.expression"/>
													</td>
													<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;&nbsp;
														<html:textarea property="addItem" tabindex="4"
															styleId="addItem" cols="30" rows="4"
															style="width:250px"></html:textarea> <img
														alt="Expression" src="<%=basePath%>/images/lookup.jpg"
														onclick="popupExpression('addItem');" tabindex="5" /> 
													</td>
												</tr>
												<tr>
													<td colspan="3">&nbsp;</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="radiusResources"
														key="radiuspolicy.rejectitems.expression" />
														<ec:elitehelp headerBundle="radiusResources" 
														text="radiuspolicy.rejectitem" header="radiuspolicy.rejectitems.expression"/>									
													</td>

													<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;&nbsp;
														<html:textarea property="rejectItem" styleId="rejectItem"
															tabindex="6" cols="30" rows="4" style="width:250px"></html:textarea>
														<img alt="Expression"
														src="<%=basePath%>/images/lookup.jpg"
														onclick="popupExpression('rejectItem');" tabindex="7" />
													</td>
												</tr>
												<tr>
													<td colspan="3">&nbsp;</td>
												</tr>
												<tr>
													<td align="left" class="captiontext" valign="top" width="12%">
														<bean:message bundle="radiusResources"
														key="radiuspolicy.replyitems.expression" />
														<ec:elitehelp headerBundle="radiusResources" 
														text="radiuspolicy.replyitem" header="radiuspolicy.replyitems.expression"/> 
													</td>

													<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;&nbsp;
														<html:textarea property="replyItem" tabindex="8"
															styleId="replyItem" cols="30" rows="4"
															style="width:250px"></html:textarea> <img
														alt="Expression" src="<%=basePath%>/images/lookup.jpg"
														onclick="popupExpression('replyItem');" tabindex="9" />
													</td>
												</tr>
												<tr>
													<td colspan="3">&nbsp;</td>
												</tr>
												<tr>
													<td colspan="3" class="tblheader-bold"><bean:message
															bundle="radiusResources"
															key="radiuspolicy.timebasecondition" /></td>
												</tr>
												<tr>
													<td colspan="3">&nbsp;</td>
												</tr>
												<tr>
													<td colspan="4" class="captiontext"><input
														type="button" value="   Add   " class="light-btn"
														onclick="openTimePeriod()" tabindex="10"> 
														<ec:elitehelp headerBundle="radiusResources" 
														text="radiuspolicy.timebasecondition" header="radiuspolicy.timebasecondition"/>
													</td>
												</tr>

												<tr>
													<td colspan="3" class="captiontext">
														<table width="90%" cellSpacing="0" cellPadding="0"
															border="0" id="timePeriodtbl">
															<tr>
																<td align="center" class="tblheader" valign="top"
																	width="24%">Month of Year</td>
																<td align="center" class="tblheader" valign="top"
																	width="24%">Day of Month</td>
																<td align="center" class="tblheader" valign="top"
																	width="24%">Day of Week</td>
																<td align="center" class="tblheader" valign="top"
																	width="24%">Time Period</td>
																<td align="center" class="tblheader" valign="top"
																	width="4%">Remove</td>
															</tr>
														</table>
													</td>
												</tr>


												<tr>
													<td colspan="3">&nbsp;</td>
												</tr>
												<tr>
													<td>&nbsp;</td>
													<td align="left" valign="middle" colspan="3">&nbsp;&nbsp;
														<input type="button" tabindex="11" name="c_btnCreate"
														onclick="customValidate();" value="   Create   "
														class="light-btn">&nbsp;&nbsp; <input type="reset"
														tabindex="12" name="c_btnDeletePolicy"
														onclick="javascript:location.href='initSearchRadiusPolicy.do'"
														value=" Cancel " class="light-btn">
													</td>
												</tr>
												<tr>
													<td colspan="3">&nbsp;</td>
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
	<div id="popupExpr" style="display: none;" title="ExpressionBuilder">
		<div id="expBuilderId" align="center"></div>
	</div>

	<div id="popupTimePeriod" title="Time Period" style="display: none;">
		<div id="validationError" class="labelText" style="color: red"></div>
		<table>
			<tr>
				<td align="left" class="labeltext" valign="top" width="20%">
					Month of Year</td>
				<td align="left" class="labeltext" valign="top" width="32%"><input
					type="text" tabindex="13" maxlength="60" size="25" value=""
					id="moy" style="width: 250px"> <span id="monthOfYearError"
					class="labelText" style="color: red"></span></td>
			</tr>

			<tr>
				<td align="left" class="labeltext" valign="top" width="20%">
					Day of Month</td>
				<td align="left" class="labeltext" valign="top" width="32%"><input
					type="text" tabindex="14" maxlength="60" size="25" value=""
					id="dom" style="width: 250px"> <span id="dayOfMonthError"
					class="labelText" style="color: red"></span></td>
			</tr>

			<tr>
				<td align="left" class="labeltext" valign="top" width="20%">
					Day of Week</td>
				<td align="left" class="labeltext" valign="top" width="32%"><input
					type="text" tabindex="15" maxlength="60" size="25" value=""
					id="dow" style="width: 250px"> <span id="dayOfWeekError"
					class="labelText" style="color: red"></span></td>
			</tr>

			<tr>
				<td align="left" class="labeltext" valign="top" width="20%">
					Time Period</td>
				<td align="left" class="labeltext" valign="top" width="32%"><input
					type="text" tabindex="16" maxlength="60" size="25" value=""
					id="timeOfPeriod" style="width: 250px"> <span
					id="timePeriodError" class="labelText" style="color: red"></span></td>
			</tr>
		</table>
	</div>
</body>
</html>
