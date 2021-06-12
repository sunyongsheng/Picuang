package top.aengus.panther.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.core.GlobalConfig;
import top.aengus.panther.dao.ImageRepository;
import top.aengus.panther.enums.ImageStatus;
import top.aengus.panther.enums.NamingRule;
import top.aengus.panther.exception.BusinessException;
import top.aengus.panther.model.ImageDTO;
import top.aengus.panther.model.ImageModel;
import top.aengus.panther.service.ImageService;
import top.aengus.panther.tool.DateFormatter;
import top.aengus.panther.tool.FileUtil;
import top.aengus.panther.tool.ImageDirUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public List<ImageDTO> findAllByAppId(String appId) {
        List<ImageModel> imageList = imageRepository.findAllByCreator(appId);
        List<ImageDTO> res = new ArrayList<>();
        for (ImageModel model : imageList) {
            res.add(ImageDTO.from(model));
        }
        return res;
    }

    @Override
    public ImageModel findImageByName(String filename) {
        return imageRepository.findBySaveName(filename);
    }

    @Override
    public ImageDTO saveImage(MultipartFile image, NamingRule rule, String dir, String appId) {
        if (image.isEmpty()) {
            throw new BusinessException("文件不能为空！");
        }

        if (!ImageDirUtil.isValidDir(dir)) {
            throw new BusinessException("路径不合法，只能为 common|post|travel");
        }

        String originName = image.getOriginalFilename();
        if (!FileUtil.isPic(originName)) {
            throw new BusinessException("非图片文件！");
        }
        String saveName = generateName(rule, originName);
        String absolutePath = generateAbsolutePath(dir, saveName);
        String relativePath = generateRelativePath(dir, saveName);
        ImageModel imageModel = new ImageModel();
        imageModel.setOriginalName(originName);
        imageModel.setSaveName(saveName);
        imageModel.setAbsolutePath(absolutePath);
        imageModel.setRelativePath(relativePath);
        imageModel.setUploadTime(System.currentTimeMillis());
        imageModel.setCreator(appId);
        imageModel.setSize(image.getSize());
        imageModel.setStatus(ImageStatus.NORMAL.getCode());
        imageRepository.save(imageModel);
        try {
            File dest = new File(absolutePath);
            FileUtil.checkAndCreateDir(dest.getParentFile());
            image.transferTo(dest);
            return ImageDTO.from(imageModel);
        } catch (IOException e) {
            log.error("保存图片异常", e);
            return null;
        }
    }

    private String generateAbsolutePath(String dir, String name) {
        return new File(new File(GlobalConfig.savePath(), dir), name).getAbsolutePath();
    }

    private String generateRelativePath(String dir, String name) {
        return FileUtil.ensureSuffix(FileUtil.ensurePrefix(dir)) + name;
    }

    private String generateName(NamingRule rule, String originName) {
        String extension = FileUtil.getExtension(originName);
        switch (rule) {
            case UUID:
                return IdUtil.fastSimpleUUID() + extension;
            case ORIGIN:
                return originName;
            case DATE_UUID_HYPHEN:
                return DateUtil.today() + "-" + IdUtil.fastSimpleUUID() + extension;
            case DATE_ORIGIN_HYPHEN:
                return DateUtil.today() + "-" + originName;
            case DATE_UUID_UNDERLINE:
                return DateFormatter.dateUnderlineFormat(new Date()) + "_" + IdUtil.fastSimpleUUID() + extension;
            case DATE_ORIGIN_UNDERLINE:
                return DateFormatter.dateUnderlineFormat(new Date()) + "_" + originName;
        }
        return generateName(NamingRule.DATE_UUID_HYPHEN, originName);
    }

    @Override
    public boolean deleteImage(Long imageId, String operation) {
        ImageModel original = imageRepository.findByIdAndCreator(imageId, operation);
        if (original == null) {
            return false;
        }
        original.delete();
        imageRepository.save(original);
        return true;
    }
}
