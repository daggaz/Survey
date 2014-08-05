#!/usr/bin/env python
import os
import sys

if __name__ == "__main__":
    BASE_DIR = os.path.dirname(__file__)
    sys.path.append(os.path.join(BASE_DIR, '../django'))
    sys.path.append(os.path.join(BASE_DIR, '../django-debug-toolbar'))
    
    from django.core.management import execute_from_command_line

    execute_from_command_line(sys.argv)
