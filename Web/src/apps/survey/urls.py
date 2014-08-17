from django.conf.urls import patterns, url

urlpatterns = patterns('apps.survey.views',
    url(r'^$', 'app', name='app'),
#     url(r'^surveys/(?P<slug>[-a-z0-9_]+)/$', 'survey', name='survey'),
#     url(r'^surveys/(?P<slug>[-a-z0-9_]+)/thanks/$', 'survey_thanks', name='survey_thanks'),
    
    url(r'^api/app/resources/$', 'app_resources', name='app_resources'),
    url(r'^api/login/$', 'api_login', name='api_login'),
    url(r'^api/login/staff/$', 'api_login', {'staff': True}, name='api_login'),
#     url(r'^api/surveys/$', 'surveys', name='surveys'),
    url(r'^api/surveys/sync/$', 'sync_surveys', name='sync_surveys'),
)
