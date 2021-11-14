package click.escuela.admin.core.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.mockito.junit.MockitoJUnitRunner;

import click.escuela.admin.core.enumator.EmailMessage;
import click.escuela.admin.core.enumator.ProcessMessage;
import click.escuela.admin.core.exception.EmailException;
import click.escuela.admin.core.exception.ProcessException;
import click.escuela.admin.core.exception.TransactionException;
import click.escuela.admin.core.feign.ProcessorController;
import click.escuela.admin.core.provider.student.dto.FileError;

@RunWith(MockitoJUnitRunner.class)
public class ProcessorConnectorTest {

	@Mock
	private ProcessorController processorController;

	private ProcessorConnector processorConnector = new ProcessorConnector();
	private String email;
	private String userName;
	private String password;
	private String schoolId;
	private String processId = UUID.randomUUID().toString();
	private MultipartFile multipart ;
	private File file = new File("EstudiantesTest.xlsx");
	
	@Before
	public void setUp() throws TransactionException, IOException {
		
		InputStream fileInp = new FileInputStream(file);
		multipart = new MockMultipartFile("EstudiantesTest.xlsx", file.getName(), "application/vnd.ms-excel",fileInp);
		email = "colegionacional@edu.gob.com";
		userName = "Tony2022";
		password = "Tony2022";
		schoolId = UUID.randomUUID().toString();

		ReflectionTestUtils.setField(processorConnector, "processorController", processorController);
	}

	@Test
	public void whenSendEmailOk() throws EmailException {
		processorConnector.sendEmail(password, userName, email, schoolId);
		verify(processorController).sendEmail(password, userName, email, schoolId);
	}

	@Test
	public void whenSendEmailIsError() throws EmailException {
		doThrow(new EmailException(EmailMessage.SEND_ERROR)).when(processorController).sendEmail(password, userName, email, schoolId);
		assertThatExceptionOfType(EmailException.class).isThrownBy(() -> {
			processorConnector.sendEmail(password, userName, email, schoolId);
		}).withMessage(EmailMessage.SEND_ERROR.getDescription());
	}
	
	@Test
	public void whenCreateIsOk() throws Exception {
		boolean hasError = false;
		try {
			processorConnector.create(multipart.getOriginalFilename(),schoolId, multipart);
		} catch (Exception e) {
			hasError = true;
		}
		assertThat(hasError).isFalse();
	}
	
	@Test
	public void whenUpdateIsOk() throws ProcessException {
		List<FileError> errors = new ArrayList<>();
		String status = "SUCCESS";
		processorConnector.update(processId, schoolId, errors, status);
		verify(processorController).update(schoolId, processId, errors, status);
	}

	@Test
	public void whenCreateIsError() throws ProcessException {
		when(processorController.saveAndRead(Mockito.any(),Mockito.anyString(),Mockito.anyString())).thenThrow(new ProcessException(ProcessMessage.CREATE_ERROR));

		assertThatExceptionOfType(ProcessException.class).isThrownBy(() -> {
			processorConnector.create(multipart.getOriginalFilename(),schoolId, multipart);
		}).withMessage(ProcessMessage.CREATE_ERROR.getDescription()); 
	}
	
	@Test
	public void whenGetByIdIsOk() throws ProcessException {
		processorConnector.getBySchoolId(schoolId);
		verify(processorController).getBySchoolId(schoolId);
	}
	
	@Test
	public void whenFileByIdIsOk() throws ProcessException, IOException {
		processorConnector.getFileById(schoolId, processId);
		verify(processorController).getFileById(schoolId, processId);
	}

}
