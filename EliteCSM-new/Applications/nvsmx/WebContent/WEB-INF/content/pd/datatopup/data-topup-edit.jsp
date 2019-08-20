<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="data.topup.update"/></h3>
    </div>

    <div class="panel-body">
        <s:form namespace="/pd/datatopup" action="data-topup" id="datatopupform" method="post"
                cssClass="form-horizontal" validate="true"
                labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
        <s:hidden name="_method" value="put"/>
        <s:token/>

            <s:if test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name() ||
                          packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@TEST.name() }">
                  <%@include file="data-topup-edit-design-mode.jsp" %>
            </s:if>
            <s:elseif test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE.name()}">
                <%@include file="data-topup-edit-live-mode.jsp" %>
            </s:elseif>
            <s:elseif test="%{packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
                <%@include file="data-topup-edit-live2-mode.jsp" %>
            </s:elseif>
            <div class="row">
                <div class="col-xs-12" align="center">
                    <div class="col-xs-12" align="center">
                        <button class="btn btn-sm btn-primary" type="submit" role="button" formaction="${pageContext.request.contextPath}/pd/datatopup/data-topup/${id}" tabindex="22"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                        <button id="btnCancel" type="button" class="btn btn-primary btn-sm" value="Cancel" tabindex="23" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/datatopup/data-topup/${id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.back"/> </button>
                    </div>
                </div>
            </div>
    </div>

    </s:form>
</div>
</div>
<script type="text/javascript">

    $(function () {
        $(".select2").select2();
        setVolumeType();
    });

    function validateForm() {
        return verifyUniquenessOnSubmit('name', 'update', '${id}', 'com.elitecore.corenetvertex.pd.topup.DataTopUpData', '', '')
                && validateQuotaVolume()
                && validateValidityPeriod();
    }
</script>
<%@include file="data-topup-validation.jsp"%>