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

def api_login(request, staff=False):
    user = authenticate(username=request.GET.get('username'),
                        password=request.GET.get('password'),
                        )
    if user is not None:
        if user.is_active:
            if not staff or (staff and user.is_staff):
                login(request, user)
                data = {'status': 'success',
                        'session_key': request.session.session_key,
                        }
            else:
                data = {'status': 'failed',
                        'reason': 'not authorised',
                        }
        else:
            data = {'status': 'failed',
                    'reason': 'account disabled',
                    }
    else:
        data = {'status': 'failed',
                'reason': 'invalid username/password',
                }
    return HttpResponse(json.dumps(data), content_type='application/json')

def sync_surveys(request):
    if request.user.is_authenticated():
        responses = json.loads(request.POST['responses'])
        acknowledgments = []
        nacks = []
        for response in responses:
            print response
            try:
                uuid = response['uuid']
                with transaction.atomic():
                    try:
                        survey = Survey.objects.get(pk=response['survey_id'])
                    except Survey.DoesNotExist:
                        nacks.append({'uuid': uuid, 'reason': 'Survey %s does not exist' % response['survey_id']})
                        continue
                    if not survey.is_live:
                        nacks.append({'uuid': uuid, 'reason': 'Survey "%s" is not live' % survey.title})
                        continue
                    if not survey.is_open:
                        nacks.append({'uuid': uuid, 'reason': 'Survey "%s" is not open' % survey.title})
                        continue
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
                'acknowledgments': acknowledgments,
                'nacks': nacks,
                }
    else:
        data = {'status': 'failed',
                'reason': 'not authorized',
                }
    return HttpResponse(json.dumps(data), content_type='application/json')
