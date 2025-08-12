package ludo.mentis.aciem.ecm.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1315866885481746561L;

	public NotFoundException() {
        super();
    }

    public NotFoundException(final String message) {
        super(message);
    }

}
