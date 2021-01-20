package top.aengus.panther.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/19
 */
@Entity(name = "images")
@Table(appliesTo = "images", comment = "上传图片记录表")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageModel {

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_DELETED = -1;

    @Id
    @Column(name = "image_id", unique = true, nullable = false)
    private Long imageId;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "save_name")
    private String saveName;

    @Column(name = "save_path")
    private String savePath;

    @Column(name = "upload_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private Timestamp uploadTime;

    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 0 COMMENT '0: 正常，-1: 已删除'")
    private int status;

    public ImageModel(String originalName, String saveName, String savePath, int status) {
        this.originalName = originalName;
        this.saveName = saveName;
        this.savePath = savePath;
        this.status = status;
    }

    public void delete() {
        this.status = STATUS_DELETED;
    }

    public boolean isDeleted() {
        return status == STATUS_DELETED;
    }

}
