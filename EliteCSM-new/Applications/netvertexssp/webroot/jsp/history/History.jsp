
<%@page import="com.elitecore.netvertexsm.ws.xsd.PromotionSubscriptionData"%>
<%@page import="com.elitecore.netvertexsm.ws.xsd.BodSubscriptionData"%>
<%@page import="com.elitecore.ssp.web.history.forms.SubscriptionHistoryForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested" %>
<%
  SubscriptionHistoryForm historyForm = (SubscriptionHistoryForm) request.getAttribute("historyForm");
  BodSubscriptionData[] bodSubscriptions = null;
  PromotionSubscriptionData[] promotionSubscriptions  = null;
  if(historyForm!=null){
	  bodSubscriptions = historyForm.getBodSubscriptions();
	  promotionSubscriptions = historyForm.getPromotionSubscriptions();
  }
  boolean bodHistory = false;
%>
<table width="100%" cellpadding="0">
	<%if(bodSubscriptions!=null && bodSubscriptions.length>0){ %>
   <tr>
   		<td  height="20" class="titlebold" width="100%">
   		  &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="history.bodsubscriptions"/>
   		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<tr>
		<td align="left" valign="top" >
		
		<table width="90%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td align="left" class="tableheader"><bean:message key="general.serialno"/></td>
				<td align="left" class="tableheader"><bean:message key="history.bod.package"/></td>
				<td align="left" class="tableheader"><bean:message key="history.bod.startime"/></td>
				<td align="left" class="tableheader"><bean:message key="history.bod.endtime"/></td>
			</tr>
			<%  int index=1; %>
			
			<logic:iterate id="bodSubscription" name="historyForm" property="bodSubscriptions" type="BodSubscriptionData">
				<tr>
					<td align="left" class="tblfirstcol"><%=index%></td>
					<td align="left" class="tblrows"><bean:write name="bodSubscription" property="subscriberPackage" /></td>
					<td align="left" class="tblrows"><bean:write name="bodSubscription" property="startTime" /></td>
					<td align="left" class="tblrows"><bean:write name="bodSubscription" property="endTime" /></td>
				</tr>
					<%index++; %>
			</logic:iterate>
			
			
		</table>	
		</td>
	</tr>
	
	<%
	bodHistory = true;
	} %>
	<%if (bodHistory) {%>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<%} %>
	<%if(promotionSubscriptions!=null && promotionSubscriptions.length>0){ %>
	 <tr>
   		<td  height="20" class="titlebold" width="100%">
   		&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="history.promotionubscriptions"/>
   		</td>
	</tr>
	<tr>
		<td>&nbsp;
		</td>
	</tr>
	<tr>
		<td align="left" valign="top" >
		
		<table width="90%" cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td align="left" class="tableheader"><bean:message key="general.serialno"/></td>
				<td align="left" class="tableheader"><bean:message key="history.prmoffer.name"/></td>
				<td align="left" class="tableheader"><bean:message key="history.prmoffer.startime"/></td>
				<td align="left" class="tableheader"><bean:message key="history.prmoffer.endtime"/></td>
			</tr>
			<%  int index=1; %>
	
			<logic:iterate id="promotionSubscription" name="historyForm" property="promotionSubscriptions" type="PromotionSubscriptionData">
			
				<tr>
					<td align="left" class="tblfirstcol"><%=index%></td>
					<td align="left" class="tblrows"><bean:write name="promotionSubscription" property="name" /></td>
					<td align="left" class="tblrows"><bean:write name="promotionSubscription" property="startTime" /></td>
					<td align="left" class="tblrows"><bean:write name="promotionSubscription" property="endTime" /></td>
				</tr>
				<%index++; %>
			</logic:iterate>
		</table>	
		</td>
	</tr>
	<%} %>
</table>

