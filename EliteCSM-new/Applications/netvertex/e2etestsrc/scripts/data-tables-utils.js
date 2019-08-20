generateStatusSummary(3, 'SEC');

var table = $('#detailReportTable').DataTable({
    'ordering': true,
    'iDisplayLength': 100
});


$('#detailReportTable span.glyphicon').on('click', function() {
    var tr = $(this).closest('tr');
    var row = table.row(tr);
    if (row.child.isShown()) {
        row.child.hide();
        tr.removeClass('shown');
    } else {
        tr.addClass('shown');
        row.child(format(row.data())).show();
        tr.addClass('shown');
    }
});


function format(data) {
    var htmlFileName = data[2];
    if (htmlFileName.includes('href')) {
        htmlFileName = getTitle(htmlFileName);
    }
    htmlFileName = htmlFileName.trim();
    htmlFileName = 'netvertex/'+htmlFileName +'/'+htmlFileName+'.html';
    var htmlResponse;
    $.ajax({
        url: htmlFileName,
        dataType: 'html',
        async: false,
        success: function(response) {
            htmlResponse = response;
        }
    });
    return htmlResponse;
}

function getTitle(htmlFileName) {
    var anchors = $('<div/>').append(htmlFileName).find('a').get();
    return anchors[0].innerText;

}
