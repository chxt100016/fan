package com.chxt.db.transaction.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chxt.db.transaction.entity.TransationPO;

@Mapper
public interface TransactionMapper extends BaseMapper<TransationPO> {

} 