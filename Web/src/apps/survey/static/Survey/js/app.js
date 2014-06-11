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
}
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
				I18N.strings = Ext.decode(response.responseText)['strings'];
				Ext.application({
					requires: ['Ext.grid.*', 'Ext.data.*', 'Ext.util.*', 'Ext.util.JSON.*', 'Ext.state.*'],
					models: ['survey.Survey'],
					name: 'Survey',
					appFolder: '/media/static/Survey/js/Survey',
					controllers: ['Main', 'Login', 'Surveys', 'Home'],
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
