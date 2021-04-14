package top.aengus.panther.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.aengus.panther.dao.AppInfoRepository;
import top.aengus.panther.model.AppInfo;
import top.aengus.panther.service.AppInfoService;

@Service
public class AppInfoServiceImpl implements AppInfoService {

    private final AppInfoRepository appInfoRepository;

    @Autowired
    public AppInfoServiceImpl(AppInfoRepository appInfoRepository) {
        this.appInfoRepository = appInfoRepository;
    }

    @Override
    public AppInfo findByAppId(String appId) {
        return appInfoRepository.findByAppId(appId);
    }

    @Override
    public boolean isSuperRoleApp(String appId) {
        AppInfo appInfo = appInfoRepository.findByAppId(appId);
        return appInfo != null && appInfo.isSuperRole();
    }
}
