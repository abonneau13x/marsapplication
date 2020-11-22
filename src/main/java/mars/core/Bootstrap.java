package mars.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import mars.service.PhotoService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@Component
public class Bootstrap implements CommandLineRunner {
    private static final Log LOG = LogFactory.getLog(Bootstrap.class);

    private final PhotoService photoService;

    public Bootstrap(PhotoService photoService) {
        this.photoService = photoService;
    }

    @Override
    public void run(String... args) {
        if(args.length == 0) {
            return;
        }

        for (String arg : args) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("Processing dates from [" + arg + "].");
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(new File(arg)))) {
                String rawDate;
                while((rawDate = reader.readLine()) != null) {
                    String earthDate = Util.parseEarthDate(rawDate);
                    if(earthDate != null) {
                        try {
                            photoService.processPhotos(earthDate);
                        } catch(Exception e) {
                            LOG.error("Failed to process photos for file [" + arg + "], date [" + rawDate + "].", e);
                        }
                    }
                }
            } catch(Exception e) {
                LOG.error("Failed to process photos for file [" + arg + "].", e);
            }
            if(LOG.isDebugEnabled()) {
                LOG.debug("Done processing dates from [" + arg + "].");
            }
        }
    }
}
