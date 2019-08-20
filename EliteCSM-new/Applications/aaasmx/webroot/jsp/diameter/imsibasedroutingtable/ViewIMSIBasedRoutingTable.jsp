<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms.UpdateDiameterPolicyForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData" %>
<%@ page import="com.elitecore.elitesm.web.diameter.imsibasedroutingtable.form.IMSIBasedRoutingTableForm" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIFieldMappingData" %>

<%
	IMSIBasedRoutingTableForm imsiBasedRoutingTableForm = (IMSIBasedRoutingTableForm)request.getAttribute("imsiBasedRoutingTableForm");
	IMSIBasedRoutingTableData imsiBasedRoutingTableData = (IMSIBasedRoutingTableData)request.getAttribute("imsiBasedRoutingTableData");
%>
<LINK REL="stylesheet" TYPE="text/css" href="<%=request.getContextPath()%>/js/datatable/jquery.dataTables.css"/> 
 <script src="<%=request.getContextPath()%>/js/datatable/jquery-1.11.1.min.js"></script> 
<script src="<%=request.getContextPath()%>/js/datatable/jquery.dataTables.min.js"></script>
<script src="<%=request.getContextPath()%>/js/calender/jquery-ui.js "></script>
<script>
function addIMSIMappings(fieldMapId,imsiPrefixVal,primaryPeerVal,secondaryPeerVal,tagValue,primaryPeerId, secondaryPeerId){
	
	var tableRowStr = $("#imsiTableTemplate").find("tr");
	$("#imsiTableMapping").append("<tr>"+$(tableRowStr).html()+"</tr>");
	var tableObj1= $('#imsiTableMapping').find("tr:last");
	
	var imsiPrefixObj = $(tableObj1).find("input[name='imsiRange']");
	$(imsiPrefixObj).attr('value',imsiPrefixVal);
	$(imsiPrefixObj).val(imsiPrefixVal);
	
	var primaryPeerObj=$(tableObj1).find("input[name='primaryPeer']");
	if(primaryPeerVal != "0"){
		$(primaryPeerObj).val(primaryPeerVal);
	}
	var functionTest = "onclick=openViewDetails(this,'"+primaryPeerId+"','"+primaryPeerVal+"','<%=EliteViewCommonConstant.DIAMETER_PEER%>');";
	$(primaryPeerObj).wrap('<span class="view-details-css" '+functionTest+' />');
	
	var secondaryPeerObj=$(tableObj1).find("input[name='secondaryPeer']");
	if(secondaryPeerVal != "0"){
		$(secondaryPeerObj).val(secondaryPeerVal);
	}
	
	functionTest = "onclick=openViewDetails(this,'"+secondaryPeerId+"','"+secondaryPeerVal+"','<%=EliteViewCommonConstant.DIAMETER_PEER%>');";
	$(secondaryPeerObj).wrap('<span class="view-details-css" '+functionTest+' />');
	
	var tagObj = $(tableObj1).find("input[name='tag']");
	$(tagObj).val(tagValue);
	
	
	
}
$(document).ready(function(){
	
	$('input[readonly]').focus(function(){
	    this.blur();
	});
	
	<%  Set<IMSIFieldMappingData> imsiFieldMappingDataSet = imsiBasedRoutingTableForm.getImsiFieldMappingSet();
	if(imsiFieldMappingDataSet != null && imsiFieldMappingDataSet.size() > 0){
		for(IMSIFieldMappingData imsiFieldMappingData : imsiFieldMappingDataSet){%>
			addIMSIMappings('<%=imsiFieldMappingData.getImsiFieldMapId()%>','<%=(imsiFieldMappingData.getImsiRange() == null ? "" :imsiFieldMappingData.getImsiRange())%>','<%=(imsiFieldMappingData.getPrimaryPeerName() == null ? "0" : imsiFieldMappingData.getPrimaryPeerName())%>','<%=(imsiFieldMappingData.getSecondaryPeerName() == null ? "0" :imsiFieldMappingData.getSecondaryPeerName())%>','<%=(imsiFieldMappingData.getTag() == null ? "" :imsiFieldMappingData.getTag())%>','<%=(imsiFieldMappingData.getPrimaryPeerId() == null ? "":imsiFieldMappingData.getPrimaryPeerId())%>','<%=(imsiFieldMappingData.getSecondaryPeerId() == null ? "" : imsiFieldMappingData.getSecondaryPeerId())%>');
	 <%}
     }%>

     oTable = $("#imsiTableMapping").dataTable({
 	    "bPaginate":true,
 	    "sPaginationType":"full_numbers",
 	    "aLengthMenu": [[10, 25, 100, -1], [10, 25, 100, "All"]],
 	    "iDisplayLength": 10,
 	    "sDom": 'Rlfrtip',
 	    "bFilter" : false,               
 	    "columns": [
 	                { "orderDataType": "dom-text", type: 'string'},
 	                { "orderDataType": "dom-select"},
 	                { "orderDataType": "dom-select" },
 	                {"orderDataType" : "dom-text"}
 	            ]
 	});
	
});

	/* Create an array with the values of all the input boxes in a column */
	$.fn.dataTable.ext.order['dom-text'] = function  ( settings, col )
	{
	    return this.api().column( col, {order:'index'} ).nodes().map( function ( td, i ) {
	        return $('input', td).val();
	    } );
	}
	 
	/* Create an array with the values of all the input boxes in a column, parsed as numbers */
	$.fn.dataTable.ext.order['dom-text-numeric'] = function  ( settings, col )
	{
	    return this.api().column( col, {order:'index'} ).nodes().map( function ( td, i ) {
	        return $('input', td).val() * 1;
	    } );
	}
	 
	/* Create an array with the values of all the select options in a column */
	$.fn.dataTable.ext.order['dom-select'] = function  ( settings, col )
	{
	    return this.api().column( col, {order:'index'} ).nodes().map( function ( td, i ) {
	        return $('select', td).val();
	    } );
	}
	 
	/* Create an array with the values of all the checkboxes in a column */
	$.fn.dataTable.ext.order['dom-checkbox'] = function  ( settings, col )
	{
	    return this.api().column( col, {order:'index'} ).nodes().map( function ( td, i ) {
	        return $('input', td).prop('checked') ? '1' : '0';
	    } );
	}
</script>
<style type="text/css">
.ui-button{
	background-color: transparent;
	border: none;
	background: none;
}
.ui-dialog-titlebar-close:hover{
	background-color: transparent;
	border: none;
	background: none;
}
</style>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <bean:define id="imsiBasedRoutingTableDataBean" name="imsiBasedRoutingTableData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData" />
    <tr> 
    
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" >
	          <tr> 
	            <td class="tblheader-bold" colspan="2" height="20%">
	              <bean:message bundle="diameterResources" key="imsibasedroutingtable.viewroutingtable"/>
	            </td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="imsibasedroutingtable.routingtablename" /> </td>
	            <td class="tblcol" width="70%" ><bean:write name="imsiBasedRoutingTableDataBean" property="routingTableName"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="imsibasedroutingtable.imsiidentityattribute" /> </td>
	            <td class="tblcol" width="70%" ><bean:write name="imsiBasedRoutingTableDataBean" property="imsiIdentityAttributes"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblheader-bold" colspan="2" height="20%">
	              <bean:message bundle="diameterResources" key="imsibasedroutingtable.viewroutingentries"/>
	            </td>
	          </tr>
	          <tr>
	          	<td class="labeltext" colspan="2">
	          		<div id="dvData">
						<table width="100%" cellspacing="0" cellpadding="0" border="0" id="imsiTableMapping" class="imsiTableMapping display" >
								<thead>
									<tr>	
										<td class="tblheader" width="24%">	
											<bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.imsirange" />
										</td>
										<td class="tblheader" width="24%">	
											<bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.primarypeer" />
										</td>
										<td class="tblheader" width="24%">	
											<bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.secondarypeer" />
										</td>
										<td class="tblheader" width="24%">	
											<bean:message bundle="diameterResources" key="routingconf.imsibasedrouting.tag" />
										</td>
									</tr>
								</thead>
								<tboby>
								</tboby>
						</table>
					</div>
	          	</td>
	          </tr>
		</table>
		</td>
    </tr> 
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>   
   </tr>
</table>
<table width="100%" cellspacing="0" cellpadding="0" border="0" style="display: none;" id="imsiTableTemplate">
	<tr>	
		<td class="allborder" width="24%">	
			<input type="text"  style="width:250px" class="noborder" name="imsiRange" readonly="readonly"/> 
 		</td>
		<td class="tblrows" width="24%">
			<input type="text"  style="width:250px;cursor: pointer;color: transparent;text-shadow: 0 0 0 #015198;" class="noborder view-details-css" name="primaryPeer" readonly="readonly"/> 
		</td>
		<td class="tblrows" width="24%">
			<input type="text"  style="width:250px;cursor: pointer;color: transparent;text-shadow: 0 0 0 #015198;" class="noborder view-details-css" name="secondaryPeer" readonly="readonly"/> 
		</td>
		<td class="tblrows" width="24%">	
			<input type="text"  style="width:250px" class="noborder" name="tag" readonly="readonly"/> 
		</td>
	</tr>
</table>
