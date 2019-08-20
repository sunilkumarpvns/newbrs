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
		<h3 class="panel-title"><s:text name="pkg.emergency.manager.order" /></h3>
	</div>
	<div class="panel-body">
		<div class="row" >
			<div class="container-fluid">
				<ul class="nav nav-tabs tab-headings" id="tabs">
					<s:iterator value="groupWiseOrderMap" status="order">
						<s:if test="%{#order.count ==1}">
							<li class="active" id="tab-<s:property value='key'/>">
						</s:if>
						<s:else>
							<li id="tab-<s:property value='key'/>" >
						</s:else>
						<a data-toggle="tab" href="#<s:property value='key'/>"><s:property value="key"/></a>
						</li>
					</s:iterator>
				</ul>
				<div class="tab-content">
					<s:iterator value="groupWiseOrderMap" status="order">
					<s:if test="%{#order.count ==1}">
					<div id="<s:property value='key'/>" class="tab-pane fade in active">
						</s:if>
						<s:else>
						<div id="<s:property value='key'/>" class="tab-pane fade">
							</s:else>
								<div class="row" style="padding: 8px 15px">
									<s:form namespace="/excludeValidation" action="policydesigner/emergency/EmergencyPkg/manageOrderEmergencyPackages" id="promotionalManageOrderForm-%{key}"  method="post" cssClass="form-horizontal" labelCssClass="col-xs-4 col-lg-2 text-right" elementCssClass="col-xs-8 col-lg-10" >
										<s:hidden id="promotionalPackagesIdsAndOrders-%{key}" name="promotionalPackagesIdsAndOrders" />
										<s:hidden value="%{key}" name="groupName" />
										<s:hidden value="%{#session.staffBelongingGroupIds}" name="groupIds" />

										<table id="promotionalPkgData-<s:property value='key'/>"  class="table table-blue" width="100%">
											<thead>
											<tr>
												<th id="dataTable.RowNumber" >#</th>
												<th id="name" ><s:text name="pkg.name"/></th>
												<th id="type"><s:text name="pkg.type"/></th>
												<th id="status" ><s:text name="pkg.status"/></th>
												<th id="packageMode"><s:text name="pkg.mode"/></th>
											</tr>
											</thead>
											<tbody id="tbody-<s:property value='key'/>">
											<s:iterator value="value" status="i">
												<tr>
													<td><s:hidden name="pkgGroupDataIdArray" value="%{id}"/>
														<s:property value="%{#i.count}"/></td>
													<td><s:hidden name="idArray" value="%{pkgData.id}"/>
														<a href="${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/view?pkgId=${pkgData.id}">
															<s:property value="%{pkgData.name}"/>
														</a>
													</td>
													<td><s:property value="%{pkgData.type}"/></td>
													<td><s:property value="%{pkgData.status}"/></td>
													<td><s:property value="%{pkgData.packageMode}"/></td>
												</tr>
											</s:iterator>
											</tbody>
										</table>
										<div class="col-xs-12" id="rowOrder-<s:property value='key'/>"></div>
									</s:form>
								</div>
								<div class="row">
									<div class="col-xs-12" align="center">
										<button id="btnSave" class="btn btn-primary btn-sm"  value="Save" onclick="prepareParemeters('<s:property value="key"/>');" ><span class="glyphicon glyphicon-floppy-disk" title="save"></span> <s:text name="button.save"></s:text> </button>
										<button id="btnBack" class="btn btn-primary btn-sm"  value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/search'"><span class="glyphicon glyphicon-backward" title="Back"></span>
											<s:text name="button.back"></s:text>
										</button>
									</div>
								</div>
						</div>
							<div id="alertFailure-<s:property value='key'/>" class="modal fade">
								<div class="modal-dialog">
									<div class="modal-content" style="width:270px;">
										<div class="modal-body modal-sm">
											<span id="noChange">No Change found in Packages Order !</span>
												<%--<span id="saveChange">Please save the changes!!!<br/>
                                                      Before Proceeding to another Tab  !</span>--%>
										</div>
										<div class="modal-footer">
											<button type="button" class="btn btn-sm btn-danger" data-dismiss="modal" >Ok</button>
										</div>
									</div>
								</div>
							</div>
							</s:iterator>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>



<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/sort/jquery.rowsorter.js"></script>

<script type="text/javascript" >

	$(document).ready(function(){


		$('#tabs a').click(function (e) {
			e.preventDefault();
			var currentyActiveGroupId = $("li.active").eq("0").attr("id").split("tab-").pop();
			if(isNullOrEmpty($("#rowOrder-"+currentyActiveGroupId).text()) == true){
				$(this).tab('show');
			}else{
				var confirm = window.confirm("Please save unsaved changes before proceeding !!! ");
				if(confirm == true ){
					return false;
				}else {
					$(this).tab('show');
				}
			}
		});

		// store the currently selected tab in the hash value
		$("ul.nav-tabs > li > a").on("shown.bs.tab", function (e) {
			var id = $(e.target).attr("href").substr(1);
			window.location.hash = id;
		});

		// on load of the page: switch to the currently selected tab
		var hash = window.location.hash;
		$('#tabs a[href="' + hash + '"]').tab('show');


		function enableDnD(){
			$(".table").rowSorter({
				onDrop: function(tbody, row, index, oldIndex) {
					var count =1;
					var pkgArray =  new Array();
					var group = $(tbody).attr("id").split("-")[1];
					$(tbody).children("tr").each(function() {
						var packageId = $(this).children().eq(1).children().eq(0).val();
						var packageIdAndOrder = packageId+"="+count;
						pkgArray.push(packageIdAndOrder);
						count++;
					});
					$("#promotionalPackagesIdsAndOrders-"+group).val(pkgArray.toString());
					var packageName = $(row).children(":eq(1)").text();
					$("#rowOrder-"+group).html("<b>"+ packageName + "</b> order changed to <b>" + (index + 1)+"</b> from <b>"+(oldIndex + 1)+" </b>");
				}
			});
		};

		enableDnD();
		prepareParemeters = function(group){
			if(isNullOrEmpty($("#rowOrder-"+group).html())==true){
				$("#alertFailure-"+group).modal('show');
			}else if(isNullOrEmpty($("#promotionalPackagesIdsAndOrders-"+group).val()) == false){
				$( "#promotionalManageOrderForm-"+group).submit();
			}else{
				$('#alertFailure-'+group).find('span').text("Improper ID and Order !");
				$("#alertFailure-"+group).modal('show');
			}
		};

	});
</script>