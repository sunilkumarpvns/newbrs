<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="lob.create" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/lob" action="lob" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="name" key="lob.name" id="lobName" cssClass="form-control focusElement" maxlength="50" tabindex="1"/>
                    <s:textfield name="alias" key="lob.alias" id="lobAlias" cssClass="form-control" tabindex="2"/>
                    <s:textarea name="description" key="lob.description" id="lobDescription" cssClass="form-control" rows="2" tabindex="3"  value="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDefaultDescription(request)}" />
                    <s:select name="status" key="lob.status" id="lobStatus" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.ResourceStatus@values()" tabindex="4" />
                </div>
                
                <div class="row">
                    <div class="col-xs-12" align="center" >
                        <s:submit cssClass="btn  btn-sm btn-primary"  type="button" role="button" tabindex="5"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                        <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/lob/lob'" tabindex="6"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                    </div>
                </div>
            </div>
        </s:form>
   </div
</div>

<script type="text/javascript">

function validateForm() {
	
	return verifyUniquenessOnSubmit('lobName','create','','com.elitecore.corenetvertex.pd.lob.LobData','','');
}
</script>