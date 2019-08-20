*******************************************************************
*					Advanced Load balancing						  *
*							  + 								  *
*					Dynamic load balancing						  *
*******************************************************************

This project is a prototype project that proposes initial design that needs to be adopted if advanced
Load balancing algorithms are to be supported.

The scope of this design includes following algorithms:
1) Round robin
2) Weighted round robin
3) Random
4) Least response time
5) Least connections (can be possible)
6) Lowest pending requests
7) SNMP OID based load balancing (useful for balancing based on remote system memory)

---------------------------
| FAILOVER and SWITCHOVER |
---------------------------
One of the biggest takeaway from this project was that Failover and Switchover features are not load
balancing algorithms. They are mechanisms that the things which are being load balanced support.
Does the load balancer need to honor the DOWN event of any system or keep sending without bothering
of the current state of the system.

Failover
--------------
In case of failover the loadbalancer needs to automatically start sending requests to the other systems.

Switchover
--------------
As per the definition of the industry switchover is manual failover. In this case due to scheduled maintenance
system administrator manually switches traffic to secondary system.



DESIGN:
---------------
In terms of design the load balancer itself has the responsibility of forwarding the request and not just selects
the entity which is the present design. The older design expects user to present the type of load balancing algorithm
and creates the actual load balancer itself. But in case of some dynamic load balancing algorithms type also needs extra
parameters such as Key in case of sticky, SNMP OID in case if we need to load balance based on remote counter.


NOTE: Presently the design treats failover and swithover as load balancing algorithms which needs to change.


 