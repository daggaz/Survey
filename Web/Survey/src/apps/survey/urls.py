from django.conf.urls import patterns, include, url

urlpatterns = patterns('',
    url(r'^surveys/$', 'apps.survey.views.surveys', name='surveys'),
    url(r'^surveys/', include('crowdsourcing.urls')),
)
