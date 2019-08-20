package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.nvsmx.TestableEndPoint;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Objects;

@RunWith(HierarchicalContextRunner.class)
public class EndPointStoreTest {

    private final ServerInformation primaryInstance = new ServerInformation("PRIMARY_INSTANCE","00001","1");
    private final ServerInformation secondaryInstance = new ServerInformation("SECONDARY_INSTANCE","00002","2");
    private final ServerInformation pdInstance1 = new ServerInformation("PD1","1","1");
    private final ServerInformation pdInstance2 = new ServerInformation("PD2","2","2");
    private final ServerInformation default_group = new ServerInformation("DEFAULT_GROUP","1");
    private final ServerInformation pdGroup1 = new ServerInformation(pdInstance1.getName(),pdInstance1.getNetServerCode(),pdInstance1.getId());
    private final ServerInformation pdGroup2 = new ServerInformation(pdInstance2.getName(),pdInstance2.getNetServerCode(),pdInstance2.getId());
    private EndPointStore endPointStore;
    private TestableEndPoint  primaryNetvertexEndPoint;
    private TestableEndPoint secondaryNetvertexEndPoint;
    private TestableNVSMXEndPoint pdEndPoint1;
    private TestableNVSMXEndPoint pdEndPoint2;




    @Before
    public void setUp() throws Exception {
        endPointStore = new EndPointStore();
        primaryNetvertexEndPoint = new TestableEndPoint(default_group,primaryInstance);
        secondaryNetvertexEndPoint = new TestableEndPoint(default_group,secondaryInstance);
        pdEndPoint1 = new TestableNVSMXEndPoint(pdGroup1,pdInstance1);
        pdEndPoint2 =  new TestableNVSMXEndPoint(pdGroup2,pdInstance2);
    }

    public class Add {
        @Test(expected = NullPointerException.class)
        public void shouldThrowNPEIfNullNetvertexEndPointAddedToStore() {
            endPointStore.addNetVertexEndPoint(null);

        }

        @Test(expected = NullPointerException.class)
        public void shouldThrowNPEIfNullNVSMXEndPointAddedToStore() {
            endPointStore.addNVSMXEndPointList(null);
        }

        @Test
        public void shouldAddNetvertexEndPoint(){
            endPointStore.addNetVertexEndPoint(primaryNetvertexEndPoint);
            Assert.assertTrue(endPointStore.getNetvertexEndPoints().contains(primaryNetvertexEndPoint));
        }

        @Test
        public void shouldAddNvsmxEndPoint(){
            endPointStore.addNVSMXEndPointList(pdEndPoint1);
            Assert.assertTrue(endPointStore.getNvsmxEndPoints().contains(pdEndPoint1));
        }


        @Test
        public void shouldNotAddNetvertexEndPointIfAlreadyExistInStore() {
            //addIng to endPoint store
            endPointStore.addNetVertexEndPoint(primaryNetvertexEndPoint);
            //if we again add,  then endpoint list should still contains only one End Point
            int sizeBeforeAddIngAgain = endPointStore.getNetvertexEndPoints().size();
            endPointStore.addIfAbsentNetVertexEndPoint(Arrays.asList(new EndPoint[]{primaryNetvertexEndPoint}));
            int sizeAfterAdding = endPointStore.getNetvertexEndPoints().size();
            Assert.assertEquals(sizeBeforeAddIngAgain,sizeAfterAdding);
        }


        @Test
        public void shouldAddNetvertexEndPointIfNotExistInStore() {
            //addIng to endPoint store
            endPointStore.addNetVertexEndPoint(primaryNetvertexEndPoint);
            //if we again add,  then endpoint list should still contains only one End Point
            int sizeBeforeAddIngAgain = endPointStore.getNetvertexEndPoints().size();
            endPointStore.addIfAbsentNetVertexEndPoint(Arrays.asList(new EndPoint[]{secondaryNetvertexEndPoint}));
            int sizeAfterAdding = endPointStore.getNetvertexEndPoints().size();
            Assert.assertEquals(sizeAfterAdding,sizeBeforeAddIngAgain+1);
        }

        @Test
        public void shouldNotAddNVSMXEndPointIfAlreadyExistInStore() {
            //addIng to endPoint store
            endPointStore.addNVSMXEndPointList(pdEndPoint1);
            //if we again add,  then endpoint list should still contains only one End Point
            int sizeBeforeAddIngAgain = endPointStore.getNvsmxEndPoints().size();
            endPointStore.addIfAbsentNvsxmEndPoint(Arrays.asList(new NVSMXEndPoint[]{pdEndPoint1}));
            int sizeAfterAdding = endPointStore.getNvsmxEndPoints().size();
            Assert.assertEquals(sizeBeforeAddIngAgain,sizeAfterAdding);
        }


        @Test
        public void shouldAddNVSMXEndPointIfNotExistInStore() {
            //addIng to endPoint store
            endPointStore.addNVSMXEndPointList(pdEndPoint1);
            //if we again add,  then endpoint list should still contains only one End Point
            int sizeBeforeAddIngAgain = endPointStore.getNvsmxEndPoints().size();
            endPointStore.addIfAbsentNvsxmEndPoint(Arrays.asList(new NVSMXEndPoint[]{pdEndPoint2}));
            int sizeAfterAdding = endPointStore.getNvsmxEndPoints().size();
            Assert.assertEquals(sizeAfterAdding,sizeBeforeAddIngAgain+1);

        }

    }


    public class Get {
        @Test
        public void shouldReturnNonNullListOfNetvertexEndPointsEvenifNoEndPointAdded() {
            Assert.assertTrue(Objects.nonNull(endPointStore.getNetvertexEndPoints()));
        }

        @Test
        public void shouldReturnNonNullMapOfNetvertexEndPointsEvenifNoEndPointAdded() {
            Assert.assertTrue(Objects.nonNull(endPointStore.getNetEngineServerCodeToEndPoint()));
        }
        @Test
        public void shouldReturnNonNullListOfNVSMXEndPointsEvenifNoNVSMXEndPointAdded() {
            Assert.assertTrue(Objects.nonNull(endPointStore.getNvsmxEndPoints()));
        }

        @Test
        public void shouldReturnNonNullMapOfNVSMXEndPointsEvenifNoNVSMXEndPointAdded() {
            Assert.assertTrue(Objects.nonNull(endPointStore.getIdToPDEndPoint()));
        }


        @Test
        public void shouldReturnNetvertexEndPointsListWhenAdded(){
            endPointStore.addNetVertexEndPoint(primaryNetvertexEndPoint);
            endPointStore.addNetVertexEndPoint(secondaryNetvertexEndPoint);
            Assert.assertEquals(2,endPointStore.getNetvertexEndPoints().size());
        }
        @Test
        public void shouldReturnNvsmxEndPointListWhenAdded() {
            endPointStore.addNVSMXEndPointList(pdEndPoint1);
            endPointStore.addNVSMXEndPointList(pdEndPoint2);
            Assert.assertEquals(2,endPointStore.getNvsmxEndPoints().size());
        }
    }
}