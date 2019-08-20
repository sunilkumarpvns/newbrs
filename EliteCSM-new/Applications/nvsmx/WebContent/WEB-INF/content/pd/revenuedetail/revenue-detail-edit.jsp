<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="revenue.detail.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/revenuedetail" id = "revenuedetail" action="revenue-detail" method="post" cssClass="form-horizontal" validate="true"
                labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="name" key="revenue.detail.name" id="revenueDetailName" cssClass="form-control focusElement" maxlength="50" tabindex="1"/>
                    <s:textfield name="revenueDetailId" key="revenue.detail.id" id="revenueDetailId" cssClass="form-control" maxlength="50" tabindex="2" readonly="true"/>
                    <s:textarea name="description" key="revenue.detail.description" id="description" cssClass="form-control" maxlength="2000" tabindex="3"/>
                </div>

                <div class="row">
                    <div class="col-xs-12" align="center">
                        <button type="submit" id="btnSubmit" class="btn btn-primary btn-sm" role="submit" id="btnSubmit" formaction="${pageContext.request.contextPath}/pd/revenuedetail/revenue-detail/${id}" tabindex="4">
                            <span class="glyphicon glyphicon-floppy-disk"></span>
                            <s:text name="button.save" />
                        </button>
                        <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/revenuedetail/revenue-detail/${id}'" tabindex="5">
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
        return verifyUniquenessOnSubmit('revenueDetailName','update','<s:property value="id"/>','com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData','','');

    }

</script>
