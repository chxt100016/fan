package com.chxt.db.transaction.repository;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chxt.db.transaction.entity.TaskPO;
import com.chxt.db.transaction.mapper.TaskMapper;

@Repository
public class TaskRepository extends ServiceImpl<TaskMapper, TaskPO> {


	public TaskPO getByTaskId(String taskId) {
		return this.lambdaQuery().eq(TaskPO::getTaskId, taskId).last("limit 1").one();

	}
} 