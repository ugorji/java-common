# java-common

This repository contains the `java-common` library.

It contains general utility functions and specific ones in io and logging.

It also contains some shared classes for managing users and groups in a generic way.

## Dependencies

This is typically used within a multi-project `gradle` build.
However, this package doesn't depend on any others.

It is used by my java-markup, java-facade and java-web packages, 
and whatever other modules depend on them.

## Building

```sh
gradle clean build
```
