package com.elitecore.corenetvertex.core.conf;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringIndentingStyle;
import com.elitecore.corenetvertex.util.ToStringable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FailReason implements ToStringable, Serializable {
    private String name;
    private List<String> failReasons;
    private List<FailReason>  childModuleFailReasons;

    public FailReason(String name) {
        this.name = name;
    }


    public void add(String failReason) {

        if(this.failReasons == null) {
            failReasons = new ArrayList<>(2);
        }

        failReasons.add(failReason);
    }

    public String getName() {
        return name;
    }

    public void addChildModuleFailReasonIfNotEmpty(FailReason failReason) {

        if(failReason.isEmpty()) {
            return;
        }

        if(this.childModuleFailReasons == null) {
            childModuleFailReasons = new ArrayList<>(2);
        }

        childModuleFailReasons.add(failReason);
    }

    public boolean isEmpty() {
        return Objects.isNull(failReasons) && Objects.isNull(childModuleFailReasons);
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.appendChild("Fail Reasons", failReasons);
        if(Collectionz.isNullOrEmpty(childModuleFailReasons) == false){
            builder.appendChildObject(". Child Fail Reasons", childModuleFailReasons);
        }
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        ToStringIndentingStyle customStyle = new ToStringIndentingStyle(builder.getCurrentIndentation());
        customStyle.setFieldNameValueSeparator(":");
        builder.pushStyle(customStyle);
        toString(builder);
        return builder.toString();
    }

    public void addAll(List<String> failReasons) {
        if(this.failReasons == null) {
            this.failReasons = new ArrayList<>();
        }
        this.failReasons.addAll(failReasons);
    }
}
