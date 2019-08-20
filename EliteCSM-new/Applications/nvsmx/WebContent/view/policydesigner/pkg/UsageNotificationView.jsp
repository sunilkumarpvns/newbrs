<script>
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
</script>
<div class="row">
    <fieldset class="fieldSet-line">
        <legend align="top"><s:text name="pkg.notifications" /> </legend>
        <div class="btn-group" style="float: right">
            <s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                <button class="btn btn-primary btn-xs dropdown-toggle" style="padding-top: 3px; padding-bottom: 3px" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                    <s:text name="pkg.notifications"/> <span class="caret"></span>
                </button>
            </s:if>
            <s:else>
                <button disabled="disabled" class="btn btn-primary btn-xs dropdown-toggle" style="padding-top: 3px; padding-bottom: 3px" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                    <s:text name="pkg.notifications"/> <span class="caret"></span>
                </button>
            </s:else>
            <ul class="dropdown-menu" style="left: auto;right: 0;margin-top: 0px;cursor:pointer">
                <li data-toggle="modal" data-target="#usageNotificationDialog" style="margin-left: 5px"><s:text name="usagenotification" /></li>
            </ul>
            <%@include file="/view/policydesigner/notification/UsageNotification.jsp" %>
        </div>
        <% if(usageNotificationData != null && usageNotificationData.size() != 0){ %>
        <nv:dataTable
                id="usageNotificationData"
                list="<%=usageNotificationData.toString()%>"
                width="100%"
                showPagination="false"
                showInfo="false"
                caption="Usage Notification"
                cssClass="table table-blue">
            <nv:dataTableColumn title="Quota Profile" beanProperty="quotaProfile.Name" tdCssClass="text-left word-break" tdStyle="min-width:50px" cssClass="word-break" hrefurl="${pageContext.request.contextPath}/policydesigner/quota/QuotaProfile/view?quotaProfileId=quotaProfile.id"/>
            <nv:dataTableColumn title="Data Service Type" beanProperty="dataServiceTypeData.name" tdCssClass="text-left text-middle word-break" cssClass="word-break" hrefurl="${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/view?serviceTypeId=dataServiceTypeData.id"/>
            <nv:dataTableColumn title="Aggregation Key" beanProperty="displayAggregationKeyValue" tdCssClass="text-left text-middle word-break" />
            <nv:dataTableColumn title="Metering Type" beanProperty="displayMeteringValue" tdCssClass="text-left text-middle word-break" tdStyle="text-align:left;" cssClass="word-break"/>
            <nv:dataTableColumn title="Email Template" beanProperty="emailTemplateData.name" tdCssClass="text-left text-middle word-break" cssClass="word-break" renderFunction="renderEmail" />
            <nv:dataTableColumn title="SMS Template" beanProperty="smsTemplateData.name" tdCssClass="text-left text-middle word-break" cssClass="word-break" renderFunction="renderSMS" />
            <nv:dataTableColumn title="Threshold (%)" beanProperty="threshold" tdCssClass="text-left text-middle word-break" cssClass="word-break" />
            <s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>" style="width:20px;" tdStyle="width:20px;"
                                    hrefurl="edit:javascript:updateUsageNotificationDialog(id)"   />
                <s:if test="%{pkgData.type == @com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name()}">
                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" style="width:20px;" tdStyle="width:20px;"
                                        hrefurl="delete:${pageContext.request.contextPath}/promotional/policydesigner/notification/UsageNotification/PromotionalUsageNotification/delete?usageNotificationId=id&groupIds=${pkgData.groups}&pkgId=${pkgData.id}"/>
                </s:if>
                <s:else>
                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" style="width:20px;" tdStyle="width:20px;"
                                        hrefurl="delete:${pageContext.request.contextPath}/policydesigner/notification/UsageNotification/delete?usageNotificationId=id&groupIds=${pkgData.groups}&pkgId=${pkgData.id}"/>
                </s:else>
            </s:if>
            <s:else>
                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>" style="width:20px;" tdStyle="width:20px;opacity:0.5" />
                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" style="width:20px;" tdStyle="width:20px;opacity:0.5" />
            </s:else>
        </nv:dataTable>
        <%}%>
    </fieldset>
</div>