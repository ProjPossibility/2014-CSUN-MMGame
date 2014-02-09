package com.example.hotpotato_2.Communication;

import android.content.Context;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by ngorgi on 1/9/14.
 */
public class IPfinder {

    public static String [] getIPs() {
        ArrayList<String> IPs = new ArrayList<String>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.isUp() && !intf.isLoopback()) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address) && inetAddress.isSiteLocalAddress()) {
                            IPs.add(inetAddress.getHostAddress());
                        }
                    }
                }

            }
        } catch (Exception ex) {

        }

        String[] ipArray = new String[IPs.size()];
        for (int i = 0; i < ipArray.length; i++) {
            ipArray[i] = IPs.get(i);
        }

        return ipArray;
    }
}