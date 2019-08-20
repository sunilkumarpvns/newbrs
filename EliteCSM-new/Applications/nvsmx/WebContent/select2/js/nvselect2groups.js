/**
 * Created by jaidiptrivedi on 13/7/17.
 */

$(document).on('select2:unselecting', 'select[multiple]', function (e) {
    $(e.target).data('unselecting', true);
    if ($(e.params.args.data.element).attr('locked') == 'true') {
        var groupName = $(e.params.args.data.element).text();
        return addWarning(".popup","You can't remove '" + groupName + "' group.");
    }

}).on('select2:open', function (e) {
    var $target = $(e.target);
    if ($target.data('unselecting')) {
        $target.removeData('unselecting');
        $target.select2('close');
    }
});

