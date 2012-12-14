package ca.n4dev.redshift.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PeriodUtils {

	public enum Period {
    	TODAY, YESTERDAY, LAST7DAYS, THISMONTH, INFINITY;
    }
	
	public static String getDateStringFrom(Date d, Period period) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmiss", Locale.US);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(d.getTime());
		c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
		
        switch (period) {
		case TODAY: break;
		case YESTERDAY: 
			c.set(Calendar.DATE, -1);
			break;
		case LAST7DAYS:
			c.set(Calendar.DATE, -7);
			break;
		case THISMONTH:
			c.set(Calendar.DAY_OF_MONTH, 1);
		case INFINITY:
			c.setTimeInMillis(1L);
        }
		
        return formater.format(c.getTime());
	}
}
