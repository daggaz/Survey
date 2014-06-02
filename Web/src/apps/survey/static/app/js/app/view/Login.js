Ext.define('Survey.view.Login', {
	extend: 'Ext.Panel',
	alias : 'widget.login',
	title : 'Login',
	layout: {
		type: 'hbox',
		align: 'middle',
		pack: 'center'
	},
	items: [{
		xtype: 'form',
		title : 'Login',
		frame: true,
		width: 250,
		bodyPadding: '10px 20px',
		items: [{
			xtype: 'textfield',
			fieldLabel: I18N.get('username'),
			name: 'username',
			itemId: 'username',
			allowblank: false
		},{
			xtype: 'textfield',
			fieldLabel: I18N.get('password'),
			name: 'password',
			itemId: 'password',
			allowblank: false,
			inputType: 'password'
		}],
		buttons:[{
			text: 'Login',
			listeners: {
				click: function() {
					var username = this.up('form').down('#username').getValue();
					var password = this.up('form').down('#password').getValue();
					this.fireEvent('signin', username, password);
				}
			}
		}]
	}]
});
