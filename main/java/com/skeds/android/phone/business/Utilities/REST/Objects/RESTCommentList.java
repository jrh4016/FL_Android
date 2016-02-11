package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.Comment;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTCommentList {

    /* List of all comments for an appointment */
    public static void query(int appointmentId) throws NonfatalException {
        Document document = RestConnector.getInstance().httpGet(
                "getcomments/" + appointmentId);
        AppDataSingleton.getInstance().getAppointment().getCommentList().clear();

        Element appointmentNode = document.getRootElement().getChild("appointment");
        if (appointmentNode == null)
            return;
        List<Element> commentsList = appointmentNode.getChildren("comments");

        if (!commentsList.isEmpty()) {
            Element commentsNode = (Element) commentsList.get(0);
            List<Element> commentList = commentsNode.getChildren("comment");

            for (int commentIterator = 0; commentIterator < commentList.size(); commentIterator++) {
                AppDataSingleton.getInstance().getAppointment().getCommentList().add(new Comment());

                Element commentNode = (Element) commentList
                        .get(commentIterator);

                // Comment ID
                if (commentNode.getAttributeValue("id") != null)
                    AppDataSingleton.getInstance().getAppointment().getCommentList().get(commentIterator)
                            .setId(Integer.parseInt(commentNode
                                    .getAttributeValue("id")));

                // Comment Text
                if (commentNode.getChildText("text") != null)
                    AppDataSingleton.getInstance().getAppointment().getCommentList().get(commentIterator)
                            .setText(commentNode.getChildText("text"));

                // Hop into the "poster" section
                List<Element> posterList = commentNode.getChildren("poster");
                Element poster = (Element) posterList.get(0);

                // Poster Name
                if (poster.getChildText("posterName") != null)
                    AppDataSingleton.getInstance().getAppointment().getCommentList().get(commentIterator)
                            .setPosterName(poster.getChildText("posterName"));

                // Poster TimeDate
                if (poster.getChildText("postDateTime") != null)
                    AppDataSingleton.getInstance().getAppointment().getCommentList().get(commentIterator)
                            .setPosterDateTime(
                                    poster.getChildText("postDateTime"));

                // Poster Email
                if (poster.getChildText("email") != null)
                    AppDataSingleton.getInstance().getAppointment().getCommentList().get(commentIterator)
                            .setPosterEmail(poster.getChildText("email"));
            }
        }
    }
}