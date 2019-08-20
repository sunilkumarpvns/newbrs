<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<script type="text/javascript">
$(document).ready(function() {
	setTitle('<bean:message bundle="servicePolicyProperties" key="servicepolicy.pcrfpolicy"/>');
});
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr> 
    <td width="10">&nbsp;</td>
    <td width="85%" valign="top" class="box">
		<%@ include file = "ViewPCRFServicePolicy.jsp" %> 
	</td>
    <td class="grey-bkgd" valign="top" width="15%">
      <%@  include file = "PCRFServicePolicyNavigation.jsp" %> 
    </td>
  </tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

