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
        background-image: none;
        background-color: #eee;
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
        <h3 class="panel-title"><s:text name="product.offer.update"/></h3>
    </div>

    <div class="panel-body">
        <s:form namespace="/pd/productoffer" action="product-offer" id="productspecform" method="post" cssClass="form-horizontal" validate="true"
                labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9"
                onsubmit="return validateForm()">
        <s:hidden name="_method" value="put" />
        <s:token/>
        <s:if test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name() ||
                      packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@TEST.name() }">
            <%@include file="product-offer-edit-design.jsp" %>
        </s:if>
        <s:elseif test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
            <%@include file="product-offer-edit-live.jsp" %>
        </s:elseif>
        <s:elseif test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
            <%@include file="product-offer-edit-live2.jsp" %>
        </s:elseif>
        <div class="row">
            <div class="col-xs-12" align="center">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-sm btn-primary" type="submit" role="button" formaction="${pageContext.request.contextPath}/pd/productoffer/product-offer/${id}" tabindex="14"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="15" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/productoffer/product-offer/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back"/> </button>
                </div>
            </div>
        </div>
    </div>


    </s:form>
</div>
</div>
<script type="text/javascript">

    var pkgMode = '<s:property value="%{packageMode}" />';

    $(function () {
        $(".select2").select2();
        checkForType();
        getPackagesBasedOnType();
    });

    function checkForType() {
        var pkgtype = $("#productOfferType").val();

        if (pkgtype == '<s:property value="@com.elitecore.corenetvertex.pkg.PkgType@BASE.name()"/>') {
            $("#validityPeriod").attr('readonly', 'true');
            $("#validityPeriodUnit").attr('disabled', 'true');
            $("#emailTemplateId").attr('disabled',true);
            $("#smsTemplateId").attr('disabled',true);
            $("#fnfEnabledId").attr('disabled',true);
            if(pkgMode == '<s:property value="%{@com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}" />'){
                $("#creditBalance").attr('readonly', 'true');
            }
        }
    }
    function validateForm() {
        var result = verifyUniquenessOnSubmit('name', 'update', '<s:text name="id"/>', 'com.elitecore.corenetvertex.pd.productoffer.ProductOfferData', '', '');

        if(result == false){
            return false;
        }

        if(validateCreditBalance() == false){
            return false;
        }

        if(typeof validateDesignForm !== "undefined" &&
            typeof validateDesignForm === "function"){
            return validateDesignForm();
        }

        return true;
    }

</script>
<script src="${pageContext.request.contextPath}/js/packageutility.js"></script>
<%@include file="product-offer-utility.jsp"%>