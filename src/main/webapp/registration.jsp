<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
    <title></title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/social-buttons-3.css"/>
</head>
<body>
<div class="page-header">
    <h1><spring:message code="label.user.login.page.title"/></h1>
</div>
<!-- 
    If the user is anonymous (not logged in), show the login form
    and social sign in buttons.
-->
<sec:authorize access="isAnonymous()">
    <!-- Login form -->
    <div class="panel panel-default">
        <div class="panel-body">
            <h2><spring:message code="label.login.form.title"/></h2>
            <!--
                Error message is shown if login fails.
            -->
            <c:if test="${param.error eq 'bad_credentials'}">
                <div class="alert alert-danger alert-dismissable">
                    <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                    <spring:message code="text.login.page.login.failed.error"/>
                </div>
            </c:if>
            <!-- Specifies action and HTTP method -->
            <form action="${pageContext.request.contextPath}/login/authenticate" method="POST" role="form">
                <!-- Add CSRF token -->
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="row">
                    <div id="form-group-email" class="form-group col-lg-4">
                        <label class="control-label" for="user-email"><spring:message code="label.user.email"/>:</label>
                        <!-- Add username field to the login form -->
                        <input id="user-email" name="username" type="text" class="form-control"/>
                    </div>
                </div>
 
                <div class="row">
                    <div id="form-group-password" class="form-group col-lg-4">
                        <label class="control-label" for="user-password"><spring:message code="label.user.password"/>:</label>
                        <!-- Add password field to the login form -->
                        <input id="user-password" name="password" type="password" class="form-control"/>
                    </div>
                </div>
                <div class="row">
                    <div class="form-group col-lg-4">
                        <!-- Add submit button -->
                        <button type="submit" class="btn btn-default"><spring:message code="label.user.login.submit.button"/></button>
                    </div>
                </div>
            </form>
            <div class="row">
                <div class="form-group col-lg-4">
                    <!-- Add create user account link -->
                    <a href="${pageContext.request.contextPath}/user/register"><spring:message code="label.navigation.registration.link"/></a>
                </div>
            </div>
        </div>
    </div>
    <!-- Social Sign In Buttons -->
    <div class="panel panel-default">
        <div class="panel-body">
            <h2><spring:message code="label.social.sign.in.title"/></h2>
            <div class="row social-button-row">
                <div class="col-lg-4">
                    <!-- Add Facebook sign in button -->
                    <a href="${pageContext.request.contextPath}/auth/facebook"><button class="btn btn-facebook"><i class="icon-facebook"></i> | <spring:message code="label.facebook.sign.in.button"/></button></a>
                </div>
            </div>
            <div class="row social-button-row">
                <div class="col-lg-4">
                    <!-- Add Twitter sign in Button -->
                    <a href="${pageContext.request.contextPath}/auth/twitter"><button class="btn btn-twitter"><i class="icon-twitter"></i> | <spring:message code="label.twitter.sign.in.button"/></button></a>
                </div>
            </div>
        </div>
    </div>
</sec:authorize>
<!-- 
    If the user is already authenticated, show a help message instead
    of the login form and social sign in buttons.
-->
<sec:authorize access="isAuthenticated()">
    <p><spring:message code="text.login.page.authenticated.user.help"/></p>
</sec:authorize>
</body>
</html>