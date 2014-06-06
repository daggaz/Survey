from django.conf.urls import patterns, url

urlpatterns = patterns('extjs.views',
    url(r'^proxy/(?P<app>[a-zA-Z0-9_]+)/(?P<model>[a-zA-Z0-9_]+)/$', 'proxy', name='extjs-proxy'),
)
