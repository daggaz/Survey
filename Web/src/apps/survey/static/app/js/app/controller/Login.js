Ext.define('Survey.controller.Login', {
	extend : 'Ext.app.Controller',
	init: function() {
		this.control({
			'login button': { signin: this.signinuser }
		});
	},
	signinuser: function(username, password) {
		// check if the username and password is valid
		console.log("Login.signin");
		Ext.Ajax.request({
			url: '/api/login/',
			method: 'GET',
			params: {username: username, password: password },
			callback: function (options, success, response) {
				if (success) {
					console.log("login success");
					console.log(response.responseText);
				} else {
					console.log("login failed");
				}
			}
		});
	}
});
