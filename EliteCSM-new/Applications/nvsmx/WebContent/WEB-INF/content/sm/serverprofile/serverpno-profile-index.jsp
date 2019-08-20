<%@taglib uri="/struts-tags/ec" prefix="s"%>

<style type="text/css">
.form-group {
	width: 100%;
	display: table;
	margin-bottom: 2px;
}
</style>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
		    <s:text name="server.profile.view"/>
		</h3>
		<div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				
			  <s:if test="#session.tabType == @com.elitecore.corenetvertex.constants.ServerGroups@PCC.getValue()">
			    <button type="button" class="btn btn-default header-btn"
					data-toggle="tooltip" data-placement="bottom" title="Audit History"
					onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=ServerProfile&refererUrl=/sm/serverprofile/server-profile/${id}'">
					<span class="glyphicon glyphicon-eye-open"></span>
				</button>
			  
			  </s:if>
				
			 <%-- <s:if test="#session.tabType == @com.elitecore.corenetvertex.constants.ServerGroups@OFFLINE_RNC.getValue()">
			     <button type="button" class="btn btn-default header-btn"
					data-toggle="tooltip" data-placement="bottom" title="Audit History"
					onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=OfflineRnce&refererUrl=/sm/serverprofile/offline-rnc/${id}'">
					<span class="glyphicon glyphicon-eye-open"></span>
				</button>
			  </s:if>--%>
			</span> <span class="btn-group btn-group-xs">
			  <s:if test="#session.tabType == @com.elitecore.corenetvertex.constants.ServerGroups@PCC.getValue()">
			  <button type="button" class="btn btn-default header-btn"
					data-toggle="tooltip" data-placement="bottom" title="edit"
					onclick="javascript:location.href='${pageContext.request.contextPath}/sm/serverprofile/server-profile/${id}/edit'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			  
			  </s:if>
			  
			  <%--<s:if test="#session.tabType == @com.elitecore.corenetvertex.constants.ServerGroups@OFFLINE_RNC.getValue()">
			  <button type="button" class="btn btn-default header-btn"
					data-toggle="tooltip" data-placement="bottom" title="edit"
					onclick="javascript:location.href='${pageContext.request.contextPath}/sm/serverprofile/offline-rnc/${id}/edit'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			  
			  </s:if>--%>
			</span> <span class="btn-group btn-group-xs"
				data-toggle="confirmation-singleton" onmousedown="" data-href="">
				<button type="button" class="btn btn-default header-btn"
					disabled="disabled" data-toggle="tooltip" data-placement="bottom"
					title="delete">
					<span class="glyphicon glyphicon-trash"></span>
				</button>
			</span>
		</div>
	</div>

	<div class="panel-body">
		<div class="row">
			<div class="container-fluid">
				<ul class="nav nav-tabs tab-headings" id="tabs">
					<li class="active" id="pccTab">
						<a data-toggle="tab" href="#pccserverprofilesection"><s:text name="server.profile.pcc" /></a></li>
				<%--	<li id="OfflineRnCTab">
						<a data-toggle="tab" href="#offlinerncserverprofilesection" onclick="offlineRncServerProfileShow()" role="button"><s:text
					 	name="server.profile.offlinernc" /></a>
					</li>
				--%></ul>
				<div class="tab-content">
					<div id="pccserverprofilesection" class="tab-pane fade in">
					
					
					 <s:form id="pccServerProfile" method="get" cssClass="form-vertical">
						<div>
							<fieldset class="fieldSet-line">
								<legend>
									<s:text name="serverprofile.pcrfService" />
								</legend>
								<div>

									<div class="col-xs-12 col-sm-6 col-lg-6">
										<s:label value="%{pcrfServiceMinThreads}"
											key="serverprofile.pcrfServiceMinThreads"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

										<s:label value="%{pcrfServiceQueueSize}"
											key="serverprofile.pcrfServiceQueueSize"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

									</div>

									<div class="col-xs-12 col-sm-6 col-lg-6">

										<s:label value="%{pcrfServiceMaxThreads}"
											key="serverprofile.pcrfServiceMaxThreads"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

										<s:label value="%{pcrfServiceWorkerThreadPriority}"
											key="serverprofile.pcrfServiceWorkerThreadPriority"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

									</div>
								</div>
							</fieldset>
						</div>
						<div>
							<fieldset class="fieldSet-line">
								<legend>
									<s:text name="serverprofile.loggig" />
								</legend>
								<div class="row">
									<div class="col-xs-12 col-sm-6 col-lg-6">

										<s:label value="%{logLevel}" key="serverprofile.logLevel"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

										<s:if
											test="rollingType==@com.elitecore.corenetvertex.constants.RollingType@TIME_BASED.value">
											<s:label
												value="%{@com.elitecore.corenetvertex.constants.RollingType@TIME_BASED.label}"
												key="serverprofile.rollingType"
												cssClass="control-label light-text word-break"
												labelCssClass="col-sm-5 col-xs-4"
												elementCssClass="col-sm-7 col-xs-8" />
										</s:if>
										<s:if
											test="rollingType==@com.elitecore.corenetvertex.constants.RollingType@SIZE_BASED.value">
											<s:label
												value="%{@com.elitecore.corenetvertex.constants.RollingType@SIZE_BASED.label}"
												key="serverprofile.rollingType"
												cssClass="control-label light-text word-break"
												labelCssClass="col-sm-5 col-xs-4"
												elementCssClass="col-sm-7 col-xs-8" />
										</s:if>
									</div>
									<div class="col-xs-12 col-sm-6 col-lg-6">

										<s:label value="%{maxRolledUnits}"
											key="serverprofile.maxRolledUnits"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

										<s:if
											test="rollingType==@com.elitecore.corenetvertex.constants.RollingType@TIME_BASED.value">
											<s:iterator
												value="@com.elitecore.corenetvertex.constants.TimeBasedRollingUnit@values()">

												<s:if test="%{value == rollingUnits}">
													<s:label value="%{unit}" key="serverprofile.rollingUnits"
														cssClass="control-label light-text word-break"
														labelCssClass="col-sm-5 col-xs-4"
														elementCssClass="col-sm-7 col-xs-8" />
												</s:if>

											</s:iterator>
										</s:if>

										<s:if
											test="rollingType==@com.elitecore.corenetvertex.constants.RollingType@SIZE_BASED.value">
											<s:label value="%{rollingUnits}"
												key="serverprofile.rollingsizeKb"
												cssClass="control-label light-text word-break"
												labelCssClass="col-sm-5 col-xs-4"
												elementCssClass="col-sm-7 col-xs-8" />
										</s:if>

									</div>
								</div>
							</fieldset>
						</div>

						<div>
							<fieldset class="fieldSet-line">
								<legend>
									<s:text name="serverprofile.notification.services" />
								</legend>
								<div class="row">
									<div class="col-xs-12 col-sm-6 col-lg-6">

										<s:label value="%{notificationServiceExecutionPeriod}"
											key="serverprofile.notificationServiceExecutionPeriod"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

										<s:label value="%{batchSize}" key="serverprofile.batchSize"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />
									</div>

									<div class="col-xs-12 col-sm-6 col-lg-6">

										<s:label value="%{maxParallelExecution}"
											key="serverprofile.maxParallelExecution"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />
									</div>


								</div>
							</fieldset>
						</div>

						<div>
							<fieldset class="fieldSet-line">
								<legend>
									<s:text name="serverprofile.diameter.stack" />
								</legend>
								<div class="row">
									<div class="col-xs-12 col-sm-6 col-lg-6">


										<s:label
											value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(diameterDuplicateReqCheckEnabled).stringName}"
											key="serverprofile.diameterDuplicateReqCheckEnabled"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />


										<s:label value="%{diameterMinThreads}"
											key="serverprofile.diameterMinThreads"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

										<s:label value="%{diameterSessionTimeout}"
											key="serverprofile.diameterSessionTimeout"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

										<s:label value="%{diameterQueueSize}"
											key="serverprofile.diameterQueueSize"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

									</div>


									<div class="col-xs-12 col-sm-6 col-lg-6">


										<s:label value="%{diameterDuplicateReqPurgeInterval}"
											key="serverprofile.diameterDuplicateReqPurgeInterval"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />



										<s:label value="%{diameterMaxThreads}"
											key="serverprofile.diameterMaxThreads"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />


										<s:label value="%{diameterSessionCleanupInterval}"
											key="serverprofile.diameterSessionCleanupInterval"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />


									</div>

									<div class="col-xs-12 col-sm-6 col-lg-6">
										<s:label value="%{diameterDwInterval}"
											key="serverprofile.diameterDwInterval"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

									</div>

								</div>
							</fieldset>
						</div>

						<div>
							<fieldset class="fieldSet-line">
								<legend>
									<s:text name="serverprofile.radius.listener" />
								</legend>
								<div class="row">
									<div class="col-xs-12 col-sm-6 col-lg-6">

										<s:label
											value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(radiusDuplicateReqCheckEnabled).stringName}"
											key="serverprofile.radiusDuplicateReqCheckEnabled"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

										<s:label value="%{radiusMinThreads}"
											key="serverprofile.radiusMinThreads"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

										<s:label value="%{radiusQueueSize}"
											key="serverprofile.radiusQueueSize"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

									</div>

									<div class="col-xs-12 col-sm-6 col-lg-6">

										<s:label value="%{radiusDuplicateReqPurgeInterval}"
											key="serverprofile.radiusDuplicateReqPurgeInterval"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

										<s:label value="%{radiusMaxThreads}"
											key="serverprofile.radiusMaxThreads"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

									</div>
								</div>
							</fieldset>
						</div>


                       </s:form>

					</div>
<%--

					<div id="offlinerncserverprofilesection" class="tab-pane fade in">
 						<s:form id="offlineRncServerProfile" method="get" action="radius-gateway" cssClass="form-vertical">

							<div style="margin-top:1%;">
							<fieldset class="fieldSet-line">
								<legend>
									<s:text name="offlineserverprofile.offline.service" />
								</legend>
								<div class="row">

									<div class="col-xs-12 col-sm-6 col-lg-6">
										<s:label value="%{minThread}"
										key="offlineserverprofile.offlinernc.minthread"
										cssClass="control-label light-text word-break"
										labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />


									<s:label value="%{maxThread}"
										key="offlineserverprofile.offlinernc.maxthread"
										cssClass="control-label light-text word-break"
										labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />


									<s:label value="%{threadPriority}"
										key="offlineserverprofile.offlinernc.threadpriority"
										cssClass="control-label light-text word-break"
										labelCssClass="col-sm-5 col-xs-4"
										elementCssClass="col-sm-7 col-xs-8" />

									</div>

									<div class="col-xs-12 col-sm-6 col-lg-6">

										<s:label value="%{fileBatchSize}"
										key="offlineserverprofile.offlinernc.filebatchsize"
										cssClass="control-label light-text word-break"
										labelCssClass="col-sm-5 col-xs-4"
										elementCssClass="col-sm-7 col-xs-8" />


									<s:label value="%{fileBatchQueue}"
										key="offlineserverprofile.offlinernc.filebatchqueue"
										cssClass="control-label light-text word-break"
										labelCssClass="col-sm-5 col-xs-4"
										elementCssClass="col-sm-7 col-xs-8" />




									<s:label value="%{scanInterval}"
										key="offlineserverprofile.offlinernc.scaninterval"
										cssClass="control-label light-text word-break"
										labelCssClass="col-sm-5 col-xs-4"
										elementCssClass="col-sm-7 col-xs-8" />

									</div>
								</div>
							</fieldset>
						</div>


						<div>
							<fieldset class="fieldSet-line">
								<legend>
									<s:text name="offlineserverprofile.offlinernc.logging" />
								</legend>
								<div class="row">
									<div class="col-xs-12 col-sm-6 col-lg-6">

										<s:label value="%{logLevel}" key="offlineserverprofile.offlinernc.loglevel"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

										<s:if
											test="rollingType==@com.elitecore.corenetvertex.constants.RollingType@TIME_BASED.value">
											<s:label
												value="%{@com.elitecore.corenetvertex.constants.RollingType@TIME_BASED.label}"
												key="offlineserverprofile.offlinernc.rollingtype"
												cssClass="control-label light-text word-break"
												labelCssClass="col-sm-5 col-xs-4"
												elementCssClass="col-sm-7 col-xs-8" />
										</s:if>
										<s:if
											test="rollingType==@com.elitecore.corenetvertex.constants.RollingType@SIZE_BASED.value">
											<s:label
												value="%{@com.elitecore.corenetvertex.constants.RollingType@SIZE_BASED.label}"
												key="offlineserverprofile.offlinernc.rollingtype"
												cssClass="control-label light-text word-break"
												labelCssClass="col-sm-5 col-xs-4"
												elementCssClass="col-sm-7 col-xs-8" />
										</s:if>
									</div>
									<div class="col-xs-12 col-sm-6 col-lg-6">

										<s:label value="%{maxRolledUnits}"
											key="offlineserverprofile.offlinernc.maxrolledunits"
											cssClass="control-label light-text word-break"
											labelCssClass="col-sm-5 col-xs-4"
											elementCssClass="col-sm-7 col-xs-8" />

										<s:if
											test="rollingType==@com.elitecore.corenetvertex.constants.RollingType@TIME_BASED.value">
											<s:iterator
												value="@com.elitecore.corenetvertex.constants.TimeBasedRollingUnit@values()">

												<s:if test="%{value == rollingUnits}">
													<s:label value="%{unit}" key="offlineserverprofile.offlinernc.rollingunits"
														cssClass="control-label light-text word-break"
														labelCssClass="col-sm-5 col-xs-4"
														elementCssClass="col-sm-7 col-xs-8" />
												</s:if>

											</s:iterator>
										</s:if>

										<s:if
											test="rollingType==@com.elitecore.corenetvertex.constants.RollingType@SIZE_BASED.value">
											<s:label value="%{rollingUnits}"
												key="offlineserverprofile.offlinernc.rollingsizeinkb"
												cssClass="control-label light-text word-break"
												labelCssClass="col-sm-5 col-xs-4"
												elementCssClass="col-sm-7 col-xs-8" />
										</s:if>

									</div>
								</div>
							</fieldset>
						</div>
						</s:form>
					</div>
				</div>
--%>
			</div>
		</div>
	</div>
</div>



<script type="text/javascript">
	function pccServerProfileShow() {
		document.forms["pccServerProfile"].action = "${pageContext.request.contextPath}/sm/serverprofile/server-profile/*";
		document.forms["pccServerProfile"].submit();
	}
	function offlineRncServerProfileShow() {
		document.forms["offlineRncServerProfile"].action = "${pageContext.request.contextPath}/sm/serverprofile/offline-rnc/*";
		document.forms["offlineRncServerProfile"].submit();
	}
	$(document)
		.ready(
			function() {
				if ('<s:property value="%{#session.tabType}"/>' == '<s:property value="@com.elitecore.corenetvertex.constants.ServerGroups@PCC.getValue()"/>') {
					$("#pccTab").attr("class", "active");
					$("#OfflineRnCTab").removeAttr("class");
					$("#pccserverprofilesection").attr("class",
							"tab-pane fade in active");
				} else if ('<s:property value="%{#session.tabType}"/>' == '<s:property value="@com.elitecore.corenetvertex.constants.ServerGroups@OFFLINE_RNC.getValue()"/>') {
					$("#OfflineRnCTab").attr("class", "active");
					$("#pccTab").removeAttr("class");
					$("#offlinerncserverprofilesection").attr("class",
							"tab-pane fade in active");
				}
		});
</script>

