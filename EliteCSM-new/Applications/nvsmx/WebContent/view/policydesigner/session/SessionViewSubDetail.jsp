<%@taglib uri="/struts-tags/ec" prefix="s"%>

<style type="text/css">
.form-group {
	width: 100%;
	display: table;
	margin-bottom: 2px;
}

.parent:HOVER .child, .parent:HOVER .child { 
	color: #4679bd;
}

.qos-width {
	min-width: 50px;
}

</style> 
<div style="line-height: 1.5; margin-top: 15px">
	<s:iterator value="sessionData.sessionInfo" var="sessionInfo">
		<s:if test="value != null">
			<s:if test="key != 'Sub.SubscriberIdentity' && key != 'CS.SessionIPv4' && key != 'CS.SessionType' && key != 'CS.UsageReservation' && key != 'CS.ActivePCCRules' && key != 'CS.ActiveChargingRuleBaseNames' ">
				<div class="parent">
					<div class="col-sm-4 col-xs-4 child word-break" style="font-weight: 700;">
						<s:property value="key" />
					</div>
					<div class="col-sm-8 col-xs-8 child word-break">
						<s:property value="%{value}" />
					</div>
				</div>

				<s:if test="%{@com.elitecore.corenetvertex.constants.PCRFKeyConstants@CS_SESSION_ID.getVal() == key}" >
					<s:set var="sessionId">
						<s:text name="%{value}"  />
					</s:set>

				</s:if>
			</s:if>
		</s:if>
	</s:iterator>
	<s:if test="sessionData.sessionType == @com.elitecore.corenetvertex.constants.SessionTypeConstant@GX.getVal() || sessionData.sessionType == @com.elitecore.corenetvertex.constants.SessionTypeConstant@RADIUS.getVal()">
		<div class="col-xs-12" style="padding-top: 20px">
			<table class="table table-blue-stripped table-condensed table-bordered">
				<caption class="caption-header" style="font-size: 12px">
					<s:text name="installed.charging.rule.base.names" />
				</caption>
				<tr>
					<th><s:text name="session.activepcc.name" /></th>
					<th><s:text name="session.activepcc.servicetype" /></th>
					<th><s:text name="session.activepcc.monitorinkey" /></th>
				</tr>
				<s:if test="sessionData.activeChargingRuleBaseNames.size() > 0">
					<s:iterator value="sessionData.activeChargingRuleBaseNames" var="activeChargingRuleBaseName">
						<tr>
							<td class="word-break"><s:property value="#activeChargingRuleBaseName.name" /></td>
							<td class="word-break">

								<s:property value="#activeChargingRuleBaseName.dataServiceType"/>
							</td>
							<td class="word-break">

								<s:property value="#activeChargingRuleBaseName.monitoringKey"/>
							</td>
						</tr>
					</s:iterator>
				</s:if>
				<s:else>
					<tr>
						<td colspan="6"><s:text name="basic.empty.list" /></td>
					</tr>
				</s:else>
			</table>
		</div>
	<div class="col-xs-12" style="padding-top: 20px">
		<table class="table table-blue-stripped table-condensed table-bordered">
			<caption class="caption-header" style="font-size: 12px">
				<s:text name="installed.default.pccrule" />
			</caption>
			<tr>
				<th><s:text name="session.activepcc.name" /></th>
				<th><s:text name="session.activepcc.servicetype" /></th>
				<th><s:text name="session.activepcc.monitorinkey" /></th>
				<th><s:text name="session.activepcc.type" /></th>
				<th><s:text name="session.activepcc.gbr" /></th>
				<th><s:text name="session.activepcc.mbr" /></th>
			</tr>
			<s:if test="sessionData.activePccrules.size() > 0">
				<s:iterator value="sessionData.activePccrules" var="activePccrule">
					<tr>
						<td class="word-break"><s:property value="#activePccrule.name" /></td>
						<td><s:property value="#activePccrule.serviceName"/></td>
						<td class="word-break"><s:property value="#activePccrule.monitoringKey"/></td>
						<td>
							<s:if test="#activePccrule.predifine == false">
								<s:text name="session.pcc.dynamic"></s:text>
							</s:if>
							<s:else>
								<s:text name="session.pcc.static"></s:text>
							</s:else>
						</td>
						<td class="qos-width word-break">
							<s:if test="#activePccrule.GBRDL != null">
								<div>
									<s:property value="#activePccrule.GBRDL"/><span class="glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow"></span>
								</div>
							</s:if>
							<s:if test="#activePccrule.GBRUL != null">
								<div>
									<s:property value="#activePccrule.GBRUL"/><span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow"></span>
								</div>
							</s:if>
						</td>
						<td class="qos-width word-break">
							<s:if test="#activePccrule.MBRDL != null">
								<div>
									<s:property value="#activePccrule.MBRDL"/><span class="glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow"></span>
								</div>
							</s:if>
							<s:if test="#activePccrule.MBRUL != null">
								<div>
									<s:property value="#activePccrule.MBRUL"/><span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow"></span>
								</div>
							</s:if>
						</td>
					</tr>
				</s:iterator>
			</s:if>
			<s:else>
				<tr>
					<td colspan="6"><s:text name="basic.empty.list" /></td>
				</tr>
			</s:else>
		</table>
	</div>
	</s:if>
	<div class="col-xs-12" style="padding-top: 20px">
		<table class="table table-blue-stripped table-condensed table-bordered">
			<caption class="caption-header" style="font-size: 12px">
				<s:text name="installed.ims.pccrule" />
			</caption>
			<tr>
				<th><s:text name="session.activepcc.name" /></th>
				<th><s:text name="session.activepcc.mediatype" /></th>
				<th><s:text name="session.activepcc.mbr" /></th>
				<th><s:text name="session.activepcc.gbr" /></th>
				<th><s:text name="session.activepcc.qci" /></th>
				<th><s:text name="session.activepcc.flowstatus" /></th>
				<th><s:text name="session.activepcc.sdf" /></th>

			</tr>
			<s:if test="sessionData.afActivePccrules.size() > 0">
				<s:iterator value="sessionData.afActivePccrules" var="afActivePccrule">
					<tr>
						<td class="word-break"><s:property value="#afActivePccrule.id" /></td>
						<td class="word-break"><s:property value="#afActivePccrule.serviceName" /></td>
						<td class="qos-width">
							<s:if test="#afActivePccrule.MBRDL != null">
								<div>
									<s:property value="#afActivePccrule.MBRDL"/><span class="glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow"></span>
								</div>
							</s:if>
							<s:if test="#afActivePccrule.MBRUL != null">
								<div>
									<s:property value="#afActivePccrule.MBRUL"/><span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow"></span>
								</div>
							</s:if>
						</td>
						<td class="qos-width word-break">
							<s:if test="#afActivePccrule.GBRDL != null">
								<div>
									<s:property value="#afActivePccrule.GBRDL"/><span class="glyphicon glyphicon-arrow-down small-glyphicons up-down-arrow"></span>
								</div>
							</s:if>
							<s:if test="#afActivePccrule.GBRUL != null">
								<div>
									<s:property value="#afActivePccrule.GBRUL"/><span class="glyphicon glyphicon-arrow-up small-glyphicons up-down-arrow"></span>
								</div>
							</s:if>
						</td>

						<td class="word-break"><s:property value="#afActivePccrule.qci.val"/> </td>
						<td class="word-break"><s:property value="#afActivePccrule.flowStatus.displayVal" /></td>
						<td class="word-break">
							<s:iterator value="#afActivePccrule.serviceDataFlows" var="serviceDataFlow">
								<s:property value="#serviceDataFlow"/> <br>
							</s:iterator>
						</td>
						</tr>
					</tr>
				</s:iterator>
			</s:if>
			<s:else>
				<tr>
					<td colspan="7"><s:text name="basic.empty.list" /></td>
				</tr>
			</s:else>
		</table>
	</div>
	<s:if test="sessionData.sessionType == @com.elitecore.corenetvertex.constants.SessionTypeConstant@GX.getVal() || sessionData.sessionType == @com.elitecore.corenetvertex.constants.SessionTypeConstant@RADIUS.getVal()">
		<s:if test="sessionData.subSessionsIdentifier != null">
			<div class="col-xs-12" style="margin-bottom: 15px">
				<button type="button" class="btn btn-sm btn-primary" onclick="loadIMSSession('<s:property value="sessionData.subSessionsIdentifier"  escapeJavaScript="true"/>')">
					<s:text name="subsession.ims.button" />
				</button>
			</div>
		</s:if>
		<s:else>
			<div class="col-xs-12" style="margin-bottom: 15px">
				<button type="button" class="btn btn-sm btn-primary" disabled="disabled">
					<s:text name="subsession.ims.button" />
				</button>
			</div>
		</s:else>
	</s:if>
</div>

<div class="modal fade" id="subSessions" tabindex="-1" role="dialog" aria-labelledby="subSessionsLabel" aria-hidden="true" >
	<div class="modal-dialog" style="width: 80%">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="subSessionsLabel"> <s:text name="subsession.ims" ></s:text> </h4>
			</div>
			<div class="modal-body" id="content"></div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal"><s:text name="button.close" /></button>
			</div>
		</div>
	</div>
</div>

<script>
	function loadIMSSession(subSessionsIdentifier) {
		if(isNullOrEmpty(subSessionsIdentifier)) {
			subSessionsIdentifier = "";
		}

		var coreSessionIds = subSessionsIdentifier;

		var encodedString =encodeURIComponent(coreSessionIds);
		criteria = {"sessionAttribute":"CORE_SESSION_ID","attributeValue":encodedString};

		var csSessionId = '<s:property value="%{sessionId}"  escapeJavaScript="true" />';

		csSessionId = csSessionId.replace(/[^a-z0-9\s]/gi,'').replace(/[_:;\s]/g,'');

		$('#content').load('${pageContext.request.contextPath}/view/policydesigner/session/SubSessionView.jsp',{'sessionId':csSessionId},function(){
			$('#content').trigger('loaded')
		});
		$("#subSessions").modal('show');
	}
</script>

