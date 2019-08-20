<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="revenue.detail.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/revenuedetail" id="revenuedetail" action="revenue-detail" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="name" key="revenue.detail.name" id="revenueDetailName" cssClass="form-control focusElement" maxlength="100" tabindex="1"/>
                    <s:textfield name="revenueDetailId" key="revenue.detail.id" id="revenueDetailId" cssClass="form-control" maxlength="100" onkeyup="javascript:this.value=this.value.toUpperCase();" tabindex="2"/>
                    <s:textarea name="description" key="revenue.detail.description" id="description" cssClass="form-control" value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}" maxlength="2000" tabindex="3"/>
                </div>

                <div class="row">
                    <div class="col-xs-12" align="center">
                        <button type="submit" class="btn btn-primary btn-sm" role="submit" formaction="${pageContext.request.contextPath}/pd/revenuedetail/revenue-detail/${id}" tabindex="5">
                            <span class="glyphicon glyphicon-floppy-disk"></span>
                            <s:text name="button.save" />
                        </button>
                        <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/revenuedetail/revenue-detail/${id}'" tabindex="6">
                            <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;
                            <s:text name="button.back" />
                        </button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script type="text/javascript">

    function validateForm() {
        return (verifyUniquenessOnSubmit('revenueDetailName','create','','com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData','','')
            && verifyUniquenessOnSubmit('revenueDetailId','create','','com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData','','id'));

    }
</script>