Ext.define('Survey.controller.Main', {
     extend: 'Ext.app.Controller',
     views: ['Main', 'Login', 'Home'],
     refs: [{
     	ref: 'MainContent',
     	selector: '#main_content'
     },{
     	ref: 'MainMenu',
     	selector: '#main_menu'
     }],
     onLaunch: function () {
     	console.log("onLaunch");
     	Ext.getCmp('loading_viewport').destroy();
     	viewport = this.application.getViewport();
     	viewport.removeAll();
		viewport.add(this.getMainView());
		this.showView('Login');
     },
     viewShowing: false,
     showView: function (view, callback) {
     	var viewShowing = this.viewShowing; 
	    this.viewShowing = true;
     	if (viewShowing)
	     	this.getMainContent().el.fadeOut({duration: 300, easing: 'easeInOut', callback: this._doShowView(view, true, callback)});
	    else
	    	this._doShowView(view, false, callback)();
     },
     _doShowView: function(view, animate, callback) {
     	controller = this;
     	return function() {
			controller.getMainContent().removeAll();
	     	controller.getMainContent().add(controller['get' + view + 'View']());
	     	if (animate)
	     		controller.getMainContent().el.fadeIn({duration: 600, easing: 'easeInOut', callback: callback});
	     	else
	     		callback();
     	}
 	 },
     showMenu: function () {
     	this.getMainMenu().el.fadeIn({duration: 1500});
     }
});
