package top.aengus.panther.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity(name = "app_infos")
@DynamicUpdate
@DynamicInsert
@Data
public class AppInfo {

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_DELETED = -1;
    public static final int STATUS_LOCKED = 1;

    public static final int ROLE_NORMAL = 0;
    public static final int ROLE_SUPER = 1;

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "role", columnDefinition = "TINYINT NOT NULL DEFAULT 0")
    private Integer role;

    @Column(name = "name_zh")
    private String name;

    @Column(name = "name_en")
    private String englishName;

    @Column(name = "create_time", columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Long createTime;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "config_id")
    private Integer configId;

    @Column(name = "status", columnDefinition = "TINYINT NOT NULL DEFAULT 0")
    private Integer statue;

    public boolean isSuperRole() {
        return this.role == ROLE_SUPER;
    }

}
