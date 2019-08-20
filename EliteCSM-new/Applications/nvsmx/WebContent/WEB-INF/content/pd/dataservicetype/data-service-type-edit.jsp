<%--
  User: jaidiptrivedi
--%>
<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/select2/css/select2.css"/>
<script src="${pageContext.request.contextPath}/select2/js/select2.full.js"></script>

<div class="panel panel-primary">
    <div class="panel-heading">
        <h3 class="panel-title"><s:text name="dataservicetype.update"/></h3>
    </div>
    <div class="panel-body">
        <s:form namespace="/pd/dataservicetype" action="data-service-type" method="post" cssClass="form-horizontal"
                validate="true" validator="validateForm('update','%{id}')" id="dataServiceType"
                labelCssClass="col-xs-12 col-sm-4 col-lg-3" elementCssClass="col-xs-12 col-sm-8 col-lg-9">
            <s:hidden name="_method" value="put"/>
            <s:token/>
            <div class="row">
                <div class="col-sm-9 col-lg-7">
                    <s:hidden name="id"/>
                    <s:textfield name="name" key="dataservicetype.name" id="serviceTypeName"
                                 cssClass="form-control focusElement"
                                 tabindex="1"/>
                    <s:textarea name="description" key="dataservicetype.description" cssClass="form-control" rows="2"
                                tabindex="2"/>
                    <s:textfield id="serviceIdentifier" name="serviceIdentifier" key="dataservicetype.serviceidentifier"
                                 cssClass="form-control" tabindex="3" type="number"
                                 onkeypress="return isNaturalInteger(event);"
                                 onblur="verifyUniqueness('serviceIdentifier','update','%{id}','com.elitecore.corenetvertex.pd.dataservicetype.DataServiceTypeData','','serviceIdentifier');"
                    />
                    <s:select list="allRatingGroups" cssClass="form-control select2" name="ratingGroupIds"
                              key="dataservicetype.ratinggroup" listValue="nameAndIdentifier" listKey="id"
                              multiple="true" tabindex="4" cssStyle="width:100%"/>
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
                            <tbody>
                            <s:hidden id="defServiceDataFlowListSize" value="%{defaultServiceDataFlows.size}"/>
                            <s:iterator value="defaultServiceDataFlows" var="defServiceDataFlow" status="stat">
                                <tr>
                                    <td style="width:18%"><s:select tabindex="6"
                                                                    name="defaultServiceDataFlows[%{#stat.index}].flowAccess"
                                                                    value="%{#defServiceDataFlow.flowAccess}"
                                                                    cssClass="form-control"
                                                                    list="@com.elitecore.corenetvertex.pd.dataservicetype.SDFFlowAccess@values()"
                                                                    listKey="val" listValue="displayValue"
                                                                    elementCssClass="col-xs-12"></s:select></td>
                                    <td style="width:12%"><s:select tabindex="7"
                                                                    name="defaultServiceDataFlows[%{#stat.index}].protocol"
                                                                    value="%{#defServiceDataFlow.protocol}"
                                                                    cssClass="form-control"
                                                                    list="@com.elitecore.corenetvertex.pd.dataservicetype.SDFProtocols@values()"
                                                                    listKey="val" listValue="displayValue"
                                                                    elementCssClass="col-xs-12"></s:select></td>
                                    <td><s:textfield tabindex="8" cssClass="form-control"
                                                     name="defaultServiceDataFlows[%{#stat.index}].sourceIP" type="text"
                                                     value="%{#defServiceDataFlow.sourceIP}"
                                                     elementCssClass="col-xs-12"/></td>
                                    <td><s:textfield tabindex="9" cssClass="form-control"
                                                     name="defaultServiceDataFlows[%{#stat.index}].sourcePort"
                                                     type="text" value="%{#defServiceDataFlow.sourcePort}"
                                                     elementCssClass="col-xs-12"/></td>
                                    <td><s:textfield tabindex="10" cssClass="form-control"
                                                     name="defaultServiceDataFlows[%{#stat.index}].destinationIP"
                                                     type="text" value="%{#defServiceDataFlow.destinationIP}"
                                                     elementCssClass="col-xs-12"/></td>
                                    <td><s:textfield tabindex="11" cssClass="form-control"
                                                     name="defaultServiceDataFlows[%{#stat.index}].destinationPort"
                                                     type="text" value="%{#defServiceDataFlow.destinationPort}"
                                                     elementCssClass="col-xs-12"/></td>
                                    <td><span tabindex="12" class="btn defaultBtn"
                                              onclick="$(this).parent().parent().remove();"><a><span
                                            class="delete glyphicon glyphicon-trash" title="delete"></span></a></span>
                                    </td>

                                </tr>
                            </s:iterator>

                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12" align="center">
                    <button class="btn btn-primary btn-sm" role="submit"
                            formaction="${pageContext.request.contextPath}/pd/dataservicetype/data-service-type/${id}"
                            tabindex="8"><span
                            class="glyphicon glyphicon-floppy-disk"></span> <s:text name="button.save"/></button>
                    <button type="button" id="btnCancel" class="btn btn-primary btn-sm" value="Cancel" tabindex="14"
                            style="margin-right:10px;"
                            onclick="javascript:location.href='${pageContext.request.contextPath}/pd/dataservicetype/data-service-type/${id}'">
                        <span class="glyphicon glyphicon-backward" title="Back"></span> DataServiceType
                    </button>
                </div>
            </div>
        </s:form>
    </div>
</div>
<script type="text/javascript">
    $(function () {
        if ('<s:text name="id"/>' == '<s:property value="@com.elitecore.corenetvertex.constants.CommonConstants@ALL_SERVICE_ID"/>') {
            $('#serviceTypeName').attr('readonly', 'true');
        }
    });

</script>
<%@include file="data-service-type-utility.jsp" %>
