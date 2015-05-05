# Local Data Downloader

## Version History
* Version 1.4 - May 05, 2015 (Using okio and okhttp)
* Version 1.3 - April 16, 2015 (Added logging)
* Version 1.2 - April 15, 2015 (Use of a config file and multiple data file support)
* Version 1.1 - April 06, 2015 (ImmunizeCA Branch)
* Version 1.0 - March 24, 2015 (Initial Code)

## Summary
The Local Data Downloader takes the JSON at the given URL(s) and saves it to a text file, with the proper formatting.
It will also check that the JSON is valid.

## Instructions
To use this, follow the instructions in Sample Config.txt. Please keep in mind that: 

* Your data must be at the given url in plain text
* Basic authentication is supported

## Branches
* master: Contains the main code 
* dev: Contains WIP code
* ImmunizeCA: Contains ImmunizeCA specific code, branched off of master. This branch downloads and saves the banner images as well. 

## Directories
* libs: All of the necessary external libraries (in jar format)
* src: The source code

## Contributors
* Julien Guerinet

##Copyright
Copyright (c) Julien Guerinet. All rights reserved.
