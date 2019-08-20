<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<script type="text/javascript">

    function reloadBasePackages() {
        var reloadedPackages = [];
        <%--var groupIds = '${groups}';--%>
        var groupIds = $("#groupNames").val();
        if(isNullOrEmpty(groupIds) == false){
            groupIds = groupIds.join();
        }
        var selectedDataPkg = $("#dataServicePkgData").val();
        var currencyValue = $("#currencyCombo").val();
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/ajax/pkgReload/reloadPackages",
            data: {
                'groupIds': groupIds,
                'currency': currencyValue
            },
            beforeSend: function () {
                $('#reloadPkg').hide();
                $('#progress').show();
            },
            success: function (data) {
                $('#reloadPkg').show();
                $('#progress').hide();
                $("#dataServicePkgData").html("");
                $("#dataServicePkgData").append('<option value>--Select--</option>');

                for (pkg in data.dataPackages) {
                    var optionDataPkg = data.dataPackages[pkg];
                    if(optionDataPkg.currency !=currencyValue){
                        continue;
                    }
                    var attribute = document.createAttribute("data-packageType");
                    attribute.value = optionDataPkg.type;
                    var option;
                    if (isNullOrEmpty(selectedDataPkg)) {
                        option = new Option(optionDataPkg.name, optionDataPkg.id);
                        $("#dataServicePkgData").append(option);
                    } else{
                        if(selectedDataPkg == optionDataPkg.id){
                            option = new Option(optionDataPkg.name, optionDataPkg.id,false,true)
                            $('#dataServicePkgData').append(option);
                        }else{
                            option = new Option(optionDataPkg.name, optionDataPkg.id);
                            $('#dataServicePkgData').append(option);
                        }
                    }
                    option.setAttributeNode(attribute);
                }
            },
            error: function (xhr, status, errmsg) {
                console.log(status);
            }
        });
    }

    function reloadAddOns() {
        var reloadedPackages = [];
        var groupIds = $("#groupNames").val();
        if(isNullOrEmpty(groupIds) == false){
            groupIds = groupIds.join();
        }
        var selectedDataPkg = $("#dataServicePkgData").val();
        var currencyValue = $("#currencyCombo").val();
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/ajax/pkgReload/reloadAddOns",
            data: {
                'groupIds': groupIds,
                'currency': currencyValue
            },
            beforeSend: function () {
                $('#reloadPkg').hide();
                $('#progress').show();
            },
            success: function (data) {
                $('#reloadPkg').show();
                $('#progress').hide();
                $("#dataServicePkgData").html("");
                $("#dataServicePkgData").append('<option value="">--Select--</option>');
                for (pkg in data.allAddOns) {
                    var optionDataPkg = data.allAddOns[pkg];
                    if(optionDataPkg.currency !=currencyValue){
                        continue;
                    }
                    var attribute = document.createAttribute("data-packageType");
                    attribute.value = optionDataPkg.type;
                    var option;
                    if (isNullOrEmpty(selectedDataPkg)) {
                        option = new Option(optionDataPkg.name, optionDataPkg.id);
                        $("#dataServicePkgData").append(option);
                    } else{
                        if(selectedDataPkg == optionDataPkg.id){
                            option = new Option(optionDataPkg.name, optionDataPkg.id,false,true)
                            $('#dataServicePkgData').append(option);
                        }else{
                            option = new Option(optionDataPkg.name, optionDataPkg.id);
                            $('#dataServicePkgData').append(option);
                        }
                    }
                    option.setAttributeNode(attribute);
                }
            },
            error: function (xhr, status, errmsg) {
                console.log(status);
            }
        });

    }

    function getPackagesBasedOnCurrency() {
        var currencyValue = $("#currencyCombo").val();
        getPackagesBasedOnType();
        $("#currency").removeAttr('value');
        $("#currency").attr('value', currencyValue);

        var priceLabelValue = '<s:property value="getText('product.offer.subscription.price')"/>'+ ' '+ '<s:property value="getText('opening.braces')"/>' + currencyValue + '<s:property value="getText('closing.braces')"/>';
        $("#lbl_price").text(priceLabelValue).change;

        var creditBalanceLabelValue = '<s:property value="getText('product.offer.credit.balance')"/>'+ ' '+ '<s:property value="getText('opening.braces')"/>' + currencyValue + '<s:property value="getText('closing.braces')"/>';
        $("#lbl_creditBalance").text(creditBalanceLabelValue).change;

    }

    function getPackagesBasedOnType() {
            var pkgType = $("#productOfferType").val();
                 var groupIds = $("#groupNames").val();
                if (isNullOrEmpty(groupIds) == false) {
                    groupIds = groupIds.join();
                }

            if (pkgType == '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@BASE.name()" />') {
                $("#btnReload").off("click").on("click", reloadBasePackages);
                $("#validityPeriod").attr('readonly', 'true');
                $("#validityPeriodUnit").attr('disabled', 'true');
                $("#creditBalance").removeAttr('readonly');
                $("#emailTemplateId").attr('disabled', 'true');
                $("#smsTemplateId").attr('disabled', 'true');
                $("#fnfEnabledId").attr('disabled', 'true');
            } else if (pkgType == '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@ADDON.name()" />') {
                $("#btnReload").off("click").on("click", reloadAddOns);
                $("#validityPeriod").removeAttr('readonly');
                $("#validityPeriodUnit").removeAttr('disabled');
                $("#creditBalance").attr('readonly', 'true');
                $("#emailTemplateId").removeAttr('disabled');
                $("#smsTemplateId").removeAttr('disabled');
                $("#fnfEnabledId").removeAttr('disabled');
            }
            var selectedDataServicePkgId = $("#dataServicePkgData").val();
            var currencyValue = $("#currencyCombo").val();

            $.ajax({
                type: "POST",
                url: "${pageContext.request.contextPath}/policydesigner/util/AjaxUtility/getPackages",
                async: true,
                dataType: "json",
                data: {
                    "pkgType": pkgType,
                    "groupIds":groupIds,
                    "currency" : currencyValue
                }, success: function (data) {
                    setOptionValue(data,selectedDataServicePkgId, groupIds ,currencyValue);
                }, error: function (data) {
                    console.log(data);
                }
            });
    }
    function setOptionValue(data,selectedDataServicePkgId, groupIds , currency){
        $("#dataServicePkgData").find('option').remove();
        $("#dataServicePkgData").append("<option value=''>--Select--</option>");

        if (isNullOrEmpty(groupIds)) {
            for(var i in data) {
                $("#dataServicePkgData").append(new Option(data[i].name, data[i].id));
            }
        } else {
            for(var i in data) {
                var optionsAsString;
                var optionData = data[i];
                if (((isNullOrEmpty(groupIds)) || (data[i].id != selectedDataServicePkgId))) {
                    optionsAsString = new Option(optionData.name, optionData.id);
                } else if(data[i].id == selectedDataServicePkgId) {
                    optionsAsString = new Option(optionData.name, optionData.id, false, true);
                }

                $("#dataServicePkgData").append(optionsAsString);
            }
        }
    }

    function validateCreditBalance(){
        var pkgType = $("#productOfferType").val();
        if (pkgType == '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@BASE.name()" />') {

            if (isNullOrEmpty($("#creditBalance").val()) == false) {
                if ($("#creditBalance").val() < 0) {
                    setError('creditBalance', '<s:text name='product.offer.credit.balance.negative'/>');
                    return false;
                }

                if ($("#creditBalance").val() > 999999.00) {
                    setError('creditBalance', '<s:text name='product.offer.credit.balance.invalid'/>');
                    return false;
                }
            }

            if (isPositiveDecimalNumber("creditBalance", $("#creditBalance").val()) == false) {
                return false;
            }
        }
    }

</script>