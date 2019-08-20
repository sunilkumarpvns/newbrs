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
	
	<div class="panel-heading">
        <h3 class="panel-title"><s:text name="ratecardgroup.manage.order" /></h3>
    </div>
    
	<div class="panel-body">
			
				<div class="row" style="padding: 8px 15px">
					<s:form namespace=""
							action=""
							id="rateCardGroupUpdateOrderForm"
							method="post"
							cssClass="form-horizontal"
							labelCssClass="col-xs-4 col-lg-2 text-right" elementCssClass="col-xs-8 col-lg-10"  >
						<table id="rateCardGroupOrderTable" style="width: 100%;" class="table table-blue dataTable no-footer" role="grid" width="100%">

							<thead>
								<tr role="row">
									<th style="width: 10px"><s:text name="ratecardgroup.order.no"/></th>
									<th> <s:text name="ratecardgroup.name"/> </th>
									<th> <s:text name="ratecardgroup.description"/> </th>
									<th> <s:text name="ratecardgroup.labels"/> </th>
								</tr>
							</thead>
							<tbody>
							<s:iterator value="list" var="rateCardGroup">
								<tr>
									<td class="dataTables_empty" > <s:property value="#rateCardGroup.orderNo"/> </td>
									<td class="dataTables_empty" >
										<s:property value="#rateCardGroup.name"/>
										<input type="hidden" name="rateCardGroupIds" value="${rateCardGroup.id}"/>
									</td>
									<td class="dataTables_empty" > <s:property value="#rateCardGroup.description"/> </td>
									<td class="dataTables_empty" > <s:property value="#rateCardGroup.labels"/> </td>
								</tr>
							</s:iterator>
						</table>
					</s:form>
					<div id="qosRowOrder"></div>
				</div>
				<div class="row">
					<div class="col-xs-12" align="center">
						<button id="btnSave" class="btn btn-primary btn-sm"  value="Save" onclick="submitForm();" ><span class="glyphicon glyphicon-floppy-disk" title="save"></span> <s:text name="button.save"></s:text> </button>
						<button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" 
							onclick="javascript:location.href='${pageContext.request.contextPath}/pd/rncpackage/rnc-package/<s:property value='#request.rncPackageId'/>'" tabindex="6">
						<span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.cancel"/></button>
					</div>
				</div>
			
		
	</div>
</div>

<!-- Modal HTML -->
<div id="alertFailure" class="modal fade" >
	<div class="modal-dialog">
		<div class="modal-content" style="width: 300px" >
			<div class="modal-body modal-sm">
				<span><s:text name="rcg.nochangefound"/></span>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-sm btn-danger" data-dismiss="modal"><s:text name="button.ok"></s:text></button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/sort/jquery.rowsorter.js"></script>

<script type="text/javascript" >

      $(document).ready(function(){

        var hasOrderChanged = false;

        function enableDnD(){
            $("#rateCardGroupOrderTable").rowSorter({
                onDrop: function(tbody, row, index, oldIndex) {
                    $('#rateCardGroupOrderTable tr').each(function() {
                        hasOrderChanged = true;
                    });
                    var rateCardGroupName = $(row).children(":eq(1)").text();
                    $("#qosRowOrder").html(rateCardGroupName + " order changed to " + (index + 1)+" from "+(oldIndex + 1));
                }
            });
        };

        enableDnD();

        submitForm = function (){
            if(hasOrderChanged){
            	var id =  "<s:property value='#request.rncPackageId'/>";         	
            	$("#rateCardGroupUpdateOrderForm").attr("action","${pageContext.request.contextPath}/pd/ratecardgroup/rate-card-group/*/manageOrder?rncPackageId="+id)
            	$( "#rateCardGroupUpdateOrderForm" ).submit();
			}else{
                $("#alertFailure").modal('show');
            }
        };
    });
</script>
