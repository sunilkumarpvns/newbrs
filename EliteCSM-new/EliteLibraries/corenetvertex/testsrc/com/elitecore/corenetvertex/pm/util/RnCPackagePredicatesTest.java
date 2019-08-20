package com.elitecore.corenetvertex.pm.util;

import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(HierarchicalContextRunner.class)
public class RnCPackagePredicatesTest {
    public class TestsGroupFilter{
        @Test
        public void thatItFilterOutDataWhichDoesNotBelongToGroupIds(){
            List<String> staffGroup = new ArrayList<>();
            staffGroup.add("group_abc");
            staffGroup.add("group_pqr");

            RncPackageData rncPackage = new RncPackageData();
            rncPackage.setGroups("group_def,group_xyz");

            Assert.assertFalse(RnCPackagePredicates.createGroupFilter(staffGroup).test(rncPackage));
        }

        @Test
        public void thatItReturnTrueIfProductBelongsToAtleastOneGroup(){
            List<String> staffGroup = new ArrayList<>();
            staffGroup.add("group_abc");
            staffGroup.add("group_def");

            RncPackageData rncPackage = new RncPackageData();
            rncPackage.setGroups("group_def");

            Assert.assertTrue(RnCPackagePredicates.createGroupFilter(staffGroup).test(rncPackage));
        }

        @Test
        public void thatItReturnsFalseWhenEmptyListIsPassedInWhenCreatingFilter(){
            List<String> staffGroup = new ArrayList<>();

            RncPackageData rncPackage = new RncPackageData();
            rncPackage.setGroups("group_def");

            Assert.assertFalse(RnCPackagePredicates.createGroupFilter(staffGroup).test(rncPackage));
        }

        @Test
        public void thatItReturnsTrueWhenNullListIsPassedInRncPackageData(){
            List<String> staffGroup = new ArrayList<>();
            staffGroup.add("group_abc");
            staffGroup.add("group_def");

            RncPackageData rncPackage = new RncPackageData();
            rncPackage.setGroups(null);

            Assert.assertTrue(RnCPackagePredicates.createGroupFilter(staffGroup).test(rncPackage));
        }
    }

    public class TestsNameFilter{
        @Test
        public void thatItReturnFalseForNameMisMatch(){
            String[] names = new String[2];
            names[0] = "name1";
            names[1] = "name2";

            RncPackageData rncPackage = new RncPackageData();
            rncPackage.setName("name3");

            Assert.assertFalse(RnCPackagePredicates.createNameFilter(names).test(rncPackage));
        }

        @Test
        public void thatItReturnsTrueWhenNameMatches(){
            String[] names = new String[2];
            names[0] = "name1";
            names[1] = "name2";

            RncPackageData rncPackage = new RncPackageData();
            rncPackage.setName("name2");

            Assert.assertTrue(RnCPackagePredicates.createNameFilter(names).test(rncPackage));
        }
    }
}
