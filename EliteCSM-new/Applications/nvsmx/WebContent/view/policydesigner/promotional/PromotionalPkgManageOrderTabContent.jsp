    <div class="row" style="padding: 8px 15px">
        <s:form namespace="/" action="promotional/policydesigner/pkg/Pkg/PromotionalPkg/manageOrderPromotionalPackages" id="promotionalManageOrderForm-%{key}"  method="post" cssClass="form-horizontal" labelCssClass="col-xs-4 col-lg-2 text-right" elementCssClass="col-xs-8 col-lg-10" >
            <s:hidden id="promotionalPackagesIdsAndOrders-%{key}" name="promotionalPackagesIdsAndOrders" />
            <s:hidden value="%{key}" name="groupName" />
            <s:hidden value="%{#session.staffBelongingGroupIds}" name="groupIds" />

            <table id="promotionalPkgData-<s:property value='key'/>"  class="table table-blue" width="100%">
                <thead>
                <tr>
                    <th id="dataTable.RowNumber" >#</th>
                    <th id="name" ><s:text name="pkg.name"/></th>
                    <th id="type"><s:text name="pkg.type"/></th>
                    <th id="status" ><s:text name="pkg.status"/></th>
                    <th id="packageMode"><s:text name="pkg.mode"/></th>
                </tr>
                </thead>
                <tbody id="tbody-<s:property value='key'/>">
                <s:iterator value="value" status="i">
                    <tr>
                        <td><s:hidden name="pkgGroupDataIdArray" value="%{id}"/>
                            <s:property value="%{#i.count}"/></td>
                        <td><s:hidden name="idArray" value="%{pkgData.id}"/>
                            <a href="${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=${pkgData.id}">
                                <s:property value="%{pkgData.name}"/>
                            </a>
                        </td>
                        <td><s:property value="%{pkgData.type}"/></td>
                        <td><s:property value="%{pkgData.status}"/></td>
                        <td><s:property value="%{pkgData.packageMode}"/></td>
                    </tr>
                </s:iterator>
                </tbody>
            </table>
            <div class="col-xs-12" id="rowOrder-<s:property value='key'/>"></div>
        </s:form>
    </div>
    <div class="row">
        <div class="col-xs-12" align="center">
            <button id="btnSave" class="btn btn-primary btn-sm"  value="Save" onclick="prepareParemeters('<s:property value="key"/>');" ><span class="glyphicon glyphicon-floppy-disk" title="save"></span> <s:text name="button.save"></s:text> </button>
            <button id="btnBack" class="btn btn-primary btn-sm"  value="Cancel" onclick="location.href='${pageContext.request.contextPath}/promotional/policydesigner/pkg/Pkg/PromotionalPkg/search?pkgType=PROMOTIONAL'"><span class="glyphicon glyphicon-backward" title="Back"></span>
                <s:text name="button.back"></s:text>
            </button>
        </div>
    </div>
</div>
<div id="alertFailure-<s:property value='key'/>" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content" style="width:270px;">
            <div class="modal-body modal-sm">
                <span id="noChange">No Change found in Packages Order !</span>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-danger" data-dismiss="modal" >Ok</button>
            </div>
        </div>
    </div>
</div>