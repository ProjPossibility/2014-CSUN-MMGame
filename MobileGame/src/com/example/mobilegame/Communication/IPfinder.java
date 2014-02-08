package com.example.mobilegame.Communication;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by Cory on 2/8/14.
 */
public class IPfinder {
    public static String getIPstring() {
        ArrayList<String> ip = getIPs();
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : ip) {
            stringBuilder.append(s);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
    public static ArrayList<String> getIPs() {
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
        return IPs;
    }
}
