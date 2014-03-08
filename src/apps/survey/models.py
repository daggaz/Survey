from __future__ import absolute_import

import itertools
import re

from operator import itemgetter

from django.contrib.auth.models import User, Group
from django.contrib.sites.models import Site
from django.core.urlresolvers import reverse
from django.db import models
from django.utils.translation import ugettext_lazy as _
from django.utils.timezone import now

class ChoiceEnum(object):
    def __init__(self, choices):
        if isinstance(choices, basestring):
            choices = choices.split()
        if all([isinstance(choices, (list,tuple)),
                all(isinstance(x, tuple) and len(x) == 2 for x in choices)]):
            values = choices
        else:
            values = zip(itertools.count(1), choices)
        for v, n in values:
            name = re.sub('[- ]', '_', n.upper())
            setattr(self, name, v)
            if isinstance(v, str):
                setattr(self, v.upper(), v)
        self._choices = values

    def __getitem__(self, idx):
        return self._choices[idx]

    def getdisplay(self, key):
        return [v[1] for v in self._choices if v[0] == key][0]


OPTION_TYPE_CHOICES = ChoiceEnum(sorted([('char', 'Text Box'),
                                         ('email', 'Email Text Box'),
                                         ('integer', 'Integer Text Box'),
                                         ('float', 'Decimal Text Box'),
                                         ('bool', 'Checkbox'),
                                         ('text', 'Text Area'),
                                         ('select', 'Drop Down List'),
                                         ('choice', 'Radio Button List'),
                                         ('bool_list', 'Checkbox List'),
                                         ('numeric_select', 'Numeric Drop Down List'),
                                         ('numeric_choice', 'Numeric Radio Button List'),
                                         ],
                                        key=itemgetter(1)
                                        ))


class LiveSurveyManager(models.Manager):
    def get_query_set(self):
        return super(LiveSurveyManager, self).get_query_set().filter(
            is_live=True,
            )

class OpenSurveyManager(LiveSurveyManager):
    def get_query_set(self):
        return super(OpenSurveyManager, self).get_query_set().filter(
            is_open=True,
            )

class ArchivedSurveyManager(LiveSurveyManager):
    def get_query_set(self):
        return super(ArchivedSurveyManager, self).get_query_set().filter(
            is_open=False,
            )


FORMAT_CHOICES = ('json', 'csv', 'xml', 'html',)

class SurveyAuditRecord(models.Model):
    survey = models.ForeignKey('Survey', related_name='audit_trail')
    time = models.DateTimeField(auto_now_add=True)
    user = models.ForeignKey(User)
    action = models.CharField(max_length=256)

class Survey(models.Model):
    title = models.CharField(max_length=80)
    slug = models.SlugField(unique=True)
    description = models.TextField(blank=True)
    is_live = models.BooleanField(default=False)
    live_date = models.DateTimeField(null=True)
    is_open = models.BooleanField(default=False)
    email = models.TextField(
        blank=True,
        help_text=(
            "Send a notification to these e-mail addresses whenever someone "
            "submits an entry to this survey. Comma delimited. Messages to "
            "staff emails will include admin urls."))
    site = models.ForeignKey(Site)
    default_report = models.ForeignKey(
        'SurveyReport',
        blank=True,
        null=True,
        related_name='reports',
        help_text=("Whenever we automatically generate a link to the results "
                   "of this survey we'll use this report. If it's left blank, "
                   "we'll use the default report behavior."))
    users = models.ManyToManyField(User, blank=True)
    
    objects = models.Manager()
    live = LiveSurveyManager()
    open = OpenSurveyManager()
    archived = ArchivedSurveyManager()
    
    def __init__(self, *args, **kwargs):
        super(Survey, self).__init__(*args, **kwargs)
        self._old_live = self.is_live
    
    def to_jsondata(self):
        kwargs = {'slug': self.slug}
        submit_url = reverse('embeded_survey_questions', kwargs=kwargs)
        report_url = reverse('survey_default_report_page_1', kwargs=kwargs)
        questions = self.questions.order_by("order")
        return dict(id=self.id,
                    title=self.title,
                    slug=self.slug,
                    description=self.description,
                    submit_url=submit_url,
                    report_url=report_url,
                    questions=[q.to_jsondata() for q in questions])

    class Meta:
        ordering = ('-live_date',)
        
    def save(self, *args, **kwargs):
        if self.is_live and not self._old_live:
            self.live_date = now()
        elif not self.is_live:
            self.live_date = None
        self._old_live = self.is_live
        return super(Survey, self).save(*args, **kwargs)
    
    def get_public_fields(self, fieldnames=None):
        if fieldnames:
            return self.get_fields(fieldnames)
        return [f for f in self.get_fields() if f.answer_is_public]

    def get_fields(self, fieldnames=None):
        if not "_fields" in self.__dict__:
            questions = self.questions.all()
            questions = questions.select_related("survey")
            self.__dict__["_fields"] = list(questions.order_by("order"))
        fields = self.__dict__["_fields"]
        if fieldnames:
            return [f for f in fields if f.fieldname in fieldnames]
        return fields

    def __unicode__(self):
        return self.title

    @models.permalink
    def get_absolute_url(self):
        return ('survey_detail', (), {'slug': self.slug})

    def get_download_url(self, format):
        url = reverse('submissions_by_format', kwargs={"format": format})
        return url + "?survey=" + self.slug

    def get_download_tag(self, format):
        a = '<a target="_blank" href="%s">%s</a>'
        return a % (self.get_download_url(format), format,)

    def get_download_tags(self, delimiter=", "):
        downloads = []
        for format in sorted(FORMAT_CHOICES):
            downloads.append(self.get_download_tag(format))
        return delimiter.join(downloads)

POSITION_HELP = ("What order does this question appear in the survey form and "
                 "in permalinks?")


class Question(models.Model):
    survey = models.ForeignKey(Survey, related_name="questions")
    fieldname = models.CharField(
        max_length=32,
        help_text=_('a single-word identifier used to track this value; '
                    'it must begin with a letter and may contain '
                    'alphanumerics and underscores (no spaces).'))
    question = models.TextField(help_text=_("Appears on the survey entry page."))
    label = models.TextField(help_text=_("Appears on the results page."))
    help_text = models.TextField(blank=True)
    required = models.BooleanField(default=False,
        help_text=_("Unsafe to change on live surveys. Radio button list and "
                    "drop down list questions will have a blank option if "
                    "they aren't required."))
    option_type = models.CharField(max_length=50,
                                   choices=OPTION_TYPE_CHOICES,
                                   help_text=_('You must not change this field on a live survey.'))
    options = models.TextField(blank=True,
                               default='',
                               help_text=_(
            'Use one option per line. On a live survey you can modify the '
            'order of these options. You can, at your own risk, add new '
            'options, but you must not change or remove options.'))
    order = models.IntegerField(help_text=POSITION_HELP)
    
    _aggregate_result = None

    def to_jsondata(self):
        return dict(fieldname=self.fieldname,
                    label=self.label,
                    is_filterable=self.is_filterable,
                    question=self.question,
                    required=self.required,
                    option_type=self.option_type,
                    options=self.parsed_options,
                    answer_is_public=self.answer_is_public,
                    cms_id=self.id,
                    help_text=self.help_text)

    class Meta:
        ordering = ('order',)
        unique_together = ('fieldname', 'survey')

    def __unicode__(self):
        return self.question

    def save(self, *args, **kwargs):
        self.numeric_is_int = True
        OTC = OPTION_TYPE_CHOICES
        if self.option_type in (OTC.NUMERIC_SELECT, OTC.NUMERIC_CHOICE):
            for option in self.parsed_options:
                try:
                    int(option)
                except ValueError:
                    float(option)
                    self.numeric_is_int = False
        elif self.option_type == OTC.FLOAT:
            self.numeric_is_int = False
        super(Question, self).save(*args, **kwargs)

    @property
    def parsed_options(self):
        if OPTION_TYPE_CHOICES.BOOL == self.option_type:
            return [True, False]
        return filter(None, (s.strip() for s in self.options.splitlines()))

    @property
    def parsed_map_icons(self):
        return filter(None, (s.strip() for s in self.map_icons.splitlines()))

    def parsed_option_icon_pairs(self):
        options = self.parsed_options
        icons = self.parsed_map_icons
        to_return = []
        for i in range(len(options)):
            if i < len(icons):
                to_return.append((options[i], icons[i]))
            else:
                to_return.append((options[i], None))
        return to_return

    @property
    def value_column(self):
        ot = self.option_type
        OTC = OPTION_TYPE_CHOICES
        if ot == OTC.BOOL:
            return "boolean_answer"
        elif self.is_float:
            return "float_answer"
        elif self.is_integer:
            return "integer_answer"
        elif ot == OTC.PHOTO:
            return "image_answer"
        return "text_answer"

    @property
    def is_numeric(self):
        OTC = OPTION_TYPE_CHOICES
        return self.option_type in [OTC.FLOAT,
                                    OTC.INTEGER,
                                    OTC.BOOL,
                                    OTC.NUMERIC_SELECT,
                                    OTC.NUMERIC_CHOICE]

    @property
    def is_float(self):
        return self.is_numeric and not self.numeric_is_int

    @property
    def is_integer(self):
        return self.is_numeric and self.numeric_is_int

class Submission(models.Model):
    survey = models.ForeignKey(Survey, related_name='submissions')
    user = models.ForeignKey(User, blank=True, null=True)
    submitted_at = models.DateTimeField(auto_now_add=True)
    
    class Meta:
        ordering = ('-submitted_at',)

    def to_jsondata(self, answer_lookup=None, include_private_questions=False):
        if not answer_lookup:
            answer_lookup = get_all_answers([self], include_private_questions)
        data = {}
        for a in answer_lookup.get(self.pk, []):
            if a.question.option_type == OPTION_TYPE_CHOICES.BOOL_LIST:
                data[a.value] = True
            else:
                data[a.question.fieldname] = a.value
        return_value = dict(data=data,
                            survey=self.survey.slug,
                            submitted_at=self.submitted_at,
                            featured=self.featured,
                            is_public=self.is_public)
        if self.user:
            return_value["user"] = self.user.username
        return return_value

    def get_answer_dict(self):
        try:
            # avoid called __getattr__
            return self.__dict__['_answer_dict']
        except KeyError:
            answers = self.answer_set.all()
            d = dict((a.question.fieldname, a.value) for a in answers)
            self.__dict__['_answer_dict'] = d
            return d

    def items(self):
        return self.get_answer_dict().items()

    def get_absolute_url(self):
        view = 'crowdsourcing.views.submission'
        return reverse(view, kwargs={"id": self.pk})

    @property
    def email(self):
        return self.get_answer_dict().get('email', '')

    def __unicode__(self):
        return u"%s Submission" % self.survey.title


class Answer(models.Model):
    submission = models.ForeignKey(Submission, related_name='answers')
    question = models.ForeignKey(Question, related_name='answers')
    text_answer = models.TextField(blank=True)
    integer_answer = models.IntegerField(blank=True, null=True)
    float_answer = models.FloatField(blank=True, null=True)
    boolean_answer = models.NullBooleanField()

    @property
    def value(self):
        return getattr(self, self.question.value_column)
    
    @value.setter
    def value(self, v):
        ot = self.question.option_type
        OTC = OPTION_TYPE_CHOICES
        if ot == OTC.BOOL:
            self.boolean_answer = bool(v)
        elif ot in (OTC.FLOAT,
                    OTC.INTEGER,
                    OTC.NUMERIC_SELECT,
                    OTC.NUMERIC_CHOICE):
            # Keep values in both the integer and float columns just in
            # case the question switches between integer and float types.
            if v:
                self.float_answer = float(v)
                self.integer_answer = int(round(self.float_answer))
            else:
                self.float_answer = self.integer_answer = None
        else:
            self.text_answer = v

    class Meta:
        ordering = ('question',)

    def __unicode__(self):
        return unicode(self.question)

class SurveyReport(models.Model):
    """
    a survey report permits the presentation of data submitted in a
    survey to be customized.  It consists of a series of display
    options, which each take a display type, a series of fieldnames,
    and an annotation.  It also has article-like fields of its own.
    """
    survey = models.ForeignKey(Survey, related_name='reports')
    title = models.CharField(
        max_length=50,
        blank=True,
        help_text=_("You may leave this field blank. Crowdsourcing will use "
                    "the survey title as a default."))
    slug = models.CharField(
        max_length=50,
        help_text=_("The default is the description of the survey."))
    # some text at the beginning
    summary = models.TextField(blank=True)
    # As crowdsourcing doesn't implement rating because we want to let you use
    # your own, we don't actually use this flag anywhere in the crowdsourcing
    # project. Rather, see settings.PRE_REPORT
    sort_by_rating = models.BooleanField(
        default=False,
        help_text="By default, sort descending by highest rating. Otherwise, "
                  "the default sort is by date descending.")
    display_the_filters = models.BooleanField(
        default=True,
        help_text="Display the filters at the top of the report page.")
    limit_results_to = models.PositiveIntegerField(
        blank=True,
        null=True,
        help_text="Only use the top X submissions.")
    featured = models.BooleanField(
        default=False,
        help_text=_("Include only featured submissions."))
    display_individual_results = models.BooleanField(
        default=True,
        help_text=_("Display separate, individual results if this field is "
                    "True and you have archivable questions, like those with "
                    "paragraph answers."))
    # A useful variable for holding different report displays so they don't
    # get saved to the database.
    survey_report_displays = None

    def get_survey_report_displays(self):
        if self.pk and self.survey_report_displays is None:
            srds = list(self.surveyreportdisplay_set.select_related('report'))
            self.survey_report_displays = srds
            for srd in self.survey_report_displays:
                srd._report = self
        return self.survey_report_displays

    def has_display_type(self, type):
        if not hasattr(type, '__iter__'):
            type = [type]
        displays = self.get_survey_report_displays()
        return bool([1 for srd in displays if srd.display_type in type])

    def has_charts(self):
        SRDC = SURVEY_DISPLAY_TYPE_CHOICES
        return self.has_display_type([SRDC.PIE, SRDC.BAR, SRDC.LINE])

    @models.permalink
    def get_absolute_url(self):
        return ('survey_report_page_1', (), {'slug': self.survey.slug,
                                             'report': self.slug})

    class Meta:
        unique_together = (('survey', 'slug'),)
        ordering = ('title',)

    def get_title(self):
        return self.title or self.survey.title

    def get_summary(self):
        return self.summary or self.survey.description or self.survey.tease

    def __unicode__(self):
        return self.get_title()


SURVEY_DISPLAY_TYPE_CHOICES = ChoiceEnum(
    'text pie map bar line slideshow download')


SURVEY_AGGREGATE_TYPE_CHOICES = ChoiceEnum('default sum count average')


class SurveyReportDisplay(models.Model):
    """ Think of this as a line item of SurveyReport. """
    report = models.ForeignKey(SurveyReport)
    display_type = models.PositiveIntegerField(
        choices=SURVEY_DISPLAY_TYPE_CHOICES)
    aggregate_type = models.PositiveIntegerField(
        choices=SURVEY_AGGREGATE_TYPE_CHOICES,
        help_text=_("We only use this field if you chose a Bar or Line Chart. "
                    "How should we aggregate the y-axis? 'Average' is good "
                    "for things like ratings, 'Sum' is good for totals, and "
                    "'Count' is good for a show of hands."),
        default=SURVEY_AGGREGATE_TYPE_CHOICES.DEFAULT)
    fieldnames = models.TextField(
        blank=True,
        help_text=_("Pull these values from Survey -> Questions -> Fieldname. "
                    "Separate by spaces. These are the y-axes of bar and line "
                    "charts."))
    x_axis_fieldname = models.CharField(
        blank=True,
        help_text=_("This only applies to bar and line charts. Use only 1 "
                    "field."),
        max_length=80)
    annotation = models.TextField(blank=True)
    limit_map_answers = models.IntegerField(
        null=True,
        blank=True,
        help_text=_('Google maps gets pretty slow if you add too many points. '
                    'Use this field to limit the number of points that '
                    'display on the map.'))
    map_center_latitude = models.FloatField(
        blank=True,
        null=True,
        help_text=_('If you don\'t specify latitude, longitude, or zoom, the '
                    'map will just center and zoom so that the map shows all '
                    'the points.'))
    map_center_longitude = models.FloatField(blank=True, null=True)
    map_zoom = models.IntegerField(
        blank=True,
        null=True,
        help_text=_('13 is about the right level for Manhattan. 0 shows the '
                    'entire world.'))
    caption_fields = models.CharField(
        max_length=200,
        blank=True,
        help_text=_('The answers to these questions will appear as '
                    'captions below their corresponding slides. Separate by '
                    'spaces.'))

    order = models.IntegerField()

    def __unicode__(self):
        type = SURVEY_DISPLAY_TYPE_CHOICES.getdisplay(self.display_type)
        return_value = [type]
        SATC = SURVEY_AGGREGATE_TYPE_CHOICES
        if self.aggregate_type != SATC.DEFAULT:
            return_value.append(SATC.getdisplay(self.aggregate_type))
        if self.x_axis_fieldname:
            if self.fieldnames:
                return_value.append("y-axes: %s" % self.fieldnames)
            return_value.append("x-axis: %s" % self.x_axis_fieldname)
        elif self.fieldnames:
            return_value.append(self.fieldnames)
        return " ".join(return_value)

    def questions(self, fields=None):
        return self._get_questions(self.fieldnames, fields)

    def x_axis_question(self, fields=None):
        return_value = self._get_questions(self.x_axis_fieldname, fields)
        if return_value:
            return return_value[0]
        return None

    def get_caption_fieldnames(self):
        return self.caption_fields.split(" ")

    def _get_questions(self, fieldnames, fields):
        names = fieldnames.split(" ")
        if fields:
            return [f for f in fields if f.fieldname in names]
        return self.get_report().survey.get_public_fields(names)

    def get_report(self):
        if hasattr(self, '_report'):
            return self._report
        return self.report

    def index_in_report(self):
        assert self.report, "This display's report attribute is not set."
        srds = self.get_report().get_survey_report_displays()
        for i in range(len(srds)):
            if srds[i] == self:
                return i
        assert False, "This display isn't in its report's displays."

    class Meta:
        ordering = ('order',)

    def __getattribute__(self, key):
        """ We provide is_text, is_pie, etc... as attirbutes to make it easier
        to write conditional logic in Django templates based on
        display_type. """
        if "is_" == key[:3]:
            for value, name in SURVEY_DISPLAY_TYPE_CHOICES._choices:
                if name == key[3:]:
                    return self.display_type == value
        return super(SurveyReportDisplay, self).__getattribute__(key)


def get_all_answers(submission_list, include_private_questions=False):
    ids = [submission.id for submission in submission_list]
    page_answers_list = Answer.objects.filter(submission__id__in=ids)
    if not include_private_questions:
        kwargs = dict(question__answer_is_public=True)
        page_answers_list = page_answers_list.filter(**kwargs)
    page_answers_list = page_answers_list.select_related("question")
    page_answers = {}
    for answer in page_answers_list:
        if not answer.submission_id in page_answers:
            page_answers[answer.submission_id] = []
        page_answers[answer.submission_id].append(answer)
    return page_answers

