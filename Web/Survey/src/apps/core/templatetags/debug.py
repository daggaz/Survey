from django import template

register = template.Library()

@register.simple_tag(takes_context=True)
def pdb(context):
    try:
        import sys
        sys.path.append(r'/home/ubuntu/Downloads/Aptana_Studio_3/plugins/org.python.pydev_2.7.0.2013032300/pysrc')
        import pydevd
        pydevd.settrace()
    except:
        import traceback
        traceback.print_exc()
    return ''
