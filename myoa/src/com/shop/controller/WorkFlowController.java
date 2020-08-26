package com.shop.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.shop.pojo.Baoxiaobill;
import com.shop.pojo.Employee;
import com.shop.pojo.Leavebill;
import com.shop.service.BaoxiaoService;
import com.shop.service.LeaveBillService;
import com.shop.service.WorkFlowService;
import com.shop.utils.Constants;

@Controller
public class WorkFlowController {

	@Autowired
	private WorkFlowService workFlowService;

	@Autowired
	private LeaveBillService leaveBillService;

	@Autowired
	private BaoxiaoService baoxiaoService;

	// 部署流程
	@RequestMapping(value = "/deployProcess")
	public String deployProcess(MultipartFile fileName, String processName) throws IOException {
		// 传入一个文件输入流和部署名字
		this.workFlowService.deployProcess(fileName.getInputStream(), processName);
		return "redirect:/processDefinitionList";
	}

	// 保存请假信息，开启请假流程
	@RequestMapping(value = "/saveStartLeave")
	public String saveStartLeave(Leavebill leaveBill, HttpSession session) {

		/**
		 * 1.将请假业务信息插入到 leavBill表中
		 * 
		 * 2.启动当前流程
		 * 
		 */
		leaveBill.setLeavedate(new Date());

		/**
		 * 1 表示当前流程正在运行
		 * 
		 * 2 表示当前流程已经全部结束
		 */
		leaveBill.setState(1);

		// 获取session中的employee
		Employee employee = (Employee) session.getAttribute(Constants.GLOBLE_USER_SESSION);

		leaveBill.setUserId(employee.getId());

		// 保存请假单
		this.leaveBillService.saveLeaveBill(leaveBill);

		// 启动流程(待办人)

		// this.workFlowService.startProcess(employee.getName());
		this.workFlowService.startProcess2(leaveBill.getId(), employee.getName());

		return "redirect:/taskList";
	}

	@RequestMapping("/saveStartBaoxiao")
	public String saveStartBaoxiao(Baoxiaobill baoxiaobill, HttpSession session) {
		// 设置当前时间
		baoxiaobill.setCreatdate(new Date());
		// 设置申请人ID
		Employee employee = (Employee) session.getAttribute(Constants.GLOBLE_USER_SESSION);
		baoxiaobill.setUserId(employee.getId().intValue());
		// 更新状态从0变成1（初始录入-->审核中）
		baoxiaobill.setState(1);
		baoxiaoService.saveBaoxiao(baoxiaobill);

		workFlowService.saveStartProcess(baoxiaobill.getId(), employee.getName());

		return "redirect:/taskList";
	}

	// 根据待办人名称查询请假任务，并跳转到前台显示
	@RequestMapping(value = "/taskList")
	public ModelAndView taskList(int flag, HttpSession session) {

		ModelAndView mv = new ModelAndView();

		// 获取session中的employee
		Employee employee = (Employee) session.getAttribute(Constants.GLOBLE_USER_SESSION);
		// 根据待办人查询任务
		List<Task> list = this.workFlowService.findTaskListByName(employee.getName(), flag);

		// 将查询到的list 保存model域中
		mv.addObject("taskList", list);
		mv.addObject("flag", flag);

		// 跳转页面
		mv.setViewName("workflow_task");

		return mv;

	}

	// 根据taskId查询出 请假单信息 和批注列表信息
	@RequestMapping(value = "/viewTaskForm")
	public String findLeaveBill(String taskId, int flag, Model model) {
		Map<String, Object> map = new HashMap<String, Object>();
		String uri = "";
		if (flag == 1) {
			uri = "approve_leave";
			// 获取请假单信息
			Leavebill leavebill = this.workFlowService.findLeaveBillListByTaskId(taskId);
			map.put("bill", leavebill);
		} else {
			uri = "approve_baoxiao";
			// 获取报销单信息
			Baoxiaobill baoxiaobill = this.workFlowService.findBaoxiaoBillListByTaskId(taskId);
			map.put("baoxiaoBill", baoxiaobill);
		}

		// 获取批注信息
		List<Comment> comments = this.workFlowService.findCommentListByTaskId(taskId);

		// 用map接收获取到的信息
		map.put("commentList", comments);
		map.put("taskId", taskId);
		// 把设置到model
		model.addAllAttributes(map);

		return uri;
	}

	// 办理任务
	@RequestMapping(value = "/submitTask")
	public String submitTask(long id, String taskId, String comment, HttpSession session) {

		// 获取session中的员工信息
		String name = ((Employee) session.getAttribute(Constants.GLOBLE_USER_SESSION)).getName();
		// 添加批注，且流程需要往前面推进
		this.workFlowService.submitTask(id, taskId, comment, name);

		return "redirect:/taskList";
	}

	// 查看当前流程图
	@RequestMapping(value = "/viewCurrentImage")
	public String viewCurrentImage(String taskId, ModelMap modelMap) {
		// 查看流程图：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
		ProcessDefinition pd = workFlowService.findProcessDefinitionByTaskId(taskId);
		modelMap.addAttribute("deploymentId", pd.getDeploymentId());
		modelMap.addAttribute("imageName", pd.getDiagramResourceName());

		// 查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中
		Map<String, Object> map = workFlowService.findCoordingByTask(taskId);
		modelMap.addAttribute("acs", map);

		return "viewimage";
	}

	// 查看流程图
	@RequestMapping(value = "/viewImage")
	public void viewImage(String deploymentId, String imageName, HttpServletResponse response) throws IOException {
		// 获取资源文件表（act_ge_bytearray）中资源图片输入流InputStream
		InputStream in = workFlowService.findImageInputStream(deploymentId, imageName);
		// 从response对象获取输出流
		ServletOutputStream out = response.getOutputStream();
		// 将输入流中的数据读取出来，写到输出流中
		int len = -1;
		byte[] b = new byte[1024];
		while ((len = in.read(b)) != -1) {
			out.write(b, 0, len);
		}
		out.close();
		in.close();

	}

	// 查询流程信息
	@RequestMapping(value = "/processDefinitionList")
	public String findProcessDefinitionList(Model model) {
		// 查询部署对象信息，对应表（act_re_deployment）
		List<Deployment> depList = workFlowService.findDeploymentList();
		// 查询流程定义的信息，对应表（act_re_procdef）
		List<ProcessDefinition> processDefinitions = this.workFlowService.findProcessDefinition();

		// 把查询结果放置到上下文对象中
		model.addAttribute("depList", depList);
		model.addAttribute("processDefinitions", processDefinitions);
		return "workflow_list";
	}

	// 删除部署信息（未完善）
	// @RequestMapping(value = "/deploymentdel")
	// public String deploymentdel(String deploymentId) {
	// // 根据id删除部署信息
	// this.workFlowService.delDeployment(deploymentId);
	// return "redirect:/processDefinitionList";
	// }

}
