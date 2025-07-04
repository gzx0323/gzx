package gzx.zxge.service;

import com.google.common.collect.Lists;
import gzx.zxge.pojo.Card;
import gzx.zxge.pojo.Player;
import gzx.zxge.pojo.PlayerRoundInfo;
import gzx.zxge.pojo.RoundInfo;
import gzx.zxge.utils.CardUtils;
import gzx.zxge.utils.ParamUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

/**
 * 回合管理器
 *
 * @author gzx0323
 * @date 2024/03/12 16:43
 */
@Service
public class RoundManager {

    private List<PlayerRoundInfo> playerRoundInfos;

    private PlayerRoundInfo currentPlayerRound;

    private RoundInfo beforeRoundCache;

    private RoundInfo currentRoundCache;

    public List<PlayerRoundInfo> getPlayerRoundInfos() {
        return playerRoundInfos;
    }

    public PlayerRoundInfo findPlayerRoundInfo(Player player) {
        for (PlayerRoundInfo playerRoundInfo : playerRoundInfos) {
            if (playerRoundInfo.getPlayer().equals(player)) {
                return playerRoundInfo;
            }
        }
        throw new RuntimeException("未匹配到的玩家回合");
    }

    public PlayerRoundInfo getCurrentPlayerRound() {
        return currentPlayerRound;
    }

    /**
     * 切换至下一个玩家执行
     *
     * @return 下一个玩家
     */
    public PlayerRoundInfo changeNextPlayer() {
        //检索当前玩家所处位置
        int currentPlayerIndex = playerRoundInfos.indexOf(currentPlayerRound);
        //向后循环
        for (int i = (currentPlayerIndex + 1); i < playerRoundInfos.size(); i++) {
            PlayerRoundInfo playerRoundInfo = playerRoundInfos.get(i);
            if (PlayerRoundInfo.CARD_STATUS_UN_LOOK.equals(playerRoundInfo.getCardStatus())
                    || PlayerRoundInfo.CARD_STATUS_LOOK.equals(playerRoundInfo.getCardStatus())) {
                currentPlayerRound = playerRoundInfo;
                return playerRoundInfo;
            }
        }
        //从头循环（轮数加1）
        currentRoundCache.setRoundNum(currentRoundCache.getRoundNum() + 1);
        for (int i = 0; i < currentPlayerIndex; i++) {
            PlayerRoundInfo playerRoundInfo = playerRoundInfos.get(i);
            if (PlayerRoundInfo.CARD_STATUS_UN_LOOK.equals(playerRoundInfo.getCardStatus())
                    || PlayerRoundInfo.CARD_STATUS_LOOK.equals(playerRoundInfo.getCardStatus())) {
                currentPlayerRound = playerRoundInfo;
                return playerRoundInfo;
            }
        }
        throw new RuntimeException("没有下一个可执行的玩家");
    }

    public RoundInfo getBeforeRoundCache() {
        return beforeRoundCache;
    }

    public RoundInfo getCurrentRoundCache() {
        return currentRoundCache;
    }

    @Resource
    private PlayerManager playerManager;


    /**
     * 启动首轮回合
     */
    public void startFirstRound() {
        startRound(null);
    }

    /**
     * 开启对局
     *
     * @param winPlayer 上局胜利的玩家 为 null 时代办首轮对局
     */
    public void startRound(Player winPlayer) {
        List<Player> players = playerManager.getAllPlayer();
        int startIndex = 0;
        if (!ObjectUtils.isEmpty(winPlayer)) {
            beforeRoundCache = currentRoundCache;
            currentRoundCache = null;
            startIndex = players.indexOf(winPlayer);
        }
        //创建对局信息
        RoundInfo roundInfo = new RoundInfo();
        roundInfo.setPoolNumber(0);
        roundInfo.setRoundNum(0);
        roundInfo.setFightShowContent("");
        currentRoundCache = roundInfo;
        //创建玩家回合信息
        playerRoundInfos = Lists.newArrayList();
        for (int i = startIndex; i < players.size(); i++) {
            Player player = players.get(i);
            PlayerRoundInfo playerRoundInfo = new PlayerRoundInfo();
            playerRoundInfo.setRoundInfo(roundInfo);
            playerRoundInfo.setPlayer(player);
            playerRoundInfos.add(playerRoundInfo);
        }
        for (int i = 0; i < startIndex; i++) {
            Player player = players.get(i);
            PlayerRoundInfo playerRoundInfo = new PlayerRoundInfo();
            playerRoundInfo.setRoundInfo(roundInfo);
            playerRoundInfo.setPlayer(player);
            playerRoundInfos.add(playerRoundInfo);
        }
        //默认当前玩家已经上底
        for (PlayerRoundInfo playerRoundInfo : playerRoundInfos) {
            playerRoundInfo.getPlayer().setChipNumber(
                    playerRoundInfo.getPlayer().getChipNumber() - ParamUtils.getBottomChipNumber());
            playerRoundInfo.setPutIntoCurrent(ParamUtils.getBottomChipNumber());
            playerRoundInfo.setPutIntoTotal(ParamUtils.getBottomChipNumber());
            currentRoundCache.setPoolNumber(currentRoundCache.getPoolNumber() + ParamUtils.getBottomChipNumber());
        }
        currentRoundCache.setFightShowContent("全部玩家已上底");
        //玩家发牌
        initPlayersCard();
        //设置当前执行玩家
        currentPlayerRound = playerRoundInfos.get(0);
        currentRoundCache.setRoundNum(1);
    }


    private void initPlayersCard() {
        for (PlayerRoundInfo playerRoundInfo : playerRoundInfos) {
            playerRoundInfo.setCards(Lists.newArrayList());
            playerRoundInfo.setCardStatus(PlayerRoundInfo.CARD_STATUS_UN_LOOK);

        }
        List<Card> cards = CardUtils.initCards();
        for (int i = 0; i < 3; i++) {
            playerRoundInfos.forEach(playerRoundInfo -> {
                playerRoundInfo.getCards().add(cards.remove(0));
            });
        }
        playerRoundInfos.forEach(playerRoundInfo -> playerRoundInfo.getCards().sort(Comparator.comparing(Card::getPointLevel).reversed()));
    }


    /**
     * 结算对局
     */
    public void settleAccountsRound() {
        //唯一存留玩家
        if (!currentRoundIsEnd()) {
            throw new RuntimeException("当前对局不允许结束！");
        }
        PlayerRoundInfo winPlayerRoundInfo = null;
        for (PlayerRoundInfo playerRoundInfo : playerRoundInfos) {
            if (PlayerRoundInfo.CARD_STATUS_UN_LOOK.equals(playerRoundInfo.getCardStatus())
                    || PlayerRoundInfo.CARD_STATUS_LOOK.equals(playerRoundInfo.getCardStatus())) {
                winPlayerRoundInfo = playerRoundInfo;
                break;
            }
        }
        if (ObjectUtils.isEmpty(winPlayerRoundInfo)) {
            throw new RuntimeException("对局异常，未找到胜利玩家！");
        }
        //结算筹码
        Player winPlayer = winPlayerRoundInfo.getPlayer();
        winPlayer.setChipNumber(winPlayer.getChipNumber() + currentRoundCache.getPoolNumber());
        //登记战果
        StringJoiner resultShow = new StringJoiner("\n");
        resultShow.add("所有玩家牌型公布：");
        for (PlayerRoundInfo playerRoundInfo : playerRoundInfos) {
            if (playerRoundInfo.getPlayer().equals(winPlayer)){
                resultShow.add(String.format("%s 玩家牌型为 %s，赢得筹码 %s", playerRoundInfo.getPlayer().getName(),
                        playerRoundInfo.getCardsDesc(), (currentRoundCache.getPoolNumber() - playerRoundInfo.getPutIntoTotal())));
            } else {
                resultShow.add(String.format("%s 玩家牌型为 %s，状态 %s, 输掉筹码 %s",
                        playerRoundInfo.getCardsDesc(), playerRoundInfo.getPlayer().getName(),playerRoundInfo.getCardStatusDesc(), playerRoundInfo.getPutIntoTotal()));
            }
        }
        //喜钱判定
        if (winPlayerRoundInfo.getLookRoundNum() == null || winPlayerRoundInfo.getLookRoundNum() > 2) {
            if (CardUtils.isHappyMoneyCard(winPlayerRoundInfo.getCards())) {
                resultShow.add(String.format("胜者为喜钱牌型，其他玩家额外给 %s 筹码", ParamUtils.happyMoneyNumber()));
                for (PlayerRoundInfo playerRoundInfo : playerRoundInfos) {
                    if (!playerRoundInfo.getPlayer().equals(winPlayer)) {
                        playerRoundInfo.getPlayer().setChipNumber(playerRoundInfo.getPlayer().getChipNumber() - ParamUtils.happyMoneyNumber());
                        winPlayer.setChipNumber(winPlayer.getChipNumber() + ParamUtils.happyMoneyNumber());
                    }
                }
            }
        }
        currentRoundCache.setResultShowContent(resultShow.toString());
        //开始新的一局
        startRound(winPlayer);
    }


    /**
     * 判定当前对局是否结束
     *
     * @return ture-是
     */
    public boolean currentRoundIsEnd() {
        int canOptionPlayerCount = 0;
        for (PlayerRoundInfo playerRoundInfo : playerRoundInfos) {
            if (PlayerRoundInfo.CARD_STATUS_UN_LOOK.equals(playerRoundInfo.getCardStatus())
                    || PlayerRoundInfo.CARD_STATUS_LOOK.equals(playerRoundInfo.getCardStatus())) {
                canOptionPlayerCount++;
            }
        }
        return canOptionPlayerCount == 1;
    }

}
