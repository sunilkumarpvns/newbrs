
<%@ page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants" %>
<%@ page import="com.elitecore.netvertexsm.web.locationconfig.area.form.AreaMgmtForm" %>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.locationconfig.city.data.CityData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData" %>

<%
	String localPath 	= request.getContextPath();
	areaMgmtForm 		= (AreaMgmtForm)request.getAttribute("areaMgmtForm");
	
	List<RegionData> 	  regionDataList 	 = areaMgmtForm.getListRegionData();
	List<CityData> 		  cityDataList 		 = areaMgmtForm.getListCityData();
	List<NetworkData> listNetworkData 	 = areaMgmtForm.getListNetworkData();
	
	long nwID = 0;
	if(areaMgmtForm.getNetworkId()!=null){
		nwID = areaMgmtForm.getNetworkId();
	}
%>

<script type="text/javascript">	
	$(document).ready(function(){
		verifyName();
		loadStatesData();
		loadCitiesData();
		loadNetworksData();
		
		$("#area").focus();
		$('table td img.delete').on('click',function() {
			$(this).parent().parent().remove(); 
		});
		$('#addLac').click(function() {
			var orderNumArray = document.getElementsByName("orderNumber");
			var currentOrderNumber=1;
			if(orderNumArray!=null && orderNumArray.length>0){
				for(var i=0; i<orderNumArray.length; i++){
					currentOrderNumber = orderNumArray[i].value;	
				}
				currentOrderNumber++;
			}
			var tableRowStr = '<tr id="lacData">'+
									'<td align="center" class="tblfirstcol" valign="middle" width="15%"><input type="text" size="6" maxlength="6" tabindex="10" name="lacCode" value=""/></td>'+
									'<td align="center" class="tblrows" valign="middle" width="25%"><textarea cols="18" rows="2" name="CI" tabindex="10" maxlength="4000" ></textarea></td>'+									
									'<td align="center" class="tblrows" valign="middle" width="25%"><textarea cols="18" rows="2" name="SAC" tabindex="10" maxlength="4000" ></textarea></td>'+
									'<td align="center" class="tblrows" valign="middle" width="25%"><textarea cols="18" rows="2" name="RAC" tabindex="10"  maxlength="4000" ></textarea></td>'+									
									'<td class="tblrows" align="center" colspan="3" width="5%"><img value="top" tabindex="10" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 0px; padding-top: 5px;" height="14" onclick="$(this).parent().parent().remove();"/></td>'+
							'</tr>';		
		   $(tableRowStr).appendTo('#3GPPLacInformationTable');
		});
		
		if($("#networkId").val()==0){
			document.getElementById("addLac").disabled=true;
			document.getElementById("addLac").className="light-btn-disabled";
			var id = "lacData";
			$("tr").each(function(){
			    if($(this).attr("id") == id){
			        $(this).remove();
			        return;
			    }
			});
		}else{
			document.getElementById("addLac").disabled=false;			
			document.getElementById("addLac").className="light-btn";
		}		
		
	});
	

	function onNetworkChange(){
		if($("#networkId").val()==0){
			document.getElementById("addLac").disabled=true;
			document.getElementById("addLac").className="light-btn-disabled";
			var id = "lacData";			
			/* 			  	
 			$("tr").each(function(){
			    if($(this).attr("id") == id){			    	
			        $(this).remove();
			        return;
			    }
			}); 
			*/
		}else{
			document.getElementById("addLac").disabled=false;
			document.getElementById("addLac").focus();
			document.getElementById("addLac").className="light-btn";
		}
	}
	
	function onCountryChange(){
		var countryIdJS = document.getElementById("countryId").value;
		var regionSelect = document.getElementById('regionId');
		regionSelect.options.length = 0;
		regionSelect.options[0] = new Option ("--Select--","0");	
		regionSelect.options[0].selected="true";	
		var index = 1;
		<%for(RegionData regionData:regionDataList){%>
				if(countryIdJS==<%=regionData.getCountryId()%>){				
					regionSelect.options[index] = new Option ('<%=regionData.getRegionName()%>','<%=regionData.getRegionId()%>');
					index++;
				}			
		<%}%>	
		setNetworkData(countryIdJS);
		setCitySelectEmpty();
		onNetworkChange();
		onStateChange();
				
	}
	function setCitySelectEmpty(){
		var citySelectOp = document.getElementById('cityId');
		citySelectOp.options.length = 0;
		citySelectOp.options[0] = new Option ("--Select--","0");	
		citySelectOp.options[0].selected="true"
	}

	function setNetworkData(countryIdJS){
		
		var networkSelect = document.getElementById('networkId');
		networkSelect.options.length = 0;
		networkSelect.options[0] = new Option ("--Select--","0");	
		networkSelect.options[0].selected="true";	
		var index = 1;
		<%for(NetworkData networkData:listNetworkData){%>
				if(countryIdJS==<%=networkData.getCountryID()%>){				
					networkSelect.options[index] = new Option ('<%=networkData.getNetworkName()%>','<%=networkData.getNetworkID()%>');
					index++;
				}			
		<%}%>		
	}

	function onStateChange(){
		var regionIdJS = document.getElementById("regionId").value;
		var citySelect = document.getElementById('cityId');
		citySelect.options.length = 0;
		citySelect.options[0] = new Option ("--Select--","0");	
		citySelect.options[0].selected="true";	
		var index = 1;
		<%for(CityData cityData:cityDataList){%>
				if(regionIdJS==<%=cityData.getRegionId()%>){				
					citySelect.options[index] = new Option('<%=cityData.getCityName()%>','<%=cityData.getCityId()%>');
					index++;
				}			
		<%}%>			
	}
	
	
	function loadStatesData(){
		var countryIdJS = document.getElementById("countryId").value;
		var regionSelect = document.getElementById('regionId');
		regionSelect.options.length = 0;
		regionSelect.options[0] = new Option ("--Select--","0");	
		//regionSelect.options[0].selected="true";	
		var index = 1;
		<%for(RegionData regionData:regionDataList){%>
				if(countryIdJS==<%=regionData.getCountryId()%>){				
					regionSelect.options[index] = new Option ('<%=regionData.getRegionName()%>','<%=regionData.getRegionId()%>');												
					<%if(areaMgmtForm.getRegionId() == regionData.getRegionId()){%>
							regionSelect.options[index].selected="true";						
					<%}%>		
					index++;
				}			
		<%}%>	
	}
	
	
	function loadCitiesData(){
		var regionIdJS = document.getElementById("regionId").value;
		var citySelect = document.getElementById('cityId');
		citySelect.options.length = 0;
		citySelect.options[0] = new Option ("--Select--","0");	
		//citySelect.options[0].selected="true";	
		var index = 1;
		<%for(CityData cityData:cityDataList){%>
				if(regionIdJS==<%=cityData.getRegionId()%>){				
					citySelect.options[index] = new Option('<%=cityData.getCityName()%>','<%=cityData.getCityId()%>');
					<%if(areaMgmtForm.getCityId() == cityData.getCityId()){%>
						citySelect.options[index].selected="true";						
					<%}%>	
					index++;
				}			
		<%}%>			
	}	
	
	function loadNetworksData(){
		var countryIdJS = document.getElementById("countryId").value;				
		var networkSelect = document.getElementById('networkId');
		networkSelect.options.length = 0;
		networkSelect.options[0] = new Option ("--Select--","0");	
		//networkSelect.options[0].selected="true";	
		var index = 1;		
		<%for(NetworkData networkData:listNetworkData){%>
				if(countryIdJS==<%=networkData.getCountryID()%>){				
					networkSelect.options[index] = new Option ('<%=networkData.getNetworkName()%>','<%=networkData.getNetworkID()%>');
					<%if(nwID == networkData.getNetworkID()){%>					
						networkSelect.options[index].selected="true";						
					<%}%>					
					index++;
				}			
		<%}%>		
	}
	
	var isValidName;
	function validate(){
		var ci_sac_rac_pattern = /^\d*[0-9]([\;|\,]\d*[0-9])*(\,|\;)?$/;
		//OLD WORKING :	var ci_sac_rac_pattern = /^\d*[0-9]([\;|\,]\d*[0-9])*?$/;
		var lacRowFlag = true;
		var lacArray = document.getElementsByName("lacCode");
		var ciArray	 = document.getElementsByName("CI");
		var sacArray = document.getElementsByName("SAC");
		var racArray = document.getElementsByName("RAC");	
		
			if(isNull(document.forms[0].area.value)){
				alert("Area Name must be specified.");
				document.forms[0].area.focus();
				return;
			}else if(!isValidName) {
				alert('Enter Valid Area Name.');
				document.forms[0].area.focus();
				return;
			}else if(document.forms[0].countryId.value=='0') {
				alert('Please select Country.');
				document.forms[0].countryId.focus();
				return;
			}else if(document.forms[0].regionId.value=='0') {
				alert('Please select State.');
				document.forms[0].regionId.focus();
				return;
			}else if(document.forms[0].cityId.value=='0') {
				alert('Please select City.');
				document.forms[0].cityId.focus();
				return;
			}else if(lacArray.length>0){
				for(var i=0; i<ciArray.length; i++){
					if(lacArray[i].value.trim().length==0){
						lacArray[i].focus();
						alert("Please provide LAC's data.");
						return;
					}
					if(!isNull(ciArray[i].value) && !ci_sac_rac_pattern.test(ciArray[i].value)){
						alert("Invalid CIs value.");
						return;
					}
					if(!isNull(sacArray[i].value) && !ci_sac_rac_pattern.test(sacArray[i].value)){
						alert("Invalid SACs value.");
						return;
					}
					if(!isNull(racArray[i].value) &&  !ci_sac_rac_pattern.test(racArray[i].value)){
						alert("Invalid RACs value.");
						return;
					}												
					
					if(ciArray[i].value!=null && ciArray[i].value.trim().length>0 && lacArray[i].value.trim().length==0){
						lacArray[i].value='';																					
						alert("Please input LACs value.");
						lacRowFlag = false;
					}
					if(sacArray[i].value!=null && sacArray[i].value.trim().length>0 && ciArray[i].value.trim().length==0){
						ciArray[i].value='';
						alert("Please input CIs value.");
						lacRowFlag = false;
					}
					if(racArray[i].value!=null && racArray[i].value.trim().length>0 && sacArray[i].value.trim().length==0){
						sacArray[i].value='';
						alert("Please input SACs value.");
						lacRowFlag = false;
					}								
				}
				if(lacRowFlag == true){
					document.forms[0].submit();	
				}
			}else{			
				document.forms[0].submit();
			}
	}

	function verifyFormat (){
		var searchName = document.getElementById("area").value;
		callVerifyValidFormat({instanceType:<%=InstanceTypeConstants.AREA_MASTER%>,isSpaceAllowed:"yes",parentId:cityId,searchName:searchName,mode:'create',id:''},'verifyNameDiv');
	}
	function verifyName() {
		var cityId 		= document.getElementById("cityId").value;
		var searchName 	= document.getElementById("area").value;
		if(jQuery.trim(searchName).length>0){
			isValidName = verifyInstanceName({instanceType:<%=InstanceTypeConstants.AREA_MASTER%>,isSpaceAllowed:"yes",parentId:cityId,searchName:searchName,mode:'update',id:'<%=areaMgmtForm.getAreaId()%>'},'verifyNameDiv');
			if(isValidName==true){
				$("#verifyNameDiv").text('');
			}
		}
	}
	
	$(document).ready(function() {	

	 	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.AREA_MASTER%>,propertyName:"param1"}, function(data){
	 		dbFieldStr = data.substring(1,data.length-3);
	 		var dbFieldArray = new Array();
	 		dbFieldArray = dbFieldStr.split(", ");
	 	 	var paramArray = new Array(); 	 	
	 		for(var i=0; i<dbFieldArray.length; i++){
	 			if(jQuery.trim(dbFieldArray[i]).length>0 && dbFieldArray[i]!='null'){ 				
	 				paramArray+=dbFieldArray[i]; 				
	 				if((i+1)<=dbFieldArray.length && dbFieldArray[i+1]!=null){
	 					paramArray+=",";
	 				}
	 			}
	 		}
	 		
	 		paramArray = paramArray.split(",");	 			 		
	 		$("#param1").autocomplete(paramArray, {
	 			minChars: 0,
	 			max: 100
	 		});
	 		return dbFieldArray;
	 	});	
	 	
	 	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.AREA_MASTER%>,propertyName:"param2"}, function(data){
	 		dbFieldStr = data.substring(1,data.length-3);
	 		var dbFieldArray = new Array();
	 		dbFieldArray = dbFieldStr.split(", ");
	 		var paramArray = new Array();
	 		for(var i=0; i<dbFieldArray.length; i++){
	 			if(jQuery.trim(dbFieldArray[i]).length>0 && dbFieldArray[i]!='null'){
	 				paramArray+=dbFieldArray[i]; 				
	 				if((i+1)<=dbFieldArray.length && dbFieldArray[i+1]!=null){
	 					paramArray+=",";
	 				}
	 			}
	 		} 
	 		
	 		paramArray = paramArray.split(",");
	 		$("#param2").autocomplete(paramArray, {
	 			minChars: 0,
	 			max: 100
	 		});
	 		return dbFieldArray;
	 	});	
	 	
	 	$.post("ListRetrivalServlet", {instanceType:<%=InstanceTypeConstants.AREA_MASTER%>,propertyName:"param3"}, function(data){
	 		dbFieldStr = data.substring(1,data.length-3);
	 		var dbFieldArray = new Array();
	 		dbFieldArray = dbFieldStr.split(", ");
	 		var paramArray = new Array();
	 		for(var i=0; i<dbFieldArray.length; i++){
	 			if(jQuery.trim(dbFieldArray[i]).length>0 && dbFieldArray[i]!='null'){
	 				paramArray+=dbFieldArray[i]; 				
	 				if((i+1)<=dbFieldArray.length && dbFieldArray[i+1]!=null){
	 					paramArray+=",";
	 				}
	 			}
	 		} 
	 		paramArray = paramArray.split(",");
	 		$("#param3").autocomplete(paramArray, {
	 			minChars: 0,
	 			max: 100
	 		});
	 		return dbFieldArray;
	 	});	
});	
	
</script> 	

<html:form action="/areaManagement.do?method=update"> 
	<html:hidden name="areaMgmtForm" property="areaId" />
	<input type="hidden" name="wifissidinfoId" id="wifissidinfoId" value='<bean:write name="areaBean" property="wifiCallingStationInfoData.wifissidinfo_id"/>' />
	<input type="hidden" name="csId" id="csId"  value='<bean:write name="areaBean" property="callingStationInfoData.csId"/>' /> 
<table cellSpacing="0" cellPadding="0" width="97%" border="0" align="right">  
	<tr> 
		<td class="tblheader-bold" colspan="3">
			<bean:message  bundle="locationMasterResources" key="area.management.title"/>
		</td>
	</tr> 
	  <tr> 
		<td colspan="3"> 
		   <table width="100%"  align="right" border="0" > 	
		   	  <tr> 
					<td align="left" class="captiontext" valign="top"  width="25%" >
						<bean:message bundle="locationMasterResources" key="area.name" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.name"/>','<bean:message bundle="locationMasterResources" key="area.name" />')"/>
					</td> 
														
					<sm:nvNameField maxLength="64" size="20" value="${areaMgmtForm.area }" name="area" id="area"/>
			   </tr>				  
	
		   	  <tr> 
					<td align="left" class="captiontext" valign="top"  width="25%" >
						<bean:message bundle="locationMasterResources" key="city.country.name" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.country"/>','<bean:message bundle="locationMasterResources" key="city.country.name" />')"/>
					</td> 
														
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="areaMgmtForm" styleId="countryId" tabindex="2" property="countryId"  size="1" style="width: 220px;" onchange="onCountryChange();" >
									  <html:option value="0">--Select--</html:option>
									   <logic:iterate id="country" name="areaMgmtForm" property="listCountryData" type="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData">
										<html:option value="<%=Long.toString(country.getCountryID())%>"><bean:write name="country" property="name"/></html:option>
									</logic:iterate> 
						</html:select><font color="#FF0000"> *</font> 	      
					</td>
			   </tr>	

		   	  <tr> 
					<td align="left" class="captiontext" valign="top"  width="25%" >
						<bean:message bundle="locationMasterResources" key="area.region" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.region"/>','<bean:message bundle="locationMasterResources" key="area.region" />')"/>
					</td> 
														
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="areaMgmtForm" styleId="regionId" tabindex="3" property="regionId"  size="1" style="width: 220px;" onchange="onStateChange();" >
									  <html:option value="0">--Select--</html:option>
						</html:select><font color="#FF0000"> *</font> 	      
					</td>
			   </tr>	
		   	  <tr> 
					<td align="left" class="captiontext" valign="top"  width="25%" >
						<bean:message bundle="locationMasterResources" key="city.title" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.city"/>','<bean:message bundle="locationMasterResources" key="city.title" />')"/>
					</td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="areaMgmtForm" styleId="cityId" tabindex="4" property="cityId"  size="1" style="width: 220px;">
									  <html:option value="0">--Select--</html:option>
						</html:select>&nbsp;<font color="#FF0000">*</font> 	      
					</td>
			   </tr>
			   	  <tr> 
					<td align="left" class="captiontext" valign="top" width="25%"><bean:message bundle="locationMasterResources" key="area.param1" />&nbsp;<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.param1"/>','<bean:message bundle="locationMasterResources" key="area.param1" />')"/></td> 
					<td align="left" class="labeltext" valign="top" width="" > 
						<html:text name="areaMgmtForm" styleId="param1" property="param1"  onkeyup="" maxlength="64" size="30" tabindex="5" />&nbsp;						
					</td> 
				  </tr>
			   	  <tr> 
					<td align="left" class="captiontext" valign="top" width="25%"><bean:message bundle="locationMasterResources" key="area.param2" />&nbsp;<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.param2"/>','<bean:message bundle="locationMasterResources" key="area.param1" />')"/></td> 
					<td align="left" class="labeltext" valign="top" width="" > 
						<html:text name="areaMgmtForm" styleId="param2" property="param2"  onkeyup="" maxlength="64" size="30" tabindex="6" />&nbsp;						
					</td> 
				  </tr>
			   	  <tr> 
					<td align="left" class="captiontext" valign="top" width="25%"><bean:message bundle="locationMasterResources" key="area.param3" />&nbsp;<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.param3"/>','<bean:message bundle="locationMasterResources" key="area.param1" />')"/></td> 
					<td align="left" class="labeltext" valign="top" width="" > 
						<html:text name="areaMgmtForm" styleId="param3" property="param3"  onkeyup="" maxlength="64" size="30" tabindex="7" />&nbsp;						
					</td> 
				  </tr>			   
		   	  <tr> 
					<td align="left" class="captiontext" valign="top"  width="25%" >
						<bean:message bundle="locationMasterResources" key="area.network" />
						<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.network"/>','<bean:message bundle="locationMasterResources" key="area.network" />')"/>						
					</td> 
														
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="areaMgmtForm"  styleId="networkId" tabindex="8" property="networkId"  size="1" style="width: 220px;" onchange="onNetworkChange()" >
									<html:option value="0">--Select--</html:option>
<%-- 									   <logic:iterate id="network" name="areaMgmtForm" property="listNetworkData" type="com.elitecore.netvertexsm.datamanager.RoutingTable.MCC_MNC.data.MCCMNCCodesData">
											<html:option value="<%=Long.toString(network.getMccMNCID())%>"><bean:write name="network" property="networkName"/></html:option>
									   </logic:iterate>  --%>
						</html:select><font color="#FF0000"></font> 	      
					</td>
			   </tr>
				<tr>
					<td class="tblheader-bold"  valign="top" colspan="2"  ><bean:message  bundle="locationMasterResources" key="area.3gpp.lcac.info"/> </td>
				</tr>			  			  			  					  			  			  					  			  			  					  			  

				<tr>
					<td colspan="2"  >					
					 <tr id='3GPPLocationAttributes'> 
					  	<td valign="middle" class="captiontext" colspan="3">  
							<table cellpadding="0" id="3GPPLacInformationTable" cellspacing="0" border="0" width="90%" class="">
								<tr> 
			          				<td>
			            				<input type="button" id="addLac" value="Add LACs" tabindex="9" class="light-btn" onclick="">
			            			</td>
			          			</tr>					
								<tr class="L">
									<td align="center" class="tblheaderfirstcol" valign="top" width="15%">LAC</td>
									<td align="center" class="tblheader" valign="top" width="25%">CIs</td>
									<td align="center" class="tblheader" valign="top" width="25%">SACs</td>
									<td align="center" class="tblheader" valign="top" width="25%">RACs</td>
									<td align="center" class="tblheaderlastcol" valign="top" width="5%">Remove</td>
								</tr>
								<logic:iterate  id="lacDataBean" name="areaBean" property="lacDataSet" type="com.elitecore.netvertexsm.datamanager.locationconfig.area.data.LacData" >
									<input type="hidden" name="lacIdALl" id="lacIdALl" value="<bean:write name="lacDataBean" property="lacId"/>" />
								</logic:iterate>
								<logic:iterate  id="lacDataBean" name="areaBean" property="lacDataSet" type="com.elitecore.netvertexsm.datamanager.locationconfig.area.data.LacData" >
								<tr id = "lacData">
									<input type="hidden" name="lacId" id="lacId"  value="<bean:write name="lacDataBean" property="lacId"/>" />
									<td align="center" class="tblfirstcol" valign="top" width="15%"><input type="text" size="10" name="lacCode"  tabindex="8"  value='<bean:write name="lacDataBean" property="lacCode"/>'/></td>
									<td align="center" class="tblrows" valign="top" width="25%"><textarea cols="18" rows="2" name="CI" tabindex="8" ><bean:write name="lacDataBean" property="strCellIds"/></textarea></td>
									<td align="center" class="tblrows" valign="top" width="25%"><textarea cols="18" rows="2" name="SAC" tabindex="8" ><bean:write name="lacDataBean" property="strSacs"/></textarea></td>									
									<td align="center" class="tblrows" valign="top" width="25%"><textarea cols="18" rows="2" name="RAC" tabindex="8" ><bean:write name="lacDataBean" property="strRacs"/></textarea></td>									
									<td class="tblrows" align="center" colspan="3" width="5%"><img value="top" tabindex="8" src="<%=basePath%>/images/minus.jpg" class="delete" style="padding-right: 0px; padding-top: 5px;" height="14" /></td>
								</tr>
								</logic:iterate>								
							</table>
						</td> 
					 </tr>
					</td>
				</tr>
	 			<tr>
					<td class="tblheader-bold"  valign="top" colspan="2"> <bean:message  bundle="locationMasterResources" key="area.wifi.ap.info"/> </td>
				</tr>					
			  	<tr id='WifiAPsInformation'> 
				  	<td valign="middle" colspan="3">  
						<table cellpadding="0" id="WiFiAPsInformationTable" cellspacing="0" border="0" width="80%">
							<tr>
								<td align="left"  class="captiontext" valign="middle" width="24%">
									<bean:message bundle="locationMasterResources" key="area.called.staion.ids" />
									<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.called.staion.ids"/>','<bean:message bundle="locationMasterResources" key="area.called.staion.ids" />')"/>
								</td>
								<td align="center" class="allborderwithtop" valign="top" width="45%"><textarea  cols="40" rows="2"  maxlength="256" name="strCallingStationIds" tabindex="11" ><bean:write name="areaBean" property="callingStationInfoData.callingStaionIds"/></textarea></td>
							</tr>
							<tr>
								<td align="left" class="captiontext" valign="middle" width="24%">
									<bean:message bundle="locationMasterResources" key="area.wifi.ap.ssids" />
									<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="area.wifi.ap.ssids"/>','<bean:message bundle="locationMasterResources" key="area.wifi.ap.ssids" />')"/>
								</td>
								<td align="center" class="allborderwithtop" valign="top" width="45%">
									<textarea cols="40" rows="2" name="strWiFiSSIDs" tabindex="12" maxlength="4000" ><bean:write name="areaBean" property="wifiCallingStationInfoData.ssids"/></textarea></td>
							</tr>
						</table>
					</td> 
				 </tr>	 
				 <tr><td width="10" class="">&nbsp;</td></tr>		            		   		 
		   		 <tr align="center">
						<td colspan="3" >
							<input type="button" property="c_btnCreate" onclick="validate();" value="  Update  " class="light-btn" tabindex="13"/>&nbsp;&nbsp;&nbsp;
							<input type="button" property="c_btnCreate" onclick="javascript:location.href='<%=basePath%>/areaManagement.do?method=initSearch'" value="  Cancel  " class="light-btn" tabindex="14"/> 
			         	</td>						
				 </tr>			  	
	   </table>  
		</td> 
	</tr>
</table>
</html:form>