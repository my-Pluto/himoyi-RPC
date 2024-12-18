package com.himoyi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RPC响应
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {

    /**
     * 响应数据
     */
    private Object data;

    /**
     * 响应数据类型
     */
    private Class<?> dataType;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 状态码
     */
    private int code;

    /**
     * 异常信息
     */
    private Exception exception;


    //构造成功信息
    public static RpcResponse sussess(Object data){
        return RpcResponse.builder().code(20000).data(data).build();
    }
    //构造失败信息userService.getUserByUserId(id);
    public static RpcResponse fail(){
        return RpcResponse.builder().code(50000).message("服务器发生错误").build();
    }
}
