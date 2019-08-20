package com.brs.searchservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import  org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.brs.searchservice.beans.Fare;
import com.brs.searchservice.service.BusServiceImpl;
@RunWith(MockitoJUnitRunner.class)
public class BusServiceTest {
	@InjectMocks
private BusServiceImpl busService;
	@Test
	public void getServiceTaxTest1() {
		Fare fare = new Fare();
		fare.setTotalbasefare("1000");
	Assert.assertEquals(50, busService.getServiceTax(fare),2);
	}

	@Test
	public void getServiceTaxTest2() {
		Fare fare = new Fare();
		fare.setTotalbasefare("100");
	Assert.assertEquals(5, busService.getServiceTax(fare),1);
	}
	
	@Test
	public void getServiceTaxTest3() {
		Fare fare = new Fare();
		fare.setTotalbasefare("100");
	Assert.assertNotEquals(6.5, busService.getServiceTax(fare),1);
	}
	
	@Test
	
	public void getServiceTaxTest4() {
		Fare fare = new Fare();
		fare.setTotalbasefare("100");
	Assert.assertTrue(busService.getServiceTax(fare)>0);
	}
	@Test
	public void getTotalBaseFare1() throws Exception{
		com.brs.searchservice.models.model.Fare fare = new com.brs.searchservice.models.model.Fare();
		fare.setTotalbasefare("1000");
	  assertEquals(1200.0,busService.getTotalBaseFare(fare, "31-08-2018"),0.0);
	}
	@Test
	public void getTotalBaseFare2() throws Exception{
		com.brs.searchservice.models.model.Fare fare = new com.brs.searchservice.models.model.Fare();
		fare.setTotalbasefare("1000");
	  assertNotEquals(1260.0,busService.getTotalBaseFare(fare, "31-08-2018"),50.0);
	}
}
