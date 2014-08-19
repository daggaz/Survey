Ext.define('Survey.view.Users', {
	extend: 'Ext.Panel',
	alias : 'widget.users',
	layout: 'card',
	items: [{
		xtype: 'userlist',
		itemId: 'userlist'
	},{
		xtype: 'edituser',
		itemId: 'edituser'
	}]
});
