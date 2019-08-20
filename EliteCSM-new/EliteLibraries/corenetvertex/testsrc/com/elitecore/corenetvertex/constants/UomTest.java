package com.elitecore.corenetvertex.constants;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Test Cases for Uom enum
 * @author dhyani.raval
 */
@RunWith(JUnitParamsRunner.class)
public class UomTest {

    @Test
    @Parameters(value = {"SECOND", "MINUTE", "HOUR"})
    public void getUomsOfSameTypeFromNameShouldReturnsTimeTypeUomsWhenValueTypeIsTime(String value) {
        List<Uom> uomsOfSameTypeFromValue = Uom.getUomsOfSameTypeFromName(value);
        Assert.assertEquals(uomsOfSameTypeFromValue.size(), 3);
        Assert.assertThat(uomsOfSameTypeFromValue, CoreMatchers.hasItems(Uom.SECOND, Uom.HOUR, Uom.MINUTE));
    }

    @Test
    @Parameters(value = {"BYTE","MB","KB","GB"})
    public void getUomsOfSameTypeFromNameShouldReturnsVolumeTypeUomsWhenValueTypeIsVolume(String value) {
        List<Uom> uomsOfSameTypeFromValue = Uom.getUomsOfSameTypeFromName(value);
        Assert.assertEquals(uomsOfSameTypeFromValue.size(),4);
        Assert.assertThat(uomsOfSameTypeFromValue, CoreMatchers.hasItems(Uom.BYTE, Uom.KB, Uom.GB, Uom.MB));
    }

    @Test
    public void getUomsOfSameTypeFromNameShouldReturnsEventTypeUomsWhenValueTypeIsEvent() {
        List<Uom> uomsOfSameTypeFromValue = Uom.getUomsOfSameTypeFromName(Uom.EVENT.name());
        Assert.assertEquals(uomsOfSameTypeFromValue.size(),1);
        Assert.assertThat(uomsOfSameTypeFromValue,CoreMatchers.hasItems(Uom.EVENT));
    }


    @Test
    public void getUomsOfSameTypeFromNameShouldReturnsNullWhenPassedValueIsNull() {
        List<Uom> uomsOfSameTypeFromValue = Uom.getUomsOfSameTypeFromName(null);
        Assert.assertTrue(uomsOfSameTypeFromValue == null);
    }

    @Test
    public void getUomsOfSameTypeFromNameShouldReturnsNullWhenPassedEmptyString() {
        List<Uom> uomsOfSameTypeFromValue = Uom.getUomsOfSameTypeFromName("");
        Assert.assertTrue(uomsOfSameTypeFromValue == null);
    }

    @Test
    public void getUomsOfSameTypeFromNameShouldReturnsNullWhenPassedInvalidValue() {
        List<Uom> uomsOfSameTypeFromValue = Uom.getUomsOfSameTypeFromName("INVALID");
        Assert.assertTrue(uomsOfSameTypeFromValue == null);
    }

    @Test
    public void getUomsOfSameTypeFromNameShouldReturnsPerPulseTypeUomsWhenValueTypeIsPerPulse() {
        List<Uom> uomsOfSameTypeFromValue = Uom.getUomsOfSameTypeFromName(Uom.PERPULSE.name());
        Assert.assertEquals(uomsOfSameTypeFromValue.size(),1);
        Assert.assertThat(uomsOfSameTypeFromValue,CoreMatchers.hasItems(Uom.PERPULSE));
    }

}
