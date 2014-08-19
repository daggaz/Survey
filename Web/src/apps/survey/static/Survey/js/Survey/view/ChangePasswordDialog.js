Ext.define('Survey.view.ChangePasswordDialog', {
    extend: 'Ext.window.Window',
    alias : 'widget.changepassworddialog',
    title: I18N.get('change_password'),
    height: 240,
    width: 500,
    layout: 'fit',
    modal: true,
    resizable: false,
    closable: false,
    items: [{
		xtype: 'form',
		itemId: 'form',
		bodyPadding: '15',
		layout: {
			type: 'vbox',
			align: 'stretch',
			defaultMargins: '0 0 7 0'
		},
		items: [{
			xtype: 'box',
			itemId: 'change_password_message',
			html: I18N.get('change_password_message')
		},{
			xtype: 'panel',
			hidden: true,
			margin: '0 0 10 0',
			cls: 'x-form-invalid-under',
			itemId: 'error'
		},{
	    	xtype: 'textfield',
	        inputType: 'password',
	        fieldLabel: I18N.get('password'),
			msgTarget: 'under',
	        itemId: 'password',
	        validator: function () {
	        	if (this.getValue().length >= 5)
	        		return true;
	        	else
	        		return I18N.get("password_to_short");
	        }
	    },{
	        xtype: 'textfield',
	        inputType: 'password',
	        fieldLabel: I18N.get('confirm_password'),
			msgTarget: 'under',
			itemId: 'password2',
	        validator: function () {
	        	if (this.getValue() == this.up('form').down('#password').getValue())
	        		return true;
	        	else
	        		return I18N.get("passwords_must_match");
	        }
	    }],
		listeners: {
			beforeAction: function (form, action, eOpts) {
				if (this.isValid()) {
					var password = this.down('#password').getValue();
					this.fireEvent('changepassword', this.user, password);
					return false;
				}
			}
		},
	    buttons: [{
	    	itemId: 'ok_button',
	    	formBind: true,
	    	text: 'OK',
			handler: function() {
				this.up('form').submit();
			}
	    },{
	    	itemId: 'cancel_button',
	    	text: 'Cancel'
	    }]
    }]
});
