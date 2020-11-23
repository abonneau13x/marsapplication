This is a Spring Boot application that can cache image files
downloaded from NASA's Mars Rover Photos API.

When built, the application produces an executable JAR file.
File paths containing dates can be submitted as arguments when running
from the command line. Photos for the dates indicated by the command line
arguments will be cached immediately.

The application also provides a simple API with three endpoints.
Examples:
* http://localhost:8080/api/v1/cache?date=2016-07-13
* http://localhost:8080/api/v1/removeFromCache?date=2016-07-13
* http://localhost:8080/api/v1/clearCache

The cache and removeFromCache methods accept a "date" parameter.
The clearCache method does not accept any parameters.
All methods return true if the operation succeeded and false if it did not.
Note that image caching is asynchronous, so the cache method will return
true if the download tasks have been submitted successfully.

The number of threads used to download images can be configured
via the spring.task.execution.pool.max-size property in
application.properties.

Allowed date formats are:
* MM/dd/yy
* MMM d, yyyy
* MMM-d-yyyy
* yyyy-MM-dd.