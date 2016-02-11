/**
 * Logger
 */
package com.skeds.android.phone.business.Utilities;

import android.content.Context;
import android.os.Environment;
import android.os.MemoryFile;
import android.text.TextUtils;
import android.util.Base64OutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Logger
 */
public final class Logger {


    private static final long MAX_FILE_SIZE = 1024 * 512;
    // set null if no messages in android system logger should be sent
    private static String TAG = "Skeds";

    private static File logdir;

    // priorites strings
    private final static String PRI_CRASH = "DIE ";
    private final static String PRI_ERR = "ERR ";
    private final static String PRI_WARN = "WAR ";
    private final static String PRI_INFO = "INF ";

    private static Printer printer = null;

    private static File logFile = null;

    public static boolean init(Context context) {

        Logger.logdir = isExternalStorageWritable() ?
                context.getExternalCacheDir()
                : context.getCacheDir();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy___hh-mm");

        String logFileName = "skeds_" + simpleDateFormat.format(new Date()) + ".log";

        logFile = new File(Logger.logdir, logFileName);

        logFile.setWritable(true);

        try {

//            if (!(CONSTANTS.build_type == BUILD_TYPES.PRODUCTION))
//                android.util.Log.i("Skeds", "Write logs into " + logFile);

            FileWriter logwriter = new FileWriter(logFile, !logFileIsFull());

            printer = new Printer(logwriter);

            logwriter.append("\fLOGS INITILIZED\n");

            logwriter.flush();

            return true;
        } catch (IOException e) {
            android.util.Log.e("Skeds",
                    "Could not open " + logFile.getAbsolutePath(), e);
        }
        return false;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private static boolean logFileIsFull() {
        if (logFile.exists())
            return logFile.length() > MAX_FILE_SIZE;

        return true;
    }

    /**
     * Log input XML
     *
     * @param tag logger tag
     * @param in  input stream with XML
     * @param enc input encoding
     * @return reader for specified input stream
     */
    public static InputStreamReader logXml(String tag, InputStream in,
                                           String enc) {
        try {
            return new LogTeeReader(tag, in, enc);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unbelivable", e);
        }
    }

    public static void logXml(String tag, String url, String xmlstr) {

        android.util.Log.d(TAG + ".xml", "[" + tag + "] " + url + "\n"
                + xmlstr);

        if (printer != null) {
            StringBuilder sb = new StringBuilder(xmlstr.length() + url.length()
                    + 80);
            printer.prepareBuffer(sb, PRI_INFO, tag);
            sb.append(xmlstr);
            printer.print(sb);
        }
    }

    public static void err(Throwable e) {
        err(null, e);
    }

    public static void err(String tag, Throwable e) {
        if (e instanceof NonfatalException) {
            NonfatalException nfe = (NonfatalException) e;
            if (nfe.logged)
                return;
            nfe.logged = true;
        }

        android.util.Log.e(TAG, e.getMessage(), e);

        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }

        if (printer != null) {
            StringBuilder sb = printer.prepareBuffer(new StringBuilder(4096 * 2),
                    PRI_ERR, tag);
            printer.printfile(appendStackTrace(sb, e));
        }
    }

    public static void info(String msg) {
        info(TAG, msg);
    }

    public static void info(String tag, String msg) {

        android.util.Log.i(TAG, "[" + tag + "] " + msg);
        if (printer != null) {
            printer.print(PRI_INFO, tag, msg);
        }
    }

    public static void err(String msg) {
        err(TAG, msg);
    }

    public static void err(String tag, String msg) {
        android.util.Log.e(TAG, "[" + tag + "] " + msg);
        if (printer != null)
            printer.print(PRI_ERR, tag, msg);
    }

    public static void warn(String msg) {
        warn(TAG, msg);
    }

    public static void warn(String tag, String msg) {
        android.util.Log.w(TAG, "[" + tag + "] " + msg);
        if (printer != null) {
            printer.print(PRI_WARN, tag, msg);
        }
    }

    public static void debug(String tag, String msg) {
        if (TAG != null) {
            android.util.Log.d(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void debug(String msg) {
        android.util.Log.d(TAG, msg);
    }

    public static void crash(Thread thread, Throwable e) {
        if (printer != null && printer.out != null) {
            StringBuilder sb = printer.prepareBuffer(
                    new StringBuilder(4096 * 2), PRI_CRASH, "CRASHDUMP");
            sb.append("Thread: " + thread.getName());
            printer.printfile(appendStackTrace(sb, e));
        }
    }

    /**
     * Log file printer
     */
    private static final class Printer {
        Writer out;

        /**
         * Create new printer.
         *
         * @param out file (or what else) where log is written to
         */
        Printer(Writer out) {
            this.out = out;
        }

        void printfile(StringBuilder buf) {
            if (out == null)
                return;
            try {
                out.append(buf);
                out.flush();
            } catch (IOException e) {
                out = null;
            }
        }

        void print(String pri, String tag, CharSequence msg) {
            // TODO take builders from object cache
            if (msg==null)return;
            StringBuilder buf = prepareBuffer(new StringBuilder(
                    msg.length() + 80), pri, tag);
            buf.append(msg).append('\n');
            printfile(buf);
        }

        StringBuilder prepareBuffer(StringBuilder buf, String pri, String tag) {
            return buf.append('\f').append("[")
                    .append(Long.toString(System.currentTimeMillis()))
                    .append("] ").append(pri).append(tag).append(": ");
        }

        void print(StringBuilder buf) {
            printfile(buf.append('\n'));
        }
    }

    /**
     * Tee input to log
     */
    private static class LogTeeReader extends InputStreamReader {
        StringBuilder logbuf;
        boolean closed = false;

        LogTeeReader(String tag, InputStream in, String enc)
                throws UnsupportedEncodingException {
            super(in, enc);
            this.logbuf = printer.prepareBuffer(new StringBuilder(4096),
                    PRI_INFO, tag);
        }

        @Override
        public void close() throws IOException {
            if (logbuf != null) {
                android.util.Log.d(TAG + ".xml", logbuf.toString());
                printer.print(logbuf);
                logbuf = null;
            }
            if (!closed) {
                closed = true;
                super.close();
            }
        }

        @Override
        public int read(char[] buf, int offset, int count) throws IOException {
            int rv = super.read(buf, offset, count);
            if (rv > 0)
                logbuf.append(buf, offset, rv);
            return rv;
        }
    }

    /**
     * Append stack trace from specified Throwable to given buffer
     *
     * @param sb dest. buffer (can not be null)
     * @param e  stack trace source (can not be null)
     * @return <code>cb</code>
     */
    private static StringBuilder appendStackTrace(StringBuilder sb, Throwable e) {
        L1:
        //
        while (e != null) {
            sb.append(e.getClass().getCanonicalName()).append(": ")
                    .append(e.getMessage()).append('\n');
            for (StackTraceElement ste : e.getStackTrace()) {
                sb.append("\t@ ").append(ste.getClassName()).append('.')
                        .append(ste.getMethodName());
                String s = ste.getFileName();
                if (s != null)
                    sb.append(" (").append(s).append(':')
                            .append(ste.getLineNumber()).append(')');
                sb.append('\n');
                if (sb.length() > 8192) {
                    sb.append("...\n");
                    break L1;
                }
            }
            e = e.getCause();
        }
        return sb;
    }

    /**
     * Generate Base64-encoded zip from last error log data
     */
    private static class ReportGenerator extends java.io.OutputStream {
        MemoryFile outFile = null;
        ZipOutputStream zipout = null;
        byte buf[] = new byte[8192];
        byte buf1[] = {0};
        int reportLen = 0;

        @Override
        public void write(int b) throws IOException {
            buf1[0] = (byte) b;
            write(buf1, 0, 1);
        }

        @Override
        public void write(byte[] buffer, int off, int len) throws IOException {
            outFile.writeBytes(buffer, off, reportLen, len);
            reportLen += len;
        }

        void dump(File infile, int maxsize) throws IOException {
            if (!infile.canRead())
                return;
            RandomAccessFile in = new RandomAccessFile(infile, "r");
            int skip = (int) in.length() - maxsize;
            if (skip > 0) {
                in.seek(skip);
                int i, len = in.read(buf);
                for (i = 0; i < len; i++)
                    if (buf[i] == '\f')
                        break;
                in.seek(skip + len);
            }
            skip = 0;
            while (true) {
                int l = in.read(buf);
                if (l < 1)
                    break;
                zipout.write(buf, 0, l);
            }
            in.close();
            in = null;
        }

        FileInputStream generateStream() throws IOException {

            OutputStream os = new FileOutputStream(new File(logdir, "skedsLog.zip"));
            ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(os));
            try {
                String filename = "skeds.log";
                if (logFile==null) return null;
                byte[] bytes = getBytesFromFile(logFile);
                ZipEntry entry = new ZipEntry(filename);
                zos.putNextEntry(entry);
                zos.write(bytes);
                zos.closeEntry();
            }catch (Exception ex){
                ex.printStackTrace();
            } finally {
                zos.close();
            }

            FileInputStream fis = new FileInputStream(new File(logdir, "skedsLog.zip"));
            return fis;
        }

        private byte[] getBytesFromFile(File file) throws IOException {
            InputStream is = new FileInputStream(file);

            // Get the size of the file
            long length = file.length();

            // You cannot create an array using a long type.
            // It needs to be an int type.
            // Before converting to an int type, check
            // to ensure that file is not larger than Integer.MAX_VALUE.
            if (length > Integer.MAX_VALUE) {
                // File is too large
            }

            // Create the byte array to hold the data
            byte[] bytes = new byte[(int) length];

            // Read in the bytes
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            // Ensure all the bytes have been read in
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }

            // Close the input stream and return bytes
            is.close();
            return bytes;
        }
    }

    /**
     * Generate error report
     *
     * @param maxsize max raw log size to be reported
     * @return base64-encoded zip of last <code>maxsize</code> bytes of logs
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public static FileInputStream generateReport() throws IOException {
        printer.out.flush();
        return new ReportGenerator().generateStream();
    }

}
