# Local Data Downloader

## Summary
The Local Data Downloader takes the JSON at the given URL and saves it to a text file, with the proper formatting.
It will also check that the JSON is valid.

## Instructions
You must give it the following parameters (in this order):

* The URL to the JSON
* The name of the text file you want to save (with an absolute path if desired)
* ImmunizeCA Branch Only: The path of the folder to save the images in
* True if there is basic authentication on the URL, false otherwise
* If the above is true, the username for authentication
* If the above is true, the password for authentication

## Branches
* master: Contains the main code 
* ImmunizeCA: Contains ImmunizeCA specific code, branched off of master. This branch downloads and saves the banner images as well. 

## Directories
* libs: All of the necessary external libraries (in jar format)
* src: The source code

## Contributors
* Julien Guerinet
