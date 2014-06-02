
var header = {
    id: 'header',
	region: 'north',
    layout: 'hbox',
    align: 'stretch',
	height: 74,
	
	items: [{
	    xtype: 'image',
	    id: 'logo',
    	src: '/media/static/app/img/logo.png',
    	width: 142
    },{
    	layout: {
    		type: 'hbox',
            align: 'middle',
            pack: 'start'
        },
        items: [{
        	xtype: 'box',
	        id: 'title',
	    	autoEl: { tag: 'div', html: I18N.get('app_title') }
        }],
        height: '100%'
    },{
    	id: 'main_menu',
    	layout: {
    		type: 'hbox',
    		align: 'middle',
    		pack: 'start'
    	},
    	html: 'menu',
    	height: '100%',
    	flex: 1
    }]
};

Ext.define('Survey.view.Main', {
	extend: 'Ext.Panel',
	alias : 'widget.main',
	id: 'main',
	layout : 'border',
	items : [
		header,
	{
		id: 'main_content',
		region: 'center',
		layout: 'fit'
	},{
		
    	id: 'footer',
		html: I18N.get('footer'),
		region: 'south'
	}]
});
