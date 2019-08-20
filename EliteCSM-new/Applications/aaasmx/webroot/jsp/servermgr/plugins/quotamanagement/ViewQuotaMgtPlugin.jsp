<!--  Plugin Mapping Template -->
<table id="plugin-mapping-table-template" style="display: none;" class="plugin-mapping-table-template">
<tr class="plugin-table-tr">
	<td style="padding: 10px;">
		<table width="100%" cellspacing="0" cellpadding="0" border="0" class="quota-mgt-plugin-table">
			<tr>
				<td class="box">
					<div class="quota_mgt_plugin_table_div">
						<div style="font-weight: bold;">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td width="100%" valign="left" class="tbl-header-bold" valign="top">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.subtitle" />
										<img src="<%=basePath%>/images/active.jpg" title="Active" class="policy-status"/>
									</td>
								</tr>
							</table>
				 		</div>
						<div class="padding_10 toggleDivs">
							<table width="100%" cellspacing="0" cellpadding="0" border="0" class="parameterlist-pre-mapping-table">
								
								<!--Name -->
								<tr>
									<td class="tblfirstcol captiontext" width="30%">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.name" />
									</td>
									<td width="70%" class="labeltext tblcol" >
										<span class="name"></span>
									</td>
								</tr>
								
								<!--Ruleset -->
								<tr>
									<td class="tblfirstcol captiontext" width="30%">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.ruleset" />
									</td>
									<td width="70%" class="labeltext tblcol" >
										<span class="ruleset"></span>
									</td>
								</tr>
								
								<!-- Prepaid Quota Type -->
								<tr>
									<td class="tblfirstcol captiontext" width="30%">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.prepaidquotatype" />
									</td>
									<td width="70%" class="labeltext tblcol" >
										<span class="prepaidQuotaType"></span>
									</td>
								</tr>
								
								<!-- Continue Further Processing -->
								<tr>
									<td class="captiontext tblfirstcol" width="20%">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.continuefurtherprocesing" />
									</td>
									<td width="80%" class="labeltext tblcol" >
										<span class="furtherProcessing" style="text-transform: capitalize;"></span>
									</td>
								</tr>
								
								<!-- Action -->
								<tr>
									<td class="captiontext tblfirstcol">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.action" />
									</td>
									<td class="labeltext tblcol">
										<span class="action"></span>
									</td>
								</tr>
								
								<!-- Type of Packet -->
								<tr>
									<td class="captiontext tblfirstcol">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.typeofpacket" />
									</td>
									<td class="labeltext tblcol">
										<span class="packetType"></span>
									</td>
								</tr>
								
								<!-- Class Attribute Key for Volume -->
								<tr>
									<td class="captiontext tblfirstcol">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.classattributekeyforvolume" />
									</td>
									<td class="labeltext tblcol">
										<span class="keyForVolume"></span>
									</td>
								</tr>
								
								<!-- Class Attribute Key for Time -->
								<tr>
									<td class="captiontext tblfirstcol">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.classattributekeyfortime" />
									</td>
									<td class="labeltext tblcol">
										<span class="keyForTime"></span>
									 </td>
								</tr>
								
								<!-- List of Attributes -->
								<tr>
									<td class="captiontext tblfirstcol">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.listofattributes" />
									</td>
									<td class="labeltext tblcol">
										<span class="strAttributes"></span>
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
</table>