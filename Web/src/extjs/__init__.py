from django.conf import settings
from django.core.exceptions import ImproperlyConfigured

if not hasattr(settings, "SERIALIZATION_MODULES") or 'extjson' not in settings.SERIALIZATION_MODULES or settings.SERIALIZATION_MODULES['extjson'] != 'extjs.serializers':
    raise ImproperlyConfigured("'extjson' must be present in settings.SERIALIZATION_MODULES and be set to 'extjs.serializers'")

if not hasattr(settings, "EXTJS_APPLICATIONS") or not isinstance(settings.EXTJS_APPLICATIONS, (list, tuple)) or not len(settings.EXTJS_APPLICATIONS):
    raise ImproperlyConfigured("settings.EXTJS_APPLICATIONS must contain at lease one application definition")
