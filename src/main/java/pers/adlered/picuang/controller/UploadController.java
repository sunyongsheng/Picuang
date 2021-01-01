package pers.adlered.picuang.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import pers.adlered.picuang.access.HttpOrHttpsAccess;
import pers.adlered.picuang.log.Logger;
import pers.adlered.picuang.prop.Prop;
import pers.adlered.picuang.result.Result;
import pers.adlered.picuang.tool.FileUtil;
import pers.adlered.picuang.tool.IPUtil;
import pers.adlered.picuang.tool.double_keys.main.DoubleKeys;
import pers.adlered.simplecurrentlimiter.main.SimpleCurrentLimiter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class UploadController {

    private static final String URL_SEPARATOR = "/";

    public static SimpleCurrentLimiter uploadLimiter = new SimpleCurrentLimiter(1, 1);
    public static SimpleCurrentLimiter cloneLimiter = new SimpleCurrentLimiter(3, 1);

    @RequestMapping("/upload")
    @ResponseBody
    public Result<String> upload(@RequestParam MultipartFile file, @RequestParam String dir,
                                 HttpServletRequest request, HttpSession session) {
        synchronized (this) {
            String addr = IPUtil.getIpAddr(request).replaceAll("\\.", "/").replaceAll(":", "/");
            boolean allowed = uploadLimiter.access(addr);
            Result<String> result = new Result<>();
            if (Prop.adminOnly()) {
                Logger.log("AdminOnly mode is on! Checking user's permission...");
                if (!logged(session)) {
                    Logger.log("User not logged! Uploading terminated.");
                    result.setCode(401);
                    result.setMsg("管理员禁止了普通用户上传文件！");
                    return result;
                }
                Logger.log("Admin is uploading...");
            }
            try {
                while (!allowed) {
                    allowed = uploadLimiter.access(addr);
                    System.out.print(".");
                    Thread.sleep(100);
                }
            } catch (InterruptedException ignored) {}
            //是否上传了文件
            if (file.isEmpty()) {
                result.setCode(406);
                return result;
            }
            //是否是图片格式
            String filename = file.getOriginalFilename();
            if (FileUtil.isPic(filename)) {
                File dest = FileUtil.generateFile(dir, filename, false);
                result.setData(filename);
                filename = dest.getName();
                Logger.log("Saving into " + dest.getAbsolutePath());
                FileUtil.checkAndCreateDir(dest.getParentFile());
                try {
                    file.transferTo(dest);
                    String url = getCorrectDirPath(dir) + filename;
                    result.setCode(200);
                    result.setMsg(url);
                    Prop.imageUploadedCount(Prop.imageUploadedCount() + 1);
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                result.setCode(500);
                result.setMsg("不是jpg/jpeg/png/svg/gif图片！");
                return result;
            }
            result.setCode(500);
            result.setMsg("未知错误。");
            return result;
        }
    }

    @RequestMapping("/clone")
    @ResponseBody
    public Result<String> clone(String url, String dir, HttpServletRequest request, HttpSession session) {
        synchronized (this) {
            String addr = IPUtil.getIpAddr(request).replaceAll("\\.", "/").replaceAll(":", "/");
            // IP地址访问频率限制
            boolean allowed = cloneLimiter.access(addr);
            try {
                while (!allowed) {
                    allowed = cloneLimiter.access(addr);
                    System.out.print(".");
                    Thread.sleep(100);
                }
            } catch (InterruptedException ignored) {}
            Result<String> result = new Result<>();
            // 基于IP地址的重复克隆检测限制
            if (!DoubleKeys.check(addr, url)) {
                result.setCode(401);
                result.setMsg("请不要重复克隆同一张图片。你可以在右上方的\"历史\"选项找到你克隆过的图片！");
                return result;
            }
            if (Prop.adminOnly()) {
                Logger.log("AdminOnly mode is on! Checking user's permission...");
                if (!logged(session)) {
                    Logger.log("User not logged! Uploading terminated.");
                    result.setCode(401);
                    result.setMsg("管理员禁止了普通用户上传文件！");
                    return result;
                }
                Logger.log("Admin is uploading...");
            }
            String regex = "(http(s)?://)?(localhost|(127|192|172|10)\\.).*";
            Matcher matcher = Pattern.compile(regex).matcher(url);
            if (matcher.matches()) {
                result.setCode(401);
                result.setMsg("Anti-SSRF系统检测到您输入了内网地址，请检查！");
                return result;
            }
            File dest = null;
            try {
                String suffixName = FileUtil.getExtension(url);
                Logger.log("SuffixName: " + suffixName);
                if (FileUtil.isPic(suffixName)) {
                    dest = FileUtil.generateFile(suffixName, dir, true);
                } else {
                    dest = FileUtil.generateFile(".png", dir, true);
                }
                Logger.log("Saving into " + dest.getAbsolutePath());
                FileUtil.checkAndCreateDir(dest.getParentFile());
                FileOutputStream fileOutputStream = new FileOutputStream(dest);
                BufferedInputStream bufferedInputStream = HttpOrHttpsAccess.post(url,
                        "",
                        null);
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = bufferedInputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, len);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                bufferedInputStream.close();
                Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(url);
                if (m.find()) {
                    result.setData("From " + m.group());
                    result.setCode(200);
                    result.setMsg(getCorrectDirPath(dir) + dest.getName());
                    Prop.imageUploadedCount(Prop.imageUploadedCount() + 1);
                } else {
                    result.setData("正则匹配失败");
                    result.setCode(400);
                }
                return result;
            } catch (Exception e) {
                // 出错时删除建立的文件，以防止无效图片过多产生
                if (dest != null) {
                    Logger.log("An exception has caught, deleting picture cache...");
                    dest.delete();
                }
                result.setCode(500);
                result.setMsg(e.getClass().toGenericString().replaceAll("public class ", ""));
                return result;
            }
        }
    }

    /**
     * 检查管理员是否已登录
     */
    public boolean logged(HttpSession session) {
        try {
            return Boolean.parseBoolean(session.getAttribute("admin").toString());
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    String getCorrectDirPath(String dir) {
        if (Prop.customSavePath) {
            if (dir == null || dir.isEmpty()) {
                return URL_SEPARATOR;
            }
            return URL_SEPARATOR + dir + URL_SEPARATOR;
        }
        return "/uploadImages/";
    }
}