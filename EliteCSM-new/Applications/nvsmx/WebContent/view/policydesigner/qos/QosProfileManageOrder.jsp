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
<%
int rows = ConfigurationProvider.getInstance().getPageRowSize();
%>
<div class="panel panel-primary">
	<div class="panel-heading" style="padding: 8px 15px">
		 <s:text name="qos.manage.order"/>
	</div>
	<div class="panel-body">
	    <div class="row" >
           <fieldset class="fieldSet-line">
				 
           		<div class="row" style="padding: 8px 15px">
           		<s:form namespace="/excludeValidation" action="policydesigner/qos/QosProfile/manageOrderQosProfiles"  id="qosProfileUpdateOrderForm" method="post" cssClass="form-horizontal" labelCssClass="col-xs-4 col-lg-2 text-right" elementCssClass="col-xs-8 col-lg-10"  >
					<s:hidden name="groupIds"/>
					<s:hidden name="pkgId" value="%{pkgId}"/>
					<s:hidden id="qosProfilesIdsAndOrders" name="qosProfilesIdsAndOrders" />
           		<nv:dataTable 
		 				id="qosProfileData" 
		 				actionUrl="/searchTable/policydesigner/util/Nvsmx/execute?pkgId=${pkgId}"
						beanType="com.elitecore.nvsmx.policydesigner.model.pkg.qos.QosProfileDetailWrapper" 
						rows="5" 
						showPagination="false"
						showInfo="false"
						cssClass="table table-blue" 
						width="100%"> 
						
						<nv:dataTableColumn title="Order#"   	
											beanProperty="dataTable.RowNumber"
											style="font-weight: bold;color: #4679bd;" 
											tdStyle="text-align:left; word-wrap:break-word;width:10px;"
											/> 										
							
						<nv:dataTableColumn title="Name"   	
											beanProperty="name" 	
											style="font-weight: bold;color: #4679bd;" 
											tdStyle="text-align:left; word-wrap:break-word;" 											
											hrefurl="${pageContext.request.contextPath}/policydesigner/qos/QosProfile/view?qosProfileId=id"/>
											
	                	<nv:dataTableColumn title="Quota Profile" 	
											beanProperty="quotaProfileName"
											cssClass="qos-th-col-left"											 											 										
											tdStyle="text-align:left; word-wrap:break-word;" style="width:200px" />

						<nv:dataTableColumn title="MBR" 	
											beanProperty="mbrdl,mbrul"
											cssClass="qos-th-col"											 											 										
											tdCssClass="qos-td-cell"	/>
																	
						<nv:dataTableColumn title="AAMBR" 	
											beanProperty="aambrdl,aambrul" 
											cssClass="qos-th-col" 
											tdCssClass="qos-td-cell"	/>
											
						<nv:dataTableColumn title="QCI" 	
											beanProperty="qci" 		
											cssClass="qos-th-col" 
											tdCssClass="qos-td-cell"/>
						 
					 </nv:dataTable>	
					 <div id="qosRowOrder"></div> 	
				</s:form>
					 
				</div>
				<div class="row">
					<div class="col-xs-12" align="center">						
						<button id="btnSave" class="btn btn-primary btn-sm"  value="Save" onclick="prepareParemeters();" ><span class="glyphicon glyphicon-floppy-disk" title="save"></span> <s:text name="button.save"></s:text> </button>
						<button id="btnBack" class="btn btn-primary btn-sm"  value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${pkgId}'"><span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.pkg"></s:text> </button>
					</div>
				</div>
			</fieldset>
       </div>
	</div>
</div>

<!-- Modal HTML -->
<div id="alertFailure" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content" style="width:270px;">
            <div class="modal-body modal-sm">
                <span>No Change found in QoS Profiles Order !</span>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-danger" data-dismiss="modal" >Ok</button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/sort/jquery.rowsorter.js"></script>
 
<script type="text/javascript" >

$(document).ready(function(){
	
	var hasOrderChanged = false;
	var qosProfileArray = new Array();
	function enableDnD(){
		$("#qosProfileData").rowSorter({
			onDrop: function(tbody, row, index, oldIndex) {
				var count =0;
				qosProfileArray =  new Array();

				$('#qosProfileData tr').each(function() {
					if(count!=0){						
						var qosOrder = $(this).children(":eq(0)").text();
						var qosNameHTML = $(this).children(":eq(1)").html();
						if(qosNameHTML!=null && qosNameHTML!='null' && qosNameHTML!='undefined'){
							var href = $(qosNameHTML).attr("href");
							var qosProfileParamAndId = href.split("?")[1];
							var qosProfileId = qosProfileParamAndId.split("=")[1];
							var qosIdAndOrder = qosProfileId+"="+count;
							qosProfileArray.push(qosIdAndOrder);
						}
					}
					hasOrderChanged = true;					
					count++;
				});
				
				var qosName = $(row).children(":eq(1)").text();
				$("#qosRowOrder").html(qosName + " order changed to " + (index + 1)+" from "+(oldIndex + 1));
			}
		});
	};
	
	enableDnD();
	prepareParemeters = function(){
		if(hasOrderChanged==false){
			$("#alertFailure").modal('show');
    		
		}else if(qosProfileArray!=null && qosProfileArray!='null' && qosProfileArray!='undefined' && qosProfileArray.length>0 ){
			document.getElementById("qosProfilesIdsAndOrders").value = qosProfileArray.toString();
			$( "#qosProfileUpdateOrderForm" ).submit();
			
 		}else{
 			$('#alertFailure').find('span').text("Improper QoSProfiles ID and Order !");  
			$("#alertFailure").modal('show');
		}
	};
	
});
</script>
