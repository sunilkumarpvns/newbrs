<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant"%>
<%@page import="com.elitecore.elitesm.web.radius.bwlist.forms.CreateBWListForm"%>
<%@page  import="java.sql.Timestamp"%>
<%@page  import="java.text.DateFormat"%>
<%@page  import="java.text.SimpleDateFormat"%>

<%
	String path = request.getContextPath();

	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	Calendar c = Calendar.getInstance();
	String dateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT);
	String statusVal=(String)request.getParameter("status");
	
	Date now = new Date(); 
	Calendar cal = Calendar.getInstance();  
	cal.add(cal.DAY_OF_MONTH, 1);
	Date tomorrow = cal.getTime();
	DateFormat dateFormatter=new SimpleDateFormat("MM/dd/yyyy HH:mm");
	String formatedDate = dateFormatter.format(tomorrow);
	
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<link rel="stylesheet" href="<%=request.getContextPath()%>/js/calender/jquery.ui.datepicker.css" /> 


<script src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.js"></script>

<script src="<%=request.getContextPath()%>/js/calender/jquery-ui.js" ></script>

<link rel="stylesheet" href="<%=request.getContextPath()%>/css/jquery-ui-timepicker-addon.css">
		
<%-- <script type="text/javascript" src="<%=request.getContextPath()%>/js/calender/jquery-1.9.1.min.js" ></script> --%>
<%-- 
<script type="text/javascript" src="<%=request.getContextPath()%>/js/calender/jquery-ui.min.js" ></script> --%>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-ui-timepicker-addon.js"></script>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-ui-sliderAccess.js"></script>  

<script>	

$(function() {
$('#strValidity').datetimepicker({
	timeFormat: 'HH:mm',
	stepHour: 1,
	stepMinute: 1,
	minDate: 0,
});
});

</script>
<script>
var dFormat;
dFormat = '<%=dateFormat%>';
	
function popUpCalendar(ctl,	ctl2, datestyle)
{
	//Get format from system parameter document.form[0].
	datestyle = dFormat;
	jsPopUpCalendar( ctl, ctl2, datestyle ); 

}
function validateCreate(){
	var attrObj = document.getElementById("attributeId");
	var attrValueObj = document.getElementById("attributeValue");
	if (isNull(attrObj.value)){
		alert("Please Enter Attribute");
		document.forms[0].attributeId.focus();
		return;	
	}
	if (isNull(attrValueObj.value)){
		alert("Please Enter Attribute Value");
		document.forms[0].attributeValue.focus();
		return;	
	}
	document.forms[0].submit();
 }
function popup(mylink, windowname){
	if (! window.focus)return true;
			var href;
	if (typeof(mylink) == 'string')
			href=mylink;
	else
			href=mylink.href;
			
	window.open(href, windowname, 'width=700,height=500,left=150,top=100,scrollbars=yes');
	return false;
}

function setColumnsOnAttrTextFields(){
	var attrVal = document.getElementById("attributeId").value;
	retriveRadiusDictionaryAttributes(attrVal,"attributeId");
}

$(document).ready(function() {
	var chkBoxVal='<%=statusVal%>';
	if(chkBoxVal == "Inactive"){
		$('#activeStatus').val("0");
	}else{
		$('#activeStatus').val("1");
	}
	
	$(function() {
	    $('.noAutoComplete').attr('autocomplete', 'off');
	});
});

setTitle('<bean:message bundle="radiusResources" key="radius.bwlist.title"/>');
</script>
<html:form action="/createBWList">
	<input type="hidden" name="checkAction" id="checkAction" />
	<html:hidden name="createBWListForm" styleId="inputMode" property="inputMode" value="create" />
	<html:hidden name="createBWListForm" styleId="activeStatus" property="activeStatus" />

	<table cellpadding="0" cellspacing="0" border="0" width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>">
		<tr>
			<td width="<%=ConfigConstant.PAGELEFTSPACE%>">&nbsp;</td>
			<td>
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="box">
							<table cellpadding="0" cellspacing="0" border="0" width="100%">
								<tr>
									<td class="table-header">
										<bean:message bundle="radiusResources" key="radius.bwlist.create" />
									</td>
								</tr>
								<tr>
									<td class="small-gap" colspan="3">&nbsp;</td>
								</tr>
								<tr>
									<td class="btns-td" valign="middle" colspan="3">
										<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
											<tr>
												<td colspan="3">&nbsp;</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top" width="18%">
													<bean:message bundle="radiusResources"
														key="radius.bwlist.attribute" /> 
															<ec:elitehelp headerBundle="radiusResources" 
																text="radius.bwlist.attribute" header="radius.bwlist.attribute"/>
												</td>
												<td align="left" class="labeltext" valign="top" width="250px" colspan="1">
													<%-- 	<html:text styleId="paramName" property="attributeId" size="35" maxlength="60" style="width: 250;" />  --%>
													<input type="text" tabindex="1" name="attributeId" id="attributeId" 
														size="30" autocomplete="off" onkeyup="setColumnsOnAttrTextFields();" 
															style="width: 250px" value='<bean:write name="createBWListForm" property='attributeId'/>' />
												</td>
												
												
												
												<td align="left" class="labeltext" valign="top" width="">
													<font color="#FF0000"> *</font>&nbsp;&nbsp;&nbsp;&nbsp;
												</td>
											</tr>

											<tr>
												<td align="left" class="labeltext" valign="top" width="18%">
													<bean:message bundle="radiusResources"
													key="radius.bwlist.attributevalue" /> 
													<ec:elitehelp headerBundle="radiusResources" 
													text="radius.bwlist.attributevalue" header="radius.bwlist.attributevalue"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="1">
													<html:textarea styleId="attributeValue" property="attributeValue" tabindex="2" rows="3" cols="30" style="width: 250;" />
												</td>
												<td align="left" class="labeltext" valign="top">
													<font color="#FF0000"> *</font>
												</td>

											</tr>
											<tr>
												<td align="left" class="labeltext"  width="18%">
													<bean:message bundle="radiusResources"
													key="radius.bwlist.validity" /> 
													<ec:elitehelp headerBundle="radiusResources" 
													text="radius.bwlist.validity" header="radius.bwlist.validity"/>
												</td>
												<td align="left"  colspan="2">
													<table>
														<tr>
															<td>
																<html:text styleId="strValidity" property="strValidity" styleClass="noAutoComplete" size="10" tabindex="3" maxlength="15" style="width: 190px;" value="<%=formatedDate %>"/>
															</td>
															<td>
																<%--  <a href="javascript:void(0)" onclick="popUpCalendar(this, document.forms[0].strValidity)">
																 <img src="<%=basePath%>/images/calendar.jpg" border="0" tabindex="4" style="width: 17px;margin: auto;"></a> --%>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align="left" class="labeltext" valign="top" width="18%">
													<bean:message bundle="radiusResources"
													key="radius.bwlist.type" /> 
													<ec:elitehelp headerBundle="radiusResources" 
													text="radius.bwlist.type" header="radius.bwlist.type"/>
												</td>
												<td align="left" class="labeltext" valign="top" colspan="1">
													<html:select property="typeName" styleId="typeName" style="width: 190px;" name="createBWListForm" tabindex="4">
														<html:option value="B">Blacklist</html:option>
														<html:option value="W">Whitelist</html:option>
													</html:select>
												</td>
												<td align="left" class="labeltext" valign="top">
													
												</td>

											</tr>

											<tr>
												<td class="btns-td" valign="middle">&nbsp;</td>
												<td class="btns-td" valign="middle" colspan="2">
													<input tabindex="5" type="button" name="c_btnCreate" onclick="validateCreate()" id="c_btnCreate2" value=" Create " class="light-btn"> 
													<input tabindex="6" type="reset" name="c_btnDeletePolicy" onclick="javascript:location.href='<%=basePath%>/initSearchBWList.do'" value="Cancel" class="light-btn">
												</td>
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

