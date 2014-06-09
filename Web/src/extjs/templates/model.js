Ext.define('{{ app_name }}.model.{{ app }}.{{ model }}', {
    extend: 'Ext.data.Model',
    alias: 'model.{{ app }}.{{ model }}',
    fields: [{{ fields|safe }}],
	proxy: {
		type: 'ajax',
		reader: {
			type: 'json',
			successProperty: 'success',
			totalProperty: 'total',
			root: 'objects'
		},
		writer: {
			type: 'json',
			allowSingle: false
		},
		url: '{% url 'extjs-proxy' app=app model=model %}'
	}
});
