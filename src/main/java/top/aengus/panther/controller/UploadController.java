package top.aengus.panther.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.access.HttpOrHttpsAccess;
import top.aengus.panther.core.GlobalConfig;
import top.aengus.panther.core.Response;
import top.aengus.panther.model.ImageModel;
import top.aengus.panther.service.ImageService;
import top.aengus.panther.tool.FileUtil;
import top.aengus.panther.tool.IPUtil;
import top.aengus.panther.tool.DoubleKeys;
import top.aengus.limiter.main.SimpleCurrentLimiter;

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

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    private static final String URL_SEPARATOR = "/";

    public static SimpleCurrentLimiter uploadLimiter = new SimpleCurrentLimiter(1, 1);
    public static SimpleCurrentLimiter cloneLimiter = new SimpleCurrentLimiter(3, 1);

    private final ImageService imageService;

    @Autowired
    public UploadController(ImageService imageService) {
        this.imageService = imageService;
    }

    @RequestMapping("/upload")
    @ResponseBody
    public Response<String> upload(@RequestParam MultipartFile file, @RequestParam String dir,
                                   HttpServletRequest request, HttpSession session) {
        synchronized (this) {
            String addr = IPUtil.getIpAddr(request).replaceAll("\\.", "/").replaceAll(":", "/");
            boolean allowed = uploadLimiter.access(addr);
            Response<String> response = new Response<>();
            if (GlobalConfig.adminOnly()) {
                logger.debug("AdminOnly mode is on! Checking user's permission...");
                if (!logged(session)) {
                    logger.error("User not logged! Uploading terminated.");
                    response.setCode(401);
                    response.setMsg("管理员禁止了普通用户上传文件！");
                    return response;
                }
                logger.info("Admin is uploading...");
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
                response.setCode(406);
                return response;
            }
            //是否是图片格式
            String originalFilename = file.getOriginalFilename();
            if (FileUtil.isPic(originalFilename)) {
                File dest = FileUtil.generateFile(dir, originalFilename, false);
                response.setData(originalFilename);
                String saveName = dest.getName();
                logger.debug("Saving into {}", dest.getAbsolutePath());
                FileUtil.checkAndCreateDir(dest.getParentFile());
                try {
                    file.transferTo(dest);
                    imageService.insertImage(new ImageModel(originalFilename, saveName, dest.getAbsolutePath(), ImageModel.STATUS_NORMAL));

                    response.setCode(200);
                    response.setMsg(getCorrectDirPath(dir) + saveName);
                    GlobalConfig.imageUploadedCount(GlobalConfig.imageUploadedCount() + 1);
                    return response;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                response.setCode(500);
                response.setMsg("不是jpg/jpeg/png/svg/gif图片！");
                return response;
            }
            response.setCode(500);
            response.setMsg("未知错误。");
            return response;
        }
    }

    // TODO 使用imageService添加记录
    @RequestMapping("/clone")
    @ResponseBody
    public Response<String> clone(String url, String dir, HttpServletRequest request, HttpSession session) {
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
            Response<String> response = new Response<>();
            // 基于IP地址的重复克隆检测限制
            if (!DoubleKeys.check(addr, url)) {
                response.setCode(401);
                response.setMsg("请不要重复克隆同一张图片。你可以在右上方的\"历史\"选项找到你克隆过的图片！");
                return response;
            }
            if (GlobalConfig.adminOnly()) {
                logger.debug("AdminOnly mode is on! Checking user's permission...");
                if (!logged(session)) {
                    logger.error("User not logged! Uploading terminated.");
                    response.setCode(401);
                    response.setMsg("管理员禁止了普通用户上传文件！");
                    return response;
                }
                logger.debug("Admin is uploading...");
            }
            String regex = "(http(s)?://)?(localhost|(127|192|172|10)\\.).*";
            Matcher matcher = Pattern.compile(regex).matcher(url);
            if (matcher.matches()) {
                response.setCode(401);
                response.setMsg("Anti-SSRF系统检测到您输入了内网地址，请检查！");
                return response;
            }
            File dest = null;
            try {
                String suffixName = FileUtil.getExtension(url);
                logger.info("SuffixName: " + suffixName);
                if (FileUtil.isPic(suffixName)) {
                    dest = FileUtil.generateFile(suffixName, dir, true);
                } else {
                    dest = FileUtil.generateFile(".png", dir, true);
                }
                logger.debug("Saving into " + dest.getAbsolutePath());
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
                    response.setData("From " + m.group());
                    response.setCode(200);
                    response.setMsg(getCorrectDirPath(dir) + dest.getName());
                    GlobalConfig.imageUploadedCount(GlobalConfig.imageUploadedCount() + 1);
                } else {
                    response.setData("正则匹配失败");
                    response.setCode(400);
                }
                return response;
            } catch (Exception e) {
                // 出错时删除建立的文件，以防止无效图片过多产生
                if (dest != null) {
                    logger.debug("An exception has caught, deleting picture cache...");
                    dest.delete();
                }
                response.setCode(500);
                response.setMsg(e.getClass().toGenericString().replaceAll("public class ", ""));
                return response;
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

    /**
     *
     * @return 返回格式 /dir/
     */
    String getCorrectDirPath(String dir) {
        if (GlobalConfig.customSavePath) {
            if (dir == null || dir.isEmpty()) {
                return GlobalConfig.defaultSaveDir() + URL_SEPARATOR;
            }
            return URL_SEPARATOR + dir + URL_SEPARATOR;
        }
        return "/uploadImages/";
    }
}