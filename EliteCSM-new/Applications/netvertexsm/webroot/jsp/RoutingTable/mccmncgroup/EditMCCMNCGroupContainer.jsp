<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<script type="text/javascript">
$(document).ready(function() {	
	setTitle('<bean:message  bundle="routingMgmtResources" key="mccmncgroup.title"/>');
});
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr> 
    <td width="10">&nbsp;</td>
    <td width="85%" valign="top">
		<%@ include file = "ViewMCCMNCGroup.jsp" %> 
	</td>
    <td width="15%" class="grey-bkgd" valign="top" rowspan="2">
      	<%@  include file = "MCCMNCGroupNavigation.jsp" %> 
    </td>
  </tr>
  <tr>
  	<td width="10">&nbsp;</td>
    <td width="85%" valign="top">
		<%@ include file = "EditMCCMNCGroup.jsp" %> 
	</td>
  </tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

