<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
</style>

<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <div class="btn-group btn-group-xs">
                <button class="btn btn-primary" style="display:none"  	id="designMode">Design</button>
                <button class="btn btn-primary" style="display:none"  	id="testMode" 		onclick="changePkgMode('Design to Test','testMode');" >Test it</button>
                <button class="btn btn-primary" style="display:none"  	id="liveMode" 		onclick="changePkgMode('Test to Live','liveMode');">Go Live</button>
                <button class="btn btn-primary" style="display:none"  	id="live2Mode" 		onclick="changePkgMode('Live to Live2','live2Mode');" >Go Live2</button>
            </div>
            &nbsp; <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="<s:text name="audit.history"/>"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${id}&auditPageHeadingName=<s:property value="name"/>&refererUrl=/pd/datatopup/data-topup/${id}'">
					<span class="glyphicon glyphicon-eye-open"></span>
				</button>
			</span>
            <span class="btn-group btn-group-xs">
					<button type="button" class="btn btn-default header-btn" data-toggle="tooltip"
                            data-placement="bottom" title="<s:text name="tooltip.edit"/>"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/pd/datatopup/data-topup/${id}/edit'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
            <s:if test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/datatopup/data-topup/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" disabled="disabled" data-toggle="tooltip" data-placement="bottom" title="<s:text name="tooltip.delete"/>" >
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
            </s:if>
            <s:else>
             <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/datatopup/data-topup/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="<s:text name="tooltip.delete"/>" >
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
            </s:else>
        </div>
    </div>
    <div class="panel-body">
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top">
                    <s:text name="basic.detail"/>
                </legend>
                <div class="row">
                <div class="col-sm-4">
                    <div class="row">
                        <s:label key="data.topup.description" value="%{description}"
                                 cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4"
                                 elementCssClass="col-sm-7 col-xs-8"/>
                        <s:label key="data.topup.status" value="%{status}" cssClass="control-label light-text"
                                 labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                        <s:label key="data.topup.type"
                                 value="%{@com.elitecore.corenetvertex.pd.topup.TopUpType@valueOf(topupType).getVal()}"
                                 cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4"
                                 elementCssClass="col-sm-7 col-xs-8"/>
                        <s:set var="priceTag">
                            <s:property value="getText('data.topup.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
                        </s:set>
                        <s:label key="priceTag" value="%{price}"
                                 cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4"
                                 elementCssClass="col-sm-7 col-xs-8"/>
                        <s:label key="data.topup.groups" value="%{groupNames}" cssClass="control-label light-text"
                                 labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                    </div>
                </div>
                <div class="col-sm-4 leftVerticalLine">
                    <div class="row">
                        <s:label key="data.topup.multiple.subscription"
                                 value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(multipleSubscription).getStringName()}"
                                 cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                 elementCssClass="col-xs-8 col-sm-7"/>
                        <s:if test="%{validityPeriod != null && validityPeriod != 0}">
                            <s:label key="data.topup.validity.period" value="%{validityPeriod}  %{validityPeriodUnit}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                     elementCssClass="col-xs-8 col-sm-7"/>
                        </s:if>
                        <s:else>
                            <s:label key="data.topup.validity.period"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                     elementCssClass="col-xs-8 col-sm-7"/>
                        </s:else>
                        <s:label key="Param 1" value="%{param1}" cssClass="control-label light-text"
                                 labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                        <s:label key="Param 2" value="%{param2}" cssClass="control-label light-text"
                                 labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                    </div>
                </div>
                <div class="col-sm-4 leftVerticalLine">
                    <div class="row">
                        <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                    </div>
                </div>
                </div>
            </fieldset>
        </div>
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top">
                    <s:text name="data.topup.applicable.pcc.profiles"/>
                </legend>
                <div class="row">
                    <div class="col-sm-12 col-xs-12">
                        <div class="row">
                            <s:label key="data.topup.applicable.pcc.profiles.names" value="%{applicablePCCProfiles}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-2 col-sm-2" elementCssClass="col-xs-10 col-sm-10"/>
                        </div>
                    </div>
                </div>
            </fieldset>
        </div>

        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top">
                    <s:text name="data.topup.quota.information"/>
                </legend>
                    <div class="row">
                        <div class="col-sm-4 col-xs-12">
                            <div class="row">
                                    <s:label key="data.topup.quotatype" value="%{quotaType}" cssClass="control-label light-text"
                                             labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                            </div>
                        </div>
                            <div class="col-sm-4 col-xs-12 leftVerticalLine">
                                <div class="row">
                                    <s:label key="data.topup.unittype" value="%{unitType}" cssClass="control-label light-text"
                                             labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                                </div>
                            </div>
                        <div class="col-sm-4 col-xs-12 leftVerticalLine">
                            <div class="row">
                                <s:if test="%{quotaType == @com.elitecore.corenetvertex.pd.topup.TopUpQuotaType@VOLUME.name()}">
                                    <s:label key="data.topup.volume.balance" value="%{volumeBalance} %{volumeBalanceUnit}" cssClass="control-label light-text"
                                         labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                                </s:if>
                                <s:else>
                                    <s:label key="data.topup.time.balance" value="%{timeBalance} %{timeBalanceUnit}" cssClass="control-label light-text"
                                             labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                                </s:else>
                            </div>
                        </div>
                    </div>
            </fieldset>
        </div>
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top">
                    <s:text name="data.topup.notification"/>
                </legend>
                 <s:if test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                <div style=" text-align: right; margin-bottom: 10px;">
                    <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" disabled="disabled"
                            onclick="$('#topUpNotificationDialog').modal('show');">
                        <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                        <s:text name="data.topup.notification.add"/>
                    </button>
                </div>
                 </s:if>
                <s:else>
                    <div style=" text-align: right; margin-bottom: 10px;">
                        <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"
                                onclick="openNotificationDialogue();">
                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                            <s:text name="data.topup.notification.add"/>
                        </button>
                    </div>
                </s:else>

                <div class="dataTables_wrapper form-inline dt-bootstrap no-footer">
                    <table class="table table-blue" id="notificationTable">
                        <thead>
                        <th><s:text name="data.topup.notification.email.template"/></th>
                        <th><s:text name="data.topup.notification.sms.template"/></th>
                        <th><s:text name="data.topup.notification.threshold"/></th>
                        <th>&nbsp;</th>
                        <th>&nbsp;</th>
                        </thead>
                        <tbody>
                        <s:if test="%{topUpNotificationList != null && topUpNotificationList.isEmpty() == false}">
                        <s:iterator value="topUpNotificationList">
                            <tr>
                                <td class="emailTemplateIds">
                                    <a href="${pageContext.request.contextPath}/policydesigner/notification/NotificationTemplate/view?notificationTemplateId=${emailTemplateData.id}"><s:property value="emailTemplateData.name"/></a><s:hidden value="%{emailTemplateData.id}"/><s:hidden value="%{id}"/> </td>
                                <td class="smsTemplateIds">
                                    <a href="${pageContext.request.contextPath}/policydesigner/notification/NotificationTemplate/view?notificationTemplateId=${smsTemplateData.id}"><s:property value="smsTemplateData.name"/></a><s:hidden value="%{smsTemplateData.id}"/></td>
                                <td class="thresholdValues"><s:property value="threshold"/></td>
                                <s:if test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                                    <td style='width:35px;opacity:0.50;'>
                                        <span disabled="disabled" class="glyphicon glyphicon-pencil"></span>
                                    </td>
                                </s:if>
                                <s:else>
                                    <td style='width:35px;'>
                                        <a style="cursor:pointer"
                                           href="javascript:updateNotification('${emailTemplateId}','${smsTemplateId}','${threshold}','${id}');"><span
                                                class="glyphicon glyphicon-pencil"></span></a>
                                    </td>
                                </s:else>
                                <s:if test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                                        <td style='width:35px;opacity: 0.50;'>
                                                          <span disabled="disabled" class="glyphicon glyphicon-trash"></span>
                                        </td>
                                </s:if>
                                 <s:else>
                                        <td style='width:35px;'>
                                            <span  data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/topupnotification/topup-notification/${id}?_method=DELETE&dataTopUpId=${dataTopUpData.id}">
                                                <a style="cursor:pointer"><span class="glyphicon glyphicon-trash"></span></a>
                                            </span>
                                        </td>
                                    </s:else>
                            </tr>
                        </s:iterator>
                        </s:if>
                        <s:else>
                            <tr>
                               <td colspan="5"><s:text name="empty.table.rows.message"/></td>
                            </tr>
                        </s:else>
                        </tbody>
                    </table>

                </div>
            </fieldset>
        </div>
        <div class="row">
            <div class="col-xs-12" align="center">
                <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/datatopup/data-topup'">
                    <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text
                        name="button.list"/></button>
            </div>
        </div>
    </div>
</div>
<script>

$(document).ready(function() {
        setPackageMode();
    //    addRequired(document.getElementById("threshold"));
    });
    function setPackageMode() {
        var pkgMode = '${packageMode}';
        console.log("pkgMode: "+pkgMode);
        $('#designMode').hide();
        $('#testMode').hide();
        $('#liveMode').hide();
        $('#live2Mode').hide();

        if (pkgMode == 'DESIGN') {
            $('#designMode').show();
            $('#designMode').html("Design");
            $('#testMode').show();
            $('#testMode').attr("class", "btn btn-default");
            $('#testMode').attr("style","font-weight: bold;");

        }else if (pkgMode == 'TEST') {
            $('#testMode').show();
            $('#testMode').html("Test");
            $('#liveMode').show();
            $('#liveMode').attr("class", "btn btn-default");
            $('#liveMode').attr("style","font-weight: bold;");

        }else if (pkgMode == 'LIVE') {
            $('#liveMode').show();
            $('#liveMode').html("Live");
            $('#live2Mode').show();
            $('#live2Mode').attr("class", "btn btn-default");
            $('#live2Mode').attr("style","font-weight: bold;");

        }else if (pkgMode == 'LIVE2') {
            $('#live2Mode').show();
            $('#live2Mode').html("Live2");
        }
    }


    function changePkgMode(fromTo,refId) {
        var classVal = $("#"+refId).attr("class");
        if(classVal.toLowerCase().indexOf("primary") >=0){
            return;
        }

        if(confirm("Change package mode from "+fromTo+" ?")){
            var pkgId = '${id}';
            var pkgMode = '${packageMode}';
            var updateURL= "${pageContext.request.contextPath}/pd/datatopup/data-topup/"+pkgId+"/updateMode?_method=put";
            $.ajax({
                async       : true,
                type 		: "POST",
                url  		: updateURL,
                data		: {'pkgId' : pkgId , 'pkgMode' : pkgMode,'groupIds':'<s:property value="groups"/>', 'entityOldGroups' : '<s:property value="groups" />'},
                success 	: function(json){
                    document.location.href = "${pageContext.request.contextPath}/pd/datatopup/data-topup/"+pkgId;
                },
                error: function (json) {
                    document.location.href = "${pageContext.request.contextPath}/pd/datatopup/data-topup/"+pkgId;
                }
            });
        }
    }
function openNotificationDialogue() {
    addRequired(document.getElementById("threshold"));
    $("#threshold").val("");
    $('#topUpNotificationDialog').modal('show');
    clearErrorMessages();
}
</script>
<%@include file="data-topup-notification-modal.jsp"%>
