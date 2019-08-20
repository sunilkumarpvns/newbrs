<%@page import="com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerRelationWithPeerGroup"%>
<%@page import="com.elitecore.elitesm.web.diameter.diameterepeergroup.form.DiameterPeerGroupForm"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.web.diameter.peer.forms.DiameterPeerForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.diameterpeergroup.data.DiameterPeerRelationWithPeerGroup"%>
<%@ page import="java.util.List"%>
<%
	DiameterPeerGroupForm diameterPeerGroupForm=(DiameterPeerGroupForm)request.getAttribute("diameterPeerGroupForm");
	List<DiameterPeerData> peersData = null;
	List<DiameterPeerRelationWithPeerGroup> configuredPeers = null;
	if (diameterPeerGroupForm != null) {
		peersData = diameterPeerGroupForm.getDiameterPeerDataList();
		configuredPeers = diameterPeerGroupForm.getPeerList();
	}
%>

<script type="text/javascript" src="<%=basePath%>/js/diameter/peergroup/diameter-peergroup.js"></script>
<script type="text/javascript">

	var peerDetails = [];
	var configuredPeers = [];
	$(document).ready(function(){

		/* verify peer group name */
		 $("#peerGroupName").focus();
		 verifyName();
		
		<% for(DiameterPeerData peerDeta : peersData) { %>
			var peerDetail = {
					peerName: '<%=peerDeta.getName() %>',
					peerID: '<%=peerDeta.getPeerUUID()%>'
				}
			peerDetails.push(peerDetail);
		<% } %>
		
	  //intialize default data
	  initializeData(peerDetails,'peerDataCheckBoxId');
	  
	  <% for(DiameterPeerRelationWithPeerGroup peerDeta : configuredPeers) { %>
		var peerDetail = {
				peerName: '<%=peerDeta.getPeerName() %>',
				peerID: '<%=peerDeta.getPeerId()%>',
				weightage:'<%=peerDeta.getWeightage()%>'
			}
		configuredPeers.push(peerDetail);
		<% } %>
		
		//populate already configured peers in Peer Group
		populateConfiguredPeers();
	
	});
	
	var isValidName;
	
	/*This function will verify valid name and duplication */
	function verifyName() {
		var searchName = document.getElementById("peerGroupName").value;
		isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIAMETER_PEER_GROUP%>',searchName,'update','<%=diameterPeerGroupForm.getPeerGroupId()%>','verifyNameDiv');
	}
	
	/* set title of header */
	setTitle('<bean:message bundle="diameterResources" key="diameterpeergroup.title"/>'); 

</script>

<html:form action="/updateDiameterPeerGroup">
	
	<!-- Required Hidden Parameters -->
	<html:hidden name="diameterPeerGroupForm" styleId="peerGroupId" property="peerGroupId" />
	<html:hidden name="diameterPeerGroupForm" styleId="auditUId" property="auditUId" />
	<html:hidden name="diameterPeerGroupForm" styleId="action" property="action"  value="update" />
	
	<table cellpadding="0" cellspacing="0" border="0" width="100%" align="right">
		<tr>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td valign="middle" colspan="5">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="diameterResources" key="diameterpeergroup.update" />
						</td>
					</tr>
					<tr>
						<td colspan="100%">&nbsp;</td>
					</tr>
					
					<!-- Peer Name -->
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="diameterResources" key="diameterpeer.name" /> 
							<ec:elitehelp headerBundle="diameterResources"  text="diameterpeer.peername" header="diameterpeer.name"/>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text name="diameterPeerGroupForm" tabindex="1" styleId="peerGroupName" property="peerGroupName" size="30"  onblur="verifyName();" maxlength="256" style="width:250px" />
							<font color="#FF0000">*</font>
						    <div id="verifyNameDiv" class="labeltext"></div>
						</td>
					</tr>
					
					<!-- Description -->
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="diameterResources" key="diameterpeergroup.description"/> 
							<ec:elitehelp headerBundle="diameterResources" text="diameterpeergroup.description" header="diameterpeergroup.description"/>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:textarea property="description" styleId="description" name="diameterPeerGroupForm" rows="2" cols="42" tabindex="3"></html:textarea>
						</td>
					</tr>

					<!-- Peer Group -->
					<tr style="margin-top: 5px">
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="diameterResources" key="diameterpeergroup.peergroup"/> 
							<ec:elitehelp headerBundle="diameterResources" text="diameterpeergroup.peergroup" header="diameterpeergroup.peergroup"/>
						</td>
						
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<table width="auto" cellspacing="0" cellpadding="0" border="0">
								<tr>
									<td align="left" valign="top"> 
										<select class="labeltext select-box-style selectedPeersIds" name="selectedPeers" id="selectedPeers" multiple="multiple" style="width: 250px;">
										</select><font color="#FF0000"> *</font>
									</td>
									<td align="left" class="labeltext" valign="top">
										<table cellspacing="0" cellpadding="0" border="0" width="20%">
											<tr>
												<td>
													<input type="button" id="addBtn" value="Add " onClick="peerPopup(this);" class="light-btn peer-popup" style="width: 75px" tabindex="2" /><br />
												</td>
											</tr>
											<tr>
												<td style="padding-top: 5px;">
													<input type="button" id="removeBtn"  value="Remove " onclick="removeData('selectedPeers','peerDataCheckBoxId',this);" class="light-btn" style="width: 75px" />
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
						
					</tr>					
					<!-- stateful -->
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="diameterResources" key="diameterpeergroup.stateful" /> 
							<ec:elitehelp headerBundle="diameterResources" text="diameterpeergroup.stateful" header="diameterpeergroup.stateful" />
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:select property="stateful" name="diameterPeerGroupForm" styleId="stateful" tabindex="4">
								<html:option value="true">True</html:option>
								<html:option value="false">False</html:option>
							</html:select>
						</td>
					</tr>

					<!-- Transaction-timeout -->
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="diameterResources" key="diameterpeergroup.transactiontimeout" />
							<ec:elitehelp headerBundle="diameterResources" text="diameterpeergroup.transactiontimeout" header="diameterpeergroup.transactiontimeout" />
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text property="transactionTimeout" styleId="transactionTimeout" name="diameterPeerGroupForm" tabindex="5" style="width:150px"></html:text>
							<font color="#FF0000">*</font>
						</td>
					</tr>
					
					<!-- GEO Redundunt Group -->
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="diameterResources" key="diameterpeergroup.georedunduntgroup" />
							<ec:elitehelp headerBundle="diameterResources" text="diameterpeergroup.georedunduntgroup" header="diameterpeergroup.georedunduntgroup" />
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:select property="geoRedunduntGroup" name="diameterPeerGroupForm" styleId="geoRedunduntGroup" tabindex="6" style="width:150px">
								<html:option value="">NONE</html:option>
								<html:optionsCollection name="diameterPeerGroupForm" property="diameterPeerGroupList" label="peerGroupName" value="peerGroupId" />
							</html:select>
						</td>
					</tr>
					
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="2">
							<input type="button" class="light-btn" value="  Update  " onclick="validateForm();" tabindex="5" />
							<input type="button" name="c_btnCancelBtn" tabindex="6" onclick="javascript:location.href='searchDiameterPeerGroup.do'" value="  Cancel  " class="light-btn" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
<!-- Add peer pop div -->

<div id="peerPopup"  title="Add Peers" style="display: none">
	<table id="addPeersTbl" class="addPeersTblName box" cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<th class="tblheader-bold"></th>
			<th class="tblheader-bold"><bean:message bundle="diameterResources" key="diameterpeergroup.peername"/></th>
			<th class="tblheader-bold"><bean:message bundle="diameterResources" key="diameterpeergroup.weightage"/></th>
		</tr>
	</table>
</div>

</html:form>