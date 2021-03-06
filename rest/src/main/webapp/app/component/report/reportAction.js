// Report directive
tsApp.directive('reportAction', [
  '$window',
  '$routeParams',
  function($window, $routeParams) {
    return {
      restrict : 'A',
      scope : {
        // selected features
        selected : '=',
        // callbacks functions
        callbacks : '='
      },
      templateUrl : 'app/component/report/reportAction.html',
      controller : [
        '$scope',
        '$sce',
        'projectService',
        'utilService',
        'metaEditingService',
        'securityService',
        function($scope, $sce, projectService, utilService, metaEditingService, securityService) {
          // Scope vars
          $scope.molecularActions = [];
          $scope.lowestNotUndone;
          $scope.highestUndone;

          // Paging variables
          $scope.pagedActions = [];
          $scope.pageSizes = utilService.getPageSizes();
          $scope.paging = utilService.getPaging();
          $scope.paging.sortField = 'timestamp';
          $scope.paging.pageSize = 10;
          $scope.paging.sortAscending = false;
          $scope.paging.callbacks = {
            getPagedList : getPagedActions
          };

          // watch component, generate the report
          $scope.$watch('selected.component',
            function() {
              console.debug('selected.component', $scope.selected.component,
                $scope.selected.project);
              if ($scope.selected.component) {
                $scope.findMolecularActions($scope.selected.component);
              }
            });

          // get the full molecular actions list
          $scope.findMolecularActions = function(component) {
            findMolecularActions(component);
          }
          function findMolecularActions(component) {

            projectService.findMolecularActions(component.id, $scope.selected.project.terminology,
              $scope.selected.project.version, null, {
                sortField : 'timestamp',
                ascending : false
              }).then(
            // Success
            function(data) {
              $scope.molecularActions = data.actions;
              $scope.molecularActions.totalCount = data.totalCount;
              $scope.lowestNotUndone = -1;
              for (var i = 0; i < $scope.molecularActions.length; i++) {
                if ($scope.lowestNotUndone == -1 && !$scope.molecularActions[i].undoneFlag) {
                  $scope.lowestNotUndone = i;
                  break;
                }
              }
              $scope.highestUndone = -1;
              for (var i = 0; i < $scope.molecularActions.length; i++) {
                if ($scope.molecularActions[i].undoneFlag) {
                  $scope.highestUndone = i;
                }
              }

              $scope.getPagedActions();
            });
          }

          // Get paged actions (assume all are loaded)
          $scope.getPagedActions = function() {
            getPagedActions();
          }
          function getPagedActions() {

            var pagedArray = utilService.getPagedArray($scope.molecularActions, $scope.paging);
            $scope.pagedActions = pagedArray.data;
            $scope.pagedActions.totalCount = pagedArray.totalCount;
          }

          // Undo action
          $scope.undoAction = function(action, overrideWarnings) {
            metaEditingService.undoAction($scope.selected.project.id, action.activityId, action.id,
              overrideWarnings).then(
            // Success
            function(data) {
              $scope.validation = data;
            });
          }

          // Redo action
          $scope.redoAction = function(action, overrideWarnings) {
            metaEditingService.redoAction($scope.selected.project.id, action.activityId, action.id,
              $scope.overrideWarnings).then(
            // Success
            function(data) {
              $scope.validation = data;
            });
          }

          // returns if the action is undoable
          $scope.isUndoable = function(action) {
            for (var i = 0; i < $scope.molecularActions.length; i++) {
              if (action.id == $scope.molecularActions[i].id) {
                return i == $scope.lowestNotUndone;
              }
            }
          }

          // returns if the action is redoable
          $scope.isRedoable = function(action) {
            for (var i = 0; i < $scope.molecularActions.length; i++) {
              if (action.id == $scope.molecularActions[i].id) {
                return i == $scope.highestUndone;
              }
            }
          }

          // Convert date to a string
          $scope.toDate = function(lastModified) {
            return utilService.toDate(lastModified);
          };

          $scope.hasPermissions = function(action) {
            return securityService.hasPermissions(action);
          }

          $scope.selfResolved = function(action) {
            var user = securityService.getUser();
            if (user.userPreferences.lastProjectRole != 'AUTHOR') {
              return true;
            } else if (user.userPreferences.lastProjectRole == 'AUTHOR' && user.editorLevel == 5
              && action.lastModifiedBy.indexOf(user.userName) > 0) {
              return true;
            }
            return false;
          }

          $scope.displayLog = function(action) {
            var objectId = action.id;
            projectService.getLog($scope.selected.project.id, objectId, 'ACTION').then(
            // Success
            function(data) {
              if (data.length > 0) {
                action.log = $sce.trustAsHtml('<span class="preformatted">' + data + '</span>');
              } else {
                action.log = $sce.trustAsHtml('<span class="preformatted">No details.</span>');
              }
            },
            // Error
            function(data) {
              utilService.handleDialogError($scope.errors, data);
            });
          }
        } ]
    };
  } ]);
