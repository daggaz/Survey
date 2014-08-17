from django.core.serializers.json import Serializer as JsonSerializer
from django.core.serializers.python import Deserializer as PythonDeserializer
from django.utils.encoding import smart_text
from django.utils import six
from django.core.serializers.base import DeserializationError
from django.db.models import ForeignKey
import sys
import json

class Serializer(JsonSerializer):
    def serialize(self, query, total=None, **kwargs):
        self.total = total
        return super(Serializer, self).serialize(query, **kwargs)
    
    def start_serialization(self):
        self.stream.write("{'success': true, ")
        if self.total is not None:
            self.stream.write("'total': %s, " % (self.total))
        self.stream.write("'objects':")
        super(Serializer, self).start_serialization()
    
    def end_serialization(self):
        super(Serializer, self).end_serialization()
        self.stream.write("}")
        
    def get_dump_object(self, obj):
        result = {
            "id": smart_text(obj._get_pk_val(), strings_only=True),
            }
        result.update(self._current)
        return result

def Deserializer(stream_or_string, Model, **options):
    if not isinstance(stream_or_string, (bytes, six.string_types)):
        stream_or_string = stream_or_string.read()
    if isinstance(stream_or_string, bytes):
        stream_or_string = stream_or_string.decode('utf-8')
    foreign_keys = [field.name for field in Model._meta.fields if isinstance(field, ForeignKey)]
    try:
        data = json.loads(stream_or_string)
        objects = []
        for datum in data:
            pk_name = Model._meta.pk.name
            if pk_name in datum:
                pk = datum[pk_name]
                del datum[pk_name]
            else:
                pk = None
            for foreign_key in foreign_keys:
                if foreign_key in datum and datum[foreign_key] == 0:
                    datum[foreign_key] = None
            obj = {'model': "%s.%s" % (Model._meta.app_label, Model._meta.object_name),
                   'pk': pk,
                   'fields': datum,
                   }
            print obj
            objects.append(obj)
        for obj in PythonDeserializer(objects, **options):
            yield obj
    except GeneratorExit:
        raise
    except Exception as e:
        # Map to deserializer error
        six.reraise(DeserializationError, DeserializationError(e), sys.exc_info()[2])
        