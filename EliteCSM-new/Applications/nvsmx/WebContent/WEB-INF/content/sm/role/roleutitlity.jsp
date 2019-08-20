<script language="javascript1.2" type="text/javascript">
    var nodes = [];
    function selectParent(data,divId) {
        var parentNode = $(divId).treeview('getParent', data);
        console.log("Parent Data : " + parentNode.text);
        if (parentNode.nodes == undefined) {
            return;
        } else {
            selectParent(parentNode,divId);
            parentNode.state.checked = true;
        }
    }

    function selectChild(data,divId) {
        var nodes = data.nodes;
        if (nodes == undefined) {
            return;
        } else {
            $(divId).treeview('expandNode', [data.nodeId, {silent: true}]);
            for (var i = 0; i < nodes.length; i++) {
                var node = nodes[i];
                $(divId).treeview('checkNode', [node.nodeId, {silent: true}]);
                selectChild(nodes[i],divId);

            }
        }
    }

    function unSelectChild(data,divId) {
        var nodes = data.nodes;
        if (nodes == undefined) {
            return;
        } else {
            $(divId).treeview('expandNode', [data.nodeId, {silent: true}]);
            for (var i = 0; i < nodes.length; i++) {
                var node = nodes[i];
                $(divId).treeview('uncheckNode', [node.nodeId, {silent: true}]);
                unSelectChild(nodes[i], divId);
            }
        }
    }

    function showAccessRightsForPolicy(key,divId) {

        var keyId = "[id='"+key+"']";
        var value = $(keyId).val();
        var showPolicyFrame ="#"+divId;
        $(showPolicyFrame).treeview({data: value, showCheckbox: true, multiSelect: true});
        $(showPolicyFrame).on('nodeChecked', function (event, data) {
            selectParent(data,showPolicyFrame);
            selectChild(data,showPolicyFrame);
            var curdata = $(showPolicyFrame).data('treeview').getNode(0);
            $(keyId).val("[" + JSON.stringify(curdata) + "]");
        });
        $(showPolicyFrame).on('nodeUnchecked', function (event, data) {
            unSelectChild(data,showPolicyFrame);
            var curdata = $(showPolicyFrame).data('treeview').getNode(0);
            $(keyId).val("[" + JSON.stringify(curdata) + "]");
        });


        $("#btnExpandAll").removeAttr("style", "display:none");
        $("#btnCollapseAll").removeAttr("style", "display:none");
    }

    function selectLeafNode(parentNode, data) {
        for (var i = 0; i < nodes.length; i++) {
            var nodeName = nodes[i];
            if (nodeName["name"] == parentNode) {
                nodeName["value"] = nodeName["value"] + data + ",";
                break;
            }

        }
    }

    function setJsonStringFromNode() {
        var jsonStr = "";
        for (var i = 0; i < nodes.length; i++) {
            if ($.trim(jsonStr.length) == 0) {
                jsonStr = JSON.stringify(nodes[i]);
            } else {
                jsonStr = jsonStr + "|" + JSON.stringify(nodes[i]);
            }

        }

        $("#jsonDataArray").val(jsonStr);//=jsonStr;
    }



    function expandAll(divId) {
        $('#'+divId).treeview('expandAll', {silent: true});
    }
    function collapseAll(divId) {

        $('#'+divId).treeview('collapseAll', {silent: true});
    }

    function setAccessRightsValues(){
        setJsonStringFromNode();
        return true;
    }
</script>