# Local Json Downloader

## Summary
The Local Json Downloader takes the Json at the given URL(s) and saves it to a text file, with the proper formatting.
It will also check that the Json is valid. Basic authentication is supported.

## Instructions
To use this: 

* Download the [jar][1] and the [sample config](sample-config.txt)
* Set up your config file (follow the instructions in sample-config.txt)
* Your data must be at the given url in plain text
* Run the jar

[1]:https://github.com/jguerinet/local-json-downloader/releases/latest

## Gradle Dependencies
* [OkHttp](https://github.com/square/okhttp)
* [jackson](https://github.com/FasterXML/jackson)

## Contributors
* [Julien Guerinet](https://github.com/jguerinet)

## Version History
See the [Change Log](CHANGELOG.md).

## Copyright 
    Copyright 2015-2016 Julien Guerinet

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
