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
		if (confirm("确定删除该条部署流程吗")) {
			window.location.href = "${pageContext.request.contextPath}/deploymentdel?deploymentId="
					+ id;
		}
	}
</script>
</head>
<body>

	<!--路径导航-->
	<ol class="breadcrumb breadcrumb_nav">
		<li>首页</li>
		<li>流程管理</li>
		<li class="active">查看流程信息</li>
	</ol>
	<!--路径导航-->

	<div class="page-content">

		<form class="form-inline">
			<div class="panel panel-default">
				<div class="panel-heading">部署信息管理列表</div>

				<div class="table-responsive">
					<table class="table table-striped table-hover">
						<thead>
							<tr>
								<th width="15%">部署ID</th>
								<th width="25%">部署名称</th>
								<th width="30%">发布时间</th>
								<!-- <th width="30%">操作</th> -->
							</tr>
						</thead>
						<tbody>
							<c:forEach var="dep" items="${depList}">
								<tr>
									<td>${dep.id}</td>
									<td>${dep.name}</td>
									<td><fmt:formatDate value="${dep.deploymentTime}"
											pattern="yyyy-MM-dd HH:mm:ss" /></td>
									<%-- <td><a href="#" onclick="delConf(${dep.id})"
										class="btn btn-danger btn-xs"><span
											class="glyphicon glyphicon-trash"></span> 删除</a></td> --%>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</form>

		<form class="form-inline">
			<div class="panel panel-default">
				<div class="panel-heading">流程定义信息列表</div>

				<div class="table-responsive">
					<table class="table table-striped table-hover">
						<thead>
							<tr>
								<th width="25%">流程定义ID</th>
								<th width="25%">流程名称</th>
								<th width="10%">流程定义版本</th>
								<th width="15%">部署ID</th>
								<th width="25%">操作</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="processDefinition" step="1"
								items="${processDefinitions}">
								<tr>
									<td>${processDefinition.id}</td>
									<td>${processDefinition.name}</td>
									<td>${processDefinition.version}</td>
									<td>${processDefinition.deploymentId}</td>
									<td><a
										href="${pageContext.request.contextPath}/viewImage?deploymentId=${processDefinition.deploymentId}&imageName=${processDefinition.diagramResourceName}"
										target="_blank" class="btn btn-success btn-xs"><span
											class="glyphicon glyphicon-eye-open"></span> 查看流程图</a></td>
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