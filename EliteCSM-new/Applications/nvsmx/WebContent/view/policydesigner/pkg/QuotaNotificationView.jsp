<%--
  Created by IntelliJ IDEA.
  User: saloni
  Date: 19/6/18
  Time: 12:29 PM
  To change this template use File | Settings | File Templates.
--%>
<script>
    /*
    * This function is used to provided custom rendering of name column of search page based
    * on fup level or aggregation keys
    * * */
    function setFupLevel(data, type, thisBean){
        if(data == '<s:property value="%{@com.elitecore.corenetvertex.constants.BalanceLevel@HSQ.fupLevel}"/>'){
            return '<s:property value="%{@com.elitecore.corenetvertex.constants.BalanceLevel@HSQ.displayVal}"/>';
        }else if(data == '<s:property value="%{@com.elitecore.corenetvertex.constants.BalanceLevel@FUP1.fupLevel}"/>'){
            return '<s:property value="%{@com.elitecore.corenetvertex.constants.BalanceLevel@FUP1.displayVal}"/>';
        }else if(data == '<s:property value="%{@com.elitecore.corenetvertex.constants.BalanceLevel@FUP2.fupLevel}"/>'){
            return '<s:property value="%{@com.elitecore.corenetvertex.constants.BalanceLevel@FUP2.displayVal}"/>';
        }
    }

    function setAggregateKeys(data, type, thisBean){
        if(data == '<s:property value="%{@com.elitecore.corenetvertex.constants.AggregationKey@BILLING_CYCLE.name()}"/>'){
            return '<s:property value="%{@com.elitecore.corenetvertex.constants.AggregationKey@BILLING_CYCLE.val}"/>';
        }else if(data == '<s:property value="%{@com.elitecore.corenetvertex.constants.AggregationKey@DAILY.name()}"/>'){
            return '<s:property value="%{@com.elitecore.corenetvertex.constants.AggregationKey@DAILY.val}"/>';
        }else if(data == '<s:property value="%{@com.elitecore.corenetvertex.constants.AggregationKey@WEEKLY.name()}"/>'){
            return '<s:property value="%{@com.elitecore.corenetvertex.constants.AggregationKey@WEEKLY.val}"/>';
        }
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

</script>
<div class="row">
    <fieldset class="fieldSet-line">
        <legend align="top"><s:text name="pkg.notifications" /> </legend>
        <div class="btn-group" style="float: right">
            <s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                <button class="btn btn-primary btn-xs dropdown-toggle" style="padding-top: 3px; padding-bottom: 3px" data-toggle="modal" data-target="#quotaNotificationDialog" aria-haspopup="true" aria-expanded="false" >
                    <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                    <s:text name="pkg.notifications"/>
                </button>
            </s:if>
            <s:else>
                <button disabled="disabled" class="btn btn-primary btn-xs dropdown-toggle" style="padding-top: 3px; padding-bottom: 3px" data-toggle="modal" data-target="#quotaNotificationDialog"
                        aria-haspopup="true" aria-expanded="false">
                    <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                    <s:text name="pkg.notifications"/>
                </button>
            </s:else>
            <%@include file="/view/policydesigner/notification/QuotaNotification.jsp" %>
        </div>
        <% if(quotaNotificationData != null && quotaNotificationData.size() != 0){ %>

        <nv:dataTable
                id="quotaNotificationData"
                list="<%=quotaNotificationData.toString()%>"
                width="100%"
                showPagination="false"
                showInfo="false"
                caption="Quota Notification"
                cssClass="table table-blue">
            <nv:dataTableColumn hiddenElement="id" style="display:none;" tdStyle="display:none;" beanProperty="id" />

            <nv:dataTableColumn title="Quota Profile" beanProperty="quotaProfile.name" tdCssClass="text-left word-break" tdStyle="min-width:50px" cssClass="word-break" hrefurl="${pageContext.request.contextPath}/policydesigner/rnc/RncProfile/view?quotaProfileId=quotaProfile.id"/>
            <nv:dataTableColumn title="Data Service Type" beanProperty="dataServiceTypeData.name" tdCssClass="text-left text-middle word-break" cssClass="word-break" hrefurl="${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/view?serviceTypeId=dataServiceTypeData.id"/>
            <nv:dataTableColumn title="Aggregation Key" beanProperty="aggregationKey" renderFunction="setAggregateKeys" tdCssClass="text-left text-middle word-break" />
            <nv:dataTableColumn title="FUP Level" beanProperty="fupLevel" renderFunction="setFupLevel" tdCssClass="text-left text-middle word-break" tdStyle="text-align:left;" cssClass="word-break"/>
            <nv:dataTableColumn title="Email Template" beanProperty="emailTemplateData.name" tdCssClass="text-left text-middle word-break" cssClass="word-break" renderFunction="renderEmail"/>
            <nv:dataTableColumn title="SMS Template" beanProperty="smsTemplateData.name" tdCssClass="text-left text-middle word-break" cssClass="word-break" renderFunction="renderSMS"/>
            <nv:dataTableColumn title="Threshold (%)" beanProperty="threshold" tdCssClass="text-left text-middle word-break" cssClass="word-break" />
            <s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>" style="width:20px;" tdStyle="width:20px;"
                                    hrefurl="edit:javascript:updateQuotaNotificationDialog(id)"   />
                <s:if test="%{pkgData.type == @com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name()}">
                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" style="width:20px;" tdStyle="width:20px;"
                                        hrefurl="delete:${pageContext.request.contextPath}/promotional/policydesigner/notification/QuotaNotification/PromotionalQuotaNotification/delete?quotaNotificationId=id&groupIds=${pkgData.groups}&pkgId=${pkgData.id}"/>
                </s:if>
                <s:else>
                    <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" style="width:20px;" tdStyle="width:20px;"
                                        hrefurl="delete:${pageContext.request.contextPath}/policydesigner/notification/QuotaNotification/delete?quotaNotificationId=id&groupIds=${pkgData.groups}&pkgId=${pkgData.id}"/>
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
