package gzx.zxge.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 参数获取-后续版本由配置文件获取
 *
 * @author gzx0323
 * @date 2024/03/15 14:33
 */
@Component
public class ParamUtils {


    private static Integer initChipNumber;
    private static Integer bottomChipNumber;
    private static Integer roundMaxChipNumber;
    private static Integer happyMoneyNumber;


    @Value("${initChipNumber}")
    public void setInitChipNumber(Integer initChipNumber) {
        ParamUtils.initChipNumber = initChipNumber;
    }

    @Value("${bottomChipNumber}")
    public void setBottomChipNumber(Integer bottomChipNumber) {
        ParamUtils.bottomChipNumber = bottomChipNumber;
    }

    @Value("${roundMaxChipNumber}")
    public void setRoundMaxChipNumber(Integer roundMaxChipNumber) {
        ParamUtils.roundMaxChipNumber = roundMaxChipNumber;
    }

    @Value("${happyMoneyNumber}")
    public void setHappyMoneyNumber(Integer happyMoneyNumber) {
        ParamUtils.happyMoneyNumber = happyMoneyNumber;
    }

    /**
     * 获取初始化筹码
     */
    public static int getInitChipNumber(){
        return initChipNumber;
    }


    /**
     * 底
     */
    public static int getBottomChipNumber(){
        return bottomChipNumber;
    }

    /**
     * 单次回合筹码上限
     */
    public static int getRoundMaxChipNumber(){
        return roundMaxChipNumber;
    }


    /**
     * 喜钱
     */
    public static int happyMoneyNumber(){
        return happyMoneyNumber;
    }

}
