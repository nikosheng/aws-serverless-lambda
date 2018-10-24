package lambda.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/7/2018
 */
public class Utils {
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * Convert the UTC time to UTC+8 Timezone
     * @param date
     * @return
     */
    public static String UTCToCST(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("Hongkong"));
        return dateFormat.format(calendar.getTime());
    }
}
