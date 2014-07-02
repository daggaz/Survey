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
	            xtype: 'textareafield',
	            fieldLabel: I18N.get('help_text'),
				name: 'help_text',
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
            	store: 'QuestionType',
    			queryMode: 'local',
			    displayField: 'name',
			    valueField: 'value',
	            listeners: {
	            	change: function(field, option_type) {
	            		var choices = field.up('#questionform').down('#choicesfield')
	            		var hasChoices = Ext.Array.contains(['choice', 'select', 'bool_list'], option_type);
	            		choices.setVisible(hasChoices);
	            		choices.allowBlank = !hasChoices;
	            		choices.validate();
	            		field.up('editquestion').updatePreview();
	            	}
	            }
            },{
            	xtype: 'textareafield',
            	itemId: 'choicesfield',
            	hidden: true,
	            fieldLabel: I18N.get('choices'),
				name: 'options',
				msgTarget: 'under',
                listeners: {
	            	change: function(field) { field.up('editquestion').updatePreview(); }
	            }
            }
        ],
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
					'background-image': "url('" + Config.media_url + "Survey/img/android-phone.png')"
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
						'background-image': "url('" + Config.media_url + "Survey/img/action-bar.png')",
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
							'background-image': "url('" + Config.media_url + "Survey/img/back-forward-buttons.png')",
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
						bodyPadding: '0 0 10 14',
						html: "* " + I18N.get('preview_required_message'),
						bodyStyle: {	
							'color': 'white',
							'font-size': '11px'
						}
					},{
						itemId: 'help_text',
						margin: '0 14',
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
		preview.down('#actionBar').update(Ext.htmlEncode(I18N.get('question') + ": " + form.label));
		preview.down('#questiontext').update((form.required ? "*" : "") + Ext.htmlEncode(form.question));
		console.log("type: " + form.option_type);
		question.removeAll();
		if (Ext.Array.contains(['char', 'integer', 'float'], form.option_type)) {
			question.add({
				xtype: 'box',
				autoEl: {
					tag: 'img',
					src: Config.media_url + 'Survey/img/text-field.png',
					width: 250,
					height: 34
				}
			});
		} else if (form.option_type == "text") {
			question.add({
				xtype: 'box',
				autoEl: {
					tag: 'img',
					src: Config.media_url + 'Survey/img/textarea-field.png',
					width: 250,
					height: 61
				}
			});
		} else if (Ext.Array.contains(['choice', 'bool_list'], form.option_type)) {
			var options = form.options.split('\n')
			var img = {'choice': 'radio', 'bool_list': 'checkbox'}[form.option_type]; 
			for (var i = 0; i < options.length; i++) {
				if (Ext.String.trim(options[i]) == "")
					continue;
				question.add({
					xtype: 'box',
					autoEl: {
						tag: 'div',
						style: 'padding: 5px 15px; color: white',
						html: '<img style="vertical-align:middle" src="' + Config.media_url + 'Survey/img/' + img + '.png"/> <span>' + Ext.htmlEncode(options[i]) + '</span>'
					}
				});
			}
		} else {
			
		}
		preview.down('#required').setVisible(form.required);
		var help_html = Ext.htmlEncode(form.help_text);
		help_html = help_html.replace(/\n/g, '<br />');
		preview.down('#help_text').update(help_html);
	}
});
