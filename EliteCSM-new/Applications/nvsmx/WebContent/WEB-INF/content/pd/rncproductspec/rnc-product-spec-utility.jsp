<%@ taglib uri="/struts-tags/ec" prefix="s" %>

<script type="text/javascript">

    var i =  '<s:property value ="productOfferServicePkgRelDataList.size"/>';
    i = parseInt(i);
    function addServicePkgRelations() {
        $("#productspecsrvpkgrelTable tbody").append("<tr>" + $("#tempServiceTypeRow").find("tr").html() + "</tr>");
        var NAME = "name";
        $("#productspecsrvpkgrelTable").find("tr:last td:nth-child(1)").find("select").attr(NAME, 'productOfferServicePkgRelDataList[' + i + '].serviceId');
        $("#productspecsrvpkgrelTable").find("tr:last td:nth-child(2)").find("select").attr(NAME, 'productOfferServicePkgRelDataList[' + i + '].rncPackageId');
        i++;
    }
</script>

<table id="tempServiceTypeRow" style="display: none;">
    <tr>
        <td><s:select list="serviceDataList" listValue="alias" listKey="id" cssClass="form-control"
                      elementCssClass="col-xs-12"></s:select></td>
        <td><s:select list="rncPkgDataList" listValue="name" listKey="id" cssClass="form-control group"
                      elementCssClass="col-xs-12"></s:select></td>
        <td style='width:35px;'>
            <span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'>
                <a><span class='delete glyphicon glyphicon-trash' title='delete'></span>
                </a>
        </span>
        </td>
    </tr>
</table>