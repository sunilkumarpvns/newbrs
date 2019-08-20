<%@include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyConstants"%>
<%@page import="com.elitecore.corenetvertex.constants.PCRFKeyType"%>
<%@page import="com.elitecore.corenetvertex.constants.LogMonitorTypes"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.server.form.ListNetServerLogsForm"%>
<%@page import="com.elitecore.netvertexsm.web.servermgr.server.LogMonitorData"%>
<%@page import="java.util.*"%>
<%@page import="java.io.File"%>

<%
	ListNetServerLogsForm listNetServerLogsForm = (ListNetServerLogsForm) request
			.getAttribute("serverLogForm");
	String registeredLogMonitorTypes = (String) request
			.getAttribute("registeredLogMonitorTypes");
%>

<script language="javascript"
	src="<%=basePath%>/js/expressionbuilder.js"></script>
<script language="JavaScript"> 

	function createTimeValidation(){
		document.getElementById("duration").value = $.trim(document.getElementById("duration").value);
		if(isNull(document.getElementById("logMonitorType").value) || document.getElementById("logMonitorType").value==0){
			alert("Log Monitor Type must be specified");
			document.getElementById("logMonitorType").focus();			
		}else if(isNull(document.getElementById("expression").value)){
			alert("Expression must be specified");
			document.getElementById("expression").focus();
		}else if(isNull(document.getElementById("duration").value) ==false && isWholeNumber(document.getElementById("duration").value)==false){
			alert("Duration must be Positive Numeric value");
			document.getElementById("duration").focus();
		}else{
			document.forms[0].submit();
		}
	}	
	
	function editTimeValidation(){
		document.getElementById("editDuration").value = $.trim(document.getElementById("editDuration").value);
		if(isNull(document.getElementById("editLogMonitorType").value) || document.getElementById("editLogMonitorType").value==0){
			alert("Log Monitor Type must be specified");
			document.getElementById("logMonitorType").focus();			
		}else if(isNull(document.getElementById("editExpression").value)){
			alert("Expression must be specified");
			document.getElementById("editExpression").focus();
		}else if(isNull(document.getElementById("editDuration").value) ==false && isWholeNumber(document.getElementById("editDuration").value)==false){
			alert("Duration must be Positive Numeric value");
			document.getElementById("editDuration").focus();
		}else{
			document.forms[1].submit();
		}
	}
 
	//Begin : Auto-scroll support for progressive log output.
	//See http://radio.javaranch.com/pascarello/2006/08/17/1155837038219.html
	//
	function AutoScroller(scrollContainer) {
	 // get the height of the viewport.
	 // See http://www.howtocreate.co.uk/tutorials/javascript/browserwindow
	 function getViewportHeight() {
	     if (typeof( window.innerWidth) == 'number') {
	         //Non-IE
	         return window.innerHeight;
	     } else if (document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight 
			
	)) {
	         //IE 6+ in 'standards compliant mode'
	         return document.documentElement.clientHeight;
	     } else if (document.body && ( document.body.clientWidth || document.body.clientHeight )) {
	         //IE 4 compatible
	         return document.body.clientHeight;
	     }
	     return null;
	 }
			
	 return {
	     bottomThreshold : 350,
	     scrollContainer: scrollContainer,
			
	     getCurrentHeight : function() {
	         var scrollDiv = $(this.scrollContainer);
	         if (scrollDiv.prop('scrollHeight') > 0){
	             return scrollDiv.prop('scrollHeight');
	         }else{
	             if (scrollDiv.prop('offsetHeight') > 0){
	                 return scrollDiv.prop('offsetHeight');
	             }else{
	            	 return 300;
	             }
	         }
	         return null; // huh?
	     },
	
	     // return true if we are in the "stick to bottom" mode
	     isSticking : function() {
	         var scrollDiv = $(this.scrollContainer);
	         var currentHeight = this.getCurrentHeight();
	         // when used with the BODY tag, the height needs to be the viewport height, instead of
	         // the element height.
	         //var height = ((scrollDiv.style.pixelHeight) ? scrollDiv.style.pixelHeight : scrollDiv.offsetHeight);
	         var height = getViewportHeight();
	         var diff = currentHeight - scrollDiv.scrollTop() - height;
	         // window.alert("currentHeight=" + currentHeight + ",scrollTop=" + scrollDiv.scrollTop + ",height=" + height);
	         return diff < this.bottomThreshold;
	     },
	
	     scrollToBottom : function() {
	         var scrollDiv = $(this.scrollContainer);
	         scrollDiv.scrollTop = (this.getCurrentHeight());
	     }
	 };
	}
	
	//scroll the current window to display the given element or the region.
	function scrollIntoView(e) {
	 function calcDelta(ex1,ex2,vx1,vw) {
	     var vx2=vx1+vw;
	     var a;
	     a = Math.min(vx1-ex1,vx2-ex2);
	     if(a>0)     return -a;
	     a = Math.min(ex1-vx1,ex2-vx2);
	     if(a>0)     return a;
	     return 0;
	 }
	
	 var D = YAHOO.util.Dom;
	
	 var r;
	 if(e.tagName!=null) r = D.getRegion(e);
	 else                r = e;
	
	 var dx = calcDelta(r.left,r.right, document.body.scrollLeft, D.getViewportWidth());
	 var dy = calcDelta(r.top, r.bottom,document.body.scrollTop,  D.getViewportHeight());
	 window.scrollBy(dx,dy);
	}
	
	//// End : autoscroll
	
	var scroller = new AutoScroller("#mainParentDiv");
	
	$(document).ready(function() {
			var isScrollBarMovedByUser=false;
			var previouHeight=0;
			$("#logMonitorListDiv").hide();
			$("#hideLogMonitors").hide();
			

			var url = "<%=basePath%>/initListNetServerLogs.do?method=readLog";					
			<%if(listNetServerLogsForm.getLogMonitorDatas()!=null && listNetServerLogsForm.getLogMonitorDatas().size()>0){%>
				autoUpdateData();		
			<%}%>
			function autoUpdateData(){
				previouHeight = $("#serverLogs").height()+16000;				
				scroll_to($('#mainParentDiv'), $("#progressDiv"));
				setInterval(fetchData,5000);
			}
			function fetchData(){
				previouHeight = $("#serverLogs").height() + 16000;	
				var stickToBottom = scroller.isSticking();
				$.post(url, {isFromTraceLog:"true"}, function(data){					
					if(data.trim()=='null'){		
					}else{
						var isNewfile = data.substring(0,5);
				        var filedata  = data.substring(5);
				       
						if(isNewfile=="false"){							
							$('#serverLogs').append(filedata);
						}else{
							document.getElementById("serverLogs").innerHTML =filedata;
						}						
						}
				});
				
				var stickToBottom = scroller.isSticking();
				if(stickToBottom){
					for(var i=0; i<20; i++){
					scroll_to($('#mainParentDiv'), $("#progressDiv"));
					}
				}
				//var currentPosition = $('#mainParentDiv').scrollTop();
				/* if((parseInt($("#serverLogs").height()-parseInt(currentPosition))) <=parseInt($("#serverLogs").height())/2){
					//var diff = parseInt(currentPosition)-parseInt($("#serverLogs").height());
					//$('#serverLogs').append("Moving to End : "+(parseInt($("#serverLogs").height())-parseInt(currentPosition))+"</br>");
					scroll_to($('#mainParentDiv'), $("#progressDiv"));
				}  */
			}
			scroll_to($('#mainParentDiv'), $("#progressDiv"));
			function scroll_to(base, target){
				previouHeight +=($("#serverLogs").height()*2); 
			    base.animate({
			    	scrollTop: previouHeight
			    },50);			    
			};
				});						
	
		function createLogMonitor(){
			setTextAreaTag(document.getElementById("expression"));			
			setTextKeyTag(document.getElementById("policyKeys"));
			loadAll();
	
			document.getElementById('logMonitorListDiv').style.display='none';
			document.getElementById('createNewLogMonitorDiv').style.display='block';
			document.getElementById('editLogMonitorDiv').style.display='none';
			document.getElementById("logMonitorType").focus();
			setShowMeImage();
		}
		
		function cancelCreateOrEdit(){
			document.getElementById('logMonitorListDiv').style.display='block';
			document.getElementById('createNewLogMonitorDiv').style.display='none';
			document.getElementById('editLogMonitorDiv').style.display='none';
			setHideMeImage();
		}
		function setHideMeImage(){
			newSrc = $("img.showLogMonitors").attr("src").replace("showme.png", "hideme.png");
			$("img.showLogMonitors").attr("src", newSrc);
		}
		function setShowMeImage(){
			newSrc = $("img.showLogMonitors").attr("src").replace("hideme.png", "showme.png");
			$("img.showLogMonitors").attr("src", newSrc);			
		}
		function openEditLogMonitorDiv(type,expression,duration){
			setShowMeImage();
			setTextAreaTag(document.getElementById("editExpression"));
			setTextKeyTag(document.getElementById("editPolicyKeys"));
			loadAll();
			
			document.getElementById('oldLogMonitorType').value=type;
			document.getElementById('oldExpression').value=expression;
			document.getElementById('editLogMonitorType').value=type;
			document.getElementById('editExpression').value=expression;
			document.getElementById('editDuration').value=duration;
			document.getElementById('editLogMonitorDiv').style.display='block';
			document.getElementById('logMonitorListDiv').style.display='none';
			document.getElementById('createMonitorListDiv').style.display='none';
			document.getElementById("editLogMonitorType").focus();
		}

		function deleteAllLogMonitors(){
			 	var isAnyCheckBoxChecked = false;		
			 	var checkBoxArray = document.getElementsByName("dataRowCheckBox");
			    for (var i=0; i < document.getElementsByName("dataRowCheckBox").length; i++){
		            if(checkBoxArray[i].checked == true){
		            	isAnyCheckBoxChecked = true;
			        }
			    }
			    
			    if(isAnyCheckBoxChecked == false){  
			        alert('No Log Monitor selected');
			        return false;
			    }else{
					var agree = confirm("Are you sure to delete Selected Log Monitors ?");
					return agree;
			    }
		}		 
		
		function changeImageAsPerListView(){
			
			if($("img.showLogMonitors").attr("src").indexOf("showme.png")!=-1){
			 	newSrc = $("img.showLogMonitors").attr("src").replace("showme.png", "hideme.png");
			}else{
			 	newSrc = $("img.showLogMonitors").attr("src").replace("hideme.png", "showme.png");
			  } 
			$("img.showLogMonitors").attr("src", newSrc);
		}
			
		function showHideLogMonitorList(thisElement){
			var newSrc ;
			if($(thisElement).attr("src").indexOf("showme.png")!=-1){
			 	newSrc = $(thisElement).attr("src").replace("showme.png", "hideme.png");
			 	$("#logMonitorListDiv").slideToggle();			 	
			}else{
			 	newSrc = $(thisElement).attr("src").replace("hideme.png", "showme.png");
				  $("#logMonitorListDiv").slideToggle();				  			 	
		}
		    $(thisElement).attr("src", newSrc);		 			
		}
		function selectAllDataRow(parentCheckBox){
			if(parentCheckBox.checked==true){
				var checkBoxArray = document.getElementsByName("dataRowCheckBox");
			    for (var i=0; i < document.getElementsByName("dataRowCheckBox").length; i++){
			    	checkBoxArray[i].checked = true;
			    }
			}else{
				var checkBoxArray = document.getElementsByName("dataRowCheckBox");
			    for (var i=0; i < document.getElementsByName("dataRowCheckBox").length; i++){
			    	checkBoxArray[i].checked = false;
			    }				
			}
		}
</script>


<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td colspan="2" valign="top"><%@ include
				file="/jsp/core/includes/common/HeaderBar.jsp"%>
		</td>
	</tr>
	<tr>
		<td colspan="2" valign="top">
			<div style="width: 90%;">
				<div id='createNewLogMonitorDiv' style="display: none; width: 100%">
					<table style="padding-left: 1.6em">
						<thead>
							<tr class="table-header">
								<td><bean:message bundle="servermgrResources"
										key="logmonitor.create" /></td>
							</tr>
						</thead>
						<tbody>
							<html:form styleId="createForm"
								action="/initListNetServerLogs?method=addLogMonitor">
								<tr>
									<td class="labeltext"><bean:message
											bundle="servermgrResources" key="logmonitor.type" /></td>
									<td class="labeltext"><select style="width: 100px"
										name="logMonitorType" id="logMonitorType">
											<option value="0">--Select--</option>
											<%
												if (registeredLogMonitorTypes != null
															&& registeredLogMonitorTypes.trim().length() > 0) {
														for (LogMonitorTypes type : LogMonitorTypes.values()) {
											%>
											<%
												if (registeredLogMonitorTypes.contains(type.getValue()))
											%>
											<option value="<%=type.getValue()%>"><%=type.getDisplayName()%></option>
											<%
												}
											%>
											<%
												}
											%>
									</select> <font color="#FF0000"> *</font></td>
								</tr>
								<tr>
									<td class="labeltext"><bean:message
											bundle="servermgrResources" key="logmonitor.expression" /></td>
									<td class="labeltext"><textarea rows="2" cols="50"
											name="expression" id="expression"></textarea><font
										color="#FF0000"> *</font></td>
									<select id="policyKeys" hidden="true">
 
										<%
											for (PCRFKeyConstants keyConstants : PCRFKeyConstants
														.values(PCRFKeyType.RULE)) {
										%>>
										<option value="<%=keyConstants.getVal()%>"><%=keyConstants.getVal()%></option>
										<%
											}
										%>
									</select>

								</tr>
								<tr>
								<tr class="labeltext">
									<td align="right" valign="top" colspan="1"></td>
									<td align="left" valign="top" colspan="2">&nbsp;<label
										class="small-text-grey"><bean:message
												bundle="descriptionResources"
												key="expression.possibleoperators" /></label>
 
									</td>
								</tr>

								<tr>
									<td class="labeltext"><bean:message
											bundle="servermgrResources" key="logmonitor.duration" /></td>
									<td class="labeltext"><input type="text" name="duration"
										maxlength="20" id="duration" size="25" /></td>
								</tr>
								<tr class="small-gap">
									<td>&nbsp;</td>
								</tr>
								<tr>
									<td class="labeltext" align="right">&nbsp;</td>
									<td class="labeltext" align="left"><input
										class="light-btn" type="button" name="create" value="Create"
										onclick="return createTimeValidation();"> <input
										class="light-btn" type="button" name="cancel" value="Cancel"
										onclick="cancelCreateOrEdit();"></td>
								</tr>
							</html:form>
						</tbody>
					</table>
				</div>
				<div id='editLogMonitorDiv' style="display: none; width: 100%">
					<table style="padding-left: 1.0em">
						<thead>
							<tr class="table-header">
								<td><bean:message bundle="servermgrResources"
										key="logmonitor.update" /></td>
							</tr>
						</thead>
						<tbody>
							<html:form styleId="editForm"
								action="/initListNetServerLogs?method=editLogMonitor">
								<input type="hidden" name="oldLogMonitorType"
									id="oldLogMonitorType" />
								<input type="hidden" name="oldExpression" id="oldExpression" />
								<tr>
									<td class="labeltext"><bean:message
											bundle="servermgrResources" key="logmonitor.type" /></td>
									<td class="labeltext"><select style="width: 100px"
										name="editLogMonitorType" id="editLogMonitorType">
											<option value="0">--Select--</option>
											<%
												if (registeredLogMonitorTypes != null
															&& registeredLogMonitorTypes.trim().length() > 0) {
														for (LogMonitorTypes type : LogMonitorTypes.values()) {
											%>
											<%
												if (registeredLogMonitorTypes.contains(type.getValue()))
											%>
											<option value="<%=type.getValue()%>"><%=type.getDisplayName()%></option>
											<%
												}
											%>
											<%
												}
											%>
									</select> <font color="#FF0000"> *</font></td>
								</tr>
								<tr>
									<td class="labeltext"><bean:message
											bundle="servermgrResources" key="logmonitor.expression" /></td>
									<td class="labeltext"><textarea rows="2" cols="50"
											name="editExpression" id="editExpression"></textarea><font
										color="#FF0000"> *</font></td>
									<select id="editPolicyKeys" hidden="true">
										<%
											for (PCRFKeyConstants keyConstants : PCRFKeyConstants
														.values(PCRFKeyType.RULE)) {
										%>
										<option value="<%=keyConstants.getVal()%>"><%=keyConstants.getVal()%></option>
										<%
											}
										%>
									</select>

								</tr>
								<tr>
								<tr class="labeltext">
									<td align="right" valign="top" colspan="1"></td>
									<td align="left" valign="top" colspan="2">&nbsp;<label
										class="small-text-grey"><bean:message
												bundle="descriptionResources"
												key="expression.possibleoperators" /></label>
									</td>
 
								</tr>
 
								<tr>
									<td class="labeltext"><bean:message
											bundle="servermgrResources" key="logmonitor.duration" /></td>
									<td class="labeltext"><input type="text"
										name="editDuration" maxlength="20" id="editDuration" size="25" /></td>
								</tr>
								<tr>
 
									<td class="labeltext" align="right">&nbsp;</td>
									<td class="labeltext" align="left"><input
										class="light-btn" type="button" name="Update" value="Update"
										onclick="return editTimeValidation();"> <input
										class="light-btn" type="button" name="cancel" value="Cancel"
										onclick="cancelCreateOrEdit();"></td>
								</tr>
							</html:form>
						</tbody>
					</table>
				</div>
				<div>
				<div align="left">
					<table align="left">
						<tr>
							<td><img
								src="<%=basePath%>/images/showme.png" class="showLogMonitors"
								alt="Edit" border="0" id="showLogMonitors" value="Show Monitors"
								onclick="showHideLogMonitorList(this);" /></td>
						</tr>
					</table>
				</div>

				<div id="logMonitorListDiv" style="display: block;">
					<html:form
						action="/initListNetServerLogs?method=deleteAllLogMonitors">
						<table style="width: 80%;" border="0" cellpadding="0"
							cellspacing="0">
							<thead>
								<tr>
									<td class="table-header" colspan="8"><bean:message
											bundle="servermgrResources" key="logmonitor.list" /></td>
								</tr>
								<tr class="vspace">
									<td class="btns-td" valign="middle" colspan="8"><input
										type="button" value="   Create   " tabindex="3"
										name="c_btnCreate" class="light-btn"
										onclick="createLogMonitor();" /> <input type="submit"
										tabindex="4" name="Delete"
										OnClick="return deleteAllLogMonitors();" value="   Delete   "
										class="light-btn"></td>
								</tr>
								<tr class="vspace">
									<td>
										<table style="padding-left: 1.6em" width="97%" border="0"
											cellpadding="0" cellspacing="0">
											<tr>
												<td align="center" class="tblheaderfirstcol" valign="top"
													width="1%"><input type="checkbox" name="toggleAll"
													value="checkbox" onclick="selectAllDataRow(this);" /></td>
												<td align="left" class="tblheader" valign="top" width="15%">
													<bean:message bundle="servermgrResources"
														key="logmonitor.type" />
												</td>
												<td align="left" class="tblheader" valign="top" width="20%">
													<bean:message bundle="servermgrResources"
														key="logmonitor.expression" />
												</td>
												<td align="left" class="tblheader" valign="top" width="10%">
													<bean:message bundle="servermgrResources"
														key="logmonitor.duration" />
												</td>
												<td align="center" class="tblheader" valign="top"
													width="20%"><bean:message bundle="servermgrResources"
														key="logmonitor.start.time" /></td>
												<td align="center" class="tblheader" valign="top"
													width="20%"><bean:message bundle="servermgrResources"
														key="logmonitor.expiry.time" /></td>
												<td align="center" class="tblheader" valign="top" width="5%">
													<bean:message key="general.edit" />
												</td>
												<td align="center" class="tblheaderlastcol" valign="top"
													width="5%"><bean:message key="general.delete" /></td>
											</tr>

											<logic:notEmpty name="listNetServerLogsForm"
												property="logMonitorDatas">
												<input type="hidden" name="totalLogMonitors"
													id="totalLogMonitors"
													value="<%=listNetServerLogsForm.getLogMonitorDatas().size()%>" />
												<%
													int i = 0;
												%>
												<logic:iterate id="logMonitorInfo"
													name="listNetServerLogsForm" property="logMonitorDatas"
													type="com.elitecore.netvertexsm.web.servermgr.server.LogMonitorData"
													indexId="counter">
													<tr id="dataRow" name="dataRow">
														<td align="left" class="tblfirstcol"><input
															type="checkbox" tabindex="8" name="dataRowCheckBox"
															value="<%=logMonitorInfo.getLogMonitorType()%>JOIN<%=logMonitorInfo.getExpression()%>"
															onclick="onOffHighlightedRow(<%=i++%>,this)" /></td>
														<td align="left" class="tblrows">
															<%
																for (LogMonitorTypes type : LogMonitorTypes.values()) {
																				if (logMonitorInfo.getLogMonitorType()
																						.equalsIgnoreCase(type.getValue())) {
															%>
															<%=type.getDisplayName()%> <%
 	}
 %> <%
 	}
 %>
														</td>
														<td align="left" class="tblrows"><bean:write
																name="logMonitorInfo" property="expression" /></td>
														<td align="left" class="tblrows"><bean:write
																name="logMonitorInfo" property="duration" /></td>
														<td align="left" class="tblrows"><bean:write
																name="logMonitorInfo" property="startTime" /></td>
														<td align="left" class="tblrows"><bean:write
																name="logMonitorInfo" property="expiryTime" /></td>
														<td align="center" class="tblrows"><a href="#"
															onclick="javascript:openEditLogMonitorDiv('<bean:write name="logMonitorInfo" property="logMonitorType" />','<bean:write name="logMonitorInfo" property="expression" />','<bean:write name="logMonitorInfo" property="duration" />');">
																<img src="<%=basePath%>/images/edit.jpg" alt="Edit"
																border="0">
														</a></td>
														<td align="center" class="tblrows"><a
															onclick="return confirm('Are you sure to delete <bean:write name="logMonitorInfo" property="logMonitorType" /> Log Monitor ?');"
															href="<%=basePath%>/initListNetServerLogs.do?method=deleteLogMonitor&logMonitorType=<bean:write name="logMonitorInfo" property="logMonitorType" />&expression=<bean:write name="logMonitorInfo" property="expression" />&duration=<bean:write name="logMonitorInfo" property="duration" />"
															tabindex="8"> <img
																src="<%=basePath%>/images/delete.jpg" alt="Delete"
																border="0">
														</a></td>
													</tr>
												</logic:iterate>
											</logic:notEmpty>
											<logic:empty name="listNetServerLogsForm"
												property="logMonitorDatas">
												<tr>
													<td colspan="8" align="center" class="tblfirstcol"><bean:message
															bundle="datasourceResources"
															key="database.datasource.norecordfound" /></td>
												</tr>
											</logic:empty>
										</table>
									</td>
								</tr>
							</thead>
						</table>
					</html:form>
				</div></div>
				<div class="small-gap">&nbsp;</div>
				<div class="small-gap">&nbsp;</div>
				<div class="small-gap">&nbsp;</div>
				<div class="small-gap">&nbsp;</div>

				<hr />

				<div id="mainParentDiv"
					style="padding-left: 2.0em; display: block; overflow: scroll;; height: 600px; width: 100%;"
					class="labeltext">
					<div id='serverLogsParent' width="100%" border="0">
						<div id='serverLogs' width="100%" border="0"></div>
						<div id="progressDiv" align="bottom">
							<%if(listNetServerLogsForm.getLogMonitorDatas()!=null && listNetServerLogsForm.getLogMonitorDatas().size()>0){%>
								<img align="bottom" alt="Fetching Logs" src="${pageContext.request.contextPath}/images/progress.gif" />
							<%}%>
						</div>
					</div>
				</div>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="3" class="small-gap">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="2" valign="top"><%@ include
				file="/jsp/core/includes/common/Footerbar.jsp"%>
		</td>
	</tr>

</table>
