Menu = new Array();
subMenu = new Array();
subSubMenu = new Array();
//== Menu Items ============================================================================================
// Enter a new line below for each Menu and 1st & 2nd level sub Menu items. The format is:
// addMenu("background colour", "background colour on mouseover", "font colour", "font-colour on mouseover", "displaying text", "URL", "target")
// For main menu items use addMenu(), for level 1 sub menu items use addSubMenu() and for level 2 sub menu items use addSubSubMenu()

addMenu("#758CAC", "#FFFFFF", "#FFFFFF", "#758CAC", "&nbsp;&nbsp;Home&nbsp;&nbsp;", "javascript:getTarget('Home.action?noChche='+new Date().getTime())", "_top");
addMenu("#758CAC", "#FFFFFF", "#FFFFFF", "#758CAC", "&nbsp;&nbsp;My Info&nbsp;&nbsp;", "javascript:void(0)", "_top");
	addSubMenu("#758CAC", "#FFFFFF", "#FFFFFF", "#758CAC", "&nbsp;&nbsp;My Profile&nbsp;&nbsp;&nbsp;&nbsp;", "javascript:getTarget('Profile.action?noChche='+new Date().getTime())", "_top");
	addSubMenu("#758CAC", "#FFFFFF", "#FFFFFF", "#758CAC", "&nbsp;&nbsp;Change Password&nbsp;&nbsp;&nbsp;&nbsp;", "javascript:getTarget('ChangePass.action?noChche='+new Date().getTime())", "_top");
addMenu("#758CAC", "#FFFFFF", "#FFFFFF", "#758CAC", "&nbsp;&nbsp;Usage Details&nbsp;&nbsp;", "javascript:getTarget('Usege_details.action?noChche='+new Date().getTime())", "_top");
//addMenu("#758CAC", "#FFFFFF", "#FFFFFF", "#758CAC", "&nbsp;&nbsp;My Plan&nbsp;&nbsp;", "javascript:getTarget('Myplan.action')", "_top");
addMenu("#758CAC", "#FFFFFF", "#FFFFFF", "#758CAC", "&nbsp;&nbsp;Services&nbsp;&nbsp;", "javascript:void(0)", "_top");
	addSubMenu("#758CAC", "#FFFFFF", "#FFFFFF", "#758CAC", "&nbsp;&nbsp;SSSS Service&nbsp;&nbsp;", "javascript:ServiceOpen('ssss')", "_top");
addMenu("#758CAC", "#FFFFFF", "#FFFFFF", "#758CAC", "&nbsp;&nbsp;Logout&nbsp;&nbsp;", "Logout.action", "_top");
showMenu('horizontal');

