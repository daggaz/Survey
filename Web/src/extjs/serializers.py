from django.core.serializers.json import Serializer as JsonSerializer
from django.utils.encoding import smart_text

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
