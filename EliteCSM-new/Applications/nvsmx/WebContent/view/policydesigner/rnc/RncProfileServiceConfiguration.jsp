<style type="text/css">
	.customized-row-margin {
		margin-right: -43px;
	}
	.vertical-scroll{
		max-height: 550px;
		overflow-y: scroll;
		overflow-x: hidden;
	}
	hr{
		margin: 0px;
		border-top: 1px solid #dae4f1;
	}

</style>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="modal col-xs-12" id="RncServiceConfDialog" tabindex="-1" role="dialog" aria-labelledby="RncServiceConfDialog" id = "RncServiceConfDialog" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
					aria-label="Close" onclick="clearRncConfigurationDialog()">
				<span aria-hidden="true">&times;</span>
			</button>
			<h4 class="modal-title set-title" id="serviceDialogTitle">
				<s:text name="rnc.configuration"  />
			</h4>
		</div>
			<s:form action="policydesigner/rnc/RncProfile/createOrUpdateDetail" method="post" cssClass="form-horizontal" validate="true"
					namespace="/" id="rncServiceConfigurationFormId" validator="validate()">
			<s:hidden name="rncProfileDetailData.fupLevel" id="fupLevel"/>
			<s:hidden name="rncProfileData.id" id="rncProfilId" />
			<s:hidden name="rncProfileDetailData.id" id="rncProfileDetailId" />
			<s:hidden id="groupIds" name="groupIds" value="%{rncProfileData.groups}"/>


			<div class="modal-body vertical-scroll" >
				<div class="row" style="margin-bottom:10px">
						<div class="col-xs-12 bottom-space">
							<div class="col-xs-6">
								<div class="row customized-row-margin">
									<strong><s:text name="rnc.profile.detail.servicetype" /></strong>
									<s:select tabindex="1" cssClass="form-control focusElement" list="dataServiceTypeData" name="rncProfileDetailData.serviceTypeId"
											  elementCssClass="col-xs-12" id="dataServiceType" listValue="getName()" listKey="getId()" onClick="setratingGroup();"/>
								</div>
							</div>
							<div class="col-xs-6">

								<div class="row customized-row-margin">
									<strong><s:text name="rnc.profile.detail.ratinggroup" /></strong>
									<s:select name="rncProfileDetailData.ratingGroupId" cssClass="form-control focusElement" list="#{}"  id="ratingGroup" tabindex="1" elementCssClass="col-xs-12">
										<s:optgroup label="Preferred Rating Groups" list="#{}"  listValue="%{name+'('+identifier+')'}" listKey="id" />
										<s:optgroup label="Other Rating Groups" list="#{}" listValue="%{name+'('+identifier+')'}" listKey="id"  />
									</s:select>
								</div>
							</div>
						</div>
					</div>
				<div class="row" style="margin-bottom:10px">
					<div class="col-xs-12">
						<div><strong><s:text name="rnc.profile.detail.volumequota"/> </strong></div>
					</div>
					<div class="col-xs-12 col-sm-12">
						<s:if test="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@TIME.name() == rncProfileData.quotaType}" >
							<div class="col-xs-6">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12"
												 placeholder="UNLIMITED" id="balance" readonly="true"
												 name="rncProfileDetailData.balance"
												 maxlength="18" onkeypress="return isNaturalInteger(event);" />
								</div>
							</div>
							<div class="col-xs-6">
								<div class="row customized-row-margin">
									<s:select cssClass="form-control" elementCssClass="col-xs-12" id="balanceUnit"
											  name="rncProfileDetailData.balanceUnit" readonly="true"
											  value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}"
											  list="@com.elitecore.corenetvertex.constants.DataUnit@values()" />
								</div>
							</div>
						</s:if>
						<s:else>
							<div class="col-xs-6">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12"
												 placeholder="UNLIMITED" id="balance"
												 name="rncProfileDetailData.balance"
												 maxlength="18" onkeypress="return isNaturalInteger(event);" />
								</div>
							</div>
							<div class="col-xs-6">
								<div class="row customized-row-margin">
									<s:select cssClass="form-control" elementCssClass="col-xs-12" id="balanceUnit"
											  name="rncProfileDetailData.balanceUnit"
											  value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}"
											  list="@com.elitecore.corenetvertex.constants.DataUnit@values()" />
								</div>
							</div>
						</s:else>
					</div>
				</div>
				<div class="row" style="margin-bottom:10px">
					<div class="col-xs-12">
						<div><strong><s:text name="rnc.profile.detail.timequota" /></strong></div>
					</div>
					<div class="col-xs-12 bottom-space">
						<s:if test="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@VOLUME.name() == rncProfileData.quotaType}" >
							<div class="col-xs-6">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" placeholder="UNLIMITED" elementCssClass="col-xs-12" name="rncProfileDetailData.timeBalance" maxlength="18" onkeypress="return isNaturalInteger(event);" id="timeBalance" readonly="true"/>
								</div>
							</div>
							<div class="col-xs-6">
								<div class="row customized-row-margin">
									<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.timeBalanceUnit" value="%{@com.elitecore.corenetvertex.constants.TimeUnit@DAY.name()}" id="timeBalanceUnit" readOnly="true"
										  list="#{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND,@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE,@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR,@com.elitecore.corenetvertex.constants.TimeUnit@DAY:@com.elitecore.corenetvertex.constants.TimeUnit@DAY,@com.elitecore.corenetvertex.constants.TimeUnit@WEEK:@com.elitecore.corenetvertex.constants.TimeUnit@WEEK,@com.elitecore.corenetvertex.constants.TimeUnit@MONTH:@com.elitecore.corenetvertex.constants.TimeUnit@MONTH}"/>
								</div>
							</div>
						</s:if>
						<s:else>
							<div class="col-xs-6">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" placeholder="UNLIMITED" elementCssClass="col-xs-12" name="rncProfileDetailData.timeBalance" maxlength="18" onkeypress="return isNaturalInteger(event);" id="timeBalance"  />
								</div>
							</div>
							<div class="col-xs-6">
								<div class="row customized-row-margin">
									<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.timeBalanceUnit" value="%{@com.elitecore.corenetvertex.constants.TimeUnit@DAY.name()}" id="timeBalanceUnit"
											  list="#{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND,@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE,@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR,@com.elitecore.corenetvertex.constants.TimeUnit@DAY:@com.elitecore.corenetvertex.constants.TimeUnit@DAY,@com.elitecore.corenetvertex.constants.TimeUnit@WEEK:@com.elitecore.corenetvertex.constants.TimeUnit@WEEK,@com.elitecore.corenetvertex.constants.TimeUnit@MONTH:@com.elitecore.corenetvertex.constants.TimeUnit@MONTH}"/>
								</div>
							</div>
						</s:else>
					</div>
				</div>

				<div class="row" style="margin-bottom:10px">
					<div class="col-xs-12">
						<div><strong><s:text name="rnc.profile.detail.pulse" /></strong></div>
					</div>
					<div class="col-xs-12 col-sm-12">
						<s:if test="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@TIME.name() == rncProfileData.quotaType}" >
							<div class="col-sm-3">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12" readonly="true"
												 id="pulseVolume"
												 name="rncProfileDetailData.pulseVolume"
												 maxlength="18" onkeypress="return isNaturalInteger(event);" />
								</div>
							</div>
							<div class="col-sm-3">
								<div class="row customized-row-margin">
										<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.pulseVolumeUnit"
												  value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}" id="pulseVolumeUnit" readonly="true"
												  list="@com.elitecore.corenetvertex.constants.DataUnit@values()" />
								</div>
							</div>
						</s:if>
						<s:else>
							<div class="col-sm-3">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12"
												 id="pulseVolume"
												 name="rncProfileDetailData.pulseVolume"
												 maxlength="15" onkeypress="return isNaturalInteger(event);" />
								</div>
							</div>
							<div class="col-sm-3">
								<div class="row customized-row-margin">
									<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.pulseVolumeUnit"
											  value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}" id="pulseVolumeUnit"
											  list="@com.elitecore.corenetvertex.constants.DataUnit@values()" />
								</div>
							</div>
						</s:else>
						<s:if test="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@VOLUME.name() == rncProfileData.quotaType}" >
							<div class="col-sm-3">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12" readonly="true"
												 id="pulseTime"
												 name="rncProfileDetailData.pulseTime"
												 maxlength="15" onkeypress="return isNaturalInteger(event);" />
								</div>
							</div>
							<div class="col-sm-3">
								<div class="row customized-row-margin">
										<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.pulseTimeUnit" readonly="true"
												  value="%{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND.name()}" id="pulseTimeUnit"
												  list="#{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND,@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE,@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR}"/>
								</div>
							</div>
						</s:if>
						<s:else>
							<div class="col-sm-3">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12"
												 id="pulseTime"
												 name="rncProfileDetailData.pulseTime"
												 maxlength="18" onkeypress="return isNaturalInteger(event);" />
								</div>
							</div>
							<div class="col-sm-3">
								<div class="row customized-row-margin">
									<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.pulseTimeUnit"
											  value="%{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND.name()}" id="pulseTimeUnit"
											  list="#{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND,@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE,@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR}"/>
								</div>
							</div>
						</s:else>

					</div>

				</div>

				<div class="row" style="margin-bottom:10px">
					<div class="col-xs-12">
                        <s:set var="priceTag">
                            <s:property value="getText('rnc.profile.detail.rate')"/> <s:property value="getText('opening.braces')"/><s:property value="%{rncProfileData.pkgData.currency}"/><s:property value="getText('closing.braces')"/>
                        </s:set>
						<div><strong><s:text name="priceTag" /></strong></div>
					</div>
					<div class="col-xs-12 col-sm-12">
						<div class="col-xs-6">
							<div class="row customized-row-margin">
								<s:textfield cssClass="form-control" elementCssClass="col-xs-12"
											 placeholder="RATE" id="rateValue" name="rncProfileDetailData.rate" maxlength="16" />
							</div>
						</div>
						<div class="col-xs-6">
							<div class="row customized-row-margin">
								<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.rateUnit"
										  value="%{@com.elitecore.corenetvertex.constants.UsageType@VOLUME.name()}" id="rateUnit"
										  list="@com.elitecore.corenetvertex.constants.UsageType@values()" />
							</div>
						</div>
					</div>

				</div>

				<div class="row" style="margin-bottom:10px">
					<div class="col-xs-12">
						<div><strong><s:text name="rnc.profile.detail.usagelimit" /></strong></div>
					</div>
					<div class="col-xs-12 col-sm-12">
						<s:if test="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@TIME.name() == rncProfileData.quotaType}" >
							<div class="col-sm-4">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12" id="dailyUsageLimitModel"
												 placeholder="DAILY" name="rncProfileDetailData.dailyUsageLimit" readonly="true"
												 maxlength="18" onkeypress="return isNaturalInteger(event);" />
								</div>
							</div>
							<div class="col-sm-4">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12" id="weeklyUsageLimitModel"
												 placeholder="WEEKLY" name="rncProfileDetailData.weeklyUsageLimit" readonly="true"
												 maxlength="18" onkeypress="return isNaturalInteger(event);" />
								</div>
							</div>
							<div class="col-sm-4">
								<div class="row customized-row-margin">
									<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.usageLimitUnit"
											  value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}" id="usageLimitUnit"
											  list="@com.elitecore.corenetvertex.constants.DataUnit@values()" readonly="true" />
								</div>
							</div>
						</s:if>
						<s:else>
							<div class="col-sm-4">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12" id="dailyUsageLimitModel"
												 placeholder="DAILY" name="rncProfileDetailData.dailyUsageLimit"
												 maxlength="18" onkeypress="return isNaturalInteger(event);" />
								</div>
							</div>
							<div class="col-sm-4">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12" id="weeklyUsageLimitModel"
												 placeholder="WEEKLY" name="rncProfileDetailData.weeklyUsageLimit"
												 maxlength="18" onkeypress="return isNaturalInteger(event);" />
								</div>
							</div>
							<div class="col-sm-4">
								<div class="row customized-row-margin">
									<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.usageLimitUnit"
											  value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}" id="usageLimitUnit"
											  list="@com.elitecore.corenetvertex.constants.DataUnit@values()" />
								</div>
							</div>
						</s:else>
					</div>
				</div>

				<div class="row" style="margin-bottom:10px">
					<div class="col-xs-12">
						<div><strong><s:text name="rnc.profile.detail.timelimit" /></strong></div>
					</div>
					<div class="col-xs-12 bottom-space">
						<s:if test="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@VOLUME.name() == rncProfileData.quotaType}" >
							<div class="col-xs-4">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.dailyTimeLimit" maxlength="18" onkeypress="return isNaturalInteger(event);" id="dailyTimeLimit" placeholder="DAILY" readonly="true" />
								</div>
							</div>
							<div class="col-xs-4">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.weeklyTimeLimit" maxlength="18" onkeypress="return isNaturalInteger(event);" id="weeklyTimeLimit" placeholder="WEEKLY" readonly="true" />
								</div>
							</div>
							<div class="col-xs-4">
								<div class="row customized-row-margin">
									<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.timeLimitUnit" readonly="true"
											  value="%{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND.name()}" id="timeLimitUnit"
											  list="#{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND,@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE,@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR}"/>
								</div>
							</div>
						</s:if>
						<s:else>
							<div class="col-xs-4">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.dailyTimeLimit" maxlength="18" onkeypress="return isNaturalInteger(event);" id="dailyTimeLimit" placeholder="DAILY" />
								</div>
							</div>
							<div class="col-xs-4">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.weeklyTimeLimit" maxlength="18" onkeypress="return isNaturalInteger(event);" id="weeklyTimeLimit" placeholder="WEEKLY"/>
								</div>
							</div>
							<div class="col-xs-4">
								<div class="row customized-row-margin">
									<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.timeLimitUnit"
											  value="%{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND.name()}" id="timeLimitUnit"
											  list="#{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND,@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE,@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR}"/>
								</div>
							</div>
						</s:else>
					</div>
				</div>

				<s:if test="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@ENABLE.booleanValue == rncProfileData.carryForward}" >
					<div class="row" style="margin-bottom:10px">
						<div class="col-xs-12">
							<div><strong><s:text name="rnc.profile.carryForwardLimit" /></strong></div>
						</div>
						<div class="col-xs-12 col-sm-12">
							<s:if test="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@TIME.name() == rncProfileData.quotaType}" >
								<div class="col-sm-3">
									<div class="row customized-row-margin">
										<s:textfield cssClass="form-control" elementCssClass="col-xs-12" readonly="true"
													 id="volumeCarryForwardLimitModel"
													 placeholder = "UNLIMITED"
													 name="rncProfileDetailData.volumeCarryForwardLimit"
													 maxlength="18" onkeypress="return isNaturalInteger(event);" />
									</div>
								</div>
								<div class="col-sm-3">
									<div class="row customized-row-margin">
										<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.volumeCarryForwardLimitUnit"
												  value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}" id="volumeCarryForwardLimitUnitModel" readonly="true" disabled="true"
												  list="@com.elitecore.corenetvertex.constants.DataUnit@values()" />
									</div>
								</div>
							</s:if>
							<s:else>
								<div class="col-sm-3">
									<div class="row customized-row-margin">
										<s:textfield cssClass="form-control" elementCssClass="col-xs-12"
													 id="volumeCarryForwardLimitModel"
													 placeholder = "UNLIMITED"
													 name="rncProfileDetailData.volumeCarryForwardLimit"
													 maxlength="15" onkeypress="return isNaturalInteger(event);" />
									</div>
								</div>
								<div class="col-sm-3">
									<div class="row customized-row-margin">
										<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.volumeCarryForwardLimitUnit" readonly="true" disabled="true"
												  value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}" id="volumeCarryForwardLimitUnitModel"
												  list="@com.elitecore.corenetvertex.constants.DataUnit@values()" />
									</div>
								</div>
							</s:else>
							<s:if test="%{@com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType@VOLUME.name() == rncProfileData.quotaType}" >
								<div class="col-sm-3">
									<div class="row customized-row-margin">
										<s:textfield cssClass="form-control" elementCssClass="col-xs-12" readonly="true"
													 id="timeCarryForwardLimit"
													 placeholder = "UNLIMITED"
													 name="rncProfileDetailData.timeCarryForwardLimit"
													 maxlength="15" onkeypress="return isNaturalInteger(event);" />
									</div>
								</div>
								<div class="col-sm-3">
									<div class="row customized-row-margin">
										<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.timeCarryForwardLimitUnit" readonly="true" disabled="true"
												  value="%{@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE.name()}" id="timeCarryForwardLimitUnit"
												  list="#{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND,@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE,@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR}"/>
									</div>
								</div>
							</s:if>
							<s:else>
								<div class="col-sm-3">
									<div class="row customized-row-margin">
										<s:textfield cssClass="form-control" elementCssClass="col-xs-12"
													 id="timeCarryForwardLimit"
													 name="rncProfileDetailData.timeCarryForwardLimit"
													 placeholder = "UNLIMITED"
													 maxlength="18" onkeypress="return isNaturalInteger(event);" />
									</div>
								</div>
								<div class="col-sm-3">
									<div class="row customized-row-margin">
										<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.timeCarryForwardLimitUnit" readonly="true" disabled="true"
												  value="%{@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE.name()}" id="timeCarryForwardLimitUnit"
												  list="#{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND,@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE,@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR}"/>
									</div>
								</div>
							</s:else>
						</div>
					</div>
				</s:if>
				<s:else>
					<div class="row" style="margin-bottom:10px">
						<div class="col-xs-12">
							<div><strong><s:text name="rnc.profile.carryForwardLimit" /></strong></div>
						</div>
						<div class="col-xs-12 col-sm-12">
							<div class="col-sm-3">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12" readonly="true"
												 id="volumeCarryForwardLimitModel"
												 placeholder = "UNLIMITED"
												 name="rncProfileDetailData.volumeCarryForwardLimit"
												 maxlength="18" onkeypress="return isNaturalInteger(event);" />
								</div>
							</div>
							<div class="col-sm-3">
								<div class="row customized-row-margin">
									<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.volumeCarryForwardLimitUnit"
											  value="%{@com.elitecore.corenetvertex.constants.DataUnit@MB.name()}" id="volumeCarryForwardLimitUnitModel" readonly="true" disabled="true"
											  list="@com.elitecore.corenetvertex.constants.DataUnit@values()" />
								</div>
							</div>
							<div class="col-sm-3">
								<div class="row customized-row-margin">
									<s:textfield cssClass="form-control" elementCssClass="col-xs-12" readonly="true"
												 id="timeCarryForwardLimit"
												 placeholder = "UNLIMITED"
												 name="rncProfileDetailData.timeCarryForwardLimit"
												 maxlength="15" onkeypress="return isNaturalInteger(event);" />
								</div>
							</div>
							<div class="col-sm-3">
								<div class="row customized-row-margin">
									<s:select cssClass="form-control" elementCssClass="col-xs-12" name="rncProfileDetailData.timeCarryForwardLimitUnit" readonly="true" disabled="true"
											  value="%{@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE.name()}" id="timeCarryForwardLimitUnit"
											  list="#{@com.elitecore.corenetvertex.constants.TimeUnit@SECOND:@com.elitecore.corenetvertex.constants.TimeUnit@SECOND,@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE:@com.elitecore.corenetvertex.constants.TimeUnit@MINUTE,@com.elitecore.corenetvertex.constants.TimeUnit@HOUR:@com.elitecore.corenetvertex.constants.TimeUnit@HOUR}"/>
								</div>
							</div>
						</div>
					</div>
				</s:else>
				<div class="row" style="margin-bottom:10px">
					<div class="col-xs-12">
						<div><strong><s:text name="rnc.profile.revenuedetail"></s:text></strong></div>
					</div>
					<div class="col-xs-12 col-sm-12">
						<div class = "col-xs-12">
							<div class="row customized-row-margin">
								<s:select name="rncProfileDetailData.revenueDetail.id" elementCssClass="col-xs-12" id="revenueDetailList" cssClass="form-control"
									  list="revenueDetails" listKey="id" listValue="name" cssStyle="width:100%"  headerKey="" headerValue="--select--"  />
							</div>
						</div>
					</div>
				</div>

				<div class="row" style="margin-bottom:10px">
					<div class="col-xs-12">
						<div id="errorDiv" style="margin-bottom:-13px"></div>
					</div>
				</div>
			</div>

			<div class="modal-footer">
				<s:submit cssClass="btn btn-sm btn-primary" type="button" role="button"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
				<button type="button" class="btn btn-default" data-dismiss="modal" id="btnCancel" onclick="clearRncConfigurationDialog()"><s:text name="button.cancel" /></button>
			</div>
			</s:form>
		</div>
	</div>
</div>

<script src="${pageContext.request.contextPath}/js/RatingGroup.js" ></script>
<script>

    function validateForm_rncServiceConfigurationFormId() {
        clearErrorMessages(rncServiceConfigurationFormId);
        var regExp = new RegExp("^((\\d{0,9}(\\.\\d{0,6})?))$");
        var volumePulse = $('#pulseVolume');
        var timePulse = $('#pulseTime');
        var rateElement = $('#rateValue');
        var volumePulseValue = volumePulse.val();
        var timePulseValue = timePulse.val();
        var rateElementValue = rateElement.val();

        var balance = $("#balance");
		var volumeCarryForwardLimit = $("#volumeCarryForwardLimitModel");
		var timeCarryForwardLimit = $("#timeCarryForwardLimit");
		var errorDiv = $("#errorDiv");

		if(validatePulseValues(volumePulse, volumePulseValue) == false){
			return false;
		}

		if(validatePulseValues(timePulse, timePulseValue) == false) {
		    return false;
		}

        if(rateElementValue <0 ){
            setErrorOnElement(rateElement,"<s:text name="rnc.profile.rate.should.zero.or.more"/>");
            return false;
        } else if(regExp.test(rateElementValue) == false){
            setErrorOnElement(rateElement,"<s:text name="rnc.profile.double.length"/>");
            return false;
        }

        if(balance.val().length==0 || balance.val() == 0){
            if(volumeCarryForwardLimit.val().length > 0){
                setErrorOnElement(volumeCarryForwardLimit,"");
            }
            if(timeCarryForwardLimit.val().length > 0){
                setErrorOnElement(timeCarryForwardLimit,"");
            }
            if(volumeCarryForwardLimit.val().length > 0 || timeCarryForwardLimit.val().length > 0){
                $(".removeOnReset").remove();
                setErrorOnElement(errorDiv,"<s:text name="rnc.profile.carryForward.not.for.unlimited.balance"/>");
                $('.removeOnReset').css("margin-bottom","-10px")
                return false;
            }
        }

        return true;
    }

    function validatePulseValues(pulseElement, pulseElementValue){
        var maxValue = 999999999999999;
         if(isNullOrEmpty(pulseElementValue) == false && pulseElementValue <= 0 || pulseElementValue > maxValue) {
            setErrorOnElement(pulseElement,"Value must be between 1 to " + maxValue);
            return false;
        }
	}

    var serviceTypeRatingGroupMapJson = JSON.parse('<s:property value="%{serviceTypeRatingGroupMapJson}" escapeHtml="false"/>');
    var staffBelongingRatingGroupJson = JSON.parse('<s:property value="%{staffBelongingRatingGroupJson}" escapeHtml="false"/>');

    var selectedratingGroup;

	function clearRncConfigurationDialog(){
        $("#RncServiceConfDialog").find($("input[type='text']")).val("");
        $("#dataServiceType")[0].selectedIndex = 0;
        selectedratingGroup=null;
        setratingGroup();
	}
    function updateRncConfiguration(id){
		clearErrorMessages(rncServiceConfigurationFormId);

        var rncProfileDetailData = <%=rncProfileDetailData%>;
		for(var i in rncProfileDetailData)
		{
			var rncProfileDetailId = rncProfileDetailData[i].id;
			if(rncProfileDetailId == id){

				$("#rncProfileDetailId").val(rncProfileDetailData[i].id);
				$("#fupLevel").val(rncProfileDetailData[i].fupLevel);

				$("#dataServiceType").val(rncProfileDetailData[i].dataServiceTypeData.id);
				$("#ratingGroup").val(rncProfileDetailData[i].ratingGroupData.id);

				$("#balance").val(rncProfileDetailData[i].balance);
				$("#balanceUnit").val(rncProfileDetailData[i].balanceUnit);

				$("#timeBalance").val(rncProfileDetailData[i].timeBalance);
				$("#timeBalanceUnit").val(rncProfileDetailData[i].timeBalanceUnit);

				$("#pulseVolume").val(rncProfileDetailData[i].pulseVolume);
				$("#pulseVolumeUnit").val(rncProfileDetailData[i].pulseVolumeUnit);
				$("#pulseTime").val(rncProfileDetailData[i].pulseTime);
				$("#pulseTimeUnit").val(rncProfileDetailData[i].pulseTimeUnit);

                $("#rateValue").val(rncProfileDetailData[i].rate);
				$("#rateUnit").val(rncProfileDetailData[i].rateUnit);

				$("#dailyUsageLimitModel").val(rncProfileDetailData[i].dailyUsageLimit);
				$("#weeklyUsageLimitModel").val(rncProfileDetailData[i].weeklyUsageLimit);
				$("#usageLimitUnit").val(rncProfileDetailData[i].usageLimitUnit);

				$("#dailyTimeLimit").val(rncProfileDetailData[i].dailyTimeLimit);
				$("#weeklyTimeLimit").val(rncProfileDetailData[i].weeklyTimeLimit);
				$("#timeLimitUnit").val(rncProfileDetailData[i].timeLimitUnit);

				$("#volumeCarryForwardLimitModel").val(rncProfileDetailData[i].volumeCarryForwardLimit);
				$("#timeCarryForwardLimit").val(rncProfileDetailData[i].timeCarryForwardLimit);

                //$("#revenueDetailList option:selected").val(rncProfileDetailData[i].revenueDetail);
				if(isNullOrEmpty(rncProfileDetailData[i].revenueDetail) == false) {
                    $("#revenueDetailList").val(rncProfileDetailData[i].revenueDetail.id);
				}

                selectedratingGroup=rncProfileDetailData[i].ratingGroupData;

                if(rncProfileDetailData[i].fupLevel > '<s:property value="%{@com.elitecore.corenetvertex.constants.BalanceLevel@fromName(rncProfileData.balanceLevel).getFupLevel()}"/>'){
                    $("#volumeCarryForwardLimitModel").attr("disabled", true);
                    $("#timeCarryForwardLimit").attr("disabled", true);
                }else{
                    $("#volumeCarryForwardLimitModel").removeAttr("disabled");
                    $("#timeCarryForwardLimit").removeAttr("disabled");
                }
			}
        }

        $("#RncServiceConfDialog").modal('show');
        setratingGroup();
	}

    function setratingGroup(){
        return fillRatingGroup("ratingGroup", serviceTypeRatingGroupMapJson[$("#dataServiceType").val()], staffBelongingRatingGroupJson, selectedratingGroup);
    }

	$(document).ready(function(){
        setratingGroup();
/*
		$("#revenueDetailList").select2({
			dropdownParent: $("#RncServiceConfDialog")
		});
*/
	});

</script>