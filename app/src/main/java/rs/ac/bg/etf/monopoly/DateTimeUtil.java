package rs.ac.bg.etf.monopoly;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {

    private static final SimpleDateFormat format=new SimpleDateFormat("dd-MM-yy");

    public static SimpleDateFormat getFormat(){
        return format;
    }

    public static String realMinutestoString(double real){
        int minutes=(int) real;
        int secs=(int)((real-minutes)*60);
        return minutes+":"+String.format("%02d",secs);

    }

    public static Date pack(int y, int m, int d){
        Calendar c=Calendar.getInstance();
        c.set(y,m,d);
        return c.getTime();
    }

    public static String getDateTime(long miliStart, long miliDur){
        LocalDateTime local=new Date(miliStart).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return local.format(DateTimeFormatter.ofPattern("dd-MM-yy"))+"\ttrajanje:\t"+miliDur/1000/60/60+"h"+miliDur/1000/60+ "min";
    }


}
