package mars.core;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;

public class Util {
    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);
    public static final String EARTH_DATE_FORMAT = "yyyy-MM-dd";

    public static final String[] SUPPORTED_DATE_FORMATS = new String[]{
            "MM/dd/yy",
            "MMM d, yyyy",
            "MMM-d-yyyy",
            EARTH_DATE_FORMAT
    };

    public static String parseEarthDate(String rawDate) {
        try {
            Date date = DateUtils.parseDateStrictly(rawDate, SUPPORTED_DATE_FORMATS);
            return DateFormatUtils.format(date, EARTH_DATE_FORMAT);
        } catch(ParseException e) {
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("[" + rawDate + "] is not a valid date.");
            }
            return null;
        }
    }
}
