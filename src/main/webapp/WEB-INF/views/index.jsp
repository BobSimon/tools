<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>后台管理系统</title>
<meta name="keyword" content="">
<meta name="description" content="">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="renderer" content="webkit">
<meta name="Author" content="zifan">
<meta name="copyright" content="胡桃夹子。All Rights Reserved">
<link href="${ctx}/static/css/bootstrap.min.css" rel="stylesheet">
<link href="${ctx}/static/font-awesome/css/font-awesome.css" rel="stylesheet">
<link href="${ctx}/static/css/plugins/toastr/toastr.min.css" rel="stylesheet">
<link href="${ctx}/static/css/plugins/gritter/jquery.gritter.css" rel="stylesheet">
<link href="${ctx}/static/css/plugins/multiselect/tree-multiselect.min.css" rel="stylesheet">
<link href="${ctx}/static/css/animate.css" rel="stylesheet">
<link href="${ctx}/static/css/style.css" rel="stylesheet">
</head>

<body class="fixed-sidebar">
	<div id="wrapper" style="width: 40%; margin-left: 28%;">
			<!-----内容区域---->
			<div class="wrapper wrapper-content animated fadeInRight">
				<div class="ibox float-e-margins ">
					<div class="ibox-content p-t-slg">
						<form name="entity" action="${ctx }/import" class="form-horizontal" enctype="multipart/form-data" method="post"" target="downloadFrom">

							<div class="form-group">
								<label class="col-sm-12 col-md-4 col-lg-2 control-label" for="title"><span class="text-danger">* </span>excel</label>
								<div class="col-sm-12 col-md-7 col-lg-9">
									<input type="file" id="exceFile" name="exceFile" value="" placeholder="请选择excel文件" class="form-control" required>
								</div>
							</div>

							<div class="hr-line-dashed"></div>


							<div class="form-group">
								<div class="col-sm-4 col-sm-offset-2">
									<button class="btn btn-primary" type="submit">
										<i class="fa fa-check"></i> 解析
									</button>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
			<iframe name="downloadFrom" style="display: none;"></iframe>
			<!-----内容结束----->

			<!----版权信息----->
			<div class="footer">
				<div class="pull-right">
					10GB of <strong>250GB</strong> Free.
				</div>
				<div>
					<strong>Copyright</strong> Example Company &copy; 2014-2015
				</div>
			</div>

		</div>
		<!---右侧内容区结束----->


		
	</div>
	<!-- 全局 scripts -->
	<script src="${ctx}/static/js/jquery-2.1.1.js"></script>
	<script src="${ctx}/static/js/bootstrap.js"></script>
	<script src="${ctx}/static/js/wuling.js"></script>
	<script src="${ctx}/static/js/plugins/pace/pace.min.js"></script>
	<script src="${ctx}/static/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>
	<script src="${ctx}/static/js/plugins/metisMenu/jquery.metisMenu.js"></script>
	<script src="${ctx}/static/js/plugins/multiselect/tree-multiselect.min.js"></script>
	<!-- 插件 scripts -->
	<script src="${ctx}/static/js/plugins/toastr/toastr.min.js" async></script>
	<script src="${ctx}/static/js/plugins/fileupload/ajaxfileupload.js"></script>
	<script>
    var _ctx = '${ctx}';
    $(document).ready(function() {

      $(".submitForm").click(function() {
        $.ajaxFileUpload({
          url: _ctx + '/import', // 用于文件上传的服务器端请求地址
          type: 'post',
          secureuri: false, // 一般设置为false
          fileElementId: "exceFile", // 文件上传空间的id属性 <input type="file"
          dataType: 'json', // 返回值类型 一般设置为json
          success: function(data, status) {// 服务器成功响应处理函数
            alert(data['msg']);
          },
          error: function(data, status, e) {// 服务器响应失败处理函数
            alert(e);
          }

        });
      });

    });
  </script>
</body>
</html>
