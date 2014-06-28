Ext.define('Survey.store.QuestionType', {
	extend: 'Ext.data.Store',
	alias: 'store.QuestionType',
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
});
