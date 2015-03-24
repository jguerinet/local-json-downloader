/*
 * Copyright (c) 2015 Julien Guerinet. All rights reserved.
 */

import com.sun.net.ssl.*;
import org.json.JSONObject;
import sun.misc.BASE64Encoder;

import javax.net.ssl.*;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * Main file
 * @author Julien Guerinet
 * @version 1.0
 * @since 1.0
 */
public class LocalDataDownloader {
    public static String urlString;
    public static String fileName;
    public static boolean basicAuthentication;
    public static String username = "";
    public static String password = "";
    public static String directory = null;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException{
        //Make sure there are the correct number of arguments
        if(args.length < 3){
            throw new IllegalArgumentException("You must have at least 3 arguments");
        }

        //Get the needed info from the args
        urlString = args[0];
        fileName = args[1];
        basicAuthentication = Boolean.valueOf(args[2]);

        //If we need basic auth, get the information
        if(basicAuthentication){
            username = args[3];
            password = args[4];

            //Check if there is a directory
            if(args.length == 6){
                directory = args[5];
            }
        }
        //Check if there is a directory
        else if(args.length == 4){
            directory = args[3];
        }

        //Set up the connection
        URL url = new URL(urlString);
        HttpURLConnection connection;

        //Check if we need an HTTP or HTTPS connection
        if(urlString.contains("https")){
            //This wil trust all certificates
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        @Override
                        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                                throws java.security.cert.CertificateException {}
                        @Override
                        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                                throws java.security.cert.CertificateException {}
                    }
            };

            //Set up the SSL encryption
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            connection = (HttpsURLConnection) url.openConnection();
        }
        else{
            connection = (HttpURLConnection) url.openConnection();
        }

        //Add the basic auth if needed
        if(basicAuthentication){
            String basicAuth = new String(Base64.getEncoder().encode((username + ":" + password).getBytes()));
            connection.setRequestProperty("Authorization", "Basic " + basicAuth);
        }

        connection.connect();
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        //Only do something if the response code was 200
        if(responseCode == 200){
            //Get the data
            String dataString = downloadString(connection.getInputStream());

            //Convert it to JSON
            JSONObject dataJSON = new JSONObject(dataString);
        }
        else{
            System.out.println("Response Code not 200, aborting");
        }
    }

    /**
     * Takes an input stream and downloads whatever is in it in a String format
     *
     * @param inputStream The input stream
     * @return The contents in a String
     * @throws IOException
     */
    private static String downloadString(InputStream inputStream) throws IOException {
        //Read the JSON from the input stream
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();

        String line;
        while ((line = in.readLine()) != null) {
            builder.append(line);
        }

        //Return the String received
        return builder.toString();
    }
}
