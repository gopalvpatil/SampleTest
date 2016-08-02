$(document).ready(function() {

    var baseUrl = globalBaseURL;

    //On click of Bank, fetch company list
    $("a[data-parent='#accordion']").click(function(event) {
        var anchor = this;
        var bankId = $(anchor).attr('bankId');
        var companiesFetched = $(anchor).attr('companiesFetched');
        if (companiesFetched == 'false') {
            $('#accordion_bank_' + bankId).append('<div>Please wait...</div>');
            $.ajax({
                url: baseUrl + '/user/banks/' + bankId + '/companies',
                type: "GET",
                datatype: "json",
                success: function(companies) {
                    $('#accordion_bank_' + bankId).empty();
                    populateCompanyList(bankId, companies);
                    $(anchor).attr('companiesFetched', 'true');
                },
                error: function(response) {
                    $('#accordion_bank_' + bankId).empty();
                    $('#accordion_bank_' + bankId).append('<div id="errorBox" class="alert alert-danger">Error fetching company details</div>');
                },
                async: false
            });
        }

        event.preventDefault();
    });

    //On click of company, fetch its detail
    $("#accordion").on("click", "a[data-parent^='#accordion_bank_']", function(event) {
        var anchor = this;
        var bankId = $(anchor).attr('bankId');
        var companyId = $(anchor).attr('companyId');
        var companyDetailFetched = $(anchor).attr('companyDetailFetched');
        if (companyDetailFetched == 'false') {
            $('#collapse_company_' + bankId + '_' + companyId).append('<div>Please wait...</div>');
            $.ajax({
                url: baseUrl + '/user/banks/' + bankId + '/companies/' + companyId,
                type: "GET",
                datatype: "json",
                success: function(company) {
                    $('#collapse_company_' + bankId + '_' + companyId).empty();
                    populateCompanyDetail(bankId, company, baseUrl);
                    $(anchor).attr('companyDetailFetched', 'true');
                },
                error: function(response) {
                    $('#collapse_company_' + bankId + '_' + companyId).empty();
                    $('#collapse_company_' + bankId + '_' + companyId).append('<div id="errorBox" class="alert alert-danger">Error fetching company detail.</div>');
                },
                async: false
            });
        }

        event.preventDefault();
    });
});

/**
 * Method to populate companies list
 * @param bankId
 * @param companies
 * @returns
 */
function populateCompanyList(bankId, companies) {
    if (companies.length > 0) {
        $.each(companies, function(index, company) {
            $('#accordion_bank_' + bankId)
                    .append($('<div class="panel panel-default"/>')
                            .append($('<div class="panel-heading"/>')
                                    .append($('<div class="panel-title"/>')
                                            .append($('<a/>').attr({'data-toggle': 'collapse',
                                                'data-parent': '#accordion_bank_' + bankId,
                                                'companyDetailFetched': false,
                                                'bankId': bankId,
                                                'companyId': company.id,
                                                'class':'pp-heading-a',
                                                'href': '#collapse_company_' + bankId + '_' + company.id})
                                                    .text(company.name)
                                                    )))
                            .append($('<div class="panel-collapse collapse"/>').attr({'id': 'collapse_company_' + bankId + '_' + company.id})));

        });
    } else {
        $('#accordion_bank_' + bankId).append($('<div/>').text('No Companies found!!'));
    }
}

/**
 * Method to populate company detail
 * @param bankId
 * @param company
 */
function populateCompanyDetail(bankId, company, baseUrl) {
    var userSelectOptions = '';
    if (company.users && company.users.length > 0) {
    	sortByName(company.users,"firstName");
        $.each(company.users, function(index, user) {
            userSelectOptions += '<option value=' + user.userId + '>' + getDisplayString(user.firstName) +
                    ' ' + getDisplayString(user.lastName) + ' (' + getDisplayString(user.baseRole) + ')</option>';
        });
    }

    var phone = company.phone;
    if (phone && phone.length > 0)
        phone = phone.substr(0, 3) + '-' + phone.substr(3, 3) + '-' + phone.substr(6, 4);

    var fax = company.fax;
    if (fax && fax.length > 0)
        fax = fax.substr(0, 3) + '-' + fax.substr(3, 3) + '-' + fax.substr(6, 4);

    $('#collapse_company_' + bankId + '_' + company.id)
            .append($('<div class="panel-body"/>')
                    .append($('<div class="row"/>')
                            .append($('<div class="col-md-6"/>')
                                    .append($('<h5 class="text-left"/>').text('Contact: ' + getDisplayString(company.mainContact)))
                                    .append($('<h5 class="text-left"/>').text('Phone: ' + getDisplayString(phone)))
                                    .append($('<h5 class="text-left"/>').text('Email: ' + getDisplayString(company.email)))
                                    .append($('<h5 class="text-left"/>').text('Fax: ' + getDisplayString(fax)))
                                    .append($('<h5 class="text-left"/>').text('Account for analysis billing: ' + getDisplayString(company.billingacount)))
                                    .append($('<br>&nbsp;</br>'))
                                    .append($('<h5 class="text-left"/>').text(getDisplayString(company.address1) + ', ' + getDisplayString(company.address2)))
                                    .append($('<h5 class="text-left"/>').text(getDisplayString(company.city) + ', ' + getDisplayString(company.state) + ' ' + getDisplayString(company.zip)))
                                    )
                            .append($('<div class="col-md-4"/>')
                                    .append($('<h3 class="text-left"/>').text('Users'))
                                    .append($('<select size="6" class="form-control"/>').html(userSelectOptions))
                                    )
                            .append($('<div class="col-md-2" style="margin-top: 47px;"/>')
                                    .append($('<a type="button" class="btn button btn-lg btn-block"/>')
                                            .attr('href', baseUrl + '/user/companysetup?companyId=' + company.id).text('Edit'))
                                    .append($('<a type="button" class="btn button btn-lg btn-block"/>')
                                            .attr('href', baseUrl + '/user/manageusers?bankId=' + bankId + '&companyId=' + company.id).text('Manage Users'))
                                    .append($('<a type="button" class="btn button btn-lg btn-block"/>')
                                            .attr('href', baseUrl + '/user/addUser').text('Add Users'))
                                    )));
}

function getDisplayString(str) {
    if (str)
        return str.trim();
    return '';
}
