<%@page import="com.elitecore.nvsmx.system.ConfigurationProvider"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>

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
		
			&nbsp; <s:property value="pkgData.name"/> 
		</h3>
		<div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Export"
						onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/export?ids=${pkgData.id}&${pkgData.id}=${pkgData.groups}&pkgId=${pkgData.id}'">
					<span class="glyphicon glyphicon-export" ></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
					onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${pkgData.id}&auditableId=${pkgData.id}&auditPageHeadingName=${pkgData.name}&refererUrl=/policydesigner/emergency/EmergencyPkg/view?pkgId=${pkgData.id}'">
					<span class="glyphicon glyphicon-eye-open" ></span>
				</button>
			</span>
			<s:if test="%{pkgData.packageMode != @com.elitecore.corenetvertex.pkg.PkgMode@LIVE2.name()}">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
					onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/delete?ids=${pkgData.id}&${pkgData.id}=${pkgData.groups}&pkgId=${pkgData.id}">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete" >
					<span class="glyphicon glyphicon-trash"></span>
				</button>
			</span>
			</s:if>
			<s:else>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
					onclick="javascript:location.href='${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/init?pkgId=${pkgData.id}&groupIds=${pkgData.groups}'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
			<span class="btn-group   btn-group-xs" data-toggle="confirmation-singleton"  >
				<button type="button" disabled="disabled" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete" >
					<span class="glyphicon glyphicon-trash"></span>
				</button>
			</span>
			</s:else>
		</div>
	</div>
	<div class="panel-body">
	    <div class="row">
			<s:hidden name="groupIds" value="%{pkgData.groups}" />
			<fieldset class="fieldSet-line">
				<legend align="top"> 
           			<s:text name="pkg.basicdetail"></s:text>
           		</legend>
				<div class="row">
					<div class="col-sm-4">
						<div class="row">
							<s:label key="pkg.description"	value="%{pkgData.description}"	cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"  />
							<s:label key="pkg.status" 		value="%{pkgData.status}" 		cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
							<s:label key="pkg.groups" 		value="%{pkgData.groupNames}" 	cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8" />
							<s:label key="pkg.ordernumber" 	value="%{pkgData.orderNumber}" 	cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
							<s:if test="%{pkgData.availabilityStartDate != null}">
								<s:set var="availStartDate">
									<s:date name="%{pkgData.availabilityStartDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
								</s:set>
								<s:label key="pkg.availability.start.date" value="%{availStartDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="pkg.availability.start.date" 	value="%{pkgData.availabilityStartDate}" 	cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
							</s:else>

							<s:if test="%{pkgData.availabilityEndDate != null}">
								<s:set var="availEndDate">
									<s:date name="%{pkgData.availabilityEndDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
								</s:set>
								<s:label key="pkg.availability.end.date" value="%{availEndDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="pkg.availability.end.date" 	value="%{pkgData.availabilityEndDate}" 	cssClass="control-label light-text" labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
							</s:else>

						</div>
					</div>
					<div class="col-sm-4 leftVerticalLine">
						<div class="row">
							
							<s:if test="%{pkgData.createdByStaff !=null}">
								<s:hrefLabel key="createdby" value="%{pkgData.createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
										 url="/sm/staff/staff/%{pkgData.createdByStaff.id}"/>
							</s:if>
							<s:else>
								<s:label key="createdby" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{pkgData.createdDate != null}">
								<s:set var="createdByDate">
									<s:date name="%{pkgData.createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
								</s:set>
								<s:label key="createdon" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="createdon" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							
							
							<s:if test="%{pkgData.modifiedByStaff !=null}">
								<s:hrefLabel key="modifiedby" value="%{pkgData.modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
										 url="/sm/staff/staff/%{pkgData.modifiedByStaff.id}"/>
							</s:if>
							<s:else>
								<s:label key="modifiedby" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{pkgData.modifiedDate != null}">
								<s:set var="modifiedByDate">
									<s:date name="%{pkgData.modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
								</s:set>
								<s:label key="lastmodifiedon" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="lastmodifiedon" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
							</s:else>
							<s:if test="%{@com.elitecore.commons.base.Strings@isNullOrBlank(@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@get('PARAM_1'))}">
								<s:label key="Param 1" value="%{pkgData.param1}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@get('PARAM_1')" value="%{pkgData.param1}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:else>
							<s:if test="%{@com.elitecore.commons.base.Strings@isNullOrBlank(@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@get('PARAM_2'))}">
								<s:label key="Param 2" value="%{pkgData.param2}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:if>
							<s:else>
								<s:label key="@com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO@get('PARAM_2')" value="%{pkgData.param2}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
							</s:else>
						</div>
					</div>
				</div>
			</fieldset>
       </div>
 	  <%@include file="/view/policydesigner/emergency/EmergencyQosProfilesView.jsp" %>
	   
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
		
	 	if(confirm("Change package mode from "+fromTo+" ?")){
	 		var pkgId = '${pkgData.id}';
	 		var pkgMode = '${pkgData.packageMode}';
			$.ajax({
				async       : true,
			    type 		: "POST",
		    	url  		: "${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/updateMode",
                dataType    : "json",
		    	data		: {'pkgId' : pkgId , 'pkgMode' : pkgMode , 'groupIds' : '<s:property value="pkgData.groups" />' , 'pkgData.groups' : '<s:property value="pkgData.groups" />', 'entityOldGroups' : '<s:property value="pkgData.groups" />'},
		    	success 	: function(json){
                    if(json.responseCode != 200){
                        updateModeCallBackfunction(json);
                    } else {
                        document.location.href = "${pageContext.request.contextPath}/policydesigner/emergency/EmergencyPkg/view?pkgId="+pkgId;
                    }
				},
			});
	 	} 
 }

function updateModeCallBackfunction(json){
    addDanger(".popup",json.responseMessage);
}

function setPackageMode() {
		var pkgMode = '${pkgData.packageMode}';
		console.log("pkgMode: "+pkgMode);
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