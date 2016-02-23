# LunaticSMTP [![Build Status](https://travis-ci.org/anlar/LunaticSMTP.svg?branch=master)](https://travis-ci.org/anlar/LunaticSMTP)

Dummy SMTP server with JavaFX gui for testing email sending applications.

## Requirements

You need Java 8u40 or newer to build and run this application.

## Usage

    Usage: java -jar LunaticSMTP.jar [options]
      Options:
        -d, --directory
           Directory to save incoming messages
           Default: incoming
        -h, --help
           Show short summary of options
           Default: false
        -j, --jump-to-last
           Automatically select last received email in GUI
           Default: false
        -n, --no-gui
           Starts application without GUI (should be used with -s argument)
           Default: false
        -p, --port
           Specify port for SMTP server
           Default: 2527
        -s, --start
           Starts SMTP server at application launch
           Default: false
        -w, --write
           Save incoming emails to disk
           Default: false

## Similar projects

**[FakeSMTP](https://nilhcem.github.io/FakeSMTP/)**

* Swing UI
* Cross-platform (Java 6+)
* Can't display HTML emails
* Open source (BSD 3-Clause License)

## Build

To build it you need Gradle:

    $ gradle release

## Copyright

Source code released under GPL3+, see [LICENSE](LICENSE) for details.
