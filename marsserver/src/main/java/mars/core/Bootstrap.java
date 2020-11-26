package mars.core;

import mars.service.PhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Component
public class Bootstrap implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

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
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("Processing dates from [" + arg + "].");
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(new File(arg)))) {
                String rawDate;
                while((rawDate = reader.readLine()) != null) {
                    String earthDate = Util.parseEarthDate(rawDate);
                    if(earthDate != null) {
                        try {
                            photoService.cachePhotos(earthDate);
                        } catch(MarsApplicationException e) {
                            LOGGER.error("Failed to process photos for file [" + arg + "], date [" + rawDate + "].", e);
                        }
                    }
                }
            } catch(IOException e) {
                LOGGER.error("Failed to process photos for file [" + arg + "].", e);
            }
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("Done processing dates from [" + arg + "].");
            }
        }
    }
}
