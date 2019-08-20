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
		<h3 class="panel-title"><s:text name="manage.order" /></h3>
	</div>
	<div class="panel-body">
					<s:form namespace="" action="" id="serverInstanceGroovyScriptManagerOrderForm" method="post" cssClass="form-horizontal" labelCssClass="col-xs-4 col-lg-2 text-right" elementCssClass="col-xs-8 col-lg-10"  >
						<table id="serverInstanceGroovyScriptManageOrderTable" style="width: 100%;" class="table table-blue dataTable no-footer" role="grid" width="100%">
							<thead>
								<tr role="row">
									<th style="width: 10px"> <s:text name="serverinstance.groovy.script.orderno"/> </th>
									<th> <s:text name="serverinstance.groovy.script.name"/> </th>
									<th> <s:text name="serverinstance.groovy.script.argument"/> </th>
								</tr>
							</thead>
							<tbody>
							<s:iterator value="groovyScriptList" var="groovyScriptData">
								<tr>
									<td class="dataTables_empty" > <s:property value="#groovyScriptData.orderNumber"/> </td>
									<td class="dataTables_empty" >
										<s:property value="#groovyScriptData.scriptName"/>
										<input type="hidden" name="groovyScriptIds" value="${groovyScriptData.id}"/>
									</td>
									<td class="dataTables_empty" > <s:property value="#groovyScriptData.argument"/> </td>
								</tr>
							</s:iterator>
						</table>
					</s:form>
					<div id="groovyScriptRowOrder"></div>
				<div class="row">
					<div class="col-xs-12" align="center">
						<button id="btnSave" class="btn btn-primary btn-sm"  value="Save" onclick="submitForm();" ><span class="glyphicon glyphicon-floppy-disk" title="save"></span> <s:text name="button.save"></s:text> </button>
						<button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14"
								onclick="javascript:location.href='${pageContext.request.contextPath}/sm/serverinstance/server-instance/${id}'">
							<span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="Server Instance"/>
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
				<span><s:text name="serverinstance.groovy.script.manage.order.no.change"/></span>
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
            $("#serverInstanceGroovyScriptManageOrderTable").rowSorter({
                onDrop: function(tbody, row, index, oldIndex) {
                    $('#serverInstanceGroovyScriptManageOrderTable tr').each(function() {
                        hasOrderChanged = true;
                    });
                    var headerKey = $(row).children(":eq(1)").text();
                    $("#groovyScriptRowOrder").html(headerKey + "Groovy Script order changed to " + (index + 1)+" from "+(oldIndex + 1));
                }
            });
        };

        enableDnD();

        submitForm = function (){
            if(hasOrderChanged){
            	$("#serverInstanceGroovyScriptManagerOrderForm").attr("action","${pageContext.request.contextPath}/sm/serverinstance/server-instance/${id}/manageOrder")
            	$("#serverInstanceGroovyScriptManagerOrderForm").submit();
			}else{
                $("#alertFailure").modal('show');
            }
        };
    });
</script>