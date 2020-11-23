package mars.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import mars.core.Constants;
import mars.core.MarsApplicationException;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PhotoServiceImpl implements PhotoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoServiceImpl.class);

    private static final int API_PAGE_SIZE = 25;

    private final RoverService roverService;
    private final TaskExecutor taskExecutor;
    private final ObjectMapper objectMapper;

    public PhotoServiceImpl(RoverService roverService, TaskExecutor taskExecutor, ObjectMapper objectMapper) {
        this.roverService = roverService;
        this.taskExecutor = taskExecutor;
        this.objectMapper = objectMapper;
    }

    public List<Photo> cachePhotos(String earthDate) throws MarsApplicationException {
        File dateDirectory = new File(
                "photo_cache" + "/" + earthDate
        );
        if(dateDirectory.exists()) {
            LOGGER.debug("Date [" + earthDate + "] is already cached.");
            String[] fileNames = dateDirectory.list();
            //noinspection ConstantConditions
            List<Photo> result = new ArrayList<>(fileNames.length);
            for(String fileName : fileNames) {
                result.add(new Photo(earthDate, fileName));
            }
            return result;
        }
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Processing photos for earthDate [" + earthDate + "].");
        }
        List<String> roverNames = roverService.requestRoverNames();
        List<Photo> photos = requestPhotos(roverNames, earthDate);
        for (Photo photo : photos) {
            // Download photos asynchronously.
            taskExecutor.execute(
                    new PhotoDownloadTask(
                            photo.getEarthDate(),
                            photo.getImgSrc()
                    )
            );
        }
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Done submitting photo processing tasks for earthDate [" + earthDate + "].");
        }
        return photos;
    }

    public List<Photo> requestPhotos(List<String> roverNames, String earthDate) throws MarsApplicationException {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Requesting photos for earthDate [" + earthDate + "].");
        }

        List<Photo> result = new ArrayList<>();
        for(String roverName : roverNames) {
            try(CloseableHttpClient client = HttpClients.createDefault()) {
                PhotoResponse response;
                int page = 1;
                do {
                    if(LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Requesting page " + page + " of photos for rover [" + roverName + "], date [" + earthDate + "].");
                    }
                    HttpGet request = new HttpGet(Constants.BASE_URL + "/rovers/" + roverName + "/photos" +
                            "?earth_date=" + earthDate +
                            "&" + Constants.API_KEY_PARAM +
                            "&page=" + page
                    );
                    HttpResponse rawResponse = client.execute(request);
                    if (rawResponse.getStatusLine().getStatusCode() != 200) {
                        throw new MarsApplicationException("Request for photos returned status code [" + rawResponse.getStatusLine().getStatusCode() + "].");
                    }
                    response = objectMapper.readValue(
                            rawResponse.getEntity().getContent(),
                            PhotoResponse.class
                    );
                    result.addAll(response.getPhotos());
                    if(LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Done requesting page " + page + " of photos for rover [" + roverName + "], date [" + earthDate + "].");
                    }
                    page++;
                } while(response.getPhotos().size() >= API_PAGE_SIZE);

            } catch (IOException e) {
                throw new MarsApplicationException("Failed to request photos for rover [" + roverName + "], date [" + earthDate + "].", e);
            }
        }
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Done requesting photos for earthDate [" + earthDate + "].");
        }
        return result;
    }

    public boolean removeFromCache(String earthDate) {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing date [" + earthDate + "] from cache.");
        }
        boolean result = FileUtils.deleteQuietly(new File("photo_cache/" + earthDate));
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Done removing date [" + earthDate + "] from cache.");
        }
        return result;
    }

    public boolean clearCache() {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Clearing cache.");
        }
        boolean result = FileUtils.deleteQuietly(new File("photo_cache"));
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Done clearing cache.");
        }
        return result;
    }
}
