
function isNumber(val){
	nre= /^\d+$/;
	var regexp = new RegExp(nre);
	if(!regexp.test(val))
	{
		return false;
	}
	return true;
}


// import jquery.class.js befor import this js file
var HCategoriesData=$.Class.extend({
	init : function(){
	   this.categories=[];	
	},
	setCategories : function(categories){
		this.categories=categories;
	},
	getCategories : function(){
		return this.categories;
	}
	
});

var HPointData=$.Class.extend({
	init : function(){
		this.name='';
		this.color='';
		this.yPoints=[]; // for numeric value must set y parameter
		this.xPoints='';
	},
	setName : function(name){
		this.name=name;
	},
	setColor : function(color){
		this.color=color;
	},
	setYPoints : function(y){
		this.yPoints.push(Number(y));
	},
	setXPoint : function(x){
		this.xPoint=Number(x);
	},
	getName : function(){
		return this.name;
	},
	getColor : function(){
		return this.color;
	},
	getYPoints : function(){
		return this.yPoints; 
	},
	getXPoint : function(){
		return this.xPoint;
	},
	buildPointData : function(){
		var pointData={};
		// check for xPoint 
		// if it is null ,consider first value of yPoints-->  y:1
		// if it is not null ,building point data like [xPoint,yPoint[0],yPoint[1],yPoint[2]..]
		// TODO EC : check value null or not  for every attributes
		/*
			pointData={
					"y":this.yPoints[0],
					"color":this.color
			};
		*/
		//alert("buildPointData calls ");
	  return pointData;			
		
	}
});

var HSeriesData=$.Class.extend({
	init : function(){
       this.pointList=[];
       this.color='';
       this.name='';
       this.type='';// data type :string
       this.yAxis=0;//possible data types are :Number|String
       this.stack=0;
	},
	setPointList : function(pointList){
		this.pointList=pointList;
	},
	setStackData : function(stack){
		this.stack = stack;
	},
	setColor : function(color){
		this.color=color;
	},
	setName : function(name){
		this.name=name;
	},
	setYAxis : function(yAxis){
		if(isNumber(yAxis)){
			this.yAxis=Number(yAxis);
		}else{
			this.yAxis=yAxis;
		}
	},
	setType: function(type){
		this.type=type;
	},
	getYAxis : function(){
		return this.yAxis; 
	},
	getType : function(){
		return this.type;
	},
	getStackData : function(){
		return this.stack;
	},
	getPointList : function(){
		return this.pointList; 
	},
	getColor : function(){
		return this.color;
	},
	getName : function(){
		return this.name;
	},
	addPoint : function(pointData){
		//if(isPointData(pointData))
		 this.pointList.push(pointData);
	},
	buildSeriesData : function(){
		// set default option in this method
		// override this method for specific chart
		return null;
	}
});


var HSeries=$.Class.extend({
	init : function(){
		// initialize property member with default values
		this.series=[];
	},
	setSeries : function(series){
		this.series=series;
	},
	getSeries : function(){
		return this.series;
	},
	addSeries : function(series){
			this.series.push(series);	
	}
	
});