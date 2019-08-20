<!--  Plugin Mapping Template -->
<table id="plugin-pre-mapping-table-template" style="display: none;" class="pre-mapping-table-class">
<tr class="plugin-table-tr">
	<td style="padding: 10px;">
		<table width="100%" cellspacing="0" cellpadding="0" border="0" class="universal-plugin-table">
			<tr>
				<td class="box">
					<div class="universal_pre_plugin_div">
						<div class="table-header plugin-policy-background" style="font-weight: bold;cursor: pointer;background-color: #E2E8EF;">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td width="100%" lign="left" class="tbl-header-bold" valign="top" style="background-color: #E2E8EF;">
										<bean:message bundle="pluginResources" key="plugin.universalpolicy" />
										<img src="<%=basePath%>/images/active.jpg" title="Active" class="policy-status"/>
									</td>
								</tr>
							</table>
				 		</div>
						<div class="padding_10 toggleDivs">
							<table width="100%" cellspacing="0" cellpadding="0" border="0" class="parameterlist-pre-mapping-table">
								<tr>
									<td>
										<table width="100%" cellspacing="0" cellpadding="0">
											<tr>
												<td colspan="2" align="center" style="padding: 10px;">
													<table width="100%" cellspacing="0" cellpadding="0" border="0" class="pre-param-list-tbl-mapping">
														<tr>
															<td colspan="5">
																<table width="100%" cellspacing="0" cellpadding="0" border="0" class="box">
																	<tr>
																		<td class="labeltext" width="23.34%" style="border-right:1px solid #ccc;font-weight: bold;background-color: #D9E6F6;">
																			<bean:message bundle="pluginResources" key="plugin.universalpolicy.name" />
																		</td>
																		<td class="labeltext" width="10%" style="border-right:1px solid #ccc">
																			<span class="name"></span>
																		</td>
																		<td class="labeltext" width="23.34%" style="border-right:1px solid #ccc;font-weight: bold;background-color: #D9E6F6;">
																			<bean:message bundle="pluginResources" key="plugin.universalpolicy.action" />
																		</td>
																		<td class="labeltext" width="10%" style="border-right:1px solid #ccc">
																			<span class="action"></span>
																		</td>
																		<td class="labeltext" width="23.34%" style="border-right:1px solid #ccc;font-weight: bold;background-color: #D9E6F6;">
																			<bean:message bundle="pluginResources" key="plugin.universalpolicy.rejectoncheckitemnotfound" />
																		</td>
																		<td class="labeltext" width="10%">
																			<span class="rejectOnCheckedItemNotFound" style="text-transform: capitalize;"></span>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
														<tr>
															<td>&nbsp;</td>
														</tr>
														<tr>
															<td class="labeltext tblheader-policy" width="20%">
																<bean:message bundle="pluginResources" key="plugin.universalpolicy.parameter.active" />
															</td>
															<td class="labeltext tblheader-policy" width="20%">
																<bean:message bundle="pluginResources" key="plugin.universalpolicy.parameter.packettype" />
															</td>
															<td class="labeltext tblheader-policy" width="20%">
																<bean:message bundle="pluginResources" key="plugin.universalpolicy.parameter.attributeid" />
															</td>
															<td class="labeltext tblheader-policy" width="20%">
																<bean:message bundle="pluginResources" key="plugin.universalpolicy.parameter.attributevalue" />
															</td>
															<td class="labeltext tblheader-policy" width="20%">
																<bean:message bundle="pluginResources" key="plugin.universalpolicy.parameter.parameterusage" />
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</div>
					</div>
				</td>
			</tr>
		</table>
	</td>
</tr>
</table> <!--  /.Plugin Mapping -->

<!--  Plugin Mapping Template Table -->
<table width="100%" cellspacing="0" cellpadding="0" border="0" id="pre-param-list-tbl-template" style="display: none;">
	<tr>
		<td class="plugin-table-firstcol labeltext" width="20%" style="border-right: 1px solid #ccc;">
			<span class="active" style="text-transform: capitalize;width:100%;"></span>
		</td>
		<td class="tbl-plugin-rows labeltext" width="20%">
			<span class="packet_type"></span>
		</td>
		<td class="tbl-plugin-rows labeltext" width="20%">
			<span class="attr_id"></span>
		</td>
		<td class="tbl-plugin-rows labeltext" width="20%">
			<span class="attribute_value"></span>
		</td>
		<td class="tbl-plugin-rows labeltext" width="20%">
			<span class="parameter_usage"></span>
		</td>
	</tr>
</table><!-- /.Plugin Mapping Template Table -->
