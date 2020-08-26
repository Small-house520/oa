package com.shop.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shop.pojo.Employee;
import com.shop.service.EmployeeService;
import com.shop.utils.Constants;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;

@Controller
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	// 生成验证码
	@RequestMapping(value = "/checkcode")
	public void checkcode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 定义图形验证码的长、宽、验证码字符数、干扰线宽度
		ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(112, 40, 4, 3);
		// 得到code
		String code = captcha.getCode();
		System.out.println(code);
		// 将验证码存入session
		request.getSession().setAttribute("checkCode_session", code);
		// 图形验证码写出，可以写出到文件，也可以写出到流
		// captcha.write("d:/shear.png");
		// 输出流
		ServletOutputStream outputStream = response.getOutputStream();
		// 读写输出流
		captcha.write(outputStream);
		// 关闭输出流
		outputStream.close();
	}

	// 跳转到登录页面
	@RequestMapping(value = "/login")
	public String login(HttpServletRequest request, Model model) {
		// ModelAndView view = new ModelAndView();
		// 获取cookie
		Cookie[] cookies = request.getCookies();
		String username = null;
		String password = null;
		if (cookies != null && cookies.length > 0) {
			// 取出cookie中存储的用户名和密码
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("username")) {
					username = cookie.getValue();
				}
				if (cookie.getName().equals("password")) {
					password = cookie.getValue();
				}
			}

		}

		// 用map接收用户名和密码
		Map<String, String> map = new HashMap<>();
		map.put("username", username);
		map.put("password", password);
		// 把map设置到model
		model.addAllAttributes(map);
		return "login";
		// view.addAllObjects(map);
		// view.setViewName("login");
		// return view;
	}

	// 登录验证
	@RequestMapping(value = "/loginCheck")
	public String loginCheck(String username, String password, String checkCode, String rememberMe, HttpSession session,
			HttpServletResponse response, Model model) {
		// 先获取生成的验证码
		String checkCode_session = (String) session.getAttribute("checkCode_session");
		// 删除session中存储的验证码
		session.removeAttribute("checkCode_session");

		// 先判断验证码是否正确，忽略大小写比较
		if (checkCode_session != null && checkCode_session.equalsIgnoreCase(checkCode)) {
			Employee employee = this.employeeService.isLogin(username);
			if (employee != null) {
				if (employee.getPassword().equals(password)) {
					// 一定查询到了某个用户
					session.setAttribute(Constants.GLOBLE_USER_SESSION, employee);

					// 把账号密码保存到cookie
					if ("true".equals(rememberMe)) {
						// 创建cookie
						Cookie cook1 = new Cookie("username", username);
						Cookie cook2 = new Cookie("password", password);
						// 设置生命周期
						cook1.setMaxAge(60 * 60 * 24);
						cook2.setMaxAge(60 * 60 * 24);
						// 服务器端创建对象后,发送到浏览器端
						response.addCookie(cook1);
						response.addCookie(cook2);
					}
					model.addAttribute("name", username);
					return "index";
				} else {
					model.addAttribute("errorMsg", "账号或密码错误");
					return "redirect:/login";
				}
			} else {
				model.addAttribute("errorMsg", "账号或密码错误");
				return "redirect:/login";
			}
		} else {
			model.addAttribute("errorMsg", "验证码错误");
			return "redirect:/login";
		}

	}

	// 注销登录
	@RequestMapping(value = "/logout")
	public String logout(HttpSession session) {
		// 清除session 信息
		session.invalidate();
		// 重定向到login.jsp
		return "redirect:/login";
	}

	// 查询员工信息
	@RequestMapping(value = "/employee")
	public String findEmployeeList(Model model) {
		List<Employee> employees = this.employeeService.findEmployeeList();
		model.addAttribute("employees", employees);
		return "employeelist";
	}

	// 跳转到添加员工页面
	@RequestMapping(value = "/employeeadd")
	public String employeeadd(Model model) {
		List<Employee> employees = this.employeeService.findEmployeeList();
		model.addAttribute("employees", employees);

		return "employeeadd";
	}

	// 添加员工
	@RequestMapping(value = "/saveEmployee")
	public String saveEmployee(Employee employee) {
		this.employeeService.saveEmployee(employee);
		return "redirect:/employee";
	}

	// 删除员工信息
	@RequestMapping(value = "/employeedelete")
	public String employeedelete(Long id) {
		this.employeeService.deleteEmployee(id);
		return "redirect:/employee";
	}

	// 跳转到修改员工页面
	@RequestMapping(value = "/employeeedit")
	public String employeeedit(Long id, Model model, HttpSession session) {
		Employee employee = null;
		// 获取要修改员工的信息
		if (id == 0) {
			id = ((Employee) session.getAttribute(Constants.GLOBLE_USER_SESSION)).getId();
		}
		employee = this.employeeService.findEmployee(id);

		List<Employee> employees = this.employeeService.findEmployeeList();

		// 把查询结果传递给model
		Map<String, Object> map = new HashMap<>();
		map.put("employee", employee);
		map.put("employees", employees);
		model.addAllAttributes(map);

		return "employeeedit";
	}

	// 修改员工信息
	@RequestMapping(value = "/updateEmployee")
	public String updateEmployee(Employee employee) {
		this.employeeService.updateEmployee(employee);
		return "redirect:/employee";
	}
}
