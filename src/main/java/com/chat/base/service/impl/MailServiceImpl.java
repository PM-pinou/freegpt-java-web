package com.chat.base.service.impl;


import com.chat.base.bean.dto.ToEmailDto;
import com.chat.base.service.MailService;
import com.chat.base.utils.CacheUtil;
import com.chat.base.utils.VerCodeGenerateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;


import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class MailServiceImpl implements MailService {
    /**
     * 注入邮件工具类
     */
    @Resource
    private JavaMailSenderImpl javaMailSender;

    @Resource
    private MailService mailService;

    @Value("${spring.mail.username}")
    private String sendMailer;

    /**
     * 检测邮件信息类
     * @param receiveEmail 接收者
     * @param subject  主题
     * @param emailMsg 内容
     */
    public void checkMail(String receiveEmail, String subject, String emailMsg){
        //  StringUtils 需要引入  commons-lang3 依赖
        //  可以用 receiveEmail == null || receiveEmail.isEmpty() 来代替
        if(StringUtils.isEmpty(receiveEmail)) {
            throw new RuntimeException("邮件收件人不能为空");
        }
        if(StringUtils.isEmpty(subject)) {
            throw new RuntimeException("邮件主题不能为空");
        }
        if(StringUtils.isEmpty(emailMsg)) {
            throw new RuntimeException("邮件内容不能为空");
        }
    }
    
    /**
     * 发送纯文本邮件
     * @param receiveEmail 接收者
     * @param subject  主题
     * @param emailMsg 内容
     */
    public Boolean sendTextMail(String receiveEmail, String subject, String emailMsg) {
        // 参数检查
        checkMail(receiveEmail, subject, emailMsg);
        try {
            // true 代表支持复杂的类型
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(), true);
            // 邮件发件人
            mimeMessageHelper.setFrom(sendMailer);
            // 邮件收件人
            mimeMessageHelper.setTo(receiveEmail.split(","));
            // 邮件主题
            mimeMessageHelper.setSubject(subject);
            // 邮件内容
            mimeMessageHelper.setText(emailMsg);
            // 邮件发送时间
            mimeMessageHelper.setSentDate(new Date());
    
            // 发送邮件
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            System.out.println("发送邮件成功: " + sendMailer + "-->" + receiveEmail);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("发送邮件失败: " + e.getMessage());
            return false;
        }
    }

    public Boolean sendEmailVerCode(ToEmailDto toEmail) {
        if(toEmail == null || toEmail.getTos() == null ) {
            return false;
        }
        toEmail.setSubject("你本次的验证码是");
        // 获取验证码
        String verCode = VerCodeGenerateUtil.generateVerCode();
        String content = "尊敬的xxx,您好:\n"
                + "\n本次请求的邮件验证码为:" + verCode + ",本验证码 5 分钟内效，请及时输入。（请勿泄露此验证码）\n"
                + "\n如非本人操作，请忽略该邮件。\n(这是一封通过自动发送的邮件，请不要直接回复）";
        toEmail.setContent(content);

        Boolean check = mailService.sendTextMail(toEmail.getTos(), toEmail.getSubject(), toEmail.getContent());
        CacheUtil.putVerification(toEmail.getTos(),verCode);
        return check;

    }
}
