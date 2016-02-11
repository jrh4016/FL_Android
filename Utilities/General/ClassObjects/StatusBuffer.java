package com.skeds.android.phone.business.Utilities.General.ClassObjects;

import com.skeds.android.phone.business.Utilities.NonfatalException;
import com.skeds.android.phone.business.Utilities.REST.RestConnector;

import org.jdom2.Document;
import org.jdom2.Element;

import java.io.Serializable;
import java.util.LinkedList;

public final class StatusBuffer implements Serializable {

    public final static class PendingStatus {
        public final int apptId;
        public final Element xml;

        public PendingStatus(int apptId, Element xml) {
            this.apptId = apptId;
            this.xml = xml;
        }
    }

    private LinkedList<PendingStatus> queue = null;

    private StatusBuffer() {
    }

    private static StatusBuffer singleton = new StatusBuffer();

    public static StatusBuffer instance() {
        return singleton;
    }

    public synchronized void append(PendingStatus pending) {
        if (queue == null)
            queue = new LinkedList<PendingStatus>();
        queue.addLast(pending);
    }

    public boolean haveQueue() {
        if (queue == null)
            return false;
        if (queue.isEmpty())
            return false;
        return true;
    }

    public void append(int apptid, Element xml) {
        append(new PendingStatus(apptid, xml));
    }

    /**
     * Send all pending items and clear queue
     *
     * @throws NonfatalException
     */
    public void flush() throws NonfatalException {
        while (true) {
            PendingStatus pending;
            synchronized (this) {
                if (queue == null)
                    return;
                if (queue.isEmpty()) {
                    queue = null;
                    return;
                }
                pending = queue.getFirst();
            }
            Document doc = new Document();
            doc.removeContent();
            doc.addContent(pending.xml);
            RestConnector.getInstance().httpPost(doc,
                    "updatetrackabletask/" + pending.apptId);
            synchronized (this) {
                queue.removeFirst();
            }
        }
    }

}