package mars.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import mars.core.Constants;
import mars.core.MarsApplicationException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoverServiceImpl implements RoverService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoverServiceImpl.class);

    private final ObjectMapper objectMapper;
    public RoverServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<String> requestRoverNames() throws MarsApplicationException {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Requesting rover names.");
        }
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(Constants.BASE_URL + "/rovers?" + Constants.API_KEY_PARAM);
            RoverResponse response = objectMapper.readValue(
                    client.execute(request).getEntity().getContent(),
                    RoverResponse.class
            );
            List<String> result = new ArrayList<>(response.getRovers().size());
            for (Rover rover : response.getRovers()) {
                result.add(rover.getName());
            }
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("Done requesting rover names.");
            }
            return result;
        } catch (IOException e) {
            throw new MarsApplicationException("Failed to request rover names.", e);
        }
    }
}
