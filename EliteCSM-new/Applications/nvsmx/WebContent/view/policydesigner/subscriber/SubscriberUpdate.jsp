<%@page import="com.elitecore.corenetvertex.constants.SubscriberStatus" %>
<%@page import="com.elitecore.corenetvertex.constants.CustomerType" %>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes" %>
<%@page import="com.elitecore.corenetvertex.constants.QuotaProfileType" %>
<%@page import="com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage" %>
<%@page import="com.elitecore.corenetvertex.pm.pkg.datapackage.Package" %>
<%@page import="java.util.List" %>
<%@ page import="com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage" %>
<%@ page import="com.elitecore.corenetvertex.pm.offer.ProductOffer" %>
<%@taglib uri="/struts-tags/ec" prefix="s" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<html>
<head>
    <style>
        .package {
            padding-left: 30px;
            padding-right: 0px;
        }

        button.ui-datepicker-current {
            display: none;
        }
    </style>
</head>
</html>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title">
            <s:text name="subscriber.update"/>
        </h3>
    </div>

    <div class="panel-body">

        <s:form validate="true" action="policydesigner/subscriber/Subscriber/update" cssClass="form-horizontal form-group-sm "
                labelCssClass="col-xs-4 col-sm-5 text-right" elementCssClass="col-xs-8 col-sm-7" theme="bootstrap"
                validator="validateForm()" id="updatesubscriberForm">


            <input type="hidden" value="${subscriber.subscriberIdentity}" id="subscriberIdentity"
                   name="subscriberIdentity"/>
            <div class="row">
                <div class="col-xs-12 col-sm-6 col-lg-4">
                    <s:textfield name="subscriber.subscriberIdentity" key="subscriber.id"
                                 cssClass="form-control focusElement" maxlength="255" readonly="true"/>
                    <s:hidden name="subscriber.createdDate"/>
                    <div class="col-xs-11">
                        <s:select name="subscriber.imsPackage" key="subscriber.imspackage" id="imsPackages"
                                  list="%{imsPackage}" listValue="name" listKey="name" listTitle="name"
                                  cssClass="form-control" elementCssClass="col-xs-8 col-sm-7 package" headerKey=" "
                                  headerValue="-- Select --"/>
                    </div>
                    <div class="col-xs-1" style="padding-top:4px;" id="reloadImsPkg">
                        <button type="button" class="btn btn-default btn-xs" role="button" id="btnRestore"
                                onclick="reloadImsRecords();">
                            <span class="glyphicon glyphicon-refresh" title="Refresh"></span>
                        </button>
                    </div>
                    <div class="col-xs-1" style="padding-top:4px;display:none;" id="progressIms">
                        <img src='${pageContext.request.contextPath}/images/progress.gif' style="width:200%;">
                    </div>
                </div>
                <div class="col-xs-12 col-sm-6 col-lg-4">
                    <s:hidden name="subscriber.subscriberMode" id="subscriberMode" key="subscriber.subscribermode"/>
                    <s:select name="subscriber.syInterface" id="syinterface" key="subscriber.syinterface"
                              list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                              cssClass="form-control" listKey="isBooleanValue()" listValue="getStringNameBoolean()"/>
                </div>
                <div class="col-xs-12 col-sm-6 col-lg-4">
                    <s:if test="%{subscriber.subscriberMode == @com.elitecore.corenetvertex.spr.data.SPRInfo$SubscriberMode@TEST}">
                        <div class="col-xs-11">
                            <s:select name="subscriber.productOffer" key="subscriber.productoffer" id="packages"
                                      cssClass="form-control select2" list="%{productOffers}" listValue="name" listKey="name" listTitle="name"
                                      elementCssClass="col-xs-8 col-sm-7 package" headerKey=""
                                      headerValue="-- Select --" cssStyle="width:100%"/>
                        </div>
                    </s:if>
                    <s:else>
                        <div class="col-xs-11">
                            <s:select name="subscriber.productOffer" key="subscriber.productoffer" id="packages"
                                      cssClass="form-control select2" list="%{liveProductOffers}" listValue="name" listKey="name" listTitle="name"
                                      elementCssClass="col-xs-8 col-sm-7 package" headerKey=""
                                      headerValue="-- Select --" cssStyle="width:100%"/>
                        </div>
                    </s:else>
                    <div class="col-xs-1" style="padding-top:4px;" id="reloadPkg">
                        <button type="button" class="btn btn-default btn-xs" role="button" id="btnReload"
                                onclick="reloadRecords();">
                            <span class="glyphicon glyphicon-refresh" title="Refresh" onclick=""></span>
                        </button>
                    </div>
                    <div class="col-xs-1" style="padding-top:4px;display:none;" id="progress">
                        <img src='${pageContext.request.contextPath}/images/progress.gif' style="width:200%;">
                    </div>
                    <s:hidden name="currencyId" id="currencyId" value="%{currency}" />
                </div>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend>
                        <s:text name="subscriber.identity.attributes"/>
                    </legend>
                    <div class="row" id="identityAttributesDetailContent">
                        <div>
                            <div class="col-sm-4">
                                <s:textfield name="subscriber.msisdn" key="subscriber.msisdn" cssClass="form-control"
                                             maxlength="15" id="msisdn"/>
                                <s:textfield name="subscriber.imsi" key="subscriber.imsi" cssClass="form-control"
                                             maxlength="100"/>
                                <s:textfield name="subscriber.imei" key="subscriber.imei" cssClass="form-control"
                                             maxlength="100"/>
                                <s:textfield name="subscriber.userName" key="subscriber.name" cssClass="form-control"
                                             maxlength="255"/>
                            </div>
                            <div class="col-sm-4">
                                <s:textfield name="subscriber.cui" key="subscriber.cui" cssClass="form-control"
                                             maxlength="255"/>
                                <s:textfield name="subscriber.mac" key="subscriber.mac" cssClass="form-control"
                                             maxlength="100"/>
                                <s:textfield name="subscriber.meid" key="subscriber.meid" cssClass="form-control"
                                             maxlength="100"/>
                            </div>
                            <div class="col-sm-4">
                                <s:textfield name="subscriber.eui64" key="subscriber.eui64" cssClass="form-control"
                                             maxlength="100"/>
                                <s:textfield name="subscriber.modifiedEui64" key="subscriber.modifiedeui64"
                                             cssClass="form-control" maxlength="100"/>
                                <s:textfield name="subscriber.sipURL" key="subscriber.sipurl" cssClass="form-control"
                                             maxlength="200"/>
                            </div>
                        </div>
                    </div>
                </fieldset>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend>
                        <s:text name="subscriber.subscription"/>
                    </legend>
                    <div class="row" id="subscriptionDetailContent">
                        <div>
                            <div class="col-sm-4">
                                <s:textfield name="subscriber.customerType" key="subscriber.customertype"
                                             cssClass="form-control" type="text" id="customerType" maxlength="10"/>
                                <s:textfield name="subscriber.status" key="subscriber.status" cssClass="form-control"
                                             type="text" id="status" maxlength="24"/>
                                <s:datepicker name="subscriber.expiryDate" key="subscriber.expirydate"
                                               parentTheme="bootstrap" changeMonth="true" changeYear="true"
                                               cssClass="form-control" showAnim="slideDown" duration="fast"
                                               showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy"
                                               timepicker="true" timepickerFormat="HH:mm:ss" id="expiryDate"
                                               readonly="true"/>
                            </div>
                            <div class="col-sm-4">
                                <s:textfield name="subscriber.parentId" key="subscriber.parentid"
                                             cssClass="form-control" maxlength="255"/>
                                <s:textfield name="subscriber.groupName" key="subscriber.groupname"
                                             cssClass="form-control" maxlength="255"/>
                            </div>
                            <div class="col-sm-4">
                                <s:textfield name="subscriber.arpu" key="subscriber.arpu" cssClass="form-control"
                                             maxlength="20" onkeypress="return isNaturalInteger(event);" type="number"/>
                                <s:textfield name="subscriber.billingDate" key="subscriber.billingdate"
                                             cssClass="form-control" maxlength="2"
                                             onkeypress="return isNaturalInteger(event);" type="number" readonly="true"/>
                            </div>
                        </div>
                    </div>
                </fieldset>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend>
                        <s:text name="subscriber.payg.roadming"/>
                    </legend>
                    <div class="row" id="paygDetailContent">
                        <div>
                            <div class="col-sm-4">
                                <s:select name="subscriber.paygInternationalDataRoaming" id="paygRoaming"
                                          key="subscriber.international.data"
                                          list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                                          cssClass="form-control" listKey="isBooleanValue()"
                                          listValue="getDisplayBooleanValue()"/>
                            </div>
                        </div>
                    </div>
                </fieldset>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend>
                        <s:text name="subscriber.personal"/>
                    </legend>
                    <div class="row" id="personalDetailContent">
                        <div>
                            <div class="col-sm-4">
                                <s:textfield name="subscriber.email" key="subscriber.email" cssClass="form-control"
                                             maxlength="100"/>
                                <s:textfield name="subscriber.password" key="subscriber.password"
                                             cssClass="form-control" maxlength="100"/>
                                <s:select name="subscriber.passwordCheck" id="passwordCheck"
                                          key="subscriber.passwordcheck"
                                          list="@com.elitecore.corenetvertex.constants.CommonStatusValues@values()"
                                          cssClass="form-control" listKey="isBooleanValue()"
                                          listValue="getStringNameBoolean()"/>
                            </div>
                            <div class="col-sm-4">
                                <s:select name="subscriber.encryptionType" id="encryptionType"
                                          key="subscriber.encryptiontype"
                                          list="@com.elitecore.corenetvertex.constants.PasswordEncryptionType@values()"
                                          cssClass="form-control" listKey="val" listValue="displayVal"/>
                                <s:datepicker name="subscriber.birthdate" id="birthDate" key="subscriber.birthdate"
                                               parentTheme="bootstrap" changeMonth="true" changeYear="true"
                                               cssClass="form-control" showAnim="slideDown" duration="fast"
                                               showOn="focus" placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy"
                                               readonly="true" maxDate="0"/>
                                <s:textfield name="subscriber.phone" key="subscriber.phone" cssClass="form-control"
                                             maxlength="15"/>
                            </div>
                            <div class="col-sm-4">
                                <s:textfield name="subscriber.country" key="subscriber.country" cssClass="form-control"
                                             type="text" id="country"/>
                                <s:textfield name="subscriber.city" key="subscriber.city" cssClass="form-control"
                                             maxlength="20"/>
                                <s:textfield name="subscriber.area" key="subscriber.area" cssClass="form-control"
                                             maxlength="20"/>
                                <s:textfield name="subscriber.zone" key="subscriber.zone" cssClass="form-control"
                                             maxlength="20"/>
                            </div>
                        </div>
                    </div>
                </fieldset>
            </div>


            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend>
                        <s:text name="subscriber.professional"/>
                    </legend>
                    <div class="row" id="professionalDetailContent">
                        <div>
                            <div class="col-sm-4">
                                <s:textfield name="subscriber.company" key="subscriber.company" cssClass="form-control"
                                             maxlength="512"/>
                                <s:textfield name="subscriber.cadre" key="subscriber.cadre" cssClass="form-control"
                                             maxlength="5"/>
                            </div>
                            <div class="col-sm-4">
                                <s:textfield name="subscriber.department" key="subscriber.department"
                                             cssClass="form-control" maxlength="20"/>
                            </div>
                            <div class="col-sm-4">
                                <s:textfield name="subscriber.role" key="subscriber.role" cssClass="form-control"
                                             maxlength="20"/>
                            </div>
                        </div>
                    </div>
                </fieldset>
            </div>

            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend>
                        <s:text name="subscriber.other"/>
                    </legend>
                    <div class="row" id="otherDetailContent">
                        <div>
                            <div class="col-sm-4">
                                <s:select name="subscriber.subscriberLevelMetering"
                                          value="subscriber.subscriberLevelMetering"
                                          key="subscriber.subscriberlevelmetering" cssClass="form-control"
                                          list="@com.elitecore.corenetvertex.spr.data.SPRInfo$SubscriberLevelMetering@values()"
                                          listKey="name()" listValue="getText(status)"/>
                                <s:textfield name="subscriber.param1" key="subscriber.param1" cssClass="form-control"/>
                                <s:textfield name="subscriber.callingStationId" key="subscriber.callingstationid"
                                             cssClass="form-control"/>
                                <s:textfield name="subscriber.billingAccountId" key="subscriber.billing.account" cssClass="form-control" maxlength="100" />
                            </div>
                            <div class="col-sm-4">
                                <s:textfield name="subscriber.param2" key="subscriber.param2" cssClass="form-control"/>
                                <s:textfield name="subscriber.param3" key="subscriber.param3" cssClass="form-control"/>
                                <s:textfield name="subscriber.framedIp" key="subscriber.framedip"
                                             cssClass="form-control"/>
                                <s:textfield name="subscriber.serviceInstanceId" key="subscriber.service.instance" cssClass="form-control" maxlength="100" />
                            </div>
                            <div class="col-sm-4">
                                <s:textfield name="subscriber.param4" key="subscriber.param4" cssClass="form-control"/>
                                <s:textfield name="subscriber.param5" key="subscriber.param5" cssClass="form-control"/>
                                <s:textfield name="subscriber.nasPortId" key="subscriber.nasportid"
                                             cssClass="form-control"/>
                            </div>
                        </div>
                    </div>
                </fieldset>
            </div>
            <div align="center">
                <buttton class="btn  btn-sm btn-primary" type="button" role="button" onclick="callUpdateModel();"><span
                        class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></buttton>
                <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel"
                        style="margin-right:10px;"
                        onclick="getEncodeURI('${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/view?subscriberIdentity=','${subscriber.subscriberIdentity}')">
                    <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text
                        name="subscriber.subscriberview"/>
                </button>
            </div>

            <div class="modal fade" id="updateSubscribers" tabindex="-1" role="dialog"
                 aria-labelledby="updateSubscriberLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                            <h4 class="modal-title" id="updateSubscriberLabel">Update Subscriber</h4>
                        </div>
                        <div class="modal-body">
                            <div>
                                <s:select name="updateAction" key="Update Action" cssClass="form-control"
                                          list="@com.elitecore.nvsmx.ws.util.UpdateActions@values()" listKey="val()"
                                          listValue="label()" id="updateAction" labelCssClass="col-xs-4"
                                          elementCssClass="col-xs-8"/>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary" data-dismiss="modal"><s:text
                                    name="subscription.close"></s:text></button>
                            <button class="btn btn-primary" role="submit" value="Update" type="submit"
                                    formaction="update" onclick="hideUpdateModel();"><s:text name="button.update"/></button>
                        </div>
                    </div>
                </div>
            </div>

        </s:form>
    </div>
</div>


<% List<ProductOffer> dataPackages = (List<ProductOffer>) request.getAttribute(Attributes.DATA_PACKAGES); %>
<% String subscriberPackageName = (String) request.getAttribute("subscriberPackageName"); %>
<% List<IMSPackage> imsPackages = (List<IMSPackage>) request.getAttribute(Attributes.IMS_PACKAGES); %>
<% String subscriberImsPackageName = (String) request.getAttribute("subscriberImsPackageName"); %>
<% List<RnCPackage> rncPackages = (List<RnCPackage>) request.getAttribute(Attributes.RNC_PACKAGES); %>
<% String subscriberRncPackageName = (String) request.getAttribute("subscriberRncPackageName"); %>

<script type="text/javascript">
    $(document).ready(function () {
        $("#packages select").val('<%=subscriberPackageName%>');
        $("#imsPackages select").val('<%=subscriberImsPackageName%>');
        $("#rncPackage select").val('<%=subscriberRncPackageName%>');
        autoCompleteForCustomerType();
        autoCompleteForStatus();
        autoCompleteForCountry();
        $(".select2").select2();
        fillPackageList();

    });

    var jsonData;
    var flag = false;

    function autoCompleteForCustomerType() {
        $('#customerType').autocomplete();
        var optionsForCustomerType = ['<%=CustomerType.PREPAID.val%>', '<%=CustomerType.POSTPAID.val%>'];
        commonAutoComplete("customerType", optionsForCustomerType);
    }

    function autoCompleteForStatus() {
        $('#status').autocomplete();
        var optionsForStatus = ['<%=SubscriberStatus.ACTIVE.name()%>', '<%=SubscriberStatus.INACTIVE.name()%>'];
        commonAutoComplete("status", optionsForStatus);
    }

    function autoCompleteForCountry() {
        $('#country').autocomplete();
        var optionsForCountry = "${countryNames}";
        commonAutoComplete("country", optionsForCountry.split(','));
    }

    //fixme -- remove validation for live,live2 and base package
    function reloadPackages(productOffers, selectedProductOffer) {
        $('#packages').empty();
        $('#packages').append(new Option('-- Select --', ''));

        for(var i=0;i<productOffers.length;i++) {
            var optionProductOffer = productOffers[i].name;
            var currency = productOffers[i].currency;
            if(isNullOrEmpty(selectedProductOffer)){
                $('#packages').append(new Option(optionProductOffer,optionProductOffer));
            }else{
                if(selectedProductOffer == optionProductOffer){
                    $('#packages').append(new Option(optionProductOffer+'('+currency+')',optionProductOffer,false,true));
                }else{
                    $('#packages').append(new Option(optionProductOffer+'('+currency+')',optionProductOffer));
                }
            }

        }
    }

    function reloadImsPackages(imsPackages) {
        $('#imsPackages').empty();
        $('#imsPackages').append(new Option('-- Select --', ''));
        for (var i = 0; i < imsPackages.length; i++) {
            $('#imsPackages').append(new Option(imsPackages[i].name, imsPackages[i].name));
        }
    }


    function reloadRnCLivePackages(liveRncPackages) {
        $('#rncPackages').empty();
        for (var i = 0; i < liveRncPackages.length; i++) {
            if (liveRncPackages[i].packageMode == 'LIVE' || liveRncPackages[i].packageMode == 'LIVE2') {
                $('#rncPackages').append(new Option(liveRncPackages[i].name, liveRncPackages[i].name));
            }
        }
    }

    function reloadRnCDataPackages(rncPackage) {

        $('#rncPackages').empty();
        for (var i = 0; i < rncPackage.length; i++) {
            if (rncPackage[i].packageMode == 'TEST' || rncPackage[i].packageMode == 'LIVE' || rncPackage[i].packageMode == 'LIVE2') {
                $('#rncPackages').append(new Option(rncPackage[i].name, rncPackage[i].name));
            }
        }
    }

    function reloadRecords() {
        var currencyValue = $("#currencyId").val();
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/ajax/pkgReload/reloadProductOffers",
            data: {
                'currency': currencyValue
            },
            beforeSend: function () {
                $('#reloadPkg').attr('style', 'display:none');
                $('#progress').show();
            },
            success: function (data) {
                jsonData = data;
                flag = true;
                reloadAllPackages(data);
                $('#progress').hide();
                $('#reloadPkg').attr('style', 'padding-top:4px;');
            },
            error: function (xhr, status, errmsg) {
                console.log(status);
            }
        });
    }

    var jsonDataRNC;
    var flagRNC = false;

    function reloadRnCRecords() {
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/ajax/pkgReload/reloadRnCPackages",
            beforeSend: function () {
                $('#reloadRnCPkg').attr('style', 'display:none');
                $('#progress').show();
            },
            success: function (data) {

                jsonDataRNC = data;
                flagRNC = true;
                reloadRnCPackages(data);
                $('#progress').hide();
                $('#reloadRnCPkg').attr('style', 'padding-top:4px;');
            },
            error: function (xhr, status, errmsg) {
                console.log(status);
            }
        });
    }

    function reloadRnCPackages(pkgs) {
        var subscriberMode = $("#subscriberMode").val();
        if (subscriberMode == 'TEST') {
            reloadRnCDataPackages(pkgs.rncPackages);
        }
        if (subscriberMode == 'LIVE') {
            reloadRnCLivePackages(pkgs.rncLivePackages);
        }
    }

    var jsonDataIms;
    var flagIms = false;

    function reloadImsRecords() {
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/ajax/pkgReload/reloadImsPackages",
            beforeSend: function () {
                $('#reloadImsPkg').attr('style', 'display:none');
                $('#progressIms').show();
            },
            success: function (data) {
                jsonDataIms = data;
                flagIms = true;
                reloadAllImsPackages(data);
                $('#progressIms').hide();
                $('#reloadImsPkg').attr('style', 'padding-top:4px;');
            },
            error: function (xhr, status, errmsg) {
                console.log(status);
            }
        });
    }

    function reloadAllImsPackages(imsPkgs) {
        var subscriberMode = $("#subscriberMode").val();
        if (subscriberMode == 'TEST') {
            reloadImsPackages(imsPkgs.imsPackages);
        }
        if (subscriberMode == 'LIVE') {
            reloadImsPackages(imsPkgs.imsLivePackages);
        }
    }

    function fillPackageList() {
        var selectedProductOffer = $("#packages").val();
        $('#packages').empty();
        $('#packages').append(new Option('-- Select --',''));

        <%int size = dataPackages.size();
          for (int i=0;i<size;i++) {
            ProductOffer productOffer = dataPackages.get(i);
            String optionProductOffer = productOffer.getName();
        %>
        if(isNullOrEmpty(selectedProductOffer)) {
            $('#packages').append(new Option('<%= optionProductOffer %>', '<%= optionProductOffer %>'));
        }else{
            if(selectedProductOffer == '<%=optionProductOffer%>'){
                $('#packages').append(new Option('<%= optionProductOffer %> (<%= productOffer.getCurrency() %>)', '<%= optionProductOffer %>',false,true));
            }else{
                $('#packages').append(new Option('<%= optionProductOffer %> (<%= productOffer.getCurrency() %>) ', '<%= optionProductOffer %>'));
            }
        }

        <%
          }
        %>
        $('#packages').select2();
    }

    function reloadAllPackages(pkgs) {
        var subscriberMode = $("#subscriberMode").val();
        var selectedProductOffer = $("#packages").val();
        if (subscriberMode == 'TEST') {
            reloadPackages(pkgs.productOffers, selectedProductOffer);
        }
        if (subscriberMode == 'LIVE') {
            reloadPackages(pkgs.liveProductOffers, selectedProductOffer);
        }
    }

    function validateForm() {
        clearErrorMessagesById('packages');
        var syInterfaceVal = $("#syinterface").val();
        var dataPackage = $('#packages').val();
        var msisdn = $("#msisdn").val();
        if (syInterfaceVal == 'false') {
            <% int allsize = dataPackages.size();
                for (int i=0;i<allsize;i++) {
               ProductOffer pkgData = dataPackages.get(i);
           %>
            var quotaProfileType = '<%= pkgData.getDataServicePkgData() == null ? null : pkgData.getDataServicePkgData().getQuotaProfileType().name()%>';
            var actualType = '<%=QuotaProfileType.SY_COUNTER_BASED.name()%>';
            var pkgName = '<%=pkgData.getName()%>';
            if (dataPackage == pkgName) {
                if (quotaProfileType == actualType) {
                    setError("packages", "<s:text name='wrong.package.selected' />");
                    return false;
                }
            }
            <%}%>
        }
        if (msisdn.trim().length != 0) {
            if (msisdn.trim().length < 5 || msisdn.trim().length > 15) {
                setError("msisdn", "<s:text name='subscriber.msisdn.invalid' />");
                return false;
            }
        }
        return true;
    }

    function callUpdateModel() {
        if (validateForm()) {
            $("#updateSubscribers").modal('show');
        }
    }

    function hideUpdateModel() {
        $("#updateSubscribers").modal('hide');
    }

    function submitForm() {
        document.forms["updatesubscriberForm"].submit();
    }

</script>





