Ext.define('Survey.controller.EditQuestion', {
	extend : 'Ext.app.Controller',
	views: ['EditQuestion'],
	init: function() {
		this.control({
			'editquestion #saveButton': {
				click: function () {
					var question = this.getQuestionForm().getRecord();
					this.getQuestionForm().updateRecord(question);
					if (question.phantom)
						this._store.add(question);
					this.getSurveys().getLayout().setActiveItem("editsurvey");
				}
			},
			'editquestion #cancelButton': {
				click: function () {
					Ext.MessageBox.confirm(I18N.get('confirm_action'), I18N.get('confirm_cancel'), Ext.bind(function(result) {
						if (result == "yes")
							this.getSurveys().getLayout().setActiveItem("editsurvey");
					}, this));
				}
			}
		});
	},
	editQuestion: function(question, store) {
		console.log("editing: " + question);
		this.getQuestionForm().loadRecord(question);
		this._store = store;
		this.getSurveys().getLayout().setActiveItem("editquestion");
	},
	refs: [{
		ref: 'surveys',
		selector: 'surveys'
	},{
		ref: 'questionForm',
		selector: 'editquestion form'
	}],
	stores: ['survey.Question']
});
