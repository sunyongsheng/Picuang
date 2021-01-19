package pers.adlered.picuang.controller.admin.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pers.adlered.picuang.core.GlobalConfig;
import pers.adlered.picuang.result.Result;
import pers.adlered.picuang.tool.DownloadUtil;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;

/**
 * <h3>picuang</h3>
 * <p>配置API</p>
 *
 * @author : https://github.com/AdlerED
 * @date : 2019-11-07 17:31
 **/
@Controller
public class ConfigActionController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigActionController.class);

    /**
     * 检查管理员是否已登录
     *
     */
    public boolean haveLogin(HttpSession session) {
        boolean logged = false;
        try {
            logged = Boolean.parseBoolean(session.getAttribute("admin").toString());
        } catch (NullPointerException ignored) {
        }
        return logged;
    }

    @RequestMapping("/api/admin/getConf")
    @ResponseBody
    public String getConf(HttpSession session, String conf) {
        if (haveLogin(session) || conf.equals(GlobalConfig.CONFIG_KEY_ADMIN_ONLY)) {
            String result = GlobalConfig.get(conf);
            if (result != null) {
                return result;
            } else {
                return "找不到配置！如果你更新过Picuang，请备份并删除当前的config.ini文件（位于 " + GlobalConfig.getConfigPath() + " ），然后重启服务端或点击\"生成新配置文件\"按钮，使Picuang重新生成新的配置文件。";
            }
        } else {
            return "Permission denied";
        }
    }

    @RequestMapping("/api/admin/setConf")
    @ResponseBody
    public Result<String> setConf(HttpSession session, String conf, String value) {
        Result<String> result = new Result<>();
        if (haveLogin(session)) {
            GlobalConfig.set(conf, value);
            result.setCode(200);
        } else {
            result.setCode(500);
        }
        return result;
    }

    @RequestMapping("/api/admin/export")
    @ResponseBody
    public void exportConfig(HttpServletResponse response, HttpSession session) {
        if (haveLogin(session)) {
            String fileName = GlobalConfig.CONFIG_FILENAME;
            DownloadUtil.downloadFile(response, fileName);
        }
    }

    @RequestMapping("/api/admin/import")
    @ResponseBody
    public Result<String> importConfig(@PathVariable MultipartFile file, HttpSession session) {
        Result<String> result = new Result<>();
        try {
            String filename = file.getOriginalFilename();
            // 如果已登录 && 文件不为空 && 是ini文件
            if (haveLogin(session) && (!file.isEmpty()) && filename.matches(".*(\\.ini)$")) {
                File config = new File(GlobalConfig.CONFIG_FILENAME);
                config.renameTo(new File(GlobalConfig.CONFIG_FILENAME + ".backup"));
                File newConfig = new File(config.getAbsolutePath());
                file.transferTo(newConfig);
                logger.info(newConfig.getPath());
                GlobalConfig.reload();
                result.setCode(200);
            } else {
                result.setCode(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(500);
        }
        return result;
    }

    /**
     * 重载功能
     * 不验证管理员是否已经登录，因为需要重载初始化后的密码
     *
     * @return
     */
    @RequestMapping("/api/admin/reload")
    @ResponseBody
    public Result<String> reloadConfig() {
        Result<String> result = new Result<>();
        GlobalConfig.reload();
        result.setCode(200);
        return result;
    }

    @RequestMapping("/api/admin/renew")
    @ResponseBody
    public Result<String> renewConfig(HttpSession session) {
        Result<String> result = new Result<>();
        if (haveLogin(session)) {
            GlobalConfig.renew();
            result.setCode(200);
        } else {
            result.setCode(500);
        }
        return result;
    }
}
