<%@ taglib uri="/struts-tags/ec" prefix="s" %>
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
            <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">

            <span class="btn-group btn-group-xs">
				<button type="button"
                        class="btn btn-default header-btn"
                        data-toggle="tooltip" data-placement="bottom"
                        title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableId=${auditableId}&auditableResourceName=${name}&refererUrl=/sm/pccservicepolicy/pcc-service-policy/${id}'">
                        <span class="glyphicon glyphicon-eye-open"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs">
					<button type="button"
                            class="btn btn-default header-btn"
                            data-toggle="tooltip"
                            data-placement="bottom" title="edit"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/sm/pccservicepolicy/pcc-service-policy/${id}/edit?serverGroupId=${sessionScope.serverGroupId}'">
					        <span class="glyphicon glyphicon-pencil"></span>
				    </button>
			</span>
            <span class="btn-group btn-group-xs"
                  data-toggle="confirmation-singleton"
                  onmousedown="deleteConfirm()"
                  data-href="${pageContext.request.contextPath}/sm/pccservicepolicy/pcc-service-policy/${id}?_method=DELETE">
			        <button type="button"
                            class="btn btn-default header-btn"
                            data-toggle="tooltip"
                            data-placement="bottom"
                            title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-sm-6">
                <div class="row">

                <s:label value="%{name}"
                         key="pccServicePolicy.name"
                         cssClass="control-label light-text"
                         labelCssClass="col-xs-4 col-sm-6 "
                         elementCssClass="col-xs-8 col-sm-6"/>

                <s:label value="%{description}"
                         key="pccServicePolicy.description"
                         cssClass="control-label light-text"
                         labelCssClass="col-xs-4 col-sm-6 "
                         elementCssClass="col-xs-8 col-sm-6"/>

                <s:label value="%{@com.elitecore.corenetvertex.constants.ServicePolicyStatus@getLabelFromInstanceName(status)}"
                         key="pccServicePolicy.status"
                         cssClass="control-label light-text"
                         labelCssClass="col-xs-4 col-sm-6 "
                         elementCssClass="col-xs-8 col-sm-6"/>

                <s:label value="%{ruleset}"
                         key="pccServicePolicy.ruleset"
                         cssClass="control-label light-text word-break"
                         labelCssClass="col-xs-4 col-sm-6 "
                         elementCssClass="col-xs-8 col-sm-6"/>

                <s:label value="%{@com.elitecore.corenetvertex.constants.RequestAction@getLabelFromInstanceName(action)}"
                         key="pccServicePolicy.action"
                         cssClass="control-label light-text"
                         labelCssClass="col-xs-4 col-sm-6 "
                         elementCssClass="col-xs-8 col-sm-6"/>

                <s:label value="%{@com.elitecore.corenetvertex.constants.SubscriberLookupOn@getLabelFromInstanceName(subscriberLookupOn)}"
                         key="pccServicePolicy.subscriberLookupOn"
                         cssClass="control-label light-text"
                         labelCssClass="col-xs-4 col-sm-6 "
                         elementCssClass="col-xs-8 col-sm-6"/>

                <s:label value="%{identityAttribute}"
                         key="pccServicePolicy.identity.attribute"
                         cssClass="control-label light-text"
                         labelCssClass="col-xs-4 col-sm-6 "
                         elementCssClass="col-xs-8 col-sm-6"/>

                <s:label
                         value="%{@com.elitecore.corenetvertex.constants.UnknownUserAction@getDisplayLabelFromInstanceName(unknownUserAction)}"
                         key="pccServicePolicy.unknown.user.action"
                         cssClass="control-label light-text"
                         labelCssClass="col-xs-4 col-sm-6 "
                         elementCssClass="col-xs-8 col-sm-6"/>

                <s:if test="%{UnknownUserProductOffer !=null}">
                    <s:hrefLabel value="%{unknownUserProductOffer.name}"
                             url="/pd/productoffer/product-offer/%{unknownUserProductOffer.id}"
                             key="pccServicePolicy.unknown.user.productoffer"
                             cssClass="control-label light-text"
                             labelCssClass="col-xs-4 col-sm-6 "
                             elementCssClass="col-xs-8 col-sm-6"/>
                </s:if>
                <s:else>
                    <s:label value=""
                             key="pccServicePolicy.unknown.user.productoffer"
                             cssClass="control-label light-text"
                             labelCssClass="col-xs-4 col-sm-6"
                             elementCssClass="col-xs-8 col-sm-6"/>
                </s:else>
                </div>
            </div>

            <div class="col-sm-6 leftVerticalLine">
                <div class="row">

                    <s:if test="%{syGateway !=null}">
                        <s:hrefLabel value="%{syGateway.name}"
                                 url="/sm/gateway/diameter-gateway/%{syGateway.id}"
                             key="pccServicePolicy.sy.gateway"
                             cssClass="control-label light-text"
                             labelCssClass="col-xs-4 col-sm-5"
                             elementCssClass="col-xs-8 col-sm-7"/>
                    </s:if>
                    <s:else>
                        <s:label value=""
                                 key="pccServicePolicy.sy.gateway"
                                 cssClass="control-label light-text"
                                 labelCssClass="col-xs-4 col-sm-5"
                                 elementCssClass="col-xs-8 col-sm-7"/>
                    </s:else>

                    <s:label value="%{syMode}"
                             key="pccServicePolicy.sy.mode"
                             cssClass="control-label light-text"
                             labelCssClass="col-xs-4 col-sm-5"
                             elementCssClass="col-xs-8 col-sm-7"/>

                    <s:if test="%{policyCdrDriver != null}">
                        <s:hrefLabel value="%{policyCdrDriver.name}"
                                     url="/sm/driver/db-cdr-driver/%{policyCdrDriver.id}"
                             key="pccServicePolicy.policy.cdr"
                             cssClass="control-label light-text"
                             labelCssClass="col-xs-4 col-sm-5"
                             elementCssClass="col-xs-8 col-sm-7"/>
                    </s:if>
                    <s:else>
                        <s:label value=""
                                 key="pccServicePolicy.policy.cdr"
                                 cssClass="control-label light-text"
                                 labelCssClass="col-xs-4 col-sm-5"
                                 elementCssClass="col-xs-8 col-sm-7"/>
                    </s:else>

                    <s:hrefLabel value="%{chargingCdrDriver.name}"
                                 url="/sm/driver/csv-driver/%{chargingCdrDriver.id}"
                             key="pccServicePolicy.charging.cdr"
                             cssClass="control-label light-text"
                             labelCssClass="col-xs-4 col-sm-5"
                             elementCssClass="col-xs-8 col-sm-7"/>

                    <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-xs-12" align="center">
                <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/pccservicepolicy/pcc-service-policy'">
                    <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                </button>
            </div>
        </div>
    </div>
</div>

