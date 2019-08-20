package com.elitecore.aaa.radius.service.handlers;

import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;

/**
 * 
 * Represents a handler which contains some state and when the request processing
 * is resumed then the handler should start processing using the state stored.
 * An example is {@link ChainHandler} which represents a chain of handlers that need
 * to be processed, when some inner handler halts the processing; during some external
 * communication, on receiving the response the request resumption should start from 
 * the next handler in the chain which is depicted in below diagram.
 * 
 * <pre>
 *                          REQUEST                               
 *                         RESUMPTION
 *                          FROM 2nd                             
 *                             |                                 
 *                             |                                 
 * +----------------------------------------------------+
 * |                           |                        |
 * |      +---------+     +----v----+      +--------+   |
 * |      |         |     |         |      |        |   |
 * | +---->    1    +     |    2    |------>   3    |. .|
 * |      |         |     |         |      |        |   |
 * |      +----+----+     +---------+      +--------+   |
 * |           |                                        |
 * +----------------------------------------------------+
 *             |       RESUMABLE HANDLER                        
 *             |                                                   
 *             v                                                   
 *          EXTERNAL                                               
 *        COMMUNICATION                                           
 *   (CHAIN SUSPENDED AT 1)                                         
 * 
 * </pre>
 * 
 * @author narendra.pathai
 *
 * @param <T> type of request packet
 * @param <V> type of response packet
 */
public interface ResumableServiceHandler<T extends ServiceRequest, V extends ServiceResponse> {
	/**
	 * 
	 * This method is called when the request resumption takes place, the implementation
	 * should locate the state stored and start the resumption process.
	 * 
	 * @param request request to be resumed
	 * @param response response packet
	 * @param session session details
	 */
	public void resumeRequest(T request, V response, ISession session);
}
