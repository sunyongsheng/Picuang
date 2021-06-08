package top.aengus.panther.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.aengus.panther.core.Response;
import top.aengus.panther.exception.BusinessException;


/**
 * @author Aengus Sun (sys6511@126.com)
 * <p>
 * date 2021/6/8
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Response<String> businessException(BusinessException exception) {
        log.error("[businessException]", exception);
        return new Response<String>().badRequest().msg(exception.getMessage());
    }
}
