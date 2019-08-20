<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%
	String basePath = request.getContextPath();
    DiameterPeerProfileData diameterPeerProfileData = (DiameterPeerProfileData) request.getAttribute("diameterPeerProfileData");
%>

<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>

<script>
setTitle('<bean:message bundle="diameterResources" key="diameterpeerprofile.diameterpeerprofile"/>');
</script>

<table width="<%=ConfigManager.get(ConfigConstant.PAGE_WIDTH) %>" border="0" cellspacing="0" cellpadding="0">
  <tr>
		<td width="<%=ConfigConstant.PAGELEFTSPACE%>">
  			&nbsp;
  		</td>
		<td>
   			<table cellpadding="0" cellspacing="0" border="0" width="100%">
    			<tr>
    				<td  cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
    				<table width="100%" border="0" cellspacing="0" cellpadding="0">				
					<tr>
						<td valign="top" align="right">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">	
							<tr>	
								<td>
									<%@include file="/jsp/diameter/peerprofiles/ViewDiameterPeerProfile.jsp"%>
				  				</td>
							</tr>
							<tr>
								<td>
									<%@ include file="/jsp/core/system/history/ViewHistoryJSON.jsp"%>
								</td>
							</tr>
    					</table>
    					</td>	
						<td width="168" class="grey-bkgd" valign="top">
							<%@ include file="/jsp/diameter/peerprofiles/DiameterPeerProfileNavigation.jsp" %>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<%@ include file="/jsp/core/includes/common/Footer.jsp" %>
	</table>
</td>
</tr>
</table>		  


