package pers.adlered.picuang.controller.admin.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pers.adlered.picuang.core.GlobalConfig;
import pers.adlered.picuang.core.Response;

import javax.servlet.http.HttpSession;

/**
 * <h3>picuang</h3>
 * <p>管理员API</p>
 *
 * @author : https://github.com/AdlerED
 * @date : 2019-11-07 17:16
 **/
@Controller
public class AdminActionController {
    /**
     * 检测管理员密码是否已经设定
     * @return
     * 500：管理员密码未设定，需要在Data中指定的文件设置密码
     * 200：管理员密码已设定，可以登录
     */
    @RequestMapping("/api/admin/init")
    @ResponseBody
    public Response<String> init() {
        Response<String> response = new Response<>();
        try {
            if (GlobalConfig.password().isEmpty()) {
                response.setCode(500);
                response.setData(GlobalConfig.getConfigPath());
            } else {
                response.setCode(200);
            }
        } catch (NullPointerException NPE) {
            response.setCode(500);
            response.setData(GlobalConfig.getConfigPath());
        }
        return response;
    }

    /**
     * 检查管理员是否已登录
     * @param session
     * @return
     */
    @RequestMapping("/api/admin/check")
    @ResponseBody
    public Response<String> check(HttpSession session) {
        Response<String> response = new Response<>();
        boolean logged = false;
        try {
            String admin = session.getAttribute("admin").toString();
            logged = Boolean.parseBoolean(admin);
        } catch (NullPointerException ignored) {
        }
        if (logged) {
            response.setCode(200);
        } else {
            response.setCode(500);
        }
        return response;
    }

    /**
     * 管理员登录
     * 管理员登录为了安全考虑，必须使用POST方法传输
     * @param session
     * @param password
     * @return
     * 500：登录失败
     * 200：登录成功
     */
    @RequestMapping(value = "/api/admin/login", method = RequestMethod.POST)
    @ResponseBody
    public Response<String> login(HttpSession session, String password) {
        Response<String> response = new Response<>();
        if (password.isEmpty()) {
            response.setCode(500);
            return response;
        }
        String truePassword = GlobalConfig.password();
        if (truePassword.equals(password)) {
            session.setAttribute("admin", "true");
            response.setCode(200);
        } else {
            response.setCode(500);
        }
        return response;
    }

    /**
     * 管理员注销
     * @param session
     * @return
     * 200：注销成功
     */
    @RequestMapping("/api/admin/logout")
    @ResponseBody
    public Response<String> logout(HttpSession session) {
        session.invalidate();
        Response<String> response = new Response<>();
        response.setCode(200);
        return response;
    }
}
