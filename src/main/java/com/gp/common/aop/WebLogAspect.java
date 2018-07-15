package com.gp.common.aop;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @description:
 * @author: SleepSleep
 * @create: 2018/7/15
 **/
@Aspect
@Component
public class WebLogAspect {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("execution(public * com.gp.controller..*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void webLog() {

    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        logger.info("URL : " + request.getRequestURL().toString());
        //请求类型
        String method = request.getMethod();
        logger.info("HTTP_METHOD : " + method);
        logger.info("IP : " + request.getRemoteAddr());

        StringBuilder params = new StringBuilder();
        if (HttpMethod.GET.toString().equals(method)) {
            //get 请求
            Enumeration<String> enu = request.getParameterNames();
            // 记录下请求内容
            params.append("requestParams:{");
            while (enu.hasMoreElements()) {
                String name = enu.nextElement();
                params.append(String.format("{name:%s,value:%s}", name, request.getParameter(name)));
            }
            params.append("}");
        } else {
            //其他请求
            Object[] args = joinPoint.getArgs();
            String s = JSONObject.toJSONString(args);
            params.append("requestBody:" + s);
        }
        logger.info(params.toString());
    }

    @AfterReturning(returning = "response", pointcut = "webLog()")
    public void doAfterReturning(Object response) throws Throwable {
        // 处理完请求，返回内容
        logger.info("response : " + JSONObject.toJSONString(response));
    }
}
