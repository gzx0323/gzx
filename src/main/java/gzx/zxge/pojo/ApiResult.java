package gzx.zxge.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author gzx0323
 * @date 2023/06/15 11:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ApiResult {

    private Boolean success;

    private String message;

    /**
     * token 已过期
     */
    public static final String CODE_TOKEN_LOSE = "100";
    private Object data;

    public static ApiResult fail(String message){
        return new ApiResult().setMessage(message).setSuccess(false);
    }

    public static ApiResult success(Object data){
        return new ApiResult().setSuccess(true).setData(data).setMessage("请求成功");
    }

    public static ApiResult success(){
        return success(null);
    }

}
