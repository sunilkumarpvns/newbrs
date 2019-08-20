package com.elitecore.nvsmx.system.driver.cdr;

import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class EDRDriverManagerTest {

    private BaseCSVDriver createMockDriver(String name, String delimeter){
        BaseCSVDriver driver = Mockito.mock(BaseCSVDriver.class);
        Mockito.when(driver.getDriverName()).thenReturn(name);
        Mockito.when(driver.getDelimiter()).thenReturn(delimeter);
        return driver;
    }

    @Test
    public void checksSingletonIsImplementedProperly(){
        EDRDriverManager cdr1 = EDRDriverManager.getInstance();
        EDRDriverManager cdr2 = EDRDriverManager.getInstance();
        Assert.assertEquals(cdr1,cdr2);
    }

    @Test
    public void createsCDRDriverAndRegistersItSuccessfullyInTheManager(){
        EDRDriverManager edrDriverManager = EDRDriverManager.getInstance();
        edrDriverManager.registerDriver(createMockDriver("test1","$"));
    }

    @Test
    public void addsAndRetrievesDriverByGetDriverMethodAndChecksForExpectedObject(){
        EDRDriverManager edrDriverManager = EDRDriverManager.getInstance();
        edrDriverManager.registerDriver(createMockDriver("test2","$"));
        Assert.assertEquals("$", EDRDriverManager.getInstance().getDriver("test2").getDelimiter());
    }

    @Test
    public void addsOtherDriverWithSameNameAndGetsItBackByNameToCheckItDoesNotOverrideOlderValues(){
        EDRDriverManager edrDriverManager = EDRDriverManager.getInstance();
        edrDriverManager.registerDriver(createMockDriver("test3","$"));
        edrDriverManager.registerDriver(createMockDriver("test3","%%%"));
        Assert.assertEquals("$", EDRDriverManager.getInstance().getDriver("test3").getDelimiter());
    }

    @Test
    public void verifiesThatStopGetsCalledForTheDriverWhenManagerStopCalled(){
        EDRDriverManager edrDriverManager = EDRDriverManager.getInstance();
        BaseCSVDriver driver = createMockDriver("test4","$");
        edrDriverManager.registerDriver(driver);
        Mockito.doNothing().when(driver).stop();
        edrDriverManager.stop();
        Mockito.verify(driver, Mockito.times(1)).stop();
    }
}
