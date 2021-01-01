package pers.adlered.picuang.prop;

import org.springframework.stereotype.Component;
import pers.adlered.picuang.controller.UploadController;
import pers.adlered.picuang.log.Logger;
import pers.adlered.simplecurrentlimiter.main.SimpleCurrentLimiter;

import java.io.*;
import java.util.Properties;
import java.util.Set;

/**
 * <h3>picuang</h3>
 * <p>自动配置文件</p>
 *
 * @author : https://github.com/AdlerED
 * @date : 2019-11-06 21:29
 **/
@Component
public class Prop {

    public static final String CONFIG_FILENAME = "config.ini";
    public static final String CONFIG_KEY_IMAGE_UPLOADED_COUNT = "imageUploadedCount";
    public static final String CONFIG_KEY_VERSION = "version";
    public static final String CONFIG_KEY_PASSWORD = "password";
    public static final String CONFIG_KEY_ADMIN_ONLY = "adminOnly";
    public static final String CONFIG_KEY_UPLOAD_LIMIT = "uploadLimit";
    public static final String CONFIG_KEY_CLONE_LIMIT = "cloneLimit";

    // 版本号
    private static final String version = "V2.4";

    private static final Properties properties = new Properties();

    static {
        put();
        Logger.log("Properties loaded.");
        reload();
    }

    public static void del() {
        File file = new File(CONFIG_FILENAME);
        file.delete();
    }

    public static void put() {
        try {
            properties.load(new BufferedInputStream(new FileInputStream(CONFIG_FILENAME)));
        } catch (FileNotFoundException e) {
            Logger.log("Generating new profile...");
            properties.put(CONFIG_KEY_IMAGE_UPLOADED_COUNT, "0");
            properties.put(CONFIG_KEY_VERSION, version);
            properties.put(CONFIG_KEY_PASSWORD, "");
            properties.put(CONFIG_KEY_ADMIN_ONLY, "off");
            properties.put(CONFIG_KEY_UPLOAD_LIMIT, "1:1");
            properties.put(CONFIG_KEY_CLONE_LIMIT, "3:1");
            try {
                properties.store(new BufferedOutputStream(new FileOutputStream(CONFIG_FILENAME)), "Save Configs File.");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException IOE) {
            IOE.printStackTrace();
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static void set(String key, String value) {
        try {
            properties.setProperty(key, value);
            Logger.log("[Prop] Set key '" + key + "' to value '" + value + "'");
            PrintWriter printWriter = new PrintWriter(new FileWriter("config.ini"), true);
            Set<Object> set = properties.keySet();
            for (Object object : set) {
                String k = (String) object;
                String v = properties.getProperty(k);
                printWriter.println(k + "=" + v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!key.equals(CONFIG_KEY_IMAGE_UPLOADED_COUNT)) {
            reload();
        }
    }

    public static String getVersion() {
        return version;
    }

    public static void reload() {
        Logger.log("Reloading profile...");
        // Reload properties
        try {
            properties.load(new BufferedInputStream(new FileInputStream(CONFIG_FILENAME)));
        } catch (Exception ignored) {}
        // Upload limit
        try {
            String uploadLimitMaster = get(CONFIG_KEY_UPLOAD_LIMIT);
            if (uploadLimitMaster.contains(":")) {
                int uploadLimitTime = Integer.parseInt(uploadLimitMaster.split(":")[0]);
                int uploadLimitTimes = Integer.parseInt(uploadLimitMaster.split(":")[1]);
                UploadController.uploadLimiter = new SimpleCurrentLimiter(uploadLimitTime, uploadLimitTimes);
                Logger.log("Upload limit custom setting loaded (" + uploadLimitTimes + " times in " + uploadLimitTime + " second) .");
            }
        } catch (Exception ignored) {}
        // Clone limit
        try {
            String cloneLimitMaster = get(CONFIG_KEY_CLONE_LIMIT);
            if (cloneLimitMaster.contains(":")) {
                int cloneLimitTime = Integer.parseInt(cloneLimitMaster.split(":")[0]);
                int cloneLimitTimes = Integer.parseInt(cloneLimitMaster.split(":")[1]);
                UploadController.cloneLimiter = new SimpleCurrentLimiter(cloneLimitTime, cloneLimitTimes);
                Logger.log("Clone limit custom setting loaded (" + cloneLimitTimes + " times in " + cloneLimitTime + " second) .");
            }
        } catch (Exception ignored) {}
    }

    public static void renew() {
        del();
        put();
    }

    public static boolean adminOnly() {
        return Prop.get(CONFIG_KEY_ADMIN_ONLY).equals("on");
    }

    public static int imageUploadedCount() {
        return Integer.parseInt(Prop.get(CONFIG_KEY_IMAGE_UPLOADED_COUNT));
    }

    public static void imageUploadedCount(int value) {
        Prop.set(CONFIG_KEY_IMAGE_UPLOADED_COUNT, String.valueOf(value));
    }

    public static String password() {
        return Prop.get(CONFIG_KEY_PASSWORD);
    }
}
