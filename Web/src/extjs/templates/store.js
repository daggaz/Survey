Ext.define('{{ app_name }}.store.{{ app }}.{{ model }}', {
	extend: 'Ext.data.Store',
	alias: 'store.{{ app }}.{{ model }}',
	model: '{{ app_name }}.model.{{ app }}.{{ model }}',
	proxy: {
		type: 'ajax',
		reader: {
			type: 'json',
			successProperty: 'success',
			totalProperty: 'total',
			root: 'objects'
		},
		url: '{% url 'extjs-proxy' app=app model=model %}'
	}
});
