package com.elitecore.corenetvertex.pm.util;

import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(HierarchicalContextRunner.class)
public class ProductOfferPredicatesTest {
    public class TestsGroupFilter{
        @Test
        public void thatItFilterOutDataWhichDoesNotBelongToGroupIds(){
            List<String> staffGroup = new ArrayList<>();
            staffGroup.add("group_abc");
            staffGroup.add("group_pqr");

            ProductOfferData productOffer = new ProductOfferData();
            productOffer.setGroups("group_def,group_xyz");

            Assert.assertFalse(ProductOfferPredicates.createGroupFilter(staffGroup).test(productOffer));
        }

        @Test
        public void thatItDoesNotReturnFalseIfProductBelongsToAtleastOneGroup(){
            List<String> staffGroup = new ArrayList<>();
            staffGroup.add("group_abc");
            staffGroup.add("group_def");

            ProductOfferData productOffer = new ProductOfferData();
            productOffer.setGroups("group_def");

            Assert.assertTrue(ProductOfferPredicates.createGroupFilter(staffGroup).test(productOffer));
        }

        @Test
        public void thatItReturnsFalseWhenEmptyListIsPassedInWhenCreatingFilter(){
            List<String> staffGroup = new ArrayList<>();

            ProductOfferData productOffer = new ProductOfferData();
            productOffer.setGroups("group_def");

            Assert.assertFalse(ProductOfferPredicates.createGroupFilter(staffGroup).test(productOffer));
        }

        @Test
        public void thatItReturnsTrueWhenNullListIsPassedInProductOfferData(){
            List<String> staffGroup = new ArrayList<>();
            staffGroup.add("group_abc");
            staffGroup.add("group_def");

            ProductOfferData productOffer = new ProductOfferData();
            productOffer.setGroups(null);

            Assert.assertTrue(ProductOfferPredicates.createGroupFilter(staffGroup).test(productOffer));
        }
    }

    public class TestsNameFilter{
        @Test
        public void thatItReturnFalseForNameMisMatch(){
            String[] names = new String[2];
            names[0] = "name1";
            names[1] = "name2";

            ProductOfferData productOffer = new ProductOfferData();
            productOffer.setName("name3");

            Assert.assertFalse(ProductOfferPredicates.createNameFilter(names).test(productOffer));
        }

        @Test
        public void thatItReturnsTrueWhenNameMatches(){
            String[] names = new String[2];
            names[0] = "name1";
            names[1] = "name2";

            ProductOfferData productOffer = new ProductOfferData();
            productOffer.setName("name2");

            Assert.assertTrue(ProductOfferPredicates.createNameFilter(names).test(productOffer));
        }
    }
}
