<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"> <s:property value="getText('ims.pkg.update')" /> </h3>
	</div>

	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/ims/IMSPkg/update" id="imsPkgUpdateForm" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
			<s:token />
			<div class="row">
				<div class="col-xs-6">
					<s:hidden id="entityOldGroups" name="entityOldGroups"  value="%{imsPkgData.groups}"/>
					<s:hidden 		name="imsPkgData.id" 	/>
					<s:textfield 	id="imsPkgName"			name="imsPkgData.name" 			key="ims.pkg.name" 		cssClass="form-control focusElement" onblur="verifyUniqueness('imsPkgName','update','%{imsPkgData.id}','com.elitecore.corenetvertex.pkg.ims.IMSPkgData','','');"  />					
					<s:textarea  	id="imsPkgDescription"	name="imsPkgData.description" 	key="ims.pkg.description" 	cssClass="form-control" />
				</div>
				<div class="col-xs-6">
					<s:hidden id="imsPkgMode"  name="imsPkgData.packageMode"/>

					<div class="form-group "><label class="col-xs-12 col-sm-4 col-lg-3 control-label" id="lbl_pkgGroup">Groups </label>

						<div class="col-xs-12 col-sm-8 col-lg-9 controls">
							<s:if test="%{imsPkgData.packageMode == @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
								<s:hidden name="groupIds" value="%{imsPkgData.groups}"/>
								<select disabled="disabled" key="pkg.groups" class="form-control select2" style="width:100%" multiple="true">
									<s:iterator value="groupInfoList">
										<option locked="<s:property value="locked"/>" <s:property value="selected"/>
												value="<s:property value="id"/>" id="<s:property value="id"/>">
											<s:property value="name"/></option>
									</s:iterator>
								</select>
							</s:if>
							<s:else>
								<select name="groupIds" key="pkg.groups" class="form-control select2" style="width:100%" multiple="true">
									<s:iterator value="groupInfoList">
										<option locked="<s:property value="locked"/>" <s:property value="selected"/>
												value="<s:property value="id"/>" id="<s:property value="id"/>">
											<s:property value="name"/></option>
									</s:iterator>
								</select>
							</s:else>
						</div>
					</div>


					<s:select 		id="imsPkgStatus"		name="imsPkgData.status" 		key="ims.pkg.status" 	cssClass="form-control"			list="@com.elitecore.corenetvertex.constants.PkgStatus@values()" 	 />
                    <s:set var="priceTag">
                        <s:property value="getText('ims.pkg.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
                    </s:set>
					<s:textfield  	id="imsPkgPrice"			name="imsPkgData.price" 				key="priceTag" type="number" 	cssClass="form-control"  />
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

<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>
<script src="${pageContext.request.contextPath}/select2/js/nvselect2groups.js"></script>

<script type="text/javascript">
$(function(){
	$(".select2").select2();
	checkForPackageMode();
});
function validateForm(){
	return verifyUniquenessOnSubmit('imsPkgName','update','<s:text name="imsPkgData.id"/>','com.elitecore.corenetvertex.pkg.ims.IMSPkgData','','')
}


function checkForPackageMode(){
	var pkgMode =$("#imsPkgMode").val();
	if(pkgMode=="LIVE2"){
		$("#imsPkgName").attr("disabled","true");
		$("#imsPkgDescription").attr("disabled","true");
		$("#groupNames").attr("disabled","true");
		$("#pkgType").attr("disabled","true");
		$("#mulitpleSubscription").attr("disabled","true");
		$("#validityPeriod").attr("disabled","true");
		$("#validityPeriodUnit").attr("disabled","true");
		$("#imsPkgPrice").attr("disabled","true");
		$("#imsPkgUpdateForm").attr("action","updateStatus");
	}
}
</script>
