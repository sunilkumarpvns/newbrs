package com.elitecore.nvsmx.ws.util;

import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertTrue;

public class RncPackageModePredicateTest {


    private List<RnCPackage> rnCPackages;
    private RncPackageModePredicate testRncPackageModePredicate;
    private RncPackageModePredicate liveRncPackageModePredicate;
    private static final String CURRENCY="INR";


    @Before
    public void setUp() throws Exception {
           rnCPackages = new ArrayList<>();
           rnCPackages.add(createRncPackage("Rnc1",PkgMode.LIVE));
           rnCPackages.add(createRncPackage("Rnc2",PkgMode.LIVE2));
           rnCPackages.add(createRncPackage("Rnc3",PkgMode.TEST));
        testRncPackageModePredicate = new RncPackageModePredicate(PkgMode.TEST);
        liveRncPackageModePredicate = new RncPackageModePredicate(PkgMode.LIVE);


    }

    @Test
    public void predicateShouldReturnPkgOfGreaterModeOrder() {
        List<RnCPackage> testList = rnCPackages.stream().filter(testRncPackageModePredicate).collect(toList());
        List<RnCPackage> liveList = rnCPackages.stream().filter(liveRncPackageModePredicate).collect(toList());
        assertTrue(testList.size() == 3);
        assertTrue(liveList.size() == 2);
    }

    @Test
    public void predicateShouldNotFilterIfPackageModeIsNotProvided(){
        List<RnCPackage> testList = rnCPackages.stream().filter(new RncPackageModePredicate(null)).collect(toList());
        List<RnCPackage> liveList = rnCPackages.stream().filter(new RncPackageModePredicate(null)).collect(toList());
        assertTrue(testList.size() == rnCPackages.size() );
        assertTrue(liveList.size() == rnCPackages.size() );
    }



    private RnCPackage createRncPackage(String name, PkgMode mode){
        return new RnCPackage(UUID.randomUUID().toString(), name, null, null, Collections.EMPTY_LIST, null,null, null, mode, null, null, null , null, ChargingType.SESSION,CURRENCY);


    }


}