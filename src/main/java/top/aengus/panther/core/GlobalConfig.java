package top.aengus.panther.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import top.aengus.panther.controller.UploadController;
import top.aengus.panther.tool.FileUtil;
import top.aengus.limiter.main.SimpleCurrentLimiter;

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
public class GlobalConfig {

    private static final Logger logger = LoggerFactory.getLogger(GlobalConfig.class);

    public static final String CONFIG_FILENAME = "config.ini";

    public static final String CONFIG_KEY_IMAGE_UPLOADED_COUNT = "imageUploadedCount";
    public static final String CONFIG_KEY_VERSION = "version";
    public static final String CONFIG_KEY_PASSWORD = "password";
    public static final String CONFIG_KEY_ADMIN_ONLY = "adminOnly";
    public static final String CONFIG_KEY_UPLOAD_LIMIT = "uploadLimit";
    public static final String CONFIG_KEY_CLONE_LIMIT = "cloneLimit";
    public static final String CONFIG_KEY_SAVE_PATH = "savePath";
    public static final String CONFIG_KEY_IMG_NAME_STRATEGY = "imgNameStrategy";
    public static final String CONFIG_KEY_DEFAULT_SAVE_DIR = "defaultSaveDir";

    // 版本号
    private static final String version = "V2.4";

    private static final Properties properties = new Properties();

    public static volatile boolean customSavePath = false;

    static {
        init();
        logger.debug("Properties loaded.");
        reload();
    }

    public static void del() {
        new File(CONFIG_FILENAME).delete();
    }

    public static void init() {
        try {
            properties.load(new BufferedInputStream(new FileInputStream(CONFIG_FILENAME)));
            savePath();
        } catch (FileNotFoundException e) {
            logger.debug("Generating new profile...");

            properties.put(CONFIG_KEY_IMAGE_UPLOADED_COUNT, "0");
            properties.put(CONFIG_KEY_VERSION, version);
            properties.put(CONFIG_KEY_PASSWORD, "");
            properties.put(CONFIG_KEY_ADMIN_ONLY, "off");
            properties.put(CONFIG_KEY_UPLOAD_LIMIT, "1:1");
            properties.put(CONFIG_KEY_CLONE_LIMIT, "3:1");
            properties.put(CONFIG_KEY_SAVE_PATH, "");
            properties.put(CONFIG_KEY_IMG_NAME_STRATEGY, "0");
            properties.put(CONFIG_KEY_DEFAULT_SAVE_DIR, "/");

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
            logger.debug("[Prop] Set key '" + key + "' to value '" + value + "'");
            PrintWriter printWriter = new PrintWriter(new FileWriter(CONFIG_FILENAME), true);
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
        logger.debug("Reloading profile...");
        // Reload properties
        try {
            properties.load(new BufferedInputStream(new FileInputStream(CONFIG_FILENAME)));
        } catch (Exception ignored) {
        }
        // Upload limit
        try {
            String uploadLimitMaster = get(CONFIG_KEY_UPLOAD_LIMIT);
            if (uploadLimitMaster.contains(":")) {
                int uploadLimitTime = Integer.parseInt(uploadLimitMaster.split(":")[0]);
                int uploadLimitTimes = Integer.parseInt(uploadLimitMaster.split(":")[1]);
                UploadController.uploadLimiter = new SimpleCurrentLimiter(uploadLimitTime, uploadLimitTimes);
                logger.debug("Upload limit custom setting loaded ( {} times in {} second ).", uploadLimitTimes, uploadLimitTime);
            }
        } catch (Exception ignored) {
        }
        // Clone limit
        try {
            String cloneLimitMaster = get(CONFIG_KEY_CLONE_LIMIT);
            if (cloneLimitMaster.contains(":")) {
                int cloneLimitTime = Integer.parseInt(cloneLimitMaster.split(":")[0]);
                int cloneLimitTimes = Integer.parseInt(cloneLimitMaster.split(":")[1]);
                UploadController.cloneLimiter = new SimpleCurrentLimiter(cloneLimitTime, cloneLimitTimes);
                logger.debug("Clone limit custom setting loaded ( {} times in {} second ).", cloneLimitTimes, cloneLimitTime);
            }
        } catch (Exception ignored) {
        }
    }

    public static void renew() {
        del();
        init();
    }

    public static String getConfigPath() {
        return new File(CONFIG_FILENAME).getAbsolutePath();
    }

    public static boolean adminOnly() {
        return get(CONFIG_KEY_ADMIN_ONLY).equals("on");
    }

    public static int imageUploadedCount() {
        return Integer.parseInt(get(CONFIG_KEY_IMAGE_UPLOADED_COUNT));
    }

    public static void imageUploadedCount(int value) {
        set(CONFIG_KEY_IMAGE_UPLOADED_COUNT, String.valueOf(value));
    }

    public static String password() {
        return get(CONFIG_KEY_PASSWORD);
    }

    /**
     * @return 返回格式 /savePath
     */
    public static String savePath() {
        String config = get(CONFIG_KEY_SAVE_PATH);
        customSavePath = true;
        if (config == null || config.isEmpty()) {
            customSavePath = false;
            config = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "static/uploadImages";
        }
        return FileUtil.ensureNoSuffix(config);
    }

    public static int imgPathStrategy() {
        try {
            return Integer.parseInt(get(CONFIG_KEY_IMG_NAME_STRATEGY));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @return 返回格式 /dir
     */
    public static String defaultSaveDir() {
        return FileUtil.ensurePrefix(FileUtil.ensureNoSuffix(get(CONFIG_KEY_DEFAULT_SAVE_DIR)));
    }
}
