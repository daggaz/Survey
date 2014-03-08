from django.conf.urls import patterns, url

urlpatterns = patterns('',
    url(r'^$', 'apps.core.views.home', name='home'),
)
