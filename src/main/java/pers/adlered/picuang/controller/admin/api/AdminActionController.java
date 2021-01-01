package pers.adlered.picuang.controller.admin.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pers.adlered.picuang.prop.Prop;
import pers.adlered.picuang.result.Result;

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
    public Result<String> init() {
        Result<String> result = new Result<>();
        try {
            if (Prop.password().isEmpty()) {
                result.setCode(500);
                result.setData(Prop.getConfigPath());
            } else {
                result.setCode(200);
            }
        } catch (NullPointerException NPE) {
            result.setCode(500);
            result.setData(Prop.getConfigPath());
        }
        return result;
    }

    /**
     * 检查管理员是否已登录
     * @param session
     * @return
     */
    @RequestMapping("/api/admin/check")
    @ResponseBody
    public Result<String> check(HttpSession session) {
        Result<String> result = new Result<>();
        boolean logged = false;
        try {
            String admin = session.getAttribute("admin").toString();
            logged = Boolean.parseBoolean(admin);
        } catch (NullPointerException ignored) {
        }
        if (logged) {
            result.setCode(200);
        } else {
            result.setCode(500);
        }
        return result;
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
    public Result<String> login(HttpSession session, String password) {
        Result<String> result = new Result<>();
        if (password.isEmpty()) {
            result.setCode(500);
            return result;
        }
        String truePassword = Prop.password();
        if (truePassword.equals(password)) {
            session.setAttribute("admin", "true");
            result.setCode(200);
        } else {
            result.setCode(500);
        }
        return result;
    }

    /**
     * 管理员注销
     * @param session
     * @return
     * 200：注销成功
     */
    @RequestMapping("/api/admin/logout")
    @ResponseBody
    public Result<String> logout(HttpSession session) {
        session.invalidate();
        Result<String> result = new Result<>();
        result.setCode(200);
        return result;
    }
}
