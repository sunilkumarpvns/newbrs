<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@page import="com.elitecore.netvertexsm.ws.xsd.BodSubscriptionData"%>
<%@page import="com.elitecore.ssp.util.constants.BaseConstant"%>
<%@page import="com.elitecore.ssp.web.core.base.forms.BaseWebForm"%>
<%@page import="com.elitecore.netvertexsm.ws.xsd.BoDPackageData"%>
<%@page import="com.elitecore.ssp.web.bod.forms.BodForm"%>
<%@page import="java.util.Date"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%
  BodForm bodForm = (BodForm)request.getAttribute(RequestAttributeKeyConstant.BOD_FORM);
  BoDPackageData[] bodPackageData =  bodForm.getBodPackageData();
  BodSubscriptionData[] bodSubscriptionDatas = bodForm.getBodSubscriptionDatas();  
%>


<script>
function unsubscribe(bodId){
	var confirmed = window.confirm("Are you sure want to unsubscribe the BoD Service?");
	if(confirmed){
		document.forms[0].bodId.value=bodId;
		document.forms[0].submit();
	}
}
function validate(){
	if(isNull(document.forms[1].startTime.value)){
		alert("Please Enter Start Time.");
		document.forms[1].startTime.focus();
		return;
	}else if(isNull(document.forms[1].duration.value)){
		alert("Please Enter Duration in Hours.");
		document.forms[1].duration.focus();
		return;
	}else if(!IsNumeric(document.forms[1].duration.value)){
		alert("Duration should be numeric value.");
		document.forms[1].duration.focus();
		return;
	}
	var confirmed = window.confirm("Are you sure want to subscribe the service?");
	if(confirmed){
		document.forms[1].submit();
	}
}
</script>
<table width="100%" cellpadding="0">
   <tr>
   		<td  height="20" class="titlebold" width="100%">
   		  &nbsp;&nbsp;&nbsp;&nbsp; Bandwidth on Demand
   		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<tr>
		<td align="left" valign="top" >
				<html:form action="/unsubscribeBoD" method="post">
				<html:hidden property="bodId" value="" styleId="bodId"/>
				<%if(bodSubscriptionDatas!=null && bodSubscriptionDatas.length>0){ %>
				<table width="90%" cellpadding="0" cellspacing="0" border="0">
					<tr>
						<td colspan="5" align="left" class="titlebold">Active Subscriptions</td>
					</tr>
					<tr>
						<td width="5%"  align="left" class="tableheader"><bean:message key="general.serialno"/></td>
						<td width="24%"  align="left" class="tableheader"><bean:message key="bod.package"/></td>
						<td width="24%"  align="left" class="tableheader"><bean:message key="bod.startime"/></td>
						<td width="24%"  align="left" class="tableheader"><bean:message key="bod.endtime"/></td>
						<td width="24%"  align="left" class="tableheader"><bean:message key="bod.unsubscribe"/></td>
					</tr>
					<%int index=1; %>
					<logic:iterate id="bodSubscriptionBean" name="bodForm" property="bodSubscriptionDatas" type="BodSubscriptionData">
					<tr>
						<td align="left" class="tblfirstcol"><%=index%></td>
						<td align="left" class="tblrows"><bean:write name="bodSubscriptionBean" property="subscriberPackage" /></td>
						<td align="left" class="tblrows"><bean:write name="bodSubscriptionBean" property="startTime" /></td>
						<td align="left" class="tblrows"><bean:write name="bodSubscriptionBean" property="endTime" /></td>
						<td align="left" class="tblrows"><input type="button" value=" Unsubscribe " onclick="unsubscribe('<bean:write name="bodSubscriptionBean" property="bodId"/>')"/></td>
					</tr>
					</logic:iterate>
				</table>
				<%}%>
				</html:form>
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td align="left" valign="top" >
		<html:form action="/subscribeBod" method="post">
			<%if(bodPackageData!=null && bodPackageData.length>0){ %>
			<table width="90%" cellpadding="0" cellspacing="0" border="0">
				<tr>
						<td colspan="5" align="left" class="titlebold">BoD Services</td>
				</tr>
				<tr>
					<td align="center" class="tableheader" valign="top" width="5%"><bean:message key="general.select"/></td>
					<td width="5%" align="left" class="tableheader"><bean:message key="general.serialno"/> </td>
					<td width="30%" align="left" class="tableheader"><bean:message key="bod.services"/></td>
					<td width="30%" align="left" class="tableheader"><bean:message key="general.description"/></td>
					<td width="30%" align="left" class="tableheader"><bean:message key="bod.rates"/></td>
				</tr>
				<%int index=1; %>
				<logic:iterate id="bodPackageBean" name="bodForm" property="bodPackageData" type="BoDPackageData">
				<tr>
					<td align="center" class="tblfirstcol" valign="top">
						<%if (index==1){ %> 
						<input type="radio" name="bodPackageId" value="<%=Long.toString(bodPackageBean.getBodPackageId())%>" checked="checked"
						<%}else { %>
						<html:radio property="bodPackageId" value="<%=Long.toString(bodPackageBean.getBodPackageId())%>"/>
						<%} %>
					</td>
					<td align="left" class="tblrows"><%=index++%></td>
					<td align="left" class="tblrows"><bean:write name="bodPackageBean" property="name"/></td>
					<td align="left" class="tblrows"><bean:write name="bodPackageBean" property="description"/></td>
					<td align="left" class="tblrows"><bean:write name="bodPackageBean" property="priceRate"/></td>
				</tr>
				</logic:iterate>
				<tr>
					<td>&nbsp;
					</td>
				</tr>
				<tr>
					<td colspan="5">
						<table width="100%">
							<tr>
								<td  class="blackboldtext" width="20%"><bean:message key="bod.starttime"/>:</td>
								<td  width="20%"><html:text property="startTime"  styleId="startTime" value="<%=com.elitecore.ssp.util.constants.BaseConstant.DATETIME_FORMATTER.format(new Date())%>"/></td>
							</tr>
							<tr>
								<td  width="20%"></td>
								<td  width="20%"  class="blacktext">DD-MM-YYYY HH:MM</td>
								<td  width="20%"></td>
								<td  width="30%"  class="blacktext"></td>
							</tr>
							<tr>
							<td  class="blackboldtext" width="20%"><bean:message key="bod.duration"/>:</td>
								<td  width="30%"  class="blackboldtext"><html:text property="duration"  styleId="duration"/></td>
								<td  class="blackboldtext" width="20%"><bean:message key="bod.durationunit"/>:</td>
								<td  class="blackboldtext" width="20%">
								<html:select property="durationUnit" >
										<html:option value="HOUR">Hour</html:option>
										<html:option value="MINUTE">Minute</html:option>
								</html:select>
								</td>
							</tr>
						</table>
					</td>
				</tr>
					<tr>
						<td>&nbsp;
						</td>
					</tr>
					<tr>
					<td  align="center" colspan="5">
						<input type="button" name="Show"   onclick="validate()" value="   Subscribe   " class="sspbutton"  >
						</td>
					</tr>
				</table>
				<%} else { %>
				<table width="90%" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td align="center" class="tableheader" valign="top" width="5%"><bean:message key="general.select"/></td>
					<td width="5%" align="left" class="tableheader"><bean:message key="general.serialno"/> </td>
					<td width="30%" align="left" class="tableheader"><bean:message key="bod.services"/></td>
					<td width="30%" align="left" class="tableheader"><bean:message key="general.description"/></td>
					<td width="30%" align="left" class="tableheader"><bean:message key="bod.rates"/></td>
				</tr>
				<tr>
					<td align="center" class="tblfirstcol" colspan="5">No records found.</td>
				</tr>
				
				</table>
				
				<%} %>
			</html:form>
		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
</table>

