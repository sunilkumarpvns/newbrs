<%--
  Created by IntelliJ IDEA.
  User: dhyani.raval
  Date: 14/11/17
  Time: 5:27 PM
  To change this template use File | Settings | File Templates.
--%>
<script type="text/javascript">

    var i = $("#LacInformationTable tbody tr").length;
    if(isNullOrEmpty(i)){
        i=0;
    }
    function addLacInformation() {
        var tableRow= "<tr name='LacInformationRow'>"+
            "<td><textarea rows='2' maxlength='6' class='form-control' name='lacInformationDataList["+i+"].lac' type='number' onkeypress='return isNaturalInteger(event);' /></td>"+
            "<td><textarea rows='2' class='form-control' name='lacInformationDataList["+i+"].ciList' type='text' maxlength='4000'/></td>"+
            "<td><textarea rows='2' class='form-control' name='lacInformationDataList["+i+"].sacList' type='text'maxlength='4000' /></td>"+
            "<td><textarea rows='2' class='form-control' name='lacInformationDataList["+i+"].racList' type='text' maxlength='4000'/></td>"+
            "<td style='width:35px;'><span class='btn defaultBtn' onclick='$(this).parent().parent().remove();'><a><span class='delete glyphicon glyphicon-trash' title='delete'></span></a></span></td>"+
            "</tr>";
        $(tableRow).appendTo('#LacInformationTable');
        $("#LacInformationTable").find("tr:last td:nth-child(1)").find("textarea").focus(); // focus on first element
        i++;
    }

    $(function(){
        $( ".select2" ).select2();
        var countryIdJS = $("#countryId").val();
        onCountryChange();
        onRegionChange();
        setNetworkData(countryIdJS);
    });

    function onCountryChange(){
        var countryIdJS = $("#countryId").val();
        var regionId = "${cityData.regionData.id}";

        $("#regionId").find('option').remove();
        $("#cityId").find('option').remove();

        var data = ${regionDataList};

        var optionsAsString = "";
        for(var i in data) {
            if(countryIdJS == data[i].countryData.id) {
                if(data[i].id == regionId){
                    optionsAsString += "<option value='" + data[i].id + "' selected>" + data[i].name + "</option>";
                } else {
                    optionsAsString += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
                }
            }
        }
        $("#regionId").append(optionsAsString);
        $( ".select2" ).select2();//need to recreate
        onRegionChange();
        setNetworkData(countryIdJS);
    }

    function onRegionChange() {
        var regionIdJS = $("#regionId").val();
        var cityId = "${cityData.id}";
        $("#cityId").find('option').remove();

        var data = ${cityDataList};

        var optionsAsString = "";
        for(var i in data) {
            if(regionIdJS == data[i].regionData.id) {
                if(data[i].id == cityId){
                    optionsAsString += "<option value='" + data[i].id + "' selected>" + data[i].name + "</option>";
                } else {
                    optionsAsString += "<option value='" + data[i].id + "'>" + data[i].name + "</option>";
                }
            }
        }
        $("#cityId").append(optionsAsString);
        $( ".select2" ).select2();//need to recreate
    }

    function setNetworkData(countryIdJS) {
        $("#networkId").find('option').remove();
        var data = ${networkDataList};
        var optionsAsString = "";
        for(var i in data) {
            if(countryIdJS == data[i].countryData.id) {
                optionsAsString += "<option value='"+data[i].id+"'>" + data[i].name + "</option>";
            }
        }
        if(isNullOrEmpty(optionsAsString)){
            $("#LacInformationTable tbody").remove();
        }
        $("#networkId").append(optionsAsString);
        $( ".select2" ).select2();//need to recreate
    }

    $("#addLacInformationSpan").click(function () {
        var networkId = $("#networkId").val();
        if(isNullOrEmpty(networkId)) {
            addWarning(".popup", "Please Select Network");
        } else {
            addLacInformation();
        }
    });


    function isValidateLacInformation(){
        var ci_sac_rac_pattern = /^\d*[0-9]([\;|\,]\d*[0-9])*(\,|\;)?$/;
        var lacInformationTableLength =  $("#LacInformationTable tbody tr").length;

        if(lacInformationTableLength >= 1) {

            var isValidLacInformation = true;

            $("#LacInformationTable tbody tr").each(function () {
                var textareaElementLac = $(this).children().first().find('textarea');
                var textareaElementCiList =$(this).children('td:nth-child(2)').find('textarea');
                var textareaElementSacList =$(this).children('td:nth-child(3)').find('textarea');
                var textareaElementRacList =$(this).children('td:nth-child(4)').find('textarea');

                if (isNullOrEmpty(textareaElementLac.val())) {
                    setErrorOnElement(textareaElementLac, "LAC value is Required");
                    isValidLacInformation = false;
                } else if(!isNaturalNumber(textareaElementLac.val())){
                    setErrorOnElement(textareaElementLac, "LAC value must be numeric");
                    isValidLacInformation = false;
                }  else if(isNullOrEmpty(textareaElementCiList.val()) == false &&  !ci_sac_rac_pattern.test(textareaElementCiList.val())){
                    setErrorOnElement(textareaElementCiList, "Invalid CIs, Only comma(,) and semicolon(;)is allowed.");
                    isValidLacInformation = false;
                } else if(isNullOrEmpty(textareaElementSacList.val()) == false &&  !ci_sac_rac_pattern.test(textareaElementSacList.val())){
                    setErrorOnElement(textareaElementSacList, "Invalid SACs, Only comma(,) and semicolon(;)is allowed.");
                    isValidLacInformation = false;
                } else if(isNullOrEmpty(textareaElementRacList.val()) == false &&  !ci_sac_rac_pattern.test(textareaElementRacList.val())){
                    setErrorOnElement(textareaElementRacList, "Invalid RACs, Only comma(,) and semicolon(;)is allowed.");
                    isValidLacInformation = false;
                }
            });

            return isValidLacInformation;
        }
    }


</script>