<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="lob.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/lob" action="lob" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div>
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="name" key="lob.name" id="lobName" cssClass="form-control focusElement" maxlength="50" tabindex="1"/>
                    <s:textfield name="alias" key="lob.alias" id="lobAlias" cssClass="form-control" tabindex="2" />
                    <s:textarea name="description" key="lob.description" id="lobDescription" cssClass="form-control" rows="2" tabindex="3"/>
                    <s:select name="status" key="lob.status" id="lobStatus" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.ResourceStatus@values()" tabindex="4" />
                </div>
    
                <div class="row">
                    <div class="col-xs-12" align="center">
                        <button type="submit" class="btn btn-primary btn-sm"  role="submit" formaction="${pageContext.request.contextPath}/pd/lob/lob/${id}" tabindex="5" ><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                        <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/lob/lob/${id}'" tabindex="6" ><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.back"/></button>
                    </div>
                </div>
            </div>
        </s:form>
    </div>
</div>

<script type="text/javascript">

function validateForm() {
	
	return verifyUniquenessOnSubmit('lobName','update','<s:property value="id"/>','com.elitecore.corenetvertex.pd.lob.LobData','','');
}
</script>