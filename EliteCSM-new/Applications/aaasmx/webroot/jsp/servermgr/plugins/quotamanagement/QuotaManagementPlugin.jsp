<link rel="stylesheet" href="<%=request.getContextPath()%>/css/plugin/plugin-button.css" />
<link rel="stylesheet" href="<%=request.getContextPath()%>/font/fontawesome/fontawesome_css/font-awesome.min.css" />
<table width="100%" cellspacing="0" cellpadding="0" border="0">
	<tr>
		<td>
			<!--  <div class="plugin-box"> -->
                <table width="100%" cellspacing="0" cellpadding="0" border="0">
                	<tr>
                		<td>
							<!-- <div class="table-header plugin-table-header-css captiontext" style="background-color: #D9E6F6;">
								<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.title" />
							</div> -->
							<div class="captiontext"  style="padding: 5px;">
								<table width="100%" cellspacing="0" cellpadding="0" border="0">
									<tr>
										<td style="padding:5px;padding-left: 10px;">
											<input type="button" value="Add Policy" class="light-btn" onclick="addQuotaMgtPlugin('plugin-mapping-table','plugin-mapping-table-template');" />
										</td>
									</tr>
								</table>
								<table width="100%" cellspacing="0" cellpadding="0" border="0" id="plugin-mapping-table">
									<tbody class="parent sortableClass ui-sortable">
									</tbody>
								</table>
							</div>
						</td>
                	</tr>
                 </table>
              <!-- </div> --> <!-- /.plugin-box -->
		 </td>
	</tr>
	<tr>
		<td align="left" style="padding-left: 350px;">
			<input type="button" value="Create" class="light-btn" onclick="validatePlugin();"/> 
			<input type="button" value="Cancel" class="light-btn" onclick="javascript:location.href='<%=basePath%>/initSearchPlugin.do?'" />
		</td>
	</tr>
	<tr>
		<td>
			&nbsp;
		</td>
	</tr>
</table>

<!-- External include files list -->
<%@include file="/jsp/servermgr/plugins/quotamanagement/QuotaMgtPlugin.jsp" %>
