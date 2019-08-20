<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@ taglib uri="http://code.google.com/p/jcaptcha4struts2/taglib/2.0" prefix="jCaptcha" %>


<script src="${pageContext.request.contextPath}/js/third-party/highcharts.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<style type="text/css">
	#add-subscription{
		float: right;
		padding: 10px 20px;
		font-size: 15px;
	}
	#ui-datepicker-div{
		z-index: 1040 !important;
	}
	#captcha-response{
		padding-top: 0 !important;
	}
	button.ui-datepicker-current {
		display: none;
	}
	#add-subscription .form-control-feedback{
		line-height : 80px;
	}
	.info-label{
		padding: 18px 30px 10px;
		margin-bottom: 0px;
		color: red;
		font-weight: 300;
	}
</style>
<script>
    function renderPkg(data, type, thisBean){
        var rncPkg = '<s:property value="%{@com.elitecore.corenetvertex.constants.CommonConstants@RNC}"/>';
        var topupPkg = '<s:property value="%{@com.elitecore.corenetvertex.pd.topup.TopUpType@TOP_UP.name()}"/>';
        var spareTopupPkg = '<s:property value="%{@com.elitecore.corenetvertex.pd.topup.TopUpType@SPARE_TOP_UP.name()}"/>';
        var bodPackage ='<s:property value="%{@com.elitecore.corenetvertex.spr.data.SubscriptionType@BOD.name()}"/>';
        var pkgId = thisBean.packageId;
        var pkgType = thisBean.packageType;

        if(pkgType == rncPkg){
            return "<a href='${pageContext.request.contextPath}/pd/rncpackage/rnc-package/"+pkgId+"'>"+data+"</a>"
        } else if(pkgType == topupPkg || pkgType == spareTopupPkg){
            return "<a href='${pageContext.request.contextPath}/pd/datatopup/data-topup/"+pkgId+"'>"+data+"</a>";
        }else if(pkgType == bodPackage){
            return "<a href='${pageContext.request.contextPath}/pd/bodpackage/bod-package/"+pkgId+"'>"+data+"</a>";
        }
        return "<a href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId="+pkgId+"'>"+data+"</a>";
    }

    function renderProductOffer(data, type, thisBean){
        if(data == undefined || data == ""){
            return "";
        }
        return "<a href='${pageContext.request.contextPath}/pd/productoffer/product-offer/"+thisBean.productOfferId+"'>"+data+"</a>";
    }

</script>
<div class="row" >
	<s:if test="%{#request.showWarning == 'true'}">
		<label class="col-xs-8 info-label" id="lbl_pkgGroup">
			<s:text name="subscription.restriction.note"></s:text>
		</label>
	</s:if>

	<%--Subscrition Modal jsp--%>
	<%@ include file="addOnSubscriptionModal.jsp" %>

	<div class="container-fluid" id="subscriptions">
		<div id="sub">
			<nv:dataTable
					list="${subscriptionInformationJsonArray}"
					cssClass="table table-condensed table-blue no-footer" id="addOns"
					width="100%"
					subTableUrl="/searchTable/policydesigner/subscriber/Subscriber/subscriptionDetail"
					showPagination="false" showInfo="false">
				<nv:dataTableColumn title="Package Name" beanProperty="packageName" renderFunction="renderPkg"/>
				<nv:dataTableColumn title="Offer Name" beanProperty="productOfferName" renderFunction="renderProductOffer"/>
				<nv:dataTableColumn title="pkgDTypeDisplayValue" beanProperty="packageType"  style="display:none;" tdStyle="display:none;"/>
				<nv:dataTableColumn title="Type" beanProperty="pkgTypeDisplayValue"/>
				<nv:dataTableColumn title="Start Time" beanProperty="startTime" />
				<nv:dataTableColumn title="Expiry Time" beanProperty="endTime" />
				<nv:dataTableColumn title="Status" beanProperty="addOnStatus" />
				<nv:dataTableColumn title="Priority" beanProperty="priority"/>
				<nv:dataTableColumn
						icon="<span class='glyphicon glyphicon-trash'></span>"
						hrefurl="delete:javascript:unsubscribeAddon()"
						style="width:20px;" id="deleteId" />
			</nv:dataTable>
		</div>

	</div>

</div>

<%--Delete Subscription Modal--%>
<%@ include file="deleteSubscriptionsModal.jsp" %>

<script type="text/javascript">
    //AutoFocus functionality for the modal.
    $('.modal').on('shown.bs.modal', function () {
        $(this).find('[autofocus]').focus();
    });

    var allTopUpsString = '<s:property value="%{allTopUpsString}" escapeHtml="false" />';
    var liveTopUpString = '<s:property value="%{liveTopUpsString}" escapeHtml="false"/>';
    if(isNullOrEmpty(allTopUpsString) == false) {
        allTopUpsString = JSON.parse(allTopUpsString.replace(/(\r\n|\n|\r)/gm, ""));
    }
    if(isNullOrEmpty(liveTopUpString) == false){
        liveTopUpString=JSON.parse((liveTopUpString.replace(/(\r\n|\n|\r)/gm,"")));
    }

    var allAddOnList = '<s:property value="%{allAddOnString}" escapeHtml="false"/> ';
    if(isNullOrEmpty(allAddOnList) == false){
        allAddOnList = JSON.parse(allAddOnList.replace(/(\r\n|\n|\r)/gm,""));
    }

    var liveAddOnList = '<s:property value="%{liveAddOnString}" escapeHtml="false"/> ';
    if(isNullOrEmpty(liveAddOnList) == false){
        liveAddOnList = JSON.parse(liveAddOnList.replace(/(\r\n|\n|\r)/gm,""));

    }

    var allBodDataList = '<s:property value="%{allBodDataString}" escapeHtml="false"/> ';
    if(isNullOrEmpty(allBodDataList) == false){
        allBodDataList = JSON.parse(allBodDataList.replace(/(\r\n|\n|\r)/gm,""));
    }

    var liveBodDataList = '<s:property value="%{liveBodDataString}" escapeHtml="false"/> ';
    if(isNullOrEmpty(liveBodDataList) == false){
        liveBodDataList = JSON.parse(liveBodDataList.replace(/(\r\n|\n|\r)/gm,""));
    }



    function validateRemark(){
        var remark = $("#remark").val();
        if(isNullOrEmpty(remark)){
            setError("remark","Please Specify Remark !");
            return false;
        }
        return true;
    }

    var description="";
    function unsubscribeAddon(){
        $('td:last-child').on('click', function (e) {
            if(('${subscriber.status}').toLowerCase() == 'deleted' || ('${subscriber.status}').toLowerCase() == 'inactive'){
                $("#notPermittedForDelete").modal('show');
            }else if('${subscriber.getProfileExpiredHours()}' >=0){
                $("#notPermittedForProfileExpired").modal('show');
            }else{
                var myRow = $(this).parent();
                var dataTableRef = $("#addOns").DataTable({});
                var row = dataTableRef.row( myRow );


                var addonSubscriptionId = row.data().addonSubscriptionId;
                var addonId = row.data().packageId;
                var packageType = row.data().packageType;
                if(packageType == '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@BASE.name()"/>'
                    || packageType == '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@PROMOTIONAL.name()"/>' ){
                    return addWarning(".popup",packageType +" "+" <s:text name='susbcription.base.promotional.unsubscription.error'/>");
                }
                if(row.data().packageDescription == null){
                    description = "";
                }else{
                    description = row.data().packageDescription;
                }
                $("#addonId").val(addonId);
                $("#addonSubscriptionId").val(addonSubscriptionId);
                $("#unSubscriptionPkgType").val(packageType);
                $('#remark').val("");
                $('#jCaptchaResponse').val("");
                $('#jCaptchaResponse').attr("autofocus","autofocus");
                $('label[for="unsubscribeDescription"]').text(description);
                $("#deleteSubscription").modal('show');
            }
        });
    }


    function showDetails(){
        var subscriberMode = '${subscriber.subscriberMode}';
        if (flag == false) {
            if (subscriberMode == 'TEST') {
                showPackageDetails(allAddOnList, allTopUpsString,allBodDataList);
            } else if (subscriberMode == 'LIVE') {
                showPackageDetails(liveAddOnList, liveTopUpString,liveBodDataList);
            }
        } else if (flag) {
            if (subscriberMode == 'TEST') {
                showPackageDetails(jsonData.productOfferAddOns, jsonData.allTopUps,jsonData.allBodData);
            } else if (subscriberMode == 'LIVE') {
                showPackageDetails(jsonData.liveProductOffersAddons, jsonData.liveTopUps,jsonData.liveBodData);
            }
        }
    }

    function addAddOnDetails(addOnId,description,price,validityPeriod,type,currency){
        $("#addonId").val(addOnId);
        $("#pkgType").val(type);
        $('label[for="description"]').html(description);
        $('label[for="price"]').text(price);
        $('label[for="validity"]').text(validityPeriod);
        if(type == 'TOP_UP'){
            type = 'Top-Up';
        }else if (type == 'SPARE_TOP_UP'){
            type = 'Spare Top-Up';
        }
        $('#subscribeForm_Type').text(type);
    }

    function showPackageDetails(addOns,topUps,bods){
        var selectedPackageId = $('#selectedAddOn').val();
        var description, price, validityPeriod, type,addOnId,currency;
        if (isNullOrEmpty(selectedPackageId) == false) {
            var idAndType = selectedPackageId.split("|");
            type = idAndType[idAndType.length - 1];
            addOnId = idAndType[0];
        }
        if (isNullOrEmpty(type) == false && type.toUpperCase() == 'ADDON') {
            for (var i = 0; i < addOns.length; i++) {
                var addOn = addOns[i];
                if (addOnId == addOn['id']) {
                    addOnId = addOn.id;
                    description = addOn.description == null ? "" : addOn.description.replace(/\n|\r\n|\r/g, '<br/>');
                    price = addOn.subscriptionPrice == null ? "N/A" : addOn.subscriptionPrice;
                    validityPeriod = (isNullOrEmpty(addOn.validityPeriod)? addOn.validity : addOn.validityPeriod) + " " + addOn.validityPeriodUnit;
                    type = 'AddOn';
					currency = addOn.currency == null ? "()" : addOn.currency;
                    break;
                }
            }
        }else if(isNullOrEmpty(type) == false && type.toUpperCase() == 'BOD') {
            for (var i = 0; i < bods.length; i++) {
                var bod = bods[i];
                if (addOnId == bods['id']) {
                    addOnId = bods.id;
                    description = bod.description == null ? "" : bod.description.replace(/\n|\r\n|\r/g, '<br/>');
                    price = bod.subscriptionPrice == null ? "N/A" : bod.subscriptionPrice;
                    validityPeriod = (isNullOrEmpty(bod.validityPeriod)? bod.validity : bod.validityPeriod) + " " + bod.validityPeriodUnit;
                    type = 'BoD';
                    break;
                }
            }
        }else{
            for (var i = 0; i < topUps.length; i++) {
                var topup = topUps[i];
                if (addOnId == topup['id']) {
                    addOnId = topup.id;
                    description = topup.description == null ? "" : topup.description.replace(/\n|\r\n|\r/g, '<br/>');
                    price = topup.price == null ? "N/A" : topup.price;
                    validityPeriod = (isNullOrEmpty(topup.validityPeriod) ? topup.validity : topup.validityPeriod) + " " + topup.validityPeriodUnit;
                    type = isNullOrEmpty(topup.topUpType)? topup.packageType:topup.topUpType;
                    break;
                }
            }
        }
        addAddOnDetails(addOnId,description,price,validityPeriod,type,currency);
    }

    $(document).ready(function(){
        fillAddOnList();
        $("#selectedAddOn").select2();
        showDetails();
        var unsubscribeFailureCount = <%=(Integer)request.getSession().getAttribute(Attributes.UNSUBSCRIBE_FAILURE_COUNT)%>;
        var path = window.location.href;
        if(path.indexOf('unsubscribeAddOn') > 0){
            $("#tab1").removeClass("active");
            $("#tab2").addClass("active");
            $("#section1").removeClass("active in");
            $("#section2").addClass("active in");
            if(isNullOrEmpty(unsubscribeFailureCount) == false && unsubscribeFailureCount >= 1){
                <%
                String addonSubscriptionId = (String)request.getAttribute("addonSubscriptionId");
                String addonId = (String)request.getAttribute("addonId");
                %>

                var addonSubscriptionId = '<%=addonSubscriptionId%>';
                var addonId = '<%=addonId%>';
                $("#addonId").val(addonId);
                $("#addonSubscriptionId").val(addonSubscriptionId);
                $("#deleteSubscription").modal('show');
            }
        }
        /*
        added an enforceFocus on dropDowns, this is because modal enforces focus on itself.
        */
        $.fn.modal.Constructor.prototype.enforceFocus = function() {};
    });


    function fillAddOnList() {
        var subscriberMode = '${subscriber.subscriberMode}';
        if(subscriberMode=='TEST'){
            if(isNullOrEmpty(allAddOnList) && isNullOrEmpty(allTopUpsString) && isNullOrEmpty(allBodDataList)){
                return false;
            }
            if(isNullOrEmpty(allAddOnList) == false) {
                setAddOns(allAddOnList);
            }
            if(isNullOrEmpty(allTopUpsString) == false) {
                setTopUps(allTopUpsString);
            }
            if(isNullOrEmpty(allBodDataList) == false){
                setBods(allBodDataList);
            }
        }
        if(subscriberMode=='LIVE'){
            if(isNullOrEmpty(liveAddOnList) && isNullOrEmpty(liveTopUpString) && isNullOrEmpty(liveBodDataList)){
                return false;
            }
            if(isNullOrEmpty(liveAddOnList) == false) {
                setAddOns(liveAddOnList);
            }
            if(isNullOrEmpty(liveTopUpString) == false) {
                setTopUps(liveTopUpString);
            }
            if(isNullOrEmpty(liveBodDataList) == false){
                setBods(liveBodDataList);
            }
        }

    }

    function clearSelectTag() {
        var elSel = document.getElementById('selectedAddOn');
        for (var i = elSel.length - 1; i>=0; i--) {
            elSel.remove(i);
        }
    }

    function setAddOns(addOnList){
        for(var i=0; i<addOnList.length; i++){
            var addOn = addOnList[i];
            $('#selectedAddOn').find('optgroup[label="Add On"]').append(new Option(addOn['name'],addOn['id']+'|ADDON'));
        }
    }


function setTopUps(topUps){
    for(var i=0; i<topUps.length; i++){
        var topup = topUps[i];
        $('#selectedAddOn').find('optgroup[label="Top-Up"]').append(new Option(topup['name'],topup['id']+'|'+topup['topUpType']));
	}
}

    function setBods(boDs){
        for(var i=0; i<boDs.length; i++){
            var bod = boDs[i];
            $('#selectedAddOn').find('optgroup[label="BOD"]').append(new Option(bod['name'],bod['id']+'|BOD'));
        }
    }



    var jsonData ;
    var flag = false; //This flag describe wether reload has been peformed or not
    function reload(){
        $.ajax({
            type 		: "POST",
            data		: {dataPackage:'${session.subscriber.productOffer}',syInterface:'${session.subscriber.syInterface}'},
            url 		: "${pageContext.request.contextPath}/ajax/pkgReload/reloadProductOfferAddOns",
            beforeSend: function(){
                $('#reloadAddOn').attr('style','display:none');
                $('#progressAddOn').show();
            },
            success 	: function(data){
                jsonData = data;
                flag = true;
                reloadPackages(data);
                $('#progressAddOn').hide();
                $('#reloadAddOn').attr('style','padding-top:32px;padding-left:0px');
            },
            error 		: function(xhr,status, errmsg) {
                console.log(status);
            }
        });
    }
    function reloadPackages(data){
        var subscriberMode = '${subscriber.subscriberMode}';
        clearSelectTag();
        if (subscriberMode == 'TEST') {
            setAddOns(data.productOfferAddOns);
            setTopUps(data.allTopUps);
            setBods(data.allBodData);
        }
        if (subscriberMode == 'LIVE') {
            setAddOns(data.liveProductOffersAddons);
            setTopUps(data.liveTopUps);
            setBods(data.liveBodData);
        }
        showDetails();
    }

 function clearDialog(){
	//fillAddOnList();
	showDetails();
	$("#subscriptionStartDate").val("");
 }


</script>