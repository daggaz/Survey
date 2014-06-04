Ext.define('Survey.controller.Surveys', {
	extend : 'Ext.app.Controller',
	init: function() {
		this.control({
			'survey grid': {
				added: function () {
					this.getStore().load();
				}
			}
		});
	},
	refs: [{
		ref: 'surveysGrid',
		selector: 'survey grid'
	}]
});
