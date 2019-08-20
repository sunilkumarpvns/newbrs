<%@include file="/jsp/core/includes/common/Header.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.elitecore.netvertexsm.web.locationconfig.city.form.CityMgmtForm"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.core.util.PageList"%>
<%@ page import="org.apache.struts.util.ResponseUtils"%>
<%@ page import="org.apache.struts.util.RequestUtils"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.elitecore.netvertexsm.util.EliteUtility"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.BaseConstant"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData" %>

<%
	CityMgmtForm 	  cityForm = (CityMgmtForm)request.getAttribute("searchCityForm");	
	List<RegionData>  regionDataList 	 = cityForm.getRegionList();	
	int iIndex =0;
%>

<script type="text/javascript">

function onCountryChange(){
	var countryIdJS = document.getElementById("countryId").value;
	var regionId = document.getElementById('regionId').value;
	//alert(regionId);
	var regionSelect = document.getElementById('regionId');
	regionSelect.options.length = 0;
	regionSelect.options[0] = new Option ("--Select--","0");	
	regionSelect.options[0].selected="true";	
	var index = 1;
	<%for(RegionData regionData:regionDataList){%>
			if(countryIdJS==<%=regionData.getCountryId()%>){				
				regionSelect.options[index] = new Option ('<%=regionData.getRegionName()%>','<%=regionData.getRegionId()%>');
				if(regionSelect[index].value==<%=cityForm.getRegionId()%>){
					regionSelect[index].selected=true;
				}
				index++;
			}			
	<%}%>
}

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
    
    var chkBoxElements = document.getElementsByName('cityId');
    
    for (var i=0; i < chkBoxElements.length; i++){
        if(chkBoxElements[i].checked == true){
                selectVar = true;
                $(this).parent().parent().css("background-color","#ff5566");
        }
    }
    if(selectVar == false){
        alert('Select atleast one City for remove process.');
    }else{
        var msg;
        msg = 'All selected Cities will be deleted. Would you like to continue ?';        
        var agree = confirm(msg);
        if(agree){
       	    document.forms[1].action = '<%=request.getContextPath()%>/cityManagement.do?method=delete';
       	 	document.forms[1].submit();
        }
    }
}

function  checkAll(){
	
		var dataRows = document.getElementsByName('dataRow');
	 	if( document.forms[1].toggleAll.checked == true) {
	 		
	 		var selectVars = document.getElementsByName('cityId');
		 	for (var i = 0; i < selectVars.length;i++){
				selectVars[i].checked = true ;
		 		dataRows[i].className='onHighlightRow';
		 	}
		 				 	
	    } else if (document.forms[1].toggleAll.checked == false){
	 		var selectVars = document.getElementsByName('cityId');	    
			for (var i = 0; i < selectVars.length; i++){
				selectVars[i].checked = false ;
				dataRows[i].className='offHighlightRow';
			}
		}
	 	
}
$(document).ready(function() {	
	setTitle('<bean:message  bundle="locationMasterResources" key="city.management.title"/>');
	checkAll();
	document.forms[0].cityName.focus();		
	onCountryChange();
});

</script>

<%
	 CityMgmtForm searchCityForm = (CityMgmtForm) request.getAttribute("searchCityForm");
	 List listCityData = searchCityForm.getCityList();
     long pageNo = searchCityForm.getPageNumber();
     long totalPages = searchCityForm.getTotalPages();
     long totalRecord = searchCityForm.getTotalRecords();
	 int count=1;
	 String strPageNo = String.valueOf(pageNo);
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
     Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
%>
<table cellpadding="0" cellspacing="0" border="0" width="100%" > 
   <%@include file="/jsp/core/includes/common/HeaderBar.jsp" %>
	<tr> 
	  <td width="10">&nbsp;</td> 
	  <td width="100%" colspan="2" valign="top" class="box"> 
		<table cellSpacing="0" cellPadding="0" width="100%" border="0"> 
	 	  <tr> 
			<td class="table-header" colspan="5">
			<bean:message bundle="locationMasterResources" key="city.search.title"/></td>
		  </tr> 
		  <tr> 
		    <td align="left" class="labeltext" valign="top" colspan="3">&nbsp;</td> 
		    
		  </tr> 
		  <tr> 
			<td colspan="3">
			   <table width="100%" id="c_tblCrossProductList" align="right" border="0" >
			   <html:form action="/cityManagement.do?method=search">
			   
			   	<html:hidden name="searchCityForm" styleId="action" property="action" />
				<html:hidden name="searchCityForm" styleId="pageNumber" property="pageNumber" value="<%=strPageNo%>"/>  
			   	<html:hidden name="searchCityForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
			   	<html:hidden name="searchCityForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
			   		 
					<tr> 
						<td align="left" class="captiontext" valign="top" width="10%">
							<bean:message bundle="locationMasterResources" key="city.name"/></td> 
						<td align="left" class="labeltext" valign="top" width="32%" > 
							<html:text property="cityName" styleId="cityName" maxlength="60" size="30" styleClass="cityName" tabindex="1"/>
						</td> 
				  	</tr>
				  	 <tr>
			   	 	<td align="left" class="captiontext" valign="top"  >
							<bean:message bundle="locationMasterResources" key="region.country" />
					</td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="searchCityForm" styleId="countryId" tabindex="2" property="countryId"  size="1" style="width: 220px;" onchange="onCountryChange();" >
									  <html:option value="0">--Select--</html:option>
									   <logic:iterate id="country" name="searchCityForm" property="countryList" type="com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData">
										<html:option value="<%=Long.toString(country.getCountryID())%>"><bean:write name="country" property="name"/></html:option>
									</logic:iterate> 
						</html:select><font color="#FF0000"></font> 	      
					</td>
				   </tr>
				    <tr>
			   	 	<td align="left" class="captiontext" valign="top"  >
							<bean:message bundle="locationMasterResources" key="city.region" />
					</td> 
					<td align="left" class="labeltext" valign="top" > 
						<html:select name="searchCityForm" styleId="regionId" tabindex="3" property="regionId"  size="1" style="width: 220px;">
									  <html:option value="0">--Select--</html:option>
<%-- 									   <logic:iterate id="region" name="searchCityForm" property="regionList" type="com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData">
										<html:option value="<%=Long.toString(region.getRegionId())%>"><bean:write name="region" property="regionName"/></html:option>
									</logic:iterate> --%> 
						</html:select><font color="#FF0000"></font> 	      
					</td>
				   </tr>
				   
				   
				  	<tr> 
	        			<td class="btns-td" valign="middle" ></td> 
            			<td  align="left"   valign="top"  >             
		        			&nbsp;<input type="submit" name="Search" width="5%" Onclick="validateSearch()"   value=" Search " class="light-btn" tabindex="4"/> 
	        			</td> 
   		  			</tr>
				</html:form>
	<%
		if(searchCityForm.getAction()!=null && searchCityForm.getAction().equalsIgnoreCase(BaseConstant.LISTACTION)){
	%>
				<html:form action="/cityManagement">
				
					<html:hidden name="searchCityForm" styleId="action" property="action" />					
					<html:hidden name="searchCityForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>" />
					<html:hidden name="searchCityForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>" />
				<tr>
					<td colspan="3">
					<table cellSpacing="0" cellPadding="0" width="100%" border="0" >
					<tr>
						<td class="table-header" width="24%" colspan="2">
							<bean:message bundle="locationMasterResources" key="city.search.list" />
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
						<td class="btns-td" valign="middle" colspan="5">
							<input type="button" value="   Create   " onclick="javascript:location.href='<%=basePath%>/cityManagement.do?method=initCreate'" tabindex="5" name="c_btnCreate" class="light-btn">
								
							<input type="button" tabindex="6" name="Delete" OnClick="removeRecord()" value="   Delete   " class="light-btn">							
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
						<%}%>
						</td>
					</tr>
					<tr height="2">
						<td></td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle" colspan="9">
						<table width="97%" border="0" cellpadding="0" cellspacing="0" id="listTable">
							<tr>
								<td align="center" class="tblheaderfirstcol" valign="top" width="1%">
									<input type="checkbox" name="toggleAll" tabindex="6" value="checkbox" onclick="checkAll()" />
								</td>
								<td align="left" class="tblheader" valign="top" width="25%">
									<bean:message bundle="locationMasterResources" key="city.title" />
								</td>
								 <td align="left" class="tblheader" valign="top" width="25%">
									<bean:message bundle="locationMasterResources" key="region.title" />
								</td>
								 <td align="left" class="tblheader" valign="top" width="25%">
									<bean:message bundle="locationMasterResources" key="region.country.name" />
								</td> 								 
								<td align="center" class="tblheaderlastcol" valign="top" width="2%">
									<bean:message bundle="datasourceResources" key="general.public.edit"/>
								</td>
							</tr>
<%	
		if(listCityData!=null && listCityData.size()>0) {
		int i=0;
%>
					<logic:iterate id="cityDataBean" name="searchCityForm" property="cityList" type="com.elitecore.netvertexsm.datamanager.locationconfig.city.data.CityData">
					<%if((cityDataBean.getRegion().getCountryId()==cityForm.getCountryId() && cityForm.getRegionId()!=0) || (cityForm.getRegionId()==cityForm.getCountryId())){%>
						<tr id="dataRow" name="dataRow" >
							<td align="center" class="tblfirstcol">
								<input type="checkbox" tabindex="7" name="cityId" onclick="onOffHighlightedRow(<%=i++%>,this)" value="<bean:write name="cityDataBean" property="cityId" />" />
							</td>
							<td align="left" class="tblrows">
							<a href="<%=basePath%>/cityManagement.do?method=view&cityId=<bean:write name="cityDataBean" property="cityId"/>" tabindex="7"> 
									<bean:write name="cityDataBean" property="cityName" />&nbsp;</a>											
							</td>
						 	<td align="left" class="tblrows">
								<a href="<%=basePath%>/regionManagement.do?method=view&regionId=<bean:write name="cityDataBean" property="regionId"/>">
								<bean:write name="cityDataBean" property="region.regionName" />&nbsp;
							</td>
						 	<td align="left" class="tblrows">
								<bean:write name="cityDataBean" property="region.countryData.name" />&nbsp;
							</td>							 
							<td align="center" class="tblrows">
								<a href="<%=basePath%>/cityManagement.do?method=initUpdate&cityId=<bean:write name="cityDataBean" property="cityId"/>" tabindex="7">
								<img src="<%=basePath%>/images/edit.jpg" alt="Edit" border="0">&nbsp;
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
								<td align="center" class="tblfirstcol" colspan="7"><bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/></td>
							</tr>							
							<% 
						}
					%>												
<%		}else{	%>
					<tr>
						<td align="center" class="tblfirstcol" colspan="7"><bean:message bundle="datasourceResources" key="database.datasource.norecordfound"/></td>
					</tr>
<%		}	%>
					</table>
					</td>
				</tr>
				<tr height="2">
					<td></td>
				</tr>
				<tr>
					<td class="btns-td" valign="middle" colspan="5">	
						<input type="button" value="   Create   " tabindex="10" name="c_btnCreate" class="light-btn"
							 onclick="javascript:location.href='<%=basePath%>/cityManagement.do?method=initCreate'"/> 
						<input type="button" name="Delete" tabindex="11" OnClick="removeRecord()" value="   Delete   " class="light-btn">													
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
							<%}%>
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
<%@include file="/jsp/core/includes/common/Footerbar.jsp" %>
</table>  

