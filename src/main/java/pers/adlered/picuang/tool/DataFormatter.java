package pers.adlered.picuang.tool;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/1
 */
public class DataFormatter {

    private static final SimpleDateFormat SEPARATOR_FORMATTER = new SimpleDateFormat("yyyy/MM/dd/HH/mm/");

    public static String separatorFormat(Date date) {
        return SEPARATOR_FORMATTER.format(date);
    }
}
