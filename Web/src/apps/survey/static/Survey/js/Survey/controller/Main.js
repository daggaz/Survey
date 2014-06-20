Ext.define('Survey.controller.Main', {
     extend: 'Ext.app.Controller',
     views: ['Main'],
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
					this.currentViewController.navigateFrom(Ext.bind(function() {
						this.showView(view);
					}, this));
				}
			}
     	});
     	
	    Ext.EventManager.addListener(window, 'beforeunload', Ext.bind(function (e) {
	    	var message = I18N.get('confirm_discard_changes');
		    if (this.currentViewController.canNavigateFrom) {
		        if (e) e.returnValue = message;
		        if (window.event) window.event.returnValue = message;
		        return message;
		    }
	    }, this),
	    this, {normalized: false});
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
	    this.currentViewController = this.getController(view);
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
			viewCmp = this.currentViewController.getMainView();
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
     },
     syncFailure: function(batch, opts) {
		var errors = [];
		Ext.each(batch.exceptions, function (exception) {
			var error = exception.getError();
			if (typeof error != 'string') {
				if (error.statusText !== undefined)
					error = error.status + ": " + error.statusText;
				else
					error = "Internal Error!";
			}
			errors.push(error);
		});
		Ext.Msg.alert(I18N.get('save_error'), errors.join("\n"));
     }
});
