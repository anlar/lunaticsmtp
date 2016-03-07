# LunaticSMTP [![Build Status](https://travis-ci.org/anlar/LunaticSMTP.svg?branch=master)](https://travis-ci.org/anlar/LunaticSMTP)

Dummy SMTP server with JavaFX gui for testing email sending applications.

![screenshot_linux](https://github.com/anlar/LunaticSMTP/raw/master/doc/images/screenshot_linux.png)

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
           Default: 2525
        -s, --start
           Starts SMTP server at application launch
           Default: false
        -t, --tray-mode
           Set tray mode (none: disable tray; enable: enable tray; minimize: enable
           tray and start application minimized)
           Default: none
           Possible Values: [none, enable, minimize]
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

To build it you need Gradle and git. From project directory execute the following:

    $ gradle release

Result jar will be created in `build/libs`.

Note: if you don't have git installed you should remove from `build.gradle` git invocation code at `gitRevision` and `gitShortRevision` variables.

## Copyright

Source code released under GPL3+, see [LICENSE](LICENSE) for details.
