package com.haotchen.server.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * code:
 * 2000 => 正常请求并响应成功
 * 4000 => 请求失败,请求参数错误
 * 5000 => 请求成功,服务端处理异常
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespBean {
    private long code;
    private String message;
    private Object data;

    /**
     * 响应成功: 返回2000 表示当前请求一切正常.
     * @param msg 消息
     * @return RespBean
     */
    public static RespBean success(String msg){
        return new RespBean(2000,msg,null);
    }
    /**
     * 响应成功: 返回2000 表示当前请求一切正常.
     * @param msg 消息
     * @return RespBean
     */
    public static RespBean success(String msg,Object obj){
        return new RespBean(2000,msg,obj);
    }
    /**
     * 响应成功: 返回 5000 表示当前请求成功但服务端处理异常.
     * @param msg 消息
     * @return RespBean
     */
    public static RespBean fail(String msg){
        return new RespBean(5000,msg,null);
    }

    /**
     * 响应成功: 返回 5000 表示当前请求成功但服务端处理异常.
     * @param msg 消息
     * @return RespBean
     */
    public static RespBean fail(String msg,Object obj){
        return new RespBean(5000,msg,obj);
    }


}
