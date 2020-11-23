package mars.controller;

import mars.core.MarsApplicationException;
import mars.core.Util;
import mars.service.PhotoService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class PhotoController {

    private static final Log LOG = LogFactory.getLog(PhotoController.class);

    private final PhotoService photoService;
    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @RequestMapping("/cache")
    public boolean cache(@RequestParam("date") String rawDate) {
        String earthDate = Util.parseEarthDate(rawDate);
        if(earthDate == null) {
            return false;
        }
        try {
            photoService.cachePhotos(earthDate);
        } catch (MarsApplicationException e) {
            LOG.error("Failed to cache photos for date [" + rawDate + "].", e);
            return false;
        }
        return true;
    }

    @RequestMapping("/removeFromCache")
    public boolean removeFromCache(@RequestParam("date") String rawDate) {
        String earthDate = Util.parseEarthDate(rawDate);
        if(earthDate == null) {
            return false;
        }
        return photoService.removeFromCache(earthDate);
    }

    @RequestMapping("/clearCache")
    public boolean clearCache() {
        return photoService.clearCache();
    }
}
