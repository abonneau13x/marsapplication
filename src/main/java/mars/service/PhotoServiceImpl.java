package mars.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mars.core.Constants;
import mars.core.MarsApplicationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class PhotoServiceImpl implements PhotoService {
    private static final Log LOG = LogFactory.getLog(PhotoServiceImpl.class);
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
            LOG.debug("Date [" + earthDate + "] is already cached.");
            String[] fileNames = dateDirectory.list();
            //noinspection ConstantConditions
            List<Photo> result = new ArrayList<>(fileNames.length);
            for(String fileName : fileNames) {
                result.add(new Photo(earthDate, fileName));
            }
            return result;
        }
        if(LOG.isDebugEnabled()) {
            LOG.debug("Processing photos for earthDate [" + earthDate + "].");
        }
        try {
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
            if(LOG.isDebugEnabled()) {
                LOG.debug("Done submitting photo processing tasks for earthDate [" + earthDate + "].");
            }
            return photos;
        } catch(Exception e) {
            throw new MarsApplicationException("Failed to process photos for date [" + earthDate + "].", e);
        }
    }

    public List<Photo> requestPhotos(List<String> roverNames, String earthDate) throws MarsApplicationException {
        if(LOG.isDebugEnabled()) {
            LOG.debug("Requesting photos for earthDate [" + earthDate + "].");
        }

        List<Photo> result = new ArrayList<>();
        for(String roverName : roverNames) {
            try(CloseableHttpClient client = HttpClients.createDefault()) {
                JsonNode photos;
                int page = 1;
                do {
                    if(LOG.isDebugEnabled()) {
                        LOG.debug("Requesting page " + page + " of photos for rover [" + roverName + "], date [" + earthDate + "].");
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
                    JsonNode response = objectMapper.readValue(
                            rawResponse.getEntity().getContent(),
                            ObjectNode.class
                    );
                    photos = response.get("photos");
                    for (JsonNode photo : photos) {
                        result.add(new Photo(photo.get("earth_date").asText(), photo.get("img_src").asText()));
                    }
                    if(LOG.isDebugEnabled()) {
                        LOG.debug("Done requesting page " + page + " of photos for rover [" + roverName + "], date [" + earthDate + "].");
                    }
                    page++;
                } while(photos.size() >= 25);

            } catch (Exception e) {
                throw new MarsApplicationException("Failed to request photos for rover [" + roverName + "], date [" + earthDate + "].", e);
            }
        }
        if(LOG.isDebugEnabled()) {
            LOG.debug("Done requesting photos for earthDate [" + earthDate + "].");
        }
        return result;
    }

    public boolean removeFromCache(String earthDate) {
        if(LOG.isDebugEnabled()) {
            LOG.debug("Removing date [" + earthDate + "] from cache.");
        }
        boolean result = FileUtils.deleteQuietly(new File("photo_cache/" + earthDate));
        if(LOG.isDebugEnabled()) {
            LOG.debug("Done removing date [" + earthDate + "] from cache.");
        }
        return result;
    }

    public boolean clearCache() {
        if(LOG.isDebugEnabled()) {
            LOG.debug("Clearing cache.");
        }
        boolean result = FileUtils.deleteQuietly(new File("photo_cache"));
        if(LOG.isDebugEnabled()) {
            LOG.debug("Done clearing cache.");
        }
        return result;
    }
}
