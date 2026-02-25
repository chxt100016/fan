package com.chxt.db.transaction.service;

import com.chxt.db.transaction.entity.MessageBoxPO;
import com.chxt.db.transaction.mapper.MessageBoxMapper;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Repository
public class MessageBoxRepositoryService extends ServiceImpl<MessageBoxMapper, MessageBoxPO> {

	public MessageBoxPO getByUniqueNo(String uniqueNo) {
		return this.lambdaQuery().eq(MessageBoxPO::getUniqueNo, uniqueNo).one();

	}

}