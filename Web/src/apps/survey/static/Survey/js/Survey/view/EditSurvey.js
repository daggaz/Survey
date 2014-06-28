Ext.define('Survey.view.EditSurvey', {
	extend: 'Ext.Panel',
	alias : 'widget.editsurvey',
	bodyPadding: '10px 20px 20px 20px',
//	overflowY: 'scroll',
	autoScroll: true,
	layout: {
		type: 'vbox',
		align: 'stretch'
	},
	items: [{
		xtype: 'form',
		itemId: 'surveyform',
		layout: {
			type: 'vbox',
			align: 'stretch',
			defaultMargins: '0 0 10 0'
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
				itemId: 'cancelButton',
				margin: '0 10 0 0',
				text: I18N.get('cancel')
			},{
				xtype: 'button',
				itemId: 'saveButton',
				formBind: true,
				text: I18N.get('save')
			}]
		},{
        	xtype: 'textfield',
            fieldLabel: I18N.get('title'),
			allowBlank: false,
			msgTarget: 'under',
            name: 'title'
        },{
            xtype: 'textareafield',
            fieldLabel: I18N.get('description'),
			allowBlank: false,
			msgTarget: 'under',
            name: 'description'
        }]
	},{
    	xtype: 'grid',
		title: I18N.get('questions'),
        frame: true,
        minHeight: 200,
        columns:[
        	{
	            text: '#',
	            dataIndex: 'order',
	            width: 50,
	            sortable: false
	        },{
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
	            dataIndex: 'required',
	            width: 80,
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
		        icon: Config.media_url + 'Survey/img/add.png',
		        text: I18N.get('new_question')
		    },{
		        xtype: 'button',
		        itemId: 'delete_button',
		        icon: Config.media_url + 'Survey/img/delete.png',
		        text: I18N.get('delete_question'),
		        disabled: true
		    },{
		        xtype: 'button',
		        itemId: 'edit_button',
		        icon: Config.media_url + 'Survey/img/edit.png',
		        text: I18N.get('edit_question'),
		        disabled: true
		    },{
		        xtype: 'button',
		        itemId: 'required_button',
		        icon: Config.media_url + 'Survey/img/tick.png',
		        text: I18N.get('set_required'),
		        disabled: true
		    },{
		        xtype: 'button',
		        itemId: 'up_button',
		        icon: Config.media_url + 'Survey/img/up.png',
		        text: I18N.get('move_up'),
		        disabled: true
		    },{
		        xtype: 'button',
		        itemId: 'down_button',
		        icon: Config.media_url + 'Survey/img/down.png',
		        text: I18N.get('move_down'),
		        disabled: true
		    }]
		}]
	}]
});
