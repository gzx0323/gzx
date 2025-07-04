package gzx.zxge.pojo;

import lombok.Data;

import java.util.List;
import java.util.StringJoiner;

/**
 * 玩家回合信息
 *
 * @author gzx0323
 * @date 2024/03/12 16:55
 */
@Data
public class PlayerRoundInfo {

    /**
     * 回合信息
     */
    private RoundInfo roundInfo;

    /**
     * 对应玩家
     */
    private Player player;

    /**
     * 总投入
     */
    private Integer putIntoTotal;


    /**
     * 当前投入
     */
    private Integer putIntoCurrent;

    private List<Card> cards;

    public static final String CARD_STATUS_UN_LOOK = "0";
    public static final String CARD_STATUS_LOOK = "1";
    public static final String CARD_STATUS_ABANDON = "2";

    public static final String CARD_STATUS_FAILED = "3";

    /**
     * 牌状态 0-焖牌 1-看牌 2-弃牌 3-被击败
     */
    private String cardStatus;

    /**
     * 击败者
     */
    private String defeaterName;


    public String getCardStatusDesc(){
        if (CARD_STATUS_UN_LOOK.equals(cardStatus)){
            return "正在焖牌";
        }
        if (CARD_STATUS_LOOK.equals(cardStatus)){
            return "已看牌";
        }
        if (CARD_STATUS_ABANDON.equals(cardStatus)){
            return "已弃牌";
        }
        if (CARD_STATUS_FAILED.equals(cardStatus)){
            return String.format("被 %s 击败", defeaterName);
        }
        return null;
    }

    public String getCardsDesc(){
        StringJoiner result = new StringJoiner("|","|","|");
        for (Card card : cards) {
            result.add(card.getDesc());
        }
        return result.toString();
    }


    /**
     * 在第几轮看牌，为 null 代表一直在焖
     */
    private Integer lookRoundNum;

}
