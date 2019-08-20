<%@ taglib uri="/struts-tags/ec" prefix="s"%>
<%@ taglib uri="/struts-jquery-tags" prefix="s"%>

<link rel="stylesheet"
	  href="${pageContext.request.contextPath}/select2/css/select2.css" />
<script
		src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<s:text name="product.spec.create" />
		</h3>
	</div>

	<div class="panel-body">
		<s:form namespace="/pd/rncproductspec" action="rnc-product-spec"
				id="productspecform" method="post" cssClass="form-horizontal"
				validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3"
				elementCssClass="col-xs-12 col-sm-8 col-lg-9"
				onsubmit="return validateForm()">
		<s:hidden name="_method" value="put" />
		<s:token />
		<div class="row">
			<div class="col-xs-6">
				<s:textfield name="name" key="product.spec.name" id="name"
							 cssClass="form-control focusElement" tabindex="1" />

				<s:textarea name="description" key="product.spec.description"
							cssClass="form-control" id="description" tabindex="2" />

				<s:select name="groups" value="groupValuesForUpdate"
						  key="product.spec.groups" cssClass="form-control select2"
						  list="#session.staffBelongingGroups" id="groupNames"
						  multiple="true" listKey="id" listValue="name"
						  cssStyle="width:100%" tabindex="3" />

				<s:select name="status" key="product.spec.status"
						  cssClass="form-control"
						  list="@com.elitecore.corenetvertex.constants.PkgStatus@values()"
						  id="productSpecStatus" tabindex="4" />
				<s:hidden name="packageMode"
						  value="%{@com.elitecore.corenetvertex.pkg.PkgMode@DESIGN.name()}" />

			</div>
			<div class="col-xs-6">

				<s:datepicker name="availabilityStartDate"
							   key="product.spec.availability.start.date" parentTheme="bootstrap"
							   changeMonth="true" changeYear="true" cssClass="form-control"
							   showAnim="slideDown" duration="fast" showOn="focus"
							   placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy"
							   timepicker="true" timepickerFormat="HH:mm:ss"
							   id="availabilityStartDate" readonly="true" tabindex="5" />

				<s:datepicker name="availabilityEndDate"
							   key="product.spec.availability.end.date" parentTheme="bootstrap"
							   changeMonth="true" changeYear="true" cssClass="form-control"
							   showAnim="slideDown" duration="fast" showOn="focus"
							   placeholder="DD-MON-YYYY HH:MM:SS" displayFormat="dd-M-yy"
							   timepicker="true" timepickerFormat="HH:mm:ss"
							   id="availabilityEndDate" readonly="true" tabindex="6" />

				<s:textfield name="validityPeriod"
							 key="product.spec.validity.period" cssClass="form-control"
							 type="number" id="validityPeriod"
							 onkeypress="return isNaturalInteger(event);" value="30"
							 tabindex="7" />

				<s:select name="subscriptionCurrency" key="product.spec.currency"
						  cssClass="form-control"
						  list="@java.util.Currency@getAvailableCurrencies()"
						  listKey="getCurrencyCode()"
						  listValue="getCurrencyCode()+' ('+getDisplayName()+')'"
						  id="ratecardCurrency"
						  value="@java.text.NumberFormat@getCurrencyInstance().getCurrency()"
						  tabindex="8" />

			</div>
		</div>
		<div class="row">
			<div id="productspecsrvpkgreldiv">
				<div class="col-sm-12">
					<table id='productspecsrvpkgrelTable'
						   class="table table-blue table-bordered">
						<caption class="caption-header">
							<s:text name="product.spec.service.package.relation" />
							<div align="right" class="display-btn">
									<span class="btn btn-group btn-group-xs defaultBtn"
										  onclick="addServicePkgRelations();" tabindex="8" id="addRow">
										<span class="glyphicon glyphicon-plus"></span>
									</span>
							</div>
						</caption>
						<thead>
						<th><s:text name="product.spec.service.type" /></th>
						<th><s:text name="product.spec.pkg" /></th>
						<th></th>
						</thead>
						<tbody>
						<s:iterator value="productOfferServicePkgRelDataList"
									var="productSpecServicePkgRel" status="stat">
							<tr>
								<td><s:select
										value="%{#productSpecServicePkgRel.serviceData.id}"
										list="serviceDataList"
										name="productOfferServicePkgRelDataList[%{#stat.index}].serviceId"
										listValue="name" cssClass="form-control" listKey="id"
										elementCssClass="col-xs-12" /></td>

								<td><s:select
										value="%{#productSpecServicePkgRel.rncPackageData.id}"
										list="rncPkgDataList"
										name="productOfferServicePkgRelDataList[%{#stat.index}].rncPackageId"
										listValue="name" cssClass="form-control group" listKey="id"
										elementCssClass="col-xs-12" /></td>
								<td style='width: 35px;'><span class='btn defaultBtn'
															   onclick='$(this).parent().parent().remove();'><a><span
										class='delete glyphicon glyphicon-trash' title='delete'></span></a></span>
								</td>
							</tr>
						</s:iterator>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-12" align="center">
				<div class="col-xs-12" align="center">
					<button class="btn btn-sm btn-primary" type="submit" role="button"
							formaction="${pageContext.request.contextPath}/pd/rncproductspec/rnc-product-spec/${id}"
							tabindex="9">
						<span class="glyphicon glyphicon-floppy-disk"></span>
						<s:text name="button.save" />
					</button>
					<button id="btnCancel" class="btn btn-primary btn-sm"
							value="Cancel" tabindex="10"
							onclick="javascript:location.href='${pageContext.request.contextPath}/pd/rncproductspec/rnc-product-spec/${id}'">
						<span class="glyphicon glyphicon-backward" title="Back"></span>
						<s:text name="button.back" />
					</button>
				</div>
			</div>
		</div>
	</div>


	</s:form>
</div>
</div>
<script type="text/javascript">
    $(function() {
        $(".select2").select2();
    });

    function validateForm() {
        return verifyUniquenessOnSubmit(
            'name',
            'create',
            '<s:text name="id"/>',
            'com.elitecore.corenetvertex.pd.productoffer.ProductOfferData',
            '', '')

    }
</script>
<%@include file="rnc-product-spec-utility.jsp"%>