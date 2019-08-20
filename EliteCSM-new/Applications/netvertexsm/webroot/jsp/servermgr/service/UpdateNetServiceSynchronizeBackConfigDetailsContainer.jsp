<%@ include file="/jsp/core/includes/common/Header.jsp"%>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr height=380px>
    <td width="10" class="small-gap">&nbsp;</td>	
    <td  width="85%" valign="top" class="box" >
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
		  <td valign="top">
			<%@ include file="ViewNetServiceInstance.jsp"%>
		  </td>
		</tr>
		<tr>
		  <td valign="top">
			<%@ include file="UpdateNetServiceSynchronizeBackConfigDetails.jsp"%>
		  </td>
		</tr>
      </table>
    </td>	
	<td width="15%" class="grey-bkgd" valign="top">
		<%@  include file="NetServiceInstanceNavigation.jsp"%>
	</td>
  </tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>
