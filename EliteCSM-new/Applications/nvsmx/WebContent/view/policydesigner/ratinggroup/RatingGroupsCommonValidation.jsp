<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%--
  Created by IntelliJ IDEA.
  User: dhyani
  Date: 12/6/17
  Time: 5:09 PM
  To change this template use File | Settings | File Templates.
--%>

<div id="confirmAlert" class="modal fade" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h5 class="modal-title"> Are You Sure? </h5>
      </div>
        <div class="modal-body" align="center"><p><s:text name="rating.group.delete.confirmation.msg" /></p></div>
        <div class="modal-footer">
          <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="return false"><s:text name="button.no"/></button>
          <button class="btn btn-primary" onclick="submit()" id="btnYes"><s:text name="button.yes"/> </button>
        </div>
    </div>
  </div>
</div>