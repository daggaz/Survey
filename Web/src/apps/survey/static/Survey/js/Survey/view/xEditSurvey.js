Ext.define('Survey.view.EditSurvey', {
	extend: 'Ext.Panel',
	alias : 'widget.editsurvey',
	bodyPadding: '10px 20px 20px 20px',
	overflowY: 'scroll',
	layout: {
		type: 'vbox',
		align: 'stretch'
	},
	items: [{
		layout: {
			type: 'hbox',
			align: 'center'
		},
		items: [{
			xtype: 'box',
			autoEl: {
				tag: 'h1',
				html: I18N.get('edit_survey')
			},
			flex: 1
		},{
			xtype: 'button',
			itemId: 'saveButton',
			text: I18N.get('save')
		}]
	},{
		xtype: 'form',
		itemId: 'surveyform',
		layout: {
			type: 'vbox',
			align: 'stretch',
			defaultMargins: '0 0 10 0'
		},
		items: [
            {
            	xtype: 'textfield',
                fieldLabel: I18N.get('title'),
                name: 'title'
            },
            {
                xtype: 'textareafield',
                fieldLabel: 'Description',
                name: 'description'
            }
        ]
	},{
    	xtype: 'grid',
		title: I18N.get('questions'),
		store: 'survey.Question',
        frame: true,
        minHeight: 200,
        columns:[
        	{
	            text: I18N.get('name'),
	            dataIndex: 'label',
	            width: 100,
	            sortable: false
	        },{
	            text: I18N.get('question'),
	            dataIndex: 'question',
	            width: 300,
	            sortable: false
	        },{
	            text: I18N.get('type'),
	            dataIndex: 'option_type',
	            width: 100,
	            sortable: false
	        },{
	            text: I18N.get('required'),
	            dataIndex: 'option_type',
	            width: 50,
	            renderer: function (value, metaData, record, rowIdx, colIdx, store) {
	            	if (value) {
	            		return '<img src="/media/static/Survey/img/tick.png" />';
				    } else {
				    	return '<img src="/media/static/Survey/img/cross.png" />';
				    }
	            },
	            sortable: false
	        }
	   	],
    	flex: 1,
    	dockedItems: [{
		    xtype: 'toolbar',
		    dock: 'top',
		    items: [{
		        xtype: 'button',
		        itemId: 'new_button',
		        icon: '/media/static/Survey/img/add.png',
		        text: I18N.get('new_question')
		    },{
		        xtype: 'button',
		        itemId: 'delete_button',
		        icon: '/media/static/Survey/img/delete.png',
		        text: I18N.get('delete_question'),
		        disabled: true
		    },{
		        xtype: 'button',
		        itemId: 'required_button',
		        icon: '/media/static/Survey/img/tick.png',
		        text: I18N.get('set_required'),
		        disabled: true
		    },{
		        xtype: 'button',
		        itemId: 'up_button',
		        icon: '/media/static/Survey/img/up.png',
		        text: I18N.get('move_up'),
		        disabled: true
		    },{
		        xtype: 'button',
		        itemId: 'down_button',
		        icon: '/media/static/Survey/img/down.png',
		        text: I18N.get('move_down'),
		        disabled: true
		    }]
		}]
	},{
		xtype: 'form',
		itemid: 'questionform',
		margin: '20 0 0 0',
        minHeight: 200,
		layout: {
			type: 'vbox',
			align: 'stretch',
			defaultMargins: '0 0 10 0'
		},
		frame: true,
		title: I18N.get('edit_question'),
		bodyPadding: '20 20 0 20',
		items: [
        ]
	}]
});
