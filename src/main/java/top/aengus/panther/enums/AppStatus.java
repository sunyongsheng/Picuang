package top.aengus.panther.enums;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/6/12
 */
public enum AppStatus {
    NORMAL(0, "正常"),
    DELETED(-1, "已删除"),
    LOCKED(1, "已锁定");

    private final Integer code;
    private final String desc;

    AppStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
