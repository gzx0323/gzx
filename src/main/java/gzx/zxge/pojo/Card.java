package gzx.zxge.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Card {

    private String name;


    /**
     * 黑红梅方
     */
    private String type;

    public static final String POINT_A = "A";
    public static final String POINT_K = "K";
    public static final String POINT_Q = "Q";
    public static final String POINT_J = "J";
    /**
     * 数值
     * "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"
     */
    private String point;

    /**
     * 得分
     * 1,2,3,4,5.....13
     */
    private Integer pointLevel;


    /**
     * 得分
     * 1,2,3,4
     */
    private Integer typeLevel;

    public String getDesc() {
        return point + type;
    }

    public Integer getNextLevel() {
        if (pointLevel > 0 && pointLevel <= 12) {
            return pointLevel + 1;
        } else {
            //13
            return 1;
        }

    }

    @Getter
    public enum TypeEum {

        TYPE_SPADE("♠", 4),
        TYPE_RED_HEART("♥", 3),
        TYPE_BLOSSOM("♣", 2),
        TYPE_BLOCK("♦", 1),
        ;

        TypeEum(String type, Integer typePoint) {
            this.type = type;
            this.typePoint = typePoint;
        }
        private Integer typePoint;
        private String type;
    }


}
