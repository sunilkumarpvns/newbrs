<%@page import="com.elitecore.ssp.web.bod.forms.BodForm"%>
<%@page import="com.elitecore.ssp.util.constants.RequestAttributeKeyConstant"%>
<%@page import="com.elitecore.ssp.util.constants.SessionAttributeKeyConstant"%>
<%@page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>
<%@page import="com.elitecore.ssp.web.bod.forms.BodForm"%>
<%@page import="com.elitecore.ssp.util.EliteUtility"%>
<%@ page import="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDPackage"%>
<%@ page import="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<head>
   <title><bean:message key="promotional.info"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>

<%	
	SubscriberProfile childObj=(SubscriberProfile)request.getSession().getAttribute(SessionAttributeKeyConstant.CHILD_OBJECT);
	BodForm bodForm = (BodForm) request.getAttribute(RequestAttributeKeyConstant.BOD_FORM);	
%>

<script type="text/javascript">
function approveBodRequest(bodPackageId){
	 
	var msg = '<bean:message key="enterprise.bod.approve.confirm.message"/>';
	var agree = confirm(msg);
	if(agree){
		document.getElementById("bodPackageId").value=bodPackageId;
		$("#pendingBodReqForm").submit();
	}	    		   			
}

function rejectBodRequest(bodPackageId){
    var rejectReason = prompt("Reject Reason:","");    
    if(rejectReason!=null){
	 	document.getElementById("bodPackageId").value=bodPackageId;		
		document.getElementById("pendingBodReqForm").rejectReason.value=rejectReason;
		$("#pendingBodReqForm").attr("action",'<%=request.getContextPath()%>/rejectBodSubReq.do');
		$("#pendingBodReqForm").submit();
    }
}

</script>


<div class="border" >
<div>
<html:form action="/subscribeBod" method="post" styleId="pendingBodReqForm">
<jsp:useBean id="dateValue" class="java.util.Date" />
<table width="97%" cellpadding="0" cellspacing="0" border="0" class="main-table" >	
	<tr>   		
   			<td colspan="2" >&nbsp;</td>   		
	</tr>
	<tr class="table-org" >   		
   		<td class="table-org-column" colspan="2"><bean:message key="promotional.info"/></td>    		
	</tr>
 	<% if( bodForm !=null && bodForm.getPendingBodSubReqData()!=null ){%>
 	<bean:define id="pendingBodRequestBean" name="bodForm" property="pendingBodSubReqData" scope="request" type="com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData"/>
 	
 		<html:hidden property="bodPackageId" styleId="bodPackageId"/>
		<html:hidden property="selectedLink" value="serviceactivation"/>
		<html:hidden property="selectedPortal" styleId="selectedPortal" value="enterprise"/>
		<html:hidden property="rejectReason" value=""/>
 	    
 	    <input type="hidden" name="bodPackageID" value='<bean:write name="pendingBodRequestBean" property="bodPackageID"/>'/>
 		<input type="hidden" name="bodPkgSubReqId" value='<bean:write name="pendingBodRequestBean" property="bodSubscriptionID"/>'/>
 		
 		<jsp:setProperty name="dateValue" property="time" value="${pendingBodRequestBean.bodStartTime}" />		
 		<input type="hidden" name="startTime" value='<fmt:formatDate value="${dateValue}" pattern="dd-MM-yyyy HH:mm:ss" />'/> 		
 		
		<jsp:setProperty name="dateValue" property="time" value="${pendingBodRequestBean.bodStartTime}" />		
 		<input type="hidden" name="endTime" value='<fmt:formatDate value="${dateValue}" pattern="dd-MM-yyyy HH:mm:ss" />'/>
 		
 		<input type="hidden" name="subscriberIdentity" value='<%=request.getParameter("subscriberID")%>'/>
		<tr class="table-gray">
			<td class="table-column" align="left" width="200px"><bean:message key="parentalcontrol.prmoffer.childname"/></td>
			<td class="table-column" align="left"  >
			 	<%=request.getParameter("subscriberID")%>
			</td>		
		</tr>	
		<tr class="table-white">
			<td class="table-column" align="left" width="200px"><bean:message key="parentalcontrol.bod.name"/></td>
			<td class="table-column" align="left" >
				<bean:write name="pendingBodRequestBean" property="bodPackageName" /> 
			</td>		
		</tr>
		<tr class="table-white">
			<td class="table-column" align="left"><bean:message key="parentalcontrol.bod.active.starttime"/></td>
			<td class="table-column" align="left" >
			<%-- <%
					java.sql.Timestamp startTimeTS = new java.sql.Timestamp(pendingBodRequestBean.getBodStartTime());
					out.println(""+startTimeTS);
			%>	 --%>			
			<jsp:setProperty name="dateValue" property="time" value="${pendingBodRequestBean.bodStartTime}" />
			<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />									
			
			<%--  <bean:write name="pendingBodRequestBean" property="bodStartTime" /> --%>
			</td>		
		</tr>				
		<tr class="table-gray">
			<td class="table-column" align="left"><bean:message key="parentalcontrol.bod.active.endtime"/></td>
			<td class="table-column" align="left" >
			<%-- <%
				java.sql.Timestamp endTimeTS = new java.sql.Timestamp(pendingBodRequestBean.getBodEndTime());
				out.println(""+endTimeTS);
			%>	 --%>								
			
			<jsp:setProperty name="dateValue" property="time" value="${pendingBodRequestBean.bodEndTime}" />
			<fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" />									
			
			<%-- <bean:write name="pendingBodRequestBean" property="bodEndTime" /> --%>	 
			</td>												 
		</tr>
		<tr class="table-grey">   		
	   			<td class="table-column" colspan="2" >&nbsp; </td>   		
		</tr>
		<tr>
			<td valign="center" class="table-column" style="text-align: center;" align="center" colspan="5">
				<input type="button" name="Approve" class="orange-btn" value="  Approve  " onclick="approveBodRequest('<bean:write name="pendingBodRequestBean" property="bodPackageID" />')"/>
				<input type="button" name="Reject" class="orange-btn" value="  Reject  " onclick="rejectBodRequest('<bean:write name="pendingBodRequestBean" property="bodPackageID" />')"/>
			</td>
		</tr>
	 <% }else {%>
		<tr class="table-white">
			<td align="center" class="tblrows" colspan="4"><bean:message key="general.norecordsfound"/></td>				
		</tr>
	 <% }%>
</table>
</html:form>
</div>	
</div>