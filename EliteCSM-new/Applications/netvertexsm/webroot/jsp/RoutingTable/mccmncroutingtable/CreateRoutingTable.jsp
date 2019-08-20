
<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.netvertexsm.web.RoutingTable.mccmncroutingtable.form.RoutingTableManagementForm" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData" %>
<%@page import="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableData"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData" %>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>


<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.0.7.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.tablednd.js"></script>
<script>
var validateUrl = '<%=basePath%>/validate.do?';
var basePath = '<%=basePath%>';
var image = '<%=request.getContextPath()%>/images/tick.jpg';
</script>
<script language="javascript" 	src="<%=request.getContextPath()%>/js/validateMapping.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/tablednd.css" type="text/css"/>

<script type="text/javascript">
 $(document).ready(function() {	
	setTitle('<bean:message  bundle="routingMgmtResources" key="routingtable.title"/>');
	document.forms[0].name.focus(); 
	setRealmCondition();
	disableAddRoutingGateway();
	$('table td img.delete').on('click',function() {
		$(this).parent().parent().remove(); 
	});
	
	<%RoutingTableManagementForm form =(RoutingTableManagementForm)request.getAttribute("routingTableManagementForm");
	List<GatewayData> gatewayList=form.getGatewayList();
	StringBuilder optionStr=new StringBuilder();
	StringBuilder optinsWeightage=new StringBuilder();
	for(GatewayData tempGateWay:gatewayList){
        optionStr.append("<option value=").append(tempGateWay.getGatewayId()).append(">").append(tempGateWay.getGatewayName()).append("</option>");
       }	
	for(int i=0;i<=10;i++){
		optinsWeightage.append("<option value=").append(i);
		if(i==1){
			optinsWeightage.append(" selected>");
		}else{
			optinsWeightage.append(">");
		}optinsWeightage.append(i).append("</option>");
	}
	%>
	$('#addRoutingGateway').click(function() {
		$("#gatewayMappingTable").append("<tr><td class='tblfirstcol' ><select Id='gatewayId' tabindex='7' style='width:100%;' class='noborder' name='gatewayId'>+'<%=optionStr%>'+</select></td>"
		          +"<td class='tblfirstcol' ><select Id='weightage' tabindex='7' style='width:99%;' class='noborder' name='weightage'>+'<%=optinsWeightage%>'+</select></td>"
		          +"<td class='tblfirstcol' align='center' ><img src='<%=basePath%>/images/minus.jpg' class='delete' onclick='$(this).parent().parent().remove();' height='15' tabindex='7' /></td></tr>");
	});	
}); 

 function setRealmCondition(){
	   var routingType=document.getElementById("type").value;
	  	if(routingType=='MCCMNC'){
		  $("#mccmncGroupSelect").show();
		  $("#defineRealmCondition").hide();
		 }
	  	else if(routingType=='REALM'){
			$("#mccmncGroupSelect").hide();
			$("#defineRealmCondition").show();
	  }
  }

 function validateMCCMNCGroup(){
	 var routingType=document.getElementById("type").value;
	 if(routingType=='MCCMNC'){
		 var routingGroup = document.getElementById("mccmncGroupId").value;
		 if(routingGroup == ""){
			 return false;
		 }
	 }
	 return true;
 } 

function duplicateGateway(){
	var flag=false;
	var gatewayId=document.getElementsByName("gatewayId");
	if(gatewayId.length>0){
		for(var j=0;j<gatewayId.length-1;j++){
		for(var k=j+1;k<gatewayId.length;k++){
			if(gatewayId[j].value==gatewayId[k].value){
				flag=true;
			}
		}
	}
	}
	return flag;
}
								
function validGatewayEntry(){
	if(document.forms[0].action.value =='2'){			
        if(document.getElementsByName("gatewayId").length==0){
            return true;
            }
		}else
			return false;
}


function disableAddRoutingGateway(){
	
	var type=document.getElementById("action").value;
	if(type=='0'){
		document.getElementById("addRoutingGateway").disabled = true;
		document.getElementById("addRoutingGateway").setAttribute("class", "light-btn-disabled");	
		var id = "dataRow";
		$("tr").each(function(){
		    if($(this).attr("id") == id){
		        $(this).remove();
		        return;
		    }
		});
	}else{
		document.getElementById("addRoutingGateway").disabled = false;
		document.getElementById("addRoutingGateway").setAttribute("class", "light-btn");
		document.getElementById("addRoutingGateway").focus();
	}
}

var isValidName;

function verifyFormat (){
	var searchName = document.getElementById("name").value;
	isValidName = callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.ROUTING_TABLE%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
}
function verifyName() {
	var searchName = document.getElementById("name").value;
	isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.ROUTING_TABLE%>',searchName:searchName,mode:'create',id:''},'verifyNameDiv');
	
}

var isValidName;
function validate(){
	verifyName();
	var flag = false;
	if(isNull(document.forms[0].name.value)){
		alert('Please provide Routing Entry name');
		document.forms[0].name.focus();
	}else if(!isValidName) {
		alert('Please Enter Valid Routing Entry Name');
		document.forms[0].name.focus();
	}else if(validGatewayEntry()){
       alert('Please provide the destination gateway entry or select Routing Action type as Local');
	}else if(duplicateGateway()) {
		alert('Error!!!\nSame destination gateway used multiple time');
	}else if(document.forms[0].type.value=='0'){
		alert('Please provide the routing type value');
	}else  if(document.forms[0].action.value == 'Select'){	 
		 alert('Please select Routing Action');
	}else if(!validateMCCMNCGroup()){
		alert('Please select MCC-MNC Group');
	}else{   		
		flag = true;
	}
	return flag;
}
</script> 	

<html:form action="/routingTableManagement.do?method=create"  onsubmit="return validate();" > 
	
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp" %>
  <tr>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0" > 
	 	  <tr> 
			<td class="table-header" colspan="2">
	        <bean:message  bundle="routingMgmtResources" key="routingtable.create"/>
			</td>
		  </tr>
          <tr> 
				<td align="left" class="labeltext" valign="top" colspan="2">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="2"> 
			   <table cellpadding="0" cellspacing="0" border="0" width="97%" style="padding-left: 1.5em" > 				   	 	
			   	  <tr> 
					<td align="left" class="labeltext" valign="top" width="10%">
						<bean:message bundle="routingMgmtResources" key="routingtable.name"/>
					</td>  
					<sm:nvNameField maxLength="40" size="30"/>
				 
				  </tr>
				   <tr> 
						<td align="left" class="labeltext" valign="top" width="10%">
							<bean:message bundle="routingMgmtResources" key="routingtable.type" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="routingtable.type"/>','<bean:message bundle="routingMgmtResources" key="routingtable.type" />')"/>
						</td>  
						<td align="left" class="labeltext" valign="top" width="32%" > 
							<html:select tabindex="2" property="type" styleId="type" onchange="setRealmCondition();" style="width: 210px">
								<html:option value="MCCMNC">MCC-MNC-BASED</html:option>
								<html:option value="REALM">CUSTOM-REALM-BASED</html:option>							
							</html:select>
						</td>	
					 </tr>
				   <tr id="mccmncGroupSelect"> 
						<td  align="left" class="labeltext" valign="top" width="10%" >
							<bean:message bundle="routingMgmtResources" key="routingtable.mccmncgroup" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="routingtable.mccmncgroup"/>','<bean:message bundle="routingMgmtResources" key="routingtable.mccmncgroup" />')"/>
						</td> 
															
						<td align="left" class="labeltext"  valign="top"  width="32%"> 
							<html:select name="routingTableManagementForm" styleId="mccmncGroupId" tabindex="3" property="mccmncGroupId"  style="width: 210px;" onchange="setRealmCondition();">
								<logic:iterate id="mccmncgroup" name="routingTableManagementForm" property="mccmncGroupDataList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData">
										<html:option value="<%=Long.toString(mccmncgroup.getMccmncGroupId())%>"><bean:write name="mccmncgroup" property="name"/></html:option>
									</logic:iterate>
								</html:select>	      
						</td>
				   </tr>
				   <tr id="defineRealmCondition"> 
						<td  align="left" class="labeltext" valign="top"  >
							<bean:message bundle="routingMgmtResources" key="routingtable.realmcondtion"/>
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="routingtable.realmcondtion"/>','<bean:message bundle="routingMgmtResources" key="routingtable.realmcondtion" />')"/>
						</td> 
						<td  align="left" class="labeltext" valign="top"  > 
							<html:textarea property="realmcondition" cols="25" rows="3"   styleId="realmcondition"   tabindex="4" /> 
							<input type="button" id="validateBtn" value="validate" class="light-btn" tabindex="6" onclick="validateCondition('realmcondition');" style="vertical-align: top;margin-top: 20px">
						</td> 
				  </tr>	
				   <tr class="labeltext" >
					<td  align="right" valign="top" colspan="1"></td>
					<td  align="left" valign="top" colspan="2" name="validCond" class="small-text-grey" id="validCond"></td>
				 </tr>
				   <tr> 
						<td align="left" class="labeltext" valign="top" width="10%">
							<bean:message bundle="routingMgmtResources" key="routingtable.action" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="routingtable.action"/>','<bean:message bundle="routingMgmtResources" key="routingtable.action" />')"/>
						</td> 
															
						<td align="left" class="labeltext" valign="top" width="20%" > 
							<html:select name="routingTableManagementForm" styleId="action" tabindex="5" property="action" size="1" onchange="disableAddRoutingGateway();" style   ="width: 210px;">
								<html:option value="0">LOCAL</html:option>
								<html:option value="2">PROXY</html:option>
							</html:select>	      
						</td>
				   </tr>
				   <tr><td>&nbsp;</td></tr>
				   </table> 
				   <tr>
						<td class="tblheader-bold" height="20%" colspan="4" style="padding-left: 2.0em"  >Destination Gateway</td>
					</tr>
					<tr><td>&nbsp;</td></tr> 
			</td> 
		    </tr>
			<tr>
				<td class="captiontext" colspan="3"><input type="button" id="addRoutingGateway" value="  Add  " class="light-btn" onclick="" tabindex="6" /></td>
			</tr>
				<td colspan="3" >
						<table class="captiontext"   id="gatewayMappingTable" border="0"
							cellpadding="0" cellspacing="0">
							<tr>
								<td align="left" class="tblheader" valign="top"  style="width: 250px; "><bean:message
										bundle="routingMgmtResources" key="routingtable.gateway.name" /></td>
								<td align="left" class="tblheader" valign="top"  style="width: 150px; "><bean:message
										bundle="routingMgmtResources" key="routingtable.gateway.weightage" /></td>
								<td align="center" class="tblheader" valign="top" style="width: 100px; ">Remove</td>
							</tr>
						</table>
				</td>
		   <tr> 
				<td align="center" class="labeltext" valign="top" colspan="3" style="width: 584px; ">&nbsp;</td> 
		  </tr> 
			   	 <tr align="center">
				  		<td align="center" valign="top" ></td>
						<td>
						<html:submit styleClass="light-btn" tabindex="8"  value="  Create  "  />
						<input type="button" value=" Cancel " tabindex="9" class="light-btn" onclick="javascript:location.href='<%=basePath%>/routingTableManagement.do?method=initSearch'"/>
						</td>
				 </tr>
	    <td width="10" class="small-gap">&nbsp;</td> 
    	<td class="small-gap" colspan="2">&nbsp;</td> 
  </tr>  
</tr>	
</table>		
<%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table>
</html:form>

