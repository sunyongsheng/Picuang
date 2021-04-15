package top.aengus.panther.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import top.aengus.panther.core.Response;
import top.aengus.panther.model.CreateAppParam;
import top.aengus.panther.service.AppInfoService;

@RestController
public class AppInfoController {

    private final AppInfoService imageService;

    @Autowired
    public AppInfoController(AppInfoService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/create/app")
    public Response<String> register(@RequestBody CreateAppParam appParam) {
        Response<String> response = new Response<>();
        String appId = imageService.createApp(appParam);
        return response.msg("注册成功，请妥善保管AppID").data(appId);
    }
}
