package com.skeds.android.phone.business.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.skeds.android.phone.business.AsyncTasks.BaseUiReportTask;
import com.skeds.android.phone.business.R;
import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.CustomQuestion;
import com.skeds.android.phone.business.Utilities.REST.Objects.RESTChecklistQuestionList;

import java.util.List;

public class ApptQuestionsFragment extends BaseSkedsFragment {

    private ListView questionsList;
    private Activity a;

    private boolean haveToAnswerRequired;

    private String metaType;

    private List<CustomQuestion> questions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_appt_questions_list, container,
                false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        a = getActivity();

        haveToAnswerRequired = a.getIntent().getBooleanExtra("haveToAnswerRequired", false);

        questions = AppDataSingleton.getInstance().getCustomQuestionList();

        questionsList = (ListView) a.findViewById(R.id.appt_questions_list);
        questionsList.setAdapter(new QuestionsAdapter(a));
        questionsList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showQuestionDialog(position);
            }
        });

        ((TextView) a.findViewById(R.id.btn_save_questions)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (haveToAnswerRequired) {
                    if (allRequiredUnswered())
                        new SendCheckListTask().execute();
                    else
                        Toast.makeText(a, "You have to Answer All Required Questions (Marked as Red)", Toast.LENGTH_LONG).show();
                } else if (atLeastOneUnswered())
                    new SendCheckListTask().execute();
                else
                    Toast.makeText(a, "Please answer required questions", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showQuestionDialog(final int questionNumber) {
        final Dialog questionDialog = new Dialog(a);
        questionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        metaType = questions.get(questionNumber).getMetaType();

        if ("boolean".equals(metaType.toLowerCase())) {
            questionDialog.setContentView(R.layout.dialog_appt_question_boolean);
        } else {
            questionDialog.setContentView(R.layout.dialog_appt_question);
        }

        final TextView questionName = (TextView) questionDialog.findViewById(R.id.dialog_appt_question_name);
        final EditText questionField = (EditText) questionDialog.findViewById(R.id.dialog_appt_question_field);
        final TextView confirmBtn = (TextView) questionDialog.findViewById(R.id.dialog_appt_question_button_confirm);
        final TextView cancelBtn = (TextView) questionDialog.findViewById(R.id.dialog_appt_question_button_cancel);
        final RadioGroup gr = (RadioGroup) questionDialog.findViewById(R.id.appt_questions_radio_group);
        final RadioButton btnTrue = (RadioButton) questionDialog.findViewById(R.id.item_appt_question_true);
        final RadioButton btnFalse = (RadioButton) questionDialog.findViewById(R.id.item_appt_question_false);

        questionName.setText(questions.get(questionNumber).getText());

        if (questionField != null)
            questionField.setText(questions.get(questionNumber).getAnswer());

        if (gr != null) {
            if ("true".equals(questions.get(questionNumber).getAnswer().toLowerCase())) {
                btnTrue.setChecked(true);
            } else
                btnFalse.setChecked(true);
        }

        if ("numeric".equals(metaType.toLowerCase())) {
            questionField.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        if ("numeric_decimal".equals(metaType.toLowerCase())) {
            questionField.setInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (questionField != null) {
                    if ("numeric".equals(metaType.toLowerCase())) {
                        questions.get(questionNumber).setAnswer(questionField.getText().toString());
                    } else if ("numeric_decimal".equals(metaType.toLowerCase())) {
                        if (!questionField.getText().toString().contains(".")) {
                            questions.get(questionNumber).setAnswer(questionField.getText().toString() + ".0");
                        } else
                            questions.get(questionNumber).setAnswer(questionField.getText().toString());
                    } else {
                        questions.get(questionNumber).setAnswer(questionField.getText().toString());
                    }

                } else {
                    if (btnTrue.isChecked())
                        questions.get(questionNumber).setAnswer("true");
                    else
                        questions.get(questionNumber).setAnswer("false");
                }
                questionDialog.dismiss();
                questionsList.invalidateViews();
            }
        });

        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                questionDialog.dismiss();
            }
        });
        questionDialog.show();
    }

    private boolean atLeastOneUnswered() {
        for (CustomQuestion q : questions) {
            if (!TextUtils.isEmpty(q.getAnswer()))
                return true;
        }
        return false;
    }

    private boolean allRequiredUnswered() {
        for (CustomQuestion q : questions) {
            if (q.isRequired())
                if (TextUtils.isEmpty(q.getAnswer()))
                    return false;
        }
        return true;
    }


    private class QuestionsAdapter extends ArrayAdapter<CustomQuestion> {

        public QuestionsAdapter(Context context) {
            super(context, R.layout.row_questions, questions);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            ViewHolder holder;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) a
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_questions, null);

                // cache view fields into the holder
                holder = new ViewHolder();
                holder.name = (TextView) v.findViewById(R.id.question_name);
                holder.unswer = (TextView) v.findViewById(R.id.question_unswer);
                // associate the holder with the view for later lookup
                v.setTag(holder);
            } else {
                // view already exists, get the holder instance from the view
                holder = (ViewHolder) v.getTag();
            }

            holder.name.setText(questions.get(position).getText());
            if (questions.get(position).isRequired()) {
                holder.name.setTextColor(a.getResources().getColor(R.color.red));
            }

            if ("true".equals(questions.get(position).getAnswer()))
                holder.unswer.setText("yes");
            else if ("false".equals(questions.get(position).getAnswer()))
                holder.unswer.setText("no");
            else
                holder.unswer.setText(questions.get(position).getAnswer());

            return v;
        }

        private class ViewHolder {
            TextView name;
            TextView unswer;
        }
    }

    private final class SendCheckListTask extends BaseUiReportTask<String> {

        SendCheckListTask() {
            super(a, R.string.async_task_string_submitting_checklist);
        }

        @Override
        protected void onSuccess() {
            a.setResult(Activity.RESULT_OK);
            a.onBackPressed();
        }

        @Override
        protected boolean taskBody(final String... args) throws Exception {
            RESTChecklistQuestionList.add(AppDataSingleton.getInstance().getAppointment().getId());
            return true;
        }
    }
}
