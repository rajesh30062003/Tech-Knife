package com.techknife.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

/**
 * Service responsible for processing HTML email templates using Thymeleaf context rendering.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    @Qualifier("emailTemplateEngine")
    private final SpringTemplateEngine templateEngine;

    /**
     * Renders an HTML email template with given model variables.
     *
     * @param templateName path or name of template (e.g., "email/base-template")
     * @param model        map of dynamic template variables
     * @return rendered HTML string content
     */
    public String renderTemplate(String templateName, Map<String, Object> model) {
        try {
            log.debug("Rendering email template '{}' with model keys: {}", templateName, model != null ? model.keySet() : "null");
            Context context = new Context();
            if (model != null) {
                model.forEach(context::setVariable);
            }
            return templateEngine.process(templateName, context);
        } catch (Exception ex) {
            log.error("Failed to render email template '{}': {}", templateName, ex.getMessage(), ex);
            throw new EmailException("EMAIL_TEMPLATE_ERROR", "Failed to render email template: " + templateName, ex);
        }
    }
}
