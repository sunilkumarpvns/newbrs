<!--  Plugin Mapping Template -->
<table id="plugin-post-mapping-table-template" style="display: none;">
<tr class="plugin-table-tr">
	<td style="padding: 10px;">
		<table width="100%" cellspacing="0" cellpadding="0" border="0" class="universal-plugin-table">
			<tr>
				<td class="box">
					<div class="universal_post_plugin_div">
					<!-- <form class="universal_post_plugin_form"> -->
						<div class="table-header plugin-policy-background" style="font-weight: bold;cursor: pointer;">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td width="97%" lign="left" class="tbl-header-bold" valign="top">
										<bean:message bundle="pluginResources" key="plugin.universalpolicy" />
									</td>
									<td width="1%" style="background-color: #D9E6F6;">
										<div class="switch">
										  <input id="uniOutHandlerEnabledDisabled" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" />
										  <label for="uniOutHandlerEnabledDisabled" class="handlerLabel"></label>
										</div>
									</td>
									<td width="1%" valign="middle" class="tbl-header-bold" style="padding-right: 5px;">
										<img alt="Delete" class="delele_proxy"  title="Delete" src="<%=request.getContextPath()%>/images/delete_proxy.png" onclick="deleteHandler(this);" height="14" width="14" style="cursor: pointer;"/>
									</td>
									<td width="2%" valign="middle" class="tbl-header-bold" style="padding-right: 10px;" onclick="expandCollapse(this);" >
										<img alt="Expand" class="expand_class" title="Expand" id="showHideImg"  src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;float: right;"/>
									</td>
								</tr>
							</table>
						</div>
						<div class="padding_10 toggleDivs">
							<table width="100%" cellspacing="0" cellpadding="0" border="0" class="parameterlist-post-mapping-table">
								<tr>
									<td class="captiontext" width="30%">
										<bean:message bundle="pluginResources" key="plugin.universalpolicy.name" />
										<ec:elitehelp  header="plugin.universalpolicy.name" headerBundle="pluginResources" text="plugin.universalpolicy.name" ></ec:elitehelp>
									</td>
									<td width="70%" class="labeltext">
										<input type="text" class="textbox_width policy-name" name="name"/>
									</td>
								</tr>
								<tr>
									<td class="captiontext" width="20%">
										<bean:message bundle="pluginResources" key="plugin.universalpolicy.action" />
										<ec:elitehelp  header="plugin.universalpolicy.action" headerBundle="pluginResources" text="plugin.universalpolicy.action" ></ec:elitehelp>
									</td>
									<td width="80%" class="labeltext">
										<select class="select_width" name="action">
											<option value="1">none</option>
											<option value="2">Stop</option>
										</select>
									</td>
								</tr>
								<tr>
									<td class="captiontext labeltext" colspan="2" style="padding-left: 25px;">
										<input type="button" value="Add Parameter" class="light-btn" onclick="addParameterList('post-param-list-tbl-mapping','post-param-list-tbl-template',this);" />
									</td>
								</tr>
								<tr>
									<td colspan="2" align="left" class="captiontext">
										<table width="100%" cellspacing="0" cellpadding="0" border="0" class="post-param-list-tbl-mapping">
											<tr>
												<td class="labeltext tblheader-policy" width="18.6%">
													<bean:message bundle="pluginResources" key="plugin.universalpolicy.parameter.active" />
													<ec:elitehelp  header="plugin.universalpolicy.parameter.active" headerBundle="pluginResources" text="plugin.universalpolicy.parameter.active" ></ec:elitehelp>
												</td>
												<td class="labeltext tblheader-policy" width="18.6%">
													<bean:message bundle="pluginResources" key="plugin.universalpolicy.parameter.packettype" />
													<ec:elitehelp  header="plugin.universalpolicy.parameter.packettype" headerBundle="pluginResources" text="plugin.universalpolicy.parameter.packettype" ></ec:elitehelp>
												</td>
												<td class="labeltext tblheader-policy" width="18.6%">
													<bean:message bundle="pluginResources" key="plugin.universalpolicy.parameter.attributeid" />
													<ec:elitehelp  header="plugin.universalpolicy.parameter.attributeid" headerBundle="pluginResources" text="plugin.universalpolicy.parameter.attributeid" ></ec:elitehelp>
												</td>
												<td class="labeltext tblheader-policy" width="18.6%">
													<bean:message bundle="pluginResources" key="plugin.universalpolicy.parameter.attributevalue" />
													<ec:elitehelp  header="plugin.universalpolicy.parameter.attributevalue" headerBundle="pluginResources" text="plugin.universalpolicy.parameter.attributevalue" ></ec:elitehelp>
												</td>
												<td class="labeltext tblheader-policy" width="18.6%">
													<bean:message bundle="pluginResources" key="plugin.universalpolicy.parameter.parameterusage" />
													<ec:elitehelp  header="plugin.universalpolicy.parameter.parameterusage" headerBundle="pluginResources" text="plugin.universalpolicy.parameter.parameterusages" ></ec:elitehelp>
												</td>
												<td class="labeltext tblheader-policy" width="7%">
													<bean:message bundle="pluginResources" key="plugin.parameterlist.remove" />
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
<table width="100%" cellspacing="0" cellpadding="0" border="0" id="post-param-list-tbl-template" style="display: none;">
	<tr>
		<td class="plugin-table-firstcol" width="18.6%">
			<select class="select_width full-width noborder" name="active">
				<option selected="selected" value="NO">No</option>
				<option value="YES">Yes</option>
			</select>
		</td>
		<td class="tbl-plugin-rows" width="18.6%">
			<select name="packet_type" class="full-width noborder">
					<option value="2">Answer</option>
					<option value="1">Request</option>
			</select>
		</td>
		<td class="tbl-plugin-rows" width="18.6%">
			<input type="text" class="full-width noborder" name="attr_id"/>
		</td>
		<td class="tbl-plugin-rows" width="18.6%">
			<input type="text" class="full-width noborder" name="attribute_value"/>
		</td>
		<td class="tbl-plugin-rows" width="18.6%">
			<select name="parameter_usage" class="full-width noborder">
				<option value="C">Check Item</option>
				<option value="A">Dynamical Assign Item</option>
				<option value="F">Filter Item</option>
				<option value="J">Reject Item</option>
				<option value="R">Reply Item</option>
				<option value="U">Update Item</option>
				<option value="V">Value Replace Item</option>
			</select>
		</td>
		<td class="tblrows" width="7%" align="middle">
			<span class='delete remove-proxy-server' onclick="deleteMe(this);"></span>&nbsp;
		</td>
	</tr>
</table><!-- /.Plugin Mapping Template Table -->
