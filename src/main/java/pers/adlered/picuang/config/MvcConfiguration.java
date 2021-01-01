package pers.adlered.picuang.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pers.adlered.picuang.prop.Prop;

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
                .addResourceLocations("classpath:static/", FILE_PROTOCOL + Prop.savePath());


//                .setCacheControl(CacheControl.maxAge(7L, TimeUnit.DAYS))
    }

}
