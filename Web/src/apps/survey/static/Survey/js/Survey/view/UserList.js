Ext.define('Survey.view.UserList', {
	extend: 'Ext.Panel',
	alias : 'widget.userlist',
	bodyPadding: '10px 20px 20px 20px',
	layout: {
		type: 'vbox',
		align: 'stretch'
	},
	items: [{
		xtype: 'box',
		autoEl: {
			tag: 'h1',
			html: I18N.get('users')
		}
	},{
		xtype: 'grid',
		title: I18N.get('users'),
		store: 'auth.User',
        loadMask: true,
        frame: true,
        viewConfig: {
	        emptyText: I18N.get('no_users_selected'),
            stripeRows: false
        },
		listeners : {
			render : function(grid) {
				grid.getStore().load();
			}
		},
        columns:[{
        	text: I18N.get('username'),
            dataIndex: 'username',
            width: 150,
            sortable: true
        },{
        	text: I18N.get('first_name'),
            dataIndex: 'first_name',
            width: 150,
            sortable: true
        },{
        	text: I18N.get('last_name'),
            dataIndex: 'last_name',
            width: 150,
            sortable: true
        },{
        	text: I18N.get('email'),
            dataIndex: 'email',
            width: 300,
            sortable: true
        },{
            text: I18N.get('staff'),
            dataIndex: 'is_staff',
            width: 70,
            sortable: true,
            align: 'center',
            renderer: function (value, metaData, record, rowIdx, colIdx, store) {
            	if(value) {
			    	return '<img src="/media/static/Survey/img/tick.png" />';
			    } else {
			    	return '<img src="/media/static/Survey/img/cross.png" />';
			    }
            }
        },{
            text: I18N.get('active'),
            dataIndex: 'is_active',
            width: 70,
            sortable: true,
            align: 'center',
            renderer: function (value, metaData, record, rowIdx, colIdx, store) {
            	if(value) {
			    	return '<img src="/media/static/Survey/img/tick.png" />';
			    } else {
			    	return '<img src="/media/static/Survey/img/cross.png" />';
			    }
            }
        }],
    	flex: 1,
    	dockedItems: [{
		    xtype: 'toolbar',
		    dock: 'top',
		    items: [{
		        xtype: 'button',
		        itemId: 'new_button',
		        icon: Config.media_url + 'Survey/img/add.png',
		        text: I18N.get('new_user')
		    },{
		        xtype: 'button',
		        itemId: 'edit_button',
		        icon: Config.media_url + 'Survey/img/edit.png',
		        text: I18N.get('edit_user'),
		        disabled: true
		    },{
		        xtype: 'button',
		        itemId: 'delete_button',
		        icon: Config.media_url + 'Survey/img/delete.png',
		        text: I18N.get('delete_user'),
		        disabled: true
		    },{
		        xtype: 'button',
		        itemId: 'staff_button',
		        icon: Config.media_url + 'Survey/img/tick.png',
		        text: I18N.get('set_staff'),
		        disabled: true
		    },{
		        xtype: 'button',
		        itemId: 'active_button',
		        icon: Config.media_url + 'Survey/img/tick.png',
		        text: I18N.get('set_active'),
		        disabled: true
		    },{
		        xtype: 'button',
		        itemId: 'password_button',
		        icon: Config.media_url + 'Survey/img/password.png',
		        text: I18N.get('change_password'),
		        disabled: true
		    }]
		}]
	},{
		xtype: 'changepassworddialog',
	}]
});
