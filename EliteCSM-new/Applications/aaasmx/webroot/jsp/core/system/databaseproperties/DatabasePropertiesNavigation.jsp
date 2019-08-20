<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <tr>
      <td colspan="2" valign="top">
         <%
            String navigationBasePath = request.getContextPath();
         	String updateDatabaseProperties = navigationBasePath + "/databaseProperties.do?method=getDatabasePropertiesData";
            String viewSystemParameterHistory = navigationBasePath + "/databaseProperties.do?method=viewHistory";
         %>
        <table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr id=header1>
					<td class="subLinksHeader" width="87%">
					<bean:message key="general.action" />
					</td>
					<td class="subLinksHeader" width="13%">
						<a href="javascript:void(0)" swapImages()">
							<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
						</a>
					</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=updateDatabaseProperties%>">
											<bean:message bundle="StaffResources" key="databaseproperties.updatedatabaseproperties" />
										</a>
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
					<a href="javascript:void(0)" swapImages()">
						<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
					</a>
				</td>
				</tr>
				<tr valign="top">
					<td colspan="2" id="backgr1">
						<div>
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
								<tr>
									<td class="subLinks">
										<a href="<%=viewSystemParameterHistory%>">
											<bean:message bundle="StaffResources" key="databaseproperties.history" />
										</a>
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