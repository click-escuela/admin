package click.escuela.admin.core.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.junit.MockitoJUnitRunner;

import click.escuela.admin.core.connector.SchoolConnector;
import click.escuela.admin.core.enumator.SchoolMessage;
import click.escuela.admin.core.exception.TransactionException;
import click.escuela.admin.core.provider.student.api.SchoolApi;
import click.escuela.admin.core.service.impl.SchoolServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class SchoolServiceTest {

	@Mock
	private SchoolConnector schoolConnector;

	private SchoolServiceImpl schoolServiceImpl = new SchoolServiceImpl();
	private SchoolApi schoolApi;

	@Before
	public void setUp() throws TransactionException {

		schoolApi = SchoolApi.builder().name("Colegio Nacional").cellPhone("47589869")
				.email("colegionacional@edu.gob.com").adress("Entre Rios 1418").countCourses(10).countStudent(20)
				.build();
		doNothing().when(schoolConnector).create(schoolApi);
		ReflectionTestUtils.setField(schoolServiceImpl, "schoolConnector", schoolConnector);
	}

	@Test
	public void whenCreateIsOk() throws TransactionException {
		schoolServiceImpl.create(schoolApi);
		verify(schoolConnector).create(schoolApi);
	}

	@Test
	public void whenCreateIsError() throws TransactionException {
		doThrow(new TransactionException(SchoolMessage.CREATE_ERROR.getCode(),
				SchoolMessage.CREATE_ERROR.getDescription())).when(schoolConnector).create(Mockito.any());
		assertThatExceptionOfType(TransactionException.class).isThrownBy(() -> {
			schoolServiceImpl.create(Mockito.any());
		}).withMessage(SchoolMessage.CREATE_ERROR.getDescription());
	}

}
