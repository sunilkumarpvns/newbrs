<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>


<%  String index = request.getParameter("realmIndexParam"); 
			
			String defaultMappingMethodCall = "setDefaultMapping("+index+")";
			String reqMappingMethodCall = "addNewMapping('requestTranslationMapTable"+index+"')";
			String respMappingMethodCall ="addNewMapping('responseTranslationMapTable"+index+"')";
			String toggleMappingMethodCall ="toggleMappingDiv('"+index+"')";
			String removeCompMethodCall = "removeComponent(table"+index+")";
			
			String rnameId = "rname"+index;
			String routingActionId="routingaction"+index;
			String authAppId = "authApp"+index;
			String acctAppId = "acctApp"+index;
			
			String realmName = request.getParameter("realmName");
			String routingActionName = request.getParameter("routingActionName");
			String authAppIdValue = request.getParameter("authAppIdValue");
			String acctAppIdValue = request.getParameter("acctAppIdValue");
			
						
			System.out.println("realmName :"+realmName);
			System.out.println("routingActionName :"+routingActionName);
			System.out.println("authAppIdValue : "+StringEscapeUtils.escapeHtml(authAppIdValue));	
			System.out.println("acctAppIdValue: "+StringEscapeUtils.escapeHtml(acctAppIdValue));	
			
			if(realmName==null){
				realmName="";
			}
			if(routingActionName==null){
				routingActionName="";
			}
			if(authAppIdValue==null){
				authAppIdValue="";
			}
			if(acctAppIdValue==null){
				acctAppIdValue="";
			}
			
			
			
		%>

<script>
		function setRoutingActionSelection(){
			var select = document.getElementById("<%=routingActionId%>");
			if(select.length >  0){
				for(var i=0;i<select.length;i++){
					if(select[i].value == "<%=routingActionName%>"){
						select[i].selected = "selected";
					}
				}
			}
		
		}
	</script>


<table width="100%" cellpadding="0" cellspacing="0" id="table<%=index%>">
	<tr>
		<td>
			<table width="100%" cellpadding="0" cellspacing="0">
				<tr>
					<td align=left class=tblheader-bold valign=top colspan=2>
						Realm-<%=index%>
					</td>
					<td class="tblheader-bold" align="right" width="15px"><img
						src='<%=request.getContextPath()%>/images/minus.jpg'
						class='delete' height='15' onclick="<%=removeCompMethodCall%>" />
					</td>
					<td class="tblheader-bold" align="right" width="15px"><img
						alt="bottom" id="toggleImageElement<%=index%>"
						onclick="<%=toggleMappingMethodCall%>"
						src="<%=request.getContextPath()%>/images/top-level.jpg" /></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<div id="toggleDivElement<%=index%>">
				<table width="100%" cellpadding="0" cellspacing="0" class="box">
					<tr>
						<td width="100%">

							<table width="100%" cellpadding="0" cellspacing="0">
								<tr>
									<td align=left class=labeltext valign=top colspan=1 width="30%">
										<bean:message bundle="driverResources" key="driver.dc.name" />
									</td>
									<td align=left class=labeltext valign=top colspan=1><input
										name="<%=rnameId%>" id="<%=rnameId%>" type="text"
										value="<%=StringEscapeUtils.escapeHtml(realmName)%>"
										style="width: 219px"> <font color="#FF0000"> *</font>
									</td>
								</tr>
								<tr>
									<td align=left class=labeltext valign=top colspan=1 width="30%">
										<bean:message bundle="driverResources"
											key="driver.dc.routingaction" />
									</td>
									<td align=left class=labeltext valign=top colspan=1><select
										name="<%=routingActionId%>" id="<%=routingActionId%>"
										style="width: 219px">
											<option value="1">Relay</option>
											<option value="0">Local</option>
											<option value="2">Proxy</option>
											<option value="3">Redirect</option>
									</select></td>
								</tr>
								<tr>
									<td align=left class=labeltext valign=top colspan=1 width="30%">
										<bean:message bundle="driverResources"
											key="driver.dc.authappid" />
									</td>
									<td align=left class=labeltext valign=top colspan=1><input
										name="<%=authAppId%>" id="<%=authAppId%>" type="text"
										value="<%=StringEscapeUtils.escapeHtml(authAppIdValue)%>"
										style="width: 219px"></td>
								</tr>
								<tr>
									<td align=left class=labeltext valign=top colspan=1 width="30%">
										<bean:message bundle="driverResources"
											key="driver.dc.acctappid" />
									</td>
									<td align=left class=labeltext valign=top colspan=1><input
										name="<%=acctAppId%>" id="<%=acctAppId%>" type="text"
										value="<%=StringEscapeUtils.escapeHtml(acctAppIdValue)%>"
										style="width: 219px"></td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td class=labeltext>&nbsp;</td>
					</tr>
					<tr>
						<td align="right">
							<table width="97%" cellpadding="0" cellspacing="0">
								<tr>
									<td align=left class="labeltext" valign=top colspan=2><b>Vendor</b>
									</td>
								</tr>
								<tr>
									<td><input type="button"
										onclick="addNewVendor('vendorTable<%=index%>')"
										value="Add Vendor" class="light-btn" /></td>
								</tr>
								<tr>
									<td width="100%" colspan="2" valign="top">
										<table cellSpacing="0" cellPadding="0" width="100%" border="0"
											id="vendorTable<%=index%>">
											<tr>
												<td align="left" class="tblheader" valign="top" width="32%"
													id="tbl_attrid">Vendor Id</td>
												<td align="left" class="tblheader" valign="top" width="32%">Auth
													Application Id</td>
												<td align="left" class="tblheader" valign="top" width="32%">Acct
													Application Id</td>
												<td align="center" class="tblheader" valign="top" width="4%">Remove</td>
											</tr>
										</table>
									</td>

								</tr>

							</table>
						</td>
					</tr>
					<tr>
						<td class=labeltext>&nbsp;</td>
					</tr>
					<tr>
						<td align="right">
							<table width="97%" cellpadding="0" cellspacing="0">
								<tr>
									<td align=left class="labeltext" valign=top colspan=2><b>Peer</b>
									</td>
								</tr>
								<tr>
									<td><input type="button"
										onclick="addNewPeer('peerTable<%=index%>')" value="Add Peer"
										class="light-btn" /></td>
								</tr>
								<tr>
									<td width="100%" colspan="2" valign="top" align="center">
										<table cellSpacing="0" cellPadding="0" width="100%" border="0"
											id="peerTable<%=index%>">
											<tr>
												<td align="left" class="tblheader" valign="top" width="24%"
													id="tbl_attrid">PeerName</td>
												<td align="left" class="tblheader" valign="top" width="24%">Communication
													Port</td>
												<td align="left" class="tblheader" valign="top" width="24%">Attempt
													Connection</td>
												<td align="left" class="tblheader" valign="top" width="24%">Watchdog
													Interval</td>
												<td align="center" class="tblheader" valign="top"
													width="20px">Remove</td>
											</tr>
										</table>
									</td>
								</tr>

							</table>
						</td>
					</tr>
					<tr>
						<td class=labeltext>&nbsp;</td>
					</tr>
				</table>
			</div>
		</td>
	</tr>

	<tr>
		<td class=labeltext>&nbsp;</td>
	</tr>
</table>




<!-- Division Section -->

<div id="popupVendorDiv" style="display: none;" title="Add Vendor">
	<table>
		<tr>
			<td align="left" class="labeltext" valign="top" width="25%">
				Vendor Id</td>
			<td align="left" class="labeltext" valign="top" width="75%">
				<%-- 	<html:text styleId="attributeids" property="attributeids" size="25" maxlength="50" style="width:200px"/>  --%>
				<input type="text" name="vendorids" id="vendorids" size="30"
				style="width: 250px" />
			</td>
		</tr>
		<tr>
			<td align="left" class="labeltext" valign="top" width="25%">Auth
				Application Id</td>
			<td align="left" class="labeltext" valign="top" width="75%"><input
				type="text" name="authappids" id="authappids" size="30"
				style="width: 250px" /></td>
		</tr>
		<tr>
			<td align="left" class="labeltext" valign="top" width="25%">Acct
				Application Id</td>
			<td align="left" class="labeltext" valign="top" width="75%"><input
				type="text" name="acctappids" id="acctappids" size="30"
				style="width: 250px" /></td>
		</tr>
	</table>
</div>
<script>
			setRoutingActionSelection();
		</script>









