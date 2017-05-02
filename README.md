SikuliCef is a library that uses [java-cef-framebuffer](https://github.com/waicool20/java-cef-framebuffer) a custom fork of the original
[java-cef](https://bitbucket.org/chromiumembedded/java-cef) project to provide a web automation interface for [SikuliX](https://github.com/RaiMan/SikuliX-2014)
without taking total control of the system mouse.

# Trying out SikuliCef

Start by setting up the basic project structure by cloning

```
# Clone this repository
git clone git://github.com/waicool20/java-cef-framebuffer.git

# Retrieve submodule dependencies
git submodule update --init --recursive --remote

```

Build the java-cef fork using the instructions at: https://bitbucket.org/chromiumembedded/java-cef/wiki/BranchesAndBuilding

###To build the library only:

```
# Windows
./gradlew.bat

# Linux / MacOSX
./gradlew.sh
```

A jar artifact containing only the library will be generated in build/libs directory.

###To build the testing demo:

```
# Windows
./gradlew.bat testJar

# Linux / MacOSX
./gradlew.sh testJar
```

A jar artifact containing the test demo jar executable will be generated in build/libs directory.
