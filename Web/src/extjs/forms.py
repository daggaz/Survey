from django import forms

class ReadForm(forms.Form):
    page = forms.IntegerField(required=False)
    start = forms.IntegerField(required=False)
    limit = forms.IntegerField(required=False)
    filter = forms.CharField(required=False)
