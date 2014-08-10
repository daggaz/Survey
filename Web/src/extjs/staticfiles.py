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
        requires = []
        associations = []
        reverse_template = "{type: 'hasMany', model: '%s', name: '%s', foreignKey: '%s'}"
        for other in models.get_models():
            for f in other._meta.fields:
                if f.rel and model._meta == f.rel.to._meta:
                    model_class = '%s.model.%s.%s' % (app_name,
                                                      other._meta.app_label,
                                                      other._meta.object_name,
                                                      )
                    requires.append("'%s'" % model_class)
                    if not f.rel.related_name or f.rel.related_name.endswith('+'):
                        name = other._meta.model_name
                    else:
                        name = f.rel.related_name
                    associations.append(reverse_template % (model_class,
                                                            name,
                                                            f.name,
                                                            ))
        
        many_template = "{type: 'hasMany', model: '%s', name: '%s', foreignKey: '%s'}"
        for f in model._meta.many_to_many:
            joining_model = f.rel.through
            model_class = '%s.model.%s.%s' % (app_name,
                                              model._meta.app_label,
                                              joining_model._meta.object_name,
                                              )
            requires.append("'%s'" % model_class)
            associations.append(many_template % (model_class,
                                                 f.name,
                                                 "%s" % f.related_query_name(),
                                                 ))
            joining_model_fields = joining_model._meta.fields
            joining_model_fields = [field for field in joining_model_fields if field.name != model._meta.pk.name]
            joining_model_fields = ["{name: '%s', type: '%s'}" % (field.name, django_to_extsjs_type_map[type(field)]) for field in joining_model_fields]
            joining_model_fields = ",\n        ".join(joining_model_fields)
            joining_model_associations = ""
            joining_model = (path.join(model._meta.app_label, "%s.js" % joining_model._meta.object_name),
                             loader.render_to_string('model.js',
                                   {'app_name': app_name,
                                    'app': model._meta.app_label,
                                    'model': joining_model._meta.object_name,
                                    'source_model': model._meta.object_name,
                                    'related_app': f.rel.to._meta.app_label,
                                    'related_model': f.rel.to._meta.object_name,
                                    'fields': joining_model_fields,
                                    'associations': joining_model_associations,
                                    }),
                             loader.render_to_string('store.js',
                                   {'app_name': app_name,
                                    'app': model._meta.app_label,
                                    'model': joining_model._meta.object_name,
                                    })
                             )
            self.joining_models.append(joining_model)
        
        foreign_template = "{type: 'belongsTo', model: '%s', foreignKey: '%s', getterName: '%s'}"
        for f in model._meta.fields:
            if f.rel:
                model_class = '%s.model.%s.%s' % (app_name,
                                                  f.rel.to._meta.app_label,
                                                  f.rel.to._meta.object_name,
                                                  )
                requires.append("'%s'" % model_class)
                associations.append(foreign_template % (model_class,
                                                        f.name,
                                                        "get%s" % f.rel.to._meta.object_name,
                                                        ))
                
        associations = ",\n        ".join(associations)
        requires = ",\n        ".join(requires)
        return loader.render_to_string('model.js',
                                       {'app_name': app_name,
                                        'app': model._meta.app_label,
                                        'model': model._meta.object_name,
                                        'requires': requires,
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
        self.joining_models = []
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
        
            for f, model, store in self.joining_models:
                model_file = path.join(application_dir, 'model', f)
                print "generating %s" % model_file
                with file(model_file, 'w') as out:
                    out.write(model)
                
                store_file = path.join(application_dir, 'store', f)
                print "generating %s" % store_file
                with file(store_file, 'w') as out:
                    out.write(store)
            
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
