Ext.define('Survey.view.Surveys', {
	extend: 'Ext.Panel',
	alias : 'widget.surveys',
	bodyPadding: '10px 20px 20px 20px',
	layout: {
		type: 'vbox',
		align: 'stretch'
	},
	items: [{
		xtype: 'box',
		autoEl: {
			tag: 'h1',
			html: I18N.get('surveys')
		}
	},{
		xtype: 'grid',
		title: I18N.get('surveys'),
		store: 'survey.Survey',
        disableSelection: true,
        loadMask: true,
        frame: true,
        viewConfig: {
	        emptyText: I18N.get('no_surveys'),
            trackOver: false,
            stripeRows: false,
//            plugins: [{
//                ptype: 'preview',
//                bodyField: 'excerpt',
//                expanded: true,
//                pluginId: 'preview'
//            }]
        },
		listeners : {
			render : function(grid) {
				grid.body.mask('Loading&helip;');
				grid.getStore().load(function () {
					grid.body.unmask();
				});
			},
			delay: 200
		},
        columns:[{
            text: I18N.get('title'),
            dataIndex: 'title',
            width: 400,
            sortable: true
        },{
            text: "Replies",
            dataIndex: 'replycount',
            width: 70,
            align: 'right',
            sortable: true
        },{
            text: "Last Post",
            dataIndex: 'lastpost',
            width: 150,
            sortable: true
        }],
    	flex: 1
	}]
});
