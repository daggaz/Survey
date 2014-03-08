from django.contrib import admin
from apps.survey.models import Survey, SurveyAuditRecord, Question

class QuestionInline(admin.StackedInline):
    model = Question
    extra = 3

class SurveyAdmin(admin.ModelAdmin):
    search_fields = ('title', 'slug', 'tease', 'description')
    prepopulated_fields = {'slug' : ('title',)}
    exclude = ('live_date',)
    inlines = [QuestionInline]

    def save_model(self, request, survey, form, change):
        if survey.pk is None:
            super(SurveyAdmin, self).save_model(request, survey, form, change)
            audit = SurveyAuditRecord(survey=survey,
                                      user=request.user,
                                      action="Created",
                                      )
            audit.save()
            if survey.is_live:
                audit = SurveyAuditRecord(survey=survey,
                                          user=request.user,
                                          action="Made live",
                                          )
                audit.save()
        else:
            old_survey = Survey.objects.get(pk=survey.pk)
            super(SurveyAdmin, self).save_model(request, survey, form, change)
            audit = SurveyAuditRecord(survey=survey,
                                      user=request.user,
                                      action="Modified",
                                      )
            audit.save()
            if survey.is_live and not old_survey.is_live:
                audit = SurveyAuditRecord(survey=survey,
                                          user=request.user,
                                          action="Made live",
                                          )
                audit.save()
            elif old_survey.is_live and not survey.is_live:
                audit = SurveyAuditRecord(survey=survey,
                                          user=request.user,
                                          action="Made hidden",
                                          )
                audit.save()

admin.site.register(Survey, SurveyAdmin)
