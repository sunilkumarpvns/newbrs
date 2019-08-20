(function ($) {

    $.fn.treeTableView = function () {

        var $table = this.find('tbody');
        var rows = $table.find('tr');
        var reverseHide = function (table, element) {
            var
                $element = $(element),
                id = $element.data('id'),
                children = table.find('tr[data-parent="' + id + '"]');

            if (children.length) {
                children.each(function (i, e) {
                    reverseHide(table, e);
                });

                $element
                    .find('.glyphicon-chevron-down')
                    .removeClass('glyphicon-chevron-down')
                    .addClass('glyphicon-chevron-right');

                children.hide();
            }
        };

        rows.each(function (index, row) {
            var
                $row = $(row),
                level = $row.data('level'),
                id = $row.data('id'),
                $columnName = $row.find('td[data-column="name"]'),
                children = $table.find('tr[data-parent="' + id + '"]');

            if (children.length) {
                var expander = $columnName.prepend('' + '<span class="treegrid-expander glyphicon glyphicon-chevron-right"></span>' + '');
                //by default hide all the children
                children.hide();
                //bind a click event to expand the child
                expander.on('click', function (e) {
                    var $target = $(e.target);
                    if ($target.hasClass('glyphicon-chevron-right')) {
                        $target
                            .removeClass('glyphicon-chevron-right')
                            .addClass('glyphicon-chevron-down');
                        children.show();
                    } else {
                        $target
                            .removeClass('glyphicon-chevron-down')
                            .addClass('glyphicon-chevron-right');
                        reverseHide($table, $row);
                    }
                });
            }
            $columnName.prepend('' + '<span class="treegrid-indent" style="width:' + 20 * level + 'px"></span>' + '');
        });
    };
    // Reverse hide all elements
}(jQuery));


function selectchildcheckbox(currentSelectedBox,isChecked,columnno){
    var parentrowid=currentSelectedBox.attr("id");
    var childcheckbox;
    currentSelectedBox.closest("tr").siblings("tr[data-parent="+parentrowid+"]").each(function(){
        childcheckbox=$(this).children("td").eq(columnno).children().eq(0);
        if(isChecked){
            childcheckbox.prop('checked',true);
        }else{
            childcheckbox.prop('checked',false);
        }
        selectchildcheckbox(childcheckbox,childcheckbox.prop('checked'),columnno);
    });
}

function selectparentcheckbox(currentrow,isChecked,columnno){
    var currentrowid=currentrow.parent().parent().prop('data-parent');
    var parentCheckBox;
    currentrow.parent().parent().siblings("tr[data-id="+currentrowid+"]").each(function(){
        parentCheckBox=$(this).children("td").eq(columnno).children().eq(0);
        if(isChecked){
            parentCheckBox.prop('checked',true);
        }/* else{
		        	   parentCheckBox.prop('checked',false);
		           } */
        selectparentcheckbox(parentCheckBox,parentCheckBox.prop('checked'),columnno);
    });
}