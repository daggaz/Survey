from django.contrib.auth.decorators import login_required
from django.core.urlresolvers import reverse
from django.http.response import HttpResponseRedirect, HttpResponse
from django.shortcuts import get_object_or_404
from django.template.response import TemplateResponse
import json

from models import Survey
from forms import SubmissionForm, QTYPE_FORM
from apps.survey.models import Question
from django.core import serializers
import itertools

def _get_survey(user, *args, **kwargs):
    survey = get_object_or_404(*args, **kwargs)
    get_object_or_404(survey.users, pk=user.pk)
    return survey

@login_required
def surveys(request):
    open_surveys = Survey.open.filter(users__pk=request.user.pk)
    archived_surveys = Survey.archived.filter(users__pk=request.user.pk)
        
    return TemplateResponse(request, 'surveys.html', {
        'open_surveys': open_surveys,
        'archived_surveys': archived_surveys, 
        })

@login_required
def survey(request, slug):
    survey = _get_survey(request.user, Survey.live, slug=slug)
    
    main_form = SubmissionForm(survey, data=request.POST or None)
    forms = [main_form]
    for question in survey.questions.all().order_by("order"):
        forms.append(QTYPE_FORM[question.option_type](
            question=question,
            prefix='%s_%s' % (question.survey.id, question.id),
            data=request.POST or None,
            ))
    
    if request.method == 'POST' and all(form.is_valid() for form in forms):
        submission = main_form.save(commit=False)
        submission.survey = survey
        submission.user = request.user
        submission.save()
        for form in forms[1:]:
            answers = form.save(commit=False)
            if not hasattr(answers, '__iter__'):
                answers = (answers,)
            for answer in answers:
                answer.submission = submission
                answer.save()
        
        return HttpResponseRedirect(reverse('survey_thanks', kwargs={'slug': slug}))
    
    return TemplateResponse(request, 'survey.html', {
        'survey': survey,
        'forms': forms,
        })

@login_required
def survey_thanks(request, slug):
    survey = _get_survey(request.user, Survey.live, slug=slug)
    
    return TemplateResponse(request, 'survey_thanks.html', {
        'survey': survey,
        })

@login_required
def sync_surveys(request):
    surveys = Survey.live.filter(users=request.user.pk)
    questions = Question.objects.filter(survey__in=surveys)
    data = serializers.serialize('json', itertools.chain(surveys, questions))
    return HttpResponse(data, mimetype='application/json')

@login_required
def sync_survey(request, slug):
    survey = _get_survey(request.user, Survey.live, slug=slug)
    data = survey
    return HttpResponse(json.dumps(data), mimetype='application/json')
