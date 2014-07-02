Ext.define('Survey.view.EditSurveyUsers', {
	extend: 'Ext.Panel',
	alias : 'widget.editsurveyusers',
	bodyPadding: '10px 20px 20px 20px',
//	overflowY: 'scroll',
	autoScroll: true,
	layout: {
		type: 'vbox',
		align: 'stretch'
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
				html: I18N.get('change_users')
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
    	xtype: 'grid',
		title: I18N.get('assigned_users'),
        frame: true,
        minHeight: 200,
        columns:[{
	            text: I18N.get('username'),
	            dataIndex: 'username',
	            width: 100,
	            sortable: false
	        },{
	            text: I18N.get('email'),
	            dataIndex: 'email',
	            width: 300,
	            sortable: false
	        }
	   	],
    	flex: 1,
    	dockedItems: [{
		    xtype: 'toolbar',
		    dock: 'top',
		    items: [{
		        xtype: 'button',
		        itemId: 'new_button',
		        icon: Config.media_url + 'Survey/img/add.png',
		        text: I18N.get('add_user')
		    },{
		        xtype: 'button',
		        itemId: 'delete_button',
		        icon: Config.media_url + 'Survey/img/delete.png',
		        text: I18N.get('remove_user'),
		        disabled: true
		    }]
		}]
	}]
});
