<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<%
    String localBasePath = request.getContextPath();
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr height=380px>
    <td width="10" class="small-gap">&nbsp;</td>	
    <td width="85%" valign="top" class="box" >
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
		  <td valign="top">
				<%@ include file="ViewNetServerInstance.jsp"%>
		  </td>
		</tr>
		<tr>
		  <td>&nbsp;</td>
		</tr>
		<tr>
		  <td valign="top">
				<%@ include file="ListNetServerConfigDetails.jsp"%>
		  </td>
		</tr>
      </table>
    </td>	
 	<td class="grey-bkgd" valign="top" width="15%">
			<%@  include file="NetServerInstanceNavigation.jsp"%>
	</td>
  </tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

