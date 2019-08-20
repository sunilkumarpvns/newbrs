package com.elitecore.corenetvertex.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.constants.CommonConstants;

import javax.annotation.Nonnull;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by aditya on 5/22/17.
 */
public class NetworkUtil {


    public static final Predicate<InetAddress> REMOVE_LOCAL_ADDRESSES= inetAddress -> {
        String hostAddress = inetAddress.getHostAddress();
        if (hostAddress.startsWith("127.0")) {
            return false;
        }
        if (hostAddress.startsWith(CommonConstants.ALL_IP_ADDRESS)) {
            return false;
        }
        if (hostAddress.contains("%")) {
            return false;
        }
        return true;
    };

    public static final Function<InetAddress,String> INET_ADDRESS_TO_HOST_NAME = InetAddress::getHostAddress;

    public static final Collector<InetAddress,String> INET_ADDRESS_IN_COMMA_STRING = new INetAddressCollector();


    public static @Nonnull <T>  T getAllIPAddresses(Predicate<InetAddress> predicate, Collector<InetAddress, T> collector) throws SocketException {

        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        collector.start();
        while (networkInterfaces.hasMoreElements()) {

            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();


            while (inetAddressEnumeration.hasMoreElements()) {
                InetAddress inetAddress = inetAddressEnumeration.nextElement();

                if (predicate.apply(inetAddress)) {
                    collector.collect(inetAddress);
                }
            }

        }
        collector.stop();
        return collector.get();
    }



    public static @Nonnull <T>  List<T> getAllIPAddresses(Predicate<InetAddress> predicate, Function<InetAddress, T> function) throws SocketException {

        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        List<T> ipAddressList = Collectionz.newArrayList();

        while (networkInterfaces.hasMoreElements()) {

            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();

            while (inetAddressEnumeration.hasMoreElements()) {
                InetAddress inetAddress = inetAddressEnumeration.nextElement();

                if (predicate.apply(inetAddress)) {
                    T mapValue = function.apply(inetAddress);
                    ipAddressList.add(mapValue);
                }
            }
        }
        return ipAddressList;
    }


  private static class INetAddressCollector implements Collector<InetAddress, String> {
      private StringBuilder inetAddressesHostNames;

      @Override
      public void start() {
          inetAddressesHostNames = new StringBuilder();
      }

      @Override
      public void stop() {
          if(inetAddressesHostNames.length() > 0 ) {
              inetAddressesHostNames.deleteCharAt(inetAddressesHostNames.length() - 1);
          }
      }

      @Override
      public void collect(InetAddress inetAddress) {
          inetAddressesHostNames.append(inetAddress.getHostAddress()).append(CommonConstants.COMMA);
      }

      @Override
      public String get() {
          if(inetAddressesHostNames != null) {
              return inetAddressesHostNames.toString();
          } else {
              return null;
          }

      }
  }

}
