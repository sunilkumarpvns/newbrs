<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
    	<h3 class="panel-title" style="display: inline;">
            <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                    	onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${id}&auditPageHeadingName=${name}&refererUrl=/pd/partner/partner/${id}'">
                		<span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/partner/partner/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/partner/partner/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>
    
    <div class="panel-body" >
       <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="partner.basic.details" /></legend>
                <div class="row">
                    <div class="col-sm-4">
                        <div class="row">
                            <s:label key="partner.name" value="%{name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="partner.legalName" value="%{partnerLegalName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="partner.isunsigned" value="%{isUnsignedPartner}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
				  		</div>
                    </div>
                    
                 	<div class="col-sm-4 leftVerticalLine">
                 		<div class="row">
							<s:if test="%{registraionDate != null}">
	                            <s:set var="registrationtDate">
	                                <s:date name="%{registraionDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
	                            </s:set>
	                            <s:label key="partner.registraionDate" value="%{registrationtDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
	                        </s:if>
	                        <s:else>
	                            <s:label key="partner.registraionDate" value="%{registraionDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
	                        </s:else>
                      		
                      		<s:label key="partner.registrationNum" value="%{registrationNum}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
  					  		<s:label key="partner.status" value="%{status}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
  		                </div>
                  	</div>
                    
                    <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <s:label key="getText('createdby')" value="%{createdByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            <s:if test="%{createdDate != null}">
                                <s:set var="createdByDate">
                                    <s:date name="%{createdDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                                </s:set>
                                <s:label key="getText('createdon')" value="%{createdByDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            </s:if>
                            <s:else>
                                <s:label key="getText('createdon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 " />
                            </s:else>
                            <s:label key="getText('modifiedby')" value="%{modifiedByStaff.userName}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            <s:if test="%{modifiedDate != null}">
                                <s:set var="modifiedByDate">
                                    <s:date name="%{modifiedDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
                                </s:set>
                                <s:label key="getText('lastmodifiedon')" value="%{modifiedByDate}" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 "/>
                            </s:if>
                            <s:else>
                                <s:label key="getText('lastmodifiedon')" value="" cssClass="control-label light-text" labelCssClass="col-xs-5 " elementCssClass="col-xs-7 " />
                            </s:else>
                        </div>
                    </div>
               </div> 
		</fieldset>    
	<div class="panel-body" >
    	<div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="partner.register.address" /></legend>
                <div class="row">
                    <div class="col-sm-3">
                        <div class="row">
                        	<s:label key="partner.country" value="%{country.name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-sm-3 leftVerticalLine">
                        <div class="row">
                        	<s:label key="partner.state" value="%{region.name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-sm-3 leftVerticalLine">
                        <div class="row">
                      <s:label key="partner.city" value="%{city.name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                        </div>
                    </div>
                    <div class="col-sm-3 leftVerticalLine">
                        <div class="row">
                      <s:label key="partner.postCode" value="%{postCode}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                   	 </div>
                  	</div>
                 </div>
			</fieldset> 
                 
		<div class="panel-body" >
		    <div class="row">
	            <fieldset class="fieldSet-line">
	                <legend align="top"><s:text name="partner.contact.details" /></legend>
	                <div class="row">
	                    <div class="col-sm-4">
	                        <div class="row">
	                            <s:label key="partner.primaryContactName" value="%{primaryContactName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
	                            <s:label key="partner.primaryContactDesignation" value="%{primaryContactDesignation}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
						  		<s:label key="partner.primaryContactNumber" value="%{primaryContactNumber}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
						 		<s:label key="partner.primaryEmailAddress" value="%{primaryEmailAddress}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
	                   		 </div>
	                    </div>
	                   
	                    <div class="col-sm-4 leftVerticalLine">
	                        <div class="row">
	                      		<s:label key="partner.helpDesk" value="%{helpDesk}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
	                      		<s:label key="partner.webSiteUrl" value="%{webSiteUrl}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
	                    		<s:label key="partner.faxNumber" value="%{faxNumber}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
	                   	 	</div>
	                   	</div>
	                   	 
	                   	<div class="col-sm-4 leftVerticalLine">
	                        <div class="row">
	                      		<s:label key="partner.secondaryContactName" value="%{secondaryContactName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
	                      		<s:label key="partner.secondaryContactDesignation" value="%{secondaryContactDesignation}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
	                      		<s:label key="partner.secondaryContactNumber" value="%{secondaryContactNumber}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
	                      		<s:label key="partner.secondaryEmailAddress" value="%{secondaryEmailAddress}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
	                   	 	</div>
	                   	 </div>
					</div>
				</fieldset>
	     	</div>
	  	</div>
 	</div>
		<div class="row">
			<fieldset class="fieldSet-line" >
				<legend align="top"><s:text name="partner.accountProfile"></s:text></legend>
					<div style="text-align: right;">
						<button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/account/account/new?partnerId=${id}'">
							<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
								<s:text name="partner.accountProfile" />
						</button>
					</div>
					
					<nv:dataTable id="accountProfileData" list="${accountDataAsJson}" showPagination="false" showInfo="false" cssClass="table table-blue">
						<nv:dataTableColumn title="Account Name"    beanProperty="name" hrefurl="${pageContext.request.contextPath}/pd/account/account/$<id>?partnerId=${id}" sortable="true"  />
						<nv:dataTableColumn title="Account Manager" beanProperty="accountManager" />
						<nv:dataTableColumn title="Creation Date" 	beanProperty="creationDate" />
						<nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/pd/account/account/$<id>/edit?partnerId=${id}" style="width:20px;border-right:0px;"/>
	           			<nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/pd/account/account/$<id>?_method=DELETE&partnerId=${id}" style="width:20px;"/>
					</nv:dataTable>
			</fieldset>
		</div>
		
		<div class="row">
			<div class="col-xs-12 back-to-list" align="center">
				<button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/partner/partner'">
					<span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list" />
				</button>
			</div>
		</div>
    </div>
   </div>
 </div>
</div>

	