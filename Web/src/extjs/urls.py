from django.conf.urls import patterns, url

urlpatterns = patterns('extjs.views',
    url(r'^proxy/(?P<app>[a-zA-Z0-9_]+)/(?P<model>[a-zA-Z0-9_]+)/create/$', 'create', name='extjs-proxy-create'),
    url(r'^proxy/(?P<app>[a-zA-Z0-9_]+)/(?P<model>[a-zA-Z0-9_]+)/read/$', 'read', name='extjs-proxy-read'),
    url(r'^proxy/(?P<app>[a-zA-Z0-9_]+)/(?P<model>[a-zA-Z0-9_]+)/update/$', 'update', name='extjs-proxy-update'),
    url(r'^proxy/(?P<app>[a-zA-Z0-9_]+)/(?P<model>[a-zA-Z0-9_]+)/destroy/$', 'destroy', name='extjs-proxy-destroy'),
    
    url(r'^proxy/(?P<app>[a-zA-Z0-9_]+)/(?P<model>[a-zA-Z0-9_]+)/(?P<related_app>[a-zA-Z0-9_]+)/(?P<related_model>[a-zA-Z0-9_]+)/create/$', 'create_m2m', name='extjs-m2m-proxy-create'),
    url(r'^proxy/(?P<app>[a-zA-Z0-9_]+)/(?P<model>[a-zA-Z0-9_]+)/(?P<related_app>[a-zA-Z0-9_]+)/(?P<related_model>[a-zA-Z0-9_]+)/read/$', 'read_m2m', name='extjs-m2m-proxy-read'),
    url(r'^proxy/(?P<app>[a-zA-Z0-9_]+)/(?P<model>[a-zA-Z0-9_]+)/(?P<related_app>[a-zA-Z0-9_]+)/(?P<related_model>[a-zA-Z0-9_]+)/update/$', 'update_m2m', name='extjs-m2m-proxy-update'),
    url(r'^proxy/(?P<app>[a-zA-Z0-9_]+)/(?P<model>[a-zA-Z0-9_]+)/(?P<related_app>[a-zA-Z0-9_]+)/(?P<related_model>[a-zA-Z0-9_]+)/destroy/$', 'destroy_m2m', name='extjs-m2m-proxy-destroy'),
)
