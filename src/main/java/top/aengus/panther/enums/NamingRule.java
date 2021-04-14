package top.aengus.panther.enums;

public enum NamingRule {

    UUID(0, "使用自动生成的「UUID」作为文件名"),
    ORIGIN(1, "使用「原始名称」作为文件名"),
    DATE_UUID_HYPHEN(2, "使用「日期-UUID」作为文件名"),
    DATE_ORIGIN_HYPHEN(3, "使用「日期-原始名称」作为文件名"),
    DATE_UUID_UNDERLINE(4, "使用「日期_UUID」作为文件名"),
    DATE_ORIGIN_UNDERLINE(5, "使用「日期_原始名称」作为文件名");

    private final Integer value;
    private final String desc;

    NamingRule(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static NamingRule fromCode(Integer code) {
        for (NamingRule rule : NamingRule.values()) {
            if (rule.value.equals(code)) {
                return rule;
            }
        }
        return DATE_UUID_HYPHEN;
    }
}
