package com.chxt.db.transaction;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface TransactionLogMapper extends BaseMapper<TransactionLogPO> {

}
