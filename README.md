# Validation Framework

## Overview

The application implements a robust validation framework that provides detailed, localized error messages for invalid input. The framework leverages Spring's validation capabilities along with externalized message properties to ensure consistent validation across the application.

## Key Features

- **Externalized Validation Messages**: All validation messages are stored in external property files, making them easy to update without code changes.
- **Internationalization Support**: Validation messages can be localized to support multiple languages.
- **Detailed Error Responses**: When validation fails, the API returns detailed error messages for each invalid field.
- **Custom Validation Annotations**: Support for custom validation rules with consistent messaging.

## Configuration

### Message Source Configuration

Validation messages are loaded from an external directory:

```java
@Configuration
public class MessageSourceConfig {

    @Value("${validation.messages.path}")
    private String messagesPath;
    
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("file:" + messagesPath + "/default");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(60); // Reload messages every 60 seconds
        return messageSource;
    }
    
    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }
}
```

### Application Properties

Configure the path to your message files in `application.properties`:

```properties
validation.messages.path=/opt/config/messages
validation.messages.reload-seconds=60
```

## Message Files Structure

Create message properties files in the configured external directory:

```
C:/Source-Code/messages/
├── default.properties       # Default application messages
├── validation.properties    # Validation-specific messages
```

### Example Message File Content

```properties
# User validation messages
user.username.not.blank=Username is required
user.username.size=Username must be between {min} and {max} characters
user.email.not.blank=Email is required
user.email.invalid=Please provide a valid email address
user.age.not.null=Age is required
user.age.min=You must be at least {value} years old
user.username.unique=This username is already taken
```

## Using Validation in DTOs

Apply validation annotations with message keys in your DTOs:

```java
@Data
public class UserDTO {
    @NotBlank(message = "{user.username.not.blank}")
    @Size(min = 4, max = 50, message = "{user.username.size}")
    @UniqueUsername
    private String username;
    
    @NotBlank(message = "{user.email.not.blank}")
    @Email(message = "{user.email.invalid}")
    private String email;
    
    @NotNull(message = "{user.age.not.null}")
    @Min(value = 18, message = "{user.age.min}")
    private Integer age;
}
```

## Error Response Format

When validation fails, the API returns a JSON response with detailed error messages:

```json
{
    "username": "Username must be between 4 and 50 characters",
    "email": "Please provide a valid email address",
    "age": "You must be at least 18 years old"
}
```

## Internationalization

To get validation messages:

```
POST /api/user/create
```

## Custom Validation

Create custom validation annotations with message keys:

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Documented
public @interface UniqueUsername {
    String message() default "{user.username.unique}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

Implement the validator logic:

```java
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return true; // Let @NotNull handle this
        }
        return !userService.usernameExists(username);
    }
}
```

## Testing Validation

You can test validation using tools like Postman or curl:

```bash
curl -X POST http://localhost:8080/api/user/create \
  -H "Content-Type: application/json" \
  -d '{"username":"a","email":"not-an-email","age":15}'
```

## Adding New Validation Messages

To add new validation messages:

1. Add the message key and text to the appropriate properties file
2. Use the message key in your validation annotations with the format `{message.key}`
3. Restart the application if `reload-seconds` is set to a value greater than 0

## Troubleshooting

If validation messages are not appearing correctly:

1. Verify the message files exist in the configured path
2. Check that the message keys in annotations match those in the properties files
3. Ensure the validator factory bean is properly configured with the message source
4. Check application logs for any errors related to loading message files
