Http-Server
===========

This is a very simple Http Server built for demonstrative purposes.

It's limited set of features include:
* Multi-Threaded design to support concurrent requests
* File-based MIME Type resolver, easily changeable to other type of resolver
* GZIP Compression for responses
* Very simple directory listing
* Configuration via external properties file (with fall-back to defaults)
* Streaming large files without loading them in-memory
* Graceful shutdown

### Requirements ###

In order to build and run Http-Server you will need: 
* Java 1.7
* Maven

### Building ###

You will need `Maven` to compile the project. Run the following command in the project root directory:

```
mvn package
```

### Running ###

By default the server will use the current working directory as a base-path for serving documents; change this
to something more appropriate in the sample `server.properties` file provided in the project root

You should run Http-Server by using the `target/http-server-${VERSION}-jar-with-dependencies.jar` 
(replace `${VERSION}` with the actual version) file created by `maven`.

The JAR file contains a main class manifest so you can run it like this:

```
java -jar target/http-server-${VERSION}-jar-with-dependencies.jar server.properties
```

The command line takes one argument, the path for the `server.properties` file. If this is not specified
or the file can't be read, default properties will be used. You can check the default properties in the
`resources` folder.

### Closing ###

To close the server press `CTRL + C`. The server will shut down gracefully.
