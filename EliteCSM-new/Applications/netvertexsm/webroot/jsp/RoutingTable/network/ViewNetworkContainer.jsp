<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<script type="text/javascript">
$(document).ready(function() {
	setTitle('<bean:message  bundle="routingMgmtResources" key="network.mgmt.title"/>');
});
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr> 
    <td width="10">&nbsp;</td>
    <td width="85%" valign="top" class="box">
		<%@ include file = "ViewNetwork.jsp" %> 
	</td>
    <td width="15%" class="grey-bkgd" valign="top">
      	<%@  include file = "NetworkNavigation.jsp" %> 
    </td>
  </tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

