/**
 *  This JS file allows you to render table and render table Data from JSON data.
 *  
 *  @author punit.j.patel
 */

var specilaCharReplacer = {
		replace : function (val){
			var replaceStr =  val.replace(/[^\w\s]/gi, "");
			return replaceStr.split(' ').join('');
		}
};



var json2html = (function () {
    var json2html = function(json) {
        this.data = json;
        this.columnHeaders = json["columnHeaders"];
        this.firstRow = json["firstRow"];
        this.rowGroupHeaders = json["rowGroupHeaders"];
        this.tableData = json["tableData"];
        
        var depth = 0;
        for (var i in this.firstRow) {
            depth = Math.max(depth, this.firstRow[i].length);
        }
        this.depth = depth;
    };

    function addTableHeader(data, table) {
        var tableHeaderRow = $("<tr/>");
        for( var i =0 ; i < data.rowGroupHeaders.length ; i++ ) {
        	tableHeaderRow.append($("<th/>").attr("rowspan", data.depth + 1).text(data.rowGroupHeaders[i]));	
        }
        for( var i =0 ; i < data.columnHeaders.length ; i++ ) {
        	tableHeaderRow.append($("<th/>").attr("colspan", data.firstRow.length ).text(data.columnHeaders[i]));	
        }
        table.append(tableHeaderRow);
        addSubHeaders(data, table);
    }
    
    function addSubHeaders(data, table) {
    	for (var index = 0; index < data.depth; index++) {
           var row = $("<tr/>");
           $.each(data.firstRow, function() {
        	   var value = (typeof this[index] != 'undefined') ? this[index] : '';
        	   var col = $("<td/>").text(value).addClass("Widget-TableGroupHeader");
        	   row.append(col);
           });
           table.append(row);	
        } 
    }
    
    function addTableRow(jQtable, data) {
    	jQtable.each(function()
        {
            $.each(data.tableData, function() {
            	var row = $("<tr/>");
            	// Add Row Group Header Columns
            	var columnIdValue = "";
            	for ( var j = 0; j < this.name.length; j++) {
            		var col = $("<td/>");
                   	col.text(this.name[j]);
                   	columnIdValue += this.name[j] + "_"; 
                    row.append(col);
				}
            	
            	 $.each(data.firstRow, function(index, item) {
            		var col = $("<td/>");
            		var id = columnIdValue+item.join("_");
            		col.attr("id", specilaCharReplacer.replace(id));
                   	//col.text();
                    row.append(col);
            	 });
            	 columnIdValue = "";
            	jQtable.append(row);	
           });
      });
		
    }
    
    
    function setIds(messageData, firstCall, div) {
    	for(var i=0; i<messageData.length; i++ ) {
    	    var tdId = specilaCharReplacer.replace(messageData[i].id);
    	    if(div.find("#"+tdId).text() != messageData[i].value) {
    	    	div.find("#"+tdId).text(messageData[i].value);
    			if(!firstCall) {
    				div.find("#"+tdId).addClass("cellGreen");
    			}
    	    }
    	    
    	}
    };
    

    function mergeCells(cells, attr) {
        var rs = 1;
        var old = null;
        cells.each(function() {
            if (old == null) {
                old = $(this);
                rs = 1;
            } else {
                if ($(this).text() == old.text()) {
                    rs++;
                    $(this).remove();
                } else {
                    if (rs > 1) {
                        old.attr(attr, rs);
                        rs = 1;
                    }
                    old = $(this);
                }
            }
        });
        if (rs > 1) {
            old.attr(attr, rs);
        }
    }
    
    json2html.prototype.renderTable = function(target) {
    	$(target).html('');
    	var table = $("<table cellpadding='0' cellspacing='0'><tbody></tbody></table>").attr("id", "twodstatstable_"+$(target).attr("id")).addClass("Widget-Table");
    	addTableHeader(this, table);
    	addTableRow(table, this);
    	for (var i = this.rowGroupHeaders.length; i > 0; i--) {
            mergeCells($('td:nth-child('+i+')', table), 'rowspan');
        }
 		$(target).append(table);
    };

    return json2html;
})();


	
//==================================================================================================


 $.renderTable = {
	 render : function(jsonData) {
	 	this.each(function() {
	 		 var html = new json2html(jsonData);
	         html.renderTable($(this));
	 	});
	 },
	 renderIds : function (messageData, firstCall) {
			var div = this;
			for(var i=0; i<messageData.length; i++ ) {
			    var tdId = specilaCharReplacer.replace(messageData[i].id);
			    var isValueChange = div.find("#"+tdId).text() != messageData[i].value;
			    if( isValueChange ) {
			    	//add length check
			    	div.find("#"+tdId).text(messageData[i].value);
					if(!firstCall) {
						div.find("#"+tdId).addClass("cellGreen");
					}
			    }
			    
			    if(messageData[i].value == 'null'){
			       div.find("#"+tdId).text('no data found');
			    }
			    
			    alarmManager.manageAlarm(div, tdId, isValueChange);
			}
	}
};
 
 
var alarmManager = {
		manageAlarm : function(div, tdId, isValueChange) {
			var alarmSpan = div.find("span."+tdId);
			if( alarmSpan.length > 0 ) {
				var alarmSpanID = $(alarmSpan).attr("id");
				var chngCounter = alarmSpanID.substring(alarmSpanID.lastIndexOf("_")+1,alarmSpanID.lastIndexOf(","));
				var idleCounter = alarmSpanID.substring(alarmSpanID.lastIndexOf(",")+1);
				if( isValueChange ) {
					chngCounter++;
					console.log("current counter : "+chngCounter);
					if(chngCounter >= 10 ) {
						 div.find("#"+ tdId).removeClass("cellYellow");
						 div.find("#"+ tdId).addClass("cellRed");
					 } else if ( chngCounter >= 5 ) {
						 div.find("#"+ tdId).addClass("cellYellow");
					 } 
				} else {
					if( chngCounter >= 5) {
						 idleCounter++;
						 if(idleCounter >= 20 ) {
							 div.find("#"+ tdId).removeClass("cellYellow");
							 div.find("#"+ tdId).removeClass("cellRed");
							 idleCounter =0;
							 chngCounter =0;
						 }
					 } else {
						 idleCounter =0;
						 chngCounter =0;
					 }
				}
				alarmSpan.attr("id", this.createAlarmID(tdId, chngCounter, idleCounter));
				/*Generate Span id with chngCounter and idleCounter's value are 0*/
			} else {
				var span = $("<span/>");
				span.attr("id", this.createAlarmID(tdId, 0, 0));
				span.addClass(tdId);
				div.append(span);
			}
			
		},
		createAlarmID : function(tdId, chngCounter, idleCounter) {
			 return new Date().getTime() + "_" +tdId + "_" + chngCounter + "," + idleCounter;
		 }
}; 

 

$.fn.extend({
	renderTable : $.renderTable.render,
	renderTableIds : $.renderTable.renderIds
});	





