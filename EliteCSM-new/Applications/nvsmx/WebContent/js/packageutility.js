function validateProductGroup(existingProductGroup, modifiedProductGroup) {
    if(isNullOrEmpty(modifiedProductGroup)) {
        $('#validateGroup').css("display","block");
        return;
    }

    for(var i = 0;i<existingProductGroup.length;i++) {
        if(modifiedProductGroup.includes(existingProductGroup[i])) {
            $('#validateGroup').css("display","none");
            continue;
        } else {
            $('#validateGroup').css("display","block");
            break;
        }
    }
}