Ext.define('Survey.controller.Main', {
     extend: 'Ext.app.Controller',
     views: ['Main', 'Login'],
     refs: [{
     	ref: 'MainContent',
     	selector: '#main_content'
     }],
     onLaunch: function () {
     	console.log("onLaunch");
     	Ext.getCmp('loading_viewport').destroy();
     	viewport = this.application.getViewport();
     	viewport.removeAll();
		viewport.add(this.getMainView());
		this.getMainContent().add(this.getLoginView());
     }
});
