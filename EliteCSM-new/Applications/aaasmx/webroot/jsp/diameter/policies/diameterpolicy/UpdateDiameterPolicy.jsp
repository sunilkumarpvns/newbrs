<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms.UpdateDiameterPolicyForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData"%>
<%@ page import="java.util.List"%>

<%
	UpdateDiameterPolicyForm updateDiameterPolicyForm = (UpdateDiameterPolicyForm)request.getAttribute("policyForm");
	DiameterPolicyData diameterPolicyData = (DiameterPolicyData)request.getAttribute("diameterPolicyData");
%>

<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>
setExpressionData("Diameter");


var isValidName;

function customValidate()
{
	var itemRetVar;
	var dicRetVar;	
	if(isNull(document.forms[0].name.value)){
		alert('Diameter Policy Name must be specified');
		return false;
	}

	if(!isValidName) {
		alert('Enter Valid Policy Name');
		document.forms[0].name.focus();
		return false;
	}

	document.forms[0].submit();
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIAMETER_POLICY%>',searchName,'update','<%=diameterPolicyData.getDiameterPolicyId()%>','verifyNameDiv');
}

function openTimePeriod(){	
	$.fx.speeds._default = 1000;
	document.getElementById("popupTimePeriod").style.visibility = "visible";
	$("#validationError").empty();
	$("#monthOfYearError").empty();
	$("#dayOfMonthError").empty();
	$("#dayOfWeekError").empty();
	$("#timePeriodError").empty();
	$( "#popupTimePeriod" ).dialog({
		modal: true,
		autoOpen: false,		
		height: "auto",
		width: 500,				
		buttons:{					
            'Add': function() {	
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
			$("#validationError").empty();
			$("#monthOfYearError").empty();
			$("#dayOfMonthError").empty();
			$("#dayOfWeekError").empty();
			$("#timePeriodError").empty();
    	}				
	});
	$( "#popupTimePeriod" ).dialog("open");
	
}

function isValidTimePeriod(monthOfYear,dayOfMonth,dayOfWeek,timePeriod){
	var timePeriodRegex = /[^\d;,-]+/g ;
	var splitRegx = /[;,-]/;
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
$(document).ready(function(){
	$('#timePeriodtbl td img.delete').live('click',function() {
		$(this).parent().parent().remove(); 
	});
});

</script>

<html:form action="/updateDiameterPolicy">

	<html:hidden name="updateDiameterPolicyForm" styleId="action"
		property="action" value="update" />
	<html:hidden name="updateDiameterPolicyForm" styleId="diameterPolicyId"
		property="diameterPolicyId" />
	<html:hidden name="updateDiameterPolicyForm" styleId="auditUId"
		property="auditUId" />
		
	<table cellpadding="0" cellspacing="0" border="0" width="100%"
		align="right">

		<tr>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td valign="middle" colspan="5">
				<table cellpadding="0" cellspacing="0" border="0" width="100%"
					height="30%">
					<tr>
						<td class="table-header" colspan="5"><bean:message
								bundle="diameterResources" key="diameterpolicy.diameterpolicy" />
						</td>
					</tr>
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="diameterResources"
								key="diameterpolicy.diameterpolicy" />
						</td>
					</tr>


					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="diameterResources"
							key="diameterpolicy.name" /> 
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpolicy.name" header="diameterpolicy.name"/>

						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text styleId="name" tabindex="1" property="name" size="30" onkeyup="verifyName();" maxlength="60" style="width:250px" />
							<font color="#FF0000"> *</font>
							<html:checkbox styleId="status" property="status" value="1" tabindex="2" />Active
							<div id="verifyNameDiv" class="labeltext"></div>
						</td>
						<td align="left" class="labeltext" valign="top">
						</td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top" width="12%">
							<bean:message bundle="diameterResources"
							key="diameterpolicy.description" />
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpolicy.description" header="diameterpolicy.description"/>
						</td>
						<td align="left" class="labeltext" valign="top" colspan="2">
							<table width="100%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td>
										<html:textarea property="description" tabindex="2" styleId="description" cols="40" rows="4" styleClass="font-family: Verdana; width: 250px;"></html:textarea>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>



					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="diameterResources"
							key="diameterpolicy.checkitems.expression" />
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpolicy.checkitems.expression" header="diameterpolicy.checkitems.expression"/> 
						</td>
						<td valign="top" colspan="2"><html:textarea
								property="checkItem" tabindex="3" styleId="checkItem" cols="60"
								rows="4"></html:textarea> <img alt="Expression"
							src="<%=basePath%>/images/lookup.jpg"
							onclick="popupExpression('checkItem');" tabindex="4" /></td>
					</tr>



					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="diameterResources"
							key="diameterpolicy.rejectitems.expression" />
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpolicy.rejectitems.expression" header="diameterpolicy.rejectitems.expression"/>
						</td>
						<td valign="top" colspan="2"><html:textarea
								property="rejectItem" tabindex="5" styleId="rejectItem"
								cols="60" rows="4"></html:textarea> <img alt="Expression"
							src="<%=basePath%>/images/lookup.jpg"
							onclick="popupExpression('rejectItem');" tabindex="6" /></td>
					</tr>

					<tr>
						<td align="left" class="captiontext" valign="top">
							<bean:message bundle="diameterResources"
							key="diameterpolicy.replyitems.expression" /> 
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpolicy.replyitems.expression" header="diameterpolicy.replyitems.expression"/>
						</td>
						<td valign="top" colspan="2"><html:textarea
								property="replyItem" tabindex="7" styleId="replyItem" cols="60"
								rows="4"></html:textarea> <img alt="Expression"
							src="<%=basePath%>/images/lookup.jpg"
							onclick="popupExpression('replyItem');" tabindex="8" /></td>
					</tr>
					<tr>
						<td colspan="3" class="tblheader-bold">
							<bean:message bundle="radiusResources" key="radiuspolicy.timebasecondition" /></td>
					</tr>
					<tr>
						<td colspan="3">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="4" class="captiontext">
							<input type="button" value="   Add   " class="light-btn" onclick="openTimePeriod()" tabindex="10"/> 
							<ec:elitehelp headerBundle="radiusResources" text="radiuspolicy.timebasecondition" header="radiuspolicy.timebasecondition"/>
						</td>
					</tr>
					<tr>
						<td colspan="3">
							<table width="90%" cellSpacing="0" cellPadding="0" border="0" id="timePeriodtbl" class="captiontext">
								<tr>
									<td align="center" class="tblheader" valign="top" width="24%">
										Month of Year
									</td>
									<td align="center" class="tblheader" valign="top" width="24%">
										Day of Month
									</td>
									<td align="center" class="tblheader" valign="top" width="24%">
										Day of Week
									</td>
									<td align="center" class="tblheader" valign="top" width="24%">
										Time Period
									</td>
									<td align="center" class="tblheader" valign="top" width="4%">
										Remove
									</td>
								</tr>
								<logic:iterate id="obj" name="diameterPolicyData" property="diameterPolicyTimePeriodList" type="com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyTimePeriod">
									<tr>
										<td class="tblfirstcol" align='right'>
											<bean:write name="obj" property="monthOfYear" /> &nbsp; 
											<input type='hidden' name='monthOfYear' id='monthOfYear' value="<bean:write name='obj' property='monthOfYear'/>" />
										</td>
										<td class="tblrows" align='right'>
											<bean:write name="obj" property="dayOfMonth" /> &nbsp; 
											<input type='hidden' name='dayOfMonth' id='dayOfMonth' value="<bean:write name='obj' property='dayOfMonth'/>" />
										</td>
										<td class="tblrows" align='right'>
											<bean:write name="obj" property="dayOfWeek" />&nbsp; 
											<input type='hidden' name='dayOfWeek' id='dayOfWeek' value="<bean:write name='obj' property='dayOfWeek'/>" />
										</td>
										<td class="tblrows" align='right'>
											<bean:write name="obj" property="timePeriod" />&nbsp; 
											<input type='hidden' name='timePeriod' id='timePeriod' value="<bean:write name='obj' property='timePeriod'/>" />
										</td>
										<td class='tblrows' align='center'>
											<img src="<%=basePath%>/images/minus.jpg" class="delete" height="15" tabindex="11" />
										</td>
									</tr>
								</logic:iterate>
							</table>
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="2"><input
							type="button" tabindex="9" name="c_btnCreate"
							onclick="customValidate()" id="c_btnCreate2" value="Update"
							class="light-btn"> <input type="reset" tabindex="10"
							name="c_btnDeletePolicy"
							onclick="javascript:location.href='<%=basePath%>/initViewDiameterPolicy.do?diameterPolicyId=<%=updateDiameterPolicyForm.getDiameterPolicyId()%>'"
							value="Cancel" class="light-btn"></td>
					</tr>
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
					Month of Year
				</td>
				<td align="left" class="labeltext" valign="top" width="32%">
					<input	type="text" tabindex="14" maxlength="60" size="25" value="" id="moy" style="width: 250px"/> 
					<span id="monthOfYearError" class="labelText" style="color: red"></span>
				</td>
			</tr>
	
			<tr>
				<td align="left" class="labeltext" valign="top" width="20%">
					Day of Month
				</td>
				<td align="left" class="labeltext" valign="top" width="32%">
					<input type="text" tabindex="15" maxlength="60" size="25" value="" id="dom" style="width: 250px"/> 
					<span id="dayOfMonthError" class="labelText" style="color: red"></span>
				</td>
			</tr>
	
			<tr>
				<td align="left" class="labeltext" valign="top" width="20%">Day of Week</td>
				<td align="left" class="labeltext" valign="top" width="32%">
					<input type="text" tabindex="16" maxlength="60" size="25" value="" id="dow" style="width: 250px"/> 
					<span id="dayOfWeekError" class="labelText" style="color: red"></span>
				</td>
			</tr>
	
			<tr>
				<td align="left" class="labeltext" valign="top" width="20%">
					Time Peroid
				</td>
				<td align="left" class="labeltext" valign="top" width="32%">
					<input type="text" tabindex="17" maxlength="60" size="25" value="" id="timeOfPeriod" style="width: 250px"/> 
					<span id="timePeriodError" class="labelText" style="color: red"></span>
				</td>
			</tr>
		</table>
	</div>
</div>