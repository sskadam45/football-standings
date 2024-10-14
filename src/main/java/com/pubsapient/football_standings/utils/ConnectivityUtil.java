package com.pubsapient.football_standings.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectivityUtil {

    public static boolean isOnline() {
        try {
            final URL url = new URL("http://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            if (responseCode > 199 && responseCode < 400) {
                return true;
            }
        } catch (IOException e) {
            System.out.println("No internet connectivity: " + e.getMessage());
        }
        return false;
    }
}