<s:form namespace="/pd/productoffer" action="product-offer" id="productspecform" method="post" cssClass="form-horizontal"
        onsubmit="return verifyRelationTable()"
        labelCssClass="col-xs-4" elementCssClass="col-xs-8">
    <s:hidden name="_method" value="put"/>
    <s:token/>
    <script type="text/javascript">
        function reloadRnCRecords(){
            var reloadedPackages = [];
            var productOfferMode = '${packageMode}';
            var selectedRncPackage = $("#dataServiceRnCPkgData").val();
            var groupIds = '${groups}';
            var currency = '${currency}';
            $.ajax({
                type 		: "POST",
                url 		: "${pageContext.request.contextPath}/ajax/pkgReload/reloadRnCPackages",
                data: {
                    'groupIds': groupIds,
                    'currency' : currency
                },
                beforeSend: function(){
                    $('#reloadRnCPkg').hide();
                    $('#progressRnC').show();
                },
                success: function(data){
                    $('#reloadRnCPkg').show();
                    $('#progressRnC').hide();
                    $("#dataServiceRnCPkgData option").remove();
                    if (productOfferMode == '<s:property value="@com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()" />' || productOfferMode == '<s:property value="@com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()" />' ) {
                        for(pkg in data.rncLivePackages){
                            var rncPackageData=data.rncLivePackages[pkg];
                            if(rncPackageData.currency != currency){
                                continue;
                            }
                            setServiceModalRncPackageOptions(rncPackageData, selectedRncPackage);
                        }
                    } else {
                        for(pkg in data.rncPackages){
                            var rncPackageData=data.rncLivePackages[pkg];
                            if(rncPackageData.currency != currency){
                                continue;
                            }
                            setServiceModalRncPackageOptions(rncPackageData, selectedRncPackage);
                        }
                    }
                    $("#dataServiceRnCPkgData").select2({
                        dropdownParent: $("#productOfferServiceRelationModal")
                    })
                },
                error 		: function(xhr,status, errmsg) {
                    console.log(status);
                }
            });
        }
        
        function setServiceModalRncPackageOptions(data,  selectedRncPackage) {
            var optionRncPackage = data;
            var attribute = document.createAttribute("data-packageType");
            attribute.value = optionRncPackage.pkgType;

            var option;
            if(data.pkgType.indexOf("<s:property value="type" />") != -1) {
                if(data.pkgType == '<s:property value="@com.elitecore.corenetvertex.pkg.RnCPkgType@MONETARY_ADDON.name()" />') {

                    if (isNullOrEmpty(selectedRncPackage)) {
                        option = new Option(optionRncPackage.name, optionRncPackage.id)
                        $("#dataServiceRnCPkgData").find('optgroup[label="Monetary AddOn"]').append(option);
                    } else{
                        if(selectedRncPackage == optionRncPackage.id){
                            option = new Option(optionRncPackage.name, optionRncPackage.id, false, true)
                            $("#dataServiceRnCPkgData").find('optgroup[label="Monetary AddOn"]').append(option);
                        }else{
                            option = new Option(optionRncPackage.name, optionRncPackage.id)
                            $("#dataServiceRnCPkgData").find('optgroup[label="Monetary AddOn"]').append(option);
                        }
                    }
                }else if(data.pkgType == '<s:property value="@com.elitecore.corenetvertex.pkg.RnCPkgType@NON_MONETARY_ADDON.name()" />'){
                    if (isNullOrEmpty(selectedRncPackage)) {
                        option = new Option(optionRncPackage.name, optionRncPackage.id)
                        $("#dataServiceRnCPkgData").find('optgroup[label="Non-Monetary AddOn"]').append(option);
                    } else{
                        if(selectedRncPackage == optionRncPackage.id){
                            option = new Option(optionRncPackage.name, optionRncPackage.id, false, true)
                            $("#dataServiceRnCPkgData").find('optgroup[label="Non-Monetary AddOn"]').append(option);
                        }else{
                            option = new Option(optionRncPackage.name, optionRncPackage.id)
                            $("#dataServiceRnCPkgData").find('optgroup[label="Non-Monetary AddOn"]').append(option);
                        }
                    }
                }else{
                    if (isNullOrEmpty(selectedRncPackage)) {
                        option = new Option(optionRncPackage.name, optionRncPackage.id)
                        $("#dataServiceRnCPkgData").append(option);
                    } else{
                        if(selectedRncPackage == optionRncPackage.id){
                            option = new Option(optionRncPackage.name, optionRncPackage.id, false, true)
                            $("#dataServiceRnCPkgData").append(option);
                        }else{
                            option = new Option(optionRncPackage.name, optionRncPackage.id)
                            $("#dataServiceRnCPkgData").append(option);
                        }
                    }
                }
                option.setAttributeNode(attribute);
            }
        }
    </script>

    <div class="modal col-xs-12" id="productOfferServiceRelationModal" tabindex="-1" role="dialog"
         aria-labelledby="productOfferServiceRelationModal" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title set-title">
                        <s:text name="product.offer.service.package.relation"/>
                    </h4>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-xs-12 col-sm-12">
                            <s:select name="productOfferServicePkgRelDataList[0].serviceId"
                                      key="product.offer.service.type" list="serviceDataList" listKey="id"
                                      listValue="name" cssClass="form-control focusElement"
                                      labelCssClass="col-xs-4 col-lg-3 text-right" elementCssClass="col-xs-8 col-sm-8"
                                      id="selectedServiceId" cssStyle="margin-bottom:10px;" tabindex="1"/>
                            <div class="cl-xs-12 col-sm-12">
                                <div class="col-sm-11">
                                    <s:if test="%{type == @com.elitecore.corenetvertex.pkg.PkgType@ADDON.name()}">
                                        <s:select name="productOfferServicePkgRelDataList[0].rncPackageId"
                                                  list="{}" listValue="name" listKey="id" key="product.offer.pkg"
                                                  id="dataServiceRnCPkgData" tabindex="2" cssStyle="width:100%" labelCssClass="col-xs-4 col-lg-3 text-right" elementCssClass="col-xs-8 col-sm-8">
                                            <s:optgroup label="Monetary AddOn" list="rncMonetaryAddOnList"
                                                        listValue="name" listKey="id"/>
                                            <s:optgroup label="Non-Monetary AddOn"
                                                        list="rncNonMonetaryAddOnList"
                                                        listValue="name" listKey="id"/>
                                        </s:select>
                                    </s:if>
                                    <s:else>
                                        <s:select name="productOfferServicePkgRelDataList[0].rncPackageId"
                                                  key="product.offer.pkg"
                                                  list="rncPkgDataList" listKey="id" listValue="name"
                                                  cssClass="form-control"
                                                  id="dataServiceRnCPkgData" tabindex="2" cssStyle="width:100%" labelCssClass="col-xs-4 col-lg-3 text-right" elementCssClass="col-xs-8 col-sm-8"/>
                                    </s:else>
                                </div>
                                <div class="col-sm-1" style="display: block; padding: 2px 35px 0 0; float:right" id="reloadRnCPkg">
                                    <button type="button" style="float: right; height: 25px; width: 35px; margin: 0px 0 0 7px;" class="btn btn-default btn-xs" role="button" id="btnRestore" onclick="reloadRnCRecords();">
                                        <span class="glyphicon glyphicon-refresh" title="Refresh"></span>
                                    </button>
                                </div>
                                <div class="col-sm-1" style="display: block;padding:0px 25px 0 0; float:right;display:none;" id="progressRnC" >
                                    <img src='${pageContext.request.contextPath}/images/progress.gif' style="width: 100%; float: right; position: relative; top: 4px; right: 4px;margin: -0px 0px 0px 7px;">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <%--<s:submit cssClass="btn  btn-sm btn-primary" id="btnSubmit"  type="button" role="button" tabindex="9" action="${pageContext.request.contextPath}/pd/productoffer/product-offer/${id}/addServicePkgRelations">
                        <span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/>
                    </s:submit>--%>
                    <button class="btn btn-sm btn-primary" type="submit" role="button" id="btnAdd"
                            onsubmit="return verifyRelationTable();"
                            formaction="${pageContext.request.contextPath}/pd/productoffer/product-offer/${id}/addServicePkgRelations"
                            tabindex="3"><span class="glyphicon glyphicon-floppy-disk"></span><s:text
                            name="button.add"/></button>
                    <button type="button" class="btn btn-default" data-dismiss="modal" id="btnCancel" tabindex="4"
                            onclick="$('#productOfferServiceRelationModal').modal('hide');"><s:text name="button.cancel"/></button>
                </div>
            </div>
        </div>
    </div>
</s:form>

<script type="text/javascript">

    function verifyRelationTable() {
        var selectedService = $("#selectedServiceId option:selected").text();
        return isValidMapping($.trim(selectedService));
    }

    function isValidMapping(selectedService) {
        var isValidMapping = true;
        $("#servicePkgRelationTable tbody  tr").each(function () {
            var configuredService = $(this).children().eq(0).text();
            configuredService = $.trim(configuredService);
            if (selectedService == configuredService) {
                setError('selectedServiceId', "<s:text name="product.offer.service.relation.service.already.configured"/>");
                isValidMapping = false;
                return false;
            }
        });
        return isValidMapping;
    }

    function setError(elementid, errorText) {
        var curElement = $("#".concat(elementid));
        var parentElement = curElement.parent();
        if(parentElement.parent().hasClass("has-error has-feedback") == false){
            parentElement.parent().addClass("has-error has-feedback");
            parentElement.append("<span class=\"glyphicon glyphicon-remove form-control-feedback removeOnReset\"></span>");
            parentElement.append("<span class=\"help-block alert-danger removeOnReset\">".concat(errorText).concat("</span>"));
        }
    }

    $(function () {
        $("#dataServiceRnCPkgData").select2({
            dropdownParent: $("#productOfferServiceRelationModal")
        });
    });
</script>