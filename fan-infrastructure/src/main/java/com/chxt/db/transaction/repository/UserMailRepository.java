package com.chxt.db.transaction.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.entity.UserMailPO;
import com.chxt.db.transaction.mapper.UserMailMapper;
import org.springframework.stereotype.Repository;


@Repository
public class UserMailRepository extends ServiceImpl<UserMailMapper, UserMailPO> {

    public UserMailPO getByUserId(String userId) {
        return this.lambdaQuery().eq(UserMailPO::getUserId, userId).one();
    }
}
