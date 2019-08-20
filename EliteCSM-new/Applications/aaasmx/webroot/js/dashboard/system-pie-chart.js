$(function () {
	
      //Progress Bar
      
      $('.progress_bar').each(function() {
  		var progressbar = $(this),
  	    progressLabel = $(this).find( ".progress-label" ),
  	    progressvalue = $(this).attr('value');
  	      
  	    progressbar.progressbar({
  	      value: false,
  	      change: function() {
  	       
  	        progressLabel.text( progressbar.progressbar( "value" ) + "% Complete" );
  	      },
  	      complete: function() {
  	        progressLabel.text( "100%" );
  	      }
  	    });
  	 
  	    function progress() {
  	                
  	      var val = progressbar.progressbar( "value" ) || 0;
  	 
  	      progressbar.progressbar( "value", val + 1 )
  	        .removeClass("beginning middle end")
  	        .addClass(val < 40 ? "beginning" : val < 75 ? "middle" : "end");
  	        
  	 
  	      if ( val < progressvalue ) {
  	        progress();
  	      }
  	      
  	    }
  	    progress();
  	 });
      
  });
  

