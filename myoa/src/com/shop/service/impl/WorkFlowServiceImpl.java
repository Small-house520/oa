package com.shop.service.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.mapper.BaoxiaobillMapper;
import com.shop.mapper.LeavebillMapper;
import com.shop.pojo.Baoxiaobill;
import com.shop.pojo.Leavebill;
import com.shop.pojo.LeavebillExample;
import com.shop.pojo.LeavebillExample.Criteria;
import com.shop.service.WorkFlowService;
import com.shop.utils.Constants;

@Service
public class WorkFlowServiceImpl implements WorkFlowService {

	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	// @Autowired
	// private FormService formService;
	// @Autowired
	// private HistoryService historyService;
	@Autowired
	private LeavebillMapper leavebillMapper;

	@Autowired
	private BaoxiaobillMapper baoxiaobillMapper;

	// 部署流程
	@Override
	public void deployProcess(InputStream in, String processName) {

		ZipInputStream inputStream = new ZipInputStream(in);

		this.repositoryService.createDeployment().name(processName).addZipInputStream(inputStream).deploy();
	}

	// 开启流程
	@Override
	public void startProcess(String name) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", name);

		this.runtimeService.startProcessInstanceByKey(Constants.Leave_KEY, map);
	}

	// 保存并启动流程实例
	@Override
	public void startProcess2(Long id, String name) {
		// 请假业务和 流程信息进行关联 BUSSINSS_KEY
		Map<String, Object> map = new HashMap<>();
		map.put("userId", name);

		// 定义规则
		String BUSSINSS_KEY = Constants.Leave_KEY + "." + id;
		map.put("objId", BUSSINSS_KEY);

		this.runtimeService.startProcessInstanceByKey(Constants.Leave_KEY, BUSSINSS_KEY, map);

	}

	@Override
	public void saveStartProcess(Integer id, String name) {
		// 使用当前对象获取到流程定义的key（对象的名称就是流程定义的key）
		String key = Constants.BAOXIAO_KEY;
		/**
		 * 从Session中获取当前任务的办理人，使用流程变量设置下一个任务的办理人 inputUser是流程变量的名称， 获取的办理人是流程变量的值
		 */
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("inputUser", name);// 表示惟一用户

		// 格式：baoxiao.id的形式（使用流程变量）
		String objId = key + "." + id;
		map.put("objId", objId);
		// 使用流程定义的key，启动流程实例，同时设置流程变量，同时向正在执行的执行对象表中的字段BUSINESS_KEY添加业务数据，同时让流程关联业务
		runtimeService.startProcessInstanceByKey(key, objId, map);
	}

	// 根据待办人名称查询任务
	@Override
	public List<Task> findTaskListByName(String name, int flag) {

		// List<Task> list =
		// this.taskService.createTaskQuery().taskAssignee(name).orderByTaskCreateTime().desc().list();
		String procDeResName = "baoxiaoprocess";
		if (flag == 1) {
			procDeResName = "LeaveBillProcessTest";
		}
		ProcessDefinition processDefinition = this.repositoryService.createProcessDefinitionQuery()
				.processDefinitionResourceNameLike("%" + procDeResName + "%").singleResult();
		if (processDefinition != null) {
			String id = processDefinition.getId();
			List<Task> list = this.taskService.createTaskQuery().taskAssignee(name).processDefinitionId(id)
					.orderByTaskCreateTime().desc().list();
			return list;
		}
		return null;
	}

	// 根据taskId获取请假单信息
	@Override
	public Leavebill findLeaveBillListByTaskId(String taskId) {
		// 先根据任务id取出task任务
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();

		// 再根据任务中流程实例id 取出 流程实例对象
		ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();

		// 然后再从流程实例中取出bussiness_key
		String businessKey = processInstance.getBusinessKey();

		// 然后从bussinessKey中切割出 leavBill的主键id
		String leaveid = "";
		if (businessKey != null && !"".equals(businessKey)) {
			leaveid = businessKey.split("\\.")[1];
		}

		// 根据leaveid 查询出当前请假单信息
		Leavebill leavebill = this.leavebillMapper.selectByPrimaryKey(Long.parseLong(leaveid));

		return leavebill;
	}

	// 根据taskId获取请假单信息
	@Override
	public Baoxiaobill findBaoxiaoBillListByTaskId(String taskId) {
		// 先根据任务id取出task任务
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();

		// 再根据任务中流程实例id 取出 流程实例对象
		ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();

		// 然后再从流程实例中取出bussiness_key
		String businessKey = processInstance.getBusinessKey();

		// 然后从bussinessKey中切割出 leavBill的主键id
		String baoxiaoid = "";
		if (businessKey != null && !"".equals(businessKey)) {
			baoxiaoid = businessKey.split("\\.")[1];
		}

		// 根据leaveid 查询出当前请假单信息
		Baoxiaobill baoxiaobill = this.baoxiaobillMapper.selectByPrimaryKey(Integer.parseInt(baoxiaoid));

		return baoxiaobill;
	}

	// 根据taskId获取批注信息
	@Override
	public List<Comment> findCommentListByTaskId(String taskId) {
		// 先根据任务id取出task任务
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();

		// 根据taskId对应的流程实例id 查询出当前批注信息
		List<Comment> list = this.taskService.getProcessInstanceComments(task.getProcessInstanceId());

		return list;
	}

	// 提交请假申请
	@Override
	public void submitTask(long id, String taskId, String comment, String name) {
		/*
		 * 在完成之前，添加一个批注信息，向act_hi_comment表中添加数据，
		 * 
		 * 用于记录对当前申请人的一些审核信息
		 * 
		 * 使用任务ID，查询任务对象，获取流程流程实例ID
		 */
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();

		// 获取流程实例ID
		String instanceId = task.getProcessInstanceId();
		/**
		 * 注意：添加批注的时候，由于Activiti底层代码是使用： String userId =
		 * Authentication.getAuthenticatedUserId(); CommentEntity comment = new
		 * CommentEntity(); comment.setUserId(userId);
		 * 所有需要从Session中获取当前登录人，作为该任务的办理人（审核人），对应act_hi_comment表中的User_ID的字段，不过不添加审核人，该字段为null
		 * 所以要求，添加配置执行使用Authentication.setAuthenticatedUserId();添加当前任务的审核人
		 */
		// 加当前任务的审核人
		Authentication.setAuthenticatedUserId(name);
		// 添加批注
		taskService.addComment(taskId, instanceId, comment);
		// 完成任务
		taskService.complete(taskId);

		// 获取流程实例
		ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery().processInstanceId(instanceId)// 使用流程实例ID查询
				.singleResult();

		if (processInstance == null) {
			Leavebill leavebill = leavebillMapper.selectByPrimaryKey(id);
			leavebill.setState(2);
			leavebillMapper.updateByPrimaryKey(leavebill);
		}

	}

	@Override
	public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
		// 使用任务ID，查询任务对象
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		// 获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 查询流程定义的对象
		ProcessDefinition pd = this.repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();

		return pd;
	}

	@Override
	public Map<String, Object> findCoordingByTask(String taskId) {
		// 存放坐标
		Map<String, Object> map = new HashMap<>();
		// 使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		// 获取流程定义的ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 获取流程定义的实体对象（对应.bpmn文件中的数据）
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) this.repositoryService
				.getProcessDefinition(processDefinitionId);
		// 流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
		ProcessInstance pi = this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
				.singleResult();
		// 获取当前活动的ID
		String activityId = pi.getActivityId();
		// 获取当前活动对象
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		// 获取坐标
		map.put("x", activityImpl.getX());
		map.put("y", activityImpl.getY());
		map.put("width", activityImpl.getWidth());
		map.put("height", activityImpl.getHeight());

		return map;
	}

	// 使用部署对象ID和资源图片名称，获取图片的输入流
	@Override
	public InputStream findImageInputStream(String deploymentId, String imageName) {
		return this.repositoryService.getResourceAsStream(deploymentId, imageName);
	}

	// 根据员工id查询出请假单信息
	@Override
	public List<Leavebill> findBaoxiaoBill(long id) {
		LeavebillExample leavebillExample = new LeavebillExample();
		Criteria criteria = leavebillExample.createCriteria();
		criteria.andUserIdEqualTo(id);
		List<Leavebill> list = this.leavebillMapper.selectByExample(leavebillExample);
		return list;
	}

	// 查询流程信息
	@Override
	public List<ProcessDefinition> findProcessDefinition() {
		// 返回查询流程定义的对象集合
		return this.repositoryService.createProcessDefinitionQuery().list();
	}

	// 查询部署对象信息，对应表（act_re_deployment）
	@Override
	public List<Deployment> findDeploymentList() {
		return this.repositoryService.createDeploymentQuery().list();
	}

	@Override
	public Task findTaskByBussinessKey(String bUSSINESS_KEY) {
		Task task = this.taskService.createTaskQuery().processInstanceBusinessKey(bUSSINESS_KEY).singleResult();
		return task;
	}

	// 根据id删除部署信息（未完善）
	// @Override
	// public void delDeployment(String deploymentId) {
	// this.repositoryService.deleteDeployment(deploymentId);
	// }

}
