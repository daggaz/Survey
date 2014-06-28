Ext.define('Survey.controller.Surveys', {
	extend : 'Ext.app.Controller',
	views: ['Surveys', 'SurveyList'],
	getMainView: function() {
		return this.getSurveysView();
	},
	canNavigateFrom: function () {
		if (this.getSurveys().getLayout().getActiveItem().itemId == "surveylist")
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
			'surveylist grid': {
				added: function () {
					this.getStore().load();
				},
				selectionchange: function (grid, selected, opts) {
					if (selected.length == 1) {
						this.getEditButton().enable();
						this.getDeleteButton().enable();
						this.getLiveButton().enable();
						this.getOpenButton().enable();
						
						this.updateOpenButton(selected[0]);
						this.updateLiveButton(selected[0]);
					} else {
						this.getEditButton().disable();
						this.getDeleteButton().disable();
						this.getLiveButton().disable();
						this.getOpenButton().disable();						
					}
				}.bind(this),
				itemdblclick: function(grid, record, item, index, e, eOpts) {
					this.editSurvey(record);
					e.stopEvent();
				}.bind(this)
			},
			'surveylist grid #new_button': {
				click: function() {
					var survey =  Ext.create('Survey.model.survey.Survey');
					this.editSurvey(survey);
				}.bind(this)
			},
			'surveylist grid #edit_button': {
				click: function() {
					var survey = this.getSurveyGrid().getSelectionModel().getSelection()[0];
					this.editSurvey(survey);
				}.bind(this)
			},
			'surveylist grid #live_button': {
				click: function() {
					var survey = this.getSurveyGrid().getSelectionModel().getSelection()[0];
					var confirm_message = survey.get('is_live') ? I18N.get('confirm_set_hidden') : I18N.get('confirm_set_visible');
					Ext.MessageBox.confirm(I18N.get('confirm_action'), confirm_message, Ext.bind(function(result) {
						if (result == "yes") {
							survey.set('is_live', !survey.get('is_live'));
							this.updateLiveButton(survey);
							this.getSurveyGrid().getStore().sync({failure: this.getController('Main').syncFailure});
						}
					}, this));
				}
			},
			'surveylist grid #open_button': {
				click: function() {
					var survey = this.getSurveyGrid().getSelectionModel().getSelection()[0];
					var confirm_message = survey.get('is_open') ? I18N.get('confirm_set_closed') : I18N.get('confirm_set_open');
					Ext.MessageBox.confirm(I18N.get('confirm_action'), confirm_message, Ext.bind(function(result) {
						if (result == "yes") {
							survey.set('is_open', !survey.get('is_open'));
							this.updateOpenButton(survey);
							this.getSurveyGrid().getStore().sync({failure: this.getController('Main').syncFailure});
						}
					}, this));
				}
			},
			'surveylist grid #delete_button': {
				click: function() {
					var survey = this.getSurveyGrid().getSelectionModel().getSelection()[0];
					Ext.MessageBox.confirm(I18N.get('confirm_action'), I18N.get('confirm_survey_delete'), Ext.bind(function(result) {
						if (result == "yes") {
							this.getSurveyGrid().getStore().remove(survey);
							this.getSurveyGrid().getStore().sync({failure: this.getController('Main').syncFailure});
						}
					}, this));
				}
			}
		});
	},
	updateLiveButton: function(selection) {
		if (selection.get('is_live')) {
			this.getLiveButton().setText(I18N.get('set_hidden'));
			this.getLiveButton().setIcon(Config.media_url + 'Survey/img/hidden.png')
		} else {
			this.getLiveButton().setText(I18N.get('set_visible'));
			this.getLiveButton().setIcon(Config.media_url + 'Survey/img/visible.png')
		}
	},
	updateOpenButton: function(selection) {
		if (selection.get('is_open')) {
			this.getOpenButton().setText(I18N.get('set_closed'));
			this.getOpenButton().setIcon(Config.media_url + 'Survey/img/closed.png')
		} else {
			this.getOpenButton().setText(I18N.get('set_open'));
			this.getOpenButton().setIcon(Config.media_url + 'Survey/img/open.png')
		}
	},
	editSurvey: function(survey) {
		this.getController('EditSurvey').editSurvey(survey);
	},
	refs: [{
		ref: 'surveys',
		selector: 'surveys'
	},{
		ref: 'surveyGrid',
		selector: 'surveylist grid'
	},{
		ref: 'newButton',
		selector: 'surveylist grid #new_button'
	},{
		ref: 'editButton',
		selector: 'surveylist grid #edit_button'
	},{
		ref: 'liveButton',
		selector: 'surveylist grid #live_button'
	},{
		ref: 'openButton',
		selector: 'surveylist grid #open_button'
	},{
		ref: 'deleteButton',
		selector: 'surveylist grid #delete_button'
	}],
	stores: ['survey.Survey']
});
