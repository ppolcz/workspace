function Hello($scope, $http) {
    $http.get('http://rest-service.guides.spring.io/greeting').
    success(function(data) {
        $scope.greeting = data;
    });
    $http.get('http://localhost:8080/tr/get/1/25').
    success(function(data) {
        $scope.trlist = data;
    });
}