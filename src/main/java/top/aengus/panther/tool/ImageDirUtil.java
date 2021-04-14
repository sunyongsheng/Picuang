package top.aengus.panther.tool;

public class ImageDirUtil {

    public static final String NAME_APP = "app";
    public static final String NAME_COMMON = "common";
    public static final String NAME_POST = "post";
    public static final String NAME_TRAVEL = "travel";
    public static final String NAME_SCREENSHOTS = "screenshots";

    public static boolean isValidDir(String dir) {
        return (NAME_COMMON.equals(dir) || NAME_POST.equals(dir) || NAME_TRAVEL.equals(dir));
    }

    public static String concat(String parent, String current) {
        return parent + FileUtil.FILE_SEPARATOR + current;
    }
}
