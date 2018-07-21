package com.gp.bo.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.gp.dao.mapper.GpPubgMatchDataMapperExt;
import com.gp.dao.model.GpPubgMatchData;
import com.gp.dao.model.GpPubgMatchDataExample;
import com.gp.bo.GpPubgMatchDataBo;

@Service("pubgMatchDataBo")
@Transactional
public class GpPubgMatchDataBoImpl implements GpPubgMatchDataBo {

    @Autowired
    private GpPubgMatchDataMapperExt pubgMatchDataMapperExt;

    @Override
    public int insert(GpPubgMatchData record) {
        return pubgMatchDataMapperExt.insertSelective(record);
    }

    @Override
    public int updateById(GpPubgMatchData record) {
        validate(record);
        return pubgMatchDataMapperExt.updateByPrimaryKeySelective(record);
    }

    @Override
    public int logicDeleteById(GpPubgMatchData record) {
        validate(record);
        return pubgMatchDataMapperExt.deleteByPrimaryKey(record);
    }

    @Override
    public int deleteById(Long id) {
        return pubgMatchDataMapperExt.deleteById(id);
    }


    @Override
    public GpPubgMatchData queryById(Long id) {
        return pubgMatchDataMapperExt.selectByPrimaryKey(id);
    }

    @Override
    public List<GpPubgMatchData> queryByExample(GpPubgMatchDataExample example) {
        return pubgMatchDataMapperExt.selectByExample(example);
    }

    @Override
    public Long countByExample(GpPubgMatchDataExample example) {
        return pubgMatchDataMapperExt.countByExample(example);
    }

    @Override
    public GpPubgMatchData queryOneByExample(GpPubgMatchDataExample example) {
        List<GpPubgMatchData> list = queryByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    private void validate(GpPubgMatchData record) {
        Assert.notNull(record, "record is null");
        Assert.notNull(record.getId(), "id is null");
    }

}
