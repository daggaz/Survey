Ext.define('Survey.view.Viewport', {
     extend: 'Ext.container.Viewport',
     requires: ['Survey.view.Loading'],
     layout: 'fit',
     alias: 'viewport',
     items: [{xtype:'loading'}]
});
