<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
</style>
<script type="text/javascript">
    function renderValidityWithUnit(data,type,thisBean){
        return data + " " + thisBean["addOnProductOfferData"]["Validity Period Unit"];
    }

    function autoSubscriptionIdRendering(data, type, thisBean){

        return "<a href='${pageContext.request.contextPath}/pd/productoffer/product-offer/" + thisBean.addOnProductOfferData.id  + "'>"+thisBean.addOnProductOfferData.Name+"</a>";

    }
</script>

<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <div class="btn-group btn-group-xs">
                <button class="btn btn-primary" style="display:none"  	id="designMode">Design</button>
                <button class="btn btn-primary" style="display:none"  	id="testMode" 		onclick="changeOfferMode('Design to Test','testMode');" >Test it</button>
                <button class="btn btn-primary" style="display:none"  	id="liveMode" 		onclick="changeOfferMode('Test to Live','liveMode');">Go Live</button>
                <button class="btn btn-primary" style="display:none"  	id="live2Mode" 		onclick="changeOfferMode('Live to Live2','live2Mode');" >Go Live2</button>
            </div>
            &nbsp; <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
            <span class="btn-group btn-group-xs">
                <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="<s:text name="audit.history"/>"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${id}&auditPageHeadingName=<s:property value="name"/>&refererUrl=/pd/productoffer/product-offer/${id}'">
                    <span class="glyphicon glyphicon-eye-open"></span>
                </button>
            </span>
            <span class="btn-group btn-group-xs">
                <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="Clone"
                        onclick="javascript:updateProductOfferNotification('${id}', '${name}');">
                    <span class="glyphicon glyphicon-duplicate"></span>
                </button>
            </span>
            <span class="btn-group btn-group-xs">
                    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip"
                            data-placement="bottom" title="<s:text name="tooltip.edit"/>"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/pd/productoffer/product-offer/${id}/edit?groupIds=${groups}'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
            </span>
             <s:if test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() || packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
             <span class="btn-group   btn-group-xs" data-toggle="confirmation-singleton" >
				<button type="button" disabled="disabled" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete" >
					<span class="glyphicon glyphicon-trash"></span>
				</button>
			</span>
             </s:if>
            <s:else>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/productoffer/product-offer/${id}?_method=DELETE">
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
                <div class="col-sm-4">
                    <div class="row">
                        <s:label key="product.offer.description" value="%{description}"
                                 cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4"
                                 elementCssClass="col-sm-7 col-xs-8"/>
                        <s:label key="product.offer.status" value="%{status}" cssClass="control-label light-text"
                                 labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                        <s:label key="product.offer.type"
                                 value="%{@com.elitecore.corenetvertex.pkg.PkgType@fromName(type).val}"
                                 cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4"
                                 elementCssClass="col-sm-7 col-xs-8"/>
                        <s:label key="product.offer.currency" value="%{currency}" cssClass="control-label light-text"
                                 labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                        <s:set var="priceTag">
                            <s:property value="getText('product.offer.subscription.price')"/>
                            <s:property value="getText('opening.braces')"/><s:property value="%{currency}"/><s:property value="getText('closing.braces')"/>
                        </s:set>
                        <s:set var="creditTag">
                            <s:property value="getText('product.offer.credit.balance')"/>
                            <s:property value="getText('opening.braces')"/><s:property value="%{currency}"/><s:property value="getText('closing.braces')"/>
                        </s:set>
                        <s:label key="priceTag" value="%{subscriptionPrice}"
                                 cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4"
                                 elementCssClass="col-sm-7 col-xs-8"/>
                        <s:label key="creditTag" value="%{creditBalance}"
                                 cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4"
                                 elementCssClass="col-sm-7 col-xs-8"/>

                        <s:label key="product.offer.groups" value="%{groupNames}" cssClass="control-label light-text"
                                 labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>

                    </div>
                </div>
                <div class="col-sm-4 leftVerticalLine">
                    <div class="row">

                        <s:if test="%{dataServicePkgData != null}">
                            <s:hrefLabel cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                         elementCssClass="col-xs-8 col-sm-7" url="/policydesigner/pkg/Pkg/view?pkgId=%{dataServicePkgData.id}" key="product.offer.data.service.pkg" value="%{dataServicePkgData.name}"/>
                        </s:if>
                        <s:else>
                            <s:label key="product.offer.data.service.pkg" value="N/A" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5"
                                     elementCssClass="col-xs-8 col-sm-7"/>
                        </s:else>
                        <s:if test="%{type == @com.elitecore.corenetvertex.pkg.PkgType@BASE.name()}">
                            <s:label key="product.offer.validity.period"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                     elementCssClass="col-xs-8 col-sm-7" value="N/A"/>
                        </s:if>
                        <s:else>
                            <s:if test="%{validityPeriod != null && validityPeriod != 0}">
                                <s:label key="product.offer.validity.period" value="%{validityPeriod}  %{validityPeriodUnit}"
                                         cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                         elementCssClass="col-xs-8 col-sm-7"/>
                            </s:if>
                            <s:else>
                                <s:label key="product.offer.validity.period"
                                         cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                         elementCssClass="col-xs-8 col-sm-7"/>
                            </s:else>
                        </s:else>
                        <s:if test="%{emailNotificationTemplateData != null}">
                            <s:hrefLabel key="product.offer.email.template"
                                         value="%{emailNotificationTemplateData.name}"
                                         url="/policydesigner/notification/NotificationTemplate/view?notificationTemplateId=%{emailNotificationTemplateData.id}"
                                         cssClass="control-label light-text"
                                         labelCssClass="col-xs-4 col-sm-5"
                                         elementCssClass="col-xs-8 col-sm-7"/>


                        </s:if>
                        <s:else>
                            <s:label key="product.offer.email.template" value="N/A" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5"
                                     elementCssClass="col-xs-8 col-sm-7"/>
                        </s:else>
                        <s:if test="%{smsNotificationTemplateData != null}">
                            <s:hrefLabel key="product.offer.sms.template" value="%{smsNotificationTemplateData.name}"
                                         url="/policydesigner/notification/NotificationTemplate/view?notificationTemplateId=%{smsNotificationTemplateData.id}"
                                         cssClass="control-label light-text"
                                         labelCssClass="col-xs-4 col-sm-5"
                                         elementCssClass="col-xs-8 col-sm-7"/>
                        </s:if>
                        <s:else>
                            <s:label key="product.offer.sms.template" value="N/A" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5"
                                     elementCssClass="col-xs-8 col-sm-7"/>
                        </s:else>


                        <s:if test="%{type == @com.elitecore.corenetvertex.pkg.PkgType@BASE.name()}">
                            <s:label key="product.offer.fnf.offer" value="N/A"
                                     cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5"
                                     elementCssClass="col-xs-8 col-sm-7"/>
                        </s:if>
                        <s:else>
                            <s:label key="product.offer.fnf.offer"
                                     value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(fnFOffer).getDisplayBooleanValue()}"
                                     cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5"
                                     elementCssClass="col-xs-8 col-sm-7"/>
                        </s:else>


                        <%--<s:label key="Param 1" value="%{param1}" cssClass="control-label light-text"
                                 labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                        <s:label key="Param 2" value="%{param2}" cssClass="control-label light-text"
                                 labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>--%>
                    </div>
                </div>
                <div class="col-sm-4 leftVerticalLine">
                    <div class="row">
                        <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                    </div>
                </div>
            </fieldset>
        </div>
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top">
                    <s:text name="product.offer.service.package.relation"/>
                </legend>
                <div style=" text-align: right; margin-bottom: 10px;">
                    <s:if test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() || packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                        <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" disabled="true">
                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                            <s:text name="product.offer.service.package.relation"/>
                        </button>
                    </s:if>
                    <s:else>
                        <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"
                                onclick="$('#productOfferServiceRelationModal').modal('show');">
                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                            <s:text name="product.offer.service.package.relation"/>
                        </button>
                        </s:else>
                </div>
                <div class="dataTables_wrapper form-inline dt-bootstrap no-footer">
                        <nv:dataTable id="servicePkgRelationTable" list="${productOfferServicePkgRelListAsJson}"
                                      width="100%"
                                      showPagination="false" showFilter="false" showInfo="false"
                                      cssClass="table table-blue">
                            <nv:dataTableColumn title="Service Type" beanProperty="serviceName"/>
                            <nv:dataTableColumn title="Package" beanProperty="rncPackageName"
                                                hrefurl="${pageContext.request.contextPath}/pd/rncpackage/rnc-package/$<displayPackageId>"/>
                            <s:if test="%{packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() && packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                                <nv:dataTableColumn title=""
                                                    icon="<span class='glyphicon glyphicon-trash'></span>"
                                                    hrefurl="delete:${pageContext.request.contextPath}/pd/productoffer/product-offer/${id}/removeServicePkgRelations?servicePkgRelationIds=id"
                                                    style="width:20px;"/>
                            </s:if>
                            <s:else>
                                <nv:dataTableColumn title=""
                                                    icon="<span disabled='disabled' class='glyphicon glyphicon-trash'></span>"
                                                    style="width:20px;"/>
                            </s:else>
                        </nv:dataTable>
                </div>
            </fieldset>
        </div>

        <!-- Auto Subscription-->

        <s:if test="%{type == @com.elitecore.corenetvertex.pkg.PkgType@BASE.name()}">
            <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top">
                    <s:text name="product.offer.auto.subscription"/>
                </legend>
                <div style=" text-align: right; margin-bottom: 10px;">
                    <s:if test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() || packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                        <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" disabled="true">
                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                            <s:text name="product.offer.auto.subscription"/>
                        </button>
                    </s:if>
                    <s:elseif test="%{productOfferAutoSubscriptionRelDatas.size >= @com.elitecore.corenetvertex.constants.CommonConstants@MAX_AUTO_SUBSCRIPTION_COUNT}">
                        <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" disabled="true">
                            <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                            <s:text name="product.offer.auto.subscription"/>
                        </button>
                    </s:elseif>
                    <s:else>
                        <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"
                            onclick="$('#productOfferAutoSubscriptionModal').modal('show');">
                        <span class="glyphicon glyphicon-plus-sign" title="Add"></span>
                        <s:text name="product.offer.auto.subscription"/>
                    </button>
                    </s:else>
                    <s:if test="%{productOfferAutoSubscriptionRelDatas.size() <= 1}">
                        <button class="btn btn-primary btn-xs" disabled="true" style="padding-top: 3px; padding-bottom: 3px">
                            <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                            <s:text name="manage.order"/>
                        </button>
                    </s:if>
                    <s:else>
                        <button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/productoffer/product-offer/${id}/initManageOrder'">
                            <span class="glyphicon glyphicon-sort" title="Manage Order"></span>
                            <s:text name="manage.order"/>
                        </button>
                    </s:else>
                </div>
                <div class="dataTables_wrapper form-inline dt-bootstrap no-footer">
                    <nv:dataTable id="autoSubscriptionTable" list="${autoSubscriptionsAsJson}"
                                  width="100%"
                                  showPagination="false" showFilter="false" showInfo="false"
                                  cssClass="table table-blue">
                        <nv:dataTableColumn title="#"
                                            beanProperty="dataTable.RowNumber"
                                            style="font-weight: bold;color: #4679bd;"
                                            tdStyle="text-align:left;width:10px;" tdCssClass="word-break"	/>
                        <nv:dataTableColumn title="Product Offer" beanProperty="addOnProductOfferData.Name"
                                            renderFunction="autoSubscriptionIdRendering"/>
                        <nv:dataTableColumn title="Advance Condition" beanProperty="advanceCondition"/>
                        <nv:dataTableColumn title="Subscription Price" beanProperty="addOnProductOfferData.Subscription Price"/>
                        <nv:dataTableColumn title="Validity" beanProperty="addOnProductOfferData.Validity Period" renderFunction="renderValidityWithUnit"/>
                        <s:if test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name() || packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                            <nv:dataTableColumn title=""
                                                icon="<span disabled='disabled' class='glyphicon glyphicon-trash'></span>"
                                                style="width:20px;"/>
                        </s:if>
                        <s:else>
                            <nv:dataTableColumn title=""
                                                icon="<span class='glyphicon glyphicon-trash'></span>"
                                                hrefurl="delete:${pageContext.request.contextPath}/pd/productoffer/product-offer/${id}/removeAutoSubscription?autoSubscriptionRelIds=id"
                                                style="width:20px;"/>

                        </s:else>
                    </nv:dataTable>
                </div>
            </fieldset>
        </div>
        </s:if>
        <div class="row">
            <div class="col-xs-12 back-to-list" align="center">
                <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/productoffer/product-offer'">
                    <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text
                        name="button.list"/></button>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function() {
        setOfferMode();
    });

    function changeOfferMode(fromTo,refId) {
        if(validPackageForChangePackageMode() == false){
            return ;
        };


        var classVal = $("#"+refId).attr("class");
        if(classVal.toLowerCase().indexOf("primary") >=0){
            return;
        }

        if(confirm("Change product offer mode from "+fromTo+" ?")){
            var offerId = '${id}';
            var pkgMode = '<s:property value="@com.elitecore.corenetvertex.pkg.PkgMode@getMode(packageMode).getNextMode()" />';
            var updateURL;
            console.log("current mode :" +pkgMode);
            var updateURL = "${pageContext.request.contextPath}/pd/productoffer/product-offer/"+offerId+"/updateMode?_method=put";

            $.ajax({
                async: true,
                type: "POST",
                url: updateURL,
                data: {
                    'offerId': offerId,
                    'pkgMode': pkgMode
                },
                success: function (json) {
                    document.location.href = "${pageContext.request.contextPath}/pd/productoffer/product-offer/" + offerId;
                },
                error: function (json) {
                    document.location.href = "${pageContext.request.contextPath}/pd/productoffer/product-offer/" + offerId;
                }
            });
        }
    }

    function setOfferMode() {

        var pkgMode = '${packageMode}';
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
    function validPackageForChangePackageMode(){
        var dataServicePkgData = '<s:property value="%{dataServicePkgId}"/>';
        var rncPackageValue = '<s:property value="%{productOfferServicePkgRelDataList.size()}" />';
        if(isNullOrEmpty(dataServicePkgData) && rncPackageValue == 0){
            addWarning(".popup",'<s:text name="product.offer.empty.data.rnc.packages"/>');
            return false;
        }
        return true;

    }
    function updateProductOfferNotification(id,name) {
        $("#productOfferNotificationId").val(id);
        $("#duplicateEntityName").val("CopyOf_" + name);
        $('#productOfferCloningForm').attr('action', "${pageContext.request.contextPath}/pd/productoffer/product-offer/"+id+"/copymodel");
        $("#productOfferCloningDialog").modal('show');
        $("#duplicateEntityName").focus();
        $("#method").val("post");
    }
</script>
<%@include file="product-offer-service-relation-modal.jsp"%>
<%@include file="product-offer-auto-subscription-modal.jsp"%>
<%@include file="product-offer-duplicate-dialog.jsp"%>