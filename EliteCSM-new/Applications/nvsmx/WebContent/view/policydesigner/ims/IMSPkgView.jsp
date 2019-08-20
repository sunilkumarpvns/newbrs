<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

<link href="${pageContext.request.contextPath}/css/third-party/bootstrap-toggle.min.css" rel="stylesheet"> 
<script src="${pageContext.request.contextPath}/js/third-party/bootstrap-toggle.min.js"></script>
<style type="text/css">
.form-group {
	width: 100%;
	display: table;
	margin-bottom: 2px;
}
</style> 

<%
 int rows = ConfigurationProvider.getInstance().getPageRowSize();
 %>

<div class="panel panel-primary">
	<div class="panel-heading" style="padding: 8px 15px">
		<h3 class="panel-title" style="display: inline;">
			<div class="btn-group btn-group-xs">
  				<button class="btn btn-primary" style="display:none"  	id="designMode">Design</button>
  				<button class="btn btn-primary" style="display:none"  	id="testMode" 		onclick="changePkgMode('Design to Test','testMode');" >Test it</button>
  				<button class="btn btn-primary" style="display:none"  	id="liveMode" 		onclick="changePkgMode('Test to Live','liveMode');">Go Live</button>
  				<button class="btn btn-primary" style="display:none"  	id="live2Mode" 		onclick="changePkgMode('Live to Live2','live2Mode');" >Go Live2</button>
			</div>
			&nbsp; <s:property value="imsPkgData.name"/> 
		</h3>
		<div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Export"
						onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/export?ids=${imsPkgData.id}&${imsPkgData.id}=${imsPkgData.groups}&pkgId=${imsPkgData.id}'">
					<span class="glyphicon glyphicon-export" ></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
					onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${imsPkgData.id}&auditableId=${imsPkgData.id}&auditPageHeadingName=${imsPkgData.name}&refererUrl=/policydesigner/ims/IMSPkg/view?pkgId=${imsPkgData.id}'">
					<span class="glyphicon glyphicon-eye-open" ></span>
				</button>
			</span>
			<s:if test="%{imsPkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
					onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/init?pkgId=${imsPkgData.id}&groupIds=${imsPkgData.groups}'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/delete?ids=${imsPkgData.id}&${imsPkgData.id}=${imsPkgData.groups}&pkgId=${imsPkgData.id}">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete" >
					<span class="glyphicon glyphicon-trash"></span>
				</button>
			</span>
			</s:if>
			<s:else>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
					onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/init?pkgId=${imsPkgData.id}&groupIds=${imsPkgData.groups}'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton"  >
				<button type="button" disabled="disabled" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete" >
					<span class="glyphicon glyphicon-trash"></span>
				</button>
			</span>
			</s:else>
		</div>
	</div>
	<div class="panel-body">
	    <div class="row">
			<s:hidden name="groupIds" value="%{imsPkgData.groups}" />
           <fieldset class="fieldSet-line">
				<legend align="top"> 
           			<s:text name="basic.detail"></s:text>
           		</legend>
           		<div class="row">
					<div class="col-sm-6">
						<div class="row">
							<s:label key="getText('ims.pkg.description')" value="%{imsPkgData.description}" cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"  />
							<s:label key="getText('ims.pkg.status')" value="%{imsPkgData.status}" cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8" />
                            <s:set var="priceTag">
                                <s:property value="getText('ims.pkg.price')"/> <s:property value="getText('opening.braces')"/><s:property value="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@getCurrency()"/><s:property value="getText('closing.braces')"/>
                            </s:set>
							<s:label key="priceTag" value="%{imsPkgData.price}" cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8" />
							<s:label key="getText('ims.pkg.groups')" value="%{imsPkgData.groupNames}" cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8" />
						</div>
					</div>
					<div class="col-sm-6 leftVerticalLine">
						<div class="row">
							
							<s:if test="%{imsPkgData.createdByStaff !=null}">
								<s:hrefLabel key="getText('createdby')" value="%{imsPkgData.createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
										 url="/sm/staff/staff/%{imsPkgData.createdByStaff.id}"/>
							</s:if>
							<s:else>
								<s:label key="getText('createdby')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{imsPkgData.createdDate != null}">
								<s:set var="createdByDate">
									<s:date name="%{imsPkgData.createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
								</s:set>
								<s:label key="getText('createdon')" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="getText('createdon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							
							
							<s:if test="%{imsPkgData.modifiedByStaff !=null}">
								<s:hrefLabel key="getText('modifiedby')" value="%{imsPkgData.modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
											 url="/sm/staff/staff/%{imsPkgData.modifiedByStaff.id}"/>
							</s:if>
							<s:else>
								<s:label key="getText('modifiedby')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{imsPkgData.modifiedDate != null}">
								<s:set var="modifiedByDate">
									<s:date name="%{imsPkgData.modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
								</s:set>
								<s:label key="getText('lastmodifiedon')" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="getText('lastmodifiedon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
						</div>
					</div>
				</div>
			</fieldset>
       </div>
       <div class="row">
			<fieldset class="fieldSet-line" >
				<legend align="top"><s:text name="IMS Services"></s:text></legend>
				<div style="text-align: right;">
					<s:if test="%{imsPkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">					
						<button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/imspkgservice/IMSPkgService/init?pkgId=${imsPkgData.id}&groupIds=${imsPkgData.groups}'">
							<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
							<s:text name="IMS Services" />
						</button>
					</s:if>
					<s:else>
						<button disabled="disabled" class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px"  >
							<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
							<s:text name="IMS Services" />
						</button>
					</s:else>
				</div>
				<nv:dataTable 
		 				id="imsPkgServices"
		 				actionUrl="/searchTable/policydesigner/ims/IMSPkg/getImsPkgServiceDataList?imsPkgId=${imsPkgData.id}"	
						beanType="com.elitecore.nvsmx.policydesigner.model.pkg.imspkgservice.IMSPkgServiceWrapper"
						dataListName="imsPkgServiceWrappers" 
						showPagination="false"
						showInfo="false"
						cssClass="table table-blue" 
						subTableUrl="/searchTable/policydesigner/ims/IMSPkg/imsPkgServiceViewDetail"
						width="100%"> 
						<nv:dataTableColumn title="Name"   		beanProperty="name" 					style="font-weight: bold;color: #4679bd;" tdStyle="text-align:left; word-wrap:break-word;" sortable="true" />
						<nv:dataTableColumn title="Media Type" 	beanProperty="mediaTypeName"  	style="font-weight: bold;color: #4679bd;" tdStyle="width:150px;"/>
						<nv:dataTableColumn title="Application Function Id" 	beanProperty="appFunctionId"  	style="font-weight: bold;color: #4679bd;" tdStyle="width:150px;"/>						
						<nv:dataTableColumn title="Action" 		beanProperty="action" 					style="font-weight: bold;color: #4679bd; width:100px;" tdStyle="width:100px;"/>													
						<s:if test="%{imsPkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
							<nv:dataTableColumn title="" sortable="false" icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/policydesigner/imspkgservice/IMSPkgService/init?imsPkgServiceId=id&groupIds=${imsPkgData.groups}" style="width:20px;" tdStyle="width:20px;"  />
							<nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" 	hrefurl="delete:${pageContext.request.contextPath}/policydesigner/imspkgservice/IMSPkgService/delete?imsPkgServiceId=id&groupIds=${imsPkgData.groups}&pkgId=${imsPkgData.id}"		style="width:20px;" tdStyle="width:20px;"/>
						</s:if>
						<s:else>
							<nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-pencil'></span>" style="width:20px;" tdStyle="width:20px;opacity:0.5" />
				            <nv:dataTableColumn title="" icon="<span class='glyphicon glyphicon-trash'></span>" style="width:20px;" tdStyle="width:20px;opacity:0.5" />
						</s:else>
			 		</nv:dataTable>	
			</fieldset>
	</div>
	</div>
</div>


<script>


$(document).ready(function() { 
	setPackageMode();
}); 

function changePkgMode(fromTo,refId) {
		var classVal = $("#"+refId).attr("class");
		if(classVal.toLowerCase().indexOf("primary") >=0){
			return;
		}
		
	 	if(confirm("Change IMS package mode from "+fromTo+" ?")){
	 		var pkgId = '${imsPkgData.id}';
	 		var pkgMode = '${imsPkgData.packageMode}';
			$.ajax({
				async       : false,
			    type 		: "POST",
		    	url  		: "${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/updateMode",
                dataType    : "json",
		    	data		: {'pkgId' : pkgId , 'pkgMode' : pkgMode,'groupIds' : '<s:property value="imsPkgData.groups" />','entityOldGroups' : '<s:property value="imsPkgData.groups" />'},
                success 	: function(json){
                    if(json.responseCode != 200){
                        updateModeCallBackfunction(json);
                    } else {
                        document.location.href = "${pageContext.request.contextPath}/policydesigner/ims/IMSPkg/view?pkgId="+pkgId;
                    }
                },
            });
        }
}
function updateModeCallBackfunction(json){
    addDanger(".popup",json.responseMessage);
}
 
function setPackageMode() {
	var pkgMode = '${imsPkgData.packageMode}';
	
	$('#designMode').hide();
	$('#testMode').hide();
	$('#liveMode').hide();
	$('#live2Mode').hide();
	
	if (pkgMode == 'DESIGN') {
		$('#designMode').show();
		$('#designMode').html("Design");
		$('#testMode').show();
		$('#testMode').attr("class", "btn btn-default");
		$('#testMode').attr("style","font-weight: bold;");
		
	}else if (pkgMode == 'TEST') {
		$('#testMode').show();
		$('#testMode').html("Test");
		$('#liveMode').show();
		$('#liveMode').attr("class", "btn btn-default");
		$('#liveMode').attr("style","font-weight: bold;");
		
	}else if (pkgMode == 'LIVE') {
		$('#liveMode').show();
		$('#liveMode').html("Live");
		$('#live2Mode').show();
		$('#live2Mode').attr("class", "btn btn-default");
		$('#live2Mode').attr("style","font-weight: bold;");
		
	}else if (pkgMode == 'LIVE2') {
		$('#live2Mode').show();
		$('#live2Mode').html("Live2");
	}
}

</script>