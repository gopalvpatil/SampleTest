$(function() {
    groupMap = new Object();

    itemsGroup = new Array();
    manualEntryGroup = new Array();
    userRoleManagementGroup = new Array();
    otherPermissionsGroup = new Array();
    paymentsGroup = new Array();

    $('#selectAll').prop('checked', false);

    // Add new role modal
    $('#newRole').click(function(event) {
        $("#items").empty();
        $("#manualEntry").empty();
        $("#userRoleManagement").empty();
        $("#otherPermissions").empty();
        $("#payments").empty();
        loadRolePermissions();
        $('.modal-body #selectAll').prop('checked', false);
        $(".modal-body #roleName").val('');
        $(".modal-body #roleLabel").val('');
        $(".modal-body #edit").val('false');
        $('#newRoleModalShow').modal('show');
    });

    $('#selectAll').click(function(e) {
        if ($('#selectAll').is(':checked')) {
            $('input[type="checkbox"]').prop('checked', this.checked);
        } else {
            $('input[type="checkbox"]').attr('checked', false);
        }
    });

    // Save Role   
    $('.saveNewRole').click(function(event) {

        $("#info").empty();
        $("#error").empty();

        var roleName = $("#roleName").val();
        var roleLabel = $("#roleLabel").val();
        var edit = $("#edit").val();
        var selectedIds = [];
        var data;

        if (edit != 'true') {
            data = {roleName: roleName, roleLabel: roleLabel, edit: edit, selectedIds: selectedIds};
        } else {
            var roleId = $("#roleId").val();
            data = {roleId: roleId, roleName: roleName, roleLabel: roleLabel, edit: edit, selectedIds: selectedIds};
        }

        $('.saveCheckbox:checked').each(function(i) {
            selectedIds.push($(this).val());
        });

        $.ajax({
            url: "saverole",
            type: "POST",
            data: JSON.stringify(data),
            contentType: 'application/json; charset=utf-8',
            datatype: "json",
            traditional: true,
            success: function(response) {
            	window.location.reload();
                $('#info').html(response);
                $('#info').show();
                $('#error').hide();
            },
            error: function(response) {
                $('#error').html(response.responseText);
                $('#error').show();
                $('#info').hide();
            },
            async: false
        });
        event.preventDefault();
        $('#newRoleModalShow').modal('hide');
    });

    //Delete function
    $('a.confirm-link').click(function(event) {
        var url = ($(this).attr('href1'));
        var id = getURLParameter(url, 'id');
        $('#deleteConfirmModal').data('id', id).modal('show');
    });

    $('a.del-link').click(function(event) {
        var id = $('#deleteConfirmModal').data('id');

        var data = {id: id};
        $.ajax({
            url: "deleterole",
            type: "POST",
            data: JSON.stringify(data),
            contentType: 'application/json; charset=utf-8',
            datatype: "json",
            traditional: true,
            success: function(response) {
                $('#info').html(response);
                $('#info').show();
                $("#row-" + id).hide('slow');
                $('#error').hide();
            },
            error: function(response) {
                $('#info').hide();
                $('#error').html(response.responseText);
                $('#error').show();
            },
            async: false
        });
        event.preventDefault();

        $('#deleteConfirmModal').modal('hide');
    });

    $('a.edit-link').click(function(event) {
        var url = ($(this).attr('href1'));
        var id = getURLParameter(url, 'id');

        $("#items").empty();
        $("#manualEntry").empty();
        $("#userRoleManagement").empty();
        $("#otherPermissions").empty();
        $("#payments").empty();

        loadRolePermissions();
        $('.modal-body #selectAll').prop('checked', false);

        var data = {id: id};
        $.ajax({
            url: "rolepermissions",
            type: "POST",
            data: JSON.stringify(data),
            contentType: 'application/json; charset=utf-8',
            datatype: "json",
            traditional: true,
            success: function(role) {
                var permissionList = role.permissions;
                $(".modal-body #roleName").val(role.name);
                $(".modal-body #roleLabel").val(role.label);
                $(".modal-body #edit").val('true');
                $(".modal-body #roleId").val(role.id);

                if (permissionList.length > 0) {
                    for (var i = 0; i < permissionList.length; i++) {
                        var permissionId = permissionList[i].id;
                        var permissionName = permissionList[i].label;
                        var checkBoxStr = '#' + permissionId;
                        $(checkBoxStr).prop('checked', true);
                    }
                }

            },
            error: function(response) {
                $('#error').html('Problem occured, role was not retrieved.');
                $('#error').show();
            },
            async: false
        });
        event.preventDefault();

        $('#newRoleModalShow').modal('show');

    });

    function getURLParameter(url, name) {
        return (RegExp(name + '=' + '(.+?)(&|$)').exec(url) || [, null])[1];
    }


});


/*
 * This method loads role permissions
 * First time it will make an ajax request to get the role permission map and store it locally
 * Next time onwards, the local map will be referred
 */
function loadRolePermissions() {
    //role permissions not available locally so Make an ajax request
    $.ajax({
        type: "GET",
        url: "allpemissions",
        dataType: "json",
        success: function(groupMapResponse) {
            //groupMap = groupMapResponse;

            var itemsList = groupMapResponse['itemsList'];
            var manualEntryList = groupMapResponse['manualEntryList'];
            var userRoleManagementList = groupMapResponse['userRoleManagementList'];
            var otherPermissionsList = groupMapResponse['otherPermissionsList'];
            var paymentsList = groupMapResponse['paymentsList'];

            if (itemsList.length > 0) {
                for (var i = 0; i < itemsList.length; i++) {
                    var permissionId = itemsList[i].id;
                    var permissionName = itemsList[i].label;

                    var items = {
                        id: permissionId,
                        name: permissionName
                    };
                    itemsGroup.push(items);

                    var itemsRecord = '<div class="col-sm-4">' +
                            '<label control-label>' +
                            '<input type="checkbox" class="case saveCheckbox"' +
                            'id="' + permissionId + '"' +
                            'name="' + permissionId + '"' +
                            'value="' + permissionId + '"' +
                            '>&nbsp;' + permissionName +
                            '</label>' +
                            '</div>';

                    $('#items').append(itemsRecord);
                }
            }

            if (manualEntryList.length > 0) {
                for (var i = 0; i < manualEntryList.length; i++) {
                    var permissionId = manualEntryList[i].id;
                    var permissionName = manualEntryList[i].label;

                    var manualEntry = {
                        id: permissionId,
                        name: permissionName
                    };
                    manualEntryGroup.push(manualEntry);

                    var manualEntryRecord = '<div class="col-sm-4">' +
                            '<label control-label>' +
                            '<input type="checkbox" class="case saveCheckbox"' +
                            'id="' + permissionId + '"' +
                            'name="' + permissionId + '"' +
                            'value="' + permissionId + '"' +
                            '>&nbsp;' + permissionName +
                            '</label>' +
                            '</div>';

                    $('#manualEntry').append(manualEntryRecord);
                }
            }

            if (userRoleManagementList.length > 0) {
                for (var i = 0; i < userRoleManagementList.length; i++) {
                    var permissionId = userRoleManagementList[i].id;
                    var permissionName = userRoleManagementList[i].label;

                    var userRoleManagement = {
                        id: permissionId,
                        name: permissionName
                    };
                    userRoleManagementGroup.push(userRoleManagement);

                    var userRoleManagementRecord = '<div class="col-sm-4">' +
                            '<label control-label>' +
                            '<input type="checkbox" class="case saveCheckbox"' +
                            'id="' + permissionId + '"' +
                            'name="' + permissionId + '"' +
                            'value="' + permissionId + '"' +
                            '>&nbsp;' + permissionName +
                            '</label>' +
                            '</div>';


                    $('#userRoleManagement').append(userRoleManagementRecord);
                }
            }

            if (otherPermissionsList.length > 0) {
                for (var i = 0; i < otherPermissionsList.length; i++) {
                    var permissionId = otherPermissionsList[i].id;
                    var permissionName = otherPermissionsList[i].label;

                    var otherPermissions = {
                        id: permissionId,
                        name: permissionName
                    };
                    otherPermissionsGroup.push(otherPermissions);

                    var otherPermissionsRecord = '<div class="col-sm-4">' +
                            '<label control-label>' +
                            '<input type="checkbox" class="case saveCheckbox"' +
                            'id="' + permissionId + '"' +
                            'name="' + permissionId + '"' +
                            'value="' + permissionId + '"' +
                            '>&nbsp;' + permissionName +
                            '</label>' +
                            '</div>';


                    $('#otherPermissions').append(otherPermissionsRecord);
                }
            }

            if (paymentsList.length > 0) {
                for (var i = 0; i < paymentsList.length; i++) {
                    var permissionId = paymentsList[i].id;
                    var permissionName = paymentsList[i].label;

                    var payments = {
                        id: permissionId,
                        name: permissionName
                    };
                    paymentsGroup.push(payments);

                    var paymentsRecord = '<div class="col-sm-4">' +
                            '<label control-label>' +
                            '<input type="checkbox" class="case saveCheckbox"' +
                            'id="' + permissionId + '"' +
                            'name="' + permissionId + '"' +
                            'value="' + permissionId + '"' +
                            '>&nbsp;' + permissionName +
                            '</label>' +
                            '</div>';


                    $('#payments').append(paymentsRecord);
                }
            }

        },
        error: function(response) {
            $('#error').html('Loading permissions failed.');
            $('#error').show();
        },
        async: false
    });
}

