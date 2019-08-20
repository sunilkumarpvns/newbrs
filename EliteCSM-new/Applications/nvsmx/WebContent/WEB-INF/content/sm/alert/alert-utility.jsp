<%@ taglib uri="/struts-tags/ec" prefix="s" %>
<style type="text/css">
    .treegrid-indent {
        width: 0px;
        height: 16px;
        display: inline-block;
        position: relative;
    }
    .treegrid-expander {
        width: 0px;
        height: 16px;
        display: inline-block;
        position: relative;
        left:-17px;
        cursor: pointer;
    }
</style>
<script src="${pageContext.request.contextPath}/js/treetableview/treetableview.js" type="text/javascript"></script>


<div class="row">
            <div id="alert-listner-rel">
                <div class="col-xs-12 col-sm-12">
                    <table class="table table-blue table-bordered" id="tree-table">
                        <caption class="caption-header"><s:text name="alert.title"/></caption>
                        <thead>
                        <tr>
                            <th><s:text name="alert.name"/></th>
                            <th><s:text name="enable.alert"/></th>
                            <th><s:text name="enable.flood.control"/></th>
                        </tr>
                        </thead>
                        <tbody>
                       <s:iterator value="alertDisplayDataList" status="stat">
                           <tr data-id='<s:property value="alert.enumName"/>'
                               data-parent='<s:property value="alert.parent"/>'
                               data-level='<s:property value="alert.orderNo"/>'>
                               <td data-column="name"><s:property value="alert.name"/></td>
                               <td>
                                   <s:if test="%{alertChecked}">
                                       <input checked id='<s:property value="alert.enumName"/>' type="checkbox"
                                              value='<s:property value="alert.enumName"/>'
                                              name="alertListenerRelDataList[<s:property value="%{#stat.index}"/>].type"
                                               <s:property value="disabledAttribute"/> />
                                   </s:if>
                                   <s:else>
                                       <input id='<s:property value="alert.enumName"/>' type="checkbox"
                                              value='<s:property value="alert.enumName"/>'
                                              name="alertListenerRelDataList[<s:property value="%{#stat.index}"/>].type"
                                              <s:property value="disabledAttribute"/> />
                                   </s:else>
                               </td>

                               <td>
                                   <s:if test="%{floodControl}">
                                       <input checked id='<s:property value="alert.enumName"/>' type="checkbox"
                                              value='true'
                                              name="alertListenerRelDataList[<s:property value="%{#stat.index}"/>].floodControl" <s:property value="disabledAttribute"/> />
                                   </s:if>
                                   <s:else>
                                       <input id='<s:property value="alert.enumName"/>' type="checkbox" value='true'
                                              name="alertListenerRelDataList[<s:property value="%{#stat.index}"/>].floodControl" <s:property value="disabledAttribute"/> />
                                   </s:else>

                               </td>
                           </tr>
                       </s:iterator>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
<script type="text/javascript">

    $("#tree-table").treeTableView();

    $("input[name$=type]").click(function(){
        selectchildcheckbox($(this),this.checked,1);
        //selectparentcheckbox($(this),this.checked,1);
        if(!this.checked){
            $(this).parent().parent().children("td").eq(2).children().eq(0).prop('checked',false);
            selectchildcheckbox($(this),false,2);
        }
    });

    $("input[name$=floodControl]").click(function(){
        selectchildcheckbox($(this),this.checked,2);
        if(this.checked){
            $(this).parent().parent().children("td").eq(1).children().eq(0).prop('checked',true);
            selectchildcheckbox($(this),true,1);
        }
    });






</script>