package com.skeds.android.phone.business.Utilities.REST.Objects;

import com.skeds.android.phone.business.Utilities.General.AppDataSingleton;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.ServiceProvider;
import com.skeds.android.phone.business.Utilities.General.ClassObjects.TimeSpan;
import com.skeds.android.phone.business.Utilities.General.UserUtilitiesSingleton;
import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

public class RESTHoursWorkedList {

    /* Gets the logged in technicians hours */
    public static void query(int techId) throws NonfatalException {
        Document document;

        if (techId == UserUtilitiesSingleton.getInstance().user.getId())
            document = RestConnector.getInstance().httpGet(
                    "gethoursworked/" + techId);
        else
            document = RestConnector.getInstance().httpGet(
                    "gethoursworkedfortech/" + techId);

        Element rootNode = document.getRootElement();

        ServiceProvider serviceProvider = new ServiceProvider();

        AppDataSingleton.getInstance().setHoursWorked(serviceProvider);

        Element serviceProviderNode = rootNode
                .getChild("serviceProviders")
                .getChild("serviceProvider");

        if (serviceProviderNode.getAttribute("id") != null)

            AppDataSingleton.getInstance().getHoursWorked().setId(
                    Integer.parseInt(serviceProviderNode
                            .getAttributeValue("id")));
        if (serviceProviderNode.getAttribute("name") != null)
            AppDataSingleton.getInstance().getHoursWorked().setName(
                    serviceProviderNode.getAttributeValue("name"));


        if (serviceProviderNode.getAttribute("lastTimeClockUpdate") != null)
            if (serviceProviderNode.getAttributeValue("lastTimeClockUpdate")
                    .equals("IN"))
                AppDataSingleton.getInstance().getHoursWorked().setClockedIn(true);
            else
                AppDataSingleton.getInstance().getHoursWorked().setClockedIn(false);

        if (serviceProviderNode.getAttribute("lastTimeClockDateTime") != null)
            AppDataSingleton.getInstance().getHoursWorked().setClockInTime(
                    serviceProviderNode
                            .getAttributeValue("lastTimeClockDateTime"));

        // todaysTimeClockRecords
        AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan.clear();
        AppDataSingleton.getInstance().getHoursWorked().todaysTimeClockRecords.timeSpan.clear();

        Element todayClockRecordsNode = serviceProviderNode
                .getChild("todaysTimeClockRecords");
        if (todayClockRecordsNode != null) {
            if (todayClockRecordsNode.getAttributeValue("totalHours") != null) {
                AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked
                        .setTotalHours(todayClockRecordsNode
                                .getAttributeValue("totalHours"));
                AppDataSingleton.getInstance().getHoursWorked().todaysTimeClockRecords
                        .setTotalHours(todayClockRecordsNode
                                .getAttributeValue("totalHours"));

            }

            List<Element> todaySpanList = todayClockRecordsNode
                    .getChildren("timeSpan");

            if (!todaySpanList.isEmpty()) {
                for (int todaySpanIterator = 0; todaySpanIterator < todaySpanList
                        .size(); todaySpanIterator++) {

                    AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan
                            .add(new TimeSpan());
                    AppDataSingleton.getInstance().getHoursWorked().todaysTimeClockRecords.timeSpan
                            .add(new TimeSpan());

                    Element timeSpanNode = (Element) todaySpanList
                            .get(todaySpanIterator);
                    if (timeSpanNode.getChildText("timeWorked") != null) {
                        AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan
                                .get(todaySpanIterator)
                                .setTimeWorked(
                                        timeSpanNode.getChildText("timeWorked"));

                        AppDataSingleton.getInstance().getHoursWorked().todaysTimeClockRecords.timeSpan
                                .get(todaySpanIterator)
                                .setTimeWorked(
                                        timeSpanNode.getChildText("timeWorked"));
                    }

                    if (timeSpanNode.getChildText("fromToString") != null) {
                        AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan
                                .get(todaySpanIterator).setFromTo(
                                timeSpanNode
                                        .getChildText("fromToString"));

                        AppDataSingleton.getInstance().getHoursWorked().todaysTimeClockRecords.timeSpan
                                .get(todaySpanIterator).setFromTo(
                                timeSpanNode
                                        .getChildText("fromToString"));

                    }

                    if (timeSpanNode.getChildText("intervalStatus") != null) {
                        AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan
                                .get(todaySpanIterator)
                                .setStatus(
                                        timeSpanNode.getChildText("intervalStatus"));

                        AppDataSingleton.getInstance().getHoursWorked().todaysTimeClockRecords.timeSpan
                                .get(todaySpanIterator)
                                .setStatus(
                                        timeSpanNode.getChildText("intervalStatus"));
                    }
                }
            }
        }

        // thisWeeksTimeClockRecords
        AppDataSingleton.getInstance().getHoursWorked().thisweekTimeClockRecords.timeSpan.clear();
        Element thisWeekClockNode = serviceProviderNode
                .getChild("thisWeeksTimeClockRecords");
        if (thisWeekClockNode != null) {
            if (thisWeekClockNode.getAttributeValue("totalHours") != null)
                AppDataSingleton.getInstance().getHoursWorked().thisweekTimeClockRecords
                        .setTotalHours(thisWeekClockNode
                                .getAttributeValue("totalHours"));

            List<Element> todaySpanList = thisWeekClockNode
                    .getChildren("timeSpan");

            if (!todaySpanList.isEmpty()) {
                for (int todaySpanIterator = 0; todaySpanIterator < todaySpanList
                        .size(); todaySpanIterator++) {

                    AppDataSingleton.getInstance().getHoursWorked().thisweekTimeClockRecords.timeSpan
                            .add(new TimeSpan());

                    Element timeSpanNode = (Element) todaySpanList
                            .get(todaySpanIterator);
                    if (timeSpanNode.getChildText("timeWorked") != null)
                        AppDataSingleton.getInstance().getHoursWorked().thisweekTimeClockRecords.timeSpan
                                .get(todaySpanIterator)
                                .setTimeWorked(
                                        timeSpanNode.getChildText("timeWorked"));

                    if (timeSpanNode.getChildText("intervalStatus") != null)
                        AppDataSingleton.getInstance().getHoursWorked().thisweekTimeClockRecords.timeSpan
                                .get(todaySpanIterator)
                                .setStatus(
                                        timeSpanNode.getChildText("intervalStatus"));

                    if (timeSpanNode.getChildText("fromToString") != null)
                        AppDataSingleton.getInstance().getHoursWorked().thisweekTimeClockRecords.timeSpan
                                .get(todaySpanIterator).setFromTo(
                                timeSpanNode
                                        .getChildText("fromToString"));
                }
            }
        }
        // lastWeeksTimeClockRecords
        AppDataSingleton.getInstance().getHoursWorked().lastweekTimeClockRecords.timeSpan.clear();
        Element lastWeekClockNode = serviceProviderNode
                .getChild("lastWeeksTimeClockRecords");
        if (lastWeekClockNode != null) {
            if (lastWeekClockNode.getAttributeValue("totalHours") != null)
                AppDataSingleton.getInstance().getHoursWorked().lastweekTimeClockRecords
                        .setTotalHours(lastWeekClockNode
                                .getAttributeValue("totalHours"));

            List<Element> todaySpanList = lastWeekClockNode
                    .getChildren("timeSpan");

            if (!todaySpanList.isEmpty()) {
                for (int todaySpanIterator = 0; todaySpanIterator < todaySpanList
                        .size(); todaySpanIterator++) {

                    AppDataSingleton.getInstance().getHoursWorked().lastweekTimeClockRecords.timeSpan
                            .add(new TimeSpan());

                    Element timeSpanNode = (Element) todaySpanList
                            .get(todaySpanIterator);
                    if (timeSpanNode.getChildText("timeWorked") != null)
                        AppDataSingleton.getInstance().getHoursWorked().lastweekTimeClockRecords.timeSpan
                                .get(todaySpanIterator)
                                .setTimeWorked(
                                        timeSpanNode.getChildText("timeWorked"));

                    if (timeSpanNode.getChildText("fromToString") != null)
                        AppDataSingleton.getInstance().getHoursWorked().lastweekTimeClockRecords.timeSpan
                                .get(todaySpanIterator).setFromTo(
                                timeSpanNode
                                        .getChildText("fromToString"));

                    if (timeSpanNode.getChildText("intervalStatus") != null)
                        AppDataSingleton.getInstance().getHoursWorked().lastweekTimeClockRecords.timeSpan
                                .get(todaySpanIterator)
                                .setStatus(
                                        timeSpanNode.getChildText("intervalStatus"));
                }
            }
        }

        // If "Today" does not exist, just skip over it
        AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan.clear();
        Element todayTimesNode = serviceProviderNode
                .getChild("todaysTimeWorked");
        if (todayTimesNode != null) {
            if (todayTimesNode.getAttributeValue("totalHours") != null)
                AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked
                        .setTotalHours(todayTimesNode
                                .getAttributeValue("totalHours"));

            List<Element> todaySpanList = todayTimesNode
                    .getChildren("timeSpan");

            if (!todaySpanList.isEmpty()) {
                for (int todaySpanIterator = 0; todaySpanIterator < todaySpanList
                        .size(); todaySpanIterator++) {

                    AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan
                            .add(new TimeSpan());

                    Element timeSpanNode = (Element) todaySpanList
                            .get(todaySpanIterator);
                    if (timeSpanNode.getChildText("timeWorked") != null)
                        AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan.get(
                                todaySpanIterator).setTimeWorked(
                                timeSpanNode.getChildText("timeWorked"));
                    if (timeSpanNode.getChildText("customerName") != null)
                        AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan.get(
                                todaySpanIterator).setCustomerName(
                                timeSpanNode.getChildText("customerName"));
                    if (timeSpanNode.getChildText("fromToString") != null)
                        AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan.get(
                                todaySpanIterator).setFromTo(
                                timeSpanNode.getChildText("fromToString"));

                    if (timeSpanNode.getChildText("intervalStatus") != null)
                        AppDataSingleton.getInstance().getHoursWorked().todayTimesWorked.timeSpan
                                .get(todaySpanIterator)
                                .setStatus(
                                        timeSpanNode.getChildText("intervalStatus"));
                }
            }
        }
        // If "This Week" does not exist, just skip over ti as well
        AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan.clear();

        Element thisweekTimesNode = serviceProviderNode
                .getChild("thisWeeksTimeWorked");
        if (thisweekTimesNode != null) {
            if (thisweekTimesNode.getAttributeValue("totalHours") != null)
                AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked
                        .setTotalHours(thisweekTimesNode
                                .getAttributeValue("totalHours"));

            List<Element> thisweekSpanList = thisweekTimesNode
                    .getChildren("timeSpan");

            if (!thisweekSpanList.isEmpty()) {

                for (int thisweekSpanIterator = 0; thisweekSpanIterator < thisweekSpanList
                        .size(); thisweekSpanIterator++) {

                    AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan
                            .add(new TimeSpan());

                    Element timeSpanNode = (Element) thisweekSpanList
                            .get(thisweekSpanIterator);
                    if (timeSpanNode.getChildText("timeWorked") != null)
                        AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan
                                .get(thisweekSpanIterator)
                                .setTimeWorked(
                                        timeSpanNode.getChildText("timeWorked"));
                    if (timeSpanNode.getChildText("customerName") != null)
                        AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan
                                .get(thisweekSpanIterator).setCustomerName(
                                timeSpanNode
                                        .getChildText("customerName"));
                    if (timeSpanNode.getChildText("fromToString") != null)
                        AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan
                                .get(thisweekSpanIterator).setFromTo(
                                timeSpanNode
                                        .getChildText("fromToString"));

                    if (timeSpanNode.getChildText("intervalStatus") != null)
                        AppDataSingleton.getInstance().getHoursWorked().thisweekTimesWorked.timeSpan
                                .get(thisweekSpanIterator)
                                .setStatus(
                                        timeSpanNode.getChildText("intervalStatus"));
                }
            }
        }

        // Finally, if "Last Week" does not exist, skip over it
        AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan.clear();

        Element lastweekTimesNode = serviceProviderNode
                .getChild("lastWeeksTimeWorked");
        if (lastweekTimesNode != null) {
            if (lastweekTimesNode.getAttributeValue("totalHours") != null)
                AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked
                        .setTotalHours(lastweekTimesNode
                                .getAttributeValue("totalHours"));

            List<Element> lastweekSpanList = lastweekTimesNode
                    .getChildren("timeSpan");

            if (!lastweekSpanList.isEmpty()) {
                for (int lastweekSpanIterator = 0; lastweekSpanIterator < lastweekSpanList
                        .size(); lastweekSpanIterator++) {

                    AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan
                            .add(new TimeSpan());

                    Element timeSpanNode = (Element) lastweekSpanList
                            .get(lastweekSpanIterator);
                    if (timeSpanNode.getChildText("timeWorked") != null)
                        AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan
                                .get(lastweekSpanIterator)
                                .setTimeWorked(
                                        timeSpanNode.getChildText("timeWorked"));
                    if (timeSpanNode.getChildText("customerName") != null)
                        AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan
                                .get(lastweekSpanIterator).setCustomerName(
                                timeSpanNode
                                        .getChildText("customerName"));
                    if (timeSpanNode.getChildText("fromToString") != null)
                        AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan
                                .get(lastweekSpanIterator).setFromTo(
                                timeSpanNode
                                        .getChildText("fromToString"));

                    if (timeSpanNode.getChildText("intervalStatus") != null)
                        AppDataSingleton.getInstance().getHoursWorked().lastweekTimesWorked.timeSpan
                                .get(lastweekSpanIterator)
                                .setStatus(
                                        timeSpanNode.getChildText("intervalStatus"));
                }
            }
        }
    }
}