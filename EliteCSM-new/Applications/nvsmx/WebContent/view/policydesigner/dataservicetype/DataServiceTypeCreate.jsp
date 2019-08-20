<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="dataservicetype.create" /></h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/dataservicetype/DataServiceType/create" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
			<s:token/>
			<div class="row">
				<div class="col-sm-9 col-lg-7">
					<s:textfield 	name="dataServiceTypeData.name"	key="dataservicetype.name" 	id="serviceTypeName" cssClass="form-control focusElement"
						onblur="verifyUniqueness('serviceTypeName','create','','com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData','','');" tabindex="1" />
					<s:textarea name="dataServiceTypeData.description" 	key="dataservicetype.description" cssClass="form-control" rows="2" tabindex="2" />
					<s:textfield id="serviceIdentifier" name="dataServiceTypeData.serviceIdentifier" key="dataservicetype.serviceidentifier" cssClass="form-control" tabindex="3" type="number" onkeypress="return isNaturalInteger(event);"
						onblur="verifyUniqueness('serviceIdentifier','create','','com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData','','serviceIdentifier');"
					/>
					<s:select list="allRatingGroups" id="ratingGroups" cssClass="form-control select2" name="ratingGroupIds" key="dataservicetype.ratinggroup" listValue="nameAndIdentifier" listKey="id" multiple="true" tabindex="4" cssStyle="width:100%"/>
				</div>
				<div id="serviceDataFlow">
							<div class="col-sm-12">
								<table id='defaultServiceDataFlowTable'  class="table table-blue table-bordered">
									<caption class="caption-header">
										<s:text name="dataservicetype.servicedataflow"/>
										<div align="right" class="display-btn" >
											<span class="btn btn-group btn-group-xs defaultBtn" onclick="addServiceDataFlow();" tabindex="5" 
												id="addRow"> <span class="glyphicon glyphicon-plus" ></span>
											</span>
										</div>
									</caption>
									<thead>
										<th><s:text name="dataservicetype.servicedataflow.flowaccess"/></th>
										<th><s:text name="dataservicetype.servicedataflow.protocol"/></th>
										<th><s:text name="dataservicetype.servicedataflow.sourceip"/></th>
										<th><s:text name="dataservicetype.servicedataflow.sourceport"/></th>
										<th><s:text name="dataservicetype.servicedataflow.destinationip"/></th>
										<th><s:text name="dataservicetype.servicedataflow.destinationport"/></th>
										<th></th>
									</thead>
								</table>
							</div>
							<div class="col-sm-6">
					</div>
			</div>
			
			<div class="row">
				<div class="col-xs-12" align="center">					
					<s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="13"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
				</div>
			</div>
		</s:form>
	</div>
</div>
<script type="text/javascript">
$(function(){
	$(".select2").select2();
});
var i=0;

function addServiceDataFlow(){
	var tableRow ='<tr>'+
	        '<td style="width:18%"><select tabindex="6" name="dataServiceTypeData.defaultServiceDataFlows['+i+'].flowAccess"  class="form-control form-control"><option value="permit in">PERMIT IN</option><option value="permit out">PERMIT OUT</option><option value="deny in">DENY IN</option><option value="deny out">DENY OUT</option></select></td>'+
			'<td style="width:12%"><select tabindex="7" name="dataServiceTypeData.defaultServiceDataFlows['+i+'].protocol"  class="form-control form-control"><option value="ip">IP</option><option value="6">TCP</option><option value="17">UDP</option></select></td>'+
			'<td><input tabindex="8" class="form-control" name="dataServiceTypeData.defaultServiceDataFlows['+i+'].sourceIP"  type="text"/></td>'+
			'<td><input tabindex="9" class="form-control" name="dataServiceTypeData.defaultServiceDataFlows['+i+'].sourcePort"  type="text"/></td>'+
			'<td><input tabindex="10" class="form-control" name="dataServiceTypeData.defaultServiceDataFlows['+i+'].destinationIP"  type="text"/></td>'+
			'<td><input tabindex="11" class="form-control" name="dataServiceTypeData.defaultServiceDataFlows['+i+'].destinationPort"  type="text"/></td>'+
			'<td><span tabindex="12" class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete"></span></a></span></td>'+
			'</tr>';
			$(tableRow).appendTo('#defaultServiceDataFlowTable');
	i++;
}

function validateForm(){
	return verifyUniquenessOnSubmit('serviceTypeName','create','','com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData','','') && verifyUniquenessOnSubmit('serviceIdentifier','create','','com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData','','serviceIdentifier');
}
</script>