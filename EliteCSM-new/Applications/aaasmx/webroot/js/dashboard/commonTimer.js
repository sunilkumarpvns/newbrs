/**
 * @summary     CommonTimer
 * @description This JS file contains the function related to widget Freeze & unFreeze features also includes 
 *              CommonTimer class  
 * @file        commonTimer.js
 * @author      Aditya Shrivastava
 *
 *
 */

/**
 * Common Timer class provide the timer functionality 
 * @param freezeButtonId
 * @param widgetID
 * @param widgetType
 * @requires jquery.timer.js 
 * e.g.
 *   	 var newTimerForWidget=new CommonTimer();
 *       newTimer.init('freebtnId',widgetId,widgetType);
 * */
var CommonTimer = function () {
     var $stopwatch,
     incrementTime = 60,
         initialTimer = 30000,
         currentTime = initialTimer,
         self,
         Timer,
         widgetId,
         type,
         MODULE = 'COMMON-TIMER',
         updateTimer = function(){
             $stopwatch.attr('value', formatTime(currentTime));
             if (currentTime === 0) {
                 resetTimer(self);
                 return;
             }
             currentTime -= incrementTime / 10;

             if (currentTime < 0) currentTime = 0;
         };
     this.resetCountdown = function () {
    	 commanFunction.log(MODULE, "Calling reset resetCountdown for widget: " + widgetId);
    	 currentTime = initialTimer;
         if (typeof Timer !== 'undefined' && typeof Timer.isActive !== 'undefined') {
             Timer.stop();
             sendUnfreezeRequest(type, widgetId, false);
         }
         $stopwatch.attr('value', 'Freeze');
     };
     this.init = function (stopwatch, widgetID, widgetType) {
         self = this;
         type = widgetType;
         widgetId = widgetID;
         $stopwatch = $('#'+stopwatch);
         Timer = $.timer(updateTimer, incrementTime, false);
     };
     this.getTimer = function () {
         return Timer;
     };
     this.getType = function () {
         return type;
     };
     this.getWidgetId = function () {
         return widgetId;
     };
 };

 //call back function for resetCountDown from update Timer function of Common Timer
 function resetTimer(parentObject) {
     parentObject.resetCountdown();
 }

 // To freeze/pause live update of widget 
 function freeze(commonTimer) {
     commonTimer.resetCountdown();
     sendUnfreezeRequest(commonTimer.getType(), commonTimer.getWidgetId(), true);
     commonTimer.getTimer().play();
 }

 
 //To resume/unfreeze widget live update
 function unfreeze(commonTimer) {
     commonTimer.resetCountdown();
 }

 //common function to send freeze or unfreeze request
 function sendUnfreezeRequest(widgetType, widgetId, isFreeze) {
     var data = {
         header: {
             id: widgetId,
             type: widgetType
         },
         body: {
             freeze: isFreeze
         }
     };
     getDashBoardSocket().sendRequest(data);
 }

 //Common functions
 function pad(number, length) {
     var str = '' + number;
     while (str.length < length) {
         str = '0' + str;
     }
     return str;
 }

 function formatTime(time) {
     var min = parseInt(time / 6000, 10),
         sec = parseInt(time / 100, 10) - (min * 60);
     return (min > 0 ? pad(min, 2) : "00") + ":" + pad(sec, 2);
 } 
 
// to hide progress bar of widgets
 function hideProgressBarDiv(progressBarDivId){
 	$('#'+progressBarDivId).hide();		
 }
	
