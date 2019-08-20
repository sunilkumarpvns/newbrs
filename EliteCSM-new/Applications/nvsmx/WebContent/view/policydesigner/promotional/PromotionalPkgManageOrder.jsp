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
		<h3 class="panel-title">
			<s:text name="pkg.promotional.manager.order" />
		</h3>
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
							<%@ include file="PromotionalPkgManageOrderTabContent.jsp"%>
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

		var proceed = false;
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
		console.log("window.location.hash ---->> "+window.location.hash);
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