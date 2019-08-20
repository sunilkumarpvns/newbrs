/*
 */


var ajaxObject=null;

function initAjaxObject(){
    if (window.XMLHttpRequest) { // Non-IE browsers
        try{
            ajaxObject = new XMLHttpRequest();
        }catch(ex){}

    } else if (window.ActiveXObject) { // IE

        try{
            ajaxObject = new ActiveXObject("Microsoft.XMLHTTP");
        }catch(ex){}

    }
}

function getPackages(serviceType){

    if(serviceType == "0" )
        return;

   
    initAjaxObject();
    var url = "getPackageList.do?serviceType="+ serviceType;
    url += "&randomNumber="+ new Date().getTime();
    ajaxObject.onreadystatechange =refreshPackageList;

    try {

        ajaxObject.open("GET", url, true);

    } catch (e) {

    //  alert(e);

    }
    ajaxObject.send(null);
  
}

function refreshPackageList(){

    if (ajaxObject.readyState == 4) {

        if (ajaxObject.status == 200) {
            
           
            var xmlText = ajaxObject.responseXML;
            if(xmlText.getElementsByTagName('package-data').length<1)
                return;

            
            var packageList = document.forms[0].packageId;
            var numberOfItems = packageList.options.length;
            for (i=0; i<numberOfItems; i++) {
                deleteOption(packageList, i);
            }
            packageList.options.length=0;
            
            
            addOption(packageList, "Packages","0");
            for(var i=0; i<xmlText.getElementsByTagName('package-data').length; i++){
                var packageData = xmlText.getElementsByTagName('package-data')[i];
                var packageId = packageData.childNodes[0].firstChild.nodeValue;
                var packageName = packageData.childNodes[1].firstChild.nodeValue;
                if( packageId!= null && parseInt(packageId)>0  && packageName !=null && packageName.length>0)
                    addOption(packageList, packageName,packageId);
            }


        } else {

    //  alert("Error: " + ajaxObject.statusText);

    }

    }
}


function addOption(theSel, theText, theValue)
{
    var newOpt = new Option(theText, theValue);
    var selLength = theSel.length;
    if(selLength==null)
        selLength=0;
    
    theSel.options[selLength] = newOpt;
}

function deleteOption(theSel, theIndex)
{ 
    var selLength = theSel.length;
    if(selLength>0)
    {
        theSel.options[theIndex] = null;
    }
}








