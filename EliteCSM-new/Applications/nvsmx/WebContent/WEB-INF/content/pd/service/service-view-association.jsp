<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title"><s:text name="service.associations" /></h3>
    </div>
    <div class="panel-body">
        <div class="row">
            <div class="row">
                <div class="panel-body">
                    <fieldset class="fieldSet-line">
                        <legend align="top"><s:text name="service.productOffers"></s:text></legend>
                        <nv:dataTable id="productOfferAssociation" list="${productOffersAssociationListAsJson}"
                                      width="100%"
                                      showPagination="true" showFilter="true" showInfo="false"
                                      cssClass="table table-blue">
                            <nv:dataTableColumn title="Name" beanProperty="Name"
                                                hrefurl="${pageContext.request.contextPath}/pd/productoffer/product-offer/$<id>"
                                                sortable="true"/>
                        </nv:dataTable>
                    </fieldset>
                </div>
            </div>
            <div class="row">
                <div class="panel-body">
                    <fieldset class="fieldSet-line">
                        <legend align="top"><s:text name="service.diameterGatewayProfiles"></s:text></legend>
                        <nv:dataTable id="diameterGatewayProfileAssociation" list="${diameterProfilesAssociationListAsJson}"
                                      width="100%"
                                      showPagination="true" showFilter="true" showInfo="false"
                                      cssClass="table table-blue">
                            <nv:dataTableColumn title="Name" beanProperty="name"
                                                hrefurl="${pageContext.request.contextPath}/sm/gatewayprofile/diameter-gateway-profile/$<id>"
                                                sortable="true"/>
                        </nv:dataTable>
                    </fieldset>
                </div>
            </div>
            <div class="row">
                <div class="panel-body">
                    <fieldset class="fieldSet-line">
                        <legend align="top"><s:text name="service.radiusGatewayProfiles"></s:text></legend>
                        <nv:dataTable id="radiusGatewayProfileAssociation" list="${radiusProfilesAssociationListAsJson}"
                                      width="100%"
                                      showPagination="true" showFilter="true" showInfo="false"
                                      cssClass="table table-blue">
                            <nv:dataTableColumn title="Name" beanProperty="name"
                                                hrefurl="${pageContext.request.contextPath}/sm/gatewayprofile/radius-gateway-profile/$<id>"
                                                sortable="true"/>
                        </nv:dataTable>
                    </fieldset>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-12 back-to-list" align="center">
                    <button type="button" class="btn btn-primary btn-sm" id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/pd/service/service'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list" />
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>