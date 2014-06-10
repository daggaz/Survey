Ext.define('Survey.controller.Surveys', {
	extend : 'Ext.app.Controller',
	init: function() {
		this.control({
			'surveys grid': {
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
				}.bind(this)
			},
			'surveys grid #new_button': {
				click: function() {
					console.log("new survey clicked");
				}
			},
			'surveys grid #edit_button': {
				click: function() {
					console.log("edit survey clicked");
				}
			},
			'surveys grid #live_button': {
				click: function() {
					var survey = this.getGrid().getSelectionModel().getSelection()[0];
					var confirm_message = survey.get('is_live') ? I18N.get('confirm_set_hidden') : I18N.get('confirm_set_visible');
					Ext.MessageBox.confirm(I18N.get('confirm_action'), confirm_message, Ext.bind(function(result) {
						if (result == "yes") {
							survey.set('is_live', !survey.get('is_live'));
							this.updateLiveButton(survey);
							this.getGrid().getStore().sync({failure: this.getController('Main').syncFailure});
						}
					}, this));
				}
			},
			'surveys grid #open_button': {
				click: function() {
					var survey = this.getGrid().getSelectionModel().getSelection()[0];
					var confirm_message = survey.get('is_open') ? I18N.get('confirm_set_closed') : I18N.get('confirm_set_open');
					Ext.MessageBox.confirm(I18N.get('confirm_action'), confirm_message, Ext.bind(function(result) {
						if (result == "yes") {
							survey.set('is_open', !survey.get('is_open'));
							this.updateOpenButton(survey);
							this.getGrid().getStore().sync({failure: this.getController('Main').syncFailure});
						}
					}, this));
				}
			},
			'surveys grid #delete_button': {
				click: function() {
					var survey = this.getGrid().getSelectionModel().getSelection()[0];
					Ext.MessageBox.confirm(I18N.get('confirm_action'), I18N.get('confirm_survey_delete'), Ext.bind(function(result) {
						if (result == "yes") {
							this.getGrid().getStore().remove(survey);
							this.getGrid().getStore().sync({failure: this.getController('Main').syncFailure});
						}
					}, this));
				}
			}
		});
	},
	updateLiveButton: function(selection) {
		if (selection.get('is_live')) {
			this.getLiveButton().setText(I18N.get('set_hidden'));
			this.getLiveButton().setIcon('/media/static/Survey/img/hidden.png')
		} else {
			this.getLiveButton().setText(I18N.get('set_visible'));
			this.getLiveButton().setIcon('/media/static/Survey/img/visible.png')
		}
	},
	updateOpenButton: function(selection) {
		if (selection.get('is_open')) {
			this.getOpenButton().setText(I18N.get('set_closed'));
			this.getOpenButton().setIcon('/media/static/Survey/img/closed.png')
		} else {
			this.getOpenButton().setText(I18N.get('set_open'));
			this.getOpenButton().setIcon('/media/static/Survey/img/open.png')
		}
	},
	refs: [{
		ref: 'grid',
		selector: 'surveys grid'
	},{
		ref: 'newButton',
		selector: 'surveys grid #new_button'
	},{
		ref: 'editButton',
		selector: 'surveys grid #edit_button'
	},{
		ref: 'liveButton',
		selector: 'surveys grid #live_button'
	},{
		ref: 'openButton',
		selector: 'surveys grid #open_button'
	},{
		ref: 'deleteButton',
		selector: 'surveys grid #delete_button'
	}],
	stores: ['survey.Survey']
});
