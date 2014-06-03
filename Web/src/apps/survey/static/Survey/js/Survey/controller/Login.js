Ext.define('Survey.controller.Login', {
	extend : 'Ext.app.Controller',
	init: function() {
		this.control({
			'login textfield': {
				specialkey: function (field, e) {
					if (e.getKey() == e.ENTER)
						field.up('form').getForm().submit();
				}
			},
			'login form': { login: this.loginUser }
		});
	},
	loginUser: function(username, password) {
		Ext.Ajax.request({
			url: '/api/login/',
			method: 'GET',
			params: {username: username, password: password },
			callback: function (options, success, response) {
				console.log(response.responseText);
				if (success) {
					Survey.getApplication().session_key = Ext.decode(response.responseText).session_key;
					console.log("login success: session = " + Survey.getApplication().session_key);
					Survey.getApplication().getMainController().showView('Home', function () {
						Survey.getApplication().getMainController().showHeader();
					});
				} else {
					console.log("login failed");
				}
			}
		});
	},
	logoutUser: function() {
		delete Survey.getApplication().session_key;
		Survey.getApplication().getMainController().hideHeader();
		Survey.getApplication().getMainController().showView('Login');
		
	}
});
