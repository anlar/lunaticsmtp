# LunaticSMTP

[![Build Status](https://github.com/anlar/lunaticsmtp/workflows/build/badge.svg)](https://github.com/anlar/lunaticsmtp/actions)
[![Release](https://img.shields.io/github/release/anlar/lunaticsmtp.svg)](https://github.com/anlar/lunaticsmtp/releases/latest)
[![License](https://img.shields.io/github/license/anlar/lunaticsmtp.svg)](https://github.com/anlar/lunaticsmtp/blob/master/LICENSE)

Dummy SMTP server with JavaFX gui for testing email sending applications.

![screenshot_linux](https://github.com/anlar/lunaticsmtp/raw/master/doc/images/screenshot_linux.png)

## Requirements

You need Java 17+ and JavaFX module to build and run this application.

## Installation

### Ubuntu (18.04+) and Debian (10+)

Download deb package from [latest release page](https://github.com/anlar/lunaticsmtp/releases/latest) and install it:

    # apt install ./lunaticsmtp_X.Y.Z-1_all.deb

## Usage

    Usage: lunaticsmtp [options]
      Options:
        -c, --cleanup      Remove saved on disk emails after shutdown (default:
                           false)
        -d, --directory    Directory to save incoming messages (default: incoming)
        -h, --help         Show short summary of options
        -j, --jump-to-last Automatically select last received email in GUI
                           (default: false)
        -n, --no-gui       Starts application without GUI (should be used with -s
                           argument) (default: false)
        -p, --port         Specify port for SMTP server (default: 2525)
        -s, --start        Starts SMTP server at application launch (default:
                           false)
        -t, --tray-mode    Set tray mode (none: disable tray; enable: enable tray;
                           minimize: enable tray and start application minimized)
                           (default: none) (values: [none, enable, minimize])
        -v, --version      Print version information and exit (default: false)
        -w, --write        Save incoming emails to disk (default: false)

## Similar projects

**[FakeSMTP](https://nilhcem.github.io/FakeSMTP/)**

* Swing UI
* Cross-platform (Java 6+)
* Can't display HTML emails
* Open source (BSD 3-Clause License)

## Development

To build this application you need Gradle and git (in addition to JDK 17+ with JavaFX module). From project directory execute one of the following commands:

1. Build and run application from source:

        $ ./gradlew run

2. Build uber-JAR (will be created in `app/build/libs`):

        $ ./gradlew shadowJar

3. Build deb-package (will be created in `app/build/distributions`):

        $ ./gradlew releaseDeb

Note: if you don't have git installed you should remove from `app/build.gradle` git invocation code at `gitRevision` and `gitShortRevision` variables.

## Copyright

Source code released under GPL3+, see [LICENSE](LICENSE) for details.

Some icons are rendered from the embedded [Font Awesome](http://fontawesome.io/) by Dave Gandy, made available under the [SIL OFL 1.1 License](http://scripts.sil.org/OFL).

