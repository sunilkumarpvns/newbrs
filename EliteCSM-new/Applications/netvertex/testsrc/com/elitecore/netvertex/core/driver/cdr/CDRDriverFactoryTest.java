package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.driverx.cdr.CDRDriver;
import com.elitecore.core.driverx.cdr.data.DBFieldMapping;
import com.elitecore.corenetvertex.constants.DriverTypes;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.conf.DatabaseDSConfiguration;
import com.elitecore.netvertex.core.conf.impl.DBDataSourceImpl;
import com.elitecore.netvertex.core.driver.cdr.conf.impl.CSVDriverConfigurationImpl.CSVDriverConfigurationBuilder;
import com.elitecore.netvertex.core.driver.cdr.conf.impl.DBCDRDriverConfigurationImpl.DBCDRDriverConfigurationBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.UUID;

import static com.elitecore.core.commons.util.constants.DataTypeConstant.STRING_DATA_TYPE;
import static org.mockito.Matchers.anyString;

public class CDRDriverFactoryTest {

    private DummyNetvertexServerContextImpl netvertexServerContext = new DummyNetvertexServerContextImpl();
    private CDRDriverFactory cdrDriverFactory = new CDRDriverFactory(netvertexServerContext, null);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {

        DatabaseDSConfiguration databaseDSConfiguration = Mockito.mock(DatabaseDSConfiguration.class);
        netvertexServerContext.getServerConfiguration().setDatabaseDSConfiguration(databaseDSConfiguration);
        DBDataSourceImpl dbDataSource = new DBDataSourceImpl();
        Mockito.doReturn(dbDataSource).when(databaseDSConfiguration).getDatasource(anyString());
    }


    @Test
    public void createDBCDRDriverWhenDriverTypeIsDB() throws InitializationFailedException {

        DBCDRDriverConfigurationBuilder dbcdrDriverConfigurationBuilder = new DBCDRDriverConfigurationBuilder(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        dbcdrDriverConfigurationBuilder.withDbFieldMapping(new DBFieldMapping("test", "test", STRING_DATA_TYPE, null));

        CDRDriver<ValueProviderExtImpl> cdrDriver = cdrDriverFactory.create(dbcdrDriverConfigurationBuilder.build());

        Assert.assertEquals(DriverTypes.DB_CDR_DRIVER, cdrDriver.getDriverType());
    }

    @Test
    public void createDBCDRDriverWhenDriverTypeIsCSV() throws InitializationFailedException {

        CSVDriverConfigurationBuilder csvDriverConfigurationBuilder = new CSVDriverConfigurationBuilder(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        csvDriverConfigurationBuilder.withCSVDriverType();

        CDRDriver<ValueProviderExtImpl> csvCdrDriver = cdrDriverFactory.create(csvDriverConfigurationBuilder.build());

        Assert.assertEquals(DriverTypes.CSV_CDR_DRIVER, csvCdrDriver.getDriverType());
    }

    @Test
    public void throwsInitializationFailedExceptionWhenDriverTypeIsNotCSVorDB() throws InitializationFailedException {

        CSVDriverConfigurationBuilder csvDriverConfigurationBuilder = new CSVDriverConfigurationBuilder(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        expectedException.expect(InitializationFailedException.class);
        expectedException.expectMessage("Unsupported Driver Type");
        cdrDriverFactory.create(csvDriverConfigurationBuilder.build());

    }

    @Test
    public void returnExistingDriverWhenDriverWithSameIdAlreadyCreated() throws InitializationFailedException {
        DBCDRDriverConfigurationBuilder dbcdrDriverConfigurationBuilder = new DBCDRDriverConfigurationBuilder(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        dbcdrDriverConfigurationBuilder.withDbFieldMapping(new DBFieldMapping("test", "test", STRING_DATA_TYPE, null));

        CDRDriver<ValueProviderExtImpl> csvCdrDriver = cdrDriverFactory.create(dbcdrDriverConfigurationBuilder.build());

        CDRDriver<ValueProviderExtImpl> sameCsvDriver = cdrDriverFactory.create(dbcdrDriverConfigurationBuilder.build());

        Assert.assertSame(csvCdrDriver, sameCsvDriver);
    }

}