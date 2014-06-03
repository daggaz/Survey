
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
    	hidden: true,
    	layout: {
    		type: 'hbox',
    		align: 'stretch',
    		pack: 'start'
    	},
    	items: [{
    		xtype: 'button',
    		text: I18N.get('home'),
    		view: 'Home',
    		handler: function () {
				this.fireEvent('selected', this.view);
			}
    	},{
    		xtype: 'button',
    		text: I18N.get('Surveys'),
    		view: 'Surveys',
    		handler: function () {
				this.fireEvent('selected', this.view);
			}
    	},{
    		xtype: 'button',
    		text: I18N.get('Users'),
    		view: 'Users',
    		handler: function () {
				this.fireEvent('selected', this.view);
			}
    	}],
    	height: '100%',
    	flex: 1
    },{
    	id: 'header_right',
    	hidden: true,
    	layout: {
    		type: 'hbox',
    		align: 'middle',
    		pack: 'start'
    	},
    	items: [{
	    	xtype: 'button',
	    	id: 'logout_button',
			text: I18N.get('logout'),
			handler: function () {
				Ext.MessageBox.confirm(I18N.get('logout'), I18N.get('confirm_logout'), Ext.bind(function(result) {
					if (result == "yes")
						this.fireEvent('logout');
				}, this));
			}
    	}],
    	height: '100%'
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
