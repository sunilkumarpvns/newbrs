<%@ taglib prefix="s" uri="/struts-tags/ec" %>

<script type="text/javascript">

    function validateValidityPeriod(){
        var validityPeriod = $("#validityPeriod").val();
        if (isNullOrEmpty(validityPeriod)) {
            setError("validityPeriod", '<s:text name="error.valueRequired"/>');
            return false;
        }
        return true;
    }
    function validateRetiredStatus(){
        var status = $("#bodPackageStatus").val();

        if (status === '<s:property value="@com.elitecore.corenetvertex.constants.PkgStatus@RETIRED.name()"/>') {
            setError('bodPackageStatus', "<s:text name='error.status.retired'/>");
            return false;
        }
        return true;
    }

    function validateEndTimeAvailability(){
        var currentDate = new Date("<%=new Timestamp(TimeSource.systemTimeSource().currentTimeInMillis())%>");
        var availStartDate = $("#availabilityStartDate").val();
        var availEndDate = $("#availabilityEndDate").val();

        if(isNullOrEmpty(availStartDate) == false && isNullOrEmpty(availEndDate) == false){
            var startDate = convertToDateFromGivenStr(availStartDate);
            var endDate = convertToDateFromGivenStr(availEndDate);

            if(endDate < startDate){
                setError('availabilityEndDate', "<s:text name='bod.package.availability.enddate.mustbe.greaterthan.startdate'/>");
                return false;
            }

            if(endDate < currentDate){
                setError('availabilityEndDate', "<s:text name='bod.package.availability.enddate.greater.than.current.time'/>");
                return false;
            }

        }
        return true;
    }

    function validateStartTimeAvailability(){
        var currentDate = new Date("<%=new Timestamp(TimeSource.systemTimeSource().currentTimeInMillis())%>");
        var availStartDate = $("#availabilityStartDate").val();

        if(isNullOrEmpty(availStartDate) == false){

            var startDate = convertToDateFromGivenStr(availStartDate);

            if(startDate < currentDate){
                setError('availabilityStartDate', "<s:text name='bod.package.availability.mustbe.greater.than.current.time'/>");
                return false;
            }
        }
        return true;
    }

    function convertToDateFromGivenStr(dateStr){

        var dateStrArr = dateStr.split('-');
        var day = dateStrArr[0];
        var monthStr = dateStrArr[1];
        var monthNum = "JanFebMarAprMayJunJulAugSepOctNovDec".indexOf(monthStr)/3 ;
        var remainEntities = dateStrArr[2].split(' ');
        var year = remainEntities[0];
        var timestampArr = remainEntities[1].split(':');
        var hour = timestampArr[0];
        var min = timestampArr[1];
        var sec = timestampArr[2];

        return new Date(year, monthNum, day, hour, min, sec, 0);
    }

</script>