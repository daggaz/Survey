Ext.define('Survey.controller.Home', {
	extend : 'Ext.app.Controller',
	views: ['Home'],
	getMainView: function() {
		return this.getHomeView();
	},
	canNavigateFrom: function() {
		return true;
	},
	navigateFrom: function(doNavigate) {
		if (this.canNavigateFrom())
			doNavigate();
	}
});
