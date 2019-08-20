package com.elitecore.nvsmx.ws.util;

import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.util.MockPackages;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

public class DataPackageModePredicateTest {
    private List<UserPackage> packages;
    private DataPackageModePredicate testRncPackageModePredicate;
    private DataPackageModePredicate liveRncPackageModePredicate;



    @Before
    public void setUp() throws Exception {
        packages = new ArrayList<>();
        packages.add(createPackage("Rnc1", PkgMode.LIVE));
        packages.add(createPackage("Rnc2",PkgMode.LIVE2));
        packages.add(createPackage("Rnc3",PkgMode.TEST));
        testRncPackageModePredicate = new DataPackageModePredicate(PkgMode.TEST);
        liveRncPackageModePredicate = new DataPackageModePredicate(PkgMode.LIVE);


    }

    @Test
    public void predicateShouldReturnPkgOfGreaterModeOrder() {
        List<UserPackage> testList = packages.stream().filter(testRncPackageModePredicate).collect(toList());
        List<UserPackage> liveList = packages.stream().filter(liveRncPackageModePredicate).collect(toList());
        assertTrue(testList.size() == 3);
        assertTrue(liveList.size() == 2);
    }

    @Test
    public void predicateShouldNotFilterIfPackageModeIsNotProvided(){
        List<UserPackage> testList = packages.stream().filter(new DataPackageModePredicate(null)).collect(toList());
        List<UserPackage> liveList = packages.stream().filter(new DataPackageModePredicate(null)).collect(toList());
        assertTrue(testList.size() == packages.size() );
        assertTrue(liveList.size() == packages.size() );
    }



    private UserPackage createPackage(String name, PkgMode mode){
        UserPackage userPackage;
        userPackage = Mockito.mock(UserPackage.class);
        Mockito.when(userPackage.getMode()).thenReturn(mode);
        return userPackage;
    }



}