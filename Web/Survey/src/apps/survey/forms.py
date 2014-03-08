from django.forms.models import ModelForm
from django.forms.forms import Form, BoundField
from django.template import Context, loader

from apps.survey.models import Submission, Answer, OPTION_TYPE_CHOICES
from django.core.exceptions import ValidationError
from django.forms.fields import CharField, IntegerField, FloatField,\
    BooleanField, EmailField, ChoiceField, MultipleChoiceField
from django.forms.widgets import Textarea, RadioSelect, CheckboxSelectMultiple
from django.utils.html import strip_tags
from django.utils.safestring import mark_safe

class BaseAnswerForm(Form):
    def __init__(self, question, *args, **kwargs):
        self.question = question
        super(BaseAnswerForm, self).__init__(*args, **kwargs)
        self._configure_answer_field()

    def _configure_answer_field(self):
        answer = self.fields['answer']
        q = self.question
        answer.required = q.required
        answer.label = q.question
        answer.help_text = q.help_text
        # set some property on the basis of question.fieldname? TBD
        return answer

    def as_template(self):
        "Helper function for fieldsting fields data from form."
        bound_fields = [BoundField(self, field, name) \
                      for name, field in self.fields.items()]
        c = Context(dict(form=self, bound_fields=bound_fields))
        t = loader.get_template('forms/form.html')
        return t.render(c)

    def save(self, commit=True):
        if self.cleaned_data['answer'] is None:
            if self.fields['answer'].required:
                raise ValidationError, _('This field is required.')
            return
        ans = Answer()
        ans.question = self.question
        ans.value = self.cleaned_data['answer']
        if commit:
            ans.save()
        return ans


class TextInputAnswer(BaseAnswerForm):
    answer = CharField()


class IntegerInputAnswer(BaseAnswerForm):
    answer = IntegerField()


class FloatInputAnswer(BaseAnswerForm):
    answer = FloatField()


class BooleanInputAnswer(BaseAnswerForm):
    answer = BooleanField(initial=False)

    def clean_answer(self):
        value = self.cleaned_data['answer']
        if not value:
            return False
        return value

    def _configure_answer_field(self):
        fld = super(BooleanInputAnswer, self)._configure_answer_field()
        # we don't want to set this as required, as a single boolean field
        # being required doesn't make much sense in a survey
        fld.required = False
        return fld


class TextAreaAnswer(BaseAnswerForm):
    answer = CharField(widget=Textarea)

class EmailAnswer(BaseAnswerForm):
    answer = EmailField()

class BaseOptionAnswer(BaseAnswerForm):
    def __init__(self, *args, **kwargs):
        super(BaseOptionAnswer, self).__init__(*args, **kwargs)
        options = self.question.parsed_options
        # appendChoiceButtons in survey.js duplicates this. jQuery and django
        # use " for html attributes, so " will mess them up.
        choices = []
        for x in options:
            choices.append(
                (strip_tags(x).replace('&amp;', '&').replace('"', "'").strip(),
                 mark_safe(x)))
        if not self.question.required and not isinstance(self, OptionCheckbox):
            choices = [('', '---------',)] + choices
        self.fields['answer'].choices = choices

    def clean_answer(self):
        key = self.cleaned_data['answer']
        if not key and self.fields['answer'].required:
            raise ValidationError, _('This field is required.')
        if not isinstance(key, (list, tuple)):
            key = (key,)
        return key

    def save(self, commit=True):
        ans_list = []
        for text in self.cleaned_data['answer']:
            ans = Answer()
            if self.submission:
                ans.submission = self.submission
            ans.question = self.question
            ans.value = text
            if commit:
                ans.save()
            ans_list.append(ans)
        return ans_list


class OptionAnswer(BaseOptionAnswer):
    answer = ChoiceField()


class OptionRadio(BaseOptionAnswer):
    answer = ChoiceField(widget=RadioSelect)


class OptionCheckbox(BaseOptionAnswer):
    answer = MultipleChoiceField(widget=CheckboxSelectMultiple)

# Each question gets a form with one element determined by the type for the
# answer.
QTYPE_FORM = {
    OPTION_TYPE_CHOICES.CHAR: TextInputAnswer,  # @UndefinedVariable
    OPTION_TYPE_CHOICES.INTEGER: IntegerInputAnswer,  # @UndefinedVariable
    OPTION_TYPE_CHOICES.FLOAT: FloatInputAnswer,  # @UndefinedVariable
    OPTION_TYPE_CHOICES.BOOL: BooleanInputAnswer,  # @UndefinedVariable
    OPTION_TYPE_CHOICES.TEXT: TextAreaAnswer,  # @UndefinedVariable
    OPTION_TYPE_CHOICES.SELECT: OptionAnswer,  # @UndefinedVariable
    OPTION_TYPE_CHOICES.CHOICE: OptionRadio,  # @UndefinedVariable
    OPTION_TYPE_CHOICES.NUMERIC_SELECT: OptionAnswer,  # @UndefinedVariable
    OPTION_TYPE_CHOICES.NUMERIC_CHOICE: OptionRadio,  # @UndefinedVariable
    OPTION_TYPE_CHOICES.BOOL_LIST: OptionCheckbox,  # @UndefinedVariable
    OPTION_TYPE_CHOICES.EMAIL: EmailAnswer,  # @UndefinedVariable
}

class SubmissionForm(ModelForm):

    def __init__(self, survey, *args, **kwargs):
        super(SubmissionForm, self).__init__(*args, **kwargs)
        self.survey = survey

    class Meta:
        model = Submission
        exclude = (
            'survey',
            'user',
            'submitted_at',
            )
