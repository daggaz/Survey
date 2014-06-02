# from forms import SubmissionForm, QTYPE_FORM
# from django.contrib.auth.decorators import login_required
# from django.core.urlresolvers import reverse
# from django.template.response import TemplateResponse
# from django.http.response import HttpResponseRedirect, HttpResponse

from django.contrib.auth import authenticate, login
from django.core import serializers
from django.db import transaction
from django.http.response import HttpResponse
from django.shortcuts import get_object_or_404

import json
import traceback

from models import Survey
from apps.survey.models import Question, Submission
from django.template.response import TemplateResponse

def _get_survey(user, *args, **kwargs):
    survey = get_object_or_404(*args, **kwargs)
    get_object_or_404(survey.users, pk=user.pk)
    return survey

def app(request):
    return TemplateResponse(request, 'app.html', {})

def app_resources(request):
    return TemplateResponse(request, 'app_resources.js', {})

# @login_required
# def surveys(request):
#     open_surveys = Survey.open.filter(users__pk=request.user.pk)
#     archived_surveys = Survey.archived.filter(users__pk=request.user.pk)
#         
#     return TemplateResponse(request, 'surveys.html', {
#         'open_surveys': open_surveys,
#         'archived_surveys': archived_surveys, 
#         })
# 
# @login_required
# def survey(request, slug):
#     survey = _get_survey(request.user, Survey.live, slug=slug)
#     
#     main_form = SubmissionForm(survey, data=request.POST or None)
#     forms = [main_form]
#     for question in survey.questions.all().order_by("order"):
#         forms.append(QTYPE_FORM[question.option_type](
#             question=question,
#             prefix='%s_%s' % (question.survey.id, question.id),
#             data=request.POST or None,
#             ))
#     
#     if request.method == 'POST' and all(form.is_valid() for form in forms):
#         submission = main_form.save(commit=False)
#         submission.survey = survey
#         submission.user = request.user
#         submission.save()
#         for form in forms[1:]:
#             answers = form.save(commit=False)
#             if not hasattr(answers, '__iter__'):
#                 answers = (answers,)
#             for answer in answers:
#                 answer.submission = submission
#                 answer.save()
#         
#         return HttpResponseRedirect(reverse('survey_thanks', kwargs={'slug': slug}))
#     
#     return TemplateResponse(request, 'survey.html', {
#         'survey': survey,
#         'forms': forms,
#         })
# 
# @login_required
# def survey_thanks(request, slug):
#     survey = _get_survey(request.user, Survey.live, slug=slug)
#     
#     return TemplateResponse(request, 'survey_thanks.html', {
#         'survey': survey,
#         })

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
            try:
                uuid = response['uuid']
                with transaction.atomic():
                    survey = Survey.objects.get(pk=response['survey_id'])
                    if not survey.submissions.filter(uuid=uuid).exists():
                        submission = Submission()
                        submission.survey = survey
                        submission.uuid = uuid
                        submission.save()
                        for answer_data in response['answers']:
                            question = survey.questions.get(pk=answer_data['question_id'])
                            answer = question.answerFromString(answer_data['value'])
                            answer.submission = submission
                            answer.save()
                    acknowledgments.append(uuid)
            except Exception as e:
                traceback.print_exc(e)
                raise
        
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
