<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }

    .order-button {
        padding-top: 3px !important;
        padding-bottom: 3px !important;
    }

</style>
<script>
    var localCard = "<s:property value="@com.elitecore.corenetvertex.pd.ratecard.RateCardScope@LOCAL.name()"/>";
    function setUnlimitedTime(data, type, thisBean) {
        if (isNullOrEmpty(data)) {
            return "UNLIMITED";
        } else {
            return data + " " + thisBean.nonMonetaryRateCardData.timeUom;
        }
    }

    function setUnlimitedEvent(data, type, thisBean) {
        if (isNullOrEmpty(data)) {
            return "UNLIMITED";
        } else {
            return data + " " + thisBean.nonMonetaryRateCardData.pulseUom;
        }
    }

    function rednerPeakRateCard(data, type, thisBean){
        if(data == undefined || data == ""){
            return "";
        }
        var monetary="<s:property value="@com.elitecore.corenetvertex.constants.RateCardType@MONETARY.name()"/>";
        if(thisBean.peakRateRateCard.type == monetary){
            {
                if(thisBean.peakRateRateCard.scope == localCard) {
                    return "<a href='${pageContext.request.contextPath}/pd/monetaryratecard/monetary-rate-card/"+thisBean.peakRateRateCard.id+"'>"+data+"</a>";
                }
                else {
                    return "<a href='${pageContext.request.contextPath}/pd/globalratecard/global-rate-card/"+thisBean.peakRateRateCard.id+"'>"+data+"</a>";
                }
            }

        } else {
            return "<a href='${pageContext.request.contextPath}/pd/nonmonetaryratecard/non-monetary-rate-card/"+thisBean.peakRateRateCard.id+"'>"+data+"</a>";
        }
    }

    function rednerOffPeakRateCard(data, type, thisBean){
        if(data == undefined || data == ""){
            return "";
        }
        var monetary="<s:property value="@com.elitecore.corenetvertex.constants.RateCardType@MONETARY.name()"/>";
        if(thisBean.offPeakRateRateCard.type == monetary){
            if(thisBean.offPeakRateRateCard.scope == localCard) {
                return "<a href='${pageContext.request.contextPath}/pd/monetaryratecard/monetary-rate-card/"+thisBean.offPeakRateRateCard.id+"'>"+data+"</a>";
            }
            else {
                return "<a href='${pageContext.request.contextPath}/pd/globalratecard/global-rate-card/"+thisBean.offPeakRateRateCard.id+"'>"+data+"</a>";
            }
        } else {
            return "<a href='${pageContext.request.contextPath}/pd/nonmonetaryratecard/non-monetary-rate-card/"+thisBean.offPeakRateRateCard.id+"'>"+data+"</a>";
        }
    }

</script>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <div class="btn-group btn-group-xs">
                <button class="btn btn-primary" style="display:none" id="designMode">Design</button>
                <button class="btn btn-primary" style="display:none" id="testMode"
                        onclick="changePkgMode('Design to Test','testMode');">Test it
                </button>
                <button class="btn btn-primary" style="display:none" id="liveMode"
                        onclick="changePkgMode('Test to Live','liveMode');">Go Live
                </button>
                <button class="btn btn-primary" style="display:none" id="live2Mode"
                        onclick="changePkgMode('Live to Live2','live2Mode');">Go Live2
                </button>
            </div>
            &nbsp; <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${id}&auditPageHeadingName=${name}&refererUrl=/pd/rncpackage/rnc-package/${id}'">
                              
                    <span class="glyphicon glyphicon-eye-open"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="Clone"
                        onclick="javascript:cloneRncPackage('${id}','${name}');">
                    <span class="glyphicon glyphicon-duplicate"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/rncpackage/rnc-package/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <s:if test="%{mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()"
                  data-href="${pageContext.request.contextPath}/pd/rncpackage/rnc-package/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
            </s:if>
            <s:else>
                 <span disabled='disabled' class="btn-group btn-group-xs" data-toggle="confirmation-singleton">
			    <button disabled="disabled" type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
            </s:else>
        </div>
    </div>
    <div class="panel-body">
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail"/></legend>
                <div class="row">
                    <div class="col-sm-6">
                        <div class="row">
                            <s:label key="rncpackage.name" value="%{name}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-5" elementCssClass="col-xs-7"/>
                            <s:label key="rncpackage.description" value="%{description}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-5"
                                     elementCssClass="col-xs-7"/>
                            <s:label key="rncpackage.groups" value="%{groupNames}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-5"
                                     elementCssClass="col-xs-7"/>
                            <s:label key="rncpackage.status" value="%{status}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-5"
                                     elementCssClass="col-xs-7"/>

                            <s:if test="%{type == @com.elitecore.corenetvertex.pkg.RnCPkgType@MONETARY_ADDON.name()}">
                                <s:label key="rncpackage.type" value="%{@com.elitecore.corenetvertex.pkg.RnCPkgType@MONETARY_ADDON.val}" cssClass="control-label light-text"
                                         labelCssClass="col-xs-5"
                                         elementCssClass="col-xs-7"/>
                            </s:if>
                            <s:elseif test="%{type == @com.elitecore.corenetvertex.pkg.RnCPkgType@NON_MONETARY_ADDON.name()}">
                                <s:label key="rncpackage.type" value="%{@com.elitecore.corenetvertex.pkg.RnCPkgType@NON_MONETARY_ADDON.val}" cssClass="control-label light-text"
                                         labelCssClass="col-xs-5"
                                         elementCssClass="col-xs-7"/>
                            </s:elseif>
                            <s:else>
                                <s:label key="rncpackage.type" value="%{@com.elitecore.corenetvertex.pkg.RnCPkgType@BASE.val}" cssClass="control-label light-text"
                                         labelCssClass="col-xs-5"
                                         elementCssClass="col-xs-7"/>
                            </s:else>
                            <s:label key="rncpackage.chargingtype" value="%{chargingType}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-5"
                                     elementCssClass="col-xs-7"/>

                            <s:label key="rncpackage.currency" value="%{currency}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-5"
                                     elementCssClass="col-xs-7"/>

                        </div>
                    </div>

                    <div class="col-sm-6 leftVerticalLine">
                        <div class="row">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
                    <!--Monetary Rate Card Data -->
                    <s:if test="%{type != @com.elitecore.corenetvertex.pkg.RnCPkgType@NON_MONETARY_ADDON.name()}">
                    <div class="row">
                        <div class="panel-body">
                            <fieldset class="fieldSet-line">
                                <legend align="top"><s:text name="rncpackage.ratecard"></s:text></legend>
                                <div style="text-align: right;">
                                    <s:if test="%{mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                                        <button class="btn btn-primary btn-xs order-button"
                                                onclick="javascript:location.href='${pageContext.request.contextPath}/pd/monetaryratecard/monetary-rate-card/new?rncPackageId=${id}'">
                                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                                            <s:text name="rncpackage.ratecard"/>
                                        </button>
                                    </s:if>
                                    <s:else>
                                        <button disabled="disabled" class="btn btn-primary btn-xs order-button">
                                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                                            <s:text name="rncpackage.ratecard"/>
                                        </button>
                                    </s:else>
                                </div>
                                <nv:dataTable id="monetaryRateCardData" list="${monetaryRateCardListAsjson}"
                                              width="100%"
                                              showPagination="false" showFilter="false" showInfo="false"
                                              cssClass="table table-blue">
                                    <nv:dataTableColumn title="Name" beanProperty="name"
                                                        hrefurl="${pageContext.request.contextPath}/pd/monetaryratecard/monetary-rate-card/$<id>"
                                                        sortable="true"/>
                                    <nv:dataTableColumn title="Pulse UOM"
                                                        beanProperty="monetaryRateCardData.pulseUnit"/>
                                    <nv:dataTableColumn title="Rate UOM" beanProperty="monetaryRateCardData.rateUnit"/>
                                    <s:if test="%{mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                                        <nv:dataTableColumn title=""
                                                            icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                            hrefurl="edit:${pageContext.request.contextPath}/pd/monetaryratecard/monetary-rate-card/$<id>/edit?rncPackageId=${id}"
                                                            style="width:20px;border-right:0px;"/>
                                        <nv:dataTableColumn title=""
                                                            icon="<span class='glyphicon glyphicon-trash'></span>"
                                                            hrefurl="delete:${pageContext.request.contextPath}/pd/monetaryratecard/monetary-rate-card/$<id>?_method=DELETE&rncPackageId=${id}"
                                                            style="width:20px;"/>
                                    </s:if>
                                    <s:else>
                                        <nv:dataTableColumn title=""
                                                            icon="<span disabled='disabled' class='glyphicon glyphicon-pencil'></span>"
                                                            style="width:20px;border-right:0px;"/>
                                        <nv:dataTableColumn title=""
                                                            icon="<span disabled='disabled' class='glyphicon glyphicon-trash'></span>"
                                                            style="width:20px;"/>
                                    </s:else>
                                </nv:dataTable>
                            </fieldset>
                        </div>
                    </div>
                    </s:if>
                    <s:if test="%{type != @com.elitecore.corenetvertex.pkg.RnCPkgType@MONETARY_ADDON.name()}">
                        <!--Non Monetary Rate Card Data -->
                        <div class="row">
                            <div class="panel-body">
                                <fieldset class="fieldSet-line">
                                    <legend align="top"><s:text name="rncpackage.nonmonetaryratecard"></s:text></legend>
                                    <div style="text-align: right;">
                                        <s:if test="%{mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                                            <button class="btn btn-primary btn-xs"
                                                    style="padding-top: 3px; padding-bottom: 3px"
                                                    onclick="javascript:location.href='${pageContext.request.contextPath}/pd/nonmonetaryratecard/non-monetary-rate-card/new?rncPackageId=${id}'">
                                                <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                                                <s:text name="rncpackage.nonmonetaryratecard"/>
                                            </button>
                                        </s:if>
                                        <s:else>
                                            <button disabled="disabled" class="btn btn-primary btn-xs order-button">
                                                <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                                                <s:text name="rncpackage.nonmonetaryratecard"/>
                                            </button>
                                        </s:else>
                                    </div>
                                    <nv:dataTable id="nonMonetaryRateCardData" list="${nonMonetaryRateCardListAsjson}"
                                                  width="100%" showPagination="false" showFilter="false"
                                                  showInfo="false"
                                                  cssClass="table table-blue">
                                        <nv:dataTableColumn title="Name" beanProperty="name"
                                                            hrefurl="${pageContext.request.contextPath}/pd/nonmonetaryratecard/non-monetary-rate-card/$<id>"
                                                            sortable="true"/>
                                        <s:if test="%{chargingType == @com.elitecore.corenetvertex.pkg.ChargingType@EVENT.name()}">
                                            <nv:dataTableColumn title="Free Units"
                                                                beanProperty="nonMonetaryRateCardData.event"
                                                                />
                                        </s:if>
                                        <s:else>
                                            <nv:dataTableColumn title="Free Units"
                                                                beanProperty="nonMonetaryRateCardData.time"
                                                                renderFunction="setUnlimitedTime"/>
                                        </s:else>
                                        <nv:dataTableColumn title="Pulse"
                                                            beanProperty="nonMonetaryRateCardData.pulse,nonMonetaryRateCardData.pulseUom"/>
                                        <s:if test="%{mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                                            <nv:dataTableColumn title=""
                                                                icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                                hrefurl="edit:${pageContext.request.contextPath}/pd/nonmonetaryratecard/non-monetary-rate-card/$<id>/edit?rncPackageId=${id}"
                                                                style="width:20px;border-right:0px;"/>
                                            <nv:dataTableColumn title=""
                                                                icon="<span class='glyphicon glyphicon-trash'></span>"
                                                                hrefurl="delete:${pageContext.request.contextPath}/pd/nonmonetaryratecard/non-monetary-rate-card/$<id>?_method=DELETE&rncPackageId=${id}"
                                                                style="width:20px;"/>
                                        </s:if>
                                        <s:else>
                                            <nv:dataTableColumn title=""
                                                                icon="<span disabled='disabled' class='glyphicon glyphicon-pencil'></span>"
                                                                style="width:20px;border-right:0px;"/>
                                            <nv:dataTableColumn title=""
                                                                icon="<span disabled='disabled' class='glyphicon glyphicon-trash'></span>"
                                                                style="width:20px;"/>
                                        </s:else>
                                    </nv:dataTable>
                                </fieldset>
                            </div>
                        </div>
                    </s:if>
                    <!--Rate Card Group Data -->
                    <div class="row">
                        <div class="panel-body">
                            <fieldset class="fieldSet-line">
                                <legend align="top"><s:text name="rncpackage.ratecardgroup"></s:text></legend>

                                <div style="text-align: right;">
                                    <s:if test="%{mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                                        <button class="btn btn-primary btn-xs order-button"
                                                onclick="validateRateCard();">
                                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                                            <s:text name="rncpackage.ratecardgroup"/>
                                        </button>
                                    </s:if>
                                    <s:else>
                                        <button disabled="disabled" class="btn btn-primary btn-xs order-button">
                                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                                            <s:text name="rncpackage.ratecardgroup"/>
                                        </button>
                                    </s:else>

                                </div>
                                <nv:dataTable id="RateCardGroupDsData" list="${rateCardGroupAsJson}" width="100%"
                                              showPagination="false" showFilter="false" showInfo="false"
                                              cssClass="table table-blue RateCardGroupDsData">
                                    <nv:dataTableColumn title="Name" beanProperty="name"
                                                        hrefurl="${pageContext.request.contextPath}/pd/ratecardgroup/rate-card-group/$<id>"
                                                        sortable="true"/>
                                    <nv:dataTableColumn title="Peak Rate Card" beanProperty="peakRateRateCard.name" renderFunction="rednerPeakRateCard"/>
                                    <nv:dataTableColumn title="Off Peak Rate Card" beanProperty="offPeakRateRateCard.name" renderFunction="rednerOffPeakRateCard"/>
                                    <s:if test="%{mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                                        <nv:dataTableColumn title=""
                                                            icon="<span class='glyphicon glyphicon-pencil'></span>"
                                                            hrefurl="edit:${pageContext.request.contextPath}/pd/ratecardgroup/rate-card-group/$<id>/edit?rncPackageId=${id}"
                                                            style="width:20px;border-right:0px;"/>
                                        <nv:dataTableColumn title=""
                                                            icon="<span class='glyphicon glyphicon-trash'></span>"
                                                            hrefurl="delete:${pageContext.request.contextPath}/pd/ratecardgroup/rate-card-group/$<id>?_method=DELETE&rncPackageId=${id}"
                                                            style="width:20px;"/>
                                    </s:if>
                                    <s:else>
                                        <nv:dataTableColumn title=""
                                                            icon="<span disabled='disabled'  class='glyphicon glyphicon-pencil'></span>"
                                                            style="width:20px;border-right:0px;"/>
                                        <nv:dataTableColumn title=""
                                                            icon="<span disabled='disabled' class='glyphicon glyphicon-trash'></span>"
                                                            style="width:20px;"/>
                                    </s:else>
                                </nv:dataTable>
                                <div class="col-xs-12" id="generalError"></div>
                            </fieldset>
                        </div>
                    </div>

                    <s:if test="%{type == @com.elitecore.corenetvertex.pkg.RnCPkgType@BASE.name() ||
                    (type == @com.elitecore.corenetvertex.pkg.RnCPkgType@NON_MONETARY_ADDON.name() && chargingType == @com.elitecore.corenetvertex.pkg.ChargingType@EVENT.name())}">
                        <%@include file="/WEB-INF/content/pd/rncnotification/rnc-notification-info.jsp" %>
                    </s:if>

                    <div class="row">
                        <div class="col-xs-12 back-to-list" align="center">
                            <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel"
                                    onclick="javascript:location.href='${pageContext.request.contextPath}/pd/rncpackage/rnc-package'">
                                <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text
                                    name="button.list"/></button>
                        </div>
                    </div>
                </div>
            </fieldset>
        </div>
    </div>
</div>
<script>
    function validateRateCard() {
        var rateCardSize = '<s:text name="%{rateCardData.size}"/>';
        var isGlobalRateCardExists = <s:text name="%{globalRateCardExists}"/>;
        if (rateCardSize < 1 && isGlobalRateCardExists == false) {
            $("#generalError").addClass("bg-danger");
            $("#generalError").text("At-least One Rate Card is Required for Configuring Rate Card Group.");
        } else {
            javascript:location.href = '${pageContext.request.contextPath}/pd/ratecardgroup/rate-card-group/new?rncPackageId=${id}';
        }
    }

    $(document).ready(function () {
        setPackageMode();
    });

    function changePkgMode(fromTo, refId) {
        var classVal = $("#" + refId).attr("class");
        if (classVal.toLowerCase().indexOf("primary") >= 0) {
            return;
        }
        if (confirm("Change package mode from " + fromTo + " ?")) {
            var rncPackageId = '${id}';
            var pkgMode = '<s:property value="@com.elitecore.corenetvertex.pkg.PkgMode@getMode(mode).getNextMode()" />';
            var updateURL = "${pageContext.request.contextPath}/pd/rncpackage/rnc-package/${id}/updateMode?_method=put";
            $.ajax({
                async: true,
                type: "POST",
                url: updateURL,
                data: {
                    'rncPackageId': rncPackageId,
                    'pkgMode': pkgMode,
                    'groupIds': '<s:property value="groups"/>',
                    'entityOldGroups': '<s:property value="groups" />'
                },
                success: function (json) {
                    document.location.href = "${pageContext.request.contextPath}/pd/rncpackage/rnc-package/" + rncPackageId;
                },
                error: function (json) {
                    document.location.href = "${pageContext.request.contextPath}/pd/rncpackage/rnc-package/" + rncPackageId;
                }
            });
        }
    }

    function setPackageMode() {

        var pkgMode = '${mode}';
        console.log("pkgMode: " + pkgMode);
        $('#designMode').hide();
        $('#testMode').hide();
        $('#liveMode').hide();
        $('#live2Mode').hide();

        if (pkgMode == 'DESIGN') {
            $('#designMode').show();
            $('#designMode').html("Design");
            $('#testMode').show();
            $('#testMode').attr("class", "btn btn-default");
            $('#testMode').attr("style", "font-weight: bold;");

        } else if (pkgMode == 'TEST') {
            $('#testMode').show();
            $('#testMode').html("Test");
            $('#liveMode').show();
            $('#liveMode').attr("class", "btn btn-default");
            $('#liveMode').attr("style", "font-weight: bold;");

        } else if (pkgMode == 'LIVE') {
            $('#liveMode').show();
            $('#liveMode').html("Live");
            $('#live2Mode').show();
            $('#live2Mode').attr("class", "btn btn-default");
            $('#live2Mode').attr("style", "font-weight: bold;");

        } else if (pkgMode == 'LIVE2') {
            $('#live2Mode').show();
            $('#live2Mode').html("Live2");
        }
    }
    function cloneRncPackage(id,name) {
        $("#rncNotificationId").val(id);
        $("#duplicateEntityName").val("CopyOf" + name);
        $('#rncPackageCloningForm').attr('action', "${pageContext.request.contextPath}/pd/rncpackage/rnc-package/"+id+"/copymodel");
        $("#rncPackageCloningDialog").modal('show');
        $("#duplicateEntityName").focus();
        $("#method").val("post");
    }

</script>

<%@include file="rnc-duplicate-dialog.jsp"%>