# M09 UF3 Practice
## Project description

Console application to create files hashes

## Project structure

The main class is called Program. When run, it 
* loads the hashing configuration parameters from the file _hash.properties_, then
*  asks for the file path and
* shows the calculated digest and the salt.
* It asks whether you want to hash another file or exit the program
* And it does all of this while checking every 60 seconds if the hash.properties file has been updated, and then, if it has, it uses the new algorithm and/or salt to generate the hashes  

## Utilities
* _SaltGenerator_: Run this class if to generate *salt.txt* with some random salts in base64

## TODO

* Programm unit tests for the hash.properties updates feature 

## Versions

| Version | Changes              |
|---------|----------------------|
| 1.0.1   | Main functionalities |