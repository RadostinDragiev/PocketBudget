package com.pocketbudget.constant;

public class EmailConstants {
//  Properties
    public static final String SMTP_HOST = "mail.smtp.host";
    public static final String SSL_PORT = "mail.smtp.socketFactory.port";
    public static final String SSL_FACTORY_CLASS = "mail.smtp.socketFactory.class";
    public static final String SMTP_AUTHENTICATION = "mail.smtp.auth";
    public static final String SMTP_PORT = "mail.smtp.port";

    public static final String SMTP_HOST_VALUE = "smtp.gmail.com";
    public static final String SSL_PORT_VALUE = "465";
    public static final String SSL_FACTORY_CLASS_VALUE = "javax.net.ssl.SSLSocketFactory";
    public static final String SMTP_AUTHENTICATION_VALUE = "true";
    public static final String SMTP_PORT_VALUE = "465";

//  Headers
    public static final String CONTENT_TYPE = "Content-type";
    public static final String FORMAT = "format";
    public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";

    public static final String CONTENT_TYPE_VALUE = "text/HTML; charset=UTF-8";
    public static final String FORMAT_VALUE = "flowed";
    public static final String CONTENT_TRANSFER_ENCODING_VALUE = "8bit";

//  Other
    public static final String EMAIL_SENDER_NAME = "NoReply-PocketBudget";

    public static final String CONTENT_TYPE_HTML = "text/html";
}
