<%@ taglib uri="/struts-tags/ec" prefix="s" %>

    <style>
        #dataServicePkgData{
            display: block;
            width: 88.666667%;
            height: 34px;
            padding: 6px 12px;
            font-size: 14px;
            line-height: 1.42857143;
            color: #555;
            background-color: #fff;
            background-image: none;
            border: 1px solid #ccc;
            border-radius: 4px;
            -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
            box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
            -webkit-transition: border-color ease-in-out .15s,-webkit-box-shadow ease-in-out .15s;
            -o-transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
            transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
        }
    </style>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="product.offer.create"/></h3>
    </div>

    <div class="panel-body">
        <s:form namespace="/pd/productoffer" action="product-offer" id="productspecform" method="post" cssClass="form-horizontal" validate="true"
                labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9"
                onsubmit="return validateForm()">
        <s:token/>
        <div class="row">
            <div class="col-xs-6">
                <s:textfield name="name" key="product.offer.name" id="name" cssClass="form-control focusElement"
                             tabindex="1"/>

                <s:textarea name="description" key="product.offer.description" cssClass="form-control"
                            id="description"
                            value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}"
                            tabindex="2"/>

                <s:select name="groups" key="product.offer.groups" cssClass="form-control select2"
                          list="#session.staffBelongingGroups" id="groupNames" multiple="true" listKey="id" onchange="getPackagesBasedOnType()"
                          listValue="name" cssStyle="width:100%" tabindex="3"/>
                <s:select name="status" key="product.offer.status" cssClass="form-control"
                          list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" id="productOfferStatus" tabindex="4"/>
                <s:select name="type" key="product.offer.type" cssClass="form-control"
                          list="@com.elitecore.corenetvertex.pkg.PkgType@getUserPlan()" id="productOfferType"
                          onchange="getPackagesBasedOnType();" tabindex="5"/>

                <s:select name="currencyCombo" id="currencyCombo" key="product.offer.currency"
                          cssClass="form-control select2" list="@java.util.Currency@getAvailableCurrencies()"
                          listKey="getCurrencyCode()" listValue="getCurrencyCode()+' ('+getDisplayName()+')'" value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"
                          cssStyle="width:100%" disabled="!@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@isMultiCurrencyEnable()" onchange="getPackagesBasedOnCurrency();" tabindex="6"/>

                <s:hidden name="currency" id="currency" value="%{@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()}" />

                <div class="form-group">
                    <div class="col-xs-12 col-sm-4 col-lg-3 control-label">
                        <strong><s:text name="product.offer.data.service.pkg"/></strong>
                    </div>
                    <div class="col-xs-12 col-sm-8 col-lg-9">
                        <s:select name="dataServicePkgId" cssClass="col-xs-10 col-sm-11" theme="simple"
                                  list="pkgDataList" id="dataServicePkgData" headerKey="" headerValue="--Select--" listKey="id" listValue="name" tabindex="7"/>
                        <div class="col-xs-2 col-sm-1" style="display: block; padding: 0px 0px 0px 0;" id="reloadPkg">
                            <button type="button" style="float: right; height: 33px; width: 35px; margin: 0px -15px 0 7px;" class="btn btn-default btn-xs" role="button" id="btnReload" >
                                <span class="glyphicon glyphicon-refresh" title="Refresh"></span>
                            </button>
                        </div>
                        <div class="col-xs-2 col-sm-1" style="padding: 0px 0px 0 0;display:none;" id="progress" >
                            <img src='${pageContext.request.contextPath}/images/progress.gif' style="width: 67%; float: right; position: relative; top: 4px;" >
                        </div>
                    </div>

                </div>
                <s:hidden name="packageMode" id="pkgMode" value="%{@com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name()}" tabindex="3"/>

            </div>
            <div class="col-xs-6">
                <s:textfield name="validityPeriod" key="product.offer.validity.period" cssClass="form-control"
                             maxlength="8" id="validityPeriod" onkeypress="return isNaturalInteger(event);"
                             value="30" tabindex="8"/>


                <s:select name="validityPeriodUnit" key="product.offer.validity.period.unit" cssClass="form-control"
                          list="@com.elitecore.corenetvertex.constants.ValidityPeriodUnit@values()"
                          id="validityPeriodUnit" listKey="name()" listValue="displayValue" tabindex="9"/>

                <s:set var="priceTag">
                    <s:property value="getText('product.offer.subscription.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
                </s:set>
                <s:set var="creditTag">
                    <s:property value="getText('product.offer.credit.balance')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
                </s:set>
                <s:textfield name="subscriptionPrice" key="priceTag"
                             id="price" maxlength="6"
                             cssClass="form-control" tabindex="10"/>
                <s:textfield name="creditBalance" key="creditTag"
                             id="creditBalance"
                             cssClass="form-control" tabindex="11"/>
                <s:select
                        id="emailTemplateId"
                        name="emailTemplateId"
                        key="product.offer.email.template"
                        cssClass="form-control"
                        listKey="id"
                        listValue="name"
                        list="emailTemplateList" headerValue="--Select--" headerKey="" tabindex="12"/>
                <s:select
                id="smsTemplateId"
                name="smsTemplateId"
                key="product.offer.sms.template"
                cssClass="form-control"
                listKey="id"
                listValue="name"
                list="smsTemplateList" headerValue="--Select--" headerKey="" tabindex="13"/>

                <%--<s:textfield name="param1" key="product.offer.param1" cssClass="form-control" id="param1"
                             tabindex="10"/>
                <s:textfield name="param2" key="product.offer.param2" cssClass="form-control" id="param2"
                             tabindex="11"/>--%>

                <s:select
                        id="fnfEnabledId"
                        name="fnFOffer"
                        key="product.offer.fnf.offer"
                        cssClass="form-control"
                        list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                        listKey="isBooleanValue()"
                        listValue="getDisplayBooleanValue()"
                        value="@com.elitecore.corenetvertex.constants.CommonStatusValues@DISABLE.isBooleanValue()"
                        tabindex="14"
                />

            </div>
        </div>

        <div class="row">
            <div class="col-xs-12" align="center">
                <s:submit cssClass="btn btn-sm btn-primary" type="button" role="button" tabindex="15">
                        <span class="glyphicon glyphicon-floppy-disk">
                        </span> <s:text name="button.save"/>
                </s:submit>
            </div>
        </div>
    </div>

    </s:form>
</div>
<script type="text/javascript">

    $(function () {
        $(".select2").select2();
        getPackagesBasedOnType();
    });

    function validateForm() {

        clearErrorMessages(productspecform);
        if( verifyUniquenessOnSubmit('name', 'create', '', 'com.elitecore.corenetvertex.pd.productoffer.ProductOfferData', '', '')==false){
            return false;
        }
        if(isNullOrEmpty($("#price").val()) == false ){
            if($("#price").val()<0){
                setError('price','<s:text name='product.offer.price.negative'/>');
                return false;
            }

            if($("#price").val() > 999999.00){
                setError('price','Max allowed value for subscription price is 999999.00');
                return false;
            }

            var regex = /^\d*(\.*\d{1,2})?$/;
            if (regex.test($("#price").val()) == false) {
                setError('price','<s:text name='error.price.only.two.decimal.places'/>');
                return false;
            }
        }

        if(isPositiveDecimalNumber("price",$("#price").val()) == false){
            return false;
        }

        //check When type is base not in AddOn
        if (validateCreditBalance() == false) {
            return false;
        }

        if($("#productOfferType").val() == '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@ADDON.name()" />' ){
            if(isNullOrEmpty($("#validityPeriod").val())){
                setError('validityPeriod','<s:text name='error.valueRequired'/>');
                return false;
            }else if($("#validityPeriod").val()<=0 || $("#validityPeriod").val() > 99999999){
                setError('validityPeriod','<s:text name='product.offer.validity.period.invalid'/>');
                return false;
            }
        }
        var status =$("#productOfferStatus").val();
        if(status === '<s:property value="@com.elitecore.corenetvertex.constants.PkgStatus@RETIRED.name()"/>'){
            setError('productOfferStatus',"<s:text name='error.status.retired'/>");
            return false;
        }
        return true;
    }

</script>
<%@include file="product-offer-utility.jsp"%>