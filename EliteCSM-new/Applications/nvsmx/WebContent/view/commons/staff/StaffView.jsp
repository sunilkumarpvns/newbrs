<%@taglib uri="/struts-tags/ec" prefix="s"%>
<style>
.form-group {
	width: 100%;
	display: table;
	margin-bottom: 2px;
}
 
#updateStaff{
	float: right; 
	margin: 10px 20px;
}
</style>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<s:text name="staff.view" />
		</h3>
	</div>

	<div class="panel-body">
		<div class="row">
			<button type="button" class="btn btn-primary btn-sm" id="updateStaff"  data-toggle="modal" data-target="#verification-modal" >
					<span class="glyphicon glyphicon-edit" title="UpdateStaff"></span> <s:text name="staff.update"></s:text>
			</button>
			<s:form action="verifyPassword/commons/staff/Staff/initUpdate" >
					<s:token/>
					<input type="hidden" name="failoverUrl" id="failoverUrl" value="commons/staff/Staff/view" />
					<s:include value="/view/commons/staff/VerifyPassword.jsp"></s:include>
			</s:form>		
	
			<fieldset class="fieldSet-line">
					<legend>
						<s:text name="staff.personal"/>
					</legend>
					<div class="row" id="personalDetailContent">
					<div class="col-xs-4 col-md-3">
						<div class="col-xs-12">
						<s:url action="commons/staff/ProfilePicture/execute" var="profilePic" includeParams="true" >
              				<s:param name="staffId">${loggedInStaffProfilePictureId}</s:param>
              			</s:url>
              			<img height="150" width="150" src='<s:property value="#profilePic"/>' />
              			</div>
					</div>
					<div class="col-xs-8 col-md-5">
						<s:label key="getText('staff.name')" value="%{staff.name}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8"/>
						<s:label key="getText('staff.username')" value="%{staff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8"/>
						<s:label key="getText('staff.email')" value="%{staff.emailAddress}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8"/>
						<s:label key="getText('staff.phone')" value="%{staff.phone}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8"/>
						<s:label key="getText('staff.mobile')" value="%{staff.mobile}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-3" elementCssClass="col-xs-8"/>
					</div>
						
					</div>
			</fieldset>
			
			
			<fieldset class="fieldSet-line">
					<legend>
						<s:text name="staff.audit"/>
					</legend>
					<div class="row" id="auditDetailContent">
					<div class="col-sm-6">
						<s:if test="%{staff.createdByStaff !=null}">
							<s:hrefLabel key="createdby" value="%{staff.createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
										 url="/sm/staff/staff/%{staff.createdByStaff.id}"/>
						</s:if>
						<s:else>
							<s:label key="createdby" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
						</s:else>

						<s:if test="%{staff.createdDate != null}">
							<s:set var="createdDate">
								<s:date name="%{staff.createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
							</s:set>
							<s:label key="getText('staff.createDate')" value="%{createdDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 col-lg-4" elementCssClass="col-xs-7 col-lg-8"/>
						</s:if>
						<s:else>
							<s:label key="getText('staff.createDate')" value="%{createdDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 col-lg-4" elementCssClass="col-xs-7 col-lg-8"/>
						</s:else>
						<s:if test="%{staff.modifiedByStaff !=null}">
							<s:hrefLabel key="modifiedby" value="%{staff.modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"
										 url="/sm/staff/staff/%{staff.modifiedByStaff.id}"/>
						</s:if>
						<s:else>
							<s:label key="modifiedby" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7" />
						</s:else>
						<s:if test="%{staff.modifiedDate != null}">
							<s:set var="lastModifiedDate">
								<s:date name="%{staff.modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
							</s:set>
							<s:label key="getText('staff.lastmodifieddate')" value="%{lastModifiedDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 col-lg-4" elementCssClass="col-xs-7 col-lg-8"/>
						</s:if>
						<s:else>
							<s:label key="getText('staff.lastmodifieddate')" value="%{lastModifiedDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 col-lg-4" elementCssClass="col-xs-7 col-lg-8"/>
						</s:else>
						
					</div>
					<div class="col-sm-6 leftVerticalLine">
						<s:if test="%{staff.lastLoginTime != null}">
							<s:set var="lastLoginTime">
								<s:date name="%{staff.lastLoginTime}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
							</s:set>
							<s:label key="getText('staff.lastlogindate')" value="%{lastLoginTime}" cssClass="control-label light-text" labelCssClass="col-xs-5 col-lg-4" elementCssClass="col-xs-7 col-lg-8"/>
						</s:if>
						<s:else>
							<s:label key="getText('staff.lastlogindate')" value="%{lastLoginTime}" cssClass="control-label light-text" labelCssClass="col-xs-5 col-lg-4" elementCssClass="col-xs-7 col-lg-8"/>
						</s:else>
					
						<s:if test="%{staff.passwordChangeDate != null}">
							<s:set var="passwordChangeDate">
								<s:date name="%{staff.passwordChangeDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
							</s:set>
							<s:label key="getText('staff.passwordchangedate')" value="%{passwordChangeDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 col-lg-4" elementCssClass="col-xs-7 col-lg-8"/>
						</s:if>
						<s:else>
							<s:label key="getText('staff.passwordchangedate')" value="%{passwordChangeDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 col-lg-4" elementCssClass="col-xs-7 col-lg-8"/>
						</s:else>
						
						<s:if test="%{staff.passwordExpiryDate != null}">
							<s:set var="passwordExpiryDate">
								<s:date name="%{staff.passwordExpiryDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
							</s:set>
							<s:label key="getText('staff.passwordexpirydate')" value="%{passwordExpiryDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 col-lg-4" elementCssClass="col-xs-7 col-lg-8"/>
						</s:if>
						<s:else>
							<s:label key="getText('staff.passwordexpirydate')" value="%{passwordExpiryDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 col-lg-4" elementCssClass="col-xs-7 col-lg-8"/>
						</s:else>
						
					</div>
						
					</div>
			</fieldset>
				
		</div>
	</div>
</div>

<script type="text/javascript">
$('#verification-modal').on('shown.bs.modal', function () {
	$('#password').focus();
})
</script>