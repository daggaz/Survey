from django.contrib.auth import authenticate, login
from django.contrib.auth.decorators import login_required
from django.core.urlresolvers import reverse
from django.http.response import HttpResponseRedirect, HttpResponse
from django.shortcuts import get_object_or_404
from django.template.response import TemplateResponse
from django.core import serializers

import json

from models import Survey
from forms import SubmissionForm, QTYPE_FORM
from apps.survey.models import Question

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

def api_login(request):
    user = authenticate(username=request.GET.get('username'),
                        password=request.GET.get('password'),
                        )
    if user is not None:
        if user.is_active:
            login(request, user)
            data = {'status': 'success',
                    'session_key': request.session.session_key,
                    }
        else:
            data = {'status': 'failed',
                    'reason': 'account disabled',
                    }
    else:
        data = {'status': 'failed',
                'reason': 'invalid username/password',
                }
    return HttpResponse(json.dumps(data), mimetype='application/json')

def sync_surveys(request):
    if request.user.is_authenticated():
        responses = json.loads(request.POST['responses'])
        acknowledgments = []
        for response in responses:
            print response
            acknowledgments.append(response['uuid'])
        
        surveys = Survey.live.filter(users=request.user.pk)
        questions = Question.objects.filter(survey__in=surveys)
        surveys = json.loads(serializers.serialize('json', surveys))
        questions = json.loads(serializers.serialize('json', questions))
        data = {'status': 'success',
                'surveys': surveys,
                'questions': questions,
                'acknowledgments': acknowledgments
                }
    else:
        data = {'status': 'failed',
                'reason': 'not authorized',
                }
    return HttpResponse(json.dumps(data), mimetype='application/json')

def sync_survey(request, slug):
    if request.user.is_authenticated():
        survey = _get_survey(request.user, Survey.live, slug=slug)
        data = {'status': 'success',
                'data': survey,
                }
    else:
        data = {'status': 'failed',
                'reason': 'not authorized',
                }
    return HttpResponse(json.dumps(data), mimetype='application/json')
