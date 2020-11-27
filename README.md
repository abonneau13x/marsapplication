This is a Spring Boot application with docker integration that can cache image files
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

The application also includes a docker-integrated client that runs on port 80.
It allows the user to enter a date, which will cause the date to be cached
(if it is not already) and will display the cached images for that date.
Please note that the client is only a proof of concept and has not been implemented
in a production-ready way.

The client can be accessed via:
* http://localhost

Future work:
* Re-implement client in React
* Configure NodeJS to serve photos directly from the photo cache by using the photo_cache
volume, rather then downloading them via the API
* Improve client look, feel, and UX
* Add integration tests for the UI itself.
* Store image metadata in a database (in a separate docker container)
and allow queries on more than date