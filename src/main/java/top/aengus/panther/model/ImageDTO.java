package top.aengus.panther.model;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author sunyongsheng (sunyongsheng@bytedance.com)
 * <p>
 * date 2021/4/13
 */
@Data
public class ImageDTO {

    // 图片文件名
    private String name;

    // 图片的URL
    private String url;

    private Timestamp uploadTime;

    private String creator;

    private Long size;

    public static ImageDTO from(ImageModel model, String baseUrl) {
        ImageDTO dto = new ImageDTO();
        dto.setName(model.getSaveName());
        dto.setUrl(baseUrl + model.getRelativePath());
        dto.setUploadTime(model.getUploadTime());
        dto.setCreator(model.getCreator());
        dto.setSize(model.getSize());
        return dto;
    }

}
