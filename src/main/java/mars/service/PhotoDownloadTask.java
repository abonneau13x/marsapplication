package mars.service;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoDownloadTask implements Runnable {
    private static final Log LOG = LogFactory.getLog(PhotoDownloadTask.class);

    private final String earthDate;
    private final String imgSrc;

    public PhotoDownloadTask(String earthDate, String imgSrc) {
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
            if(LOG.isDebugEnabled()) {
                LOG.debug("Downloading photo for date [" + earthDate + "], imgSrc [" + imgSrc + "].");
            }
            //noinspection ResultOfMethodCallIgnored
            photoFile.getParentFile().mkdirs();
            try(CloseableHttpClient client = HttpClients.createDefault();
                FileOutputStream outputStream = new FileOutputStream(photoFile)) {

                //noinspection ResultOfMethodCallIgnored
                photoFile.getParentFile().mkdirs();

                HttpGet request = new HttpGet(imgSrc);
                IOUtils.copy(
                        client.execute(request).getEntity().getContent(),
                        outputStream
                );
            } catch (IOException e) {
                LOG.error("Failed to download photo for date [" + earthDate + "], imgSrc [" + imgSrc + "].", e);
            }
            if(LOG.isDebugEnabled()) {
                LOG.debug("Done downloading photo for date [" + earthDate + "], imgSrc [" + imgSrc + "].");
            }
        } else {
            if(LOG.isDebugEnabled()) {
                LOG.debug("Not downloading photo for date [" + earthDate + "], imgSrc [" + imgSrc + "] because it is already cached.");
            }
        }
    }
}
