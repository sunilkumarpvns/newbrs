<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<script type="text/javascript">
$(document).ready(function(){
	setTitle('<bean:message bundle="biTemplateResources" key="bitemplate.header" />');
});
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <%@ include file="/jsp/core/includes/common/HeaderBar.jsp" %>
  <tr> 
    <td width="10">&nbsp;</td>
    <td width="85%" valign="top" class="box">    	
		<table width="97%" border="0" align="right" cellspacing="0" cellpadding="0" height="15%">
			<tr><td colspan="4" class="tblheader-bold"  ><bean:message bundle="biTemplateResources" key="bitemplate.view" /></td></tr>
				<td valign="top" align="left">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" align="left">
					<tr>
						<td class="tblfirstcol" width="20%" height="20%"><bean:message bundle="biTemplateResources" key="bitemplate.name" /></td>
						<td class="tblcol" width="40%" height="20%"><bean:write name="biTemplateForm" property="name" />&nbsp;</td>
					</tr>
					<tr>
						<td class="tblfirstcol" width="20%" height="20%"><bean:message bundle="biTemplateResources" key="bitemplate.key" /></td>
						<td class="tblcol" width="40%" height="20%"><bean:write name="biTemplateForm" property="bikey" />&nbsp;</td>
					</tr>
					<tr><td width="10" class="small-gap">&nbsp;</td><td class="small-gap" colspan="2">&nbsp;</td></tr> 
				</table>
				</td>
			</tr>
			<tr>
			  	 
			    <td colspan="4" valign="top">
					<%@ include file = "UploadCSVFile.jsp" %> 
				</td>
			</tr>
		</table>
	</td>
    <td width="15%" class="grey-bkgd" valign="top" rowspan="2">
      	<%@  include file = "BITemplateNavigation.jsp" %> 
    </td>
  </tr>
   <%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table>

