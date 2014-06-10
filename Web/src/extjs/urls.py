from django.conf.urls import patterns, url

urlpatterns = patterns('extjs.views',
    url(r'^proxy/(?P<app>[a-zA-Z0-9_]+)/(?P<model>[a-zA-Z0-9_]+)/create/$', 'create', name='extjs-proxy-create'),
    url(r'^proxy/(?P<app>[a-zA-Z0-9_]+)/(?P<model>[a-zA-Z0-9_]+)/read/$', 'read', name='extjs-proxy-read'),
    url(r'^proxy/(?P<app>[a-zA-Z0-9_]+)/(?P<model>[a-zA-Z0-9_]+)/update/$', 'update', name='extjs-proxy-update'),
    url(r'^proxy/(?P<app>[a-zA-Z0-9_]+)/(?P<model>[a-zA-Z0-9_]+)/destroy/$', 'destroy', name='extjs-proxy-destroy'),
)
