<%@taglib uri="/struts-tags/ec" prefix="s" %>
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
             &nbsp; <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom"
                        title="<s:text name="audit.history"/>"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/commons/audit/Audit/view?actualId=${id}&auditableId=${id}&auditPageHeadingName=<s:property value="name"/>&refererUrl=/pd/rncproductspec/rnc-product-spec/${id}'">
					<span class="glyphicon glyphicon-eye-open"></span>
				</button>
			</span>
			<span class="btn-group btn-group-xs">
					<button type="button" class="btn btn-default header-btn" data-toggle="tooltip"
                            data-placement="bottom" title="<s:text name="tooltip.edit"/>"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/pd/rncproductspec/rnc-product-spec/${id}/edit?groupIds=${groups}'">
					<span class="glyphicon glyphicon-pencil"></span>
				</button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/pd/rncproductspec/rnc-product-spec/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="<s:text name="tooltip.delete"/>" >
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>
    <div class="panel-body">
            <div class="row">
                <fieldset class="fieldSet-line">
                    <legend align="top">
                        <s:text name="basic.detail"/>
                    </legend>
                <div class="col-sm-4">
                    <div class="row">
                        <s:label key="product.spec.description" value="%{description}"
                                 cssClass="control-label light-text word-break" labelCssClass="col-sm-5 col-xs-4"
                                 elementCssClass="col-sm-7 col-xs-8"/>
                        <s:label key="product.spec.status" value="%{status}" cssClass="control-label light-text"
                                 labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                        <s:label key="product.spec.groups" value="%{groupNames}" cssClass="control-label light-text"
                                     labelCssClass="col-sm-5 col-xs-4" elementCssClass="col-sm-7 col-xs-8"/>
                    </div>
                </div>
                <div class="col-sm-4 leftVerticalLine">
					<div class="row">

						<s:if test="%{validityPeriod != null && validityPeriod != 0}">
							<s:label key="product.spec.validity.period"
								value="%{validityPeriod}  %{validityPeriodUnit}"
								cssClass="control-label light-text"
								labelCssClass="col-xs-4 col-sm-7"
								elementCssClass="col-xs-8 col-sm-5" />
						</s:if>

						<s:if test="%{availabilityStartDate != null}">
							<s:set var="availStartDate">
								<s:date name="%{availabilityStartDate}"
									format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
							</s:set>
							<s:label key="product.spec.availability.start.date"
								value="%{availStartDate}" cssClass="control-label light-text"
								labelCssClass="col-xs-4 col-sm-5"
								elementCssClass="col-xs-8 col-sm-7" />
						</s:if>
						<s:else>
							<s:label key="product.spec.availability.start.date"
								value="%{availabilityStartDate}"
								cssClass="control-label light-text"
								labelCssClass="col-sm-5 col-xs-4"
								elementCssClass="col-sm-7 col-xs-8" />
						</s:else>

						<s:if test="%{availabilityEndDate != null}">
							<s:set var="availEndDate">
								<s:date name="%{availabilityEndDate}"
									format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}" />
							</s:set>
							<s:label key="product.spec.availability.end.date"
								value="%{availEndDate}" cssClass="control-label light-text"
								labelCssClass="col-xs-4 col-sm-5"
								elementCssClass="col-xs-8 col-sm-7" />
						</s:if>
						<s:else>
							<s:label key="product.spec.availability.end.date"
								value="%{availabilityEndDate}"
								cssClass="control-label light-text"
								labelCssClass="col-sm-5 col-xs-4"
								elementCssClass="col-sm-7 col-xs-8" />
						</s:else>


					</div>
				</div>
                <div class="col-sm-4 leftVerticalLine">
                    <div class="row">
                        <s:if test="%{createdByStaff !=null}">
                            <s:label key="createdby" value="%{createdByStaff.userName}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                     elementCssClass="col-xs-8 col-sm-7"/>
                        </s:if>
                        <s:else>
                            <s:label key="createdby" value="" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                        </s:else>
                        <s:if test="%{createdDate != null}">
                            <s:set var="createdByDate">
                                <s:date name="%{createdDate}"
                                        format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
                            </s:set>
                            <s:label key="createdon" value="%{createdByDate}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                        </s:if>
                        <s:else>
                            <s:label key="createdon" value="" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                        </s:else>


                        <s:if test="%{modifiedByStaff !=null}">
                            <s:label key="modifiedby" value="%{modifiedByStaff.userName}"
                                     cssClass="control-label light-text" labelCssClass="col-xs-4 col-sm-5"
                                     elementCssClass="col-xs-8 col-sm-7"/>
                        </s:if>
                        <s:else>
                            <s:label key="modifiedby" value="" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                        </s:else>
                        <s:if test="%{modifiedDate != null}">
                            <s:set var="modifiedByDate">
                                <s:date name="%{modifiedDate}"
                                        format="%{@com.elitecore.nvsmx.system.util.NVSMXUtil@getDateFormat()}"/>
                            </s:set>
                            <s:label key="lastmodifiedon" value="%{modifiedByDate}" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                        </s:if>
                        <s:else>
                            <s:label key="lastmodifiedon" value="" cssClass="control-label light-text"
                                     labelCssClass="col-xs-4 col-sm-5" elementCssClass="col-xs-8 col-sm-7"/>
                        </s:else>
                    </div>
                </div>
                </fieldset>
            </div>
		
		<div class="row">
			<fieldset class="fieldSet-line">
				<legend align="top">
					<s:text name="product.spec.service.package.relation" />
				</legend>
				<div class="dataTables_wrapper form-inline dt-bootstrap no-footer">
					<nv:dataTable 
					    id="productSpecData"
						list="${productSpecificationDataAsJson}" 
						width="100%"
						showPagination="true"						
						cssClass="table table-blue"
						subTableUrl="/pd/rncproductspec/rnc-product-spec/*/viewDetails">
						<nv:dataTableColumn title="Service" beanProperty="serviceData.alias" />
						<nv:dataTableColumn title="Rnc Package" beanProperty="rncPackageData.name" />
						

					</nv:dataTable>
				</div>
			</fieldset>
			 <div class="row" >
                  	<div class="col-xs-12 back-to-list" align="center">	
                      	<button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/rncproductspec/rnc-product-spec'" tabindex="6"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                  	</div>
               </div>
		</div>
	</div>
</div>