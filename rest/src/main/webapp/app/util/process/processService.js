// Process Service
var processUrl = 'process';
tsApp.service('processService', [
  '$http',
  '$q',
  'Upload',
  'gpService',
  'utilService',
  function($http, $q, Upload, gpService, utilService) {
    console.debug('configure processService');

    // add algorithm config
    this.addAlgorithmConfig = function(projectId, algo) {
      console.debug('addAlgorithmConfig', projectId, algo);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.put(processUrl + '/config/algo/add?projectId=' + projectId, algo).then(
      // success
      function(response) {
        console.debug('  algo = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };

    // add process config
    this.addProcessConfig = function(projectId, process) {
      console.debug('addProcessConfig', projectId, process);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.put(processUrl + '/config/add?projectId=' + projectId, process).then(
      // success
      function(response) {
        console.debug('  process = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };

    // find process config
    this.findProcessConfigs = function(projectId, query, pfs) {
      console.debug('findProcessConfig', projectId, query, pfs);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.post(
        processUrl + '/config?projectId=' + projectId
          + (query ? '&query=' + utilService.prepQuery(query) : ''), utilService.prepPfs(pfs))
        .then(
        // success
        function(response) {
          console.debug('  processes = ', response.data);
          gpService.decrement();
          deferred.resolve(response.data);
        },
        // error
        function(response) {
          utilService.handleError(response);
          gpService.decrement();
          deferred.reject(response.data);
        });
      return deferred.promise;
    };

    // get algorithm config
    this.getAlgorithmConfig = function(projectId, id) {
      console.debug('getAlgorithmConfig', projectId, id);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/config/algo/' + id + '?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  algo = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };

    // get process config
    this.getProcessConfig = function(projectId, id) {
      console.debug('getProcessConfig', projectId, id);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/config/' + id + '?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  process = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };

    // cancel process
    this.cancelProcess = function(projectId, id) {
      console.debug('cancelProcess', projectId, id);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/execution/' + id + '/cancel?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  cancel = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };
    
    // execute process
    this.executeProcess = function(projectId, id, background) {
      console.debug('executeProcess', projectId, id, background);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/config/' + id + '/execute?projectId=' + projectId + '&background=' + background).then(
      // success
      function(response) {
        console.debug('  execute = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };

    // find currently executing processes
    this.findCurrentlyExecutingProcesses = function(projectId) {
      console.debug('executing processes', projectId, id, background);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/executing?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  executing = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };
    
    // get algorithm config for key
    this.getAlgorithmConfigForKey = function(projectId, key) {
      console.debug('get algorithm config for key', projectId, key);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/config/algo/key/' + key + '?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  config = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };
    
    // get algorithm log
    this.getAlgorithmLog = function(projectId, algorithmExecutionId) {
      console.debug('get algorithm log', projectId, algorithmExecutionId);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/algo/' + algorithmExecutionId + '/log?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  log = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };
    
    // get algorithm progress
    this.getAlgorithmProgress = function(projectId, id) {
      console.debug('get algorithm progress', projectId, id);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/algo/' + id + '/progress?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  progress = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };
   

       
    // get process log
    this.getProcessLog = function(projectId, processExecutionId) {
      console.debug('get process log', projectId, processExecutionId);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/' + processExecutionId + '/log?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  log = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };
    
    // get process progress
    this.getProcessProgress = function(projectId, id) {
      console.debug('get process progress', projectId, id);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/' + id + '/progress?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  progress = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };
   
    
    // get insertion algorithms
    this.getInsertionAlgorithms = function(projectId) {
      console.debug('get insertion algorithms', projectId);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/algo/insertion?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  insertion = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };
    
    // get maintenance algorithms
    this.getMaintenanceAlgorithms = function(projectId) {
      console.debug('get maintenance algorithms', projectId);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/algo/maintenance?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  maintenance = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };
    
    // get release algorithms
    this.getReleaseAlgorithms = function(projectId) {
      console.debug('get release algorithms', projectId);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/algo/release?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  release = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };
    
    // restart process
    this.restartProcess = function(projectId, id) {
      console.debug('restart process', projectId, id);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/execution/' + id + '/restart?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  restart = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };
   
    // remove algorithm config
    this.removeAlgorithmConfig = function(projectId, id) {
      console.debug('removeAlgorithmConfig', projectId, id);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http['delete'](processUrl + '/config/algo/' + id + '/remove?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  successful delete');
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };

    // remove process config
    this.removeProcessConfig = function(projectId, id) {
      console.debug('removeProcessConfig', projectId, id);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http['delete'](processUrl + '/config/' + id + '/remove?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  successful delete');
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };

    // remove process execution
    this.removeProcessExecution = function(projectId, id) {
      console.debug('removeProcessExecution', projectId, id);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http['delete'](processUrl + '/execution/' + id + '/remove?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  remove process execution');
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };

    // update algorithm config
    this.updateAlgorithmConfig = function(projectId, algo) {
      console.debug('updateAlgorithmConfig', projectId, algo);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.post(processUrl + '/config/algo/update?projectId=' + projectId, algo).then(
      // success
      function(response) {
        console.debug('  algo = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };

    // update process config
    this.updateProcessConfig = function(projectId, process) {
      console.debug('updateProcessConfig', projectId, process);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.post(processUrl + '/config/update?projectId=' + projectId, process).then(
      // success
      function(response) {
        console.debug('  process = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };

    // find process exec
    this.findProcessExecs = function(projectId, query, pfs) {
      console.debug('findProcessExecs', projectId, query, pfs);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.post(
        processUrl + '/exec?projectId=' + projectId
          + (query ? '&query=' + utilService.prepQuery(query) : ''), utilService.prepPfs(pfs))
        .then(
        // success
        function(response) {
          console.debug('  processes = ', response.data);
          gpService.decrement();
          deferred.resolve(response.data);
        },
        // error
        function(response) {
          utilService.handleError(response);
          gpService.decrement();
          deferred.reject(response.data);
        });
      return deferred.promise;
    };

    // get process exec
    this.getProcessExec = function(projectId, id) {
      console.debug('getProcessExec', projectId, id);
      var deferred = $q.defer();

      // Get projects
      gpService.increment();
      $http.get(processUrl + '/exec/' + id + '?projectId=' + projectId).then(
      // success
      function(response) {
        console.debug('  process = ', response.data);
        gpService.decrement();
        deferred.resolve(response.data);
      },
      // error
      function(response) {
        utilService.handleError(response);
        gpService.decrement();
        deferred.reject(response.data);
      });
      return deferred.promise;
    };

    // end
  } ]);
