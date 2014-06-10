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
		api: {
			create  : '{% url 'extjs-proxy-create' app=app model=model %}',
		    read    : '{% url 'extjs-proxy-read' app=app model=model %}',
		    update  : '{% url 'extjs-proxy-update' app=app model=model %}',
		    destroy : '{% url 'extjs-proxy-destroy' app=app model=model %}'
		}
	}
});
