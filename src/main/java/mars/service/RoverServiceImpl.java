package mars.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mars.core.MarsApplicationException;
import mars.core.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoverServiceImpl implements RoverService {
    private static final Log LOG = LogFactory.getLog(RoverServiceImpl.class);

    private final ObjectMapper objectMapper;
    public RoverServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<String> requestRoverNames() throws MarsApplicationException {
        if(LOG.isDebugEnabled()) {
            LOG.debug("Requesting rover names.");
        }
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(Constants.BASE_URL + "/rovers?" + Constants.API_KEY_PARAM);
            ObjectNode response = objectMapper.readValue(
                    client.execute(request).getEntity().getContent(),
                    ObjectNode.class
            );

            JsonNode rovers = response.get("rovers");
            List<String> result = new ArrayList<>(rovers.size());
            for (JsonNode jsonNode : rovers) {
                result.add(jsonNode.get("name").asText());
            }
            if(LOG.isDebugEnabled()) {
                LOG.debug("Done requesting rover names.");
            }
            return result;
        } catch (IOException e) {
            throw new MarsApplicationException("Failed to request rover names.", e);
        }
    }
}
