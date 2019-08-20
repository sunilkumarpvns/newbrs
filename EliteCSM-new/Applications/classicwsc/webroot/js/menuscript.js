// Use Freely as long as following disclaimer is intact ::
//---------------------------------------------------------------
// Cross Browser Multi-Orientation 3 Tier Menu v1.0 4th December 2004
// This script written by Rik Comery. www.ricom.co.uk
// For support, visit the "Resources" section at www.ricom.co.uk           
// All rights reserved.   

// Please read the instructions.htm file for configuration instructions. If this is not available, the entire menu script 
// with images and instructions can be downloaded at http://ricom.co.uk/resources/navigation-menu/navigation-menu.php                                      

//== Configuration =================================================================================
var border = "F3F3F3";   
var menuWidth = 700;           // Width of entire menu. Use 0 for default
var cellPadding = 2;         // Cell Padding  
var borderWidth = 1;         // Border width (for no border, enter 0)  THIS VALUE APPLIES TO ALL MENUS  
var subMenuDelay = 1;        // Time sub menu stays visible for (in seconds). THIS VALUE APPLIES TO ALL MENUS
var fontDecoration = "none"  // Font decoration of all menu items (none, underline, overline)
var statusBar = "0" ;        // Message shown in status bar. 
                             //   Type "0" to show the URL. (normal behaviour of browser)
                             //   Type "1" to show the menu title
                             //   Type anything else to show a custom message.


// Main menu 
//var mainIndicate = "<img src='/images/arrow-down.gif' alt='' border='0'>";  // Symbol to show if a sub menu is present.  For no symbol, enter "none" 
var mainIndicate = "none";  // Symbol to show if a sub menu is present.  For no symbol, enter "none" 
var fontFace = "Arial";      // Font of main menu items
var fontSize = "12px";       // Font size of main menu items
var fontWeight = "normal"    // Font Weight of main menu items

// Level 1 sub menu
var sVerticalOffset = 0;     // Vertical offset of Sub Menu. (negative values move sub menu up) 
var sHorizontalOffset = 0;   // Horizontal offset of Sub Menu. (negative values move sub menu to the left) 
var subIndicate = "<img src='images/arrow-right.gif' alt='' border='0'>";   // Symbol to show if a sub menu is present.  For no symbol, enter "none" 
var sfontFace = "Arial";     // Font of main menu items
var sfontSize = "12px";      // Font size of main menu items
var sfontWeight = "normal";    // Font Weight of main menu items
var sTransparency = "100"    // Opacity of sub menus. 0 - 100.  (IE Only.)

// Level 2 sub menu 
var ssVerticalOffset = 2      // Vertical offset of Sub Menu. (negative values move sub menu up) 
var ssHorizontalOffset = 0    // Horizontal offset of Sub Menu. (negative values move sub menu to the left)
var ssfontFace = "Arial";     // Font of main menu items
var ssfontSize = "12px";      // Font size of main menu items
var ssfontWeight = "normal";    // Font Weight of main menu items
var ssTransparency = "100"    // Opacity of sub menus. 0 - 100.  (IE Only.)

//== End Configuration - Do not edit below this line =======================================================

function addMenu(back,backH,font,fontH,title,url,target){
i = Menu.length;
  Menu[i] = new Array(back,backH,font,fontH,title,url,target); 
  subMenu[i] = new Array();
  subSubMenu[i] = new Array();
}

function addSubMenu(back,backH,font,fontH,title,url,target){
j = subMenu[i].length;
  subMenu[i][j] = new Array(back,backH,font,fontH,title,url,target)
  subSubMenu[i][j] = new Array();    
}

function addSubSubMenu(back,backH,font,fontH,title,url,target){
k = subSubMenu[i][j].length;
  subSubMenu[i][j][k] = new Array(back,backH,font,fontH,title,url,target)  
}

var timer; 

function showMenu(orientation){
  if(orientation==""){orientation=="horizontal"}
  buildMenu(orientation);
  buildSubMenu();
  buildSubSubMenu();
}
  
function buildMenu(orientation){
  build = '<table width="100%" border="0" cellpadding="0" cellspacing="0" bgcolor="'+border+'" class="MenuText"><tr><td><table border="0" cellpadding="0" cellspacing="'+borderWidth+'" width="50%"><tr>';
  for (x=0;x<Menu.length; x++){
    build += '<td nowrap="nowrap" style="cursor:hand; background-color:'+Menu[x][0]+'" onclick="tdMouseClick(\'MenuLink'+x+'\')">';
    build += '<div align="center" id="Menu'+x+'" style="padding:'+cellPadding+'px; background-color:'+Menu[x][0]+'" onMouseOver="clearTimer(); hideAll(\'main\'); showSubMenu(\'Menu\','+x+',\''+orientation+'\'); showStatus(\'over\',\'Main\','+x+'); return true;" onMouseOut="timerHide(); ">';
    build += '<a id="MenuLink'+x+'" href="'+Menu[x][5]+'" target="'+Menu[x][6]+'" style="font-family:'+fontFace+';font-size:'+fontSize+';font-weight:'+fontWeight+';color:'+Menu[x][2]+';text-decoration:'+fontDecoration+'">'+Menu[x][4]
    if(mainIndicate.toLowerCase()!="none" && subMenu[x].length>0){build += '<span style="padding-left:3px">'+mainIndicate+'<\/span>';}    
    build += '<\/a><\/div><\/td>';
    if(orientation.toLowerCase()=="vertical" && x<(Menu.length-1)){build += "<\/tr><tr>"}
  }
  build += '<\/tr><\/table><\/td><\/tr><\/table>';  
  document.writeln(build);
}

function buildSubMenu(){
  for(y=0; y<subMenu.length; y++){   
    buildSub = '<div id="Sub'+y+'" style="position:absolute; left:0; top:0; visibility:hidden">';
    if(subMenu[y].length>0){ 
      buildSub += '<table border="0" cellpadding="0" cellspacing="0" bgcolor="'+border+'" style="filter: alpha(Opacity='+sTransparency+')"><tr><td><table border="0" cellpadding="0" cellspacing="'+borderWidth+'" width="100%" align="center">';
      for(z=0; z<subMenu[y].length; z++){  
        buildSub += '<tr><td nowrap="nowrap" onclick="tdMouseClick(\'SubLink'+y+z+'\')"><div id="Sub'+y+z+'" style="cursor:hand;padding:'+cellPadding+'px; background-color:'+subMenu[y][z][0]+'" onMouseOver="showStatus(); clearTimer(); hideAll(\'sub\'); showSubSubMenu(\'Sub\','+y+','+z+'); showStatus(\'over\',\'Sub\','+y+','+z+'); return true;" onMouseOut="timerHide()">';        
        buildSub += '<a id="SubLink'+y+z+'" href="'+subMenu[y][z][5]+'" target="'+subMenu[y][z][6]+'" style="font-family:'+sfontFace+';font-size:'+sfontSize+';font-weight:'+sfontWeight+';color:'+subMenu[y][z][2]+';text-decoration:'+fontDecoration+'">'+subMenu[y][z][4]+'<\/a>';
        if(subIndicate.toLowerCase()!="none" && subSubMenu[y][z].length>0){
          buildSub += '<span style="padding-left:3px">'+subIndicate+'<\/span>';
        }  
        buildSub += '<\/div><\/td><\/tr>';
      }
      buildSub += '<\/table><\/td><\/tr><\/table>';
    }
    buildSub += '<\/div>';
    document.write (buildSub)
  }
}

function buildSubSubMenu(){
  for(x=0; x<Menu.length; x++){
    if(subMenu[x].length>0){
      for(y=0; y<subMenu[x].length; y++){
        buildSubSub = '<div id="SubSub'+x+y+'" style="position:absolute; left:0; top:0; visibility:hidden">'
        if(subSubMenu[x][y].length>0){
          buildSubSub += '<table border="0" cellpadding="0" cellspacing="0" bgcolor="'+border+'" style="filter: alpha(Opacity='+ssTransparency+')"><tr><td><table border="0" cellpadding="0" cellspacing="'+borderWidth+'" width="100%" align="center">';
          for(z=0; z<subSubMenu[x][y].length; z++){
            buildSubSub += '<tr><td nowrap="nowrap" onclick="tdMouseClick(\'SubSubLink'+x+y+z+'\')"><div id="SubSub'+x+y+z+'" style="font-family:'+fontFace+';font-size:'+fontSize+';font-weight:'+ssfontWeight+';cursor:hand;padding:'+cellPadding+'px; background-color:'+subSubMenu[x][y][z][0]+'" onMouseOver="clearTimer(); hideAll(\'subsub\'); highlightSubSubMenu('+x+','+y+','+z+'); showStatus(\'over\',\'SubSub\','+x+', '+y+','+z+'); return true;" onMouseOut="timerHide()">';
            buildSubSub += '<a id="SubSubLink'+x+y+z+'" href="'+subSubMenu[x][y][z][5]+'" target="'+subSubMenu[x][y][z][6]+'" style="font-family:'+ssfontFace+';font-size:'+ssfontSize+';font-weight:'+ssfontWeight+';color:'+subSubMenu[x][y][z][2]+';text-decoration:'+fontDecoration+'">'+subSubMenu[x][y][z][4]+'<\/a>';
            buildSubSub += '<\/div><\/td><\/tr>';
          }
          buildSubSub += '<\/table><\/td><\/tr><\/table>';         
        }
        buildSubSub += '<\/div>';         
        document.writeln(buildSubSub)   
      }     
    }
  }
}

function showSubMenu(obj,id,orientation){
  document.getElementById(obj+id).style.backgroundColor=Menu[id][1];
  document.getElementById(obj+"Link"+id).style.color=Menu[id][3];
  getOffset(eval('document.getElementById("'+obj+id+'")'),"left")
  getOffset(eval('document.getElementById("'+obj+id+'")'),"top")
  getOffset(eval('document.getElementById("'+obj+id+'")'),"height")
  getOffset(eval('document.getElementById("'+obj+id+'")'),"width")

  vOff=(orientation.toLowerCase()=="vertical")?oWidth+borderWidth:0
  hOff=(orientation.toLowerCase()=="vertical")?oHeight+borderWidth:0
  
  document.getElementById("Sub"+id).style.left=oLeft-borderWidth+sHorizontalOffset+vOff;
  document.getElementById("Sub"+id).style.top=oTop+oHeight+sVerticalOffset-hOff;
  document.getElementById("Sub"+id).style.visibility="visible";
}

function showSubSubMenu(obj,id, subid){
  document.getElementById(obj+id+subid).style.backgroundColor=subMenu[id][subid][1];
  document.getElementById(obj+"Link"+id+subid).style.color=subMenu[id][subid][3];
  
  getOffset(eval('document.getElementById("'+obj+id+subid+'")'),"left")
  getOffset(eval('document.getElementById("'+obj+id+subid+'")'),"top")
  getOffset(eval('document.getElementById("'+obj+id+subid+'")'),"height")
  getOffset(eval('document.getElementById("'+obj+id+subid+'")'),"width")
  
  document.getElementById("SubSub"+id+subid).style.left=oLeft+oWidth+ssHorizontalOffset;
  document.getElementById("SubSub"+id+subid).style.top=oTop-borderWidth+ssVerticalOffset;
  document.getElementById("SubSub"+id+subid).style.visibility="visible";
}

function highlightSubSubMenu(id, subid, subsubid){
  document.getElementById("SubSub"+id+subid+subsubid).style.backgroundColor=subSubMenu[id][subid][subsubid][1];
  document.getElementById("SubSubLink"+id+subid+subsubid).style.color=subSubMenu[id][subid][subsubid][3];
}

function timerHide(level,id,subid){
  timer = setTimeout("hideAll()",(subMenuDelay*1000))
}

function clearTimer(){
  if(timer){clearTimeout(timer)}
}

// Hide all sub menu items, and recolour as required.
function hideAll(level){
  showStatus('out')  
  for(x=0;x<Menu.length; x++){
    if(level!="sub"&&level!="subsub"){
      document.getElementById("Menu"+x).style.backgroundColor=Menu[x][0]
      document.getElementById("MenuLink"+x).style.color=Menu[x][2];
    }
    for(y=0;y<subMenu[x].length; y++){
      if(subSubMenu[x][y].length>0){
        if(level!="subsub"){document.getElementById("SubSub"+x+y).style.visibility="hidden";}
        for(z=0; z<subSubMenu[x][y].length; z++){
          document.getElementById("SubSub"+x+y+z).style.backgroundColor=subSubMenu[x][y][z][0]
          document.getElementById("SubSubLink"+x+y+z).style.color=subSubMenu[x][y][z][2]
        }
      }
      if(level!="subsub"){
        document.getElementById("Sub"+x+y).style.backgroundColor=subMenu[x][y][0]
        document.getElementById("SubLink"+x+y).style.color=subMenu[x][y][2]}
    }
    if(level!="sub"&&level!="subsub"){document.getElementById("Sub"+x).style.visibility="hidden";}
  }
}


// Find positioning for sub menus
function getOffset(obj, dim) {
  if(dim=="left") 
  {     
    oLeft = obj.offsetLeft;  
    while(obj.offsetParent!=null) 
    {    
      oParent = obj.offsetParent     
      oLeft += oParent.offsetLeft 
      obj = oParent 	
    }
    return oLeft
  }
  else if(dim=="top")
  {
    oTop = obj.offsetTop;
    while(obj.offsetParent!=null) 
    {
      oParent = obj.offsetParent
      oTop += oParent.offsetTop
      obj = oParent 	
    }
    return oTop
  }
  else if(dim=="width")
  {
    oWidth = obj.offsetWidth
    return oWidth
  }  
  else if(dim=="height")
  {
    oHeight = obj.offsetHeight
    return oHeight
  }    
  else
  {
    alert("Error: invalid offset dimension '" + dim + "' in getOffset()")
    return false;
  }
}

// Display correct text in status bar
function showStatus(state,level,id,subid,subsubid){
  if(statusBar==0){msg=5}
  else if(statusBar==1){msg=4}
  else {msg=0}
    
  if(state=="over"){
    if(level=="Main" && msg>0){displayStatus=Menu[id][msg]}
    else if(level=="Sub" && msg>0){displayStatus=subMenu[id][subid][msg]}
    else if(level=="SubSub" && msg>0){displayStatus=subSubMenu[id][subid][subsubid][msg]}
    else{displayStatus=statusBar;}
  }
  else{displayStatus="";}
  window.status=displayStatus
}

// when you click the box, perform the same function as if the user had clicked the hyperlink
function tdMouseClick(theElement)
{
  if(document.getElementById && document.all){document.getElementById(theElement).click();}
}
//== End Script ============================================================================================