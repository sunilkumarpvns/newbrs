   function generateStatusSummary(timeLapsedColumn,baseUnit) {
         var successCases = 0;
         failedTestCases = 0;
         totalTestCases = 0;
         totalExecutionTime = 0;
         $('#detailReportTable tbody tr').each(function() {
           totalTestCases++;
           var status = $(this).children().eq(4).text();
           var executionTime = $(this).children().eq(timeLapsedColumn).text();
           totalExecutionTime = parseInt(totalExecutionTime) + parseInt(executionTime);
           if (status == 'PASS') {
             successCases++;
           } else {
             failedTestCases++;
           }
         });
         var statusRow = '<tr><td>' + totalTestCases + '</td><td>' + successCases + '</td> <td>' + failedTestCases + '</td><td>' + convertMS(totalExecutionTime,baseUnit) + '</td></tr>';
         $('#summaryReportTable tbody').append(statusRow);
       }

    function convertMS(ms, baseUnit) {
    var m, s;
    var divisier = 1000;
    if (baseUnit == 'SEC') {
      divisier = 1;
    }
    s = Math.floor(ms / divisier);
    m = Math.floor(s / 60);
    s = s % 60;
    return m + "min " + s + "sec"
  };
