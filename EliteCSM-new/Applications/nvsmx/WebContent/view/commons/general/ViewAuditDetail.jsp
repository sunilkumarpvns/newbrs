
<%@page import="com.elitecore.commons.base.Strings"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@ page import="com.elitecore.corenetvertex.sm.audit.AuditData" %>
<%@ page import="com.elitecore.commons.base.Strings" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/history/simple.css" />
<script src="${pageContext.request.contextPath}/js/history/treeTable.js"></script>
<script type="text/javascript">
	com_github_culmat_jsTreeTable.register(this);
</script>
<%
Attributes.count++;
AuditData auditData = (AuditData)request.getAttribute("auditData");


String pageId = new String();
if(request.getAttribute("pageId") != null){
	pageId = (String)request.getAttribute("pageId");
}else{
	pageId = auditData.getResourceId();
}
String difference = null;
if(auditData.getDifference()!= null){
	 difference = new String(auditData.getDifference());
	 if(auditData.getHierarchy() != null){
		 String splitedHierarchyString[] = auditData.getHierarchy().split("<br>");
		 String resourceId = auditData.getResourceId();
		 String actualResourceId = auditData.getActualResourceId();
		 int i;
		 if(Strings.isNullOrBlank(actualResourceId) == false && actualResourceId.equals(resourceId) == false){
			for(i = 0 ; i < splitedHierarchyString.length ; i=i+2){
				if(pageId.equals(splitedHierarchyString[i])){
					i++;
					break;
				}
			}
		 	for(int j=splitedHierarchyString.length-1;j>i;j=j-2){
		 		difference = "[{\"Field\":\""+splitedHierarchyString[j]+"\",\"NewValue\":\"\",\"OldValue\":\"\",\"values\":"+difference+"}]";
		 	}
		 }
	 }
%>
	<table class="table table-history" style="border: 0px; border-spacing: 0 !important; margin-bottom: 0px;" >
		<tr id="currentAuditData" style="background: #ecf1f8">
			<td class="labeltext border-class" style="width: 100%">
				<div id="container<%=Attributes.count %>"></div>
				<script>
					var idofContainer="#container"+<%=Attributes.count%>;
					var options = {
						mountPoint : $(idofContainer),
						idAttr : 'Field',
						renderedAttr : {
						Field : 'Field',
							OldValue : 'OldValue',
							NewValue : 'NewValue'
						},
						childrenAttr : 'values',
						replaceContent : true,
						tableAttributes : {},
						slider : false,
						initialExpandLevel : 10,
					};
					appendTreetable(makeTree(<%=difference%>, 'Field'), options);
				</script> 
			</td>
		</tr>
	</table>
<%}%>