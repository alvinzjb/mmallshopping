<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/7/12
  Time: 9:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Hello World!</title>
  </head>
  <body>
 <form name="form1" action="${pageContext.request.contextPath}/manager/product/onload.do" method="post" enctype="Multipart/form-data">
   <input type="file" name="upload_file" />
   <input type="submit" name="文件上传"/>
 </form>
  </body>
</html>
