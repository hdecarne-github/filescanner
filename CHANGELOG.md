## Changelog:
This is a beta release of the completely rewritten FileScanner 2.0 application.

Main enhancements are:
* A new generic format specification driven [scan engine](https://github.com/hdecarne/filescanner-engine).
* Ad-hoc full-text search support via [Apache Lucene](https://lucene.apache.org).
* Native Installer, Launcher and automatic update support via [Install4J](https://www.ej-technologies.com/products/install4j/overview.html).

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

![Install4J](http://certmgr.carne.de/install4j_small.png) The provided installer/launcher packages have been created using the multi-platform installer builder [Install4J](https://www.ej-technologies.com/products/install4j/overview.html).

### v2.0.0-beta3 (2021-01-26)
* Switch to Java11
* Add alternate view selection
* Various fixes and enhancements

### v2.0.0-beta2 (2020-04-13)
* Add supported for paged rendering of large scan results
* New or updated decoders: Mach-O and ELF image format
* Various fixes and enhancements

### v2.0.0-beta1 (2020-03-08)
* Integrate update check for Install4j based packages
* New or updated decoders: CPIO format
