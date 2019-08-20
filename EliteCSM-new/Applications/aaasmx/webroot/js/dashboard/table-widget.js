


$.tableWidget = {
		createTableWidget : function (interval) {
			var widgetHandler  = {
					div : this,
					firstCall : true,
					intervalCounter : 0,
					renderData : function(data) {
						this.div.renderTable(data);
					},
					updateData : function(data) {
						if(this.intervalCounter++ == interval) {
							this.div.find(".cellGreen").each(function(){
								$(this).removeClass("cellGreen");
							});
							this.intervalCounter = 0;
						}
						this.div.renderTableIds(data, this.firstCall);
						this.firstCall = false; 
					}
			};
			return new Widget($(this).attr("id"), widgetHandler);
		}
};

$.fn.extend({
	createDefaultTableWidget : $.tableWidget.createTableWidget
});
