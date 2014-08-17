var console = console || {log: function () {}};
//Ext.Loader.config.disableCaching = false;
var I18N = {
	get: function (key) {
		var value = this.strings[key];
		if (value === undefined)
			console.log("I18N missing translation:" + key);
		return this.strings[key] || key;
	},
	strings: {}
};
var Config = {

};
Ext.onReady(function () {
	Ext.create('Ext.Viewport', {
		id: 'loading_viewport',
		layout: 'fit',
		items:[{
			cls: 'loading',
			layout: {
				type: 'hbox',
		        align: 'middle',
		        pack: 'center'
		    },
		    items: [{
		    	xtype: 'box',
		    	autoEl: { tag: 'div', html: 'Loading&hellip;' }
		    }],
		    height: '100%'
		}]
	});

Ext.Ajax.request({
		url: '/api/app/resources/',
		callback: function (options, success, response) {
			if (success) {
				data = Ext.decode(response.responseText);
				I18N.strings = data['strings'];
				Config.media_url = data['media_url'];
				Ext.application({
					requires: ['Ext.grid.*', 'Ext.data.*', 'Ext.util.*', 'Ext.util.JSON.*', 'Ext.state.*', 'Ext.window.*'],
					models: ['survey.Survey', 'survey.Question', 'survey.Submission'],
					name: 'Survey',
					appFolder: Config.media_url + 'Survey/js/Survey',
					controllers: ['Main', 'Login', 'Surveys', 'Home', 'EditSurvey', 'EditQuestion', 'EditSurveyUsers', 'Users'],
					autoCreateViewport: true,
					refs: [{
						ref: 'viewport',
						selector: 'viewport'
					}]
				});
			} else {
				alert("Error!");
			}
		}
	});
});
