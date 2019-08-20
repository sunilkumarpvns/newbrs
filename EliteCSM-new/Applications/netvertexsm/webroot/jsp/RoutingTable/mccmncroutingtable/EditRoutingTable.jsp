<%@ page import="java.util.List" %>
<%@ page import="com.elitecore.netvertexsm.web.RoutingTable.mccmncroutingtable.form.RoutingTableManagementForm" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData" %>
<%@page import="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableGatewayRelData"%>
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
	$(document).ready(function(){	
		verifyName();
		document.forms[0].name.focus();
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
			$("#gatewayMappingTable").append("<tr><td class='tblfirstcol' ><select Id='gatewayId' tabindex='9' style='width:100%;' class='noborder' name='gatewayId'>+'<%=optionStr%>'+</select></td>"
			          +"<td class='tblfirstcol' ><select Id='weightage' tabindex='9' style='width:99%;' class='noborder' name='weightage'>+'<%=optinsWeightage%>'+</select></td>"
			          +"<td class='tblfirstcol' align='center' ><img src='<%=basePath%>/images/minus.jpg' class='delete' onclick='$(this).parent().parent().remove();'  height='15' tabindex='9' /></td></tr>");

		});			
	});  
	
	function disableAddRoutingGateway(){
		
		var type=document.getElementById("action").value;
		if(type=='0'){
			document.getElementById("addRoutingGateway").disabled = true;
			document.getElementById("addRoutingGateway").setAttribute("class", "light-btn-disabled");
		}else{
			document.getElementById("addRoutingGateway").disabled = false;
			document.getElementById("addRoutingGateway").setAttribute("class", "light-btn");
	}
	}
	
	var isValidName;
	
	function verifyFormat (){
		var searchName = document.getElementById("name").value;
		isValidName = callVerifyValidFormat({instanceType:'<%=InstanceTypeConstants.ROUTING_TABLE%>',searchName:searchName,mode:'update',id:'<%=form.getRoutingTableId()%>'},'verifyNameDiv');
	}
	
	function verifyName() {
		var searchName = document.getElementById("name").value;
		isValidName = verifyInstanceName({instanceType:'<%=InstanceTypeConstants.ROUTING_TABLE%>',searchName:searchName,mode:'update',id:'<%=form.getRoutingTableId()%>'},'verifyNameDiv');
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
	
	function validate(){
		verifyName();
		var flag = false;
	 	if(isNull(document.forms[0].name.value)){
			alert('Please provide Routing Entry name');
			document.forms[0].name.focus();
		}else if(validGatewayEntry()){
		    alert('Please provide the destination gateway entry or select Routing Action type as Local');		       
		}else if(duplicateGateway()) {
			alert('Error!!!\nSame destination gateway used multiple time');			
		}else if(!isValidName) {
			alert('Please Enter Valid Routing Entry Name');
			document.forms[0].name.focus();			
		}else{   			
			flag = true;
		}
	 	return flag;
}
	
</script> 	

<html:form action="/routingTableManagement.do?method=update" onsubmit="return validate();"  > 
<html:hidden name="routingTableManagementForm" property="routingTableId"/>
<html:hidden name="routingTableManagementForm" property="type"/>
	
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
  <tr> 
    <td colspan="2" valign="top" align="right"> 
      <table cellpadding="0" cellspacing="0" border="0" width="97%">
					<tr  >
						<td class="tblheader-bold" valign="top"  colspan="3"><bean:message bundle="routingMgmtResources" key="routingtable.update" /></td>
					</tr>
					<tr>
						<td align="left" class="labeltext" valign="top" width="15%">
							<bean:message bundle="routingMgmtResources"	key="routingtable.name" />

						</td>
						<sm:nvNameField maxLength="40" size="30" value="${routingTableManagementForm.name}" />
					</tr>
				    <logic:equal name="routingTableManagementForm" property="type" value="MCCMNC">
				   <tr> 
						<td align="left" class="labeltext" valign="top" width="15%" >
							<bean:message bundle="routingMgmtResources" key="routingtable.mccmncgroup" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="routingtable.mccmncgroup"/>','<bean:message bundle="routingMgmtResources" key="routingtable.mccmncgroup" />')"/>
						</td> 
															
						<td align="left" class="labeltext"  valign="top" width="32%"> 
							<html:select name="routingTableManagementForm" styleId="mccmncGroupId" tabindex="3" property="mccmncGroupId" size="1" style="width: 220px;">
								<logic:iterate id="mccmncgroup" name="routingTableManagementForm" property="mccmncGroupDataList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData">
										<html:option value="<%=Long.toString(mccmncgroup.getMccmncGroupId())%>"><bean:write name="mccmncgroup" property="name"/></html:option>
									</logic:iterate>
								</html:select>	      
						</td>
				   </tr>
				   </logic:equal>
				   <logic:equal name="routingTableManagementForm" property="type" value="REALM">
				   <tr> 
					<td align="left" class="labeltext" valign="top" width="15%">
						<bean:message bundle="routingMgmtResources" key="routingtable.realmcondtion"/>
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="routingtable.realmcondtion"/>','<bean:message bundle="routingMgmtResources" key="routingtable.realmcondtion" />')"/>
						
					</td> 
					<td align="left" class="labeltext" valign="top" width="32%" > 
						<html:textarea property="realmcondition" cols="40" rows="3" styleId="realmcondition" tabindex="4" />
						<input type="button" value="validate" class="light-btn" tabindex="6" id="validateBtn" onclick="validateCondition('realmcondition');" style="vertical-align: top;margin-top: 20px"> 
					</td>
				  </tr>	
				  <tr class="labeltext" >
					<td  align="right" valign="top" colspan="1"></td>
					<td  align="left" valign="top" colspan="2" name="validCond" class="small-text-grey" id="validCond"></td>
				 </tr>
				  </logic:equal> 
				   <tr> 
						<td align="left" class="labeltext" valign="top" width="15%" >
							<bean:message bundle="routingMgmtResources" key="routingtable.action" />
							<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="routingtable.action"/>','<bean:message bundle="routingMgmtResources" key="routingtable.action" />')"/>
						</td> 
						<td align="left" class="labeltext" valign="top" width="32%"> 
							<html:select name="routingTableManagementForm" styleId="action" tabindex="5" property="action" size="1" onchange="disableAddRoutingGateway();"   style="width: 220px;">
								<html:option value="0">LOCAL</html:option>
								<html:option value="2">PROXY</html:option>
							</html:select>	      
						</td>
				   </tr>
				   <tr><td>&nbsp;</td></tr>
				  	<tr>
						<td class="tblheader-bold" height="20%" colspan="3">Add Destination Gateway</td>
					</tr>
					<tr><td  colspan="2">&nbsp;
						<table  id="gatewayMappingTable" border="0"
							cellpadding="0" cellspacing="0"  >
							<tr>
								<td  align="left" ><input type="button" id="addRoutingGateway" value="  Add  " onClick="" class="light-btn"   tabindex="6"/>	</td>
							</tr>
							<tr>
								<td align="left" class="tblheaderfirstcol" valign="top" width="200px"><bean:message
										bundle="routingMgmtResources" key="routingtable.gateway.name" /></td>
								<td align="left" class="tblheader" valign="top" width="140px" ><bean:message
										bundle="routingMgmtResources" key="routingtable.gateway.weightage" /></td>
								<td align="left" class="tblheaderlastcol" valign="top" width="75px" >Remove</td>
							</tr>
							<logic:iterate id="gateway" name="routingTableManagementForm"  property="routingTableGatewayRelData" type="com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableGatewayRelData">
									<tr>
										<td align="left" class="tblfirstcol">
											<html:select styleClass="noborder" name="gateway" property="gatewayId" tabindex="7"  style="width: 100%;">
										 		<logic:iterate id="gtway" name="routingTableManagementForm" property="gatewayList" type="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData">
													<html:option value="<%=Long.toString(gtway.getGatewayId())%>"><bean:write name="gtway" property="gatewayName"/></html:option>
												</logic:iterate>
											</html:select>
										</td>
										<td align="left" class="tblrows">
												<html:select styleClass="noborder" name="gateway" property="weightage" tabindex="7"  style="width: 100%;">
												<%for(int i=0;i<=10;i++){%>
												<html:option value="<%=Integer.toString(i)%>"><%=i%></html:option>
												<%}%>
												</html:select> 									
										</td>
										<td class='tblrows' align='center' ><img src='<%=basePath%>/images/minus.jpg' class='delete'  height='15' onclick="removeMe()" tabindex="8" /></td>
									</tr>
							</logic:iterate>
						</table>
					</td></tr>
				  </table> 
    	</td> 
 	 	 <tr> 
				<td align="center" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
			   	 <tr align="center">
				  		<td align="center" valign="top" ></td>
						<td>							
						<html:submit styleClass="light-btn" tabindex="10"  value="  Update  "  />					
						<input type="button" value=" Cancel " tabindex="11" class="light-btn" onclick="javascript:location.href='<%=basePath%>/routingTableManagement.do?method=initSearch'"/></td>
				 </tr>
	    <td width="10" class="small-gap">&nbsp;</td> 
    <td class="small-gap" colspan="2">&nbsp;</td> 
  </tr>  
</tr>
</table>
</html:form>

<%@include file="/jsp/core/includes/common/Footer.jsp" %>