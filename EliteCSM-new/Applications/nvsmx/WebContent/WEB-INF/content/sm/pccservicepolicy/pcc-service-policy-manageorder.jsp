<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<style type="text/css">
	.form-group {
		width: 100%;
		display: table;
		margin-bottom: 2px;
	}
	table.sorting-table {cursor: move;}
</style>
<div class="panel panel-primary">
	<div class="panel-heading" style="padding: 8px 15px">
		<h3 class="panel-title">
			<s:text name="pccServicePolicy.manage.order"/>
		</h3>
	</div>
	<div class="panel-body">
		<div class="row" >
			<fieldset class="fieldSet-line">
				<div class="row" style="padding: 8px 15px">
					<s:form namespace=""
							action=""
							id="pccServicePolicyUpdateOrderForm"
							method="post"
							cssClass="form-horizontal"
							labelCssClass="col-xs-4 col-lg-2 text-right" elementCssClass="col-xs-8 col-lg-10"  >
						<table id="pccServicePoliciesOrderTable" style="width: 100%;" class="table table-blue dataTable no-footer" role="grid" width="100%">

							<thead>
								<tr role="row">
									<th style="width: 10px"> Order </th>
									<th> <s:text name="pccServicePolicy.name"/> </th>
									<th> <s:text name="pccServicePolicy.description"/> </th>
								</tr>
							</thead>
							<tbody>
							<s:iterator value="list" var="pccServicePolicy">
								<tr>
									<td class="dataTables_empty" > <s:property value="#pccServicePolicy.orderNumber"/> </td>
									<td class="dataTables_empty" >
										<s:property value="#pccServicePolicy.name"/>
										<input type="hidden" name="pccServicePoliciesIds" value="${pccServicePolicy.id}"/>
									</td>
									<td class="dataTables_empty" > <s:property value="#pccServicePolicy.description"/> </td>
								</tr>
							</s:iterator>
						</table>
					</s:form>
					<div id="qosRowOrder"></div>
				</div>
				<div class="row">
					<div class="col-xs-12" align="center">
						<button id="btnSave" class="btn btn-primary btn-sm"  value="Save" onclick="submitForm();" ><span class="glyphicon glyphicon-floppy-disk" title="save"></span> <s:text name="button.save"></s:text> </button>
						<button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14"
								onclick="javascript:location.href='${pageContext.request.contextPath}/sm/pccservicepolicy/pcc-service-policy'">
							<span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
						</button>
					</div>
				</div>
			</fieldset>
		</div>
	</div>
</div>

<!-- Modal HTML -->
<div id="alertFailure" class="modal fade" >
	<div class="modal-dialog">
		<div class="modal-content" style="width: 300px" >
			<div class="modal-body modal-sm">
				<span><s:text name="pccServicePolicy.nochangefound"/></span>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-sm btn-danger" data-dismiss="modal" >OK</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/sort/jquery.rowsorter.js"></script>

<script type="text/javascript" >

    $(document).ready(function(){

        var hasOrderChanged = false;

        function enableDnD(){
            $("#pccServicePoliciesOrderTable").rowSorter({
                onDrop: function(tbody, row, index, oldIndex) {
                    $('#pccServicePoliciesOrderTable tr').each(function() {
                        hasOrderChanged = true;
                    });
                    var pccServicePolicyName = $(row).children(":eq(1)").text();
                    $("#qosRowOrder").html(pccServicePolicyName + " order changed to " + (index + 1)+" from "+(oldIndex + 1));
                }
            });
        };

        enableDnD();

        submitForm = function (){
            if(hasOrderChanged){
            	$("#pccServicePolicyUpdateOrderForm").attr("action","${pageContext.request.contextPath}/sm/pccservicepolicy/pcc-service-policy/1/manageOrder")
            	$( "#pccServicePolicyUpdateOrderForm" ).submit();
			}else{
                $("#alertFailure").modal('show');
            }
        };
    });
</script>
