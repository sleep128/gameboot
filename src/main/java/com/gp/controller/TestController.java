package com.gp.controller;

import com.gp.bo.impl.GpPubgMatchDataBoImpl;
import com.gp.dao.model.GpPubgMatchData;
import com.gp.dao.model.GpPubgMatchDataExample;
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

    @ApiOperation(value = "插入", notes = "插入数据")
    @RequestMapping(value = "/testInsert", method = RequestMethod.POST)
    public int testInsert(@RequestBody GpPubgMatchData gpPubgMatchData) {
        int insert = gpPubgMatchDataBo.insert(gpPubgMatchData);
        return insert;
    }

    @ApiOperation(value = "查询", notes = "根据创建人查询")
    @RequestMapping(value = "/testGet", method = RequestMethod.GET)
    public List<GpPubgMatchData> testGet(@RequestParam String creater) {
        GpPubgMatchDataExample example = new GpPubgMatchDataExample();
        example.createCriteria().andCreatorEqualTo(creater);
        List<GpPubgMatchData> gpPubgMatchData = gpPubgMatchDataBo.queryByExample(example);
        return gpPubgMatchData;
    }


}
