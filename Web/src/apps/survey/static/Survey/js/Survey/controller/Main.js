Ext.define('Survey.controller.Main', {
     extend: 'Ext.app.Controller',
     views: ['Main', 'Login', 'Home', 'Surveys'],
     refs: [{
     	ref: 'MainContent',
     	selector: '#main_content'
     },{
     	ref: 'MainMenu',
     	selector: '#main_menu'
     },{
     	ref: 'HeaderRight',
     	selector: '#header_right'
     }],
     init: function () {
     	this.control({
			'main #logout_button': {
				logout: function() {
					console.log("logout event");
					Survey.getApplication().getLoginController().logoutUser();
				}
			},
			'main #main_menu button': {
				selected: function(view) {
					this.showView(view);
				}
			}
     	});
     },
     onLaunch: function () {
     	Ext.getCmp('loading_viewport').destroy();
     	viewport = this.application.getViewport();
     	viewport.removeAll();
		viewport.add(this.getMainView());
		this.showView('Login', Ext.bind(function () {
			this.getMainContent().down('textfield').focus();
		}, this));
     },
     viewShowing: false,
     showView: function (view, callback) {
     	var viewShowing = this.viewShowing;
	    this.viewShowing = true;
     	if (viewShowing)
	     	this.getMainContent().el.fadeOut({opacity: 0.1, duration: 300, easing: 'easeInOut', callback: this._doShowView(view, true, callback)});
	    else
	    	this._doShowView(view, false, callback)();
     },
     _doShowView: function(view, animate, callback) {
     	console.log("loading view " + view)
     	callback = callback || function () {};
     	return Ext.bind(function() {
			this.getMainContent().removeAll();
			viewCmp = this['get' + view + 'View']();
	     	this.getMainContent().add(viewCmp);
	     	this.getMainContent().doLayout();
	     	Ext.ComponentQuery.query('main #main_menu button[view!='+view+']').forEach(function (cmp) {
	     		cmp.el.removeCls('selected');
	     	});
			Ext.ComponentQuery.query('main #main_menu button[view='+view+']').forEach(function (cmp) {
	     		cmp.el.addCls('selected');
	     	});
			if (animate)
	     		this.getMainContent().el.fadeIn({duration: 600, easing: 'easeInOut', callback: callback});
			else
	     		callback();
     	}, this);
 	 },
     showHeader: function () {
     	this.getMainMenu().show();
     	this.getHeaderRight().show();
     	this.getMainMenu().el.fadeIn({duration: 1500});
     	this.getHeaderRight().el.fadeIn({duration: 1500});
     },
     hideHeader: function () {
     	this.getMainMenu().hide();
     	this.getHeaderRight().hide();
     }
});
