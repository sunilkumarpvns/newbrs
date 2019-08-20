<%--
  User: jaidiptrivedi
--%>
<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="dataservicetype.create"/></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/dataservicetype" id="dataServiceTypeForm" action="data-service-type" method="post"
                cssClass="form-horizontal" validate="true" labelCssClass="col-xs-12 col-sm-4 col-lg-3"
                validator="validateForm('create','')"
                elementCssClass="col-xs-12 col-sm-8 col-lg-9">
        <s:token/>
        <div class="row">
            <div class="col-sm-9 col-lg-7">
                <s:textfield name="name" key="dataservicetype.name" id="serviceTypeName"
                             cssClass="form-control focusElement"
                             tabindex="1"/>
                <s:textarea name="description" key="dataservicetype.description" cssClass="form-control" rows="2"
                            tabindex="2"/>
                <s:textfield id="serviceIdentifier" name="serviceIdentifier" key="dataservicetype.serviceidentifier"
                             cssClass="form-control" tabindex="3" type="number"
                             onkeypress="return isNaturalInteger(event);"
                             onblur="verifyUniqueness('serviceIdentifier','create','','com.elitecore.corenetvertex.pd.dataservicetype.DataServiceTypeData','','serviceIdentifier');"/>
                <s:select list="allRatingGroups" cssClass="form-control select2" name="ratingGroupIds"
                          key="dataservicetype.ratinggroup" listValue="nameAndIdentifier" listKey="id" multiple="true"
                          tabindex="4" cssStyle="width:100%"/>
            </div>
            <div id="serviceDataFlow">
                <div class="col-sm-12">
                    <table id='defaultServiceDataFlowTable' class="table table-blue table-bordered">
                        <caption class="caption-header">
                            <s:text name="dataservicetype.servicedataflow"/>
                            <div align="right" class="display-btn">
											<span class="btn btn-group btn-group-xs defaultBtn"
                                                  onclick="addServiceDataFlow();" tabindex="5"
                                                  id="addRow"> <span class="glyphicon glyphicon-plus"></span>
											</span>
                            </div>
                        </caption>
                        <thead>
                        <th><s:text name="dataservicetype.servicedataflow.flowaccess"/></th>
                        <th><s:text name="dataservicetype.servicedataflow.protocol"/></th>
                        <th><s:text name="dataservicetype.servicedataflow.sourceip"/></th>
                        <th><s:text name="dataservicetype.servicedataflow.sourceport"/></th>
                        <th><s:text name="dataservicetype.servicedataflow.destinationip"/></th>
                        <th><s:text name="dataservicetype.servicedataflow.destinationport"/></th>
                        <th></th>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
                <div class="col-sm-6">
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <s:submit cssClass="btn  btn-sm btn-primary" type="button" role="button" tabindex="13"><span
                            class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></s:submit>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="5"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/pd/dataservicetype/data-service-type'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> <s:text name="button.list"/>
                    </button>
                </div>
            </div>
            </s:form>
        </div>
    </div>

</div>
<%@include file="data-service-type-utility.jsp" %>
