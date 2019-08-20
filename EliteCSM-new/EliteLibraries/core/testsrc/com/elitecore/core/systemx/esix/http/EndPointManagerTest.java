package com.elitecore.core.systemx.esix.http;

import com.elitecore.core.systemx.esix.EndPoint;
import com.elitecore.core.systemx.esix.configuration.EndPointConfiguration;
import com.elitecore.core.systemx.esix.configuration.EndPointConfigurationImpl;
import org.apache.http.client.HttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.UnknownHostException;

public class EndPointManagerTest {

    private HttpClient poolableHttpClient = new HTTPClientFactory().getPoolableHttpClient(100,
            3000,3000);
    EndPointConfiguration endPointConfiguration1;
    EndPointConfiguration endPointConfiguration2;
    EndPointConfiguration endPointConfiguration3;
    private boolean isScanned;

    @Before
    public void setup() throws UnknownHostException{
        endPointConfiguration1 =new EndPointConfigurationImpl("connector1",
                "2.2.2.2",
                "80",
                "","connector1","","");
        endPointConfiguration2 =new EndPointConfigurationImpl("connector1",
                "6.6.6.6.6",
                "90",
                "","connector2","","");
        endPointConfiguration3 =new EndPointConfigurationImpl("connector3",
                "localhost",
                "90",
                "","connector3","","");
        isScanned = false;
    }

    @Test
    public void create_pd_end_point_and_add_in_endpointmanager_and_get_it_back_by_connector_id(){
        EndPointManager.getInstance().addEndPoint(endPointConfiguration1,poolableHttpClient,null);
        Assert.assertNotNull(EndPointManager.getInstance().getEndPoint(endPointConfiguration1.getId()));
    }

    @Test
    public void create_pd_instance_with_scanner_passed_from_out_side_and_check_it_executes_scanner(){
        Scanner scanner = new Scanner(){
            public boolean isAccessible(String ip, int port){
                isScanned=true;
                return isScanned;
            }
        };
        EndPointManager.getInstance().addEndPoint(endPointConfiguration3,poolableHttpClient,null,scanner);
        Assert.assertNotNull(EndPointManager.getInstance().getEndPoint(endPointConfiguration3.getId()));
        Assert.assertTrue(isScanned);
    }

    @Test
    public void add_end_point_with_same_id_and_it_must_not_update_existing_one_nothing_will_happen(){
        EndPointManager.getInstance().addEndPoint(endPointConfiguration1,poolableHttpClient,null);
        EndPointManager.getInstance().addEndPoint(endPointConfiguration2,poolableHttpClient,null);
        EndPoint endPoint = EndPointManager.getInstance().getEndPoint(endPointConfiguration1.getId());
        Assert.assertNotNull(endPoint);
        Assert.assertEquals("connector1",endPoint.getName());
    }

    @Test
    public void it_should_add_endpoint_even_if_invalid_ip_address_is_found(){
        EndPointManager.getInstance().addEndPoint(endPointConfiguration2,poolableHttpClient,null);
        endPointConfiguration2 = Mockito.spy(endPointConfiguration2);
        EndPoint endPoint = EndPointManager.getInstance().getEndPoint(endPointConfiguration1.getId());
        Assert.assertNotNull(endPoint);
    }
}
