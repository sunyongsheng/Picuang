package pers.adlered.picuang.tool;

import pers.adlered.picuang.log.Logger;
import pers.adlered.picuang.core.GlobalConfig;

import java.io.File;
import java.util.UUID;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/1
 */
public class FileUtil {

    public static final String FILE_SEPARATOR = File.separator;

    public static String ensurePrefix(String string) {
        if (string == null || string.isEmpty()) return FILE_SEPARATOR;
        return string.startsWith(FILE_SEPARATOR) ? string : FILE_SEPARATOR + string;
    }

    public static String ensureNoPrefix(String string) {
        if (string == null || string.isEmpty()) return "";
        return string.startsWith(FILE_SEPARATOR) ? string.substring(1) : string;
    }

    public static String ensureSuffix(String string) {
        if (string == null || string.isEmpty()) return FILE_SEPARATOR;
        return string.endsWith(FILE_SEPARATOR) ? string : string + FILE_SEPARATOR;
    }

    public static String ensureNoSuffix(String string) {
        if (string == null || string.isEmpty()) return "";
        return string.endsWith(FILE_SEPARATOR) ? string.substring(0, string.length() - 1) : string;
    }

    public static String getExtension(String filename) {
        if (filename == null) return ".jpg";
        String suffixName = filename.substring(filename.lastIndexOf("."));
        suffixName = suffixName.toLowerCase();
        return suffixName;
    }

    public static boolean isPic(String filename) {
        if (filename == null) return false;
        return (filename.endsWith(".jpeg")
                || filename.endsWith(".jpg")
                || filename.endsWith(".png")
                || filename.endsWith(".gif")
                || filename.endsWith(".svg")
                || filename.endsWith(".bmp")
                || filename.endsWith(".ico")
                || filename.endsWith(".tiff"));
    }

    public static String generateFilename(String originalName, boolean forceUUID) {
        int strategy = GlobalConfig.imgPathStrategy();
        if (forceUUID || strategy == 0) {
            String suffixName = getExtension(originalName);
            return UUID.randomUUID() + suffixName;
        } else if (strategy == 1) {
            return originalName;
        }
        return originalName;
    }

    public static void checkAndCreateDir(File dir) {
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                Logger.log("创建目录失败: " + dir);
            }
        }
    }

    public static String getFileStoreDir(String dir) {
        if (dir == null || dir.isEmpty()) {
            return GlobalConfig.savePath() + ensureSuffix(GlobalConfig.defaultSaveDir());
        }
        return GlobalConfig.savePath() + ensureSuffix(ensurePrefix(dir));
    }

    public static File generateFile(String dir, String originalFileName, boolean forceUUID) {
        String path = getFileStoreDir(dir);
        String fileName = generateFilename(originalFileName, forceUUID);
        return new File(path, fileName);
    }
}
