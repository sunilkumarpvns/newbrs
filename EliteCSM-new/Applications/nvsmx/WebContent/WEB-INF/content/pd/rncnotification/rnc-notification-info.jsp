<script>
    var rncNotificationId ;
    function updateRncNotification(id,rateCardId,emailTemplateId,smsTemplateId,threshold) {
        clearErrorMessages();
        rncNotificationId = id;
        if(isNullOrEmpty(emailTemplateId)){
            emailTemplateId = "";
        }
        if(isNullOrEmpty(smsTemplateId)){
            smsTemplateId = "";
        }
       $("#rncNotificationId").val(id);
        $("#rateCardId").val(rateCardId);
        $("#emailTemplateId").val(emailTemplateId);
        $("#smsTemplateId").val(smsTemplateId);
        $("#thresholdId").val(threshold);
        $('#rncNotificationForm').attr('action', "${pageContext.request.contextPath}/pd/rncnotification/rnc-notification/"+id);
        $("#rncNotificationDialog").modal('show');
        $("#method").val("put");

    }

    function updateNotificationInfo(data,type,thisBean){
        var notificationFunction = "javascript:updateRncNotification('"+thisBean.id+"','"+thisBean.rateCardId+"','"+thisBean.emailTemplateId+"','"+thisBean.smsTemplateId+"','"+thisBean.threshold+"');"
        return "<a style='cursor:pointer' href="+notificationFunction+"><span class='glyphicon glyphicon-pencil'></span></a>";
    }
    function clearDialog() {
        clearErrorMessages(rncNotificationForm);
        $("#emailTemplateId").val("");
        $("#smsTemplateId").val("");
        $("#thresholdId").val("");
    }

    function renderEmail(data, type, thisBean){
        if(data == undefined || data == ""){
            return "";
        }
        return "<a href='${pageContext.request.contextPath}/policydesigner/notification/NotificationTemplate/view?notificationTemplateId="+thisBean.emailTemplateData.id+"'>"+data+"</a>";
    }
    function renderSMS(data, type, thisBean){
        if(data == undefined || data == ""){
            return "";
        }
        return "<a href='${pageContext.request.contextPath}/policydesigner/notification/NotificationTemplate/view?notificationTemplateId="+thisBean.smsTemplateData.id+"'>"+data+"</a>";
    }
    function renderNonMonetaryRateCard(data, type, thisBean) {
        return "<a href='${pageContext.request.contextPath}/pd/nonmonetaryratecard/non-monetary-rate-card/"+thisBean.rateCardData.id+"'>"+data+"</a>";
    }
</script>
<div class="row">
    <div class="panel-body">
    <fieldset class="fieldSet-line">
        <legend align="top"><s:text name="rncpackage.notification" /> </legend>
        <div class="btn-group" style="float: right">
            <s:if test="%{mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                <button class="btn btn-primary btn-xs dropdown-toggle" style="padding-top: 3px; padding-bottom: 3px" data-toggle="modal" data-target="#rncNotificationDialog"
                        aria-haspopup="true" aria-expanded="false" >
                    <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                    <s:text name="rncpackage.notification"/>
                </button>
            </s:if>
            <s:else>
                <button disabled="disabled" class="btn btn-primary btn-xs dropdown-toggle" style="padding-top: 3px; padding-bottom: 3px" data-toggle="modal" data-target="#rncNotificationDialog"
                        aria-haspopup="true" aria-expanded="false" >
                    <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                    <s:text name="rncpackage.notification"/>
                </button>
            </s:else>
            <%@include file="/WEB-INF/content/pd/rncnotification/rnc-notification-dialog.jsp" %>
        </div>
        <nv:dataTable
                id="rncNotificationData"
                list="${rncNotificationListAsJson}"
                width="100%"
                showPagination="false"
                showInfo="false"
                cssClass="table table-blue">
            <nv:dataTableColumn title="Non-Monetary Rate Card" beanProperty="rateCardData.name" tdCssClass="text-left word-break" tdStyle="min-width:50px" cssClass="word-break" renderFunction="renderNonMonetaryRateCard"/>
            <nv:dataTableColumn title="Email Template" beanProperty="emailTemplateData.name" tdCssClass="text-left text-middle word-break" cssClass="word-break" renderFunction="renderEmail"/>
            <nv:dataTableColumn title="SMS Template" beanProperty="smsTemplateData.name" tdCssClass="text-left text-middle word-break" renderFunction="renderSMS"/>
            <nv:dataTableColumn title="Threshold (%)" beanProperty="threshold" tdCssClass="text-left text-middle word-break" cssClass="word-break" />
            <s:if test="%{mode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                <nv:dataTableColumn title="" style="width:20px;" tdStyle="width:20px;"
                                    renderFunction="updateNotificationInfo"  />
                 <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>"
                                hrefurl="delete:${pageContext.request.contextPath}/pd/rncnotification/rnc-notification/$<id>?_method=DELETE&rncPackageId=${id}"
                                style="width:20px;"/>
            </s:if>
            <s:else>
                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>" style="width:20px;" tdStyle="width:20px;opacity:0.5" />
                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" style="width:20px;" tdStyle="width:20px;opacity:0.5" />
            </s:else>
        </nv:dataTable>
    </fieldset>
    </div>
</div>