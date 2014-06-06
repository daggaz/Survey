from django.db import models
from django.http.response import Http404, HttpResponseBadRequest, HttpResponse
from django.core import serializers

serializer = serializers.get_serializer('extjson')()

from extjs.forms import ReadForm

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
