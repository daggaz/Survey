Ext.define('Survey.controller.Surveys', {
	extend : 'Ext.app.Controller',
	init: function() {
		this.control({
			'surveys grid': {
				added: function () {
					this.getStore().load();
				},
				selectionchange: function (grid, selected, opts) {
					if (selected.length == 1) {
						this.getEditButton().enable();
						var live = this.getLiveButton();
						var open = this.getOpenButton();
						live.enable();
						open.enable();
						this.getDeleteButton().enable();
						
						if (selected[0].data.is_open)
							open.setText(I18N.get('set_closed'));
						else
							open.setText(I18N.get('set_open'));
						
						if (selected[0].data.is_live)
							live.setText(I18N.get('set_hidden'));
						else
							live.setText(I18N.get('set_visible'));
					}
				}.bind(this)
			},
			'surveys grid #new_button': {
				click: function() {
					console.log("new survey clicked");
				}
			},
			'surveys grid #edit_button': {
				click: function() {
					console.log("edit survey clicked");
				}
			},
			'surveys grid #live_button': {
				click: function() {
					console.log("live clicked");
				}
			},
			'surveys grid #open_button': {
				click: function() {
					console.log("open clicked");
				}
			},
			'surveys grid #delete_button': {
				click: function() {
					console.log("delete survey clicked");
				}
			}
		});
	},
	refs: [{
		ref: 'grid',
		selector: 'surveys grid'
	},{
		ref: 'newButton',
		selector: 'surveys grid #new_button'
	},{
		ref: 'editButton',
		selector: 'surveys grid #edit_button'
	},{
		ref: 'liveButton',
		selector: 'surveys grid #live_button'
	},{
		ref: 'openButton',
		selector: 'surveys grid #open_button'
	},{
		ref: 'deleteButton',
		selector: 'surveys grid #delete_button'
	}],
	stores: ['survey.Survey']
});
