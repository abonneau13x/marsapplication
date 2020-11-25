package mars.controller;

import mars.core.MarsApplicationException;
import mars.core.Util;
import mars.service.Photo;
import mars.service.PhotoService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/v1")
public class PhotoController {
    private final PhotoService photoService;
    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @RequestMapping("/cache")
    public List<Photo> cache(@RequestParam("date") String rawDate) throws MarsApplicationException {
        String earthDate = Util.parseEarthDate(rawDate);
        if(earthDate == null) {
            return Collections.emptyList();
        }
        return photoService.cachePhotos(earthDate);
    }

    @RequestMapping("/cachedDates")
    public List<String> cachedDates() {
        return photoService.getCachedDates();
    }

    @RequestMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam("date") String rawDate, @RequestParam("fileName") String fileName) throws IOException, MarsApplicationException {
        String earthDate = Util.parseEarthDate(rawDate);
        if(earthDate == null) {
            throw new MarsApplicationException("[" + rawDate + "] is not a valid date.");
        }
        File photoFile = new File(
                "photo_cache" +
                        "/" + earthDate +
                        "/" + fileName
        );
        if(!photoFile.exists()) {
            throw new MarsApplicationException("File [" + fileName + "] does not exist in cache for date [" + rawDate + "].");
        }
        Resource resource = new UrlResource(photoFile.toURI());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
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
