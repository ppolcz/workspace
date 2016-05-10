console.log("your message here");

angular.module('hello', []).controller('home', function($http) {
	var self = this;
	$http.get('http://rest-service.guides.spring.io/greeting').then(function(response) {
		self.greeting = response.data;
	})
});

angular.module('myApp',[]).controller('home', function($http) {
	var self = this;
	$http.get('resource/').then(function(response) {
		self.greeting = response.data;
	})
});
