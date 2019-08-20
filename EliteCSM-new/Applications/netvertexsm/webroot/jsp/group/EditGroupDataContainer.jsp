<%@ include file="/jsp/core/includes/common/Header.jsp"%>

<script type="text/javascript">
$(document).ready(function(){
	setTitle('<bean:message  bundle="groupDataMgmtResources" key="group.title"/>');	
});
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  <tr>
  <td width="10">&nbsp;</td>
  <td valign="top" align="right" width="85%">
  <table width="100%" border="0" cellspacing="0" cellpadding="0" >				
			<tr>
				 <td valign="top" align="right" width="85%" class="box">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">	
						
						<tr>	
							<td>
								<%@ include file = "ViewGroupData.jsp" %> 
							</td>
						</tr>
						<tr>	
							<td>
								<%@ include file = "EditGroupData.jsp" %>
							</td>
						</tr> 
   					</table>
  				</td> 
				<td width="15%" class="grey-bkgd" valign="top">
					   <%@ include file = "GroupNavigation.jsp" %> 
				</td>
			</tr>
		</table>
		</td></tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

