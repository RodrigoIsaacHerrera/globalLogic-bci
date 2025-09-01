package org.example.shared;

import java.time.LocalDateTime;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ErrorResponseTest {
	@Test
	public void ErrorResponseTesting() {
		LocalDateTime timestamp = null;
		int code = 123;
		String detail = "abc";
		ErrorResponse expected = new ErrorResponse(null, 123, "abc");
		ErrorResponse actual = new ErrorResponse(timestamp, code, detail);

		assertEquals(expected, actual);
	}
}
