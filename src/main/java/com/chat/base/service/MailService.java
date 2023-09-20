package com.chat.base.service;

import com.chat.base.bean.dto.ToEmailDto;

import javax.servlet.http.HttpServletRequest;

public interface MailService {

    public void checkMail(String receiveEmail, String subject, String emailMsg);
    public  Boolean sendTextMail(String receiveEmail, String subject, String emailMsg);

    public Boolean sendEmailVerCode(ToEmailDto toEmail);
}
