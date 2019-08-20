<div class="modal fade" id="PCCRuleAssociation" tabindex="-1" role="dialog" aria-labelledby="addSubscriptionLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title" id="addSubscriptionLabel">PCC Rule Association</h4>
      </div>

        <div class="modal-body">
          <nv:dataTable
                  id="PCCRuleDataGlobalSearch"
                  list="${qosProfileDetailsjson}"
                  beanType="com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData"
                  width="100%"
                  showPagination="false"
                  showInfo="false"
                  cssClass="table table-blue"
                  >
            <nv:dataTableColumn title="Qos Profile" beanProperty="Qos Profile"
                                tdCssClass="text-left text-middle word-break"
                                hrefurl="${pageContext.request.contextPath}/policydesigner/qos/QosProfile/view?qosProfileId=qosProfileId"
                                tdStyle="width:200px" />
            <nv:dataTableColumn title="Package"
                                beanProperty="Package"
                                hrefurl="${pageContext.request.contextPath}/policydesigner/pkg/Pkg/view?pkgId=pkgId"
                                tdCssClass="text-left text-middle word-break" tdStyle="width:100px" />
          </nv:dataTable>

        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-primary" data-dismiss="modal" >Close</button>
        </div>
    </div>
  </div>
</div>