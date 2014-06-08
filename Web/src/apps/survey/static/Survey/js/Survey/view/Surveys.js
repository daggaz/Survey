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
        loadMask: true,
        frame: true,
        viewConfig: {
	        emptyText: I18N.get('no_surveys'),
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
            width: 300,
            sortable: true
        },{
            text: I18N.get('live'),
            dataIndex: 'is_live',
            width: 70,
            sortable: true,
            tooltip: I18N.get('live_column_tip'),
            renderer: function (value, metaData, record, rowIdx, colIdx, store) {
            	if(value) {
            		metaData.tdAttr = 'data-qtip="' + I18N.get('survey_live') + '"';
			    	return '<img src="/media/static/Survey/img/tick.png" />';
			    } else {
			    	metaData.tdAttr = 'data-qtip="' + I18N.get('survey_not_live') + '"';
			    	return '<img src="/media/static/Survey/img/cross.png" />';
			    }
            }
        },{
            text: I18N.get('open'),
            dataIndex: 'is_open',
            width: 70,
            sortable: true,
            tooltip: I18N.get('open_column_tip'),
            renderer: function (value, metaData, record, rowIdx, colIdx, store) {
            	if(value) {
            		metaData.tdAttr = 'data-qtip="' + I18N.get('survey_open') + '"';
			    	return '<img src="/media/static/Survey/img/tick.png" />';
			    } else {
			    	metaData.tdAttr = 'data-qtip="' + I18N.get('survey_not_open') + '"';
			    	return '<img src="/media/static/Survey/img/cross.png" />';
			    }
            }
        }],
    	flex: 1,
    	dockedItems: [{
		    xtype: 'toolbar',
		    dock: 'top',
		    items: [{
		        xtype: 'button',
		        itemId: 'new_button',
		        icon: '/media/static/Survey/img/add.png',
		        text: I18N.get('new_survey')
		    },{
		        xtype: 'button',
		        itemId: 'edit_button',
		        icon: '/media/static/Survey/img/edit.png',
		        text: I18N.get('edit_survey'),
		        disabled: true
		    },{
		        xtype: 'button',
		        itemId: 'delete_button',
		        icon: '/media/static/Survey/img/delete.png',
		        text: I18N.get('delete_survey'),
		        disabled: true
		    },{
		        xtype: 'button',
		        itemId: 'live_button',
		        icon: '/media/static/Survey/img/live.png',
		        text: I18N.get('set_visible'),
		        disabled: true
		    },{
		        xtype: 'button',
		        itemId: 'open_button',
		        icon: '/media/static/Survey/img/open.png',
		        text: I18N.get('set_open'),
		        disabled: true
		    }]
		}]
	}]
});
