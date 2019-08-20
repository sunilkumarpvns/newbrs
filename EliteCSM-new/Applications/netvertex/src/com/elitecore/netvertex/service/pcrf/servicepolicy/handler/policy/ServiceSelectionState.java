package com.elitecore.netvertex.service.pcrf.servicepolicy.handler.policy;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class ServiceSelectionState {
    private Map<Long, PackageSelectionState> serviceToPackageSelectionState;

    public ServiceSelectionState() {
        serviceToPackageSelectionState = new HashMap<>();
    }

    public ServiceSelectionState(Map<Long, PackageSelectionState> serviceToPackageSelectionState) {
        this.serviceToPackageSelectionState = serviceToPackageSelectionState;
    }


    public PackageSelectionState getPackageSelectionState(long serviceIdentifier) {
        return serviceToPackageSelectionState.get(serviceIdentifier);
    }

    public void add(long serviceIdentifier, PackageSelectionState packageSelectionState) {
        serviceToPackageSelectionState.put(serviceIdentifier, packageSelectionState);
    }

    public Map<Long, PackageSelectionState> getAll() {
        return serviceToPackageSelectionState;
    }

    public void toString(IndentingWriter indentingWriter){
        if(serviceToPackageSelectionState ==null || serviceToPackageSelectionState.isEmpty()){
            indentingWriter.println("No service wise PCC selection state");
        } else {
            for(Map.Entry<Long, PackageSelectionState> packageSelectionEntry: serviceToPackageSelectionState.entrySet()){
                PackageSelectionState packageSelectionState = packageSelectionEntry.getValue();
                indentingWriter.println("Service Id = "+packageSelectionEntry.getKey());
                indentingWriter.incrementIndentation();
                packageSelectionState.toString(indentingWriter);
                indentingWriter.decrementIndentation();
                indentingWriter.println();
            }
        }
    }

    public String toString(){
        StringWriter stringBuffer=new StringWriter();
        PrintWriter out=new PrintWriter(stringBuffer);
        toString(new IndentingPrintWriter(out));
        return stringBuffer.toString();
    }
}
