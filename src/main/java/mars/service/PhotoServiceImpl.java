package mars.service;

import mars.core.MarsApplicationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class PhotoServiceImpl implements PhotoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoServiceImpl.class);

    private final RoverService roverService;
    private final MarsApiService marsApiService;

    public PhotoServiceImpl(RoverService roverService, MarsApiService marsApiService) {
        this.roverService = roverService;
        this.marsApiService = marsApiService;
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
            LOGGER.debug("Downloading photos for earthDate [" + earthDate + "].");
        }
        List<String> roverNames = roverService.requestRoverNames();
        List<Photo> photos = marsApiService.requestPhotos(roverNames, earthDate);
        photos.parallelStream().forEach(photo -> {
            File photoFile = new File(
                    "photo_cache" +
                            "/" + earthDate +
                            "/" + StringUtils.substringAfterLast(photo.getImgSrc(), "/")
            );
            //noinspection ResultOfMethodCallIgnored
            photoFile.getParentFile().mkdirs();
            marsApiService.downloadPhoto(photo.getImgSrc(), photoFile);
        });
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Done downloading " + photos.size() + " photos for earthDate [" + earthDate + "].");
        }
        return photos;
    }

    @Override
    public List<String> getCachedDates() {
        String[] cachedDates = new File("photo_cache").list();
        return cachedDates != null ? Arrays.asList(cachedDates) : Collections.emptyList();
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
