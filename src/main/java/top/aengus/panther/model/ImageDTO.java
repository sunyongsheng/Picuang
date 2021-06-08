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

    private Long id;

    // 图片文件名
    private String name;

    // 图片的URL
    private String url;

    private Long uploadTime;

    private String creator;

    private Long size;

    public static ImageDTO from(ImageModel model) {
        ImageDTO dto = new ImageDTO();
        dto.setName(model.getSaveName());
        dto.setUrl(model.getUrl());
        dto.setUploadTime(model.getUploadTime());
        dto.setCreator(model.getCreator());
        dto.setSize(model.getSize());
        return dto;
    }

}
