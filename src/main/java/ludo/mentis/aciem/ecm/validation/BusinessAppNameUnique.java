package ludo.mentis.aciem.ecm.validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import ludo.mentis.aciem.ecm.service.BusinessAppService;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import static java.lang.annotation.ElementType.*;


/**
 * Validate that the name value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = BusinessAppNameUnique.BusinessAppNameUniqueValidator.class
)
public @interface BusinessAppNameUnique {

    String message() default "{Exists.businessApp.name}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class BusinessAppNameUniqueValidator implements ConstraintValidator<BusinessAppNameUnique, String> {

        private final BusinessAppService businessAppService;
        private final HttpServletRequest request;

        public BusinessAppNameUniqueValidator(final BusinessAppService businessAppService,
                                              final HttpServletRequest request) {
            this.businessAppService = businessAppService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equalsIgnoreCase(businessAppService.get(Long.parseLong(currentId)).getName())) {
                // value hasn't changed
                return true;
            }
            return !businessAppService.nameExists(value);
        }

    }

}
