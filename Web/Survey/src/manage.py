#!/usr/bin/env python
import os
import sys

if __name__ == "__main__":
    print "DJANGO_SETTINGS_MODULE: %s" % os.environ.get('DJANGO_SETTINGS_MODULE', None)
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "settings")

    from django.core.management import execute_from_command_line

    execute_from_command_line(sys.argv)
