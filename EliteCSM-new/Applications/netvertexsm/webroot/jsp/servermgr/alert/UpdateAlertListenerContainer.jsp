<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.alert.data.AlertListenerTypeData"%>
<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.alert.data.BaseAlertListener"%>
<%@page import="com.elitecore.netvertexsm.util.constants.InstanceTypeConstants"%>
<link rel="stylesheet" href="jquery/css/jquery.treetable.css" />
<link rel="stylesheet" href="jquery/css/jquery.treetable.theme.default.css" />
<script src="jquery/libs/jquery/jquery.treetable.js"></script>



<%
    AlertListenerData alertListenerData =(AlertListenerData)request.getAttribute("alertListenerData");
    AlertListenerTypeData  alertListenertypeData = alertListenerData.getAlertListenerTypeData();
    request.setAttribute("alertListenertypeData",alertListenertypeData);
 	
%>
<%!public void drawTree(JspWriter out,AlertTypeData alertData,List<String> selectedAlertsType,List<String> selectedFloodControl) throws Exception {
	if(alertData.getType().equalsIgnoreCase("L")){
		if(selectedAlertsType.contains(alertData.getAlertTypeId())){
	     	    out.write("<tr data-tt-id="+alertData.getAlertTypeId()+" data-tt-parent-id="+alertData.getParentId()+" class='dataRow'>"
	     	                 +"<td align='left' class='tblfirstcol'>"+alertData.getName()+"</td>"
	     	                 +"<td align='center' class='tblrows'><input type='checkbox' name='alertEnable' value="+alertData.getAlertTypeId()+" checked></td>");
	       if(selectedFloodControl.contains(alertData.getAlertTypeId())){
		       out.write("<td align='center' class='tblrows'><input type='checkbox' name='floodcontrolenable' value="+alertData.getAlertTypeId()+" checked></td></tr>");
		     }else{
			   out.write("<td align='center' class='tblrows'><input type='checkbox' name='floodcontrolenable' value="+alertData.getAlertTypeId()+" ></td></tr>");
		  }
	   }else{
		 out.write("<tr data-tt-id="+alertData.getAlertTypeId()+" data-tt-parent-id="+alertData.getParentId()+" class='dataRow'><td align='left' class='tblfirstcol'>"+alertData.getName()+"</td><td align='center' class='tblrows'><input type='checkbox' name='alertEnable' value="+alertData.getAlertTypeId()+"></td><td align='center' class='tblrows'><input type='checkbox' name='floodcontrolenable' value="+alertData.getAlertTypeId()+"></td></tr>");
	}
}else{
		Set<AlertTypeData> childList = alertData.getNestedChildDetailList();
		Iterator<AlertTypeData> childListIterator = childList.iterator();
		while(childListIterator.hasNext()) {
			AlertTypeData childAlertData = (AlertTypeData)childListIterator.next();
			if(childAlertData.getType().equalsIgnoreCase("P")){
				if(selectedAlertsType.contains(childAlertData.getAlertTypeId())){
					     out.write("<tr data-tt-id="+childAlertData.getAlertTypeId()+" data-tt-parent-id="+childAlertData.getParentId()+" class='dataRow'>"
					                +"<td align='left' class='tblfirstcol'><b>"+childAlertData.getName()+"</b></td>"
					                +"<td align='center' class='tblrows'><input type='checkbox' name='alertEnable' value="+childAlertData.getAlertTypeId()+" checked></td>");
					  if(selectedFloodControl.contains(childAlertData.getAlertTypeId())){
						out.write("<td align='center' class='tblrows'><input type='checkbox' name='floodcontrolenable' value="+childAlertData.getAlertTypeId()+" checked></td></tr>");
					    }else{
					    	out.write("<td align='center' class='tblrows'><input type='checkbox' name='floodcontrolenable' value="+childAlertData.getAlertTypeId()+"></td></tr>");	
					    }
				}else{
						 out.write("<tr data-tt-id="+childAlertData.getAlertTypeId()+" data-tt-parent-id="+childAlertData.getParentId()+" class='dataRow'><td align='left' class='tblfirstcol'><b>"+childAlertData.getName()+"</b></td><td align='center' class='tblrows'><input type='checkbox' name='alertEnable' value="+childAlertData.getAlertTypeId()+"></td><td align='center' class='tblrows'><input type='checkbox' name='floodcontrolenable' value="+childAlertData.getAlertTypeId()+"></td></tr>");
				
				}
			}
			drawTree(out,childAlertData,selectedAlertsType,selectedFloodControl);
		}
	}
}%>
<script type="text/javascript">
$(document).ready(function(){
	setTitle('<bean:message bundle="servermgrResources" key="servermgr.alert.alertlistener"/>');
});
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
  
  <tr>
    <td width="10" class="small-gap">&nbsp;</td>	
    	<td width="85%" class="box" >
    		<table width="100%" border="0" cellspacing="0" cellpadding="0">
    		    <%if("ALT0001".equalsIgnoreCase(alertListenerData.getTypeId())){
    		    	BaseAlertListener alertFileListenerData = alertListenerData.getAlertListener();
    		    	request.setAttribute("alertFileListenerData",alertFileListenerData);
    		    	%>
				<tr>
					<td valign="top">
						<%@ include file="ViewFileAlertListener.jsp"%>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<%@ include file="UpdateFileAlertListener.jsp"%>
					</td>
				</tr>
				<%}else if("ALT0002".equalsIgnoreCase(alertListenerData.getTypeId())){
					BaseAlertListener alertTrapListenerData = alertListenerData.getAlertListener();
    		    	request.setAttribute("alertTrapListenerData",alertTrapListenerData);
    			%>
				<tr>
					<td valign="top">
						<%@ include file="ViewTrapAlertListener.jsp"%>
					</td>
				</tr>
				<tr>
					<td valign="top">
						<%@ include file="UpdateTrapAlertListener.jsp"%>
					</td>
				</tr>
				<%} %>
    		</table>
        </td>	
		<td width="15%" class="grey-bkgd" valign="top">
			<%@  include file="AlertListenerNavigation.jsp"%>
		</td>
  </tr>
  <%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>
<script>
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

</script>
