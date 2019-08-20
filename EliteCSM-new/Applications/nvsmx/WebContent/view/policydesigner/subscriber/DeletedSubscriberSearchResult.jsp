<%@page import="com.elitecore.commons.base.Strings"%>
<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<%
	String criteria = (String)request.getAttribute(Attributes.CRITERIA);
%>
<script type="text/javascript">
	var criteria = <%=criteria%>;
</script>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="subscriber.deletedsubscribers"/> </h3>		
	</div>
	<div class="panel-body table-responsive"  > 

		<span class="btn-group btn-group-sm">
			
				
			<div class="btn-group btn-group-sm">
				<span class="btn-group btn-group-sm"  onmousedown="return restoreRecords(this);" data-href="javascript:restoreSubscribers();">
					<button id="btnRestore" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="Restore" role="button">
						<span class="glyphicon glyphicon-retweet" title="Restore"></span>
					</button>
				</span>
  				<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" data-target="#restoreAll" aria-expanded="false">
    				<span class="caret"></span>
    				<span class="sr-only">Toggle Dropdown</span>
  				</button>
  				<span id="restoreAll">
 					<ul class="dropdown-menu" >
    					<li><a href="javascript:location.href='${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/restoreAll'">Restore All</a></li>
				 	</ul>
				</span>
				<span class="btn-group btn-group-sm"  onclick="return removeRecords(this);" onmousedown="return removeRecords(this);" data-href="javascript:purgeSubscribers();">
					<button id="btnDelete" type="button" class="btn btn-default" data-toggle="tooltip" data-placement="right" title="Purge" role="button">
						<span class="glyphicon glyphicon-trash" title="delete"></span>
					</button>
				</span>
  				<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" data-target="#purgeAll" aria-haspopup="true" aria-expanded="false">
    				<span class="caret"></span>
    				<span class="sr-only">Toggle Dropdown</span>
  				</button>
  				<span id="purgeAll">
 				<ul class="dropdown-menu">
    				<li><a href="javascript:location.href='${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/purgeAll'"><s:text name="subscriber.purge.all"></s:text></a></li>
		 		</ul>
		 		</span>
				</div>
				
				
		</span>
		
	<s:form id="deleteSubscriberSearchForm" >
		<nv:dataTable
				id="deletedSubscriberTable"
				actionUrl="/searchTable/policydesigner/subscriber/Subscriber/searchDeletedSubscriber"
				beanType="com.elitecore.corenetvertex.spr.data.SPRInfo"
				subTableUrl=""
				width="100%"
				showPagination="true"
				cssClass="table table-blue">
		<nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="subscriberIdentity" style="width:10px;" />
			<nv:dataTableColumn title="Subscriber Identity" beanProperty="subscriberIdentity"  hrefurl="${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/viewDeletedProfile?subscriberIdentity=subscriberIdentity"	/>
			<nv:dataTableColumn title="Name" 		 		beanProperty="userName" />
			<nv:dataTableColumn title="Package"           	beanProperty="dataPackage" 	/>
			<nv:dataTableColumn title="Customer Type"       beanProperty="customerType" 	/>
			<nv:dataTableColumn title="Status"           	beanProperty="status" 	/>
			<nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-retweet'></span>"  hrefurl="edit:${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/restoreSubscriber?subscriberIdentity=subscriberIdentity" style="width:20px;border-right:0px;"  />
			<nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>"  hrefurl="delete:${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/purgeSubscriber?subscriberIdentity=subscriberIdentity" style="width:20px;"  />
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
        return addWarning(".popup","At least select one Subscriber for purge");
    }else{
		var flag = false;
		flag = deleteConfirmMsg(obj,"Purge deleted subscribers ?");
    	if(flag==true){
    		purgeSubscribers();
    	}
		return flag;
    }
}   
function purgeSubscribers(){
	document.forms["deleteSubscriberSearchForm"].action = "purgeSubscribers";
	document.forms["deleteSubscriberSearchForm"].submit();
}

function restoreRecords(obj){
	var selectVar = getSelectedValues();
    if(selectVar == false){
       return addWarning(".popup","At least select one Subscriber for restoring");
    }else{
		var flag =deleteConfirmMsg(obj,"Restore deleted subscribers ?");
    	if(flag==true){
    		restoreSubscribers();
    	}
    }
}
function restoreSubscribers(){
	document.forms["deleteSubscriberSearchForm"].action = "restoreSubscribers";
	document.forms["deleteSubscriberSearchForm"].submit();
}

</script>
 
