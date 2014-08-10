Ext.define('Survey.view.UserSelectionDialog', {
    extend: 'Ext.window.Window',
    alias : 'widget.userselectiondialog',
    title: I18N.get('select_user'),
    height: 142,
    width: 270,
    layout: 'fit',
    modal: true,
    resizable: false,
    closable: false,
    items: [
    ],
    buttons: [{
    	itemId: 'ok_button',
    	text: 'OK',
    },{
    	itemId: 'cancel_button',
    	text: 'Cancel',
    	handler: function () {
    		console.log(this);
    		this.up('.window').close();
    	}
    }]
});
