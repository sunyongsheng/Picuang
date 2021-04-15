package top.aengus.panther.model;

import lombok.Data;

@Data
public class CreateAppParam {

    private String name;

    private String englishName;

    private String phone;

    private String email;

    public AppInfo toAppInfo() {
        AppInfo info = new AppInfo();
        info.setName(name);
        info.setEnglishName(englishName);
        info.setPhone(phone);
        info.setEmail(email);
        return info;
    }

}
