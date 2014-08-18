Ext.define('Survey.controller.EditUser', {
	extend : 'Ext.app.Controller',
	views: ['EditUser'],
	init: function() {
		this.control({
			'edituser #cancelButton': {
				click: function () {
					Ext.MessageBox.confirm(I18N.get('confirm_action'), I18N.get('confirm_cancel'), Ext.bind(function(result) {
						if (result == "yes")
							this.getUsers().getLayout().setActiveItem("userlist");
					}, this));
				}
			},
			
			'edituser #saveButton': {
				click: function () {
					var user = this.getUserForm().getRecord();
					this.getUserForm().updateRecord(user);
					
					var checkColumnUnique = function(store, column, extra) {
						var error = false;
						var values = {};
						if (extra !== undefined)
							values[extra] = extra;
						store.data.each(function(item) {
							if (item.get(column) in values) {
								error = true;
								return false;
							}
							values[item.get(column)] = 1;
						});
						return !error;
					};
					var error = null;
					if (!checkColumnUnique(this.getAuthUserStore(), 'username', user.phantom ? user.get('username') : undefined))
						error = I18N.get('unique_user_names');
					
					if (error) {
						Ext.MessageBox.show({
                            title: I18N.get('save_error'),
                            msg: error,
                            icon: Ext.MessageBox.ERROR,
                            buttons: Ext.Msg.OK
                        });
                        return;
					}
					
					var store = this.getAuthUserStore();
					console.log("saving " + user.id);
					console.log(store.getById(user.id))
					var newUser = user.phantom;
					if (newUser) {
						user.set('date_joined', new Date());
						user.set('last_login', new Date());
						store.add(user);
					}
					if (store.getNewRecords().length > 0 || store.getModifiedRecords().length > 0 || store.getRemovedRecords().length > 0) {
						store.sync({
							success: function (rec, op) {
								this.getUsers().getLayout().setActiveItem("userlist");
							}.bind(this),
							failure: this.getController('Main').syncFailure
						});
					} else {
						this.getUsers().getLayout().setActiveItem("userlist");
					}
				}
			}
		});
	},
	editUser: function(user) {
		console.log("editing: " + user);
		this.getUserForm().loadRecord(user);
		this.getUserForm().getForm().clearInvalid();
		this.getUsers().getLayout().setActiveItem("edituser");
	},
	refs: [{
		ref: 'users',
		selector: 'users'
	},{
		ref: 'userForm',
		selector: 'edituser form'
	}],
	stores: ['auth.User']
});
