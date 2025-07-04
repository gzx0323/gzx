package gzx.zxge.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.StringJoiner;

@Data
@Accessors(chain = true)
public class Player {

    private String name;

    private String password;

    private String userToken;

    public static final String GAME_STATUS_UN_READY = "0";
    public static final String GAME_STATUS_PLAY_ING = "2";

    /**
     * 玩家状态 0-未准备 2-游戏中
     */
    private String gameStatus;

    /**
     * 筹码
     */
    private Integer chipNumber;




}
