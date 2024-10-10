<%@ page import="application.dto.UserDTO" %>
<%@ page import="application.dto.OrderDTO" %>
<%@ page import="application.dto.OrderProductDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="presentation.Controller" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    session.setAttribute("orders", Controller.getAllOrders((UserDTO) session.getAttribute("user")));
%>

<html>
<head>
    <title>Orders</title>
</head>
<body>
<h1>Orders</h1>
<a href="index.jsp">&lt; Go back</a>
<br>
<br>
<br>

<table border="1">
    <thead>
    <th>Order id</th>
    <th>Order date</th>
    <th>Pack date</th>
    <th>Ship date</th>
    <th>Products</th>
    </thead>

    <c:forEach items="${sessionScope.orders}" var="order">
        <tr>
            <td>${order.id}</td>
            <td>${order.ordered}</td>

            <!-- Packed field -->
            <c:if test="${order.packed == null && (sessionScope.user.permissionLevel == 'Admin' || sessionScope.user.permissionLevel == 'Worker')}">
                <td>
                    <form action="${pageContext.request.contextPath}/controller?page=orders&action=orderPack" method="POST">
                        <input type="text" style="display:none" name="orderId" value="${order.id}">
                        <input type="submit" value="Pack"/>
                    </form>
                </td>
            </c:if>
            <c:if test="${order.packed == null && !(sessionScope.user.permissionLevel == 'Admin' || sessionScope.user.permissionLevel == 'Worker')}">
                <td>
                    ---
                </td>
            </c:if>
            <c:if test="${order.packed != null}">
                <td>${order.packed}</td>
            </c:if>

            <c:if test="${order.packed != null && order.shipped == null && (sessionScope.user.permissionLevel == 'Admin' || sessionScope.user.permissionLevel == 'Worker')}">
                <td>
                    <form action="${pageContext.request.contextPath}/controller?page=orders&action=orderShip" method="POST">
                        <input type="text" style="display:none" name="orderId" value="${order.id}">
                        <input type="submit" value="Ship"/>
                    </form>
                </td>
            </c:if>
            <c:if test="${order.shipped == null && (order.packed == null || !(sessionScope.user.permissionLevel == 'Admin' || sessionScope.user.permissionLevel == 'Worker'))}">
                <td>
                    ---
                </td>
            </c:if>
            <c:if test="${order.packed != null && order.shipped != null}">
                <td>${order.shipped}</td>
            </c:if>
            <c:if test="${order.packed == null && order.shipped == null && (sessionScope.user.permissionLevel == 'Admin' || sessionScope.user.permissionLevel == 'Worker')}">
                <td> --- </td>
            </c:if>

            <td>
                <table border="1">
                    <thead>
                    <th>Category</th>
                    <th>Name</th>
                    <th>Amount</th>
                    </thead>
                    <c:forEach items="${order.products}" var="product">
                        <tr>
                            <td>${product.category}</td>
                            <td>${product.productName}</td>
                            <td>${product.quantity}</td>
                        </tr>
                    </c:forEach>
                </table>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
