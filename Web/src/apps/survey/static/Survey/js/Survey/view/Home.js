Ext.define('Survey.view.Home', {
	extend: 'Ext.Panel',
	alias : 'widget.home',
	bodyPadding: '10px 20px',
	html: '<h1>' + I18N.get('welcome') + '</h1>' +
		  '<p>' + I18N.get('welcome_message') + '</p>' +
		  '<h2>' + I18N.get('getting_started') + '</h2>' +
		  '<p>' + I18N.get('getting_started_message') + '</p>'
});
