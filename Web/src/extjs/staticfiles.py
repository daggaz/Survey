from os import path, makedirs
import errno

from django.conf import settings
from django.contrib.staticfiles.finders import BaseFinder
from django.core.exceptions import ImproperlyConfigured
from django.db import models
from django.template import loader

extjs_to_django_type_map = {'string': (models.CharField, models.TextField, models.SlugField, models.EmailField,),
                            'int': (models.IntegerField, models.PositiveIntegerField, models.PositiveSmallIntegerField, models.ForeignKey,),
                            'float': (models.FloatField,),
                            'boolean': (models.BooleanField, models.NullBooleanField,),
                            'date': (models.DateField, models.DateTimeField,),
                            }
django_to_extsjs_type_map = {}
for js_type, django_types in extjs_to_django_type_map.items():
    for django_type in django_types:
        django_to_extsjs_type_map[django_type] = js_type

class ExtjsFinder(BaseFinder):
    def model_to_extjs_model(self, app_name, model):
        fields = model._meta.fields
        fields = [field for field in fields if field.name != model._meta.pk.name]
        fields = ["{name: '%s', type: '%s'}" % (field.name, django_to_extsjs_type_map[type(field)]) for field in fields]
        fields = ",\n        ".join(fields)
        associations = []
        association_template = "{type: 'hasMany', model: '%s.model.%s.%s', name:'%s', foreignKey:'%s'}"
        for rel in model._meta.get_all_related_objects():
            associations.append(association_template % (app_name,
                                                        rel.model._meta.app_label,
                                                        rel.model._meta.object_name,
                                                        rel.get_accessor_name(),
                                                        rel.field.name,
                                                        ))
        associations = ",\n        ".join(associations)
        return loader.render_to_string('model.js',
                                       {'app_name': app_name,
                                        'app': model._meta.app_label,
                                        'model': model._meta.object_name,
                                        'fields': fields,
                                        'associations': associations,
                                        })
    
    def model_to_extjs_store(self, app_name, model):
        return loader.render_to_string('store.js',
                                       {'app_name': app_name,
                                        'app': model._meta.app_label,
                                        'model': model._meta.object_name,
                                        })
    
    def __init__(self, *args, **kwargs):
        super(ExtjsFinder, self).__init__(*args, **kwargs)
        applications = settings.EXTJS_APPLICATIONS
        generation_dir = path.join(path.dirname(__file__), 'static') 
        for application in applications:
            if not isinstance(application, dict):
                raise ImproperlyConfigured('application definitions in settings.EXTJS_APPLICATIONS must be dictionaries')
            app_name = application.get('name')
            if app_name is None:
                raise ImproperlyConfigured('application definitions in settings.EXTJS_APPLICATIONS must specify a "name" attribute')
            prefix = application.get('prefix', '')
            application_dir = path.join(generation_dir, prefix, app_name)
            for model in models.get_models():
                model_dir = path.join(application_dir, 'model', model._meta.app_label)
                self.mkdir_p(model_dir)
                f = path.join(model_dir, "%s.js" % (model._meta.object_name))
                print "generating %s" % f
                with file(f, 'w') as out:
                    out.write(self.model_to_extjs_model(app_name, model))
                
                store_dir = path.join(application_dir, 'store', model._meta.app_label)
                self.mkdir_p(store_dir)
                f = path.join(store_dir, "%s.js" % (model._meta.object_name))
                print "generating %s" % f
                with file(f, 'w') as out:
                    out.write(self.model_to_extjs_store(app_name, model))
    
    def mkdir_p(self, dir_name):
        try:
            makedirs(dir_name)
        except OSError as exc:
            if exc.errno == errno.EEXIST and path.isdir(dir_name):
                pass
            else:
                raise
    
    def find(self, path, all=False):
        return []

    def list(self, ignore_patterns):
        return []
