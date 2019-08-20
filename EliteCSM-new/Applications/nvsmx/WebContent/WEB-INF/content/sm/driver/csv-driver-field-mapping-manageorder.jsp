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
		<h3 class="panel-title"><s:text name="csv.driver.field.mapping.manage.order" /></h3>
	</div>
	<div class="panel-body">
					<s:form namespace="" action="" id="csvDriverFieldMappingManageOrderForm" method="post" cssClass="form-horizontal" labelCssClass="col-xs-4 col-lg-2 text-right" elementCssClass="col-xs-8 col-lg-10"  >
						<table id="csvDriverFieldMappingManageOrderTable" style="width: 100%;" class="table table-blue dataTable no-footer" role="grid" width="100%">
							<thead>
								<tr role="row">
									<th style="width: 10px"> Order# </th>
									<th> <s:text name="csv.field.mapping.header"/> </th>
									<th> <s:text name="csv.mapping.pcrf.key"/> </th>
								</tr>
							</thead>
							<tbody>
							<s:iterator value="csvDriverFieldMappingDataList" var="csvDriverFieldMappingData">
								<tr>
									<td class="dataTables_empty" > <s:property value="#csvDriverFieldMappingData.orderNo"/> </td>
									<td class="dataTables_empty" >
										<s:property value="#csvDriverFieldMappingData.headerField"/>
										<input type="hidden" name="csvDriverFieldMappingDataIds" value="${csvDriverFieldMappingData.id}"/>
									</td>
									<td class="dataTables_empty" > <s:property value="#csvDriverFieldMappingData.pcrfKey"/> </td>
								</tr>
							</s:iterator>
						</table>
					</s:form>
					<div id="qosRowOrder"></div>
				<div class="row">
					<div class="col-xs-12" align="center">
						<button id="btnSave" class="btn btn-primary btn-sm"  value="Save" onclick="submitForm();" ><span class="glyphicon glyphicon-floppy-disk" title="save"></span> <s:text name="button.save"></s:text> </button>
						<button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14"
								onclick="javascript:location.href='${pageContext.request.contextPath}/sm/driver/csv-driver/${id}'">
							<span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="Driver"/>
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
				<span><s:text name="csv.driver.field.mapping.manage.order.no.change"/></span>
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
            $("#csvDriverFieldMappingManageOrderTable").rowSorter({
                onDrop: function(tbody, row, index, oldIndex) {
                    $('#csvDriverFieldMappingManageOrderTable tr').each(function() {
                        hasOrderChanged = true;
                    });
                    var headerKey = $(row).children(":eq(1)").text();
                    $("#qosRowOrder").html(headerKey + "HeaderKey order changed to " + (index + 1)+" from "+(oldIndex + 1));
                }
            });
        };

        enableDnD();

        submitForm = function (){
            if(hasOrderChanged){
            	$("#csvDriverFieldMappingManageOrderForm").attr("action","${pageContext.request.contextPath}/sm/driver/csv-driver/${id}/manageOrder")
            	$( "#csvDriverFieldMappingManageOrderForm" ).submit();
			}else{
                $("#alertFailure").modal('show');
            }
        };
    });
</script>
