<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"> <s:property value="getText('ims.pkg.create')" /> </h3>
	</div>

	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/ims/IMSPkg/create" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
			<s:token />
			<div class="row">
				<div class="col-xs-6">
					<s:textfield 	name="imsPkgData.name" 		key="ims.pkg.name" 	id="imsPkgName"		cssClass="form-control focusElement" 
					    onblur="verifyUniqueness('imsPkgName','create','','com.elitecore.corenetvertex.pkg.ims.IMSPkgData','','');"  />					
					<s:textarea 	name="imsPkgData.description" 	key="ims.pkg.description" 	cssClass="form-control" />
					
										
				</div>
				<div class="col-xs-6">
					<s:select 		name="groupIds" 	    key="ims.pkg.groups"    	cssClass="form-control select2"	list="#session.staffBelongingGroups" id="groupNames" multiple="true" listKey="id" listValue="name"  cssStyle="width:100%"/>
					<s:select 		name="imsPkgData.status" 		key="ims.pkg.status" 		cssClass="form-control"			list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" 	id="imsPkgStatus" />
					<s:hidden name="imsPkgData.packageMode" value="%{@com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name()}"/>
                    <s:set var="priceTag">
                        <s:property value="getText('ims.pkg.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
                    </s:set>
					<s:textfield 	name="imsPkgData.price" 					key="priceTag" type="number" 	cssClass="form-control"  />
				</div>
				
			</div>

			<div class="row">
				<div class="col-xs-12" align="center">					
						<s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>		
				</div>
			</div>

		</s:form>
	</div>
</div>
<script type="text/javascript">

$(function(){
	$(".select2").select2();
});
function validateForm(){
	var isValidName = verifyUniquenessOnSubmit('imsPkgName','create','','com.elitecore.corenetvertex.pkg.ims.IMSPkgData','','')
	var isRetiredStatus = true;
    var status =$("#imsPkgStatus").val();
    if(status === '<s:property value="@com.elitecore.corenetvertex.constants.PkgStatus@RETIRED.name()"/>'){
    	setError('imsPkgStatus',"<s:text name='error.status.retired'/>");
    	isRetiredStatus = false;
    }
	return  (isValidName && isRetiredStatus);
}

</script>
