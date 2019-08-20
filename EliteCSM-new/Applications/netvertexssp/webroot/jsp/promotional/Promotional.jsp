<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@page import="com.elitecore.netvertexsm.ws.xsd.PromotionalData"%>
<%@page import="com.elitecore.ssp.web.promotional.forms.PromotionalForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%
PromotionalForm promotionalForm  = (PromotionalForm)request.getAttribute(RequestAttributeKeyConstant.PROMOTIONAL_OFFER_FORM);
PromotionalData promotionalData =  promotionalForm.getPromotionalData();
PromotionalData[] activatedPromotionalData = promotionalForm.getActivatedPromotionalData();
%>
<script>
function unsubscribe(addOnId){
	var confirmed = window.confirm("Are you sure want to unsubscribe the Promotion?");
	if(confirmed){
		document.forms[0].addOnPackageId.value=addOnId;
		document.forms[0].submit();
	}
}
function subscribe(){
var confirmed = window.confirm("Are you sure want to subscribe the Promotion?");
	if(confirmed){
		document.forms[1].submit();
	}
}

</script>
<table width="100%" cellpadding="0">
   <tr>
   		<td  height="20" class="titlebold" width="100%">
   		  &nbsp;&nbsp;&nbsp;&nbsp; Current Active Promotions
   		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<tr>
		<td align="left" valign="top" >
		<html:form action="/unsubscribePromotion" method="post">
		<html:hidden property="addOnPackageId" value="" styleId="addOnPackageId"/>
		<table width="80%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td width="10%" align="left" class="tableheader"><bean:message key="general.serialno"/></td>
				<td width="30%" align="left" class="tableheader"><bean:message key="general.name"/></td>
				<td width="20%" align="left" class="tableheader"><bean:message key="promotional.rates"/></td>
				<td width="10%" align="left" class="tableheader"><bean:message key="promotional.unsubscribe"/></td>
			</tr>
			<%if (activatedPromotionalData!=null){ 
			int index =1;
			%>
			<logic:iterate id="promotionalBean" name="promotionalForm"  property="activatedPromotionalData" type="PromotionalData">
			
			<tr>
				<td align="left" class="tblfirstcol"><%=index++%></td>
				<td align="left" class="tblrows"><bean:write name="promotionalBean" property="name"/></td>
				<td align="left" class="tblrows"><bean:write name="promotionalBean" property="price"/></td>
				<td align="left" class="tblrows"><input type="button" value=" Unsubscribe " onclick="unsubscribe('<bean:write name="promotionalBean" property="addOnPackageId"/>')"/></td>
			</tr>
			</logic:iterate>
			<%}else{ %>
			<tr>
				<td colspan="5" align="center" class="tblfirstcol">No Records Found.</td>
			</tr>
			<%} %>
		</table>
		</html:form>	
		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	 <tr>
   		<td  height="20" class="titlebold" width="100%">
   		  &nbsp;&nbsp;&nbsp;&nbsp; Promotional Offer
   		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<tr>
		<td align="left" valign="top" >
		
			
		<html:form action="/subscribePromotion" method="post">
		<html:hidden property="addOnPackageId" value="<%=Long.toString(promotionalData.getAddOnPackageId())%>"/>
		<table width="100%">
			
			<tr>
				<td width="25%"   align="left" class="blackboldtext">Promotional Name:</td>
				<td align="left" class="blacktext"><%=promotionalData!=null?promotionalData.getName()!=null?promotionalData.getName():"-":"-"%></td>
			</tr>
			
			<tr>
				<td align="left" class="blackboldtext">Price:</td>
				<td align="left" class="blacktext"><%=promotionalData!=null?promotionalData.getPrice()!=null?promotionalData.getPrice():"-":"-"%></td>
			</tr>
			<tr>
				<td align="left" class="blackboldtext">Offer End Date:</td>
				<td align="left" class="blacktext"><%=promotionalData!=null?promotionalData.getOfferEndDate()!=null?promotionalData.getOfferEndDate():"-":"-"%></td>
			</tr>
			<tr>
				<td align="left" class="blackboldtext">&nbsp;</td>
			</tr>
			
			<tr>
				<td align="left" class="blackboldtext"></td>
				<td align="left" class="blacktext"><html:button property="c_btnOK" value="  Subscribe " styleClass="sspbutton" onclick="subscribe()"/>	</td>
			</tr>
		</table>
		</html:form>
	
		</td>
	</tr>
	
</table>

