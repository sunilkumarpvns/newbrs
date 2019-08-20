<%@ taglib uri="/struts-tags/ec" prefix="s"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="dataservicetype.update" /></h3>
	</div>
	<div class="panel-body">
		<s:form namespace="/" action="policydesigner/dataservicetype/DataServiceType/update" method="post" cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9" validator="validateForm()">
			<s:token/>
			<div class="row">
				<div class="col-sm-9 col-lg-7">
					<s:hidden name="dataServiceTypeData.id" />
					<s:textfield 	name="dataServiceTypeData.name" 		key="dataservicetype.name" 	id="serviceTypeName"		cssClass="form-control focusElement"
						onblur="verifyUniqueness('serviceTypeName','update','%{dataServiceTypeData.id}','com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData','','');" tabindex="1" />
					<s:textarea name="dataServiceTypeData.description" 	key="dataservicetype.description" cssClass="form-control" rows="2" tabindex="2" />
					<s:textfield id="serviceIdentifier" name="dataServiceTypeData.serviceIdentifier" key="dataservicetype.serviceidentifier" cssClass="form-control" tabindex="3" type="number" onkeypress="return isNaturalInteger(event);"
						onblur="verifyUniqueness('serviceIdentifier','update','%{dataServiceTypeData.id}','com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData','','serviceIdentifier');"
					/>
					<s:select list="allRatingGroups" cssClass="form-control select2" name="ratingGroupIds" key="dataservicetype.ratinggroup" listValue="nameAndIdentifier" listKey="id" multiple="true" tabindex="4" cssStyle="width:100%"/>
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
									<tbody>
									<s:hidden id="defServiceDataFlowListSize" value="%{dataServiceTypeData.defaultServiceDataFlows.size}" />
									<s:iterator  value="dataServiceTypeData.defaultServiceDataFlows" var="defServiceDataFlow" status="stat" >
										<tr>
											<td style="width:18%"><s:select tabindex="6" name="dataServiceTypeData.defaultServiceDataFlows[%{#stat.index}].flowAccess" value="%{#defServiceDataFlow.flowAccess}" cssClass="form-control" list="#{'permit in':'PERMIT IN','permit out':'PERMIT OUT','deny in':'DENY IN','deny out':'DENY OUT'}" elementCssClass="col-xs-12"></s:select></td>
											<td style="width:12%"><s:select tabindex="7" name="dataServiceTypeData.defaultServiceDataFlows[%{#stat.index}].protocol" value="%{#defServiceDataFlow.protocol}" cssClass="form-control" list="#{'ip':'IP','6':'TCP','17':'UDP'}" elementCssClass="col-xs-12"></s:select></td>
											<td><s:textfield tabindex="8" cssClass="form-control" name="dataServiceTypeData.defaultServiceDataFlows[%{#stat.index}].sourceIP"  type="text" value="%{#defServiceDataFlow.sourceIP}" elementCssClass="col-xs-12"/></td>
											<td><s:textfield tabindex="9" cssClass="form-control" name="dataServiceTypeData.defaultServiceDataFlows[%{#stat.index}].sourcePort"  type="text" value="%{#defServiceDataFlow.sourcePort}" elementCssClass="col-xs-12"/></td>
											<td><s:textfield tabindex="10" cssClass="form-control" name="dataServiceTypeData.defaultServiceDataFlows[%{#stat.index}].destinationIP"  type="text" value="%{#defServiceDataFlow.destinationIP}" elementCssClass="col-xs-12"/></td>
											<td><s:textfield tabindex="11" cssClass="form-control" name="dataServiceTypeData.defaultServiceDataFlows[%{#stat.index}].destinationPort"  type="text" value="%{#defServiceDataFlow.destinationPort}" elementCssClass="col-xs-12" /></td>
											<td><span tabindex="12" class="btn defaultBtn" onclick="$(this).parent().parent().remove();"><a><span class="delete glyphicon glyphicon-trash" title="delete" ></span></a></span></td>
											
										</tr>
									</s:iterator>
									
									
									</tbody>
								</table>
							</div>
					</div>
			</div>
			
			<div class="row">
				<div class="col-xs-12" align="center">					
					<s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="13"><span class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
					<button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14" style="margin-right:10px;" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/dataservicetype/DataServiceType/view?serviceTypeId=${dataServiceTypeData.id}'"><span class="glyphicon glyphicon-backward" title="Back"></span> DataServiceType</button>
				</div>
			</div>
		</s:form>
	</div>
</div>
<script type="text/javascript">
$(function(){
	$(".select2").select2();
	if('<s:text name="dataServiceTypeData.id"/>' == 'DATA_SERVICE_TYPE_1'){
		$('#serviceTypeName').attr('readonly', 'true');
	}
});
var i=$("#defServiceDataFlowListSize").val();

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
	return verifyUniquenessOnSubmit('serviceTypeName','update','<s:text name="dataServiceTypeData.id"/>','com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData','','') && verifyUniquenessOnSubmit('serviceIdentifier','update','<s:text name="dataServiceTypeData.id"/>','com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData','','serviceIdentifier');
}
</script>