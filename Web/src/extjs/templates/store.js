Ext.define('{{ app_name }}.store.{{ app }}.{{ model }}', {
	extend: 'Ext.data.Store',
	alias: 'store.{{ app }}.{{ model }}',
	model: '{{ app_name }}.model.{{ app }}.{{ model }}'
});
