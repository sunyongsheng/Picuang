package top.aengus.panther.service;

import top.aengus.panther.model.AppInfo;
import top.aengus.panther.model.CreateAppParam;

/**
 * @author sunyongsheng (sunyongsheng@bytedance.com)
 * <p>
 * date 2021/4/13
 */
public interface AppInfoService {

    AppInfo findByAppId(String appId);

    boolean isSuperRoleApp(String appId);

    String createApp(CreateAppParam appInfo);
}
