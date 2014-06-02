var I18N = {
	get: function (key) {
		console.log("I18N: " + key + " = " + this.strings[key]);
		return this.strings[key];
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
					name: 'Survey',
					appFolder: '/media/static/app/js/app',
					controllers: ['Main', 'Login'],
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