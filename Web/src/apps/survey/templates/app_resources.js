{% load i18n %}
{% load staticfiles %}
{
	'media_url': '{% static "" %}',
	'strings': {
		'app_title': '{% trans "Bangladesh" %}',
		'username': '{% trans "Username" %}',
		'password': '{% trans "Password" %}',
		'footer': '{% trans "&copy; Jamie Cockburn 2014" %}',
		'login': '{% trans "Login" %}',
		'logout': '{% trans "Logout" %}',
		'confirm_logout': '{% trans "Are you sure you want to logout?" %}',
		'home': '{% trans "Home" %}',
		'surveys': '{% trans "Surveys" %}',
		'survey': '{% trans "Survey" %}',
		'no_surveys': '{% trans "No surveys" %}',
		'title': '{% trans "Title" %}',
		'users': '{% trans "Users" %}',
		'live': '{% trans "Visible" %}',
		'live_column_tip': '{% trans "Indicates if the survey is visible" %}',
		'survey_live': '{% trans "This survey is visible" %}',
		'survey_not_live': '{% trans "This survey is not visible" %}',
		'open': '{% trans "Open" %}',
		'open_column_tip': '{% trans "Indicates if the survey is accepting new submissions" %}',
		'survey_open': '{% trans "This survey is accepting new submissions" %}',
		'survey_not_open': '{% trans "This survey is not accepting any submissions" %}',
		'new_survey': '{% trans "New Survey" %}',
		'edit_survey': '{% trans "Edit Survey" %}',
		'set_visible': '{% trans "Set Visible" %}',
		'set_hidden': '{% trans "Set Hidden" %}',
		'set_open': '{% trans "Set Open" %}',
		'set_closed': '{% trans "Set Closed" %}',
		'delete_survey': '{% trans "Delete Survey" %}',
		'save_error': '{% trans "Error Saving Data" %}',
		'confirm_action': '{% trans "Confirm Action" %}',
		'confirm_set_visible': '{% trans "Are you sure you want to make this survey visible?" %}',
		'confirm_set_hidden': '{% trans "Are you sure you want to make this survey hidden? This will hide all reports and prevent submissions." %}',
		'confirm_set_open': '{% trans "Are you sure you want to make this survey open? This will allow submissions to be made." %}',
		'confirm_set_closed': '{% trans "Are you sure you want to make this survey closed? This will prevent any submissions being made." %}',
		'confirm_survey_delete': '{% trans "Are you sure you want to delete this survey? This action cannot be undone." %}',
		'general': '{% trans "General" %}',
		'questions': '{% trans "Questions" %}',
		'question': '{% trans "Question" %}',
		'new_question': '{% trans "New Question" %}',
		'delete_question': '{% trans "Delete Question" %}',
		'set_required': '{% trans "Set Required" %}',
		'set_optional': '{% trans "Set Optional" %}',
		'move_up': '{% trans "Move Up" %}',
		'move_down': '{% trans "Move Down" %}',
		'name': '{% trans "Name" %}',
		'type': '{% trans "Type" %}',
		'required': '{% trans "Required" %}',
		'edit_question': '{% trans "Edit Question" %}',
		'save': '{% trans "Save" %}',
		'description': '{% trans "Description" %}',
		'preview_required_message': '{% trans "This question is required" %}',
		'previous': '{% trans "Previous" %}',
		'next': '{% trans "Next" %}',
        'question_type': '{% trans "Question Type" %}',
		'text': '{% trans "Text" %}',
        'multi_line_text': '{% trans "Multi-line Text" %}',
        'number': '{% trans "Number" %}',
        'decimal_number': '{% trans "Decimal Number" %}',
        'drop_down_list': '{% trans "Drop-down List" %}',
        'radio_button_list': '{% trans "Radio Button List" %}',
        'multiple_checkbox_list': '{% trans "Multiple Checkbox List" %}',
        'confirm_cancel': '{% trans "Are you sure you want to discard your changes?" %}',
        'cancel': '{% trans "Cancel" %}',
        'confirm_discard_changes': '{% trans "Are you sure you want to leave this page? You will lose your changes!" %}',
        'confirm_set_required': '{% trans "Are you sure you want make this question required?" %}',
        'confirm_set_optional': '{% trans "Are you sure you want make this question optional?" %}',
        'unique_question_names': '{% trans "Error: Question names must be unique!" %}',
        'unique_survey_names': '{% trans "Error: Survey names must be unique!" %}',
        'help_text': '{% trans "Help text" %}',
        'choices': '{% trans "Choices" %}',
        'responses': '{% trans "Responses" %}',
        'assigned_users': '{% trans "Assigned Users" %}',
        'change_users': '{% trans "Assign Users" %}',
	}
}
