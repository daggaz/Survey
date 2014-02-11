from django.template.response import TemplateResponse
from crowdsourcing.models import Survey

def surveys(request):
    archived_surveys = Survey.objects.filter(is_published=True)
    live_surveys = Survey.live.all()
    
    if not request.user.is_authenticated():
        live_surveys = live_surveys.filter(require_login=False)
        archived_surveys = archived_surveys.filter(require_login=False)
    
    archived_surveys = archived_surveys.exclude(pk__in=[survey.pk for survey in live_surveys])
    
    return TemplateResponse(request, 'surveys.html', {
        'live_surveys': live_surveys,
        'archived_surveys': archived_surveys, 
        })
