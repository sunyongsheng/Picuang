package pers.adlered.picuang.tool;

import pers.adlered.picuang.prop.Prop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * <h3>picuang</h3>
 * <p>工具箱</p>
 *
 * @author : https://github.com/AdlerED
 * @date : 2019-11-06 11:09
 **/
public class ToolBox {
    public static String getSuffixName(String filename) {
        if (filename == null) return ".jpg";
        String suffixName = filename.substring(filename.lastIndexOf("."));
        suffixName = suffixName.toLowerCase();
        return suffixName;
    }

    public static boolean isPic(String suffixName) {
        return (suffixName.equals(".jpeg")
                || suffixName.equals(".jpg")
                || suffixName.equals(".png")
                || suffixName.equals(".gif")
                || suffixName.equals(".svg")
                || suffixName.equals(".bmp")
                || suffixName.equals(".ico")
                || suffixName.equals(".tiff"));
    }

    public static String getPicStoreDir(String dir) {
        return Prop.savePath() + dir + File.separator;
    }

    public static String getPicFilename(String originalName, boolean forceUUID) {
        int strategy = Prop.imgPathStrategy();
        if (forceUUID || strategy == 0) {
            String suffixName = getSuffixName(originalName);
            return UUID.randomUUID() + suffixName;
        } else if (strategy == 1) {
            return  originalName;
        }
        return originalName;
    }

    public static File generatePicFile(String dir, String originalFileName, boolean forceUUID) {
        String path = getPicStoreDir(dir);
        String fileName = getPicFilename(originalFileName, forceUUID);
        return new File(path + fileName);
    }

    public static String getDirByTime() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm/");
        return simpleDateFormat.format(date);
    }

    public static String getINIDir() {
        return new File(Prop.CONFIG_FILENAME).getAbsolutePath();
    }
}
