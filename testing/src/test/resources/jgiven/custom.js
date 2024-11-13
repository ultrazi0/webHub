angular.module('jgivenReportApp').config(['$provide', function($provide) {
    $provide.decorator('$controller', ['$delegate', function($delegate) {
        return function(constructor, locals, later, indent) {
            const ctrl = $delegate(constructor, locals, later, indent);
            // Check if this is the specific controller we want to extend
            if (constructor === 'JGivenReportCtrl as appCtrl') {
                // Add the new function to the $scope of the controller
                locals.$scope.getCaseStep = function (step, scenarioCase, scope) {
                    const indexes = [scope.$index];
                    for (let i = 0; i < step.depth; i++) {
                        scope = scope.$parent.$parent.$parent;
                        indexes.push(scope.$index);
                    }

                    let caseStep = scenarioCase.steps[indexes.pop()];

                    indexes.reverse().forEach(index => {
                        caseStep = caseStep.nestedSteps[index];
                    });

                    return caseStep;
                };
            }
            return ctrl;
        };
    }]);
}]);
