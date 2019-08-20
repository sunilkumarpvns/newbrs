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
	                       onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${id}&auditPageHeadingName=${name}&refererUrl=/pd/account/account/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/pd/account/account/${id}/edit?partnerId=${partnerIds}'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/account/account/${id}?_method=DELETE&partnerId=${partnerIds}">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>
    
    <div class="panel-body" >
        <div class="row">
            <fieldset class="fieldSet-line">
              	<legend align="top"><s:text name="basic.detail" /></legend>
                <div class="row">
                    <div class="col-sm-4">
                        <div class="row">
                            <s:label key="account.name" value="%{name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="account.accountManager" value="%{accountManager}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="account.accountCurrency" value="%{accountCurrency}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
					
							<s:if test="%{creationDate != null}">
                            <s:set var="ctrDate">
                                <s:date name="%{creationDate}" format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
                            </s:set>
                            	<s:label key="account.creationDate" value="%{ctrDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                            </s:if>
                            <s:else>
                            <s:label key="account.creationDate" value="%{creationDate}" cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                            </s:else>	
 						</div>
                    </div>
                    <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <s:label key="account.timeZone" value="%{timeZone}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="account.lob" value="%{lob.name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="account.partnerGroup" value="%{partnerGroup.name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
							<s:label key="account.productSpecification" value="%{productSpecification.name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
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
               		
					<div class="row">
				    	<div class="panel-body" >
				        	<fieldset class="fieldSet-line" >
								<legend align="top"><s:text name="account.prefixes"></s:text></legend>
								<div style="text-align: right;">
									<button class="btn btn-primary btn-xs" style="padding-top: 3px; padding-bottom: 3px" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/prefixes/prefixes/new?accountId=${id}'">
										<span class="glyphicon glyphicon-plus-sign" title="Add"></span>
										<s:text name="account.prefixes" />
									</button>
								</div>
											
								<nv:dataTable id="PrefixesData" list="${prefixesDataAsJson}" showPagination="false" showInfo="false" cssClass="table table-blue">
									<nv:dataTableColumn title="Prefix"   	 beanProperty="prefix" hrefurl="${pageContext.request.contextPath}/pd/prefixes/prefixes/$<id>?accountId=${id}" sortable="true" tdCssClass="word-break"/>
									<nv:dataTableColumn title="Prefix Name"  beanProperty="name"/>
									<nv:dataTableColumn title="Country Code" beanProperty="countryCode"/>
									<nv:dataTableColumn title="Area Code"    beanProperty="areaCode"/>
									<nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-pencil'></span>" hrefurl="edit:${pageContext.request.contextPath}/pd/prefixes/prefixes/$<id>/edit?accountId=${id}" style="width:20px;border-right:0px;"/>
				 					<nv:dataTableColumn title=""  icon="<span class='glyphicon glyphicon-trash'></span>" hrefurl="delete:${pageContext.request.contextPath}/pd/prefixes/prefixes/$<id>?_method=DELETE&accountId=${id}" 	 style="width:20px;"  />
				 		   		</nv:dataTable>
							</fieldset>
						</div>
					</div>
					
				    <div class="row">
						<div class="col-xs-12 back-to-list" align="center">
							<button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/partner/partner/${partnerId}'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.partner"/></button>               		
						</div>
					</div>
				</div>	
            </fieldset>
        </div>
    </div>
</div>