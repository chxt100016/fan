package com.chxt.db.transaction.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chxt.db.transaction.entity.TransactionLogPO;

@Mapper
public interface TransactionLogMapper extends BaseMapper<TransactionLogPO> {

}
