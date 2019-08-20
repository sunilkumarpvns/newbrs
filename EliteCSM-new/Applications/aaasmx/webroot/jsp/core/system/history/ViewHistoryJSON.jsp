<%@page import="com.elitecore.elitesm.web.history.HistoryData" %>
<html>
<head>
<%
	int counter=0; 
	String name=request.getParameter("name");
	String strSystemAuditId=request.getParameter("systemAuditId");
	if(strSystemAuditId != null){
		strSystemAuditId=strSystemAuditId.trim();
	}
%>
<meta charset="utf-8" />
<script src="<%=request.getContextPath()%>/js/history/jquery-2.0.3.min.js"></script>
<link rel="stylesheet" 	href="<%=request.getContextPath()%>/css/history/jquery-ui.css" />
<script src="<%=request.getContextPath()%>/js/history/jquery-ui.js"></script>
<script src="<%=request.getContextPath()%>/js/history/treeTable.js"></script>
<style>
.new{
background-color: red;
}
</style>
<script>
$("table.jsTT tr[data-tt-level='2']").wrapAll( '<div class="new" />');
</script>
<script type="text/javascript">
		com_github_culmat_jsTreeTable.register(this);
</script>

<script>

function enrich(node) {
	var desc = 0
	var childCount = 0
	if (node['children']) {
		$.each(node['children'], function(i, child) {
			desc += child['descendants'] + 1
		})
		childCount = node['children'].length
	}
	node['descendants'] = desc
	node['childCount'] = childCount
}

function renderItem(tr, item){
	tr.append($('&lt;td&gt;' + item.name.toUpperCase() + '&lt;/td&gt;'))
	tr.append($('&lt;td colspan="5"&gt;&lt;a " href="http://en.wikipedia.org/wiki/'+item.geb+'" target="inWikipedia" &gt;' + item.geb + '&lt;/a&gt;&lt;/td&gt;'))
}
$(document).ready(function(){
	$('html, body').animate({
        scrollTop: $("#currentAuditData").offset().top
    }, 500); 
});
</script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/history/simple.css">
</head>
<body>
<table width="100%" cellspacing="0" cellpadding="0" border="0">
		<tr>
			<td class="tblheader-bold" colspan="3">
	  			  View History
			</td>
		</tr>
		<%int index=0; %>
		<logic:notEmpty name="lstDatabaseDSHistoryDatas">
		<logic:iterate id="historyData" name="lstDatabaseDSHistoryDatas"  type="com.elitecore.elitesm.web.history.DatabaseHistoryData">
		<tr>
			<td class="td-padding">
				<div class="history-contents">
				<table width="100%" cellspacing="0" cellpadding="0" border="0">
						<logic:iterate id="historyInfoData" property="historyData" name="historyData"  type="com.elitecore.elitesm.web.history.HistoryData">
							<%if(strSystemAuditId != null){ %>
								<logic:equal name="historyData" property="systemAuditId" value="<%=strSystemAuditId%>">
								<%String json=historyInfoData.getHistory();%>
								<tr>
									<td class="labeltext" style="vertical-align: top;padding-top: 10px;background-color: #F2F2F2;">
										&nbsp;<img alt="user" src="<%=basePath%>/images/useravatar.png" height="16" width="18"/>&nbsp;
										<font class="userclass" style="text-transform: capitalize;vertical-align: top;">
											<bean:write name="historyData" property="userName" />
										</font> 
										<font style="vertical-align: top;">made changes from </font>
										<font class="userclass" style="text-transform: capitalize;vertical-align: top;">
											<bean:write name="historyData" property="ipAddress" /> 
										</font>
										<font style="vertical-align: top;"> Address -
											<bean:write name="historyData" property="lastupdatetime" />
										</font>
									</td>
								</tr>
								 <tr id="currentAuditData">
									<td class="labeltext border-class" style="background-color:  #F2F2F2;">
										<div id="container<%=index%>"></div>
										<code>
											<script>
												var idofContainer="#container"+<%=index%>;
												var options = {
														mountPoint : $(idofContainer),
														idAttr : 'Field',
														renderedAttr : {
															Field : 'Field',
															OldValue : 'OldValue',
															NewValue : 'NewValue'
														},
														childrenAttr:'values',
														replaceContent : true,
														tableAttributes : {
														},
														slider :false,
														initialExpandLevel : 10,
													}
												appendTreetable(makeTree(<%=json%>, 'Field'), options);
											</script>
										</code>
										<%index++; %>
									</td>
								</tr>
								</logic:equal>
								<logic:notEqual name="historyData" property="systemAuditId" value="<%=strSystemAuditId%>">
								<%String json=historyInfoData.getHistory();%>
								<tr>
									<td class="labeltext" style="vertical-align: top;padding-top: 10px;">
										&nbsp;<img alt="user" src="<%=basePath%>/images/useravatar.png" height="16" width="18"/>&nbsp;
										<font class="userclass" style="text-transform: capitalize;vertical-align: top;">
											<bean:write name="historyData" property="userName" />
										</font> 
										<font style="vertical-align: top;">made changes from </font>
										<font class="userclass" style="text-transform: capitalize;vertical-align: top;">
											<bean:write name="historyData" property="ipAddress" /> 
										</font>
										<font style="vertical-align: top;"> Address -
											<bean:write name="historyData" property="lastupdatetime" />
										</font>
									</td>
								</tr>
								 <tr>
									<td class="labeltext border-class">
										<div id="container<%=index%>"></div>
										<code>
											<script>
												var idofContainer="#container"+<%=index%>;
												var options = {
														mountPoint : $(idofContainer),
														idAttr : 'Field',
														renderedAttr : {
															Field : 'Field',
															OldValue : 'OldValue',
															NewValue : 'NewValue'
														},
														childrenAttr:'values',
														replaceContent : true,
														tableAttributes : {
														},
														slider :false,
														initialExpandLevel : 10,
													}
												appendTreetable(makeTree(<%=json%>, 'Field'), options);
											</script>
										</code>
										<%index++; %>
									</td>
								</tr>
								</logic:notEqual>
							<%}else{ %>
								<%String json=historyInfoData.getHistory();%>
								<tr>
									<td class="labeltext" style="vertical-align: top;padding-top: 10px;">
										&nbsp;<img alt="user" src="<%=basePath%>/images/useravatar.png" height="16" width="18"/>&nbsp;
										<font class="userclass" style="text-transform: capitalize;vertical-align: top;">
											<bean:write name="historyData" property="userName" />
										</font> 
										<font style="vertical-align: top;">made changes from </font>
										<font class="userclass" style="text-transform: capitalize;vertical-align: top;">
											<bean:write name="historyData" property="ipAddress" /> 
										</font>
										<font style="vertical-align: top;"> Address -
											<bean:write name="historyData" property="lastupdatetime" />
										</font>
									</td>
								</tr>
								<tr>
									<td class="labeltext border-class" >
										<div id="container<%=index%>"></div>
										<code>
											<script>
												var idofContainer="#container"+<%=index%>;
												var options = {
														mountPoint : $(idofContainer),
														idAttr : 'Field',
														renderedAttr : {
															Field : 'Field',
															OldValue : 'OldValue',
															NewValue : 'NewValue'
														},
														childrenAttr:'values',
														replaceContent : true,
														tableAttributes : {
														},
														slider :false,
														initialExpandLevel : 10,
													}
												appendTreetable(makeTree(<%=json%>, 'Field'), options);
											</script>
										</code>
										<%index++; %>
									</td>
								</tr>
							<%}%>
						</logic:iterate>
				</table>
				</div>
			</td>
		</tr>
		</logic:iterate>
		</logic:notEmpty>
		<logic:empty name="lstDatabaseDSHistoryDatas">
		<tr>
			<td colspan="2" align="center" class="labeltext no-history-details no-history-container" >No History Found</td>
		</tr>
		</logic:empty>
</table>
</body>
</html>