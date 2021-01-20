let $incidentTabPanel = {
    changeComponentState: function(source) {
        let parentElement = source.parentElement;
        let componentId = parentElement.id;
        let statusId    = source.id.replace(componentId + "-", "");
        // console.log("componentId: " + componentId + ", status: " + statusId);
        if (!($("#" + componentId + "-remove").hasClass("selected"))) {
            $("#" + componentId).children("span").removeClass("selected");
            $("#" + source.id).addClass("selected");
        }
    } // changeComponentState
   ,removeComponent: function(source) {
        let parentElement = source.parentElement;
        let componentId = parentElement.id;
        let statusId    = source.id.replace(componentId + "-", "");
        // console.log("componentId: " + componentId + ", status: " + statusId);
        if (!$("#" + source.id).hasClass("selected")) {
            $("#" + source.id).addClass("selected");
            $("#" + parentElement.id).find(".component-name").addClass("removed");
        } else {
            $("#" + source.id).removeClass("selected");
            $("#" + parentElement.id).find(".component-name").removeClass("removed");
        }
    } // removeComponent
   ,changeAllComponentsState: function(source) {
        let parentElement = source.parentElement;
        let componentId = parentElement.id;
        let statusId    = source.id.replace(componentId + "-", "");
        // console.log("componentId: " + componentId + ", status: " + statusId);
        let f = this.changeComponentState;
        $(".component-name").not(".removed").each( function(idx, value) {
            let parentId = $(this).parent().attr("id");
            f($("#" + parentId + "-" + statusId)[0]);
        });
    } // changeAllComponentsState
   ,removeAllComponents: function(source) {
        if (!$("#" + source.id).hasClass("slfvnosjhb")) {
            $("#" + source.id).addClass("slfvnosjhb");
            $(".component-name").addClass("removed");
            $(".component-button.remove").not(".header").addClass("selected");
        } else {
            $("#" + source.id).removeClass("slfvnosjhb");
            $(".component-name").removeClass("removed");
            $(".component-button.remove").not(".header").removeClass("selected");
        }
    } // removeAllComponents
   ,getComponentsConfig: function() {
        let result = {};
        result["remove"]               = [];
        result["operational"]          = this.getComponents("operational");
        result["degraded_performance"] = this.getComponents("degraded_performance");
        result["partial_outage"]       = this.getComponents("partial_outage");
        result["major_outage"]         = this.getComponents("major_outage");
        result["under_maintenance"]    = this.getComponents("under_maintenance");
        $(".component-name.removed").each(function() {
            result["remove"].push($(this).parent().attr("id"));
        });
        console.log(result);
        return result;
    }
   ,getComponents(state) {
        let result = [];
        $(".component-name").not(".removed").each(function() {
            $(this).parent().find("." + state + ".selected").each(function (){
                result.push($(this).parent().attr("id"));
            });
        });
        return result;
    }
   ,test: function() {
        console.log("incident tab panel test");
    }
} // $incidentTabPanel
// jQuery(function () {
//     console.log("incident-tab-panel loaded");
// });
