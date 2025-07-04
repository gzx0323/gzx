package gzx.zxge.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class HandCard {

    /**
     * 手牌
     */
    private List<Card> handCardList;
    /**
     * 得分
     */
    private Integer score;

    /**
     * 手牌 数值最大得分
     */
    private Integer itemPointMaxScore;

    /**
     * 手牌 花色最大得分
     */
    private Integer itemTypeMaxScore;

    /**
     * 是否排序
     */
    private Boolean sort = false;

    public HandCard(List<Card> handCardList) {
        this.handCardList = handCardList;
    }

    private Integer getItemPointMaxScore() {
        List<Card> sortCardList = getSortCardList();
        Map<Integer, List<Card>> collect = sortCardList.stream().collect(Collectors.groupingBy(Card::getPointLevel));
        if (isDouble()) {
            for (Integer i : collect.keySet()) {
                List<Card> cards = collect.get(i);
                if (cards.size() == 2) {
                    return i;
                }
            }
        }
        return sortCardList.get(2).getPointLevel();

    }

    private Integer getitemTypeMaxScore() {
        List<Card> sortCardList = getSortCardList();
        return sortCardList.get(2).getTypeLevel();
    }

    /**
     * 排下序
     *
     * @return
     */
    private List<Card> getSortCardList() {
        if (sort) {
            return handCardList;
        }
        handCardList = handCardList.stream()
                .sorted(Comparator.comparingInt(Card::getPointLevel)
                        .thenComparingInt(Card::getTypeLevel))
                .collect(Collectors.toList());
        sort = true;
        return handCardList;
    }

    /**
     * 是否是对子
     */
    private Boolean isDouble() {
        List<Card> sortCardList = getSortCardList();
        return sortCardList.stream().map(Card::getPointLevel).distinct().count() == 2;
    }

    /**
     * 是否是顺子
     */
    public Boolean isStraight() {
        List<Card> sortCardList = getSortCardList();
        if (is23A()) {
            return true;
        }

        return Objects.equals(sortCardList.get(0).getNextLevel(), sortCardList.get(1).getPointLevel())
                && Objects.equals(sortCardList.get(1).getNextLevel(), sortCardList.get(2).getPointLevel());
    }


    /**
     * 是否是金花
     */
    private Boolean isFlower() {
        List<Card> sortCardList = getSortCardList();
        return sortCardList.stream().map(Card::getType).distinct().count() == 1;
    }

    /**
     * 判断顺金
     *
     * @return
     */
    public Boolean isStraightFlower() {
        return isFlower() && isStraight();
    }

    /**
     * 判断豹子
     *
     * @return
     */
    public Boolean isLeopard() {
        List<Card> sortCardList = getSortCardList();
        return sortCardList.stream().map(Card::getPointLevel).distinct().count() == 1;
    }

    public Boolean is235() {
        List<Card> sortCardList = getSortCardList();
        return Objects.equals(sortCardList.get(0).getPointLevel(), 2)
                && Objects.equals(sortCardList.get(1).getPointLevel(), 3)
                && Objects.equals(sortCardList.get(2).getPointLevel(), 5)
                && !isFlower();
    }

    public Boolean is23A() {
        List<Card> sortCardList = getSortCardList();
        return Objects.equals(sortCardList.get(0).getPointLevel(), 2)
                && Objects.equals(sortCardList.get(1).getPointLevel(), 3)
                && Objects.equals(sortCardList.get(2).getPointLevel(), 13)
                && !isFlower();
    }

    /**
     * 计算得分
     *
     * 最后两位-->第三张牌黑红梅芳
     * 往前两位-->第二张牌黑红梅芳
     * 往前两位-->单牌最大牌黑红梅芳
     *
     * 往前两位-->第三张牌最大
     * 往前两位-->第二张牌最大
     * 往前两位-->单牌最大
     *
     * 往前两位-->对子数值 往前两位-->顺子最大数值 往前两位-->金花最大数值  往前两位-->顺金数值  往前两位-->豹子数值
     *
     */
    public BigDecimal calculate() {
        List<Card> sortCardList = getSortCardList();
        String thirdTypeScore = String.format("%02d", sortCardList.get(0).getTypeLevel());
        String thirdPointScore = String.format("%02d", sortCardList.get(0).getPointLevel());
        String secondTypeScore = String.format("%02d", sortCardList.get(1).getTypeLevel());
        String secondPointScore = String.format("%02d", sortCardList.get(1).getPointLevel());

        Integer pointMaxScoreInt = getItemPointMaxScore();
        String pointMaxScore = String.format("%02d", pointMaxScoreInt);
        String typeMaxScore = String.format("%02d", getitemTypeMaxScore());

        String doubleValue = isDouble() ? String.format("%02d", pointMaxScoreInt) : "00";
        String straightValue = isStraight() ? String.format("%02d", pointMaxScoreInt) : "00";
        String flowerValue = isFlower() ? String.format("%02d", pointMaxScoreInt) : "00";
        String straightFlowerValue = isStraightFlower() ? String.format("%02d", pointMaxScoreInt) : "00";
        String leopardValue = isLeopard() ? String.format("%02d", pointMaxScoreInt) : "00";
        String value = leopardValue + straightFlowerValue + flowerValue + straightValue + doubleValue
                + pointMaxScore + secondPointScore + thirdPointScore + typeMaxScore + secondTypeScore + thirdTypeScore;
        return new BigDecimal(value);
    }

}
