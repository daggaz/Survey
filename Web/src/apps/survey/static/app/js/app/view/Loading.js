Ext.define('Survey.view.Loading', {
	extend: 'Ext.Panel',
	alias : 'widget.loading',
	layout: {
		type: 'hbox',
        align: 'middle',
        pack: 'center'
    },
    items: [{
    	xtype: 'box',
    	autoEl: { tag: 'div', html: 'Loading&hellip;' }
    }],
    height: '100%'
});
