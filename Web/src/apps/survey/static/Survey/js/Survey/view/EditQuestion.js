Ext.define('Survey.view.EditQuestion', {
	extend: 'Ext.Panel',
	alias : 'widget.editquestion',
	bodyPadding: '10px 20px 20px 20px',
//	overflowY: 'scroll',
	autoScroll: true,
	layout: {
		type: 'vbox',
		align: 'stretch'
	},
	items: [{
		layout: {
			type: 'hbox',
			align: 'stretchmax'
		},
		items: [{
			xtype: 'form',
			itemId: 'questionform',
			layout: {
				type: 'vbox',
				align: 'stretch',
				defaultMargins: '0 0 10 0'
			},
			items: [{
				layout: {
					type: 'hbox',
					align: 'bottom'
				},
				items: [{
					xtype: 'box',
					autoEl: {
						tag: 'h1',
						html: I18N.get('edit_question')
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
	            fieldLabel: I18N.get('name'),
	            name: 'label',
				allowBlank: false,
				msgTarget: 'under',
	            listeners: {
	            	change: function(field) { field.up('editquestion').updatePreview(); }
	            }
	        },
	        {
	            xtype: 'textfield',
	            fieldLabel: I18N.get('question'),
	            name: 'question',
				allowBlank: false,
				msgTarget: 'under',
	            listeners: {
	            	change: function(field) { field.up('editquestion').updatePreview(); }
	            }
	        },{
	        	xtype: 'checkboxfield',
                boxLabel: I18N.get('required'),
                name: 'required',
                inputValue: true,
				allowBlank: false,
				msgTarget: 'under',
                listeners: {
	            	change: function(field) { field.up('editquestion').updatePreview(); }
	            }
            },{
            	xtype: 'combobox',
                fieldLabel: I18N.get('question_type'),
            	name: 'option_type',
				forceSelection : true,
				allowBlank: false,
				msgTarget: 'under',
            	store: Ext.create('Ext.data.Store', {
				    fields: ['value', 'name'],
				    data : [
				        {"value": "char", "name": I18N.get('text')},
				        {"value": "text", "name": I18N.get('multi_line_text')},
				        {"value": "integer", "name": I18N.get('number')},
				        {"value": "float", "name": I18N.get('decimal_number')},
				        {"value": "select", "name": I18N.get('drop_down_list')},
				        {"value": "choice", "name": I18N.get('radio_button_list')},
				        {"value": "bool_list", "name": I18N.get('multiple_checkbox_list')}
				    ]
				}),
    			queryMode: 'local',
			    displayField: 'name',
			    valueField: 'value',
	            listeners: {
	            	change: function(field) { field.up('editquestion').updatePreview(); }
	            }
            }],
			flex: 1
		},{
			margin: '0 20',
			width: 1,
			bodyStyle: {
				background: '#444'
			}
		},{
			layout: {
				type: 'vbox',
				align: 'stretch'
			},
			items: [{
				itemId: 'questionpreview',
				width: 272,
				height: 503,
				bodyPadding: '40 11 57 11',
				bodyStyle: {
					'background-image': "url('/media/static/Survey/img/android-phone.png')"
				},
				layout: {
					type: 'vbox',
					align: 'stretch'
				},
				items: [{
					height: 38,
					itemId: 'actionBar',
					bodyPadding: '12 0 0 40',
					bodyStyle: {
						'background-image': "url('/media/static/Survey/img/action-bar.png')",
						'color': 'white'
					}
				},{
					layout: {
						type: 'vbox',
						align: 'stretch'
					},
					bodyStyle: {
						'background': '#002042'
					},
					items: [{
						itemId: 'questiontext',
						bodyPadding: '10 5 10 14',
						bodyStyle: {	
							'color': 'white',
							'font-size': '11px'
						}
					},{
						itemId: 'question'
					},{
						height: 31,
						margin: '10 0',
						bodyStyle: {
							'background-image': "url('/media/static/Survey/img/back-forward-buttons.png')",
							'color': 'white'
						},
						layout: 'hbox',
						bodyPadding: '10 0 0 0',
						items: [{
							margin: '0 0 0 12',
							width: 115,
							html: I18N.get('previous'),
							bodyStyle: {
								'color': 'white',
								'font-size': '11px',
								'text-align': 'center'
							}
						},{
							width: 115,
							html: I18N.get('next'),
							bodyStyle: {
								'color': 'white',
								'font-size': '11px',
								'text-align': 'center'
							}
						}]
					},{
						itemId: 'required',
						bodyPadding: '0 0 0 14',
						html: "* " + I18N.get('preview_required_message'),
						bodyStyle: {	
							'color': 'white',
							'font-size': '11px'
						}
					}],
					flex: 1
				}]
			}]
		}]
	}],
	updatePreview: function() {
		var form = this.down('#questionform').getValues();
		var preview = this.down('#questionpreview');
		var question = preview.down('#question')
		console.log('updating preview');
		preview.down('#actionBar').update(I18N.get('question') + ": " + form.label);
		preview.down('#questiontext').update((form.required ? "*" : "") + form.question);
		console.log("type: " + form.option_type);
		if (form.option_type == 'char') {
			question.removeAll();
			question.add({
				xtype: 'box',
				autoEl: {
					tag: 'img',
					src: '/media/static/Survey/img/text-field.png',
					width: 250,
					height: 34
				}
			})
		} else {
			
		}
		preview.down('#required').setVisible(form.required);
	}
});
