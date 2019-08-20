<%@page import="com.elitecore.elitesm.util.constants.EliteViewCommonConstant"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNFieldMappingData"%>
<%@page import="com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData"%>
<%@page import="com.elitecore.elitesm.web.diameter.msisdnbasedroutingtable.form.MSISDNBasedRoutingTableForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.web.diameter.policies.diameterpolicy.forms.UpdateDiameterPolicyForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.diameterpolicy.data.DiameterPolicyData"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.imsibasedroutingtable.data.IMSIBasedRoutingTableData" %>
<%@ page import="com.elitecore.elitesm.web.diameter.imsibasedroutingtable.form.IMSIBasedRoutingTableForm" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Set"%>

<%
	MSISDNBasedRoutingTableForm msisdnBasedRoutingTableForm = (MSISDNBasedRoutingTableForm)request.getAttribute("msisdnBasedRoutingTableForm");
	MSISDNBasedRoutingTableData msisdnBasedRoutingTableData = (MSISDNBasedRoutingTableData)request.getAttribute("msisdnBasedRoutingTableData");
%>
<LINK REL="stylesheet" TYPE="text/css" href="<%=request.getContextPath()%>/js/datatable/jquery.dataTables.css"/> 
 <script src="<%=request.getContextPath()%>/js/datatable/jquery-1.11.1.min.js"></script> 
<script src="<%=request.getContextPath()%>/js/datatable/jquery.dataTables.min.js"></script>
<script src="<%=request.getContextPath()%>/js/calender/jquery-ui.js "></script>
<script>
function addMSISDNMappings(fieldMapId,imsiPrefixVal,primaryPeerVal,secondaryPeerVal,tagValue,primaryPeerId, secondaryPeerId){
	
	var tableRowStr = $("#msisdnTableTemplate").find("tr");
	$("#msisdnTableMapping").append("<tr>"+$(tableRowStr).html()+"</tr>");
	var tableObj1= $('#msisdnTableMapping').find("tr:last");
	
	var msisdnRangeObj = $(tableObj1).find("input[name='msisdnRange']");
	$(msisdnRangeObj).attr('value',imsiPrefixVal);
	$(msisdnRangeObj).val(imsiPrefixVal);
	
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
	
	<%  Set<MSISDNFieldMappingData> msisdnFieldMappingDataSet = msisdnBasedRoutingTableForm.getMsisdnFieldMappingSet();
	if(msisdnFieldMappingDataSet != null && msisdnFieldMappingDataSet.size() > 0){
		for(MSISDNFieldMappingData msisdnFieldMappingData : msisdnFieldMappingDataSet){%>
			addMSISDNMappings('<%=msisdnFieldMappingData.getMsisdnFieldMapId()%>','<%=(msisdnFieldMappingData.getMsisdnRange() == null ? "" :msisdnFieldMappingData.getMsisdnRange())%>','<%=(msisdnFieldMappingData.getPrimaryPeerName() == null ? "0" : msisdnFieldMappingData.getPrimaryPeerName())%>','<%=(msisdnFieldMappingData.getSecondaryPeerName() == null ? "0" :msisdnFieldMappingData.getSecondaryPeerName())%>','<%=(msisdnFieldMappingData.getTag() == null ? "" :msisdnFieldMappingData.getTag())%>','<%= msisdnFieldMappingData.getPrimaryPeerId() == null ? "" : msisdnFieldMappingData.getPrimaryPeerId()%>','<%= msisdnFieldMappingData.getSecondaryPeerId() == null ? "" : msisdnFieldMappingData.getSecondaryPeerId()%>');
	 <%}
     }%>

     oTable = $("#msisdnTableMapping").dataTable({
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
    <bean:define id="msisdnBasedRoutingTableDataBean" name="msisdnBasedRoutingTableData" scope="request" type="com.elitecore.elitesm.datamanager.diameter.msisdnbasedroutingtable.data.MSISDNBasedRoutingTableData" />
    <tr> 
    
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" >
	          <tr> 
	            <td class="tblheader-bold" colspan="2" height="20%">
	              <bean:message bundle="diameterResources" key="msisdnbasedroutingtable.viewroutingtable"/>
	            </td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="msisdnbasedroutingtable.tablename" /> </td>
	            <td class="tblcol" width="70%" ><bean:write name="msisdnBasedRoutingTableDataBean" property="routingTableName"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="msisdnbasedroutingtable.msisdnidentityattributes" /> </td>
	            <td class="tblcol" width="70%" ><bean:write name="msisdnBasedRoutingTableDataBean" property="msisdnIdentityAttributes"/>&nbsp;</td>
	          </tr>
	           <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="msisdnbasedroutingtable.msisdnlength" /> </td>
	            <td class="tblcol" width="70%" ><bean:write name="msisdnBasedRoutingTableDataBean" property="msisdnLength"/>&nbsp;</td>
	          </tr>
	           <tr> 
	            <td class="tblfirstcol" width="30%" ><bean:message bundle="diameterResources" key="msisdnbasedroutingtable.mcc" /> </td>
	            <td class="tblcol" width="70%" ><bean:write name="msisdnBasedRoutingTableDataBean" property="mcc"/>&nbsp;</td>
	          </tr>
	          <tr> 
	            <td class="tblheader-bold" colspan="2" height="20%">
	              <bean:message bundle="diameterResources" key="msisdnbasedroutingtable.routingentries"/>
	            </td>
	          </tr>
	          <tr>
	          	<td class="labeltext" colspan="2">
	          		<div id="dvData">
						<table width="100%" cellspacing="0" cellpadding="0" border="0" id="msisdnTableMapping" class="msisdnTableMapping display" >
								<thead>
									<tr>	
										<td class="tblheader" width="24%">	
											<bean:message bundle="diameterResources" key="msisdnbasedroutingtable.msisdnrange" />
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
<table width="100%" cellspacing="0" cellpadding="0" border="0" style="display: none;" id="msisdnTableTemplate">
	<tr>	
		<td class="allborder" width="24%">	
			<input type="text"  style="width:250px" class="noborder" name="msisdnRange" readonly="readonly"/> 
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

