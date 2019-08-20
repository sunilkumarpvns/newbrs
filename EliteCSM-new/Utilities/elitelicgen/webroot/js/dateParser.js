function getParsedDate(strDate,dateformat) 
{
	var strDate;
	var strDateArray;
	var strDay;
	var strMonth;
	var strYear;
	var intday;
	var intMonth;
	var intYear;
	var strSeparatorArray = new Array("-"," ","/",".",",");
	var intElementNr;
	var err = 0;
	var strMonthArray = new Array(12);
	
	strMonthArray[0] = "Jan";
	strMonthArray[1] = "Feb";
	strMonthArray[2] = "Mar";
	strMonthArray[3] = "Apr";
	strMonthArray[4] = "May";
	strMonthArray[5] = "Jun";
	strMonthArray[6] = "Jul";
	strMonthArray[7] = "Aug";
	strMonthArray[8] = "Sep";
	strMonthArray[9] = "Oct";
	strMonthArray[10] = "Nov";
	strMonthArray[11] = "Dec";

	var mmIndex = (dateformat.toUpperCase()).indexOf("MM");
	var ddIndex = (dateformat.toUpperCase()).indexOf("DD");
	var yyIndex = (dateformat.toUpperCase()).indexOf("YYYY");
	
	var dIndex = 0;
	var mIndex = 0;
	var yIndex = 0;	
	
		
	
	for (intElementNr = 0; intElementNr < strSeparatorArray.length; intElementNr++) 
	{
		if (strDate.indexOf(strSeparatorArray[intElementNr]) != -1) 
		{
			strDateArray = strDate.split(strSeparatorArray[intElementNr]);			
			strDay = strDateArray[0];					
			strMonth = strDateArray[1];					
			strYear = strDateArray[2];	
		}
	}
		
	if (strYear.length == 2)
	{
		if(parseInt(strYear) < 90)
			strYear = '20' + strYear;
		else
			strYear = '19' + strYear;
	}
		
	intMonth = parseInt(strMonth);

	if (isNaN(intMonth)) 
	{
		for (i = 0;i<12;i++) 
		{
			if (strMonth.toUpperCase() == strMonthArray[i].toUpperCase()) 
			{
				intMonth = i+1;
				strMonth = strMonthArray[i];
				i = 12;
			}
		}	
	}

	strDate = strDay + "," + strMonthArray[intMonth-1] + "," + strYear ;		
	return strDate;
}


var MONTH_NAMES=new Array('January','February','March','April','May','June','July','August','September','October','November','December','Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');
var DAY_NAMES=new Array('Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sun','Mon','Tue','Wed','Thu','Fri','Sat');

function LZ(x){return(x<0||x>9?"":"0")+x}

function _getInt(str,i,minlength,maxlength)
{
	for(var x=maxlength;
x>=minlength;
x--)
{
	var token=str.substring(i,i+x);
if(token.length < minlength)
{
	return null;
}
if(_isInteger(token))
{
	return token;
}
}
return null;
}

function _isInteger(val)
{
	var digits="1234567890";
for(var i=0;
i < val.length;
i++)
{
	if(digits.indexOf(val.charAt(i))==-1)
{
	return false;
}
}
return true;
}

//THIS FUNCTION RETURNS A VALID DATE STRING TO CREATE A DATE OBJECT FROM THE GIVEN STRING AND ITS FORMAT
function getDateFromFormat(val,format)
{
	
	val=val+"";
format=format+"";
var i_val=0;
var i_format=0;
var c="";
var token="";
var token2="";
var x,y;
var now=new Date();
var year=now.getYear();
var month=now.getMonth()+1;
var date=1;
var hh=now.getHours();
var mm=now.getMinutes();
var ss=now.getSeconds();
var ampm="";
while(i_format < format.length)
{
	c=format.charAt(i_format);
token="";
while((format.charAt(i_format)==c) &&(i_format < format.length))
{
	token += format.charAt(i_format++);
}
if(token=="yyyy" || token=="yy" || token=="y")
{
	if(token=="yyyy")
{
	x=4;
y=4;
}
if(token=="yy")
{
	x=2;
y=2;
}
if(token=="y")
{
	x=2;
y=4;
}
year=_getInt(val,i_val,x,y);
if(year==null)
{
	return 0;
}
i_val += year.length;
if(year.length==2)
{
	if(year > 89)
{
	year=1900+(year-0);
}
else
{
	year=2000+(year-0);
}
}
}
else if(token=="MMM"||token=="NNN")
{
	month=0;
for(var i=0;
i<MONTH_NAMES.length;
i++)
{
	var month_name=MONTH_NAMES[i];
if(val.substring(i_val,i_val+month_name.length).toLowerCase()==month_name.toLowerCase())
{
	if(token=="MMM"||(token=="NNN"&&i>11))
{
	month=i+1;
if(month>12)
{
	month -= 12;
}
i_val += month_name.length;
break;
}
}
}
if((month < 1)||(month>12))
{
	return 0;
}
}
else if(token=="EE"||token=="E")
{
	for(var i=0;
i<DAY_NAMES.length;
i++)
{
	var day_name=DAY_NAMES[i];

if(val.substring(i_val,i_val+day_name.length).toLowerCase()==day_name.toLowerCase())
{
	i_val += day_name.length;
break;
}
}
}
else if(token=="MM"||token=="M")
{
	month=_getInt(val,i_val,token.length,2);
if(month==null||(month<1)||(month>12))
{
	return 0;
}
i_val+=month.length;
}
else if(token=="dd"||token=="d")
{
	date=_getInt(val,i_val,token.length,2);
if(date==null||(date<1)||(date>31))
{
	return 0;
}
i_val+=date.length;
}
else if(token=="hh"||token=="h")
{
	hh=_getInt(val,i_val,token.length,2);
if(hh==null||(hh<1)||(hh>12))
{
	return 0;
}
i_val+=hh.length;
}
else if(token=="HH"||token=="H")
{
	hh=_getInt(val,i_val,token.length,2);
if(hh==null||(hh<0)||(hh>23))
{
	return 0;
}
i_val+=hh.length;
}
else if(token=="KK"||token=="K")
{
	hh=_getInt(val,i_val,token.length,2);
if(hh==null||(hh<0)||(hh>11))
{
	return 0;
}
i_val+=hh.length;
}
else if(token=="kk"||token=="k")
{
	hh=_getInt(val,i_val,token.length,2);
if(hh==null||(hh<1)||(hh>24))
{
	return 0;
}
i_val+=hh.length;
hh--;
}
else if(token=="mm"||token=="m")
{
	mm=_getInt(val,i_val,token.length,2);
if(mm==null||(mm<0)||(mm>59))
{
	return 0;
}
i_val+=mm.length;
}
else if(token=="ss"||token=="s")
{
	ss=_getInt(val,i_val,token.length,2);
if(ss==null||(ss<0)||(ss>59))
{
	return 0;
}
i_val+=ss.length;
}
else if(token=="a")
{
	if(val.substring(i_val,i_val+2).toLowerCase()=="am")
{
	ampm="AM";
}
else if(val.substring(i_val,i_val+2).toLowerCase()=="pm")
{
	ampm="PM";
}
else
{
	return 0;
}
i_val+=2;
}
else
{
	if(val.substring(i_val,i_val+token.length)!=token)
{
	return 0;
}
else
{
	i_val+=token.length;
}
}
}
if(i_val != val.length)
{
	return 0;
}
if(month==2)
{
	if( ((year%4==0)&&(year%100 != 0) ) ||(year%400==0) )
{
	if(date > 29)
{
	return 0;
}
}
else
{
	if(date > 28)
{
	return 0;
}
}
}
if((month==4)||(month==6)||(month==9)||(month==11))
{
	if(date > 30)
{
	return 0;
}
}
if(hh<12 && ampm=="PM")
{
	hh=hh-0+12;
}
else if(hh>11 && ampm=="AM")
{
	hh-=12;
}
var newdate=new Date(year,month-1,date,hh,mm,ss);
return newdate.getTime();
}
