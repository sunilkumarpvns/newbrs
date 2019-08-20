<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags/dialog" prefix="nvx" %>
<%
	int rows = ConfigurationProvider.getInstance().getPageRowSize();
	
%>
<%
String criteriaJson = (String)request.getAttribute(Attributes.CRITERIA);
%>
 
<script type="text/javascript">

function getSelectedValues(){
	var selectedData = false;
	var selectedDatas = document.getElementsByName("ids");
	 
    for (var i=0; i < selectedDatas.length; i++){
    	 if(selectedDatas[i].name == 'ids'){ 
            if(selectedDatas[i].checked == true){
            	selectedData = true;
             }
        }
    }
    return selectedData;
}

function removeRecords(obj) {
	var selectVar = getSelectedValues();
    if(selectVar == false){
		return addWarning(".popup","At least select one template for delete");
    }else{
		var flag = false;
		flag = deleteConfirmMsg(obj,"Delete Selected Notifications ?");
    	if(flag==true){
    		removeData();
    	}
		return false;
    }
}   

function createNotificationTemplate(){
	document.forms["notificationTemplateSearchResult"].action = "${pageContext.request.contextPath}/policydesigner/notification/NotificationTemplate/init";
	document.forms["notificationTemplateSearchResult"].submit();
}

function removeData(){
	document.forms["notificationTemplateSearchResult"].action = "${pageContext.request.contextPath}/policydesigner/notification/NotificationTemplate/delete";
	document.forms["notificationTemplateSearchResult"].submit();
}
var jsonString = <%=criteriaJson%>;
if(isNullOrEmpty(jsonString) == false){
	var criteria = jsonString;
}

</script>

<s:form  id="notificationTemplateSearchResult" method="post" cssClass="form-vertical">
	<div class="panel panel-primary">
		<div class="panel-heading">
			<h3 class="panel-title">
				<s:text name="Notification Template Search" />
			</h3>
		</div>

		<div class="panel-body">				
			<span class="btn-group btn-group-sm">
				<button class="btn btn-default" role="button" id="btnAddTemplate" onclick="createNotificationTemplate();" >
					<span class="glyphicon glyphicon-plus" title="Add"></span>
				</button>
						
				<span class="btn-group btn-group-sm" onmousedown="return removeRecords(this);" data-href="javascript:removeData();">
					<button id="btnDeleteTemplate" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="delete" role="button">
						<span class="glyphicon glyphicon-trash" title="delete"></span>
					</button>
				</span>
			</span>
		<nv:dataTable 							
						id="notificationTemplateData"
						beanType="com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData"
						actionUrl="/genericSearch/policydesigner/notification/NotificationTemplate/searchData" 
						rows="<%=rows%>"
						subTableUrl=""
						width="100%" 
						showPagination="true"
						cssClass="table table-blue">	
			<nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="id" style="width:5px !important;" />			
			<nv:dataTableColumn title="Name" 	beanProperty="name"  hrefurl="${pageContext.request.contextPath}/policydesigner/notification/NotificationTemplate/view?notificationTemplateId=id"		 sortable="true" />
			<nv:dataTableColumn title="Subject"       beanProperty="subject"  		sortable="true" />
			<nv:dataTableColumn title="Template Type" beanProperty="templateType" 			sortable="true" />
			<nv:dataTableColumn title="" 			 icon="<span class='glyphicon glyphicon-pencil'></span>"	hrefurl="edit:${pageContext.request.contextPath}/policydesigner/notification/NotificationTemplate/init?notificationTemplateId=id&groupIds=groups" style="width:20px;border-right:0px;"  />
			<nv:dataTableColumn title="" 			 icon="<span class='glyphicon glyphicon-trash'></span>" 	hrefurl="delete:${pageContext.request.contextPath}/policydesigner/notification/NotificationTemplate/delete?ids=id" 	 style="width:20px;"  />
		</nv:dataTable>
		
	</div>
</div>
</s:form>