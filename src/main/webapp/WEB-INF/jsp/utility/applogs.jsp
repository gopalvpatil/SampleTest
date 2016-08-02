<%@ page import="com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<jsp:useBean id="now" class="java.util.Date" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script	src="<%=request.getContextPath()%>/static/thirdparty/notify/js/notify.min.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/positivepay/js/applogs.js" type="text/javascript"></script>
<style>
    .alert {
        padding: 0px 0px 0px 10px;
        margin-bottom: 5px;
        border-radius: 5px;
    }
</style>
<div>
    <div class="positivepay-column-spacer">
        <div class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">Application Log | <span>Current as of <fmt:formatDate value="${now}" type="both" dateStyle="long" timeStyle="short"/></span></div>
            </div>
            <div class="panel-body">
                <div class="form-group">
                    <select id="logLevel" name="logLevel">
                        <option value="">Select log level...</option>
                        <option value="INFO">INFO</option>
                        <option value="DEBUG">DEBUG</option>
                        <option value="WARN">WARN</option>
                        <option value="ERROR">ERROR</option>
                        <option value="TRACE">TRACE</option>
                        <option value="ALL">ALL</option>
                    </select>
                </div>
                <div id="ajax-loader-appLogs"></div>
                <div id="error-loading-appLogs" class="alert alert-danger alert-dismissable hidden">
                    <button type="button" class="close" data-dismiss="alert"
                            aria-hidden="true">&times;</button>
                    <img alt="error loading account info"
                         src="<%=request.getContextPath()%>/static/positivepay/images/icons/red-alert-sprite.png"/>
                    Error while loading account information. Please try again later.
                </div>
                <div id="appLogs" class="hidden">
                </div>
                <div class="ajax-loader-autoscroll"></div>
                <div id="noAppLogs" class="alert alert-info alert-dismissable hidden">
                    <button type="button" class="close" data-dismiss="alert"
                            aria-hidden="true">&times;</button>
                    <img alt="No Account Info"
                         src="<%=request.getContextPath()%>/static/positivepay/images/icons/caution-sprite.png" />
                    No logs to display.
                </div>
            </div>
        </div>
    </div>
    /fetchErrorStackByTransactionId?transactionid=<%=TransactionIdThreadLocal.get()%> <br />
    /fetchByTransactionId?transactionid=<%=TransactionIdThreadLocal.get()%> <br />
    /fetchByPattern?pattern=anyPattern <br />
</div>