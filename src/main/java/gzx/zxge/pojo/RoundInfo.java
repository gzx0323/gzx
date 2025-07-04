package gzx.zxge.pojo;

import gzx.zxge.utils.ParamUtils;
import lombok.Data;

/**
 * @author gzx0323
 * @date 2024/03/12 16:52
 */
@Data
public class RoundInfo {

    /**
     * 轮数
     */
    private Integer roundNum;

    /**
     * 池底
     */
    private Integer poolNumber;

    /**
     * 上一轮焖牌筹码
     */
    private Integer lastUnLookChipNumber = ParamUtils.getBottomChipNumber();

    /**
     * 上一轮看牌筹码
     */
    private Integer lastLookChipNumber = (ParamUtils.getBottomChipNumber() * 2);

    /**
     * 战局提示
     */
    private String fightShowContent;

    /**
     * 战斗结果
     */
    private String resultShowContent;


    /**
     * 获取筹码最低增加限制
     */
    public int getMinAddChipNumber(String cardStatus) {
        if (PlayerRoundInfo.CARD_STATUS_UN_LOOK.equals(cardStatus)) {
            if (lastLookChipNumber % 2 ==0){
                return Math.max(lastUnLookChipNumber, lastLookChipNumber / 2);
            } else {
                return Math.max(lastUnLookChipNumber, (lastLookChipNumber + 1) / 2);
            }
        }

        if (PlayerRoundInfo.CARD_STATUS_LOOK.equals(cardStatus)) {
            return Math.max(lastUnLookChipNumber * 2, lastLookChipNumber);
        }

        throw new IllegalArgumentException("不被允许的牌状态操作");
    }

    public void setMinAddChipNumber(int chipNumber, String cardStatus) {
        if (PlayerRoundInfo.CARD_STATUS_UN_LOOK.equals(cardStatus)) {
            lastUnLookChipNumber = chipNumber;
            return;
        }
        if (PlayerRoundInfo.CARD_STATUS_LOOK.equals(cardStatus)) {
            lastLookChipNumber = chipNumber;
            return;
        }
        throw new IllegalArgumentException("不被允许的牌状态操作");
    }


    /**
     * 校验筹码是否达标
     */
    public void judgeChipNumber(int chipNumber, String cardStatus) {
        int minAddChipNumber = getMinAddChipNumber(cardStatus);
        if (chipNumber < minAddChipNumber) {
            throw new IllegalArgumentException("筹码数量不得低于" + minAddChipNumber);
        }
        if (PlayerRoundInfo.CARD_STATUS_UN_LOOK.equals(cardStatus)) {
            if (chipNumber > ParamUtils.getRoundMaxChipNumber()) {
                throw new IllegalArgumentException("筹码数量不得超过上限");
            }
        }
        if (PlayerRoundInfo.CARD_STATUS_LOOK.equals(cardStatus)) {
            if (chipNumber > (ParamUtils.getRoundMaxChipNumber() * 2)) {
                throw new IllegalArgumentException("筹码数量不得超过上限");
            }
        }
    }


}
