$(document).ready(function() {
	paymentsList = new Array();
	exceptionDataList = new Array();
	itemList = new Array();
	var header = window.localStorage.getItem("lookfor");
	if(header == 'Payments') {
		populatePaymentListForPaymentsAndItemsPage();
	} else {
		populateItemListForPaymentsAndItemsPage();
	}
});

function updateFilter() {
	window.localStorage.setItem("showpaymentsdata", "true");
}