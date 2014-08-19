Ext.define('Survey.view.EditUser', {
	extend: 'Ext.Panel',
	alias : 'widget.edituser',
	bodyPadding: '10px 20px 20px 20px',
//	overflowY: 'scroll',
	autoScroll: true,
	layout: {
		type: 'vbox',
		align: 'stretch'
	},
	items: [{
		xtype: 'form',
		itemId: 'userform',
		layout: {
			type: 'vbox',
			align: 'stretch',
			defaultMargins: '0 0 10 0'
		},
		items: [{
			layout: {
				type: 'hbox',
				align: 'center'
			},
			items: [{
				xtype: 'box',
				autoEl: {
					tag: 'h1',
					html: I18N.get('edit_user')
				},
				flex: 1
			},{
				xtype: 'button',
				itemId: 'cancelButton',
				margin: '0 10 0 0',
				text: I18N.get('cancel')
			},{
				xtype: 'button',
				itemId: 'saveButton',
				formBind: true,
				text: I18N.get('save')
			}]
		},{
        	xtype: 'textfield',
            fieldLabel: I18N.get('username'),
			allowBlank: false,
			msgTarget: 'under',
            name: 'username'
        },{
        	xtype: 'textfield',
            fieldLabel: I18N.get('first_name'),
			allowBlank: false,
			msgTarget: 'under',
            name: 'first_name'
        },{
            xtype: 'textfield',
            fieldLabel: I18N.get('last_name'),
			allowBlank: false,
			msgTarget: 'under',
            name: 'last_name'
        },{
            xtype: 'textfield',
            fieldLabel: I18N.get('email'),
			allowBlank: true,
			msgTarget: 'under',
			name: 'email'
        },{
        	xtype: 'checkboxfield',
            boxLabel: I18N.get('staff'),
            name: 'is_staff',
            inputValue: true,
			allowBlank: false,
			msgTarget: 'under',
        },{
        	xtype: 'checkboxfield',
            boxLabel: I18N.get('active'),
            name: 'is_active',
            inputValue: true,
			allowBlank: false,
			msgTarget: 'under',
        }]
	}]
});
