package com.shop.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import com.shop.pojo.Baoxiaobill;
import com.shop.pojo.Leavebill;

public interface WorkFlowService {
	public void deployProcess(InputStream in, String processName);

	// 保存并启动流程实例
	public void startProcess(String name);

	// 保存并启动流程实例
	public void startProcess2(Long id, String name);

	public List<Task> findTaskListByName(String name, int flag);

	public Leavebill findLeaveBillListByTaskId(String taskId);

	public Baoxiaobill findBaoxiaoBillListByTaskId(String taskId);

	public List<Comment> findCommentListByTaskId(String taskId);

	// 提交请假申请
	public void submitTask(long id, String taskId, String comment, String name);

	public ProcessDefinition findProcessDefinitionByTaskId(String taskId);

	public Map<String, Object> findCoordingByTask(String taskId);

	public InputStream findImageInputStream(String deploymentId, String imageName);

	public List<Leavebill> findBaoxiaoBill(long id);

	public List<ProcessDefinition> findProcessDefinition();

	public List<Deployment> findDeploymentList();

	public void saveStartProcess(Integer id, String name);

	public Task findTaskByBussinessKey(String bUSSINESS_KEY);

	// public void delDeployment(String id);

}
