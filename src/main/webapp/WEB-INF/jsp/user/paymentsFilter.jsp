<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/static/thirdparty/bootstrap/css/bootstrap-multiselect.css" type="text/css" />
<script src="<%=request.getContextPath()%>/static/positivepay/js/fieldValidator.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/thirdparty/notify/js/notify.min.js" type="text/javascript"></script>
<script	src="<%=request.getContextPath()%>/static/thirdparty/bootstrap/js/bootstrap-multiselect.js" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/static/positivepay/js/paymentsFilter.js" type="text/javascript"></script>	
<script src="<%=request.getContextPath()%>/static/positivepay/js/angular/angular.min.js" type="text/javascript"></script>
<div class="row" id="paymentsAndItemsController" ng-controller="paymentsAndItemsController">
	<div class="positivepay-column-spacer">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<div class="panel-title">Payments and Items</div>
			</div>
			<div class="panel-body">				
				<div class="form-group">
					<label for="lookFor">Look for</label>
					<select id="lookFor" name="lookFor" onchange="displaySearchFilters(this.value);">
						<option value="">Select..</option>
						<option value="Payments">Payments</option>
						<option value="Items">Items</option>
					</select>
				</div>
				<div class="form-group">
					<label for="selectedfilter">Selected Filter</label><span class="pull-right pp-ajax-loader {{waitprocess ? 'show' : 'hide'}}">Please wait...</span>
					<select id="selectedfilter" name="selectedfilter" ng-model="selectedfilter"
										ng-options="selectedfilter.filterName for selectedfilter in paymentFilters" ng-change="applyFilterToTable(this)">
						<option value="">Select Filter</option>
					</select>
					<span class="pp-ajax-loader {{waitfilter ? 'show' : 'hide'}}">Please wait...</span>
				</div>
				<div id="ajax-loader-filter"></div>
				<div id="filterTbl" class="table-responsive" style="display:none">
					<table id="filterSearchTbl">
						<tr id="itemTypeRow" class="tblRow itemListCls">
							<td>
								<label class="control-label" for="itemType">Item Type</label>
							</td>
							<td>
								<select id="itemTypeSearchOption" name="itemTypeSearchOption" style="width: 125px;">
									<option value="contains">contains</option>
									<option value="doesNotContain">does not contain</option>
									<option value="isOneOf">is one of</option>
									<option value="isNotOneOf">is not one of</option>
								</select>
							</td>
							<td>
								<select id="itemType" name="itemType" ng-model="itemType" size="3" style="width: 300px;" multiple class="form-control" >
								</select>
							</td>
						</tr>
						
						<tr id="row1" class="tblRow">
							<td>
								<label class="control-label" for="bank">Bank</label>
							</td>
							<td>
								<select id="bankSearchOption" name="bankSearchOption"style="width: 125px;">
									<option value="contains">contains</option>
									<option value="doesNotContain">does not contain</option>
									<option value="isOneOf">is one of</option>
									<option value="isNotOneOf">is not one of</option>
								</select>
							</td>
							<td>
								<select id="bank" name="bank" ng-model="bank" size="3" style="width: 300px;" multiple class="form-control" >
								</select>
							</td>
						</tr>
						<tr id="row2" class="tblRow">
							<td>
								<label class="control-label" for="comp">Company</label>
							</td>
							<td>
								<select id="compSearchOption" name="compSearchOption" style="width: 125px;">
									<option value="contains">contains</option>
									<option value="doesNotContain">does not contain</option>
									<option value="isOneOf">is one of</option>
									<option value="isNotOneOf">is not one of</option>
								</select>
							</td>
							<td>
								<select id="comp" name="comp" ng-model="comp" onDropdownHide="test()" size="3" style="width: 300px;" multiple class="form-control" >
								</select>
								<!--<select id="comp" name="comp" ng-model="comp"
								ng-options="company.name for company in fiteredCompany"
								ng-change="getAccountsByCompany();addSelectionToFilterMap()" style="width: 300px;">
								<option value="">Select Company</option>-->
							</select>
							</td>
						</tr>
						<tr id="row3" class="tblRow">
							<td>
								<label class="control-label" for="accnt">Account No.</label>
							</td>
							<td>
								<select id="accntSearchOption" name="accntSearchOption"style="width: 125px;">
									<option value="contains">contains</option>
									<option value="doesNotContain">does not contain</option>
									<option value="isOneOf">is one of</option>
									<option value="isNotOneOf">is not one of</option>
								</select>
							</td>
							<td>
								<select id="accnt" name="accnt" ng-model="accnt" size="3" style="width: 300px;" multiple class="form-control">
								</select>
								<!--<select id="accnt" name="accnt" ng-model="accnt"
									 ng-options="accnt for accnt in accounts" ng-change="addSelectionToFilterMap()" style="width: 300px;">
									<option value="">Select Account</option>
								</select>-->
							</td>
						</tr>
						
						<tr id="row4" class="tblRow">
							<td>
								<label class="control-label" for="snum">Check Number</label>
							</td>
							<td>
								<select id="snumSearchOption" name="snumSearchOption" onchange="toggleNumericFields('snum');" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isGreaterThan">is greater than</option>
									<option value="isLessThan">is less than</option>
									<option value="isNotGreaterThan">is not greater than</option>
									<option value="isNotLessThan">is not less than</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<input type="text" id="snum"
									name="snum" ng-model="snum" ng-model-onblur style="width: 300px;"/>
							</td>
							
							<td>
								<input type="text" id="snumTo"
									name="snumTo" class="hidden" ng-model="snumTo" ng-model-onblur style="width: 300px;"/>
							</td>
							
						</tr>
						
						<tr id="itemAmountRow" class="tblRow itemListCls">
							<td>
								<label class="control-label" for="itemAmount">Item Amount</label>
							</td>
							<td>
								<select id="itemAmountSearchOption" name="itemAmountSearchOption" onchange="toggleNumericFields('itemAmount');" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isGreaterThan">is greater than</option>
									<option value="isLessThan">is less than</option>
									<option value="isNotGreaterThan">is not greater than</option>
									<option value="isNotLessThan">is not less than</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<input type="text" id="itemAmount"
									name="itemAmount" ng-model="itemAmount" ng-model-onblur style="width: 300px;"/>
							</td>
							<td>
								<input type="text" id="itemAmountTo"
									class="hidden" name="itemAmountTo" ng-model="itemAmountTo" ng-model-onblur style="width: 300px;"/>
							</td>
						</tr>
						
						<tr id="itemDateRow" class="tblRow itemListCls">
							<td>
								<label class="control-label" for="itemDate">Item Date</label>
							</td>
							<td>
								<select id="itemDateSearchOption" name="itemDateSearchOption" 
									onchange="toggleDate('itemDate');" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isAfter">is after</option>
									<option value="isBefore">is before</option>
									<option value="isNotAfter">is not after</option>
									<option value="isNotBefore">is not before</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<div id="itemDateFromDiv" class="date-input">
									<input type="text" id="itemDateFrom" name="itemDateFrom" ng-model="itemDateFrom" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>
							<td>
								<div id="itemDateToDiv" class="date-input hidden">
									<input type="text" id="itemDateTo" name="itemDateTo" ng-model="itemDateTo" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>
						</tr>
						
						<tr id="createMethodRow" class="tblRow itemListCls">
							<td>
								<label class="control-label" for="createMethod">Created Method</label>
							</td>
							<td>
								<select id="createMethodSearchOption" name="createMethodSearchOption" style="width: 125px;">
									<option value="contains">contains</option>
									<option value="doesNotContain">does not contain</option>
									<option value="isOneOf">is one of</option>
									<option value="isNotOneOf">is not one of</option>
								</select>
							</td>
							<td>
								<select id="createMethod" name="createMethod" ng-model="createMethod" size="3" style="width: 300px;" multiple class="form-control" >
								</select>
							</td>
						</tr>
						
						<tr id="createDateRow" class="tblRow itemListCls">
							<td>
								<label class="control-label" for="createDate">Created Date</label>
							</td>
							<td>
								<select id="createDateSearchOption" name="createDateSearchOption" 
									onchange="toggleDate('createDate');" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isAfter">is after</option>
									<option value="isBefore">is before</option>
									<option value="isNotAfter">is not after</option>
									<option value="isNotBefore">is not before</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<div id="createDateFromDiv" class="date-input">
									<input type="text" id="createDateFrom" name="createDateFrom" ng-model="createDateFrom" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>
							<td>
								<div id="createDateToDiv" class="date-input hidden">
									<input type="text" id="createDateTo" name="createDateTo" ng-model="createDateTo" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>
						</tr>
						
						
						<tr id="row5" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="pymntStatus">Payment Status</label>
							</td>
							<td>
								<select id="pymntStatusSearchOption" name="pymntStatusSearchOption" style="width: 125px;">
									<option value="contains">contains</option>
									<option value="doesNotContain">does not contain</option>
									<option value="isOneOf">is one of</option>
									<option value="isNotOneOf">is not one of</option>
								</select>
							</td>
							<td>
								<select id="pymntStatus" name="pymntStatus" ng-model="pymntStatus" size="3" style="width: 300px;" multiple class="form-control" >
								</select>
								<!--<select id="pymntStatus" name="pymntStatus" ng-model="pymntStatus"
									ng-change="addSelectionToFilterMap()" ng-options="pymntStatus.name for pymntStatus in allpaymentStatus" style="width: 300px;">
									<option value="">Select Payment Status</option>
								</select>-->
							</td>
						</tr>
						
						<!-- <tr id="row6" class="tblRow">
							<td>
								<label class="control-label" for="payAmt">Payment Amount</label>
							</td>
							<td>
								<select>
									<option value="">equals</option>
								</select>
							</td>
							<td>
								<input type="text" id="payAmt"
									name="payAmt" ng-model="payAmt" ng-model-onblur />
							</td>
						</tr> -->
						
						<!-- <tr id="row7" class="tblRow">
							<td>
								<label class="control-label" for="payDate">Payment Date</label>
							</td>
							<td>
								<select>
									<option value="">equals</option>
								</select>
							</td>
							<td>
								<input type="text" id="payDate"
									name="payDate" ng-model="payDate" ng-model-onblur />
							</td>
						</tr> -->
						
						<tr id="row8" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="matchStatus">Match Status</label>
							</td>
							<td>
								<select id="matchStatusSearchOption" name="matchStatusSearchOption" style="width: 125px;">
									<option value="contains">contains</option>
									<option value="doesNotContain">does not contain</option>
									<option value="isOneOf">is one of</option>
									<option value="isNotOneOf">is not one of</option>
								</select>
							</td>
							<td>
								<select id="matchStatus" name="matchStatus" ng-model="matchStatus" size="3" style="width: 300px;" multiple class="form-control" >
								</select>
								<!--<select id="matchStatus" name="matchStatus" ng-model="matchStatus"
									ng-change="addSelectionToFilterMap()" ng-options="matchStatus for matchStatus in allmatchStatus" style="width: 300px;">
									<option value="">Select Match Status</option>
								</select>-->
							</td>
						</tr>
						
						<tr id="row9" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="exType">Exception Type</label>
							</td>
							<td>
								<select id="exTypeSearchOption" name="exTypeSearchOption" style="width: 125px;">
									<option value="contains">contains</option>
									<option value="doesNotContain">does not contain</option>
									<option value="isOneOf">is one of</option>
									<option value="isNotOneOf">is not one of</option>
								</select>
							</td>
							<td>
								<select id="exType" name="exType" ng-model="exType" size="3" style="width: 300px;" multiple class="form-control" >
								</select>
								<!--<select id="exType" name="exType" ng-model="exType"
									ng-change="addSelectionToFilterMap()" ng-options="exType.exceptionType for exType in allexceptionTypes" style="width: 300px;">
									<option value="">Select Exception Types</option>
								</select>-->
							</td>
						</tr>
						
						<tr id="row10" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="exStatus">Exception Status</label>
							</td>
							<td>
								<select id="exStatusSearchOption" name="exStatusSearchOption" style="width: 125px;">
									<option value="contains">contains</option>
									<option value="doesNotContain">does not contain</option>
									<option value="isOneOf">is one of</option>
									<option value="isNotOneOf">is not one of</option>
								</select>
							</td>
							<td>
								<select id="exStatus" name="exStatus" ng-model="exStatus" size="3" style="width: 300px;" multiple class="form-control" >
								</select>
								<!--<select id="exStatus" name="exStatus" ng-model="exStatus"
									ng-change="addSelectionToFilterMap()" ng-options="exStatus.name for exStatus in allexceptionStatus" style="width: 300px;">
									<option value="">Select Exception Status</option>
								</select>-->
							</td>
						</tr>
						
						<tr id="row11" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="exCreateDate">Exception Created Date</label>
							</td>
							<td>
								<select id="exCreateDateSearchOption" name="exCreateDateSearchOption" 
									onchange="toggleDate('exCreateDate');" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isAfter">is after</option>
									<option value="isBefore">is before</option>
									<option value="isNotAfter">is not after</option>
									<option value="isNotBefore">is not before</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<div id="exCreateDateFromDiv" class="date-input">
									<input type="text" id="exCreateDateFrom" name="exCreateDateFrom" ng-model="exCreateDateFrom" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>
							<td>
								<div id="exCreateDateToDiv" class="date-input hidden">
									<input type="text" id="exCreateDateTo" name="exCreateDateTo" ng-model="exCreateDateTo" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>
						</tr>
						
						<tr id="row12" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="resAction">Resolution Action</label>
							</td>
							<td>
								<select id="resActionSearchOption" name="resActionSearchOption" style="width: 125px;">
									<option value="contains">contains</option>
									<option value="doesNotContain">does not contain</option>
									<option value="isOneOf">is one of</option>
									<option value="isNotOneOf">is not one of</option>
								</select>
							</td>
							<td>
								<!--  <select id="resAction" name="resAction" ng-model="resAction" ng-options="resAction for resAction in resolutionActions" style="width: 300px;">
									<option value="">Select Resolution Actions</option>
								</select>-->
								<select id="resAction" name="resAction" ng-model="resAction" size="3" style="width: 300px;" multiple class="form-control" >
								</select>
							</td>
						</tr>
						
						<tr id="row13" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="issueAmt">Issued Amount</label>
							</td>
							<td>
								<select id="issueAmtSearchOption" name="issueAmtSearchOption" onchange="toggleNumericFields('issueAmt');" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isGreaterThan">is greater than</option>
									<option value="isLessThan">is less than</option>
									<option value="isNotGreaterThan">is not greater than</option>
									<option value="isNotLessThan">is not less than</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<input type="text" id="issueAmt" name="issueAmt" ng-model="issueAmt" ng-model-onblur style="width: 300px;"/>
							</td>
							<td>
								<input type="text" id="issueAmtTo" class="hidden" name="issueAmtTo" ng-model="issueAmtTo" ng-model-onblur style="width: 300px;"/>
							</td>
						</tr>
						
						<tr id="row14" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="issueDate">Issued Date</label>
							</td>
							<td>
								<select id="issueDateSearchOption" name="issueDateSearchOption" 
									onchange="toggleDate('issueDate');" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isAfter">is after</option>
									<option value="isBefore">is before</option>
									<option value="isNotAfter">is not after</option>
									<option value="isNotBefore">is not before</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<div id="issueDateFromDiv" class="date-input">
									<input type="text" id="issueDateFrom" name="issueDateFrom" ng-model="issueDateFrom" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>
							<td>
								<div id="issueDateToDiv" class="date-input hidden">
									<input type="text" id="issueDateTo" name="issueDateTo" ng-model="issueDateTo" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>
						</tr>
						
						<tr id="row15" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="paidAmt">Paid Amount</label>
							</td>
							<td>
								<select id="paidAmtSearchOption" name="paidAmtSearchOption" onchange="toggleNumericFields('paidAmt');" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isGreaterThan">is greater than</option>
									<option value="isLessThan">is less than</option>
									<option value="isNotGreaterThan">is not greater than</option>
									<option value="isNotLessThan">is not less than</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<input type="text" id="paidAmt" name="paidAmt" ng-model="paidAmt" ng-model-onblur style="width: 300px;"/>
							</td>
							<td>
								<input type="text" id="paidAmtTo" class="hidden" name="paidAmtTo" ng-model="paidAmtTo" ng-model-onblur style="width: 300px;"/>
							</td>
						</tr> 
						
						<tr id="row16" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="paidDate">Paid Date</label>
							</td>
							<td>
								<select id="paidDateSearchOption" name="paidDateSearchOption"
									onchange="toggleDate('paidDate');" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isAfter">is after</option>
									<option value="isBefore">is before</option>
									<option value="isNotAfter">is not after</option>
									<option value="isNotBefore">is not before</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<div id="paidDateFromDiv" class="date-input">
									<input type="text" id="paidDateFrom" name="paidDateFrom" ng-model="paidDateFrom" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>
							<td>
								<div id="paidDateToDiv" class="date-input hidden">
									<input type="text" id="paidDateTo" name="paidDateTo" ng-model="paidDateTo" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>
						</tr>
						
						
						<tr id="row17" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="stopDate">Stop Date</label>
							</td>
							<td>
								<select id="stopDateSearchOption" name="stopDateSearchOption" 
									onchange="toggleDate('stopDate');" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isAfter">is after</option>
									<option value="isBefore">is before</option>
									<option value="isNotAfter">is not after</option>
									<option value="isNotBefore">is not before</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<div id="stopDateFromDiv" class="date-input">
									<input type="text" id="stopDateFrom" name="stopDateFrom" ng-model="stopDateFrom" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>
							<td>
								<div id="stopDateToDiv" class="date-input hidden">
									<input type="text" id="stopDateTo" name="stopDateTo" ng-model="stopDateTo" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>							
						</tr>
						
						<tr id="row18" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="stopExpDate">Stop Expiration Date</label>
							</td>
							<td>
								<select id="stopExpDateSearchOption" name="stopExpDateSearchOption" 
									onchange="toggleDate('stopExpDate');" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isAfter">is after</option>
									<option value="isBefore">is before</option>
									<option value="isNotAfter">is not after</option>
									<option value="isNotBefore">is not before</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<div id="stopExpDateFromDiv" class="date-input">
									<input type="text" id="stopExpDateFrom" name="stopExpDateFrom" ng-model="stopExpDateFrom" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>
							<td>
								<div id="stopExpDateToDiv" class="date-input hidden">
									<input type="text" id="stopExpDateTo" name="stopExpDateTo" ng-model="stopExpDateTo" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>								
						</tr>
						
						<tr id="row19" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="stopAmt">Stop Amount</label>
							</td>
							<td>
								<select id="stopAmtSearchOption" name="stopAmtSearchOption" onchange="toggleNumericFields('stopAmt');" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isGreaterThan">is greater than</option>
									<option value="isLessThan">is less than</option>
									<option value="isNotGreaterThan">is not greater than</option>
									<option value="isNotLessThan">is not less than</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<input type="text" id="stopAmt" name="stopAmt" ng-model="stopAmt" ng-model-onblur style="width: 300px;"/>
							</td>
							<td>
								<input type="text" id="stopAmtTo" class="hidden" name="stopAmtTo" ng-model="stopAmtTo" ng-model-onblur style="width: 300px;"/>
							</td>
						</tr>
						
						<tr id="row20" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="voidDate">Void Date</label>
							</td>
							<td>
								<select id="voidDateSearchOption" name="voidDateSearchOption" 
									onchange="toggleDate('voidDate');" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isAfter">is after</option>
									<option value="isBefore">is before</option>
									<option value="isNotAfter">is not after</option>
									<option value="isNotBefore">is not before</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<div id="voidDateFromDiv" class="date-input">
									<input type="text" id="voidDateFrom" name="voidDateFrom" ng-model="voidDateFrom" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>
							<td>
								<div id="voidDateToDiv" class="date-input hidden">
									<input type="text" id="voidDateTo" name="voidDateTo" ng-model="voidDateTo" ng-model-onblur style="width: 300px;"/>
								</div>
							</td>
						</tr>
						
						<tr id="row21" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="voidAmt">Void Amount</label>
							</td>
							<td>
								<select id="voidAmtSearchOption" name="voidAmtSearchOption" onchange="toggleNumericFields('voidAmt');" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isGreaterThan">is greater than</option>
									<option value="isLessThan">is less than</option>
									<option value="isNotGreaterThan">is not greater than</option>
									<option value="isNotLessThan">is not less than</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<input type="text" id="voidAmt"	name="voidAmt" ng-model="voidAmt" ng-model-onblur style="width: 300px;"/>
							</td>
							<td>
								<input type="text" id="voidAmtTo" class="hidden" name="voidAmtTo" ng-model="voidAmtTo" ng-model-onblur style="width: 300px;"/>
							</td>
						</tr>
						
						<tr id="row22" class="tblRow paymentListCls">
							<td>
								<label class="control-label" for="traceNum">Trace No.</label>
							</td>
							<td>
								<select id="traceNumSearchOption" name="traceNumSearchOption" onchange="toggleNumericFields('traceNum')" style="width: 125px;">
									<option value="equals">equals</option>
									<option value="isGreaterThan">is greater than</option>
									<option value="isLessThan">is less than</option>
									<option value="isNotGreaterThan">is not greater than</option>
									<option value="isNotLessThan">is not less than</option>
									<option value="isBetween">is between</option>
								</select>
							</td>
							<td>
								<input type="text" id="traceNum" name="traceNum" ng-model="traceNum" ng-model-onblur  style="width: 300px;"/>
							</td>
							<td>
								<input type="text" id="traceNumTo" class="hidden" name="traceNumTo" ng-model="traceNumTo" ng-model-onblur  style="width: 300px;"/>
							</td>
						</tr>
						
						<!-- <tr id="row23" class="tblRow">
							<td>
								<label class="control-label" for="refNum">Reference Number</label>
							</td>
							<td>
								<select>
									<option value="">equals</option>
								</select>
							</td>
							<td>
								<input type="text" id="refNum"
									name="refNum" ng-model="refNum" ng-model-onblur />
							</td>
						</tr> -->
					</table>
					
					<div class="pull-right" align="right">
						<div id="filterSaveMessage"></div>
						<button type="button" class="btn button" ng-click="clearFields(true)">
							Clear
						</button>
						<button type="button" class="btn button" ng-click="saveNewFilter()">
							Save to New Filter
						</button>
						<button type="button" class="btn button" ng-click="search()">
							Search
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade bs-example-modal-lg" id="saveFilterPopup" style="display:none;">
		<div class="modal-dialog modal-lg">
		    <div class="modal-content">
				<div class="modal-body">
					<div id="ajax-loader-savefilter"></div>
					<label class="control-label" for="filterName">Filter Name:</label>
					<input type="text" id="filterName" name="filterName"/>
					</br>
					<label class="control-label" for="filterDescription">Filter Description:</label>
					<input type="text" id="filterDescription" name="filterDescription"/>
					<div class="pull-right" align="right">
						<button type="button" class="btn button" ng-click="saveFilter()" >
							Save Filter
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>