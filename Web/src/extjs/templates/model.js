Ext.define('Django.model.{{ app }}.{{ model }}', {
    extend: 'Ext.data.Model',
    fields: [{{ fields|safe }}]
});

Ext.define('Djando.store.{{ app }}.{{ model }}', {
	extend: 'Ext.data.JsonStore',
	alias: 'store.Django.survey.Survey',
	model: 'Django.model.{{ app }}.{{ model }}',
	proxy: {
		type: 'ajax',
		url: '{% url 'extjs-proxy' app=app model=model %}'
	}
});
