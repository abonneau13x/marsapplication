package mars.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;

@Configurable
public class PhotoDownloadTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoDownloadTask.class);

    private final MarsApiService marsApiService;

    private final String earthDate;
    private final String imgSrc;

    public PhotoDownloadTask(MarsApiService marsApiService, String earthDate, String imgSrc) {
        this.marsApiService = marsApiService;
        this.earthDate = earthDate;
        this.imgSrc = imgSrc;
    }

    @Override
    public void run() {
        File photoFile = new File(
                "photo_cache" +
                        "/" + earthDate +
                        "/" + StringUtils.substringAfterLast(imgSrc, "/")
        );
        if (!photoFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            photoFile.getParentFile().mkdirs();
            marsApiService.downloadPhoto(imgSrc, photoFile);
        } else {
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("Not downloading photo for date [" + earthDate + "], imgSrc [" + imgSrc + "] because it is already cached.");
            }
        }
    }
}
