# lerkeveld-telsysteem

## Requirements
* java 14 or higher

## Setup
* add the fontawesome fa-solid-900.ttf file to src/main/resources. This files can be downloaded from the [fontawesome site](https://fontawesome.com/download) under the section Free from Web.
* on linux, allow gradlew to run with ```chmod +x gradlew```.

## Running code
Run the program in linux with:
```sh
./gradlew run
```
and in windows:
```cmd
gradlew.bat run
```

## Building application
Build the cross platform application jar in linux with
```sh
./gradlew jar
```
and in windows:
```cmd
gradlew.bat jar
```

The jar is stored in build/libs.
This jar can be run with any java version equal to or higher than the version used to build the jar:
```sh
javaw -jar build/libs/lerkeveld-telsysteem.jar
```
