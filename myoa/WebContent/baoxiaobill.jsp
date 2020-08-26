<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>流程管理</title>

<!-- Bootstrap -->
<link href="bootstrap/css/bootstrap.css" rel="stylesheet">
<link href="css/content.css" rel="stylesheet">
<style type="text/css">
th, td {
	text-align: center;
}
</style>


<script src="js/jquery.min.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript">
	//确认删除函数
	function delConf(id) {
		if (confirm("请确定是否删除该条报销记录")) {
			window.location.href = "${pageContext.request.contextPath}/baoxiaobilldel?id="
					+ id;
		}
	}
</script>
</head>
<body>

	<!--路径导航-->
	<ol class="breadcrumb breadcrumb_nav">
		<li>首页</li>
		<li>报销管理</li>
		<li class="active">我的报销单</li>
	</ol>
	<!--路径导航-->

	<div class="page-content">
		<form class="form-inline">
			<div class="panel panel-default">
				<div class="panel-heading">报销单列表</div>

				<div class="table-responsive">
					<table class="table table-striped table-hover">
						<thead>
							<tr>
								<th width="5%">ID</th>
								<th width="15%">标题</th>
								<th width="10%">报销金额</th>
								<th width="20%">备注</th>
								<th width="15%">时间</th>
								<th width="10%">状态</th>
								<th width="25%">操作</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="bill" items="${baoxiaoList}">
								<tr>
									<td>${bill.id}</td>
									<td>${bill.title}</td>
									<td>${bill.money}</td>
									<td>${bill.remark}</td>
									<td><fmt:formatDate value="${bill.creatdate}"
											pattern="yyyy-MM-dd HH:mm:ss" /></td>
									<td><c:choose>
											<c:when test="${bill.state==0}">
				        	   		初始录入
				        	   </c:when>
											<c:when test="${bill.state==1}">
				        	  		审核中
				        	   </c:when>
											<c:otherwise>
				        	   	    审核完成
				        	   </c:otherwise>
										</c:choose></td>
									<td><c:choose>
											<c:when test="${bill.state==0}">
												<a
													href="${pageContext.request.contextPath }/leaveBillAction_input?id=${bill.id}"
													class="btn btn-info btn-xs"><span
													class="glyphicon glyphicon-edit"></span> 修改</a>
												<a href="leaveBillAction_delete?id=${bill.id}"
													class="btn btn-danger btn-xs"><span
													class="glyphicon glyphicon-remove"></span> 删除</a>
												<a
													href="${pageContext.request.contextPath }/workflowAction_startProcess?id=${bill.id}"
													class="btn btn-success btn-xs"><span
													class="glyphicon glyphicon-plus"></span> 申请请假</a>
											</c:when>
											<c:when test="${bill.state==1}">
												<a
													href="${pageContext.request.contextPath }/workflowAction_viewHisComment?id=${bill.id}"
													class="btn btn-success btn-xs"><span
													class="glyphicon glyphicon-eye-open"></span> 查看审核记录</a>&nbsp;&nbsp;
												<a
													href="${pageContext.request.contextPath}/viewCurrentImageByBill?billId=${bill.id}"
													target="_blank" class="btn btn-success btn-xs"><span
													class="glyphicon glyphicon-eye-open"></span> 查看当前流程图</a>
											</c:when>
											<c:otherwise>
												<a
													href="${pageContext.request.contextPath }/workflowAction_viewHisComment?id=${bill.id}"
													class="btn btn-success btn-xs"><span
													class="glyphicon glyphicon-eye-open"></span> 查看审核记录</a>&nbsp;&nbsp;
												<a href="#" onclick="delConf(${bill.id})"
													class="btn btn-danger btn-xs"><span
													class="glyphicon glyphicon-remove"></span> 删除</a>
											</c:otherwise>
										</c:choose></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</form>

	</div>
</body>
</html>