package gzx.zxge.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import gzx.zxge.constants.GameConsts;
import gzx.zxge.pojo.ApiResult;
import gzx.zxge.pojo.Player;
import gzx.zxge.pojo.PlayerRoundInfo;
import gzx.zxge.pojo.RoundInfo;
import gzx.zxge.pojo.*;
import gzx.zxge.utils.CardUtils;
import gzx.zxge.utils.ParamUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 玩家管理器
 */
@Service
public class PlayerManager {

    @Resource
    private GameEngine gameEngine;

    @Resource
    private RoundManager roundManager;

    /**
     * 玩家信息
     */
    private static final List<Player> PLAYER_LIST = Lists.newCopyOnWriteArrayList();

    public List<Player> getAllPlayer() {
        return PLAYER_LIST;
    }

    public void clearPlayer() {
        PLAYER_LIST.clear();
    }

    /**
     * 玩家注册
     */
    public ApiResult playerRegister(Map<String, String> param) {
        Preconditions.checkArgument(GameEngine.GAME_STATUS_CREATE_ING.equals(gameEngine.getGameStatus()), "仅能在游戏等待状态注册玩家信息");
        String userName = param.get("userName");
        String password = param.get("password");
        Preconditions.checkArgument(!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(password), "用户名或密码不能为空");
        Player player = new Player().setName(userName).setPassword(password);
        synchronized (PLAYER_LIST) {
            if (PLAYER_LIST.stream().anyMatch(ele -> userName.equals(ele.getName()))) {
                return ApiResult.fail("用户名已存在!");
            }
            PLAYER_LIST.add(player);
        }
        //生成唯一标识
        String userToken = String.valueOf(UUID.randomUUID().getLeastSignificantBits() * -1);
        player.setUserToken(userToken);
        return ApiResult.success(userToken);
    }

    /**
     * 玩家登录
     */
    public ApiResult playerLogin(Map<String, String> param) {
        String userName = param.get("userName");
        String password = param.get("password");
        Preconditions.checkArgument(!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(password), "用户名或密码不能为空");
        Player matchPlayer = null;
        for (Player player : PLAYER_LIST) {
            if (userName.equals(player.getName()) && password.equals(player.getPassword())) {
                matchPlayer = player;
                break;
            }
        }
        if (matchPlayer == null) {
            return ApiResult.fail("用户名或密码不正确");
        }
        //生成唯一标识
        String userToken = String.valueOf(UUID.randomUUID().getLeastSignificantBits() * -1);
        matchPlayer.setUserToken(userToken);
        return ApiResult.success(userToken);
    }


    /**
     * 玩家进入游戏开始状态
     */
    public void player2StartGame() {
        //检测玩家加入信息
        Preconditions.checkArgument(PLAYER_LIST.size() > 1 && PLAYER_LIST.size() < 10, "参与人数不足或超量");

        //初始化玩家进入游戏中状态
        PLAYER_LIST.forEach(ele -> ele.setGameStatus(Player.GAME_STATUS_PLAY_ING));
        //初始化筹码数
        PLAYER_LIST.forEach(ele -> ele.setChipNumber(ParamUtils.getInitChipNumber()));
    }


    public Player matchPlayerByToken(String token) throws IllegalArgumentException {
        for (Player player : PLAYER_LIST) {
            if (token.equals(player.getUserToken())) {
                return player;
            }
        }
        throw new IllegalArgumentException(ApiResult.CODE_TOKEN_LOSE);
    }

    public Player matchPlayerByName(String name) throws IllegalArgumentException {
        for (Player player : PLAYER_LIST) {
            if (name.equals(player.getName())) {
                return player;
            }
        }
        throw new IllegalArgumentException(ApiResult.CODE_TOKEN_LOSE);
    }

    public boolean isMasterPlayer(String token) {
        Player player = matchPlayerByToken(token);
        return player.getUserToken().equals(PLAYER_LIST.get(0).getUserToken());
    }

    public void playerOption(String token, String buttonType, Map<String, String> param) {
        Player player = matchPlayerByToken(token);
        Preconditions.checkArgument(!Player.GAME_STATUS_UN_READY.equals(player.getGameStatus()), "目前阶段不允许的操作");
        PlayerRoundInfo playerRoundInfo = roundManager.findPlayerRoundInfo(player);
        //执行权校验
        Preconditions.checkArgument(roundManager.getCurrentPlayerRound().equals(playerRoundInfo), "未到你的操作回合");
        //某些操作的结果 可影响战局结束
        if (GameConsts.LOOK_CARD_BUTTON.equals(buttonType)) {
            lookCardOption(playerRoundInfo);
        } else if (GameConsts.ABANDON_CARD_BUTTON.equals(buttonType)) {
            abandonCardOption(playerRoundInfo);
        } else if (GameConsts.ADD_CHIP.equals(buttonType)) {
            addChipOption(playerRoundInfo, param);
        } else if (buttonType.startsWith(GameConsts.FIGHT_PLAYER_BUTTON)) {
            //对拼操作
            fightOption(playerRoundInfo, buttonType.replaceAll(GameConsts.FIGHT_PLAYER_BUTTON, ""));
        } else {
            throw new IllegalArgumentException("未知的操作");
        }
    }

    /**
     * 增加筹码
     */
    private void addChipOption(PlayerRoundInfo playerRoundInfo, Map<String, String> param) {
        int chipNum = Integer.parseInt(param.get("chipNum"));
        RoundInfo roundInfo = playerRoundInfo.getRoundInfo();
        roundInfo.judgeChipNumber(chipNum, playerRoundInfo.getCardStatus());
        roundInfo.setMinAddChipNumber(chipNum, playerRoundInfo.getCardStatus());
        Player player = playerRoundInfo.getPlayer();
        player.setChipNumber(player.getChipNumber() - chipNum);
        playerRoundInfo.setPutIntoCurrent(chipNum);
        playerRoundInfo.setPutIntoTotal(playerRoundInfo.getPutIntoTotal() + chipNum);
        roundInfo.setPoolNumber(roundInfo.getPoolNumber() + chipNum);
        //执行权流转到下一个玩家
        PlayerRoundInfo nextPlayerRoundInfo = roundManager.changeNextPlayer();
        if (PlayerRoundInfo.CARD_STATUS_UN_LOOK.equals(playerRoundInfo.getCardStatus())) {
            roundInfo.setFightShowContent(String.format("玩家 %s 焖加 %s 筹码 ，请玩家 %s 操作",
                    player.getName(), chipNum, nextPlayerRoundInfo.getPlayer().getName()));
        } else {
            roundInfo.setFightShowContent(String.format("玩家 %s 看牌加 %s 筹码 ，请玩家 %s 操作",
                    player.getName(), chipNum, nextPlayerRoundInfo.getPlayer().getName()));
        }

    }

    /**
     * 对拼操作
     */
    private void fightOption(PlayerRoundInfo playerRoundInfo, String fightPlayerName) {
        //发起方付出对应的筹码
        RoundInfo roundInfo = playerRoundInfo.getRoundInfo();
        int chipNum = roundInfo.getMinAddChipNumber(playerRoundInfo.getCardStatus());
        Player player = playerRoundInfo.getPlayer();
        player.setChipNumber(player.getChipNumber() - chipNum);
        playerRoundInfo.setPutIntoCurrent(chipNum);
        playerRoundInfo.setPutIntoTotal(playerRoundInfo.getPutIntoTotal() + chipNum);
        roundInfo.setPoolNumber(roundInfo.getPoolNumber() + chipNum);
        //玩家对拼
        String fightShowContent ;
        Player fightPlayer = matchPlayerByName(fightPlayerName);
        PlayerRoundInfo fightPlayerRoundInfo = roundManager.findPlayerRoundInfo(fightPlayer);
        if (CardUtils.compareCard(playerRoundInfo.getCards(), fightPlayerRoundInfo.getCards())) {
            //战胜
            fightPlayerRoundInfo.setCardStatus(PlayerRoundInfo.CARD_STATUS_FAILED);
            fightPlayerRoundInfo.setDefeaterName(player.getName());
            fightShowContent = String.format("玩家 %s 击败了 玩家 %s", player.getName(), fightPlayer.getName());
        } else {
            //战败
            playerRoundInfo.setCardStatus(PlayerRoundInfo.CARD_STATUS_FAILED);
            playerRoundInfo.setDefeaterName(fightPlayerName);
            fightShowContent = String.format("玩家 %s 击败了 玩家 %s", fightPlayer.getName(), player.getName());
        }

        if (roundManager.currentRoundIsEnd()){
            //触发结束流程
            roundManager.settleAccountsRound();
        } else {
            //执行权流转到下一个玩家
            PlayerRoundInfo nextPlayerRoundInfo = roundManager.changeNextPlayer();
            roundInfo.setFightShowContent(String.format("%s，请玩家 %s 操作",
                    fightShowContent, nextPlayerRoundInfo.getPlayer().getName()));
        }
    }

    /**
     * 弃牌操作
     */
    private void abandonCardOption(PlayerRoundInfo playerRoundInfo) {
        Preconditions.checkArgument(!PlayerRoundInfo.CARD_STATUS_ABANDON.equals(playerRoundInfo.getCardStatus()), "你已经弃过牌");
        playerRoundInfo.setCardStatus(PlayerRoundInfo.CARD_STATUS_ABANDON);
        RoundInfo roundInfo = playerRoundInfo.getRoundInfo();
        //战局影响
        if (roundManager.currentRoundIsEnd()){
            //触发结束流程
            roundManager.settleAccountsRound();
        } else {
            //执行权流转到下一个玩家
            PlayerRoundInfo nextPlayerRoundInfo = roundManager.changeNextPlayer();
            roundInfo.setFightShowContent(String.format("%s 玩家弃牌，请玩家 %s 操作",
                    playerRoundInfo.getPlayer().getName(), nextPlayerRoundInfo.getPlayer().getName()));
        }

    }

    /**
     * 看牌操作
     */
    private void lookCardOption(PlayerRoundInfo playerRoundInfo) {
        Preconditions.checkArgument(PlayerRoundInfo.CARD_STATUS_UN_LOOK.equals(playerRoundInfo.getCardStatus()), "只有焖牌才可以请求看牌");
        playerRoundInfo.setCardStatus(PlayerRoundInfo.CARD_STATUS_LOOK);
        RoundInfo roundInfo = playerRoundInfo.getRoundInfo();
        //记录在第几轮看牌，用于喜钱判断
        playerRoundInfo.setLookRoundNum(roundInfo.getRoundNum());
        int chipNum = roundInfo.getMinAddChipNumber(PlayerRoundInfo.CARD_STATUS_LOOK);
        roundInfo.setFightShowContent(String.format("%s 玩家看牌，请继续操作，最少需要上注 %s",
                playerRoundInfo.getPlayer().getName(), chipNum));
    }


}
