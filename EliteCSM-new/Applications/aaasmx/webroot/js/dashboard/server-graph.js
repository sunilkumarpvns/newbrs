function updateGarbageCollectionData(psMarkSweepCount,psMarkSweepTime,psScavengeCount,psScavengeTime){
	 $('#garbageCollectionTable tr:nth-child(2) td:nth-child(2)').html(psMarkSweepCount);
	 $('#garbageCollectionTable tr:nth-child(2) td:nth-child(3)').html(psMarkSweepTime);
	 $('#garbageCollectionTable tr:nth-child(3) td:nth-child(2)').html(psScavengeCount);
	 $('#garbageCollectionTable tr:nth-child(3) td:nth-child(3)').html(psScavengeTime); 
	 
	 $('#garbageCollectionTable tr:nth-child(2) td:nth-child(1)').css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	 $('#garbageCollectionTable tr:nth-child(2) td:nth-child(2)').css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
	 $('#garbageCollectionTable tr:nth-child(2) td:nth-child(3)').css({"border-bottom-color": "#CCC","border-bottom-width":"1px","border-bottom-style":"solid"});
}
function updateJVMMemoryDetails(psEdenused,psEdenmax,psEdenusage,psEdenpeakused,psEdenpeakmax,psSurvivorused,psSurvivormax,psSurvivorusage,psSurvivorpeakused,psSurvivorpeakmax,oldgenused,oldgenmax,oldgenusage,oldgenpeakused,oldgenpeakmax){
    
	 $('#JVMMemory tr:nth-child(3) td:nth-child(3)').html(psEdenused);
	 $('#JVMMemory tr:nth-child(3) td:nth-child(4)').html(psEdenmax);
	 $('#JVMMemory tr:nth-child(3) td:nth-child(5)').html("");
	 $('#JVMMemory tr:nth-child(3) td:nth-child(5)').html("<table id='innerheapMemory' style='border-bottom: none;'><tr><td><div class='progress_bar' id='edenProgress' value ='50' style='height: 15px;width:150px;'></div></td><td class='tabledata-text'>"+psEdenusage+"%</td></tr></table>");
	
	 $('#JVMMemory tr:nth-child(3) td:nth-child(6)').html(psEdenpeakused);
	 $('#JVMMemory tr:nth-child(3) td:nth-child(7)').html(psEdenpeakmax);
	 
	 $('#JVMMemory tr td').css('border-bottom','1pt solid #CCC');
	 $('#JVMMemory tr td table tr td').css('border-bottom','none');
 
	 $('#JVMMemory tr:nth-child(4) td:nth-child(2)').html(psSurvivorused);
	 $('#JVMMemory tr:nth-child(4) td:nth-child(3)').html(psSurvivormax);
	 
	 $('#JVMMemory tr:nth-child(4) td:nth-child(4)').html("");
	 $('#JVMMemory tr:nth-child(4) td:nth-child(4)').html("<table id='innerSurvivorTable' style='border-bottom: none;'><tr><td><div class='psSurvivor_Progress' id='psSurvivor_Progress' value ='"+psSurvivorusage+"' style='height: 15px;width:150px;'></div></td><td class='tabledata-text'>"+psSurvivorusage+"%</td></tr></table>");
	 $('#JVMMemory tr:nth-child(4) td:nth-child(5)').html(psSurvivorpeakused);
	 $('#JVMMemory tr:nth-child(4) td:nth-child(6)').html(psSurvivorpeakmax); 
	 
	 $('#JVMMemory tr:nth-child(5) td:nth-child(2)').html(oldgenused);
	 $('#JVMMemory tr:nth-child(5) td:nth-child(3)').html(oldgenmax);
	 
	 $('#JVMMemory tr:nth-child(5) td:nth-child(4)').html("");
	 $('#JVMMemory tr:nth-child(5) td:nth-child(4)').html("<table id='inneroldGenTable' style='border-bottom: none;'><tr><td><div class='psOldGen_Progress' id='psOldGen_Progress' value ='"+oldgenusage+"' style='height: 15px;width:150px;'></div></td><td class='tabledata-text'>"+oldgenusage+"%</td></tr></table>");
	
	 $('#JVMMemory tr:nth-child(5) td:nth-child(5)').html(oldgenpeakused);
	 $('#JVMMemory tr:nth-child(5) td:nth-child(6)').html(oldgenpeakmax); 
}

function updateNonHeapDetails(permgenused,permgenmax,permgenusage,permgenpeakused,permgenpeakmax,codecacheused,codecachemax,codecacheusage,codecachepeakused,codecachepeakmax){
	 $('#JVMMemory tr:nth-child(6) td:nth-child(3)').html(permgenused);
	 $('#JVMMemory tr:nth-child(6) td:nth-child(4)').html(permgenmax);
	 
	 $('#JVMMemory tr:nth-child(6) td:nth-child(5)').html("");
	 $('#JVMMemory tr:nth-child(6) td:nth-child(5)').html("<table id='innerpermGenTable' style='border-bottom: none;'><tr><td><div class='psPermGen_Progress' id='psPermGen_Progress' value ='"+permgenusage+"' style='height: 15px;width:150px;'></div></td><td class='tabledata-text'>"+permgenusage+"%</td></tr></table>");
	 $('#JVMMemory tr:nth-child(6) td:nth-child(6)').html(permgenpeakused);
	 $('#JVMMemory tr:nth-child(6) td:nth-child(7)').html(permgenpeakmax); 
	
	 $('#JVMMemory tr:nth-child(7) td:nth-child(2)').html(codecacheused);
	 $('#JVMMemory tr:nth-child(7) td:nth-child(3)').html(codecachemax);
	 
	 $('#JVMMemory tr:nth-child(7) td:nth-child(4)').html("");
	 $('#JVMMemory tr:nth-child(7) td:nth-child(4)').html("<table id='innercodecacheTable' border='0' style='border: none;'><tr><td><div class='codeCache_Progress' id='codeCache_Progress' value ='"+codecacheusage+"' style='height: 15px;width:150px;'></div></td><td class='tabledata-text'>"+codecacheusage+"%</td></tr></table>");
	 $('#JVMMemory tr:nth-child(7) td:nth-child(5)').html(codecachepeakused);
	 $('#JVMMemory tr:nth-child(7) td:nth-child(6)').html(codecachepeakmax); 
	
}
$(function() {
	$( "#edenProgress" ).progressbar({
	value: 37
	});
	});