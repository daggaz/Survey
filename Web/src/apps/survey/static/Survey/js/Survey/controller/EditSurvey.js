Ext.define('Survey.controller.EditSurvey', {
	extend : 'Ext.app.Controller',
	views: ['EditSurvey'],
	init: function() {
		this.control({
			'editsurvey grid': {
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
					}
				}.bind(this),
				itemdblclick: function(grid, question, item, index, e, eOpts) {
					this.editQuestion(question);
					e.stopEvent();
				}.bind(this)
			},
			'editsurvey grid #new_button': {
				click: function() {
					var question =  Ext.create('Survey.model.survey.Question');
					this.editQuestion(question);
				}.bind(this)
			},
			'editsurvey grid #edit_button': {
				click: function() {
					var question = this.getQuestionGrid().getSelectionModel().getSelection()[0];
					this.editQuestion(question);
				}.bind(this)
			},
			'editsurvey grid #delete_button': {
				click: function() {
					var question = this.getQuestionGrid().getSelectionModel().getSelection()[0];
					Ext.MessageBox.confirm(I18N.get('confirm_action'), I18N.get('confirm_question_delete'), Ext.bind(function(result) {
						if (result == "yes") {
							this.getQuestionGrid().getStore().remove(question);
						}
					}, this));
				}
			},
			'editsurvey grid #required_button': {
				click: function() {
					var question = this.getQuestionGrid().getSelectionModel().getSelection()[0];
					var message = question.get('required') ? I18N.get('confirm_set_optional') : I18N.get('confirm_set_required');
					Ext.MessageBox.confirm(I18N.get('confirm_action'), message, Ext.bind(function(result) {
						if (result == "yes") {
							question.set('required', !question.get('required'));
							this.updateRequiredButton(question);
						}
					}, this));
				}
			},
			'editsurvey #cancelButton': {
				click: function () {
					Ext.MessageBox.confirm(I18N.get('confirm_action'), I18N.get('confirm_cancel'), Ext.bind(function(result) {
						if (result == "yes")
							this.getSurveys().getLayout().setActiveItem("surveylist");
					}, this));
				}
			},
			'editsurvey #saveButton': {
				click: function () {
					var survey = this.getSurveyForm().getRecord();
					this.getSurveyForm().updateRecord(survey);
					var questionNames = {};
					var error = false;
					survey.questionsStore.data.each(function(question) {
						if (question.get('label') in questionNames) {
							error = true;
							return false;
						}
						questionNames[question.get('label')] = 1;
					});
					if (error) {
						Ext.MessageBox.show({
                            title: I18N.get('save_error'),
                            msg: I18N.get('unique_question_names'),
                            icon: Ext.MessageBox.ERROR,
                            buttons: Ext.Msg.OK
                        });
                        return;
					}
					var store = this.getSurveySurveyStore();
					console.log("saving " + survey.id);
					console.log(store.getById(survey.id))
					if (survey.phantom)
						store.add(survey);
					var saved = function () {
						var store = this.getQuestionGrid().getStore();
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
					}.bind(this);
					if (store.getNewRecords().length > 0 || store.getModifiedRecords().length > 0 || store.getRemovedRecords().length > 0) {
						store.sync({
							success: function (rec, op) {
								saved();
							}.bind(this),
							failure: this.getController('Main').syncFailure
						});
					} else {
						saved();
					}
				}
			}
		});
	},
	updateRequiredButton: function(selection) {
		if (selection.get('required')) {
			this.getRequiredButton().setText(I18N.get('set_optional'));
			this.getRequiredButton().setIcon('/media/static/Survey/img/cross.png')
		} else {
			this.getRequiredButton().setText(I18N.get('set_required'));
			this.getRequiredButton().setIcon('/media/static/Survey/img/tick.png')
		}
	},
	updateUpButton: function(selection) {
		if (selection.order == 0) {
			this.getUpButton().disable();
		} else {
			this.getUpButton().enable();
		}
	},
	updateDownButton: function(selection) {
		if (selection.order >= this.getQuestionGrid().getStore().getCount()-1) {
			this.getDownButton().disable();
		} else {
			this.getDownButton().enable();
		}
	},
	editSurvey: function(survey) {
		console.log("editing: " + survey);
		this.getSurveyForm().loadRecord(survey);
		this.getQuestionGrid().reconfigure(survey.questions());
		this.getSurveyForm().getForm().clearInvalid();
		this.getSurveys().getLayout().setActiveItem("editsurvey");
	},
	editQuestion: function(question) {
		this.getController('EditQuestion').editQuestion(question, this.getQuestionGrid().getStore());
	},
	refs: [{
		ref: 'surveys',
		selector: 'surveys'
	},{
		ref: 'surveyForm',
		selector: 'editsurvey form'
	},{
		ref: 'questionGrid',
		selector: 'editsurvey grid'
	},{
		ref: 'editButton',
		selector: 'editsurvey grid #edit_button'
	},{
		ref: 'deleteButton',
		selector: 'editsurvey grid #delete_button'
	},{
		ref: 'requiredButton',
		selector: 'editsurvey grid #required_button'
	},{
		ref: 'upButton',
		selector: 'editsurvey grid #up_button'
	},{
		ref: 'downButton',
		selector: 'editsurvey grid #down_button'
	}],
	stores: ['survey.Survey', 'survey.Question']
});
