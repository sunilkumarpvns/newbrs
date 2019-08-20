


 String.prototype.strReverse = function() {
	var newstring = "";
	for (var s=0; s < this.length; s++) {
		newstring = this.charAt(s) + newstring;
	}
	return newstring;
	
};


function chkPass(pwd) {
     	
	var nScore=0, nLength=0, nAlphaUC=0, nAlphaLC=0,nMidChar=0, nNumber=0, nSymbol=0,nUnqChar=0,nRepChar=0, nRepInc=0, nConsecAlphaUC=0, nConsecAlphaLC=0, nConsecNumber=0, nConsecSymbol=0,nSeqAlpha=0, nSeqNumber=0, nSeqSymbol=0, nSeqChar=0;
	var nMultConsecAlphaUC=2, nMultConsecAlphaLC=2, nMultConsecNumber=2;
	var nMultSeqAlpha=3, nMultSeqNumber=3, nMultSeqSymbol=3;
	var nMultLength=4, nMultNumber=4, nMultMidChar=2;
	var nMultSymbol=6;
	var sAlphas = "abcdefghijklmnopqrstuvwxyz";
	var sNumerics = "01234567890";
	var sSymbols = ")!@#$%^&*()";
	
	
	
	if (pwd) {
		nScore = parseInt(pwd.length * nMultLength);
		nLength = pwd.length;
		
		var arrPwd = pwd.replace(/\s+/g,"").split(/\s*/);
		var arrPwdLen = arrPwd.length;
		
		
		for (var a=0; a < arrPwdLen; a++) {
			if (arrPwd[a].match(/[A-Z]/g)) {
				
				nAlphaUC++;
			}
			else if (arrPwd[a].match(/[a-z]/g)) { 
				
				nAlphaLC++;
			}
			else if (arrPwd[a].match(/[0-9]/g)) { 
				if (a > 0 && a < (arrPwdLen - 1)) { nMidChar++; }
				
				nNumber++;
			}
			else if (arrPwd[a].match(/[^a-zA-Z0-9_]/g)) { 
				if (a > 0 && a < (arrPwdLen - 1)) { nMidChar++; }
				
				nSymbol++;
			}
			
			
			var bCharExists = false;
			for (var b=0; b < arrPwdLen; b++) {
				if (arrPwd[a] == arrPwd[b] && a != b) { 
					bCharExists = true;
					nRepInc += Math.abs(arrPwdLen/(b-a));
				}
			}
			if (bCharExists) { 
				nRepChar++; 
				nUnqChar = arrPwdLen-nRepChar;
				nRepInc = (nUnqChar) ? Math.ceil(nRepInc/nUnqChar) : Math.ceil(nRepInc); 
			}
		}
		
		
		for (var s=0; s < 23; s++) {
		
			var sFwd = sAlphas.substring(s,parseInt(s+3));
			var sRev = sFwd.strReverse();
			
			if (pwd.toLowerCase().indexOf(sFwd) != -1 || pwd.toLowerCase().indexOf(sRev) != -1) {
			
			 nSeqAlpha++; nSeqChar++;}
		}
		
		
		for (var s=0; s < 8; s++) {
			var sFwd = sNumerics.substring(s,parseInt(s+3));
			var sRev = sFwd.strReverse();
			
			if (pwd.toLowerCase().indexOf(sFwd) != -1 || pwd.toLowerCase().indexOf(sRev) != -1) { 
			
			nSeqNumber++; nSeqChar++;}
		}
		
		
		for (var s=0; s < 8; s++) {
			var sFwd = sSymbols.substring(s,parseInt(s+3));
			var sRev = sFwd.strReverse();
			
			if (pwd.toLowerCase().indexOf(sFwd) != -1 || pwd.toLowerCase().indexOf(sRev) != -1) { nSeqSymbol++; nSeqChar++;}
		}
		
			
		
		if (nAlphaUC > 0 && nAlphaUC < nLength) {
		   
		   	nScore = parseInt(nScore + ((nLength - nAlphaUC) * 2));
			
			
		}
		 
		if (nAlphaLC > 0 && nAlphaLC < nLength) {	
			nScore = parseInt(nScore + ((nLength - nAlphaLC) * 2)); 
			
		}
		
		if (nNumber > 0 && nNumber < nLength) {	
			nScore = parseInt(nScore + (nNumber * nMultNumber));
			
		}
		
		if (nSymbol > 0) {	
			nScore = parseInt(nScore + (nSymbol * nMultSymbol));
		
		}
		 		
		if ((nAlphaLC > 0 || nAlphaUC > 0) && nSymbol === 0 && nNumber === 0) {  
			nScore = parseInt(nScore - nLength);
			
		}
		if (nAlphaLC === 0 && nAlphaUC === 0 && nSymbol === 0 && nNumber > 0) {  
			nScore = parseInt(nScore - nLength); 
			
		}
		if (nRepChar > 0) {  
			nScore = parseInt(nScore - nRepInc);
			
		}
		if (nConsecAlphaUC > 0) {  
			nScore = parseInt(nScore - (nConsecAlphaUC * nMultConsecAlphaUC)); 
			
		}
		if (nConsecAlphaLC > 0) {  
			nScore = parseInt(nScore - (nConsecAlphaLC * nMultConsecAlphaLC)); 
			
		}
		if (nConsecNumber > 0) { 
			nScore = parseInt(nScore - (nConsecNumber * nMultConsecNumber));  
			
		}
		if (nSeqAlpha > 0) { 
			nScore = parseInt(nScore - (nSeqAlpha * nMultSeqAlpha)); 
			
		}
		if (nSeqNumber > 0) {  
			nScore = parseInt(nScore - (nSeqNumber * nMultSeqNumber)); 
			
		}
		if (nSeqSymbol > 0) {  
			nScore = parseInt(nScore - (nSeqSymbol * nMultSeqSymbol)); 
			
		}
		
		
		var score = 0;
		if (nScore > 100) { nScore = 100; } else if (nScore < 0) { nScore = 0; }
		if (nScore >= 0 && nScore < 30) {score=1 }
		else if (nScore >= 30 && nScore < 70) {score=2 }
		else if (nScore >= 70 ) {score=3 }
		
		return score;
		
	}
	else  {
		return 0;
		
	}
}



