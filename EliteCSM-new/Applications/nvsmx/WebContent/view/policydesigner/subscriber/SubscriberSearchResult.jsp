<%@page import="java.util.Map"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@page import="java.lang.String"%>
<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<%@taglib uri="/struts-tags/ec" prefix="s" %>
<%
	String criteriaVal = (String)request.getAttribute(Attributes.CRITERIA);
    if(criteriaVal==null || criteriaVal.trim().length()==0){
    	criteriaVal = "{}";
    }
%>
<script type="text/javascript">
	var criteria = <%=criteriaVal%>;
	function addSubscriber(){
	document.forms["SearchSubscriberForm"].action = "initCreate";
	document.forms["SearchSubscriberForm"].submit();
}

</script>

<s:form id="SearchSubscriberForm" method="post" cssClass="form-vertical">
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><s:text name="subscriber.search"/> </h3>		
	</div>
	<div class="panel-body table-responsive"  > 
	<span class="btn-group btn-group-sm">
				<button class="btn btn-default" role="button" id="btnAddTemplate" onclick="addSubscriber();" >
					<span class="glyphicon glyphicon-plus" title="Add"></span>
				</button>
			</span>
			
		<nv:dataTable  actionUrl="/searchTable/policydesigner/subscriber/Subscriber/searchSubscriber"  cssClass="display table table-blue" 
						id="subscriberData" 
						width="100%" 
						beanType="com.elitecore.corenetvertex.spr.data.SPRInfoImpl"
						dataListName="dataList" 
						showPagination="true"
						showInfo="true"
						>	
							
			<nv:dataTableColumn title="<input type='checkbox' name='selectAll'  id='selectAll' />"  beanProperty="subscriberIdentity" 			style="width:10px;" />
			<nv:dataTableColumn title="Subscriber Identity" beanProperty="subscriberIdentity"  hrefurl="${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/view?subscriberIdentity=subscriberIdentity"	/>			
			<nv:dataTableColumn title="Name" 		 		beanProperty="userName" />
			<nv:dataTableColumn title="Product Offer"        beanProperty="productOffer" 	/>
			<nv:dataTableColumn title="Customer Type"       beanProperty="customerType" 	/>
			<nv:dataTableColumn title="Status"           	beanProperty="status" 	/>
			<nv:dataTableColumn title="Mode"           	beanProperty="subscriberMode" 	/>
			<nv:dataTableColumn title="MSISDN"		        beanProperty="msisdn" 	/> 
			<nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/initUpdate?subscriberIdentity=subscriberIdentity" style="width:20px;border-right:0px;"  />
			<nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>"  hrefurl="delete:javascript:callDeleteModel(subscriberIdentity)" style="width:20px;"  />
		</nv:dataTable>
	</div>
</div>
</s:form>

<%@include file="/view/policydesigner/subscriber/DeleteSubscriberModel.jsp" %>
