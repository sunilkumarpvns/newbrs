<div class="row">
    <div class="col-xs-6">
        <s:textfield name="name" key="product.offer.name" id="name" cssClass="form-control focusElement" tabindex="1"/>

        <s:textarea name="description" key="product.offer.description" cssClass="form-control" id="description" tabindex="2"/>

        <s:select name="groups" value="groupValuesForUpdate" key="product.offer.groups" cssClass="form-control select2" onchange="getPackagesBasedOnType()"
                  list="#session.staffBelongingGroups" id="groupNames" multiple="true" listKey="id"
                  listValue="name" cssStyle="width:100%" tabindex="3"/>
        <div class="form-group">
            <div class = "col-xs-12 col-sm-4 col-lg-3"></div>
            <div class = "col-xs-12 col-sm-8 col-lg-9"><span id="validateGroup" style="display: none;"><strong style="color: red">Note : </strong>Changing group may invalidate product offer.</span></div>
        </div>
        <s:select name="status" key="product.offer.status" cssClass="form-control"
                  list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" id="productSpecStatus" tabindex="4"/>
        <s:hidden name="packageMode" value="%{packageMode}" />

        <s:select disabled="true" name="type" key="product.offer.type" cssClass="form-control"
                  list="@com.elitecore.corenetvertex.pkg.PkgType@getUserPlan()" id="productOfferType"
                  onchange="getPackagesBasedOnType();" tabindex="5"/>
        <s:hidden name="type"/>

        <s:select name="currencyCombo" id="currencyCombo" key="product.offer.currency"
                  cssClass="form-control select2" list="@java.util.Currency@getAvailableCurrencies()"
                  listKey="getCurrencyCode()" listValue="getCurrencyCode()+' ('+getDisplayName()+')'" value="%{currency}"
                  cssStyle="width:100%" disabled="!@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@isMultiCurrencyEnable()" onchange="getDataPackagesBasedOnCurrency();"/>

        <s:hidden name="currency" id="currency" value="%{currency}" />

        <div class="form-group">
            <div class="col-xs-12 col-sm-4 col-lg-3 control-label">
                <strong><s:text name="product.offer.data.service.pkg"/></strong>
            </div>
            <div class="col-xs-12 col-sm-8 col-lg-9">
                <s:select name="dataServicePkgId" cssClass="col-xs-10 col-sm-11" theme="simple"
                          list="pkgDataList" id="dataServicePkgData" headerKey="" headerValue="--Select--" listKey="id" listValue="name" tabindex="6"/>
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
    </div>
    <div class="col-xs-6">

        <s:textfield name="validityPeriod" key="product.offer.validity.period" cssClass="form-control"
                     maxlength="8" id="validityPeriod" onkeypress="return isNaturalInteger(event);"
                     tabindex="7" />

        <s:select name="validityPeriodUnit" key="product.offer.validity.period.unit" cssClass="form-control"
                  list="@com.elitecore.corenetvertex.constants.ValidityPeriodUnit@values()"
                  id="validityPeriodUnit" listKey="name()" listValue="displayValue" tabindex="8"/>

        <s:set var="priceTag">
            <s:property value="getText('product.offer.subscription.price')"/> <s:property value="getText('opening.braces')"/><s:property value="%{currency}"/><s:property value="getText('closing.braces')"/>
        </s:set>
        <s:set var="creditTag">
            <s:property value="getText('product.offer.credit.balance')"/> <s:property value="getText('opening.braces')"/><s:property value="%{currency}"/><s:property value="getText('closing.braces')"/>
        </s:set>

        <s:textfield name="subscriptionPrice" key="priceTag"
                     id="price" cssClass="form-control" tabindex="9" maxlength="6"/>
        <s:textfield name="creditBalance" key="creditTag"
                     id="creditBalance"
                     cssClass="form-control" tabindex="10"/>

        <s:select
                id="emailTemplateId"
                name="emailTemplateId"
                key="product.offer.email.template"
                cssClass="form-control"
                listKey="id"
                listValue="name"
                list="emailTemplateList" headerValue="--Select--" headerKey="" tabindex="11"/>
        <s:select
                id="smsTemplateId"
                name="smsTemplateId"
                key="product.offer.sms.template"
                cssClass="form-control"
                listKey="id"
                listValue="name"
                list="smsTemplateList" headerValue="--Select--" headerKey="" tabindex="12"/>


        <s:select
                id="fnfEnabledId"
                name="fnFOffer"
                key="product.offer.fnf.offer"
                cssClass="form-control"
                list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                listKey="isBooleanValue()"
                listValue="getDisplayBooleanValue()"
                tabindex="13"
        />


        <%--<s:textfield name="param1" key="product.offer.param1" cssClass="form-control" id="param1" tabindex="13"/>
        <s:textfield name="param2" key="product.offer.param2" cssClass="form-control" id="param2" tabindex="14"/>--%>
    </div>
</div>
<script type="text/javascript">

    $(function () {
        document.getElementById("dataServicePkgData").setAttribute("style","background-color: white;");
        $("#btnReload").off("click").on("click", reloadBasePackages);
        $('#dataServicePkgData').val('${dataServicePkgId}');
        fetchExistingProductGroup();
        getDataPackagesBasedOnCurrency();
    })

    function fetchExistingProductGroup() {
        existingProductGroup = $('[name=groups]').val();
    }

    $('[name=groups]').on("change",function() {
        validateProductGroup(existingProductGroup, $('[name=groups]').val());
    })

    function validateDesignForm() {
        clearErrorMessages();
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
/*        if(isNullOrEmpty($("#creditBalance").val()) == false ){
            if($("#creditBalance").val()<0){
                setError('creditBalance','<s:text name='product.offer.credit.balance.negative'/>');
                return false;
            }

            if($("#creditBalance").val() > 999999.00){
                setError('creditBalance','Max allowed value for credit balance is 999999.00');
                return false;
            }
        }

        if(isPositiveDecimalNumber("creditBalance",$("#creditBalance").val()) == false){
            return false;
        }*/

        if($("#productOfferType").val() == '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@ADDON.name()" />' ){
            if(isNullOrEmpty($("#validityPeriod").val())){
                setError('validityPeriod','<s:text name='error.valueRequired'/>');
                return false;
            }else if($("#validityPeriod").val()<=0 || $("#validityPeriod").val() > 99999999){
                setError('validityPeriod','<s:text name='product.offer.validity.period.invalid'/>');
                return false;
            }
        }

        return true;
    }

    function getDataPackagesBasedOnCurrency() {
        var currencyValue = $("#currencyCombo").val();

        getPackagesBasedOnType();

        $("#currency").val(currencyValue);

        var systemParameterUpdated = <s:property value="getCurrencyUpdateAllowed()"/>;
        if(systemParameterUpdated == false){
            $("#currencyCombo").attr("disabled",true);
        }

        var priceLabelValue = '<s:property value="getText('product.offer.subscription.price')"/>'+ ' '+ '<s:property value="getText('opening.braces')"/>' + currencyValue + '<s:property value="getText('closing.braces')"/>';
        $("#lbl_price").text(priceLabelValue).change;

        var creditBalanceLabelValue = '<s:property value="getText('product.offer.credit.balance')"/>'+ ' '+ '<s:property value="getText('opening.braces')"/>' + currencyValue + '<s:property value="getText('closing.braces')"/>';
        $("#lbl_creditBalance").text(creditBalanceLabelValue).change;
    }

    function reloadRecords(){
        var reloadedPackages = [];
        $.ajax({
            type 		: "POST",
            url 		: "${pageContext.request.contextPath}/ajax/pkgReload/reloadPackages",
            beforeSend: function(){
                $('#reloadPkg').hide();
                $('#progress').show();
            },
            success: function(data){

                for(pkg in data.dataPackages){
                    reloadedPackages.push(data.dataPackages[pkg])
                }

                $.ajax({
                    type 		: "POST",
                    url 		: "${pageContext.request.contextPath}/ajax/pkgReload/reloadAddOns",
                    success: function(data){

                        $('#reloadPkg').show();
                        $('#progress').hide();
                        for(pkg in data.allAddOns){
                            reloadedPackages.push(data.allAddOns[pkg])
                        }

                        $("#dataServicePkgData").html("");

                        for(pkg in reloadedPackages){
                            $("#dataServicePkgData").append("<option value='"+reloadedPackages[pkg].id+"' data-packageType='"+reloadedPackages[pkg].type+"'>"+reloadedPackages[pkg].name+"</option>")
                        }

                        checkForAddOn();
                    },
                    error 		: function(xhr,status, errmsg) {
                        console.log(status);
                    }
                });

            },
            error 		: function(xhr,status, errmsg) {
                console.log(status);
            }
        });

    }
</script>