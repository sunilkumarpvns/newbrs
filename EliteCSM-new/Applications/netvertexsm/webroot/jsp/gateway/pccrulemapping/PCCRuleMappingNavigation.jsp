<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr> 
		<td colspan="2" valign="top"> 

<%
	RuleMappingData ruleMappingData = (RuleMappingData) request.getAttribute("ruleMappingData");
	// view information url
	String viewRuleMapping = basePath+"/pccRuleManagement.do?method=view&ruleMappingId="+ruleMappingData.getRuleMappingId();
	String editRuleMapping = basePath+"/pccRuleManagement.do?method=initUpdate&ruleMappingId="+ruleMappingData.getRuleMappingId();
%>                                                    

	<table border="0" width="100%" cellspacing="0" cellpadding="0">
		<tr id=header1>
			<td class="subLinksHeader" width="87%">
				<bean:message key="general.action" />
			</td>
			<td class="subLinksHeader" width="13%">
				<a href="javascript:void(0)" onClick="swapImages()"><img src="<%=basePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
			</td>
		</tr>
		<tr valign="top">
			<td colspan="2" id="backgr1">
				<div>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="subLinks">
								<a href="<%=editRuleMapping%>">Update PCC Rule Mapping</a>
							</td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
		<tr id=header1>
			<td class="subLinksHeader" width="87%">
				<bean:message key="general.view" />
			</td>
			<td class="subLinksHeader" width="13%">
				<a href="javascript:void(0)" onClick="swapImages()"><img src="<%=basePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow"></a>
			</td>
		</tr>
		<tr valign="top">
			<td colspan="2" id="backgr1">
				<div>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="subLinks">
								<a href="<%=viewRuleMapping%>">View PCC Rule Mapping</a>
							</td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>
		</td>
  </tr> 

</table>