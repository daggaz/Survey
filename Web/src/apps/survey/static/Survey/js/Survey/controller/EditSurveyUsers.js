Ext.define('Survey.controller.EditSurveyUsers', {
	extend : 'Ext.app.Controller',
	views: ['EditSurveyUsers', 'UserSelectionDialog'],
	init: function() {
		this.control({
			'editsurveyusers grid': {
				selectionchange: function (grid, selected, opts) {
					if (selected.length == 1) {
						this.getDeleteButton().enable();
					} else {
						this.getDeleteButton().disable();
					}
				}.bind(this)
			},
			'editsurveyusers grid #add_button': {
				click: function() {
					var controller = this;
					var users = Ext.create('Survey.store.auth.User', {
						listeners: {
							load: function (records, operation, success) {
								this.filterBy(function (user) {
									var result = {'value': true};
									controller.getUserGrid().getStore().each(function (other) {
										if (user.get('id') == other.get('id'))
											result['value'] = false;
									});
									return result['value'];
								});
								controller.getUserDialog().show();
							}
						}
					});
					controller.getUserDialog().down('grid').reconfigure(users);
				}.bind(this)
			},
			'editsurveyusers grid #delete_button': {
				click: function() {
					var user = this.getUserGrid().getSelectionModel().getSelection()[0];
					this.getUserGrid().getStore().remove(user);
					var remove = [];
					this.getSurveySurvey_usersStore().each(function (link) {
						if (link.get('survey') == this.survey.get('id') && link.get('user') == user.get('id'))
							remove.push(link);
					}.bind(this));
					Ext.each(remove, function (link) {
						this.getSurveySurvey_usersStore().remove(link);
					}.bind(this));
				}.bind(this)
			},
			'editsurveyusers #cancelButton': {
				click: function () {
					Ext.MessageBox.confirm(I18N.get('confirm_action'), I18N.get('confirm_cancel'), Ext.bind(function(result) {
						if (result == "yes")
							this.getSurveys().getLayout().setActiveItem("surveylist");
					}, this));
				}
			},
			'editsurveyusers #saveButton': {
				click: function () {
					var store = this.getSurveySurvey_usersStore();
					if (store.getNewRecords().length > 0 || store.getModifiedRecords().length > 0 || store.getRemovedRecords().length > 0) {
						store.sync({
							success: function (rec, op) {
								this.getSurveys().down('surveylist').down('grid').getView().refresh();
								this.getSurveys().getLayout().setActiveItem("surveylist");
							}.bind(this),
							failure: this.getController('Main').syncFailure
						});
					} else {
						this.getSurveys().getLayout().setActiveItem("surveylist");
					}
				}
			},
			'userselectiondialog #ok_button': {
				click: function () {
					console.log('bob');
					this.getUserDialog().hide();
					var grid = this.getUserDialog().down('grid');
					Ext.each(grid.getSelectionModel().getSelection(), function (user) {
						console.log(user);
						console.log(user.get('email'));
						var link = Ext.create('Survey.model.survey.Survey_users');
						link.set('survey', this.survey.get('id'));
						link.set('user', user.get('id'));
						this.getSurveySurvey_usersStore().add(link);
						this.getUserGrid().getStore().add(user);
					}.bind(this));
					grid.getSelectionModel().clearSelections();
				}
			},
			'userselectiondialog #cancel_button': {
				click: function () {
					this.getUserDialog().hide();
					var grid = this.getUserDialog().down('grid');
					grid.getSelectionModel().deselectAll();
				}
			}
		});
	},
	editSurveyUsers: function(survey) {
		this.survey = survey;
		console.log("editing users for: " + survey);
		this.getUserGrid().getSelectionModel().deselectAll();
		
		var controller = this;
		this.getSurveySurvey_usersStore().pageSize = null;
		this.getSurveySurvey_usersStore().load(function (records, operation, success) {
			var ids = [];
			Ext.each(records, function (link) {
				if (link.get('survey') == survey.get('id'))
					ids.push(link.get('user'));
			});
			var userIsAssociated = function (user) {
				var result = {'value': false};
				Ext.each(ids, function (id) {
					if (user.get('id') == id)
						result['value'] = true;
				});
				return result['value'];
			};
			var users = Ext.create('Survey.store.auth.User', {
				listeners: {
					load: function (records, operation, success) {
						this.filterBy(userIsAssociated);
						controller.getSurveys().getLayout().setActiveItem("editsurveyusers");
					}
				}
			});
			controller.getUserGrid().reconfigure(users);
		});
	},
	refs: [{
		ref: 'surveys',
		selector: 'surveys'
	},{
		ref: 'userGrid',
		selector: 'editsurveyusers grid'
	},{
		ref: 'addButton',
		selector: 'editsurveyusers grid #add_button'
	},{
		ref: 'deleteButton',
		selector: 'editsurveyusers grid #delete_button'
	},{
		ref: 'userDialog',
		selector: 'editsurveyusers #userdialog'
	}],
	stores: ['survey.Survey', 'survey.Survey_users', 'auth.User']
});
