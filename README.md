This is a Spring Boot application that can cache image files
downloaded from NASA's Mars Rover Photos API.

When built, the application produces an executable JAR file.
File paths containing dates can be submitted as arguments when running
from the command line. Photos for the dates indicated by the command line
arguments will be cached immediately.

The application also provides a simple API with five endpoints.
Examples:
* http://localhost:8080/api/v1/cache?date=2016-07-13
* http://localhost:8080/api/v1/cachedDates
* http://localhost:8080/api/v1/download?date=2016-07-13&fileName=1F521638128EFFCR03P1214L0M1-BR.JPG
* http://localhost:8080/api/v1/removeFromCache?date=2016-07-13
* http://localhost:8080/api/v1/clearCache

The cache method accepts a "date" parameter and returns a list of
photos cached for that date.

The cachedDates method does not accept and parameters and returns a list of dates that
are currently cached.

The download method accepts a "date" parameter and a "fileName"
parameter and returns the image file.
 
The removeFromCache method accepts a "date" parameter and does not return anything.

The clearCache method does not accept any parameters and does not return anything.

The number of threads used to download images can be configured
via the spring.task.execution.pool.max-size property in
application.properties.

Allowed date formats are:
* MM/dd/yy
* MMM d, yyyy
* MMM-d-yyyy
* yyyy-MM-dd.

Note: PhotoControllerTest appears to have regressed,
most likely as a result of dependency changes. It appears
that Tomcat is no longer starting at the beginning
of the test. This requires further investigation.