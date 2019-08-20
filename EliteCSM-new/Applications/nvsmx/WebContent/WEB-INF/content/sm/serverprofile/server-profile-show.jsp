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
            <s:text name="serverprofile.view" />
        </h3>
        <div class="nv-btn-group" align="right">
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=ServerProfile&refererUrl=/sm/serverprofile/server-profile/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
					<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/serverprofile/server-profile/${id}/edit'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="" data-href="">
			    <button type="button" class="btn btn-default header-btn" disabled="disabled" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>

    <div class="panel-body">

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="serverprofile.pcrfService"/> </legend>
                    <div class="row">

                        <div class="col-xs-12 col-sm-6 col-lg-6">
                             <s:label value="%{pcrfServiceMinThreads}" key="serverprofile.pcrfServiceMinThreads"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:label value="%{pcrfServiceQueueSize}" key="serverprofile.pcrfServiceQueueSize"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                        </div>

                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:label value="%{pcrfServiceMaxThreads}" key="serverprofile.pcrfServiceMaxThreads"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:label value="%{pcrfServiceWorkerThreadPriority}" key="serverprofile.pcrfServiceWorkerThreadPriority"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                        </div>
                    </div>
                </fieldset>
            </div>


            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="serverprofile.loggig"/> </legend>
                    <div class="row" >
                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:label value="%{logLevel}"
                                     key="serverprofile.logLevel"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:if test="rollingType==@com.elitecore.corenetvertex.constants.RollingType@TIME_BASED.value">
                                <s:label value="%{@com.elitecore.corenetvertex.constants.RollingType@TIME_BASED.label}"
                                         key="serverprofile.rollingType"
                                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                            </s:if>
                            <s:if test="rollingType==@com.elitecore.corenetvertex.constants.RollingType@SIZE_BASED.value">
                                <s:label value="%{@com.elitecore.corenetvertex.constants.RollingType@SIZE_BASED.label}"
                                         key="serverprofile.rollingType"
                                         cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                            </s:if>
                        </div>
                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:label value="%{maxRolledUnits}" key="serverprofile.maxRolledUnits"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:if test="rollingType==@com.elitecore.corenetvertex.constants.RollingType@TIME_BASED.value">
                                <s:iterator value="@com.elitecore.corenetvertex.constants.TimeBasedRollingUnit@values()" >

                                    <s:if test="%{value == rollingUnits}">
                                        <s:label value="%{unit}"   key="serverprofile.rollingUnits"
                                             cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                                    </s:if>

                                </s:iterator>
                            </s:if>

                            <s:if test="rollingType==@com.elitecore.corenetvertex.constants.RollingType@SIZE_BASED.value">
                                <s:label value="%{rollingUnits}"   key="serverprofile.rollingsizeKb"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                            </s:if>

                        </div>
                    </div>
                </fieldset>
            </div>

          <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="serverprofile.notification.services"/> </legend>
                    <div class="row" >
                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:label value="%{notificationServiceExecutionPeriod}" key="serverprofile.notificationServiceExecutionPeriod"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:label value="%{batchSize}" key="serverprofile.batchSize"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                        </div>

                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:label value="%{maxParallelExecution}" key="serverprofile.maxParallelExecution"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                        </div>


                    </div>
                </fieldset>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend> <s:text name="serverprofile.diameter.stack"/> </legend>
                    <div class="row" >
                        <div class="col-xs-12 col-sm-6 col-lg-6">


                            <s:label value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(diameterDuplicateReqCheckEnabled).stringName}"
                                     key="serverprofile.diameterDuplicateReqCheckEnabled"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8" />


                            <s:label value="%{diameterMinThreads}" key="serverprofile.diameterMinThreads"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:label value="%{diameterSessionTimeout}" key="serverprofile.diameterSessionTimeout"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:label value="%{diameterQueueSize}" key="serverprofile.diameterQueueSize"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                        </div>


                        <div class="col-xs-12 col-sm-6 col-lg-6">


                            <s:label value="%{diameterDuplicateReqPurgeInterval}" key="serverprofile.diameterDuplicateReqPurgeInterval"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>



                            <s:label value="%{diameterMaxThreads}" key="serverprofile.diameterMaxThreads"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>


                            <s:label value="%{diameterSessionCleanupInterval}" key="serverprofile.diameterSessionCleanupInterval"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>


                        </div>

                        <div class="col-xs-12 col-sm-6 col-lg-6">
                            <s:label value="%{diameterDwInterval}" key="serverprofile.diameterDwInterval"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                        </div>

                    </div>
                </fieldset>
            </div>

             <div class="row">
                <fieldset class="fieldSet-line">
                    <legend>  <s:text name="serverprofile.radius.listener"/> </legend>
                    <div class="row" >
                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:label value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(radiusDuplicateReqCheckEnabled).stringName}"
                                     key="serverprofile.radiusDuplicateReqCheckEnabled"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:label value="%{radiusMinThreads}" key="serverprofile.radiusMinThreads"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:label value="%{radiusQueueSize}"     key="serverprofile.radiusQueueSize"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                        </div>

                        <div class="col-xs-12 col-sm-6 col-lg-6">

                            <s:label value="%{radiusDuplicateReqPurgeInterval}" key="serverprofile.radiusDuplicateReqPurgeInterval"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                            <s:label value="%{radiusMaxThreads}" key="serverprofile.radiusMaxThreads"
                                     cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                        </div>
                    </div>
                </fieldset>
            </div>

    </div>
</div>

