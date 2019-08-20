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
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="diameter.gateway.profile.service.guiding.manage.order" /></h3>
	</div>
	<div class="panel-body">
					<s:form namespace="" action="" id="diameterGwProfileServiceGuidingManageOrderForm" method="post" cssClass="form-horizontal" labelCssClass="col-xs-4 col-lg-2 text-right" elementCssClass="col-xs-8 col-lg-10"  >
						<table id="diameterGwProfileServiceGuidingManageOrderTable" style="width: 100%;" class="table table-blue dataTable no-footer" role="grid" width="100%">
							<thead>
								<tr role="row">
									<th style="width: 10px"> Order# </th>
									<th> <s:text name="diameter.profile.service.guiding.name"/> </th>
									<th> <s:text name="diameter.profile.service.guiding.condition"/> </th>
								</tr>
							</thead>
							<tbody>
							<s:iterator value="serviceGuidings" var="serviceGuidingData">
								<tr>
									<td class="dataTables_empty" > <s:property value="#serviceGuidingData.orderNumber"/> </td>
									<td class="dataTables_empty" >
										<s:property value="#serviceGuidingData.serviceData.name"/>
										<input type="hidden" name="serviceGuidingIds" value="${serviceGuidingData.id}"/>
									</td>
									<td class="dataTables_empty" > <s:property value="#serviceGuidingData.condition"/> </td>
								</tr>
							</s:iterator>
						</table>
					</s:form>
					<div id="groovyScriptRowOrder"></div>
				<div class="row">
					<div class="col-xs-12" align="center">
						<button id="btnSave" class="btn btn-primary btn-sm"  value="Save" onclick="submitForm();" ><span class="glyphicon glyphicon-floppy-disk" title="save"></span> <s:text name="button.save"></s:text> </button>
						<button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14"
								onclick="javascript:location.href='${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}'">
							<span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="Gateway Profile"/>
						</button>
					</div>
				</div>
	</div>
</div>

<!-- Modal HTML -->
<div id="alertFailure" class="modal fade" >
	<div class="modal-dialog">
		<div class="modal-content" style="width: 300px" >
			<div class="modal-body modal-sm">
				<span><s:text name="diameter.gateway.profile.groovy.script.manage.order.no.change"/></span>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-sm btn-danger" data-dismiss="modal" ><s:text name="button.ok"/></button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/sort/jquery.rowsorter.js"></script>

<script type="text/javascript" >

    $(document).ready(function(){

        var hasOrderChanged = false;

        function enableDnD(){
            $("#diameterGwProfileServiceGuidingManageOrderTable").rowSorter({
                onDrop: function(tbody, row, index, oldIndex) {
                    $('#diameterGwProfileServiceGuidingManageOrderTable tr').each(function() {
                        hasOrderChanged = true;
                    });
                    var headerKey = $(row).children(":eq(1)").text();
                    $("#groovyScriptRowOrder").html(headerKey + "Service Guiding order changed to " + (index + 1)+" from "+(oldIndex + 1));
                }
            });
        };

        enableDnD();

        submitForm = function (){
            if(hasOrderChanged){
            	$("#diameterGwProfileServiceGuidingManageOrderForm").attr("action","${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/${id}/manageOrderForServiceGuiding")
            	$("#diameterGwProfileServiceGuidingManageOrderForm").submit();
			}else{
                $("#alertFailure").modal('show');
            }
        };
    });
</script>
