package top.aengus.panther.service.impl;

import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.aengus.panther.dao.AppInfoRepository;
import top.aengus.panther.enums.AppRole;
import top.aengus.panther.enums.AppStatus;
import top.aengus.panther.model.AppInfo;
import top.aengus.panther.model.CreateAppParam;
import top.aengus.panther.service.AppInfoService;

import java.util.Date;

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

    @Override
    public String createApp(CreateAppParam appInfo) {
        String appId = IdUtil.fastSimpleUUID();
        AppInfo toSave = appInfo.toAppInfo();
        toSave.setAppId(appId);
        toSave.setCreateTime(new Date().getTime());
        toSave.setRole(AppRole.NORMAL.getCode());
        toSave.setStatus(AppStatus.NORMAL.getCode());
        appInfoRepository.save(toSave);
        return appId;
    }
}
