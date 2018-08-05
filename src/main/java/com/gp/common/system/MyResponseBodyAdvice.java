package com.gp.common.system;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.gp.entity.BaseResult;
import com.sun.xml.internal.ws.client.sei.ResponseBuilder;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @description:
 * @author: SleepSleep
 * @create: 2018/7/27
 **/
@ControllerAdvice
public class MyResponseBodyAdvice implements ResponseBodyAdvice<Object>{

    private static final String SUB_TYPE = "json";
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {

        String name = methodParameter.getMethod().getName();
        Class<?> returnType = methodParameter.getMethod().getReturnType();
        return !(returnType.isAssignableFrom(ResponseEntity.class));
        //org.springframework.http.ResponseEntity
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        String subtype = mediaType.getSubtype();
        //只处理json 返回
        if(!SUB_TYPE.equals(subtype)){
            return body;
        }
        if(body instanceof BaseResult || body instanceof String){
            return body;
        }
        return new BaseResult(body,Boolean.TRUE,200,"success");
    }
}
