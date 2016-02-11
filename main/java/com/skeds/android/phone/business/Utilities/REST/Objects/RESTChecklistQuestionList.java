package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

public class RESTChecklistQuestionList {

    public static void add(int appointmentId) throws NonfatalException {
        if (AppDataSingleton.getInstance().getCustomQuestionList().isEmpty())
            return;
        Element rootNode = new Element("answerCustomQuestionsRequest");

        for (int i = 0; i < AppDataSingleton.getInstance()
                .getCustomQuestionList().size(); i++) {
            Element answerNode = new Element("answer");

            Element textNode = new Element("text");
            textNode.setText(AppDataSingleton.getInstance()
                    .getCustomQuestionList().get(i).getAnswer());
            answerNode.addContent(textNode);

            Element questionIdNode = new Element("questionId");
            questionIdNode.setText(String.valueOf(AppDataSingleton
                    .getInstance().getCustomQuestionList().get(i).getId()));
            answerNode.addContent(questionIdNode);

            rootNode.addContent(answerNode);
        }

        RestConnector.getInstance().httpPostCheckSuccess(new Document(rootNode),
                "answercustomquestions/" + appointmentId);
    }
}