//Disables CTRL+N 
//opens help on F1
/*
document.onkeydown = disableNewWin;
          
 function disableNewWin() 
 { 
     if(window.event && window.event.keyCode == 17)  
     { // Capture and remap ctrl
       window.event.keyCode = 555; 	
       return false; 
     } 
            
     if(window.event && window.event.keyCode == 78)  
     { // Capture and remap N
       window.event.keyCode = 555; 	
       return false; 
     } 
 }
 */
function openHelpPage()
{
	window.open('/help/Index.htm','help','resizable,status,scrollbars,titlebar=no')
}
