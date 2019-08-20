
<s:form namespace="/pd/productoffer" action="product-offer" id="autosubscriptionform" method="post" cssClass="form-horizontal"
        onsubmit="return verifyAutoSubscriptionTable()"
        labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9">
    <s:hidden name="_method" value="put"/>
    <s:token/>
    <style>
        #productOfferData{
            display: block;
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
    <script type="text/javascript">
        function reloadProductOffer(){
            var reloadedPackages = [];
            var selectedAutoSubscription = $("#productOfferData").val();
            var groupIds = '${groups}';
            var currency = '${currency}';
            $.ajax({
                type 		: "POST",
                url 		: "${pageContext.request.contextPath}/ajax/pkgReload/reloadProductOfferAddOns",
                data: {
                    'groupIds': groupIds,
                    'currency' : currency
                },
                beforeSend: function(){
                    $('#reloadProductOffer').hide();
                    $('#progressProductOffer').show();
                },
                success: function(data){

                    $('#reloadProductOffer').show();
                    $('#progressProductOffer').hide();
                    $("#productOfferData").html("");
                    for(pkg in data.productOfferAddOns){
                        var productOffer=data.productOfferAddOns[pkg];
                        if(productOffer.currency != currency){
                            continue;
                        }
                        setModalAutoSubscriptionOptions(productOffer,selectedAutoSubscription);
                    }
                },
                error 		: function(xhr,status, errmsg) {
                    console.log(status);
                }
            });
        }

        function setModalAutoSubscriptionOptions(data,  selectedAutoSubscription) {
            var optionAutoSubscription = data;
            var attribute = document.createAttribute("data-packageType");
            attribute.value = optionAutoSubscription.pkgType;

            var option;

            if (isNullOrEmpty(selectedAutoSubscription)) {
                option = new Option(optionAutoSubscription.name, optionAutoSubscription.id)
                $("#productOfferData").append(option);
            } else{
                if(selectedAutoSubscription == optionAutoSubscription.id){
                    option = new Option(optionAutoSubscription.name, optionAutoSubscription.id, false, true)
                    $("#productOfferData").append(option);
                }else{
                    option = new Option(optionAutoSubscription.name, optionAutoSubscription.id)
                    $("#productOfferData").append(option);
                }
            }
            option.setAttributeNode(attribute);
        }
    </script>

    <!-- FIXME CHECK ARIAL-LABLEDBY -->
    <div class="modal col-xs-12" id="productOfferAutoSubscriptionModal" tabindex="-1" role="dialog"
         aria-labelledby="productOfferAutoSubscriptionModal" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title set-title">
                        <s:text name="product.offer.auto.subscription"/>
                    </h4>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-xs-12 col-sm-12">
                            <div class="cl-xs-12 col-sm-12">
                                <div class="col-sm-11">
                                    <s:select name="productOfferAutoSubscriptionRelDatas[0].addOnProductOfferId"
                                              key="product.offer.auto.sub.addon.product.offer"
                                              list="addOnProductOfferForSuggestions" listKey="id" listValue="name"
                                              cssClass="form-control"
                                              id="productOfferData" tabindex="2" cssStyle="width:100%" labelCssClass="col-xs-4 col-lg-3 text-right" elementCssClass="col-xs-8 col-sm-8" />
                                </div>
                                <div class="col-sm-1" style="display: block; padding: 2px 35px 0 0; float:right" id="reloadProductOffer">
                                    <button type="button" style="float: right; height: 25px; width: 35px; margin: 0px 0 0 7px;" class="btn btn-default btn-xs" role="button" id="btnRestore" onclick="reloadProductOffer();">
                                        <span class="glyphicon glyphicon-refresh" title="Refresh"></span>
                                    </button>
                                </div>
                                <div class="col-sm-1" style="display: block;padding:0px 25px 0 0; float:right;display:none;" id="progressProductOffer" >
                                    <img src='${pageContext.request.contextPath}/images/progress.gif' style="width: 100%; float: right; position: relative; top: 4px; right: 4px;margin: -0px 0px 0px 7px;">
                                </div>
                            </div>
                            <s:textarea name="productOfferAutoSubscriptionRelDatas[0].advanceCondition" key="product.offer.auto.sub.advancecondition"  cssClass="form-control" id="advanceCondition" tabindex="1" value="" cssStyle="margin-bottom:10px"/>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-sm btn-primary" type="submit" role="button" id="btnAdd"
                            onclick="return verifyAutoSubscriptionTable();"
                            formaction="${pageContext.request.contextPath}/pd/productoffer/product-offer/${id}/addAutoSubscription"
                            tabindex="3"><span class="glyphicon glyphicon-floppy-disk"></span><s:text
                            name="button.add"/></button>
                    <button type="button" class="btn btn-default" data-dismiss="modal" id="btnCancel" tabindex="4"
                            onclick="$('#productOfferAutoSubscriptionModal').modal('hide');"><s:text name="button.cancel"/></button>
                </div>
            </div>
        </div>
    </div>
</s:form>

<script type="text/javascript">




    function verifyAutoSubscriptionTable() {
        clearErrorMessages(autosubscriptionform)
        var selectedProductOffer = $("#productOfferData option:selected").text();
        return isValidMapping($.trim(selectedProductOffer));
    }

    function isValidMapping(selectedProductOffer) {
        var isValidMapping = true;

        $("#autoSubscriptionTable tbody  tr").each(function () {
            var configuredProductOffer = $(this).children().eq(1).text();

            configuredProductOffer = $.trim(configuredProductOffer);
            if (selectedProductOffer == configuredProductOffer) {
                setError('productOfferData', "<s:text name="product.offer.auto.sub.duplicate.error"/>");
                isValidMapping = false;
                return false;
            }
        });
        return isValidMapping;
    }

    function clearErrorMessages(form) {
        $(".has-error").removeClass("has-error");
        $(".has-success").removeClass("has-success");
        $(".has-feedback").removeClass("has-feedback");
        $(".alert").remove();
        $(".nv-input-error").removeClass("nv-input-error");
        $(".glyphicon-remove").remove();
        $(".alert-danger").remove();
        $(".removeOnReset").remove();
    }

    function setError(elementid, errorText) {
        var curElement = $("#".concat(elementid));
        var parentElement = curElement.parent();
        if (parentElement.parent().hasClass("has-error has-feedback") == false) {
            parentElement.parent().addClass("has-error has-feedback");
            parentElement.append("<span class=\"glyphicon glyphicon-remove form-control-feedback removeOnReset\"></span>");
            parentElement.append("<span class=\"help-block alert-danger removeOnReset\">".concat(errorText).concat("</span>"));
        }
    }

    $(function () {
        $("#productOfferData").select2({
            dropdownParent: $("#productOfferAutoSubscriptionModal")
        });
    });
</script>