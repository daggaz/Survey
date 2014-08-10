Ext.define('Survey.controller.EditSurveyUsers', {
	extend : 'Ext.app.Controller',
	views: ['EditSurveyUsers', 'UserSelectionDialog'],
	init: function() {
		this.control({
			'editsurveyusers grid': {
				added: function () {
					this.getStore().load();
				},
				reconfigure: function(grid, store) {
					store.load();
				},
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
					Ext.create('widget.userselectiondialog', {
					}).show();
				}.bind(this)
			},
			'editsurveyusers grid #remove_button': {
				click: function() {
//					var question = this.getQuestionGrid().getSelectionModel().getSelection()[0];
//					this.editQuestion(question);
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
					var store = this.getUserGrid().getStore();
					if (store.getNewRecords().length > 0 || store.getModifiedRecords().length > 0 || store.getRemovedRecords().length > 0) {
						store.sync({
							success: function (rec, op) {
								this.getSurveys().getLayout().setActiveItem("surveylist");
							}.bind(this),
							failure: this.getController('Main').syncFailure
						});
					} else {
						this.getSurveys().getLayout().setActiveItem("surveylist");
					}
				}
			}
		});
	},
	editSurveyUsers: function(survey) {
		console.log("editing urers for: " + survey);
		this.getUserGrid().getSelectionModel().deselectAll();
		this.getSurveys().getLayout().setActiveItem("editsurveyusers");
		this.getUserGrid().reconfigure(survey.users());
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
