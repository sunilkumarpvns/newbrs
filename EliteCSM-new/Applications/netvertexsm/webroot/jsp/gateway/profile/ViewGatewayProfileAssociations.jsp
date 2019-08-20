
<%@ page import="java.util.*" %>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData"%>
<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=basePath%>/css/displaytag.css"/>

<%
	GatewayProfileData gatewayProfileData = (GatewayProfileData)request.getAttribute("gatewayProfileData");
	Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
%>

<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
		<td valign="top" align="right"> 
	<table cellpadding="0" cellspacing="0" border="0" width="100%" align="left">
	<bean:define id="gatewayProfileBean" name="gatewayProfileData" scope="request" type="com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData"/>
	
	<tr>
		<td align="right" class="labeltext" valign="top"> 
		<table cellpadding="0" cellspacing="0" border="0" width="97%" align="right" >
			<tr>
				<td class="tblheader-bold"  valign="top" colspan="3"  ><bean:message bundle="gatewayResources" key="gatewayprofile.associations"/></td>
			</tr>
			<tr>
				<td class="small-gap">&nbsp;</td>
			</tr>
			<tr>
				<td class="small-gap">&nbsp;</td>
			</tr>
			  <tr>
			     <td class="tblheader-bold" colspan="4"><bean:message bundle="gatewayResources" key="gatewayprofile.gateway.associations" /></td>
			  </tr>	
	          <tr>
				<td> 
						    <%int i=0; %>
							<display:table   id="gatewayData" name="gatewayProfileData.gatewaySet" cellpadding="0" cellspacing="0" requestURI="/viewGatewayProfile.do?viewAssociations=true&profileId=${gatewayProfileData.profileId}"  pagesize="<%=pageSize%>"  style="width:100%;margin:0px 0 0px 0">										
			  				<display:column  headerClass="tblheaderfirstcol" style="border:1px solid rgb(204, 204, 204);border-top:0px; width:2%" title='Sr#'>
			  					&nbsp;&nbsp;&nbsp;&nbsp;<%=pageContext.getAttribute("gatewayData_rowNum")%>
			  				</display:column>					    				
			  				<display:column headerClass="tblheader" style="border:1px solid rgb(204, 204, 204);padding-left:5px;border-left:0px;border-top:0px;width:20%" title="Name">
			  				<a href="<%=basePath%>/viewGateway.do?gatewayId=<bean:write name="gatewayData" property="gatewayId"/>&<%=gatewayProfileData.getCommProtocolId()%> "> 
											<bean:write name="gatewayData" property="gatewayName" />&nbsp;
							</a>
							</display:column>
			  				<display:column headerClass="tblheaderlastcol" style="border:1px solid rgb(204, 204, 204);padding-left:5px;border-left:0px;border-top:0px;width:30%" title="Description">
			  						<bean:write name="gatewayData" property="description" />&nbsp;
							</display:column>
			  				<display:setProperty name="paging.banner.first"  	value='<span class="pagelinks"> [ First / Prev ] {0} [ <a href="{3}">Next</a> / <a href="{4}">Last</a> ] </span>'></display:setProperty>
			  				<display:setProperty name="paging.banner.last"  	value='<span class="pagelinks"> [ <a href="{1}">First</a> / <a href="{2}">Prev</a> ] {0} [ Next / Last ] </span>'></display:setProperty>
			  				<display:setProperty name="paging.banner.full"  	value='<span class="pagelinks"> [ <a href="{1}">First</a> / <a href="{2}">Prev</a> ] {0} [ <a href="{3}">Next</a> / <a href="{4}">Last</a> ]</span>'></display:setProperty>					    				
			  				<display:setProperty name="paging.banner.placement"  	value="bottom"></display:setProperty>
			  				<display:setProperty name="basic.empty.showtable" 		value="true"></display:setProperty>
			  				<display:setProperty name="paging.banner.no_items_found" value='<tr><td colspan="{0}" class="table-gray" align="center"></td></tr>'></display:setProperty>
			  				<display:setProperty name="basic.msg.empty_list_row" value='<tr><td colspan="{0}" class="table-gray" style="text-align:center;background-color: #D9E6F6;" align="center">No Record Found</td></tr>'></display:setProperty>    				
			  				<display:setProperty name="paging.banner.onepage" value='<span class="pagelinks" style="width:59.6%"></span>'></display:setProperty>			  									    									    									    
						    </display:table>
						</td>
				</tr>								
			 </table> 
	         </td>
	      </tr>
</table>
	</td>
    </tr>
</table>


