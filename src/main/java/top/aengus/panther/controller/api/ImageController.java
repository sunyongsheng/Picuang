package top.aengus.panther.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.core.Constants;
import top.aengus.panther.core.Response;
import top.aengus.panther.enums.NamingRule;
import top.aengus.panther.model.AppInfo;
import top.aengus.panther.model.ImageDTO;
import top.aengus.panther.service.ImageService;
import top.aengus.panther.tool.ImageDirUtil;
import top.aengus.panther.tool.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
public class ImageController extends ApiV1Controller {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * POST /api/v1/image
     *
     * @param rule 命令规则
     * @param dirPath 保存目录，
     * @param file 图片文件
     */
    @PostMapping("/image")
    public Response<ImageDTO> upload(HttpServletRequest request,
                                     @RequestParam(value = "name_rule", defaultValue = "2") Integer rule,
                                     @RequestParam(value = "dir", required = false) String dirPath,
                                     @RequestParam("file") MultipartFile file) {
        Response<ImageDTO> response = new Response<>();

        AppInfo appInfo = (AppInfo) request.getAttribute(Constants.REQUEST_APP_INFO_INTERNAL);
        if (appInfo.isSuperRole() && StringUtil.isEmpty(dirPath)) {
            dirPath = ImageDirUtil.concat(ImageDirUtil.NAME_APP, appInfo.getEnglishName());
        } else if (!appInfo.isSuperRole()) {
            dirPath = ImageDirUtil.concat(ImageDirUtil.NAME_APP, appInfo.getEnglishName());
        }
        NamingRule namingRule = NamingRule.fromCode(rule);
        ImageDTO res = imageService.saveImage(file, namingRule, dirPath, appInfo.getAppId());
        if (res != null) {
            log.info("上传图片成功：name={}, namingRule={}", res.getName(), namingRule.getDesc());
            return response.success().msg("保存成功").data(res);
        } else {
            log.warn("上传图片失败，内部错误");
            return response.unknownError().msg("保存失败，未知错误");
        }
    }

    /**
     * URL GET /api/v1/images
     */
    @GetMapping("/images")
    public Response<List<ImageDTO>> getAllImage(HttpServletRequest request) {
        Response<List<ImageDTO>> response = new Response<>();
        AppInfo appInfo = (AppInfo) request.getAttribute(Constants.REQUEST_APP_INFO_INTERNAL);
        return response.data(imageService.findAllByAppId(appInfo.getAppId()));
    }
}
