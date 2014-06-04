from django.db import models
from django.http.response import Http404, HttpResponseBadRequest, HttpResponse
from django.core import serializers
from django.shortcuts import render_to_response

serializer = serializers.get_serializer('extjson')()

from extjs.forms import ReadForm

_field_map = {models.CharField: 'string',
              models.TextField: 'string',
              models.SlugField: 'string',
              models.IntegerField: 'int',
              models.FloatField: 'float',
              models.BooleanField: 'boolean',
              models.DateField: 'date',
              models.DateTimeField: 'string',
              models.ForeignKey: 'int',
              }

def model(request, app, model):
    model_name = model
    model = models.get_model(app, model)
    if model is None:
        raise Http404()
    
    fields = model._meta.fields
    fields = [field for field in fields if field.name != model._meta.pk.name]
    fields = ["{name: '%s', type: '%s'}" % (field.name, _field_map[type(field)]) for field in fields]
    fields = ",\n".join(fields)
    return render_to_response('model.js',
                              {'app': app,
                               'model': model_name,
                               'fields': fields,
                               },
                              content_type="application/json"
                              )

def proxy(request, app, model):
    model = models.get_model(app, model)
    if model is None:
        raise Http404()
    
    read = ReadForm(request.REQUEST)
    if read.is_valid():
        query = model.objects.all()
        start = read.cleaned_data['start']
        limit = read.cleaned_data['limit']
        total = query.count()
        if start and limit is not None:
            query = query[start:start+limit]
        elif start:
            query = query[start:]
        elif limit is not None:
            query = query[:limit]
        return HttpResponse(str(serializer.serialize(query, total=total, ensure_ascii=False)))
    else:
        return HttpResponseBadRequest()
