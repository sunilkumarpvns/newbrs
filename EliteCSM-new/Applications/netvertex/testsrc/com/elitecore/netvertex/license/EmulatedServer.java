package com.elitecore.netvertex.license;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EmulatedServer {

    private ServerSocket serverSocket ;
    private boolean isClosed = false;
    private String response;
    private String responseCode = "200";

    private class ServerThread implements Runnable{
        @Override
        public void run() {
           try {
               while (isClosed==false) {
                   Socket clientSocket = serverSocket.accept();
                   System.out.println("Now client can connect");

                   BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                   BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                   String s;
                   while ((s = in.readLine()) != null) {
                       System.out.println(s);
                       if (s.isEmpty()) {
                           break;
                       }
                   }

                   out.write("HTTP/1.0 "+responseCode+" OK\r\n");
                   out.write("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n");
                   out.write("Server: Apache/0.8.4\r\n");
                   out.write("Content-Type: text/html\r\n");
//                   out.write("Content-Length: 59\r\n");
                   out.write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n");
                   out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
                   out.write("\r\n");
                   out.write(response);

                   System.out.println("Closing and returning response");
                   out.close();
                   in.close();
                   clientSocket.close();
               }
           } catch (Exception e){
               //Sonar Ignore
           }
        }
    }

    public void startSocket(int port, String response, String responseCode) throws IOException{
        this.responseCode = responseCode;
        stopServer();
        System.out.println("Starting server on port : " + port);
        serverSocket = new ServerSocket(port);
        isClosed=false;
        this.response = response;
        // repeatedly wait for connections, and process

        new Thread(new ServerThread()).start();
    }

   /* public void startSocket(int port, String response, String responseCode) throws IOException{
        startSocket(port,response);
    }*/

    public void stopServer() throws IOException{
        if(serverSocket!=null){
            serverSocket.close();
        }
        isClosed = true;
    }

}
