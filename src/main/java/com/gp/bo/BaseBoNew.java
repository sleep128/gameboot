package com.gp.bo;

import com.gp.dao.model.BaseModel;

import java.util.List;

public interface BaseBoNew<T extends BaseModel, E> {

 /**
  * 插入记录
  * @param record
  * @return
  */
 int insert(T record);

 /**
  * 根据id更新记录
  * @param record
  * @return
  */
 int updateById(T record);

 /**
  * 根据id逻辑删除记录
  * @param record
  * @return
  */
 int logicDeleteById(T record);

 /**
  * 根据id物理删除记录
  * @param id
  * @return
  */
 int deleteById(Long id);

  T queryById(Long id);

  List<T> queryByExample(E example);

  Long countByExample(E example);

  T queryOneByExample(E example);

}
