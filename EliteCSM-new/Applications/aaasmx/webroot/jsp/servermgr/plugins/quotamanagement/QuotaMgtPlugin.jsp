<!--  Plugin Mapping Template -->
<table id="plugin-mapping-table-template" style="display: none;" class="plugin-mapping-table-template">
<tr class="plugin-table-tr">
	<td style="padding: 10px;">
		<table width="100%" cellspacing="0" cellpadding="0" border="0" class="quota-mgt-plugin-table">
			<tr>
				<td class="box">
					<div class="quota_mgt_plugin_table_div">
						<div class="table-header plugin-policy-background" style="font-weight: bold;cursor: pointer;">
							<table border="0" cellspacing="0" cellpadding="0" width="100%">
								<tr>
									<td width="97%" valign="left" class="tbl-header-bold" valign="top">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.subtitle" />
									</td>
									<td width="1%" style="background-color: #D9E6F6;">
										<div class="switch">
										  <input id="quotaEnabledDisabled" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);" />
										  <label for="quotaEnabledDisabled" class="handlerLabel"></label>
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
							<table width="100%" cellspacing="0" cellpadding="0" border="0" class="parameterlist-pre-mapping-table">
								
								<tr>
									<td class="captiontext" width="30%">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.name" />
										<ec:elitehelp headerBundle="pluginResources" text="plugin.prepaidquotamanager.name" header="plugin.prepaidquotamanager.name"/>
									</td>
									<td width="70%" class="labeltext">
										<input id="name" name="name" type="text" style="width: 260px;"/>
										<font color="#FF0000" colspan="3"> *</font>
									</td>
								</tr>
								
								<!-- Prepaid Quota Type -->
								<tr>
									<td class="captiontext" width="30%">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.ruleset" />
										<ec:elitehelp headerBundle="pluginResources" text="plugin.prepaidquotamanager.ruleset" header="plugin.prepaidquotamanager.ruleset"/>
									</td>
									<td width="70%" class="labeltext">
										<input id="ruleset" name="ruleset" type="text" style="width: 260px;"/>
									</td>
								</tr>
								
								<!-- Prepaid Quota Type -->
								<tr>
									<td class="captiontext" width="30%">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.prepaidquotatype" />
										<ec:elitehelp headerBundle="pluginResources" text="plugin.prepaidquotamanager.prepaidquotatype" header="plugin.prepaidquotamanager.prepaidquotatype"/>
									</td>
									<td width="70%" class="labeltext">
										<select id="prepaidQuotaType" name="prepaidQuotaType">
											<option value="BOTH">BOTH</option>
											<option value="TIME">TIME</option>
											<option value="VOLUME">VOLUME</option>
										</select>
									</td>
								</tr>
								
								<!-- Continue Further Processing -->
								<tr>
									<td class="captiontext" width="20%">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.continuefurtherprocesing" />
										<ec:elitehelp headerBundle="pluginResources" text="plugin.prepaidquotamanager.continuefurtherprocesing" header="plugin.prepaidquotamanager.continuefurtherprocesing"/>
									</td>
									<td width="80%" class="labeltext">
										<select id="furtherProcessing" name="furtherProcessing" >
											<option value="false">False</option>
											<option value="true">True</option>
										</select>
									</td>
								</tr>
								
								<!-- Action -->
								<tr>
									<td class="captiontext">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.action" />
										<ec:elitehelp headerBundle="pluginResources" text="plugin.prepaidquotamanager.action" header="plugin.prepaidquotamanager.action"/>
									</td>
									<td class="labeltext">
										<select id="action" name="action">
											<option value="1">ACCEPT</option>
											<option value="3">DROP</option>
										</select>
									</td>
								</tr>
								
								<!-- Type of Packet -->
								<tr>
									<td class="captiontext">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.typeofpacket" />
										<ec:elitehelp headerBundle="pluginResources" text="plugin.prepaidquotamanager.typeofpacket" header="plugin.prepaidquotamanager.typeofpacket"/>
									</td>
									<td class="labeltext">
										<select id="packetType" name="packetType">
											<option value="43">CoA</option>
											<option value="40">DM</option>
										</select>
									</td>
								</tr>
								
								<!-- Class Attribute Key for Volume -->
								<tr>
									<td class="captiontext">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.classattributekeyforvolume" />
										<ec:elitehelp headerBundle="pluginResources" text="plugin.prepaidquotamanager.classattributekeyforvolume" header="plugin.prepaidquotamanager.classattributekeyforvolume"/>
									</td>
									<td class="labeltext">
										<input id="keyForVolume" name="keyForVolume" type="text" value="MAX_SESSION_VOLUME=" style="width: 260px;"/>
									</td>
								</tr>
								
								<!-- Class Attribute Key for Time -->
								<tr>
									<td class="captiontext">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.classattributekeyfortime" />
										<ec:elitehelp headerBundle="pluginResources" text="plugin.prepaidquotamanager.classattributekeyfortime" header="plugin.prepaidquotamanager.classattributekeyfortime"/>
									</td>
									<td class="labeltext">
										<input id="keyForTime" name="keyForTime" type="text" value="MAX_SESSION_TIME=" style="width: 260px;"/>
									</td>
								</tr>
								
								<!-- List of Attributes -->
								<tr>
									<td class="captiontext">
										<bean:message bundle="pluginResources" key="plugin.prepaidquotamanager.listofattributes" />
										<ec:elitehelp headerBundle="pluginResources" text="plugin.prepaidquotamanager.listofattributes" header="plugin.prepaidquotamanager.listofattributes"/>
									</td>
									<td class="labeltext">
										<input id="strAttributes" name="strAttributes" type="text" value="0:1;0:4;0:31;24757:44=0:50;24757:60=0;0:89" style="width: 260px;"/>
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