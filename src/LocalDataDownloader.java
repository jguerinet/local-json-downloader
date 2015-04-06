/*
 * Copyright (c) 2015 Julien Guerinet. All rights reserved.
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
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

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException,
            KeyManagementException{
        //Make sure there are the correct number of arguments
        if(args.length < 4){
            throw new IllegalArgumentException("You must have at least 4 arguments");
        }

        //Get the needed info from the args
        String urlString = args[0];
        String fileName = args[1];
        String bannersLocation = args[2];

        boolean basicAuthentication = Boolean.valueOf(args[3]);

        //If we need basic auth, get the information
        String username;
        String password;
        if(basicAuthentication){
            username = args[4];
            password = args[5];
        }
        else{
            username = "";
            password = "";
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
            String basicAuth = new String(Base64.getEncoder().encode(
                    (username + ":" + password).getBytes()));
            connection.setRequestProperty("Authorization", "Basic " + basicAuth);
        }

        connection.connect();
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        //Only do something if the response code was 200
        if(responseCode == 200){
            //Get the data
            String dataString = downloadString(connection.getInputStream());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode dataJSON = mapper.readTree(dataString);

            //Go through the nodes that have a Banner Id
            for(JsonNode node : dataJSON.findParents("BannerId")){
                // Go through the banner's image links
                JsonNode imageJSON = node.get("Image");

                long bannerId = node.get("BannerId").asLong();

                //Download the English image
                String englishFile = downloadImage(bannersLocation, imageJSON.get("en").asText(),
                        bannerId, "en");

                //Replace the English image path with the file name
                ((ObjectNode)imageJSON).put("en", englishFile);

                //Download the French image
                String frenchFile = downloadImage(bannersLocation, imageJSON.get("fr").asText(),
                        bannerId, "fr");

                //Replace the French image path with the file name
                ((ObjectNode)imageJSON).put("fr", frenchFile);
            }

            //Set up the file writer
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");

            //Set up the JSON Object Writer
            ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();

            //Write the JSON to the file
            String string = objectWriter.writeValueAsString(dataJSON);
            writer.print(string);
            writer.flush();
            writer.close();

            System.out.println("Writing complete, aborting");
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

    /**
     * Download the banner image
     *
     * @param imagePath  The folder to store the image in
     * @param imageURL   The image URL
     * @param bannerId   The banner Id for the file name
     * @param languageId The language Id for the file name
     * @return The file name
     * @throws IOException
     */
    private static String downloadImage(String imagePath, String imageURL, long bannerId,
                                      String languageId) throws IOException{
        //Set up the file name
        String fileName = "banner" + bannerId + "-" + languageId + ".jpg";

        URL url = new URL(imageURL);
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while ((n = in.read(buf)) != -1){
            out.write(buf, 0, n);
        }

        out.close();
        in.close();
        byte[] response = out.toByteArray();

        FileOutputStream fos = new FileOutputStream(imagePath + "/" + fileName);
        fos.write(response);
        fos.close();

        return fileName;
    }
}