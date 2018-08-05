package com.gp.controller;

import com.gp.bo.GpPubgPlayerBo;
import com.gp.bo.impl.GpPubgMatchDataBoImpl;
import com.gp.dao.model.GpPubgMatchData;
import com.gp.dao.model.GpPubgMatchDataExample;
import com.gp.dao.model.GpPubgPlayer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:
 * @author: SleepSleep
 * @create: 2018/7/15
 **/

@Api(value = "test", tags = "测试入口")
@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    private GpPubgMatchDataBoImpl gpPubgMatchDataBo;

    @Resource
    private GpPubgPlayerBo pubgPlayerBo;

    @ApiOperation(value = "插入比赛数据", notes = "插入数据")
    @RequestMapping(value = "/testInsert", method = RequestMethod.POST)
    public int testInsert(@RequestBody GpPubgMatchData gpPubgMatchData) {
        int insert = gpPubgMatchDataBo.insert(gpPubgMatchData);
        return insert;
    }

    @ApiOperation(value = "根据创建人查询比赛数据", notes = "根据创建人查询")
    @RequestMapping(value = "/testGet", method = RequestMethod.GET)
    public List<GpPubgMatchData> getMatchByPlayer(@RequestParam Long player) {
        GpPubgMatchDataExample example = new GpPubgMatchDataExample();
        example.createCriteria().andPlayerIdEqualTo(player);
        List<GpPubgMatchData> gpPubgMatchData = gpPubgMatchDataBo.queryByExample(example);
        return gpPubgMatchData;
    }

    @ApiOperation(value = "插入玩家信息", notes = "根据创建人查询")
    @RequestMapping(value = "/insertPlayer", method = RequestMethod.POST)
    public Integer insertPlayer(@RequestBody GpPubgPlayer gpPubgPlayer) {
        int insert = pubgPlayerBo.insert(gpPubgPlayer);
        return insert;
    }

    @ApiOperation(value = "测试事务", notes = "事务")
    @RequestMapping(value = "/insertPlayerTest", method = RequestMethod.POST)
    public void insertPlayerTest(){

        pubgPlayerBo.testTranzaction();

    }


}
