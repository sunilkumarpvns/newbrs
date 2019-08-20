
<%@page import="com.elitecore.netvertexsm.web.devicemgmt.form.DeviceManagementForm"%>
<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.netvertexsm.web.RoutingTable.mccmncgroup.form.MCCMNCGroupManagementForm" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandOperatorRelData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.OperatorData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData" %>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.0.7.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/tablednd.css" type="text/css"/>
 
<script type="text/javascript">

$('#mccmncMappingTable td img.delete').on('click',function() {
	$(this).parent().parent().remove(); 
});

function openMCCMNCCodes(){	
	setMCCMNCCodeList();
	var divName = 'MCCMNCDiv';
	var tableName = 'MCCMNCTable';
	var mappingTableName = 'mccmncMappingTable';
	var idArrayName = 'mccmncCode';
	var chkBoxArrayName = 'mccMNCID'; 
	
	$.fx.speeds._default = 1000;
	document.getElementById(divName).style.visibility = "visible";		
	$( "#"+divName ).dialog({
		modal: true,
		autoOpen: false,		
		height: 300,
		minHeight:200,
		width: 500,		
		position:["top", 150],
		buttons:{					
            'Add': function() {
            var tblrowcount = document.getElementById(tableName).rows.length;
        	var tblrows = document.getElementById(tableName).rows;
			var chkBoxElements = document.getElementsByName(chkBoxArrayName);
  
        	if(chkBoxElements!=null && chkBoxElements.length>0){
        		for(var i=0; i<chkBoxElements.length;i++){
        			
        			if(chkBoxElements[i].checked==true && tblrows[i+1].style.display!="none"){
        				tblrows[i+1].style.display="none";
        				$("#"+mappingTableName).append("<tr><td class='tblfirstcol' >" + tblrows[i+1].cells[1].innerHTML + "<input type='hidden' name='"+idArrayName+"' value='"+chkBoxElements[i].value+"'/></td>"
        						          +"<td class='tblrows' >" + tblrows[i+1].cells[2].innerHTML + "</td>"
        						          +"<td class='tblrows' >" + tblrows[i+1].cells[3].innerHTML + "</td>"
        						          +"<td class='tblrows' align='center' ><img src='<%=basePath%>/images/minus.jpg' class='delete' height='15' /></td></tr>");
        				$('#'+mappingTableName+' td img.delete').on('click',function() {
        					$(this).parent().parent().remove();
        				});
        		
        			}
        		}
        		
        	}
        	$(this).dialog('close');
        	
        },                
		    Cancel: function() {
            	$(this).dialog('close');
        	}
        },
    	open: function() {
    		var chkBoxElements = document.getElementsByName(chkBoxArrayName);
    		var idArrayElements = document.getElementsByName(idArrayName);
    		var tblrows = document.getElementById(tableName).rows;
    		
			for(var i=0;i<chkBoxElements.length;i++){
				var hide = false;
				for(var j=0;j<idArrayElements.length;j++){
					if(chkBoxElements[i].value==idArrayElements[j].value){
						tblrows[i+1].style.display="none";
						hide= true;
					}
				}
				if(hide==false){
					chkBoxElements[i].checked = false;
					tblrows[i+1].style.display="";
				}
			}    		
    	},
    	close: function() {
    		
    	}				
	});
	$( "#"+divName).dialog("open");
	
}


<%
MCCMNCGroupManagementForm form =(MCCMNCGroupManagementForm)request.getAttribute("mccMNCGroupManagmentForm");
%>

function setMCCMNCCodeList(){
	var brandID=<%=form.getBrandID()%>;
	var table=document.getElementById("MCCMNCTable");
	for (var i =table.rows.length-1; i >0; i--)
 	{
		table.deleteRow(i);
 	}
 	<%List<NetworkData> codeList=form.getMccmncCodesDataList();%>	
	<%for(NetworkData tmpCode:codeList){%>		

	if(brandID==<%=tmpCode.getBrandID()%>){					
		$("#MCCMNCTable").append("<tr><td class='tblfirstcol' >"+"<input type=checkbox name=mccMNCID value='"+<%=tmpCode.getNetworkID()%>+"'</td>"
		          +"<td class='tblrows' > <%=tmpCode.getOperatorData().getName()%> </td>"
		          +"<td class='tblrows' > <%=tmpCode.getNetworkName()%></td>"
		          +"<td class='tblrows' >"+<%=tmpCode.getMcc()%> +"-"+ <%=tmpCode.getMnc()%>+"</td></tr>");
		          
		}
	<%}%>
}

function verifyFormat (){
	var searchName = document.getElementById("name").value;
	isValidName = callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.MCCMNC_GROUP%>',searchName:searchName,mode:'update',id:'<%=form.getMccmncGroupId()%>'},'verifyNameDiv');
}
function verifyName() {
   
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.MCCMNC_GROUP%>',searchName:searchName,mode:'update',id:'<%=form.getMccmncGroupId()%>'},'verifyNameDiv');

}
var isValidName;
function validate(){
	verifyName();
	var flag = false;
	
	if(isNull(document.forms[0].name.value)){
		alert("please provide group name.");
		document.forms[0].name.focus();
	} else if(!isValidName) {
		alert('Enter Valid group Name');
		document.forms[0].name.focus();		
	} else {   
		flag = true;
	}
	return flag;
}


$(document).ready(function() {		
	verifyName();
	document.forms[0].name.focus();
});

</script> 

<html:form action="/mccmncGroupManagement.do?method=update" onsubmit="return validate();" > 
<html:hidden name="mccmncGroupManagementForm" property="mccmncGroupId"/>
<html:hidden name="mccmncGroupManagementForm" property="operatorID"/>
<html:hidden name="mccmncGroupManagementForm" property="brandID"/>


<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 

   
	<tr> 
		<td colspan="2" align="right"> 
			 	<table cellpadding="0" cellspacing="0" border="0" width="97%">
 			<tr>
				<td class="tblheader-bold"  valign="top" colspan="2"  >	<bean:message  bundle="routingMgmtResources" key="mccmncgroup.update.link"/> </td>
			</tr>			   
			   	
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="16%">            
						<bean:message bundle="routingMgmtResources" key="mccmncgroup.groupname"/>
					</td>
						<sm:nvNameField maxLength="40" size="32" value="${mccmncGroupManagementForm.name}"/>
					</tr>
					 <tr> 
					<td align="left" class="labeltext" valign="top" width="16%">
						<bean:message bundle="routingMgmtResources" key="mccmncgroup.description"/>
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:textarea property="description" cols="40" rows="3" styleId="description" tabindex="2" /> 
					</td> 
				  </tr>				
			   </table>  
			</td> 
		</tr>

		<tr><td>&nbsp;</td></tr>
					
					<td colspan="2" align="right">
						<table width="97%" id="mccmncMappingTable" border="0"
							cellpadding="0" cellspacing="0">
						<tr>
						<td ><input type="button" id="addMCCMNCbutton" value="Add MCC-MNC Code" class="light-btn"
							onclick="openMCCMNCCodes();" tabindex="2" /></td>
						</tr>
						<tr>
								<td align="left" class="tblheaderfirstcol" valign="top" width="30%"><bean:message
										bundle="routingMgmtResources" key="network.operator" /></td>
								<td align="left" class="tblheader" valign="top" ><bean:message
										bundle="routingMgmtResources" key="network.networkname" /></td>
								<td align="left" class="tblheader" valign="top"><bean:message
										bundle="routingMgmtResources" key="network.mcc" />-<bean:message
										bundle="routingMgmtResources" key="network.mnc" /></td>
								<td align="center" class="tblheaderlastcol" valign="top" >Remove</td>
						</tr>
								<logic:iterate id="mccmncCodes" name="mccmncGroupData"  property="mccmncCodeGroupRelDataList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCCodeGroupRelData">
									<tr >
										<td align="left" class="tblfirstcol">
										<bean:write name="mccmncCodes" property="mccmncCodeData.operatorData.name" />  &nbsp;
										<input type='hidden' name='mccmncCode' value="<bean:write name="mccmncCodes" property="mccMNCID"/>" />
										</td>
										<td align="left" class="tblrows"><bean:write name="mccmncCodes" property="mccmncCodeData.networkName" />&nbsp;</td>		
										<td align="left" class="tblrows"><bean:write name="mccmncCodes" property="mccmncCodeData.mcc" />-<bean:write name="mccmncCodes" property="mccmncCodeData.mnc" />&nbsp;</td>		
										<td class='tblrows' align='center' ><img src='<%=basePath%>/images/minus.jpg' class='delete'  height='15' onclick="removeMe()" /></td>	
									</tr>
								</logic:iterate>
						</table>
					</td>
	  </td> 
	 <tr> 
				<td align="center" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
			   	 <tr align="center">
				  		<td align="center" valign="top" ></td>
						<td>
							<html:submit styleClass="light-btn" tabindex="3"  value="  Update  "  />
							<input type="button" value=" Cancel " tabindex="4" class="light-btn" onclick="javascript:location.href='<%=basePath%>/mccmncGroupManagement.do?method=initSearch'"/>
						</td>
				 </tr>
 	    <td width="10" class="small-gap">&nbsp;</td> 
    <td class="small-gap" colspan="2">&nbsp;</td> 
  </tr>  
</tr>			
</table>     	 


 <div id="MCCMNCDiv" title="MCC MNCs" style="display: none;">	
	<table cellpadding="0" cellspacing="0" width="99%" id="MCCMNCTable">
		<tr>
			<td align="left" class="tblheader" valign="top" width="*"><bean:message key="general.select" /></td>
			<td align="left" class="tblheader" valign="top" width="*"><bean:message bundle="routingMgmtResources" key="network.operator" /></td>
			<td align="left" class="tblheader" valign="top" width="30%"><bean:message bundle="routingMgmtResources" key="network.networkname" /></td>
			<td align="left" class="tblheader" valign="top" width="*"><bean:message bundle="routingMgmtResources" key="network.mcc" />-<bean:message bundle="routingMgmtResources" key="network.mnc" /></td>
		</tr>
	  </table>
</div>
</html:form>