<%@ taglib prefix="nv" uri="http://www.elitecore.com/nvsmx/tags" %>
<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 13/12/17
  Time: 5:53 PM
  To change this template use File | Settings | File Templates.
--%>
<div class="modal fade" id="licenseViewDialog" tabindex="-1" role="dialog" aria-labelledby="licenseViewDialogLabel" aria-hidden="true" >
    <div class="modal-dialog" style="width: 80%">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="licenseDialog"> <s:text name="license" ></s:text> </h4>
            </div>
            <div class="modal-body" id="content">

                <nv:dataTable
                        id="LicenseDataListAsJsonOfView"
                        list="${licenseDataListAsJson}"
                        rows="50"
                        width="100%"
                        showPagination="false"
                        showInfo="false"
                        cssClass="table table-blue">
                    <nv:dataTableColumn title="License Name" beanProperty="key" width="40%" />
                    <nv:dataTableColumn title="Version" beanProperty="version" width="20%"/>
                    <nv:dataTableColumn title="Validity" beanProperty="value" width="40%"/>
                </nv:dataTable>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal"><s:text name="button.close" /></button>
            </div>
        </div>
    </div>
</div>

