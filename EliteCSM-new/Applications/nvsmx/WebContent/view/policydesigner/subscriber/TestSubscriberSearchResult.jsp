<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@page import="com.google.gson.JsonArray"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<% JsonArray testSubscribers = (JsonArray)request.getAttribute(Attributes.TEST_SUBSCRIBERS);
%>
<style>
.form-inline .form-group {
    display: inline-block !important;
    margin-bottom: 0 !important;
    vertical-align: middle !important;
}
.form-inline .form-control {
    display: inline-block !important;
    vertical-align: middle !important;
    width: auto !important; 
}
</style>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="subscribers.testsubscribers"/> </h3>		
	</div>
	<div class="panel-body table-responsive"  > 

		<div class="row" style="margin-bottom: 10px;">
			<div class="col-xs-2">
			<span class="btn-group btn-group-sm">
				<span class="btn-group btn-group-sm" onmousedown="return removeRecords(this);" data-href="javascript:removeData();">
					<button id="btnDelete" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="delete" role="button">
						<span class="glyphicon glyphicon-trash" title="delete"></span>
					</button>
				</span>
			</span>
			</div>
			<div class="col-xs-10" >
			<s:form action="policydesigner/subscriber/Subscriber/addTestSubscriber" id="addTestSubscriberForm" cssClass="form-inline" cssStyle="float: right" >
				<s:textfield cssClass="form-control" placeholder="Subscriber Identity" id="subscriberIdentity" name="subscriberIdentity"/>
				<s:submit type="button" name="Create" cssClass="btn btn-primary" value=""><span class="glyphicon glyphicon-plus"></span></s:submit>
			</s:form>
			</div>
		</div>
		<s:form id="testSubscriberSearchForm">	

		
		<nv:dataTable  list="<%=testSubscribers.toString()%>" cssClass="display table table-blue"
						id="testSubscriber" 
						width="100%" 
						showPagination="true"
						showInfo="true"
						>	
			<nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="value" style="width:10px;" />
			<nv:dataTableColumn id="subscriberIdentity" title="Subscriber Identity" beanProperty="value"  hrefurl="${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/view?subscriberIdentity=value"	/>
			<nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>"  hrefurl="edit:${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/initUpdate?subscriberIdentity=value" style="width:20px;border-right:0px;" />
			<nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>"  hrefurl="delete:${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/removeTestSubscriber?subscriberIdentity=value" style="width:20px;"  />
		</nv:dataTable>
		</s:form>
	</div>
	
</div>

<script type="text/javascript">

function getSelectedValues(){
	var isDataSelected = false;
	var selectedDatas = document.getElementsByName("ids");
	 
    for (var i=0; i < selectedDatas.length; i++){
    	 if(selectedDatas[i].name == 'ids'){ 
            if(selectedDatas[i].checked == true){
            	isDataSelected = true;
             }
        }
    }
    return isDataSelected;
}

function removeRecords(obj) {
	var selectVar = getSelectedValues();
    if(selectVar == false){
        return addWarning(".popup","At least select one Subscriber for delete");
    }else{
		var flag = false;
		flag = deleteConfirmMsg(obj,"Delete test subscriber ?");
    	if(flag==true){
    		removeData();
    	}
		return flag;
    }
}   


function removeData(){
	document.forms["testSubscriberSearchForm"].action = "removeTestSubscribers";
	document.forms["testSubscriberSearchForm"].submit();
}


</script>
 
