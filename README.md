This is a Spring Boot application that can cache image files
downloaded from NASA's Mars Rover Photos API.

When built, the application produces an executable JAR file.
File paths containing dates can be submitted as arguments when running
from the command line. Photos for the dates indicated by the command line
arguments will be cached immediately.

The application also provides a simple API with three endpoints.
* cache
* removeFromCache
* clearCache

The cache and removeFromCache methods accept a "date" parameter.
The clearCache method does not accept any parameters.
All methods return true if the operation succeeded and false if it did not.
Note that image caching is asynchronous, so the cache method will return
true if the download tasks have been submitted successfully.

Please note that I have not tested deployment of this application
into a container yet and as a result I have not been able to test the
controller.

Allowed date formats are:
* MM/dd/yy
* MMM d, yyyy
* MMM-d-yyyy
* yyyy-MM-dd.