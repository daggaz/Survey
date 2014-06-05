from django.contrib.staticfiles.finders import BaseFinder
from django.db import models
from django.conf import settings
from django.core.exceptions import ImproperlyConfigured
from os import path, makedirs
import errno

class ExtjsFinder(BaseFinder):
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
            for model in models.get_models():
                dest = path.join(generation_dir, prefix, app_name, 'model', model._meta.app_label)
                try:
                    makedirs(dest)
                except OSError as exc:
                    if exc.errno == errno.EEXIST and path.isdir(dest):
                        pass
                    else:
                        raise
                f = path.join(dest, "%s.js" % (model._meta.object_name))
                print "generating %s" % f
                with file(f, 'w') as out:
                    out.write(f)

    def find(self, path, all=False):
        return None
