
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=basePath%>/css/displaytag.css" >
<%
	request.getContextPath(); 
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<bean:define id="cityBean" name="cityData" scope="request" type="com.elitecore.netvertexsm.datamanager.locationconfig.city.data.CityData" />
	 <tr>
		<td valign="top" align="right"> 
			<table cellpadding="0" cellspacing="0" border="0" width="99%" style="padding-left: 2.0em">
 			<tr>
				<td class="tblheader-bold"  valign="top" colspan="2"  ><bean:message  bundle="locationMasterResources" key="city.view.title"/> </td>
			</tr>			
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="city.name" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> <bean:write name="cityBean" property="cityName"/>&nbsp;</td>					
				  </tr>
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="region.name" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> 
					<a href="<%=basePath%>/regionManagement.do?method=view&regionId=<bean:write name="cityBean" property="region.regionId"/>" tabindex="7">
					<bean:write name="cityBean" property="region.regionName" />
					</a>&nbsp;
					</td>					
				  </tr>
				  <tr> 
					<td align="left" class="tbllabelcol" valign="top" width="30%" ><bean:message bundle="locationMasterResources" key="region.country.name" /></td> 
					<td align="left" class="tblrows" valign="top" width="70%"> <bean:write name="cityBean" property="region.countryData.name"/>&nbsp;</td>					
				  </tr>
				  <tr><td>&nbsp;</td></tr>
			     <tr>
					<td colspan="2" >
						<table align="left"  border="0" cellpadding="0" cellspacing="0" width ="60%" >						
						<tr>
						<td class="tblheader-bold" ><bean:message bundle="locationMasterResources" key="city.area.associated" /></td>
						</tr>																											
						<tr>
						<td> 
							<display:table cellpadding="0" cellspacing="0" name="areaList" id="areaData" requestURI="/cityManagement.do?action=view&method=post"  pagesize="<%=pageSize%>"  style="width:100%;margin:0px 0 0px 0">										
			  				<display:column headerClass="tblheaderfirstcol" style="border:1px solid rgb(204, 204, 204);width:40px;border-top:0px;" title='Sr#'>&nbsp;&nbsp;&nbsp;&nbsp;<%=pageContext.getAttribute("areaData_rowNum")%></display:column>					    				
			  				<display:column headerClass="tblheaderlastcol" style="border:1px solid rgb(204, 204, 204);padding-left:5px;border-left:0px;border-top:0px;" title="Area">
			  					<a href="<%=basePath%>/areaManagement.do?method=view&areaId=<bean:write name="areaData" property="areaId"/>">
			  						<bean:write name="areaData" property="area"/>
			  					</a>
			  				</display:column>
			  				<display:setProperty name="paging.banner.first"  	value='<span class="pagelinks"> [ First / Prev ] {0} [ <a href="{3}">Next</a> / <a href="{4}">Last</a> ] </span>'></display:setProperty>
			  				<display:setProperty name="paging.banner.last"  	value='<span class="pagelinks"> [ <a href="{1}">First</a> / <a href="{2}">Prev</a> ] {0} [ Next / Last ] </span>'></display:setProperty>
			  				<display:setProperty name="paging.banner.full"  	value='<span class="pagelinks"> [ <a href="{1}">First</a> / <a href="{2}">Prev</a> ] {0} [ <a href="{3}">Next</a> / <a href="{4}">Last</a> ]</span>'></display:setProperty>					    				
			  				<display:setProperty name="paging.banner.placement"  	value="bottom"></display:setProperty>
			  				<display:setProperty name="basic.empty.showtable" 		value="true"></display:setProperty>
			  				<display:setProperty name="paging.banner.item_name" 	value="Area"></display:setProperty>
			  				<display:setProperty name="paging.banner.items_name" 	value="Areas"></display:setProperty>
			  				<display:setProperty name="paging.banner.no_items_found" value='<tr><td colspan="{0}" class="table-gray" align="center"></td></tr>'></display:setProperty>
			  				<display:setProperty name="basic.msg.empty_list_row" value='<tr><td colspan="{0}" class="table-gray" style="text-align:center;background-color: #D9E6F6;" align="center">No Record Found</td></tr>'></display:setProperty>    				
			  				<display:setProperty name="paging.banner.onepage" value='<span class="pagelinks" style="width:59.6%"></span>'></display:setProperty>			  									    									    									    
						    </display:table>
						</td>
						</tr>								
						</table>
					</td></tr>
			</table>
		</td>
	</tr>
	<tr><td>&nbsp;</td>
	</tr>
</table>