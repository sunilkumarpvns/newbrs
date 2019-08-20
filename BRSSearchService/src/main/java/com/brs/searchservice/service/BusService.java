package com.brs.searchservice.service;

import com.brs.searchservice.beans.Response;

public interface BusService {
public Response searchBus(String source,String destination,String doj,String drj);
}
