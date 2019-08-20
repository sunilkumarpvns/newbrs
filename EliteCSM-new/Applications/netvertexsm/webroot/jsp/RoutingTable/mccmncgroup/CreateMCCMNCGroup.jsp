<%@ include file="/jsp/core/includes/common/Header.jsp" %>
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
$(document).ready(function() {	
	setTitle('<bean:message  bundle="routingMgmtResources" key="mccmncgroup.title"/>');
	document.forms[0].name.focus();
	//setOperator();
	setMCCMNCCodeList(); 
});


function addMCCMNCCodes(){
	if(document.forms[0].brandID.value=='0'){
		alert("Atleast one brand must be selected.");
	}else{   
		openMCCMNCCodes();	
	}
	
}

function setMCCMNCCodeList(){
	var brandID=document.getElementById("brandID").value;
	var table=document.getElementById("MCCMNCTable");
	for (var i =table.rows.length-1; i >0; i--)
 	{
		table.deleteRow(i);
 	}
	
 	<%MCCMNCGroupManagementForm form =(MCCMNCGroupManagementForm)request.getAttribute("mccMNCGroupManagmentForm");
	List<NetworkData> codeList=form.getMccmncCodesDataList();%>	
	<%for(NetworkData tmpCode:codeList){%>		

	if(brandID==<%=tmpCode.getBrandID()%>){					
		$("#MCCMNCTable").append("<tr><td class='tblfirstcol' >"+"<input type=checkbox name=mccMNCID value='"+<%=tmpCode.getNetworkID()%>+"'</td>"
		          +"<td class='tblrows' > <%=tmpCode.getOperatorData()!=null?tmpCode.getOperatorData().getName():""%> </td>"
		          +"<td class='tblrows' > <%=tmpCode.getNetworkName()%> </td>"
		          +"<td class='tblrows' >"+<%=tmpCode.getMcc()%> +"-"+ <%=tmpCode.getMnc()%>+"</td></tr>"); 
		          
		}
	<%}%>
}

<%request.getContextPath();%>

function verifyFormat (){
	var searchName = document.getElementById("name").value;
	isValidName = callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.MCCMNC_GROUP%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}

function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.MCCMNC_GROUP%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}

var isValidName;
function validate(){
	verifyName();
	var flag = false;
	if(isNull(document.forms[0].name.value)){
		alert("please provide group name.");
		document.forms[0].name.focus();
	}else if(!isValidName) {
		alert('Enter Valid group Name');
		document.forms[0].name.focus();		
	}else if(document.forms[0].brandID.value=='0'){
		
		alert("Atleast one brand must be selected.");
	}else{   
		flag = true;
	}
	return flag;
}
function clearOperatorSelectTag() {
	document.forms[0].operatorID.options.length=1;
}

</script> 	

<html:form action="/mccmncGroupManagement.do?method=create" onsubmit="return validate();" > 
	
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
	<bean:message  bundle="routingMgmtResources" key="mccmncgroup.create"/>
			</td>
		  </tr>
          <tr> 
				<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		   <tr> 
				<td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table cellpadding="0" cellspacing="0" border="0" width="97%" > 				   	 	
			   	  <tr> 
					<td align="left" class="captiontext" valign="top" width="22%">
						<bean:message bundle="routingMgmtResources" key="mccmncgroup.groupname"/>
						
					</td> 
					<sm:nvNameField maxLength="40" size="32" value="${MCCMNCGroupManagementForm.name}"/>
				  </tr>		
			   	  <tr> 
					<td align="left" class="captiontext" valign="top" width="10%">
						<bean:message bundle="routingMgmtResources" key="mccmncgroup.description"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="network.operator"/>','<bean:message bundle="routingMgmtResources" key="network.operator" />')"/>
						
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:textarea property="description" cols="40" rows="3" styleId="description" tabindex="2" /> 
					</td> 
				  </tr>		
			   	   	 <tr> 
						<td align="left" class="captiontext" valign="top" width="10%" >
							<bean:message bundle="routingMgmtResources" key="network.brand" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="network.brand"/>','<bean:message bundle="routingMgmtResources" key="network.brand" />')"/>
						</td> 
															
						<td align="left" class="labeltext" valign="top" width="70%"> 
							<html:select name="mccmncGroupManagementForm" styleId="brandID" tabindex="3" property="brandID"   onchange="setMCCMNCCodeList();" size="1" style="width: 220px;">
									<html:option value="0">--Select--</html:option>
									<logic:iterate id="brand" name="mccmncGroupManagementForm" property="brandDataList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandData">
										<html:option value="<%=Long.toString(brand.getBrandID())%>"><bean:write name="brand" property="name"/></html:option>
									</logic:iterate>
								</html:select><font color="#FF0000"> *</font> 	      
						</td>
				   </tr>	
			   	 
			   </table>  
			</td> 
		</tr>

		<tr><td>&nbsp;</td></tr>
					<tr>
						<td class="captiontext" colspan="5"><input type="button"
							id="addMCCMNCbutton" value="Add MCC-MNC Code" class="light-btn"
							onclick="addMCCMNCCodes();"  tabindex="5"/></td>
					</tr>
					<td colspan="3">
						<table class="captiontext"width="99%" id="mccmncMappingTable" border="0"
							cellpadding="0" cellspacing="0">
							<tr>
								<td align="left" class="tblheader" valign="top" width="30%"><bean:message
										bundle="routingMgmtResources" key="network.operator" /></td>
								<td align="left" class="tblheader" valign="top" ><bean:message
										bundle="routingMgmtResources" key="network.networkname" /></td>
								<td align="left" class="tblheader" valign="top" ><bean:message
										bundle="routingMgmtResources" key="network.mcc" />-<bean:message
										bundle="routingMgmtResources" key="network.mnc" /></td>
								<td align="center" class="tblheader" valign="top" >Remove</td>
							</tr>
						</table>
					</td>
				
	  </td> 
	 <tr> 
				<td align="center" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
			   	 <tr align="center">
				  		<td align="center" valign="top" ></td>
						<td>
						<html:submit styleClass="light-btn" tabindex="6"  value="  Create  "   />
						<input type="button" value=" Cancel " tabindex="7" class="light-btn" onclick="javascript:location.href='<%=basePath%>/mccmncGroupManagement.do?method=initSearch'"/></td>
				 </tr>
	    <td width="10" class="small-gap">&nbsp;</td> 
    <td class="small-gap" colspan="2">&nbsp;</td> 
  </tr>  
</tr>	
</table>		
<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

 <div id="MCCMNCDiv" title="MCC MNCs" style="display: none;">	
	<table cellpadding="0" cellspacing="0" width="100%" id="MCCMNCTable">
		<tr>
			<td align="left" class="tblheader" valign="top" width="*"><bean:message key="general.select" /></td>
			<td align="left" class="tblheader" valign="top" width="*"><bean:message bundle="routingMgmtResources" key="network.operator" /></td>
			<td align="left" class="tblheader" valign="top" width="*"><bean:message bundle="routingMgmtResources" key="network.networkname" /></td>
			<td align="left" class="tblheader" valign="top" width="*"><bean:message bundle="routingMgmtResources" key="network.mcc" />-<bean:message bundle="routingMgmtResources" key="network.mnc" /></td>
		</tr>
	  </table>
</div>
</html:form>

