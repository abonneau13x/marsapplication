package mars.service;

import mars.core.MarsApplicationException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class PhotoServiceImpl implements PhotoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoServiceImpl.class);

    private final RoverService roverService;
    private final MarsApiService marsApiService;
    private final TaskExecutor taskExecutor;

    public PhotoServiceImpl(RoverService roverService, MarsApiService marsApiService, TaskExecutor taskExecutor) {
        this.roverService = roverService;
        this.marsApiService = marsApiService;
        this.taskExecutor = taskExecutor;
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
        List<Photo> photos = marsApiService.requestPhotos(roverNames, earthDate);
        for (Photo photo : photos) {
            // Download photos asynchronously.
            taskExecutor.execute(
                    new PhotoDownloadTask(
                            marsApiService,
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
