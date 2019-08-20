<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="nested" %>
<%@ taglib uri="/WEB-INF/displaytag.tld" prefix="display" %>
<%@page import="com.elitecore.ssp.subscriber.SubscriberProfile"%>


<script type="text/javascript">
function  checkAllCB(){	
	var elements = document.getElementsByName("selectall");	 		 	
	for (var i = 0; i < elements.length;i++){		 	
		elements[i].checked = document.getElementById("mainCb").checked;		 	
	} 
}  
function addChild(){
	$("#action").val("addmember");
	$("#displayTableForm").submit();
	
}
</script>

<%@include file="/jsp/InitSearchSubscriberProfile.jsp" %>
<html:form action="/searchSubscriberProfiler" method="post" styleId="displayTableForm">
<html:hidden property="action" styleId="action" value="addmember"/> 
<table width="97%" cellpadding="0" cellspacing="0" border="0" class="main-table" >
		<tr class="table-org" height="30px">   		
   			<td class="table-org-column" align="left" colspan="3"><bean:message key="enterprise.subscriberslist"/></td>    		
		</tr>
		<tr>
		 	<td>
		 		<display:table name="subscriberProfileDataList" id="childAccountDataTable" requestURI="/searchSubscriberProfiler.do?action=search&method=post"  pagesize="10"  style="width:100%">
    				<display:column headerClass="paging-table-column" style="text-align:center" title='<input type="checkbox" onclick="checkAllCB()" id="mainCb" name="mainCb" style="width:auto;"/>'><input type="checkbox" id="cb" value='<bean:write name="childAccountDataTable" property="subscriberID" />' name="selectall" /></display:column>
    				<display:column property="userName"  class="left-Align"  headerClass="table-org-column"/>
    				<display:column property="company"  class="left-Align" headerClass="table-org-column" />
    				<display:column property="department" class="left-Align" headerClass="table-org-column"/>
    				<display:column property="zone" class="left-Align" headerClass="table-org-column"/>
    				<display:setProperty name="css.tr.even" value="table-gray"></display:setProperty>
    				<display:setProperty name="css.tr.odd" value="table-white"></display:setProperty>
    				<display:setProperty name="paging.banner.placement" value="bottom"></display:setProperty>
    				<display:setProperty name="basic.empty.showtable" value="true"></display:setProperty>
    				<display:setProperty name="paging.banner.no_items_found" value='<tr><td colspan="{0}" class="table-gray" align="center"></td></tr>'></display:setProperty>
    				<display:setProperty name="basic.msg.empty_list_row" value='<tr><td colspan="{0}" class="table-gray" align="center">No Records Found.</td></tr>'></display:setProperty>    				
    				<display:setProperty name="sort.amount" value="list"></display:setProperty>
    			</display:table>		 		
		 	</td>
		</tr>
		<tr>		
			<td align="center">
				<input type="button" onclick="addChild();" value="Add Member" class="orange-btn"/>				
			</td>
		</tr>
</table>
</html:form>
 