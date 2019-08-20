<%@page import="com.elitecore.netvertexsm.util.constants.AlertListenerConstant"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>


<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertTypeData"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
<%@page import="java.lang.String"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.Writer"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.util.Iterator"%>
<link rel="stylesheet" href="jquery/css/jquery.treetable.css" />
<link rel="stylesheet" href="jquery/css/jquery.treetable.theme.default.css" />
<script src="jquery/libs/jquery/jquery.treetable.js"></script>
<script type="text/javascript">
jQuery(document).ready(function(){
		setTitle('<bean:message bundle="servermgrResources" key="servermgr.alert.filealertlistener"/>');
		
});
</script>

<%
	List<AlertTypeData> alertTypeDataList = (List<AlertTypeData>) request.getAttribute("alertTypeDataList");
%>

<%!public void drawTree(JspWriter out,AlertTypeData alertData) throws Exception {
	if(alertData.getType().equalsIgnoreCase("L")) {
		out.write("<tr data-tt-id="+alertData.getAlertTypeId()+" data-tt-parent-id="+alertData.getParentId()+" class='dataRow'><td align='left' class='tblfirstcol'>"+alertData.getName()+"</td><td align='center' class='tblrows'><input type='checkbox' name='alertEnable' value="+alertData.getAlertTypeId()+"></td><td align='center' class='tblrows'>");
		if(alertData.getFloodControl().equalsIgnoreCase(AlertListenerConstant.FLOODCONTROL_ENABLE)){
			out.println("<input type='checkbox' name='floodcontrolenable' value="+alertData.getAlertTypeId()+" checked>");
		}else {
			out.println("<input type='checkbox' name='floodcontrolenable' value="+alertData.getAlertTypeId()+" >");
		}
		out.println("</td></tr>");
	}else{
		Set<AlertTypeData> childList = alertData.getNestedChildDetailList();
		Iterator<AlertTypeData> childListIterator = childList.iterator();
		while(childListIterator.hasNext()) {
			AlertTypeData childAlertData = (AlertTypeData)childListIterator.next();
			if(childAlertData.getType().equalsIgnoreCase("P")){
				 out.write("<tr data-tt-id="+childAlertData.getAlertTypeId()+" data-tt-parent-id="+childAlertData.getParentId()+" class='dataRow'><td align='left' class='tblfirstcol'><b>"+childAlertData.getName()+"</b></td><td align='center' class='tblrows'><input type='checkbox' name='alertEnable' value="+childAlertData.getAlertTypeId()+"></td><td align='center' class='tblrows'>");
				 if(childAlertData.getFloodControl().equalsIgnoreCase(AlertListenerConstant.FLOODCONTROL_ENABLE)){
				 	out.println("<input type='checkbox' name='floodcontrolenable' value="+childAlertData.getAlertTypeId()+" checked >");
				 }else{
					 out.println("<input type='checkbox' name='floodcontrolenable' value="+childAlertData.getAlertTypeId()+">");
				 }
				 out.println("</td></tr>"); 
			}
			  drawTree(out,childAlertData);
			}
	}
}%>



<script>
function validateCreate(){	
	 if(isNull(document.forms[0].fileName.value)){
		document.forms[0].fileName.focus();
		alert('File name must be specified');
     }else if(document.forms[0].rollingType[1].checked && !isNumber(document.forms[0].rollingUnitSizedBased.value)){
  		 alert('Rolling Unit must be Numeric.');
     }else if(document.forms[0].rollingType[1].checked && !isNumber(document.forms[0].maxRollingUnitSizedBased.value)){
    	 alert('Max Rolled Unit must be Numeric.');
     }else if(isAlertConfigured()==false){
         alert('Atleast one alert must be selected');
   	}else{
         if(document.forms[0].rollingType[0].checked){
             document.forms[0].rollingUnit.value=document.forms[0].rollingUnitTimeBased.value;
             document.forms[0].maxRollingUnit.value=document.forms[0].maxRollingUnitTimeBased.value;  
         }else if(document.forms[0].rollingType[1].checked){
        	 document.forms[0].rollingUnit.value=document.forms[0].rollingUnitSizedBased.value;
             document.forms[0].maxRollingUnit.value=document.forms[0].maxRollingUnitSizedBased.value;
         }
         document.forms[0].submit();         
	}
}	

function isAlertConfigured(){
	var isAlertConfigured=false;
	$("input[name=alertEnable]").each(function(){
                 if(this.checked){
                	 isAlertConfigured=true;
                     return false;
                 }
 		});
		return isAlertConfigured;
}

function isNumber(val){
	nre= /^\d+$/;
	var regexp = new RegExp(nre);
	if(!regexp.test(val))
	{
		return false;
	}
	return true;
}

function initialized(){

	   document.getElementById('sizedBasedRollingUnit').style.display='none';
	   document.getElementById('sizedBasedMaxRolledUnit').style.display='none';
	   document.getElementById('timeBasedRollingUnit').style.display='';
	   document.getElementById('timeBasedMaxRolledUnit').style.display='none';
	   document.forms[0].rollingType[0].checked=true;	   

	   }
function setTimeBased(){

	document.getElementById('sizedBasedRollingUnit').style.display='none';
	document.getElementById('sizedBasedMaxRolledUnit').style.display='none';
	document.getElementById('timeBasedRollingUnit').style.display='';
	document.getElementById('timeBasedMaxRolledUnit').style.display='none';
	
}

function setSizeBased(){

	document.getElementById('sizedBasedRollingUnit').style.display='';
	document.getElementById('sizedBasedMaxRolledUnit').style.display='';
	document.getElementById('timeBasedRollingUnit').style.display='none';
	document.getElementById('timeBasedMaxRolledUnit').style.display='none';
	
}

</script>

<html:form action="/createAlertListener">
<html:hidden name="createAlertListenerForm" property="typeId" value=""/>
<html:hidden name="createAlertListenerForm" property="rollingUnit" />
<html:hidden name="createAlertListenerForm" property="maxRollingUnit" />

<table cellpadding="0" cellspacing="0" border="0" width="100%">	
	
		<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
		<tr>
			<td width="10">
				&nbsp;
			</td>
			<td class="box" cellpadding="0" cellspacing="0" border="0" width="100%">
				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td class="table-header" colspan="5">
							 <bean:message bundle="servermgrResources" key="servermgr.alert.filealertlistener"/>
						</td>
					</tr>
					<tr>
						<td class="small-gap" colspan="2">
							&nbsp;
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle" colspan="2">
							<table cellpadding="0" cellspacing="0" border="0" width="100%" height="30%">
								
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">
										<bean:message bundle="servermgrResources" key="servermgr.alert.filename"/>
										
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="filealertlistener.filename"/>','FileName')"  />										
								
									</td>
									<td align="left" class="labeltext" valign="top" colspan="2" width="82%">
										<html:text styleId="fileName" property="fileName" size="25" maxlength="100" />
										<font color="#FF0000"> *</font>
										
									</td>
								</tr>
								
								<tr>
								<td class="small-gap">&nbsp;</td>
								</tr>
								
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">
								       <bean:message bundle="servermgrResources" key="servermgr.alert.rollingtype"/>
									<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="filealertlistener.rolling"/>','Rolling Type')"  />											
									</td>
									<td align="left" class="labeltext" valign="top" >
				    					<html:radio  styleId="rollingType" property="rollingType" onclick="setTimeBased();" value="1"/>Time-Based
							            <html:radio  styleId="rollingType" property="rollingType" onclick="setSizeBased();" value="2"/>Size-Based
										      
									</td>
								</tr>
								
								<tr>
								<td class="small-gap">&nbsp;</td>
								</tr>
								
								
								
								<!-- Size Based selected ,display below portion -->
								<tr id="sizedBasedRollingUnit">
									<td align="left" class="labeltext" valign="top" width="18%">
									<bean:message bundle="servermgrResources" key="servermgr.alert.rollingunitkbs"/>
									<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="filealertlistener.rollingunit"/>','Rolling Unit')"  />										
									</td>
									<td align="left" class="labeltext" valign="top" >
				    				    <html:text styleId="rollingUnitSizedBased" property="rollingUnitSizedBased" size="25" maxlength="100" />
										<font color="#FF0000"> *</font>      
									</td>
								</tr>
								<tr>
								<td class="small-gap">&nbsp;</td>
								</tr>
								<tr id="sizedBasedMaxRolledUnit">
									<td align="left" class="labeltext" valign="top" width="18%">
											<bean:message bundle="servermgrResources" key="servermgr.alert.maxrollingunit"/>
											<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="filealertlistener.maxrollunit"/>','Max Rolled Unit')"  />										
										
									</td>
									<td align="left" class="labeltext" valign="top" >
				    				    <html:text styleId="maxRollingUnitSizedBased" property="maxRollingUnitSizedBased" size="25" maxlength="100" />
										<font color="#FF0000"> *</font>      
									</td>
								</tr>
								<!-- end -->
								<!-- Time Based selected ,display below portion -->
								<tr id="timeBasedRollingUnit">
									<td align="left" class="labeltext" valign="top" width="18%">
											<bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit"/>
									<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="filealertlistener.rollingunittime"/>','Rolling Unit')"  />										
									
									</td>
								
									<td align="left" class="labeltext" valign="top" >
			    				 
				   					    <html:select  styleId="rollingUnitTimeBased" property="rollingUnitTimeBased" value="5" size="1">
										   <html:option value="3" ><bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit.minute"/></html:option>
										   <html:option value="4" ><bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit.hour"/></html:option>
										   <html:option value="5" ><bean:message bundle="servermgrResources" key="servermgr.alert.rollingunit.daily"/></html:option>
					   					</html:select><font color="#FF0000"> *</font>      
							      </td>
								</tr>
								<tr>
								<td class="small-gap">&nbsp;</td>
								</tr>
								<tr id="timeBasedMaxRolledUnit">
									<td align="left" class="labeltext" valign="top" width="18%">
											<bean:message bundle="servermgrResources" key="servermgr.alert.maxrollingunit"/>
									</td>
									<td align="left" class="labeltext" valign="top" >
				    				    <html:text styleId="maxRollingUnitTimeBased" property="maxRollingUnitTimeBased" size="25" maxlength="100" value="0"/>
										<font color="#FF0000"> *</font>      
									</td>
								</tr>
								
								
								<tr>
								<td class="small-gap">&nbsp;</td>
								</tr>
								
								<tr>
									<td align="left" class="labeltext" valign="top" width="18%">
										  <bean:message bundle="servermgrResources" key="servermgr.alert.comprolledunit"/>		
										<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="filealertlistener.compressrollunit"/>','Compress Rolled Unit')"  />										
										
									</td>
									<td align="left" class="labeltext" valign="top" >
				    				    <html:select  styleId="compRollingUnit" property="compRollingUnit" size="1" value="false">
										   <html:option value="true" >True</html:option>
					   					   <html:option value="false" >False</html:option>
										</html:select><font color="#FF0000"> *</font>      
									</td>
								</tr>
							
   							 <tr>
				   		            <td align="left" class="labeltext" valign="top" >&nbsp;</td>
						           <td align="left" class="labeltext" valign="top" colspan="2" >&nbsp;</td>
				        	   </tr>
		    			   <tr> 
							  <td align="left" class="labeltext" valign="top" >&nbsp;</td>
							  <td align="left" class="labeltext" valign="top" colspan="2" >&nbsp;</td>
						   </tr>
						
							<tr>
								<td class="tblheader-bold" align="left" colspan="7">								
									<bean:message bundle="servermgrResources" key="servermgr.alert.alertslist"/>
								<img src="<%=basePath%>/images/help-hover.jpg" height="12" width="12" style="cursor: pointer" onclick="parameterDescription('<bean:message bundle="descriptionResources" key="filealertlistener.alertlist"/>','Alert List')"  />										
										
								</td>
							</tr>
							<tr>
						     <td align="left" colspan="3" class="labeltext">
						     <table width="50%" cellspacing="0" cellpadding="0" border="0" id="alerttable">
						     <thead>
								<tr>
									<td align="center" class="tblheader-bold" valign="top"><bean:message bundle="servermgrResources" key="servermgr.alert.alertname"/></td>
									<td align="center" class="tblheader-bold" valign="top" width="*"><bean:message bundle="servermgrResources" key="servermgr.alert.enable"/></td>
									<td align="center" class="tblheader-bold" valign="top" width="*"><bean:message bundle="servermgrResources" key="servermgr.alert.floodcontrol"/></td>
								</tr>
						     </thead>
						     <tbody>
                                  <%
                                  	for(int i=0;i<alertTypeDataList.size();i++) {
                                                                    		AlertTypeData data = alertTypeDataList.get(i);
                                                                    		if(data.getType().equalsIgnoreCase("P")){
                                                                    			out.println("<tr data-tt-id="+data.getAlertTypeId()+" data-tt-parent-id="+data.getParentId()+" class='dataRow'><td align='left' class='tblfirstcol'><b>"+data.getName()+"</b></td><td align='center' class='tblrows'><input id='Netvertex-PCRF-Alerts-Enable' type='checkbox' name='alertEnable' value="+data.getAlertTypeId()+"></td><td align='center' class='tblrows'>&nbsp;");
                                                                    			if(data.getFloodControl().equalsIgnoreCase("1")){		
                                                                    				out.println("<input id='Netvertex-PCRF-Alerts-FloodControl' type='checkbox' name='floodcontrolenable' value="+data.getAlertTypeId()+" checked>");
                                                                    			}else{
                                                                    				out.println("<input id='Netvertex-PCRF-Alerts-FloodControl' type='checkbox' name='floodcontrolenable' value="+data.getAlertTypeId()+">");
                                                                    			}
                                                                    			 out.println("</td></tr>");                                  		
                                                                    		}
                                                                    	    drawTree(out,data);
                                                                    		                                  	
                                                                    	}
                                  %>
                             </tbody>  
                            </table>
						    </td>
						   </tr>
								
								<tr>
									<td class="btns-td" valign="middle">
										&nbsp;
									</td>
									<td class="btns-td" valign="middle" colspan="2">
										<input type="button" name="c_btnCreate" onclick="validateCreate()" id="c_btnCreate2" value="Create" class="light-btn">
									 	<input type="reset" name="c_btnCancel" onclick="javascript:location.href='<%=basePath%>/initSearchAlertListener.do?/>'" value="Cancel" class="light-btn"> 
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>		
</html:form>
<script>
initialized();

	$("#alerttable").treetable({
		expandable : true,
		clickableNodeNames:true,
		indent:15
	});
	$("tr.dataRow").hover(function() {
		$(this).css("background", "#D9E6F6");
	}, function() {
		$(this).css("background", "");
	});
    $("input[name=alertEnable]").click(function(){
              selectchildcheckbox($(this),this.checked,1);
              //selectparentcheckbox($(this),this.checked,1);
              if(!this.checked){
              	$(this).parent().parent().children("td").eq(2).children().eq(0).prop('checked',false);
              	selectchildcheckbox($(this),false,2);
              }
     });
   
    $("input[name=floodcontrolenable]").click(function(){
        selectchildcheckbox($(this),this.checked,2);
        if(this.checked){
        	$(this).parent().parent().children("td").eq(1).children().eq(0).prop('checked',true);
        	selectchildcheckbox($(this),true,1);
        }
});     
	function selectchildcheckbox(currentSelectedBox,isChecked,columnno){
		 var parentrowid=currentSelectedBox.val();
		 var childcheckbox;
	      currentSelectedBox.closest("tr").siblings("tr[data-tt-parent-id="+parentrowid+"]").each(function(){
	    	  childcheckbox=$(this).children("td").eq(columnno).children().eq(0);
                    if(isChecked){        
                      childcheckbox.prop('checked',true);
                     }else{
                      childcheckbox.prop('checked',false);
                     }
                    selectchildcheckbox(childcheckbox,childcheckbox.prop('checked'),columnno);
        });
   }

	function selectparentcheckbox(currentrow,isChecked,columnno){
		var currentrowid=currentrow.parent().parent().prop('data-tt-parent-id');
		var parentCheckBox;
		currentrow.parent().parent().siblings("tr[data-tt-id="+currentrowid+"]").each(function(){
			      parentCheckBox=$(this).children("td").eq(columnno).children().eq(0);
		          if(isChecked){
		             parentCheckBox.prop('checked',true);
		           }/* else{
		        	   parentCheckBox.prop('checked',false);
		           } */
		          selectparentcheckbox(parentCheckBox,parentCheckBox.prop('checked'),columnno);
		});
	}
</script>


