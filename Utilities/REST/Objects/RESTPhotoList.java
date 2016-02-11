package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.UserPhoto;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTPhotoList {

    /* Gets photos, either for an appointment, a customer, or equipment */
    public static void query(long appointmentId, long equipmentId)
            throws NonfatalException {
        int custId = AppDataSingleton.getInstance().getCustomer().getId();
        Element rootNode = new Element("getPhotos");

        if (appointmentId != 0) {
            Element appointmentIdNode = new Element("appointmentId");
            appointmentIdNode.setText(String.valueOf(appointmentId));
            rootNode.addContent(appointmentIdNode);
        }

        if (equipmentId != 0) {
            Element equipmentIdNode = new Element("equipmentId");
            equipmentIdNode.setText(String.valueOf(equipmentId));
            rootNode.addContent(equipmentIdNode);
        }

        Document document = RestConnector.getInstance().httpPost(
                new Document(rootNode), "getphotos/" + custId);
        AppDataSingleton.getInstance().getPhotoList().clear();

        Element el = document.getRootElement().getChild("photos");
        if (el == null)
            return;
        List<Element> photosList = el.getChildren("photo");

        for (int i = 0; i < photosList.size(); i++) {
            AppDataSingleton.getInstance().getPhotoList().add(new UserPhoto());

            Element photoNode = (Element) photosList.get(i);

            if (photoNode.getAttributeValue("id") != null)
                AppDataSingleton.getInstance().getPhotoList()
                        .get(i)
                        .setId(Integer.parseInt(photoNode
                                .getAttributeValue("id")));

            if (photoNode.getChild("url") != null)
                AppDataSingleton.getInstance().getPhotoList().get(i)
                        .setURL(photoNode.getChild("url").getText());

            if (photoNode.getChild("tagText") != null)
                AppDataSingleton.getInstance().getPhotoList().get(i)
                        .setTagText(photoNode.getChild("tagText").getText());

            if (photoNode.getChild("dateTaken") != null)
                AppDataSingleton.getInstance().getPhotoList().get(i)
                        .setDate(photoNode.getChild("dateTaken").getText());

            if (photoNode.getChild("photographer") != null)
                AppDataSingleton.getInstance().getPhotoList()
                        .get(i)
                        .setPhotographer(
                                photoNode.getChild("photographer").getText());

            if (photoNode.getChild("appointmentId") != null)
                AppDataSingleton.getInstance().getPhotoList()
                        .get(i)
                        .setAppointmentId(
                                Integer.parseInt(photoNode.getChild(
                                        "appointmentId").getText()));

            if (photoNode.getChild("equipmentId") != null)
                AppDataSingleton.getInstance().getPhotoList()
                        .get(i)
                        .setEquipmentId(
                                Integer.parseInt(photoNode.getChild(
                                        "equipmentId").getText()));

        }
    }
}