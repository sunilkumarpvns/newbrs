+----------------------------------------------------+
|				   INTERACTIVE CLI					 |
|               CODENAME [EUREKA CLI]				 |
+----------------------------------------------------+

With this prototype we have targeted ELITEAAA-1581.

SALIENT FEATURES:
1) Continuous pushing of output from server side. Useful for statistics type of commands (running in a loop)
2) Blocking for user input in between command execution

This prototype opens dimensions for advanced level commands that have control of remote side.

Below is the flow diagram explaining the major steps involved in achieving interactivity


        CLIENT (CLI)                                             SERVER (COMMAND REPO)
   +----------------------------+   1 CONNECT TO REMOTE   +--------------------------------+
   |                            +------------------------->  USES STATIC URL SO THAT       |
   | USES DYNAMIC JMX URL       |   2 FETCH MXBEAN        |  CLIENT ALWAYS KNOWS THE URL   |
   | WHICH IS NEWLY CREATED     +------------------------->                                |
   | PER EXECUTION AND PER      |                         |  service:jmx:rmi:///jndi/rmi   |
   | CLIENT                     |   5 COMMAND FIRED       |  ://hostname:port/jmxrmi       |
   |                            +------------------------->                                |
   | service:jmx:rmi://hostname |   6 FETCH MXBEAN        |                                |
   | :port/stub/[BASE 64 STR]   <-------------------------+                                |
   |                            |   7 COMMAND OUTPUT      |                                |
   |                            <-------------------------+                                |
   +----+---------^------+--^---+  POSSIBLY INTERACTIVE   +--+--^--+-------^---------------+
        |         |      |  |                                |  |  |       |
        |         |      |  |       8 BLOCK FOR USER INPUT   |  |  |       |
        |         |      |  +--------------------------------+  |  |       |
        +---------+      |          9 USER INPUT                |  +-------+
      3 CREATE MXBEAN    +--------------------------------------+   10 CONTINUE COMMAND
      4 HOST MXBEAN                                                    EXECUTION
                                                                       (POSSIBLY REPEAT
                                                                        STEP 7,8,9,10)
                                                                        

EXPLANATION OF STEPS:
------------------------------------------

1) CONNECT TO REMOTE
CLI connects to the SERVER using the static URL provided.

2) FETCH MXBEAN
CLI tries to fetch the command controller or the command repository MXBean located at the remote
side. CLI fires the commands executed by user using this MXBean

3) CREATE MXBEAN
Client creates its own MXBean server. Taking note of the newly generated dynamic URL.

4) HOST MXBEAN
Client hosts all the MXBeans in the MBean server. Example Terminal MXBean for providing control
to the server of client terminal

5) COMMAND FIRED
When a command is fired on the client side, the client uses command controller to communicate
with the server and sends information such as its address [DYNAMIC URL], command fired with parameters
to the server.

6) FETCH MXBEAN
When server receives the call from client side, then it tries to connect to client MBean server using
the provided URL and fetches all the required MXBeans.

7) COMMAND OUTPUT [BLOCKING]
Server executes the command requested and pushes the output using the MXBeans. This communication can
go on for a bit of time as server may continue to push the output for some amount of time.

8) BLOCK FOR USER INPUT [BLOCKING]
Some commands on the server side may need additional parameters that are needed for further execution.
So the commands can block for user input using the MXBeans.

9) USER INPUT
As soon as user input is received on the client side, the input is marshalled to server.

10) CONTINUE                                                                         
On receiving the user input command execution is resumed. These steps of waiting for user input may continue
until command execution completes.