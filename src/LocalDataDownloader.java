/*
 * Copyright (c) 2015 Julien Guerinet. All rights reserved.
 */

/**
 * Main file
 * @author Julien Guerinet
 * @version 1.0
 * @since 1.0
 */
public class LocalDataDownloader {
    public static String url;
    public static String fileName;
    public static boolean basicAuthentication;
    public static String username = "";
    public static String password = "";
    public static String directory = null;

    public static void main(String[] args){
        //Make sure there are the correct number of arguments
        if(args.length < 3){
            throw new IllegalArgumentException("You must have at least 3 arguments");
        }

        //Get the needed info from the args
        url = args[0];
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
    }
}
