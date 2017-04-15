SikuliCef is a library that uses [java-cef-framebuffer](https://github.com/waicool20/java-cef-framebuffer) a custom fork of the original
[java-cef](https://bitbucket.org/chromiumembedded/java-cef) project to provide a web automation interface for [SikuliX](https://github.com/RaiMan/SikuliX-2014)
without taking total control of the system mouse.

# Trying out SikuliCef

Start by setting up the basic project structure by cloning
```
# Clone this repository
git clone git@github.com:waicool20/java-cef-framebuffer.git

# Retrieve submodule dependencies
git submodule update --recursive --remote

```
Build the java-cef fork using the instructions at: https://bitbucket.org/chromiumembedded/java-cef/wiki/BranchesAndBuilding

Build java sources using:
```
# windows
./gradlew.bat

# linux / mac
./gradlew.sh
```

A jar artifact will be generated in build/libs which can run a small demo
