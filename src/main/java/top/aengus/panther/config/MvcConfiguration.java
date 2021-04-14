package top.aengus.panther.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;
import top.aengus.panther.component.ApiRequestInterceptor;
import top.aengus.panther.core.GlobalConfig;
import top.aengus.panther.tool.FileUtil;

/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/1/1
 */
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    private static final String FILE_PROTOCOL = "file:///";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:static/", FILE_PROTOCOL + FileUtil.ensureSuffix(GlobalConfig.savePath()));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getApiInterceptor())
            .addPathPatterns("/api/v1/**");
    }

    @Bean
    public HandlerInterceptor getApiInterceptor() {
        return new ApiRequestInterceptor();
    }

}
