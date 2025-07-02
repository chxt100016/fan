package com.chxt.gateway.transaction;

import org.springframework.stereotype.Component;

import com.chxt.db.transaction.entity.TaskPO;
import com.chxt.db.transaction.repository.TaskRepository;
import com.chxt.domain.transaction.parser.PasswordHelper;

import jakarta.annotation.Resource;

@Component
public class PasswordHelperGateWay implements PasswordHelper{

	@Resource
	private TaskRepository taskRepository;

	@Override
	public String getPassword(String channel, Long timeStamp, String fileName) {
		String taskId = channel + ":" + timeStamp;
		TaskPO exist = this.taskRepository.getByTaskId(taskId);
		if (exist != null && "complete".equals(exist.getStatus())) {
			return exist.getData();
		} else {
			TaskPO taskPO = TaskPO.builder().taskId(taskId).status("init").remark(fileName).build();
			this.taskRepository.save(taskPO);
			return null;
		}
	}



}
