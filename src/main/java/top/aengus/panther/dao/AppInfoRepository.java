package top.aengus.panther.dao;

import org.springframework.data.repository.CrudRepository;
import top.aengus.panther.model.AppInfo;


public interface AppInfoRepository extends CrudRepository<AppInfo, Integer> {

    AppInfo findByAppId(String appId);
}
