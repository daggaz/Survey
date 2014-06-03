Ext.define('Survey.view.Login', {
	extend: 'Ext.Panel',
	alias : 'widget.login',
	title : I18N.get('login'),
	layout: {
		type: 'hbox',
		align: 'middle',
		pack: 'center'
	},
	items: [{
		xtype: 'form',
		frame: true,
		width: 350,
		bodyPadding: '10px 20px',
    	defaultType: 'textfield',
		items: [{
			fieldLabel: I18N.get('username'),
			name: 'username',
			itemId: 'username',
			msgTarget: 'under',
			allowBlank: false
		},{
			fieldLabel: I18N.get('password'),
			name: 'password',
			itemId: 'password',
			allowBlank: false,
			inputType: 'password',
			msgTarget: 'under'
		}],
		buttons:[{
			text: I18N.get('login'),
			formBind: true,
			disabled: true,
			handler: function() {
				this.up('form').submit();
			}
		}],
		listeners: {
			beforeAction: function (form, action, eOpts) {
				if (this.isValid()) {
					var username = this.down('#username').getValue();
					var password = this.down('#password').getValue();
					this.fireEvent('login', username, password);
					return false;
				}
			}
		}
	}]
});
