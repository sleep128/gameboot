package com.gp.entity;

import java.io.Serializable;

/**
 * @description:
 * @author: SleepSleep
 * @create: 2018/7/27
 **/
public class BaseResult implements Serializable{

    private static final long serialVersionUID = -504851495090463063L;
    public Integer code;
    public Object data;
    public Boolean hasError;
    public String resultMsg;

    public BaseResult(Object data,Boolean hasError,Integer code, String resultMsg) {
        this.code = code;
        this.data = data;
        this.hasError = hasError;
        this.resultMsg = resultMsg;
    }


}
