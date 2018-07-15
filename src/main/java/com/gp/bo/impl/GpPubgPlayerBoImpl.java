package com.gp.bo.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.gp.dao.mapper.GpPubgPlayerMapperExt;
import com.gp.dao.model.GpPubgPlayer;
import com.gp.dao.model.GpPubgPlayerExample;
import com.gp.bo.GpPubgPlayerBo;

@Service("pubgPlayerBo")
public class GpPubgPlayerBoImpl implements GpPubgPlayerBo {

    @Autowired
    private GpPubgPlayerMapperExt pubgPlayerMapperExt;

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

}
