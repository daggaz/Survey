Ext.define('Survey.controller.Users', {
	extend : 'Ext.app.Controller',
	views: ['Users', 'UserList', 'EditUser'],
	getMainView: function() {
		return this.getUsersView();
	},
	canNavigateFrom: function () {
		if (this.getUsers().getLayout().getActiveItem().itemId == "userlist")
			return true;
	},
	navigateFrom: function(doNavigate) {
		if (this.canNavigateFrom())
			doNavigate();
		else
			Ext.MessageBox.confirm(I18N.get('confirm_action'), I18N.get('confirm_discard_changes'), Ext.bind(function(result) {
				if (result == "yes")
					doNavigate();
			}, this));
	},
	init: function() {
		this.control({
			'userlist grid': {
				added: function () {
					this.getStore().load();
				},
				selectionchange: function (grid, selected, opts) {
					if (selected.length == 1) {
						this.getEditButton().enable();
						this.getDeleteButton().enable();
						this.getActiveButton().enable();
						this.getStaffButton().enable();
						
						this.updateActiveButton(selected[0]);
						this.updateStaffButton(selected[0]);
					} else {
						this.getEditButton().disable();
						this.getDeleteButton().disable();
						this.getActiveButton().disable();
						this.getStaffButton().disable();
					}
				}.bind(this),
				itemdblclick: function(grid, record, item, index, e, eOpts) {
					this.editUser(record);
					e.stopEvent();
				}.bind(this)
			},
			'userlist grid #new_button': {
				click: function() {
					var user =  Ext.create('Survey.model.auth.User');
					this.editUser(user);
				}.bind(this)
			},
			'userlist grid #edit_button': {
				click: function() {
					var user = this.getUserGrid().getSelectionModel().getSelection()[0];
					this.editUser(user);
				}.bind(this)
			},
			'userlist grid #delete_button': {
				click: function() {
					var user = this.getUserGrid().getSelectionModel().getSelection()[0];
					Ext.MessageBox.confirm(I18N.get('confirm_action'), I18N.get('confirm_user_delete'), Ext.bind(function(result) {
						if (result == "yes") {
							this.getUserGrid().getStore().remove(user);
							this.getUserGrid().getStore().sync({failure: this.getController('Main').syncFailure});
						}
					}, this));
				}
			},
			'userlist grid #active_button': {
				click: function() {
					var user = this.getUserGrid().getSelectionModel().getSelection()[0];
					var confirm_message = user.get('is_active') ? I18N.get('confirm_set_inactive') : I18N.get('confirm_set_active');
					Ext.MessageBox.confirm(I18N.get('confirm_action'), confirm_message, Ext.bind(function(result) {
						if (result == "yes") {
							user.set('is_active', !user.get('is_active'));
							this.updateActiveButton(user);
							this.getUserGrid().getStore().sync({failure: this.getController('Main').syncFailure});
						}
					}, this));
				}
			},
			'userlist grid #staff_button': {
				click: function() {
					var user = this.getUserGrid().getSelectionModel().getSelection()[0];
					var confirm_message = user.get('is_staff') ? I18N.get('confirm_set_not_staff') : I18N.get('confirm_set_staff');
					Ext.MessageBox.confirm(I18N.get('confirm_action'), confirm_message, Ext.bind(function(result) {
						if (result == "yes") {
							user.set('is_staff', !user.get('is_staff'));
							this.updateStaffButton(user);
							this.getUserGrid().getStore().sync({failure: this.getController('Main').syncFailure});
						}
					}, this));
				}
			}
		});
	},
	updateActiveButton: function(selection) {
		if (selection.get('is_active')) {
			this.getActiveButton().setText(I18N.get('set_inactive'));
			this.getActiveButton().setIcon(Config.media_url + 'Survey/img/cross.png')
		} else {
			this.getActiveButton().setText(I18N.get('set_active'));
			this.getActiveButton().setIcon(Config.media_url + 'Survey/img/tick.png')
		}
	},
	updateStaffButton: function(selection) {
		if (selection.get('is_staff')) {
			this.getStaffButton().setText(I18N.get('set_not_staff'));
			this.getStaffButton().setIcon(Config.media_url + 'Survey/img/cross.png')
		} else {
			this.getStaffButton().setText(I18N.get('set_staff'));
			this.getStaffButton().setIcon(Config.media_url + 'Survey/img/tick.png')
		}
	},
	editUser: function(user) {
		this.getController('EditUser').editUser(user);
	},
	refs: [{
		ref: 'users',
		selector: 'users'
	},{
		ref: 'userGrid',
		selector: 'userlist grid'
	},{
		ref: 'newButton',
		selector: 'userlist grid #new_button'
	},{
		ref: 'editButton',
		selector: 'userlist grid #edit_button'
	},{
		ref: 'deleteButton',
		selector: 'userlist grid #delete_button'
	},{
		ref: 'activeButton',
		selector: 'userlist grid #active_button'
	},{
		ref: 'staffButton',
		selector: 'userlist grid #staff_button'
	}],
	stores: ['auth.User']
});
