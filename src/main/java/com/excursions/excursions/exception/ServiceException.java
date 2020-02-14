package com.excursions.excursions.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.excursions.excursions.log.message.ServiceLogMessages.SERVICE_LOG_EXCEPTION;

@Slf4j
public class ServiceException extends RuntimeException {
    public static final String SERVICE_EXCEPTION_SERVICE_NAME_DEFAULT_VALUE = "service not set";

    public static final String SERVICE_EXCEPTION_WRONG_ENTITY_FORMAT_STRING = "save/update entity with wrong params: %s";
    public static final String SERVICE_EXCEPTION_EXIST_ENTITY_FORMAT_STRING = "save/update exist entity";
    public static final String SERVICE_EXCEPTION_NO_ENTITY_WITH_ID_FORMAT_STRING = "no entity with id %s";

    public static final String SERVICE_EXCEPTION_WRONG_INPUT_ARGS = "%s - wrong input args";

    public static final String SERVICE_EXCEPTION_WRONG_RESPONSE = "%s - wrong response: %s";

    @Getter
    private String serviceName = SERVICE_EXCEPTION_SERVICE_NAME_DEFAULT_VALUE;

    private ServiceException(String serviceName, String message) {
        super(message);
        if(serviceName != null) {
            this.serviceName = serviceName;
        }
        log.error(SERVICE_LOG_EXCEPTION, serviceName, message);
    }

    public static ServiceException serviceExceptionNoEntityWithId(String service, Long id) {
        return new ServiceException(
                service,
                String.format(SERVICE_EXCEPTION_NO_ENTITY_WITH_ID_FORMAT_STRING, id)
        );
    }

    public static ServiceException serviceExceptionWrongEntity(String service, String message) {
        return new ServiceException(
                service,
                String.format(SERVICE_EXCEPTION_WRONG_ENTITY_FORMAT_STRING, message)
        );
    }

    public static ServiceException serviceExceptionExistEntity(String service) {
        return new ServiceException(
                service,
                SERVICE_EXCEPTION_EXIST_ENTITY_FORMAT_STRING
        );
    }

    public static ServiceException serviceExceptionWrongInputArgs(String service, String methodInfo) {
        return new ServiceException(
                service,
                String.format(SERVICE_EXCEPTION_WRONG_INPUT_ARGS, methodInfo)
        );
    }

    public static ServiceException serviceExceptionWrongResponse(String service, String methodInfo, String response) {
        return new ServiceException(
                service,
                String.format(SERVICE_EXCEPTION_WRONG_RESPONSE, methodInfo, response)
        );
    }
}
