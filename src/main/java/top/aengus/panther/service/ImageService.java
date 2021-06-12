package top.aengus.panther.service;

import org.springframework.web.multipart.MultipartFile;
import top.aengus.panther.enums.NamingRule;
import top.aengus.panther.model.ImageDTO;
import top.aengus.panther.model.ImageModel;

import java.util.List;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/20
 */
public interface ImageService {

    List<ImageDTO> findAllByAppId(String appId);

    ImageModel findImageByName(String filename);

    ImageDTO saveImage(MultipartFile image, NamingRule rule, String dir, String appId);

    boolean deleteImage(Long imageId, String operator);

}
