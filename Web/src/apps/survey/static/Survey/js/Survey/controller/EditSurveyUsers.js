Ext.define('Survey.controller.EditSurveyUsers', {
	extend : 'Ext.app.Controller',
	views: ['EditSurveyUsers'],
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
						this.getEditButton().enable();
						this.getDeleteButton().enable();
						this.getRequiredButton().enable();
						
						this.updateRequiredButton(selected[0]);
						this.updateUpButton(selected[0]);
						this.updateDownButton(selected[0]);
					} else {
						this.getEditButton().disable();
						this.getDeleteButton().disable();
						this.getRequiredButton().disable();
						this.getUpButton().disable();
						this.getDownButton().disable();
					}
				}.bind(this),
				itemdblclick: function(grid, question, item, index, e, eOpts) {
					this.editQuestion(question);
					e.stopEvent();
				}.bind(this)
			},
			'editsurveyusers grid #add_button': {
				click: function() {
//					var question =  Ext.create('Survey.model.survey.Question');
//					this.editQuestion(question);
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
//					var checkColumnUnique = function(store, column, extra) {
//						var error = false;
//						var values = {};
//						if (extra !== undefined)
//							values[extra] = extra;
//						store.data.each(function(item) {
//							if (item.get(column) in values) {
//								error = true;
//								return false;
//							}
//							values[item.get(column)] = 1;
//						});
//						return !error;
////					};
//					var error = null;
//					if (!checkColumnUnique(survey.questionsStore, 'label'))
//						error = I18N.get('unique_question_names');
//					else if (!checkColumnUnique(this.getSurveySurveyStore(), 'title', survey.phantom ? survey.get('title') : undefined))
//						error = I18N.get('unique_survey_names');
//					
//					if (error) {
//						Ext.MessageBox.show({
//                            title: I18N.get('save_error'),
//                            msg: error,
//                            icon: Ext.MessageBox.ERROR,
//                            buttons: Ext.Msg.OK
//                        });
//                        return;
//					}
//					
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
		this.getUserGrid().reconfigure(survey.questions());
		this.getSurveys().getLayout().setActiveItem("editsurveyusers");
	},
	refs: [{
		ref: 'surveys',
		selector: 'surveys'
	},{
		ref: 'userGrid',
		selector: 'editsurveyusers grid'
	},{
		ref: 'editButton',
		selector: 'editsurveyusers grid #add_button'
	},{
		ref: 'deleteButton',
		selector: 'editsurvey grid #remove_button'
	}],
	stores: ['survey.Survey', 'auth.User']
});
