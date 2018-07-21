package com.gp.bo.impl;
import java.util.List;

import com.gp.dao.mapper.GpPubgMatchDataMapperExt;
import com.gp.dao.model.GpPubgMatchData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.gp.dao.mapper.GpPubgPlayerMapperExt;
import com.gp.dao.model.GpPubgPlayer;
import com.gp.dao.model.GpPubgPlayerExample;
import com.gp.bo.GpPubgPlayerBo;

@Service("pubgPlayerBo")
@Transactional
public class GpPubgPlayerBoImpl implements GpPubgPlayerBo {

    @Autowired
    private GpPubgPlayerMapperExt pubgPlayerMapperExt;

    @Autowired
    private GpPubgMatchDataMapperExt pubgMatchDataMapperExt;

    @Override
    public int insert(GpPubgPlayer record) {
        return pubgPlayerMapperExt.insertSelective(record);
    }

    @Override
    public int updateById(GpPubgPlayer record) {
        validate(record);
        return pubgPlayerMapperExt.updateByPrimaryKeySelective(record);
    }

    @Override
    public int logicDeleteById(GpPubgPlayer record) {
        validate(record);
        return pubgPlayerMapperExt.deleteByPrimaryKey(record);
    }

    @Override
    public int deleteById(Long id) {
        return pubgPlayerMapperExt.deleteById(id);
    }


    @Override
    public GpPubgPlayer queryById(Long id) {
        return pubgPlayerMapperExt.selectByPrimaryKey(id);
    }

    @Override
    public List<GpPubgPlayer> queryByExample(GpPubgPlayerExample example) {
        return pubgPlayerMapperExt.selectByExample(example);
    }

    @Override
    public Long countByExample(GpPubgPlayerExample example) {
        return pubgPlayerMapperExt.countByExample(example);
    }

    @Override
    public GpPubgPlayer queryOneByExample(GpPubgPlayerExample example) {
        List<GpPubgPlayer> list = queryByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    private void validate(GpPubgPlayer record) {
        Assert.notNull(record, "record is null");
        Assert.notNull(record.getId(), "id is null");
    }

    @Override
    public void testTranzaction() {
        GpPubgPlayer player = new GpPubgPlayer();
        player.setIsDeleted("n");
        player.setPlayerType("tran");
        insert(player);
        GpPubgMatchData matchData = new GpPubgMatchData();
        matchData.setPlayerId(player.getId());
        matchData.setMatchData("testTran");
        matchData.setIsDeleted("abc");
        pubgMatchDataMapperExt.insertSelective(matchData);
    }
}
