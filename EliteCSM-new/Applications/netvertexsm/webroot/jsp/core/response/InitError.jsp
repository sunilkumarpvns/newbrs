<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<% String message = ((String) session.getAttribute("errorDetails")); %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td width="100%" background="<%=request.getContextPath()%>/images/popup-btn-bkgd.jpg" >&nbsp;</td>
	</tr>
	<tr>
		<td align="left" class="small-gap">&nbsp;</td>
	</tr>
	<tr>
		<td width="100%" class="tblheader-bold" valign="bottom" style="font-size: 20px;">&nbsp;Initialization Error</td>
	</tr>
	<tr>
		<td align="left" >&nbsp;</td>
	</tr>	
	<tr>
		<td align="left" class="small-gap" style="font-size: 17px;">&nbsp;&nbsp;<%=message%> </td>
	</tr>
	<tr>
		<td align="left" >&nbsp;</td>
	</tr>
	<tr>
		<td rowspan="2" bgcolor="#00477F"  class="small-gap">&nbsp;</td>
	</tr>
</table>
<%@ include file="/jsp/core/includes/common/Footer.jsp"%>

