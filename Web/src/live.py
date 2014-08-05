import settings
import sys
globals().update(vars(sys.modules['settings']))

# Database
# https://docs.djangoproject.com/en/1.6/ref/settings/#databases
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'undpdb',
        'USER': 'root',
        'PASS': '',
        'HOST': 'localhost',
    }
}

