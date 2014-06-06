Ext.define('{{ app_name }}.model.{{ app }}.{{ model }}', {
    extend: 'Ext.data.Model',
    alias: 'model.{{ app }}.{{ model }}',
    fields: [{{ fields|safe }}]
});
