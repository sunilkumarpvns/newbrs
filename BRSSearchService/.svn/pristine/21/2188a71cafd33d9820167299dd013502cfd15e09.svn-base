package com.brs.searchservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brs.searchservice.beans.Response;
import com.brs.searchservice.service.BusService;

@RestController
@RequestMapping(value="bus")
public class BusController {
	@Autowired
private BusService busService;
	@RequestMapping(value="searchBus",method=RequestMethod.GET)
	@ResponseBody
	public Response searchBus(
			@RequestParam("source") String source,
			@RequestParam("destination")		String destination,
			@RequestParam("doj")		String doj,
			@RequestParam("drj")		String drj) {
 return  busService.searchBus(source, destination, doj, drj);
}
}
