package mars.core;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.util.Date;

public class Util {
    private static final Log LOG = LogFactory.getLog(Util.class);
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
            if(LOG.isDebugEnabled()) {
                LOG.debug("[" + rawDate + "] is not a valid date.");
            }
            return null;
        }
    }
}
