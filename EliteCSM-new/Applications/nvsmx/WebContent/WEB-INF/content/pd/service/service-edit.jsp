<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="service.update" /></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/service" action="service" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4" elementCssClass="col-xs-12 col-sm-8" validator="validateForm()">
            <s:hidden name="_method" value="put" />
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-8">
                    <s:textfield name="name" key="service.name" id="serviceName" cssClass="form-control focusElement"  maxlength="50" tabindex="1"/>
                    <s:textfield name="serviceId" key="service.alias" id="serviceId" cssClass="form-control" tabindex="2" readonly="true"/>
                    <s:textarea name="description" key="service.description" id="description" cssClass="form-control" maxlength="2000" tabindex="3"/>
  				    <s:select name="status" key="service.status" cssClass="form-control" list="@com.elitecore.corenetvertex.constants.ResourceStatus@values()" id="resourceStatus" maxlength="50" tabindex="4" />
  				</div>
                               
                <div class="row">
					<div class="col-xs-12" align="center">
						<button type="submit" id="btnSubmit" class="btn btn-primary btn-sm" role="submit" id="btnSubmit" formaction="${pageContext.request.contextPath}/pd/service/service/${id}" tabindex="5">
							<span class="glyphicon glyphicon-floppy-disk"></span>
							<s:text name="button.save" />
						</button>
						<button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/service/service/${id}'" tabindex="6">
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
	
   return verifyUniquenessOnSubmit('serviceName','update','<s:property value="id"/>','com.elitecore.corenetvertex.pd.service.ServiceData','','');  
   
}

$(function(){
    $( "#serviceGroups" ).select2();
});
</script>
