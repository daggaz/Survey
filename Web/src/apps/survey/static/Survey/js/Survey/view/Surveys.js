Ext.define('Survey.view.Surveys', {
	extend: 'Ext.Panel',
	alias : 'widget.surveys',
	layout: 'card',
	items: [{
		xtype: 'surveylist',
		itemId: 'surveylist'
	},{
		xtype: 'editsurvey',
		itemId: 'editsurvey'
	}]
});
