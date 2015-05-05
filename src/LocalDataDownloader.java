/*
 * Copyright (c) 2015 Julien Guerinet. All rights reserved.
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Main class, executes the main code for downloading and saving the local data
 * @author Julien Guerinet
 * @version 1.2
 * @since 1.0
 */
public class LocalDataDownloader {
   /* FILE STRINGS */
    /**
     * The URL in the file
     */
    private static final String URL = "Data URL:";
    /**
     * The name of the text file you want to save (with an absolute path if desired)
     */
    private static final String FILE_NAME = "File Name:";
    /**
     * Username for authentication, if any
     */
    private static final String USERNAME = "Username:";
    /**
     * Password for authentication, if any
     */
    private static final String PASSWORD = "Password:";

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException,
            KeyManagementException{
        //Instantiate the instance variables
        String urlString = null;
        String username = null;
        String password = null;

        //Read from the config file
        BufferedReader configReader = null;
        try{
            configReader = new BufferedReader(new FileReader("../config.txt"));
        }
        catch(FileNotFoundException e){
            try{
                configReader = new BufferedReader(new FileReader("config.txt"));
            }
            catch(FileNotFoundException ex){
                System.out.println("Error: Config file not found");
                System.exit(-1);
            }
        }

        //Go through the file, line by line
        String line;
        while ((line = configReader.readLine()) != null) {
            //Get the URL
            if(line.startsWith(URL)){
                urlString = line.replace(URL, "").trim();
            }
            //Get the username
            else if(line.startsWith(USERNAME)){
                username = line.replace(USERNAME, "").trim();
            }
            //Get the password
            else if(line.startsWith(PASSWORD)){
                password = line.replace(PASSWORD, "").trim();
            }
            //Get the file name - signifies the end of the info for one file
            else if(line.startsWith(FILE_NAME)){
                //Have a line separation for each file
                System.out.println();
                //Download the info with the given information
                downloadInfo(urlString, line.replace(FILE_NAME, "").trim(), username, password);
                //Reset everything
                urlString = null;
                username = null;
                password = null;
            }
        }
        configReader.close();
    }

    private static void downloadInfo(String urlString, String fileName, String username,
                                     String password) throws IOException{
        //Make there is a URL
        if(urlString == null){
            System.out.println("Error: URL Cannot be null. Skipping");
            return;
        }
        //Make sure there is a file path
        else if(fileName == null){
            System.out.println("Error: The file name cannot be null. Skipping");
            return;
        }

        //Set up the connection
        Request.Builder builder = new Request.Builder()
                .get()
                .url(urlString);

        //Add the basic auth if needed
        if(username != null && password != null){
            String basicAuth = new String(Base64.getEncoder().encode(
                    (username + ":" + password).getBytes()));
            builder.addHeader("Authorization", "Basic " + basicAuth);
        }

        System.out.println("Connecting to " + urlString);
        Response response = new OkHttpClient().newCall(builder.build()).execute();
        int responseCode = response.code();
        System.out.println("Response Code: " + responseCode);

        //Only do something if the response code was 200
        if(responseCode == 200){
            ObjectMapper mapper = new ObjectMapper();
            JsonNode dataJSON = mapper.readTree(response.body().string());

            //Set up the file writer
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");

            //Set up the JSON Object Writer
            ObjectWriter objectWriter = mapper.writer().withDefaultPrettyPrinter();

            //Write the JSON to the file
            String string = objectWriter.writeValueAsString(dataJSON);
            writer.print(string);
            writer.flush();
            writer.close();

            System.out.println("Writing to " + fileName + " complete.");
        }
        else{
            System.out.println("Response Code not 200, skipping");
            System.out.println("Response Message: " + response.message());
        }
    }
}