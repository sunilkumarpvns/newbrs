<%@ include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.util.PageList"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.BaseConstant"%>
<%@ page import="com.elitecore.netvertexsm.web.locationconfig.area.form.AreaMgmtForm"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.locationconfig.city.data.CityData" %>

<% 	int iIndex =0;
	AreaMgmtForm 	  		areaMgmtForm = (AreaMgmtForm)request.getAttribute("areaMgmtForm");
	List<RegionData> 	  regionDataList = areaMgmtForm.getListRegionData();
	List<CityData> 		  	cityDataList = areaMgmtForm.getListCityData();	
%>

<script type="text/javascript">

$(document).ready(function(){
	setTitle('<bean:message  bundle="locationMasterResources" key="area.management.title"/>');
	 checkAll();
	$("#area").focus();
	onCountryChange();
});

function validateSearch(){
	document.forms[0].pageNumber.value = 1;
	document.forms[0].submit();
}

function navigate(pageNumber ){
	document.forms[0].pageNumber.value = pageNumber;
	document.forms[0].actionName='list';
	document.forms[0].submit();
}
function removeRecord(){
    var selectVar = false;
    
    var chkBoxElements = document.getElementsByName('areaId');
    
    for (var i=0; i < chkBoxElements.length; i++){
        if(chkBoxElements[i].checked == true){
                selectVar = true;
        }
    }
    
    if(selectVar == false){
        alert('Select at least one Area Instance for remove process.');
    }else{
        var msg;
        msg = 'All selected Area\'s Instances would be deleted. Would you like to continue ?';        
        var agree = confirm(msg);
        if(agree){
       	    document.forms[1].action = '<%=request.getContextPath()%>/areaManagement.do?method=delete';
       	 	document.forms[1].submit();
        }
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
				if(regionSelect[index].value==<%=areaMgmtForm.getRegionId()%>){
					regionSelect[index].selected=true;
				}
				index++;
			}			
	<%}%>	
	
	onStateChange();
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
				if(citySelect[index].value==<%=areaMgmtForm.getCityId()%>){
					citySelect[index].selected=true;
				}
				index++;
			}			
	<%}%>			
}

function  checkAll(){
	var dataRows = document.getElementsByName('dataRow');
	var checkBoxElements = document.getElementsByName('areaId');
 	for (var i = 0; i < checkBoxElements.length;i++){
 		checkBoxElements[i].checked = document.forms[1].toggleAll.checked ;
 		if(document.forms[1].toggleAll.checked){
 			dataRows[i].className='onHighlightRow';
 		}else{
 			dataRows[i].className='offHighlightRow';
 		}
 	}
}

</script>

<%
	//AreaMgmtForm areaMgmtForm = (AreaMgmtForm) request.getAttribute("areaMgmtForm");
     List areaDataList = areaMgmtForm.getAreaDataList();   
             
     long pageNo = areaMgmtForm.getPageNumber();
     long totalPages = areaMgmtForm.getTotalPages();
     long totalRecord = areaMgmtForm.getTotalRecords();
     Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	 int count=1;
	 String strPageNo = String.valueOf(pageNo);
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
<%@ include file="/jsp/core/includes/common/HeaderBar.jsp" %> 
  <tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0">				
	 	  <tr> 
			<td class="table-header" colspan="3"><bean:message  bundle="locationMasterResources" key="area.management.search.title"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		  </tr> 
		  <tr> 
			<td colspan="3"> 
			   <table width="100%" align="left" border="0" >
				   <html:form action="/areaManagement.do?method=search" >
				   <html:hidden name="areaMgmtForm" styleId="actionName" property="actionName" />
				   <html:hidden name="areaMgmtForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>"/>  
				   <html:hidden name="areaMgmtForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
				   <html:hidden name="areaMgmtForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />				      			
				  
				  <tr>
					<td align="left" class="captiontext" valign="top" width="20%">
						<bean:message bundle="locationMasterResources" key="area.name"/></td>
					<td align="left" valign="top" width="*" >
	     				<html:text name="areaMgmtForm" property="area" styleId="area" tabindex="1" size="20"/>
					 </td>
				  </tr>
				  <tr> 
					<td align="left" class="captiontext" valign="top" width="15%"><bean:message bundle="locationMasterResources" key="city.country.name" />&nbsp;</td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="areaMgmtForm" styleId="countryId" tabindex="2" property="countryId"  size="1" style="width: 220px;" onchange="onCountryChange();"  onkeypress=";" >
									  <html:option value="0">--Select--</html:option>
									  <logic:iterate id="country" name="areaMgmtForm" property="listCountryData" type="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData">
										<html:option value="<%=Long.toString(country.getCountryID())%>"><bean:write name="country" property="name"/></html:option>
									  </logic:iterate> 
						</html:select> 	      
					</td>
				  </tr>
				  <tr> 
					<td align="left" class="captiontext" valign="top" width="15%"><bean:message bundle="locationMasterResources" key="area.region" />&nbsp;</td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="areaMgmtForm" styleId="regionId" tabindex="3" property="regionId"  size="1" style="width: 220px;" onchange="onStateChange()" >
									  <html:option value="0">--Select--</html:option>
						</html:select> 	      
					</td>
				 </tr>
				 <tr> 
					<td align="left" class="captiontext" valign="top" width="15%"><bean:message bundle="locationMasterResources" key="city.title" />&nbsp;</td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="areaMgmtForm" styleId="cityId" tabindex="4" property="cityId" onchange="verifyName();"  size="1" style="width: 220px;">
									  <html:option value="0">--Select--</html:option>
						</html:select> 	      
					</td>
				</tr>				  			 
				  <tr> 
	        		<td class="btns-td" valign="middle" >&nbsp;</td> 
            		<td valign="middle" >             
		        		<input type="submit" name="Search"  Onclick="validateSearch()" value="   Search   " tabindex="5" class="light-btn" /> 
	        		</td> 
   		  		  </tr>	
				  
				</html:form> 
	<%
		if(areaMgmtForm.getActionName()!=null && areaMgmtForm.getActionName().equalsIgnoreCase(BaseConstant.LISTACTION)){
	%>
				<html:form action="/areaManagement.do">
				
					<html:hidden name="areaMgmtForm" styleId="actionName" property="actionName" />
					<html:hidden name="areaMgmtForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="areaMgmtForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				<tr>
					<td colspan="3">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
						<tr>
						<td class="table-header" width="24%" colspan="2">
							<bean:message bundle="locationMasterResources" key="area.search.list" />
						</td>
						<td align="left" class="blue-text" valign="middle" width="62%" colspan="3">&nbsp;</td>
						<td align="right" class="blue-text" valign="middle" width="14%" colspan="4">
								<% if(totalRecord == 0) { %>
								<% }else if(pageNo == totalPages+1) { %>
							    	[<%=((pageNo-1)*pageSize)+1%>-<%=totalRecord%>] of <%= totalRecord %>
								<% } else if(pageNo == 1) { %>
									[<%=(pageNo-1)*pageSize+1%>-<%=(pageNo-1)*pageSize+pageSize%>] of <%= totalRecord %>
								<% } else { %>
									[<%=((pageNo-1)*pageSize)+1%>-<%=((pageNo-1)*pageSize)+pageSize%>] of <%= totalRecord %>
								<% } %>
							</td>
						</tr>
						
						<tr>
							<td class="btns-td" valign="middle" colspan="5" >
								<input type="button"   value="   Create   "  onclick="javascript:location.href='<%=basePath%>/areaManagement.do?method=initCreate'"  class="light-btn" tabindex="6" /> 
								<input type="button"   value="   Delete   " onclick="removeRecord()" class="light-btn" tabindex="7" >
							</td>
							<td class="btns-td" align="right" >
						<% if(totalPages >= 1) { %>
			  				<% if(pageNo == 1){ %>
			  	    			<a  href="#" onclick="navigate('<%=pageNo+1%>')"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a href="#" onclick="navigate('<%=totalPages+1%>')"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
							<% } %>
						  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
						  		<a  href="#" onclick="navigate('<%=1%>')"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
								<a  href="#" onclick="navigate('<%= pageNo-1%>')"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
								<a  href="#" onclick="navigate('<%=pageNo+1%>')"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
								<a  href="#" onclick="navigate('<%=totalPages+1%>')"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
						  	<% } %>
						 	<% if(pageNo == totalPages+1) { %>
							 	<a  href="#" onclick="navigate('<%=1%>')"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
								<a  href="#" onclick="navigate('<%= pageNo-1%>')"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
							<% } %>
						<% } %>
							</td>
						</tr>						
						<tr>
							<td class="btns-td" valign="middle" colspan="9">
								<table width="97%" border="0" cellpadding="0" cellspacing="0" id="listTable">
									<tr>
										<td align="center" class="tblheaderfirstcol" valign="top" width="1%"> <input type="checkbox" name="toggleAll" tabindex="8" value="checkbox" onclick="checkAll()" /> </td>
										<td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="locationMasterResources" key="area.name" /></td>											
										<td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="locationMasterResources" key="city.title" /> </td>
										<td align="left" class="tblheader" valign="top" width="20%"><bean:message bundle="locationMasterResources" key="region.title" /> </td>										
										<td align="left" class="tblheader" valign="top" width="25%"><bean:message bundle="locationMasterResources" key="region.country.name" /></td>																				
										<td align="left" class="tblheaderlastcol" valign="top" width="1%"><bean:message key="general.edit" /></td>
									</tr>								 
	                               <%	
										if(areaDataList!=null && areaDataList.size()>0) {
											int i=0;
									%>
								<logic:iterate id="areaBean" name="areaMgmtForm"  property="areaDataList" type="com.elitecore.netvertexsm.datamanager.locationconfig.area.data.AreaData">
								<%
									if( (areaMgmtForm.getCountryId()>0 && areaMgmtForm.getRegionId()==0 && areaMgmtForm.getCityId()==0 && areaMgmtForm.getCountryId()== areaBean.getCityData().getRegion().getCountryId())){										
								%>
									<tr id="dataRow" name="dataRow">								
										<td align="center" class="tblfirstcol"><input type="checkbox" tabindex="9" name="areaId" value="<bean:write name="areaBean" property="areaId"/>"  onclick="onOffHighlightedRow(<%=i++%>,this)" /></td>
										<td align="left" class="tblrows">
											<a href="<%=basePath%>/areaManagement.do?method=view&areaId=<bean:write name="areaBean" property="areaId"/>" tabindex="9">
												<bean:write name="areaBean" property="area" />
											</a>											
										</td>											
											
										<td align="left" class="tblrows">
											<logic:notEmpty name="areaBean" property="cityData">
												<bean:write name="areaBean" property="cityData.cityName"/>
											</logic:notEmpty>																	
											&nbsp;										
										</td>
										
										<td align="left" class="tblrows">											
											<logic:notEmpty name="areaBean" property="cityData">
												<bean:write name="areaBean" property="cityData.region.regionName"/>
											</logic:notEmpty>																	
											&nbsp;											
										</td>

										<td align="left" class="tblrows">
											
											<logic:notEmpty name="areaBean" property="cityData">
												<bean:write name="areaBean" property="cityData.region.countryData.name"/>
											</logic:notEmpty>																	
											&nbsp;																				 																	
										</td>
																																
										<td align="center" class="tblrows">
											<a href="<%=basePath%>/areaManagement.do?method=initUpdate&areaId=<bean:write name="areaBean" property="areaId"/>" tabindex="9">
												<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
											</a>
										</td>																																				
									</tr>
									<% count=count+1; %>
									<% iIndex += 1; %>
									<%} else if(areaMgmtForm.getRegionId()>0 && areaMgmtForm.getCityId()==0 && areaMgmtForm.getRegionId()==areaBean.getCityData().getRegionId()){
										
									%>
									
									<tr id="dataRow" name="dataRow">
									
										<td align="center" class="tblfirstcol"><input type="checkbox" tabindex="9" name="areaId" value="<bean:write name="areaBean" property="areaId"/>"  onclick="onOffHighlightedRow(<%=i++%>,this)" /></td>
										<td align="left" class="tblrows">
											<a href="<%=basePath%>/areaManagement.do?method=view&areaId=<bean:write name="areaBean" property="areaId"/>" tabindex="9">
												 <bean:write name="areaBean" property="area" />
											</a>											
										</td>											
											
										<td align="left" class="tblrows">
											<logic:notEmpty name="areaBean" property="cityData">
												<bean:write name="areaBean" property="cityData.cityName"/>
											</logic:notEmpty>																	
											&nbsp;										
										</td>
										
										<td align="left" class="tblrows">											
											<logic:notEmpty name="areaBean" property="cityData">
												<bean:write name="areaBean" property="cityData.region.regionName"/>
											</logic:notEmpty>																	
											&nbsp;											
										</td>

										<td align="left" class="tblrows">
											
											<logic:notEmpty name="areaBean" property="cityData">
												<bean:write name="areaBean" property="cityData.region.countryData.name"/>
											</logic:notEmpty>																	
											&nbsp;																				 																	
										</td>
																						
										<td align="center" class="tblrows">
											<a href="<%=basePath%>/areaManagement.do?method=initUpdate&areaId=<bean:write name="areaBean" property="areaId"/>" tabindex="9">
												<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
											</a>
										</td>																																				
									</tr>	
									<% count=count+1; %>
									<% iIndex += 1; %>								
									<%}else if(areaMgmtForm.getCityId()>0 && areaBean.getCityId()==areaMgmtForm.getCityId()){%>
									<tr id="dataRow" name="dataRow">								
										<td align="center" class="tblfirstcol"><input type="checkbox" tabindex="9" name="areaId" value="<bean:write name="areaBean" property="areaId"/>"  onclick="onOffHighlightedRow(<%=i++%>,this)" /></td>
										<td align="left" class="tblrows">
											<a href="<%=basePath%>/areaManagement.do?method=view&areaId=<bean:write name="areaBean" property="areaId"/>" tabindex="9">
												<bean:write name="areaBean" property="area" />
											</a>											
										</td>											
											
										<td align="left" class="tblrows">
											<logic:notEmpty name="areaBean" property="cityData">
												<bean:write name="areaBean" property="cityData.cityName"/>
											</logic:notEmpty>																	
											&nbsp;										
										</td>
										<td align="left" class="tblrows">											
											<logic:notEmpty name="areaBean" property="cityData">
												<bean:write name="areaBean" property="cityData.region.regionName"/>
											</logic:notEmpty>																	
											&nbsp;											
										</td>

										<td align="left" class="tblrows">
											
											<logic:notEmpty name="areaBean" property="cityData">
												<bean:write name="areaBean" property="cityData.region.countryData.name"/>
											</logic:notEmpty>																	
											&nbsp;																				 																	
										</td>
																						
										<td align="center" class="tblrows">
											<a href="<%=basePath%>/areaManagement.do?method=initUpdate&areaId=<bean:write name="areaBean" property="areaId"/>" tabindex="9">
												<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
											</a>
										</td>																																				
									</tr>									
									<% count=count+1; %>
									<% iIndex += 1; %>									
									<%}else if(areaMgmtForm.getCountryId()==0){
										
									%>
									<tr id="dataRow" name="dataRow">								
										<td align="center" class="tblfirstcol"><input type="checkbox" tabindex="9" name="areaId" value="<bean:write name="areaBean" property="areaId"/>"  onclick="onOffHighlightedRow(<%=i++%>,this)" /></td>
										<td align="left" class="tblrows">
											<a href="<%=basePath%>/areaManagement.do?method=view&areaId=<bean:write name="areaBean" property="areaId"/>" tabindex="9">
												<bean:write name="areaBean" property="area" />
											</a>											
										</td>											
											
										<td align="left" class="tblrows">
											<logic:notEmpty name="areaBean" property="cityData">
											<a href="<%=basePath%>/cityManagement.do?method=view&cityId=<bean:write name="areaBean" property="cityId"/>">
												<bean:write name="areaBean" property="cityData.cityName"/>
											</logic:notEmpty>																	
											&nbsp;										
										</td>
										
										<td align="left" class="tblrows">											
											<logic:notEmpty name="areaBean" property="cityData">
												<a href="<%=basePath%>/regionManagement.do?method=view&regionId=<bean:write name="areaBean" property="cityData.region.regionId"/>">
												<bean:write name="areaBean" property="cityData.region.regionName"/>
											</logic:notEmpty>																	
											&nbsp;											
										</td>

										<td align="left" class="tblrows">
											
											<logic:notEmpty name="areaBean" property="cityData">
												<bean:write name="areaBean" property="cityData.region.countryData.name"/>
											</logic:notEmpty>																	
											&nbsp;																				 																	
										</td>
																						
										<td align="center" class="tblrows">
											<a href="<%=basePath%>/areaManagement.do?method=initUpdate&areaId=<bean:write name="areaBean" property="areaId"/>" tabindex="9">
												<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">
											</a>
										</td>																																				
									</tr>
									<% count=count+1; %>
									<% iIndex += 1; %>									
									<%}%>
								</logic:iterate>
									<%
										if(iIndex==0){
											%>
											<tr>
												<td align="center" class="tblfirstcol" colspan="8"><bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/></td>
											</tr>											
											<%
										}
									%>
																				
								<%	}else{	%>
									<tr>
										<td align="center" class="tblfirstcol" colspan="8"><bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/></td>
									</tr>
								<%	}	%>
							</table>
							</td>
						</tr>
						<tr height="2">
							<td></td>
						</tr>
						<tr>
							<td class="btns-td" valign="middle" colspan="5" >
								<input type="button"   value="   Create   "  onclick="javascript:location.href='<%=basePath%>/areaManagement.do?method=initCreate'"  class="light-btn" /> 
								<input type="button"   value="   Delete   " onclick="removeRecord()" class="light-btn">
							</td>
							<td class="btns-td" align="right">
							<% if(totalPages >= 1) { %>
				  				<% if(pageNo == 1){ %>
				  	    			<a  href="#" onclick="navigate('<%=pageNo+1%>')"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a href="#" onclick="navigate('<%=totalPages+1%>')"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
								<% } %>
							  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
							  		<a  href="#" onclick="navigate('<%=1%>')"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="#" onclick="navigate('<%= pageNo-1%>')"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
									<a  href="#" onclick="navigate('<%=pageNo+1%>')"><img  src="<%=basePath%>/images/next.jpg"  name="Image61" onmouseover="MM_swapImage('Image61','','<%=basePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" ></a>
									<a  href="#" onclick="navigate('<%=totalPages+1%>')"><img  src="<%=basePath%>/images/last.jpg"  name="Image612" onmouseover="MM_swapImage('Image612','','<%=basePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" ></a>
							  	<% } %>
							 	<% if(pageNo == totalPages+1) { %>
								 	<a  href="#" onclick="navigate('<%=1%>')"><img  src="<%=basePath%>/images/first.jpg" name="Image511" onmouseover="MM_swapImage('Image511','','<%=basePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" ></a>
									<a  href="#" onclick="navigate('<%= pageNo-1%>')"><img  src="<%=basePath%>/images/previous.jpg" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=basePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" ></a>
								<% } %>
							<% } %>
							</td>						
						</tr>
						</table></td>
						</tr>										
						<tr>
							<td colspan="3">&nbsp;</td>
						</tr>
				</html:form>	
		<% 	} %>								
			  </table>  
		</td> 
	</tr>	          
</table>
</td>
</tr>
<%@ include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table> 

