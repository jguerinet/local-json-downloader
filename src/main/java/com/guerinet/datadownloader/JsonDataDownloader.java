/*
 * Copyright 2015 Julien Guerinet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guerinet.datadownloader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Main class, executes the main code for downloading and saving the local data
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class JsonDataDownloader {
   /* FILE STRINGS */
    /**
     * Header of the URL where the info is
     */
    private static final String URL = "Data URL:";
    /**
     * Header of the path of the file to save the info to
     */
    private static final String FILE_PATH = "File Name:";
    /**
     * Header of the username for authentication, if any
     */
    private static final String USERNAME = "Username:";
    /**
     * Header of the password for authentication, if any
     */
    private static final String PASSWORD = "Password:";
    /**
     * {@link OkHttpClient} instance
     */
    private static OkHttpClient client;
    /**
     * Json {@link ObjectMapper}
     */
    private static ObjectMapper mapper;
    /**
     * Json {@link ObjectWriter}
     */
    private static ObjectWriter writer;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException,
            KeyManagementException {
        // Check if the config file path is part of the args
        String configPath = "config.txt";
        if (args.length == 1) {
            configPath = args[0];
        }

        // Instantiate the instance variables
        String url = null;
        String username = null;
        String password = null;

        // Read from the config file
        BufferedReader configReader;
        try {
            configReader = new BufferedReader(new FileReader(configPath));
        } catch (FileNotFoundException e) {
            System.out.println("Error: Config file not found");
            System.exit(-1);
            return;
        }

        // Set up the OkHttp client
        client = new OkHttpClient();

        // Set up the Json object mapper
        mapper = new ObjectMapper();

        // Set up the Json object writer
        writer = mapper.writer().withDefaultPrettyPrinter();

        // Go through the file, line by line
        String line;
        while ((line = configReader.readLine()) != null) {
            if (line.startsWith(URL)) {
                // Get the URL
                url = line.replace(URL, "").trim();
            } else if (line.startsWith(USERNAME)) {
                // Get the username
                username = line.replace(USERNAME, "").trim();
            } else if (line.startsWith(PASSWORD)) {
                // Get the password
                password = line.replace(PASSWORD, "").trim();
            } else if (line.startsWith(FILE_PATH)) {
                // Get the file name - signifies the end of the info for one file
                // Have a line separation for each file
                System.out.println();
                // Download the info with the given information
                downloadInfo(url, line.replace(FILE_PATH, "").trim(), username, password);
                // Reset everything
                url = null;
                username = null;
                password = null;
            }
        }
        configReader.close();
    }

    /**
     * Downloads the info using the given parameters
     *
     * @param url      URL to download the file from
     * @param filePath Path of the file to save the info to
     * @param username Basic auth username, if any
     * @param password Basic auth password, if any
     * @throws IOException
     */
    private static void downloadInfo(String url, String filePath, String username, String password) throws IOException {
        if (url == null) {
            // Make sure there is a URL
            System.out.println("Error: URL cannot be null. Skipping");
            return;
        } else if (filePath == null) {
            // Make sure there is a file path
            System.out.println("Error: File path cannot be null. Skipping");
            return;
        }

        // Build the request
        Request.Builder builder = new Request.Builder()
                .get()
                .url(url);

        // Add the basic auth if needed
        if (username != null && password != null) {
            builder.addHeader("Authorization", Credentials.basic(username, password));
        }

        System.out.println("Connecting to " + url);

        Response response;
        try {
            response = client.newCall(builder.build()).execute();
        } catch (IOException e) {
            // Catch the exception here to be able to continue a build even if we are not connected
            System.out.println("IOException while connecting to the URL");
            System.out.println("Error Message: " + e.getMessage());
            return;
        }

        System.out.println("Response Code: " + response.code());

        // Only do something if the call was a success
        if (response.isSuccessful()) {
            // Parse the data into Json format
            JsonNode dataJson = mapper.readTree(response.body().string());

            // Write the resulting Json onto the file
            writer.writeValue(new File(filePath), dataJson);

            System.out.println("Writing to " + filePath + " complete.");
        } else {
            System.out.println("Request not successful, skipping");
            System.out.println("Response Message: " + response.message());
        }
    }
}