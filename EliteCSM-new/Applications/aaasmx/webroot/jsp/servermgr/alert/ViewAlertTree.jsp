<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertListenerTypeData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.BaseAlertListener"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.alert.data.AlertTypeData"%>



<% 
    String[] selectedAlertsType = (String[]) request.getAttribute("selectedAlertTypes");;
	List<AlertTypeData> alertTypeDataList = (List<AlertTypeData>) request.getAttribute("alertTypeDataList");
%>


<table width="100%" border="0" cellspacing="0" cellpadding="0"  >
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%" valign="top"><bean:message bundle="servermgrResources" key="servermgr.alert.enabledalerts" /></td>
					<td align="left" colspan="2" class="labeltext">
						<logic:notEmpty name="selectedAlertTypes" >
						<ul id="example">
							<% 
                             	for(int i=0;i<alertTypeDataList.size();i++) {
                                	AlertTypeData data = alertTypeDataList.get(i);
                                  	if(data.getType().equalsIgnoreCase("P")){
                                  		out.println("<li class='hideData'><b>"+data.getName()+"</b>"+"<ul>");                                  		
                                  	}
                                  	drawTree(out,data,selectedAlertsType);
                                  	if(data.getType().equalsIgnoreCase("P")){
                                  		out.println("</ul></li>");
                                  	}                                  	
                                 }
                             %>
						</ul>
						</logic:notEmpty>
						<logic:empty name="selectedAlertTypes" >
							No Alerts Enabled
						</logic:empty>
						</td>
			</tr>
</table>



<%! 
public void drawTree(JspWriter out,AlertTypeData alertData,String[] selectedAlertsType) throws Exception {
	boolean flag = false;
	if(selectedAlertsType != null && selectedAlertsType.length > 0) {
		for(int i=0;i<selectedAlertsType.length;i++) {
			String alertId = selectedAlertsType[i];
			if((alertData.getAlertTypeId()).equalsIgnoreCase(alertId))
				flag = true;
		}
	}
	if(alertData.getType().equalsIgnoreCase("L")) {
		if(flag)
			out.write("<li class=checkedData>"+alertData.getName()+"</li>");
		else
			out.write("<li class='hideData'>"+alertData.getName()+"</li>");
	}else{
		Set<AlertTypeData> childList = alertData.getNestedChildDetailList();
		Iterator<AlertTypeData> childListIterator = childList.iterator();
		while(childListIterator.hasNext()) {
			AlertTypeData childAlertData = (AlertTypeData)childListIterator.next();
			if(childAlertData.getType().equalsIgnoreCase("P")){
				out.write("<li class=hideData> <b>" + childAlertData.getName() + "</b>" + "<ul>");
			}
			drawTree(out,childAlertData,selectedAlertsType);
			if(childAlertData.getType().equalsIgnoreCase("P")){
			out.write("</li></ul>");
			}
		}
	}
}
%>

<script>
$(document).ready(function(){
	$('ul#example').collapsibleCheckboxTree('expanded');
	if($("#expand").length > 0){
		$("#expand").addClass("light-btn");
		$("#expand").after("&nbsp;");
		$("#collapse").addClass("light-btn");
	} 
	$(".checkedData").each(function(index,item){
			//alert($(this).html());
			$(this).removeClass("hideData");
			$(this).addClass("ViewData");
			setParentData(this);
		
		
	});
	//Hide Data 
	$(".hideData").hide();	
	//$.trigger($("#expand").click());
});

function setParentData(data){
	if($(data).parent().parent('li').length > 0){
		$(data).parent().parent('li').removeClass("hideData");
		$(data).parent().parent('li').addClass("ViewData");
		setParentData($(data).parent().parent('li'));
	}
}


</script>          