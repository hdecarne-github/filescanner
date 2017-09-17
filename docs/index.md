## FileScanner
Did you ever wonder what is inside a file. The FileScanner tool is capable to detect and decode several file formats even recursively. Data structures can be inspected in detail. Contained data can be viewed or exported.

This makes FileScanner a suitable tool for those who want to see the details or want to get a better understanding of a file format.

If you want to see a specific format be supported by FileScanner, just drop me a
message with a link to an example via [info@filescanner.org](mailto:info@filescanner.org)

### Current version (1.x)
![FileScanner 1.x](http://hdecarne.github.io/filescanner1.png)
The current 1.x version of the tool is available at [SourceForge.net](http://sourceforge.net/projects/filescanner/).

To run the software you need to have a Java 6 Runtime Environment or later installed. To install the tool fetch a package suitable for your platform from the project's download page and extract it to a directory of your choice. After that the directory contains a single executable Jar and the license files. Run the application by typing
```
 java -jar <filescanner jar> <command line>
```
in a terminal or use the corresponding menu of your desktop environment. As a command line argument you can optionally provide the file to scan.

### Next Generation version (2.x)
The next version of the tool is a complete rewrite with two major goals in mind:

 * A more generic scan and decoding engine that makes it easier to add additional formats (as long as they can be described in a reasonable simply manner).
 * A pure Java solution with not platform specific dependencies.

Version 2.x is currently under development on [GitHub](https://github.com/hdecarne/filescanner).
