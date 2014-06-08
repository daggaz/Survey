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
	refs: [{
		ref: 'error',
		selector: 'login #error'
	}],
	loginUser: function(username, password) {
		Ext.Ajax.request({
			url: '/api/login/',
			method: 'GET',
			params: {username: username, password: password },
			callback: Ext.bind(function (options, success, response) {
				console.log(response.responseText);
				if (success) {
					var result = Ext.decode(response.responseText);
					if (result.status == "success") {
						this.getError().hide();
						Survey.getApplication().session_key = result.session_key;
						console.log("login success: session = " + Survey.getApplication().session_key);
						Survey.getApplication().getMainController().showView('Surveys', function () {
							Survey.getApplication().getMainController().showHeader();
						});
					} else {
						this.getError().show();
						this.getError().update(result.reason);
					}
				} else {
					Ext.Msg.show({
						title: I18N.get('error'),
						msg: 'Server error',
           				icon: Ext.Msg.ERROR,
           				buttons: Ext.Msg.OK
           				});
					console.log("login failed");
				}
			}, this)
		});
	},
	logoutUser: function() {
		delete Survey.getApplication().session_key;
		Survey.getApplication().getMainController().hideHeader();
		Survey.getApplication().getMainController().showView('Login');
		
	}
});
