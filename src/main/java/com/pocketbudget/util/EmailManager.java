package com.pocketbudget.util;

import java.io.File;

public interface EmailManager {
    void sendSSLEmail(String toEmail, String subject, File body);
}
