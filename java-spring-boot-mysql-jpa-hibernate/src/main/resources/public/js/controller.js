// (function() {

/*
 * value = 1; array = [0,value,2,3]; console.log($.inArray(value, array));
 */

angular.module('ClusterApp', [ 'datatables' ]).controller('ClusterCtrl',
		function($scope, $http) {
			// $http.get("http://www.w3schools.com/angular/customers_mysql.php").success(function
			$http.get("cl/get").success(function(response) {
				$scope.names = response;
			});

			$scope.testingClick = function(name) {
				console.log(name);
			};

			$scope.getParentName = function(parent) {
				if (parent == null) {
					return "";
				} else {
					return parent.name;
				}
			}
		});

angular.module('ClusterTreeApp', [ 'angularTreeview' ]).controller(
		'ClusterTreeCtrl', function($scope, $http) {

			$http.get("cl/get-tree").success(function(response) {
				$scope.roleList = [ response ];
			});

		});

angular.module('TransactionApp', [ 'datatables' ]).controller(
		'TransactionCtrl', function($scope, $http) {
			// $http.get("http://www.w3schools.com/angular/customers_mysql.php").success(function
			$http.get("tr/get/0/225").success(function(response) {
				$scope.names = response;
			});

			$scope.testingClick = function(name) {
				console.log(name);
			};
		});
// })();
