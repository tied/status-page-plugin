function sort_options(element) {
    var options = $('#' + element + ' option');
    var arr = options.map(function(_, o) { return { t: $(o).text(), v: o.value }; }).get();
    arr.sort(function(o1, o2) { return o1.t > o2.t ? 1 : o1.t < o2.t ? -1 : 0; });
    options.each(function(i, o) {
        o.value = arr[i].v;
        $(o).text(arr[i].t);
    });
}
function move_items(from, to, items) {
    for (let idx = 0; idx < items.length; idx++) {
        // AJS.log(items[idx].value + " -> " + items[idx].text + " : " + items[idx].selected);
        $('#' + to).append('<option value="' + items[idx].value + '">' + items[idx].text + '</option>');
        $('#' + from + ' option[value=' + items[idx].value + ']').remove();
        sort_options(to);
    }
}
function get_selected(items) {
    let result = [];
    for (let idx = 0; idx < items.length; idx++) {
        // AJS.log(idx + " : " + items[idx].value + " -> " + items[idx].text + " (" + items[idx].selected + ")");
        if (items[idx].selected) {
            result.push(items[idx]);
        }
    }
    return result;
}
function get_select_values_string(element) {
    let result = "";
    let arr = AJS.$("#" + element + " option");
    for(let i = 0; i < arr.length; i++) {
        result += arr[i].value;
        if (i < arr.length - 1)
            result += ",";
    }
    return result;
}

function admin_add_projects() {
    move_items('available-projects', 'selected-projects', get_selected($("#available-projects option")));
}
function admin_add_all_projects() {
    move_items('available-projects', 'selected-projects', $("#available-projects option"));
}
function admin_remove_projects() {
    move_items('selected-projects', 'available-projects', get_selected($("#selected-projects option")));
}
function admin_remove_all_projects() {
    // remove_items($("#selected-projects option"));
    move_items('selected-projects', 'available-projects', $("#selected-projects option"));
}

function admin_add_roles() {
    move_items('available-roles', 'selected-roles', get_selected($("#available-roles option")));
}
function admin_add_all_roles() {
    move_items('available-roles', 'selected-roles', $("#available-roles option"));
}
function admin_remove_roles() {
    move_items('selected-roles', 'available-roles', get_selected($("#selected-roles option")));
}
function admin_remove_all_roles() {
    move_items('selected-roles', 'available-roles', $("#selected-roles option"));
}

function admin_update_config() {

    let projects   = get_select_values_string("selected-projects");
    let roles      = get_select_values_string("selected-roles");
    let field_name = AJS.$("#custom-field-name")[0].value;

    // AJS.log("~~~ SAVING CONFIGURATION:");
    // AJS.log("       projects: " + projects);
    // AJS.log("       roles   : " + roles);
    // AJS.log("       field_id: " + field_id);
    // AJS.log("~~~~~~~~~~~~~~~~~~~~~~~~~~")
    AJS.$.ajax({
        url: AJS.contextPath() + "/rest/ws-slink-statuspage/1.0/admin",
        type: "PUT",
        contentType: "application/json",
        data: '{ "projects": "' + projects + '", "roles": "' +  roles + '", "custom_field": "' + field_name + '"}',
        processData: false
    }).done(function () {
        JIRA.Messages.showSuccessMsg("configuration saved")
    }).error(function (error, message) {
        // AJS.log("---------------------------------------------------");
        // AJS.log(error);
        // AJS.log("---------------------------------------------------");
        // AJS.log(message);
        // AJS.log("---------------------------------------------------");
        JIRA.Messages.showErrorMsg("could not save configuration: <br><br>" + error.responseText)
    });
}
