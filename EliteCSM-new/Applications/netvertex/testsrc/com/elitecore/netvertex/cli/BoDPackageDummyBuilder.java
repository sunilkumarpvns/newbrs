package com.elitecore.netvertex.cli;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.core.util.cli.TableFormatter;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

public class BoDPackageDummyBuilder {

    private static final int WIDTH_POLICYNAME = 30;
    private static final int WIDTH_STATUS = 20;
    private static final int WIDTH_TYPE = 5;
    private static final int WIDTH_MODE = 20;
    private static final int WIDTH_REMARK = 50;
    private static int[] widthStatus = {WIDTH_POLICYNAME,WIDTH_STATUS,WIDTH_TYPE, WIDTH_MODE ,WIDTH_REMARK};
    private static String[] headerStatus = {"Policy Name","Status","Type", "Mode" ,"Remarks"};

    public static BoDPackage createDummyBoDPackage(String appender){
        return new BoDPackage("Test_BoD_ID_"+appender, "Test_BoD_Name_"+appender, "Test_BoD_Description"
                , PkgMode.TEST, PkgStatus.ACTIVE, 30, ValidityPeriodUnit.DAY
                , null, null, null, null
                , PolicyStatus.SUCCESS, null, null, null, null, null);
    }

    public static String viewBoDPackageNames(List<String> boDPackageNames, String command) {
        StringWriter stringWriter = new StringWriter();
        IndentingWriter out =  new IndentingPrintWriter(stringWriter);
        if(Collectionz.isNullOrEmpty(boDPackageNames) == false){
            Collections.sort(boDPackageNames);
            out.println();
            out.incrementIndentation();
            out.println("Select BoD Package name to "+command);
            out.println("---------------------------------");
            out.println();
            for(String pkgName : boDPackageNames){
                out.println(pkgName.replace(" ", "\\ "));
            }
            out.decrementIndentation();
        } else {
            out.println("No BoD Package configured");
        }
        out.println();
        out.close();
        return stringWriter.toString();
    }

    public static String viewBoDPackagesDefinition(List<BoDPackage> boDPackages) {

        StringWriter stringWriter = new StringWriter();
        IndentingWriter out =  new IndentingPrintWriter(stringWriter);

        if(Collectionz.isNullOrEmpty(boDPackages) == false){
            out.println();
            for(BoDPackage boDPackage : boDPackages){
                out.println(boDPackage);
            }
        }else{
            out.println("NO BoD PACKAGE FOUND");
        }
        out.println();
        out.close();
        return stringWriter.toString();
    }

    public static String viewPolicyStatus(List<PolicyDetail> listPolicy){
        TableFormatter formatter=new TableFormatter(headerStatus,widthStatus,TableFormatter.ALL_BORDER);
        if(listPolicy != null && listPolicy.isEmpty()==false){
            for(PolicyDetail policy : listPolicy){
                formatter.addRecord(formatpolicy(policy));
            }

            return formatter.getFormattedValues();
        }
        formatter.add("NO POLICY FOUND",TableFormatter.CENTER);
        return formatter.getFormattedValues();

    }

    private static String[] formatpolicy(PolicyDetail policy){
        String[] data = new String[widthStatus.length];
        data[0] = policy.getName().replace(" ", "\\ ");
        data[1] = policy.getStatus().status;
        data[2] = policy.getPackageType() +" - "+ policy.getEntityType();
        data[3] = policy.getPackageMode().name();

        if(Strings.isNullOrBlank(policy.getRemark()) == false) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            printWriter.print(policy.getRemark());
            data[4] = stringWriter.toString();
        } else {
            data[4] = "--";
        }

        return data;
    }
}