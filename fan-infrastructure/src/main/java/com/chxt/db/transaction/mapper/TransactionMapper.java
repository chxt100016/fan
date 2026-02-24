package com.chxt.db.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chxt.db.transaction.entity.TransactionPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionMapper extends BaseMapper<TransactionPO> {

} 