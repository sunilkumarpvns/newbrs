<%@page import="com.elitecore.corenetvertex.spr.data.SPRInfoImpl"%>
<%@page import="com.elitecore.nvsmx.system.constants.Attributes"%>
<%@taglib uri="/struts-tags/ec" prefix="s"%>
<style type="text/css">
	.form-group {
	width: 100%;
	display: table;
	margin-bottom: 2px;
}
</style>
<script>
$(document).ready(function(){
	 
	$('#tabs a').click(function (e) {
		e.preventDefault();
		$(this).tab('show');
	});
	
	// store the currently selected tab in the hash value
	$("ul.nav-tabs > li > a").on("shown.bs.tab", function (e) {
		var id = $(e.target).attr("href").substr(1);
		window.location.hash = id;
	});
	
	// on load of the page: switch to the currently selected tab
	var hash = window.location.hash;
	$('#tabs a[href="' + hash + '"]').tab('show');

	$("#hrForDeleteModel").remove();

});




</script>
<div class="panel panel-primary">
	<div class="panel-heading" style="padding: 8px 15px">
		<h3 class="panel-title" style="display:inline;"><s:text name="subscriber.details"></s:text></h3>
   		<s:if test="%{subscriber.status.equals(@com.elitecore.corenetvertex.constants.SubscriberStatus@DELETED.name()) == false}">
   		<div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Edit"
			    	onclick="getEncodeURI('${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/initUpdate?subscriberIdentity=','${subscriber.subscriberIdentity}')">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onclick="callDeleteModel('<s:property value="%{subscriber.subscriberIdentity}" />')">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Delete" >
					<span class="glyphicon glyphicon-trash"></span>
				</button>
			</span>
		</div>
		</s:if>
		<s:elseif  test="%{subscriber.status.equals(@com.elitecore.corenetvertex.constants.SubscriberStatus@DELETED.name())}">
			<div class="nv-btn-group" align="right">
				<span class="btn-group btn-group-xs" data-toggle="confirmation-singleton"
				 onmousedown="addEncodedURI('${pageContext.request.contextPath}/policydesigner/subscriber/Subscriber/purgeSubscriber?subscriberIdentity=','${subscriber.subscriberIdentity}',this,deleteConfirm,'data-href')">
					<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Purge" >
						<span class="glyphicon glyphicon-trash"></span>
					</button>
				</span>
			</div>
		</s:elseif>
	</div>

	<div class="panel-body">
		<div class="row" style="margin-bottom: 15px;font-size: 13px">
			<div class="container-fluid">
				<div class="col-xs-6 col-sm-6">
					<div class="row">
						<s:label key="subscriber.subscriberIdentity" value="%{subscriber.subscriberIdentity}"
								 cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-6"
								 elementCssClass="col-xs-8 col-sm-6"/>
						<s:label key="subscriber.productoffer" value="%{subscriber.productOffer}"
								 cssClass="control-label light-text word-break" labelCssClass="col-xs-4 col-sm-6"
								 elementCssClass="col-xs-8 col-sm-6"/>
						<s:label key="subscriber.currency" value="%{currency}"
								 cssClass="control-label light-text word-break" labelCssClass="col-xs-4 col-sm-6"
								 elementCssClass="col-xs-8 col-sm-6"/>
						<s:label key="subscriber.imspackage" value="%{subscriber.imsPackage}"
								 cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
								 elementCssClass="col-xs-8 col-sm-7"/>
					</div>
				</div>

				<div class="col-xs-6 col-sm-6">
					<div class="row">
						<s:label key="subscriber.subscribermode" value="%{subscriber.subscriberMode}"
								 cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
								 elementCssClass="col-xs-8 col-sm-7"/>
						<s:if test="%{subscriber.status.equals(@com.elitecore.corenetvertex.constants.SubscriberStatus@ACTIVE.name())}">
							<s:set var="cssType" value="%{'color:green'}"></s:set>
						</s:if>

						<s:elseif
								test="%{subscriber.status.equals(@com.elitecore.corenetvertex.constants.SubscriberStatus@INACTIVE.name())}">
							<s:set var="cssType" value="%{'color:#ff5c33'}"></s:set>
						</s:elseif>

						<s:elseif
								test="%{subscriber.status.equals(@com.elitecore.corenetvertex.constants.SubscriberStatus@DELETED.name())}">
							<s:set var="cssType" value="%{'color:red'}"></s:set>
						</s:elseif>
						<s:label key="subscriber.status" value="%{subscriber.status}"
								 cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
								 elementCssClass="col-xs-8 col-sm-7" cssStyle="%{cssType}"/>
						<s:label key="subscriber.syinterface"
								 value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(subscriber.syInterface).getStringNameBoolean()}"
								 cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
								 elementCssClass="col-xs-8 col-sm-7"/>
					</div>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="container-fluid">
			<ul class="nav nav-tabs tab-headings" id="tabs">
				<li class="active" id="tab1" >
					<a data-toggle="tab" href="#section1"><s:text name="subscriber.profile.details"></s:text></a>
				</li>
				<li id="tab2">
					<a data-toggle="tab" href="#section2"><s:text name="subscriber.subscriptions"></s:text></a>
				</li>
				<li id="tab3">
					<a data-toggle="tab" href="#section3"><s:text name="subscriber.active.sessions"></s:text></a>
				</li>
				<li id="tab4">
					<a data-toggle="tab" href="#section4"><s:text name="subscriber.monetaryBalance"></s:text></a>
				</li>
				<li id="tab5">
					<a data-toggle="tab" href="#section5"><s:text name="subscriber.alternateIdentities"></s:text></a>
				</li>
			</ul>
			<div class="tab-content">
				<div id="section1" class="tab-pane fade in active">
				<fieldset class="fieldSet-line">
						<legend>
							<a data-toggle="collapse" data-target="#identityAttributesContent"	href="#" id="identityAttributes">
								<span class="caret"></span>
								<s:text name="subscriber.identity.attributes"/> </a>
						</legend>
						<div class="row collapse in" id="identityAttributesContent">
							<div> 
								<div class="col-sm-4">
									<div class="row">
										<s:label key="subscriber.msisdn" value="%{subscriber.msisdn}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.imsi" value="%{subscriber.imsi}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>	
										<s:label key="subscriber.imei" value="%{subscriber.imei}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.name" value="%{subscriber.userName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</div>							 
								</div>
								<div class="col-sm-4 leftVerticalLine">
									<div class="row">
										<s:label key="subscriber.cui" value="%{subscriber.cui}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.mac" value="%{subscriber.mac}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.meid" value="%{subscriber.meid}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</div>
								</div>
								<div class="col-sm-4 leftVerticalLine">
									<div class="row">
										<s:label key="subscriber.eui64" value="%{subscriber.eui64}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.modifiedeui64" value="%{subscriber.modifiedEui64}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.sipurl" value="%{subscriber.sipURL}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</div>
								</div>
							</div>
						</div>
					</fieldset>

					<fieldset class="fieldSet-line">
						<legend>
							<a data-toggle="collapse" data-target="#subscriptionDetailsContent"	href="#" id="subscriptionDetails" >
								<span class="caret"></span>
								<s:text name="subscriber.subscription"/>
							</a>
						</legend>
						<div class="row collapse in" id="subscriptionDetailsContent">
							<div>
								<div class="col-sm-4">
									<div class="row">
										<s:label key="subscriber.customertype" value="%{subscriber.customerType}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:if test="%{subscriber.expiryDate != null}">
											<s:set var="expiryDate">
												<s:date name="%{subscriber.expiryDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
											</s:set>
											<s:label key="subscriber.expirydate" value="%{expiryDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										</s:if>
										<s:else>
											<s:label key="subscriber.expirydate" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										</s:else>

									</div>
								</div>
								<div class="col-sm-4 leftVerticalLine">
									<div class="row">
										<s:label key="subscriber.parentid" value="%{subscriber.parentId}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.groupname" value="%{subscriber.groupName}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</div>
								</div>
								<div class="col-sm-4 leftVerticalLine">
									<div class="row">
										<s:label key="subscriber.arpu" name="%{subscriber.arpu}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.billingdate" value="%{subscriber.billingDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</div>
								</div>

							</div>
						</div>
					</fieldset>

					<fieldset class="fieldSet-line">
						<legend>
							<a data-toggle="collapse" data-target="#subscriptionDetailsContent"	href="#" id="paygDetail" >
								<span class="caret"></span>
								<s:text name="subscriber.payg.roadming"/>
							</a>
						</legend>
						<div class="row collapse in" id="paygDetailContent">
							<div>
								<div class="col-sm-4">
									<div class="row">
										<s:label key="subscriber.international.data" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(subscriber.paygInternationalDataRoaming).getDisplayBooleanValue()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</div>
								</div>
							</div>
						</div>
					</fieldset>
					
					<fieldset class="fieldSet-line">
						<legend>
							<a data-toggle="collapse" data-target="#personalDetailContent"	href="#" id="personalDetails" >
								<span class="caret"> </span> 
								<s:text name="subscriber.personal"/> 
							</a>
						</legend>
						<div class="row collapse in" id="personalDetailContent">
							<div>
								<div class="col-sm-4">
									<div class="row">
										<s:label key="subscriber.id" value="%{subscriber.subscriberIdentity}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.email" value="%{subscriber.email}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									 	<s:label key="subscriber.passwordcheck" value="%{@com.elitecore.corenetvertex.constants.CommonStatusValues@fromBooleanValue(subscriber.passwordCheck).getStringNameBoolean()}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.encryptiontype" value="%{@com.elitecore.corenetvertex.constants.PasswordEncryptionType@fromValue(subscriber.encryptionType).displayVal}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</div>
								</div>
								<div class="col-sm-4 leftVerticalLine">
									<div class="row">
										<s:label key="createdon" value="%{subscriber.createdDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="lastmodifiedon" value="%{subscriber.modifiedDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:if test="%{subscriber.birthdate != null}">
											<s:set var="birthDate">
												<s:date name="%{subscriber.birthdate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
											</s:set>
											<s:label key="subscriber.birthdate" value="%{birthDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</s:if>
									<s:else>
										<s:label key="subscriber.birthdate" value="" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</s:else>
										<s:label key="subscriber.phone" value="%{subscriber.phone}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
								 	</div>
								</div>
								<div class="col-sm-4 leftVerticalLine">
									<div class="row">
										<s:label key="subscriber.country" value="%{subscriber.country}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.city" value="%{subscriber.city}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.area" value="%{subscriber.area}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
								 		<s:label key="subscriber.zone" value="%{subscriber.zone}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>								 		
								 	</div>
								</div>
							</div>
						</div>
					</fieldset>
					
					<fieldset class="fieldSet-line">
						<legend>
							<a data-toggle="collapse" data-target="#professionalDetailsContent"	href="#" id="professionalDetails">
								<span class="caret"></span>	
								<s:text name="subscriber.professional"/>
							</a>
						</legend>
						<div class="row collapse in" id="professionalDetailsContent">
							<div>
								<div class="col-sm-4">
									<div class="row">
										<s:label key="subscriber.company" value="%{subscriber.company}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.cadre" value="%{subscriber.cadre}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</div>								 	
								</div>
								<div class="col-sm-4 leftVerticalLine">
									<div class="row">
										<s:label key="subscriber.department" value="%{subscriber.department}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</div>
								</div>
								<div class="col-sm-4 leftVerticalLine">
									<div class="row">
										<s:label key="subscriber.role" value="%{subscriber.role}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</div>
								</div>
							</div>
						</div>
					</fieldset>
					
					
					<fieldset class="fieldSet-line">
						<legend>
							<a data-toggle="collapse" data-target="#otherDetailsContent" href="#" id="otherDetails" >
								<span class="caret"></span>
								<s:text name="subscriber.other"/>
							</a>
						</legend>
						<div class="row in collapse" id="otherDetailsContent" >
							<div>
								<div class="col-sm-4">
									<div class="row">
										<s:label key="subscriber.subscriberlevelmetering" value="%{subscriber.subscriberLevelMetering}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.param1" value="%{subscriber.param1}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.callingstationid" value="%{subscriber.callingStationId}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.billing.account" value="%{subscriber.billingAccountId}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</div>
								</div>
								<div class="col-sm-4 leftVerticalLine">
									<div class="row">
										<s:label key="subscriber.param2" value="%{subscriber.param2}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.param3" value="%{subscriber.param3}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.framedip" value="%{subscriber.framedIp}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.service.instance" value="%{subscriber.serviceInstanceId}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</div>
								</div>
								<div class="col-sm-4 leftVerticalLine">
									<div class="row">
										<s:label key="subscriber.param4" value="%{subscriber.param4}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.param5" value="%{subscriber.param5}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
										<s:label key="subscriber.nasportid" value="%{subscriber.nasPortId}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
									</div>
								</div>
							</div>
						</div>
					</fieldset>
				</div>
				<div id="section2" class="tab-pane fade">
					<s:include value="/view/policydesigner/subscriber/SubscriptionInformation.jsp"></s:include>
				</div>
				<div id="section3" class="tab-pane fade">
					<s:include value="/view/policydesigner/subscriber/SubscriberActiveSession.jsp"></s:include>
				</div>
				<div id="section4" class="tab-pane fade">
					<s:include value="/view/policydesigner/subscriber/SubscriberMonetaryBalance.jsp"></s:include>
				</div>
				<div id="section5" class="tab-pane fade">
					<s:include value="/view/policydesigner/subscriber/SubscriberAlternateIdentities.jsp"></s:include>
				</div>
			</div>

			</div>
		</div>


	</div>
</div>

<%@include file="/view/policydesigner/subscriber/DeleteSubscriberModel.jsp" %>

<div class="modal fade" id="notPermittedForDelete" tabindex="-1" role="dialog" aria-labelledby="notPermittedForDeleteLabel" aria-hidden="true" >
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-body" style="font-size: 14px">
				<s:text name="Operation is not permitted for DELETED / INACTIVE Subscriber Profile" />
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal"><s:text name="subscription.close"></s:text> </button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="notPermittedForProfileExpired" tabindex="-1" role="dialog" aria-labelledby="notPermittedForDeleteLabel" aria-hidden="true" >
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-body" style="font-size: 14px">
				<s:text name="Operation is not permitted for Expired Subscriber Profile" />
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" data-dismiss="modal"><s:text name="subscription.close"></s:text> </button>
			</div>
		</div>
	</div>
</div>
