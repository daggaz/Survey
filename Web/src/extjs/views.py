from django.db import models
from django.http.response import Http404, HttpResponseBadRequest, HttpResponse,\
    HttpResponseNotAllowed
from django.core import serializers
import json
from django.contrib.comments import get_model

deseralizer = serializers.get_deserializer('extjson')

from extjs.forms import ReadForm

def _get_model_or_404(app, model):
    model = models.get_model(app, model)
    if model is None:
        raise Http404()
    return model

def read(request, app, model):
    model = _get_model_or_404(app, model)
    
    if request.method == "GET":
        read = ReadForm(request.GET)
        if read.is_valid():
            query = model.objects.all()
            
            filter = read.cleaned_data['filter'].strip()
            if filter:
                filter = json.loads(filter)
                print "filter: %s" % filter
                conditions = {}
                for condition in filter:
                    conditions[condition['property']] = condition['value']
                query = query.filter(**conditions)

            total = query.count()
            
            start = read.cleaned_data['start']
            limit = read.cleaned_data['limit']
            if start and limit is not None:
                query = query[start:start+limit]
            elif start:
                query = query[start:]
            elif limit is not None:
                query = query[:limit]
            
            print query
            #print query.query
            
            serializer = serializers.get_serializer('extjson')()
            result = str(serializer.serialize(query, total=total, ensure_ascii=False))
            print "xxx: %s" % result
            return HttpResponse(result, content_type='application/json')
        else:
            return HttpResponseBadRequest()
    else:
        return HttpResponseNotAllowed()

def update(request, app, model):
    model = _get_model_or_404(app, model)
    
    if request.method == "POST":
        if request.is_ajax():
            print request.body
            for obj in deseralizer(request.body, model):
                obj.save()
            return HttpResponse()
        else:
            return HttpResponseBadRequest()
    else:
        return HttpResponseNotAllowed()

def destroy(request, app, model):
    model = _get_model_or_404(app, model)
    
    if request.method == "POST":
        if request.is_ajax():
            print request.body
            model.objects.filter(pk__in=[getattr(obj.object, model._meta.pk.name) for obj in deseralizer(request.body, model)]).delete()
            return HttpResponse()
        else:
            return HttpResponseBadRequest()
    else:
        return HttpResponseNotAllowed()
    
def create(request, app, model):
    model = _get_model_or_404(app, model)
    
    if request.method == "POST":
        if request.is_ajax():
            print request.body
            saved = []
            for obj in deseralizer(request.body, model):
                obj.save()
                saved.append(obj.object)
            serializer = serializers.get_serializer('extjson')()
            return HttpResponse(str(serializer.serialize(saved, ensure_ascii=False)), content_type='application/json')
        else:
            return HttpResponseBadRequest()
    else:
        return HttpResponseNotAllowed()

def create_m2m(request, app, model, related_app, related_model):
    pass

def read_m2m(request, app, model, related_app, related_model):
    model = _get_model_or_404(app, model)
    related_model = _get_model_or_404(related_app, related_model)
    
    joining_model = None
    for field in model._meta.many_to_many:
        if field.rel.to._meta == related_model._meta:
            joining_model = field.rel.through
            break
    if not joining_model:
        raise Http404()
    print joining_model
    
    if request.method == "GET":
        read = ReadForm(request.GET)
        if read.is_valid():
            query = joining_model.objects.all()
            
            filter = read.cleaned_data['filter'].strip()
            if filter:
                filter = json.loads(filter)
                print "filter: %s" % filter
                conditions = {}
                for condition in filter:
                    conditions[condition['property']] = condition['value']
                query = query.filter(**conditions)
            
            start = read.cleaned_data['start']
            limit = read.cleaned_data['limit']
            if start and limit is not None:
                query = query[start:start+limit]
            elif start:
                query = query[start:]
            elif limit is not None:
                query = query[:limit]
            
            #print query.query
            print query
            total = query.count()
            
            serializer = serializers.get_serializer('extjson')()
            result = str(serializer.serialize(query, total=total, ensure_ascii=False))
            print "xxx: %s" % result
            return HttpResponse(result, content_type='application/json')
        else:
            return HttpResponseBadRequest()
    else:
        return HttpResponseNotAllowed()

def update_m2m(request, app, model, related_app, related_model):
    pass

def destroy_m2m(request, app, model, related_app, related_model):
    pass