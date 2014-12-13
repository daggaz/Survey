Ext.define('{{ app_name }}.model.{{ app }}.{{ model }}', {
    extend: 'Ext.data.Model',
    alias: 'model.{{ app }}.{{ model }}',
    requires: [
    ],
    fields: [
    	{{ fields|safe }}
    ],
	associations: [
		{{ associations|safe }}
	],
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
		pageParam: null,
		startParam: null,
		limitParam: null,
		api: {
			{% if related_model %}
			    create  : '{% url 'extjs-m2m-proxy-create' app=app model=source_model related_app=related_app related_model=related_model %}',
			    read    : '{% url 'extjs-m2m-proxy-read' app=app model=source_model related_app=related_app related_model=related_model %}',
			    update  : '{% url 'extjs-m2m-proxy-update' app=app model=source_model related_app=related_app related_model=related_model %}',
			    destroy : '{% url 'extjs-m2m-proxy-destroy' app=app model=source_model related_app=related_app related_model=related_model %}'
			{% else %}
				create  : '{% url 'extjs-proxy-create' app=app model=model %}',
				read    : '{% url 'extjs-proxy-read' app=app model=model %}',
				update  : '{% url 'extjs-proxy-update' app=app model=model %}',
				destroy : '{% url 'extjs-proxy-destroy' app=app model=model %}'
			{% endif %}
		}
	},
});
