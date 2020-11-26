package mars.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import mars.core.Constants;
import mars.core.MarsApplicationException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarsApiServiceImpl implements MarsApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarsApiServiceImpl.class);

    private static final int API_PAGE_SIZE = 25;

    private final ObjectMapper objectMapper;
    public MarsApiServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Rover> requestRovers() throws MarsApplicationException {
        LOGGER.debug("Requesting rovers.");
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(Constants.BASE_URL + "/rovers?" + Constants.API_KEY_PARAM);
            RoverResponse response = objectMapper.readValue(
                    client.execute(request).getEntity().getContent(),
                    RoverResponse.class
            );
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("Done requesting rovers.");
            }
            return response.getRovers();
        } catch (IOException e) {
            throw new MarsApplicationException("Failed to request rovers.", e);
        }
    }

    @Override
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
        return result;
    }

    @Override
    public void downloadPhoto(String imgSrc, File photoFile) {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Downloading photo for imgSrc [" + imgSrc + "].");
        }
        try(CloseableHttpClient client = HttpClients.createDefault();
            FileOutputStream outputStream = new FileOutputStream(photoFile)) {
            HttpGet request = new HttpGet(imgSrc);
            IOUtils.copy(
                    client.execute(request).getEntity().getContent(),
                    outputStream
            );
        } catch (IOException e) {
            LOGGER.error("Failed to download photo for imgSrc [" + imgSrc + "].", e);
        }
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Done downloading photo for imgSrc [" + imgSrc + "].");
        }
    }
}
