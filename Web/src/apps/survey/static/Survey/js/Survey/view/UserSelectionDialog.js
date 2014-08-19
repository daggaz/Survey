Ext.define('Survey.view.UserSelectionDialog', {
    extend: 'Ext.window.Window',
    alias : 'widget.userselectiondialog',
    title: I18N.get('select_user'),
    height: 400,
    width: 500,
    layout: 'fit',
    modal: true,
    resizable: false,
    closable: false,
    items: [{
    	xtype: 'grid',
        frame: false,
        store: 'auth.User',
        listeners : {
        	added: function () {
				this.getStore().load();
			},
			reconfigure: function(grid, store) {
				store.load();
			}
		},
        viewConfig: {
	        emptyText: I18N.get('no_users'),
            stripeRows: false
        },
        columns:[{
	            text: I18N.get('username'),
	            dataIndex: 'username',
	            width: 100,
	            sortable: false
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
	            sortable: false
	        }
	   	],
    	flex: 1
    }],
    buttons: [{
    	itemId: 'ok_button',
    	text: 'OK'
    },{
    	itemId: 'cancel_button',
    	text: 'Cancel'
    }]
});
