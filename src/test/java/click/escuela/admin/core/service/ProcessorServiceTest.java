package click.escuela.admin.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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
import org.mockito.junit.MockitoJUnitRunner;

import click.escuela.admin.core.connector.ProcessorConnector;
import click.escuela.admin.core.enumator.EducationLevels;
import click.escuela.admin.core.enumator.GenderType;
import click.escuela.admin.core.enumator.ProcessMessage;
import click.escuela.admin.core.exception.ProcessException;
import click.escuela.admin.core.provider.processor.dto.ResponseCreateProcessDTO;
import click.escuela.admin.core.provider.processor.service.impl.ProcessorServiceImpl;
import click.escuela.admin.core.provider.student.api.StudentApiFile;
import click.escuela.admin.core.service.impl.StudentBulkUpload;

@RunWith(MockitoJUnitRunner.class)
public class ProcessorServiceTest {
	
	@Mock
	private ProcessorConnector processorConnector;
	
	@Mock
	private StudentBulkUpload studentBulkUpload;

	private ProcessorServiceImpl processorServiceImpl = new ProcessorServiceImpl();
	private String schoolId = "1234";
	private MockMultipartFile multipart ;
	private File file = new File("EstudiantesTest.xlsx");
	private List<StudentApiFile> students =  new ArrayList<>();
	private String id = UUID.randomUUID().toString();
	private String processId = UUID.randomUUID().toString();

	@Before
	public void setUp() throws Exception  {
		InputStream fileInp = new FileInputStream(file);
		multipart = new MockMultipartFile("EstudiantesTest.xlsx", file.getName(), "application/vnd.ms-excel",fileInp);
		StudentApiFile student =StudentApiFile.builder().name("Tony").surname("Liendro").document("377758269").gender(GenderType.MALE.toString())
				.cellPhone("1523554622").division("3").grade("7").level(EducationLevels.TERCIARIO.toString())
				.email("tony@gmail.com").adressApi(null).parentApi(null).line(1)
				.build();
		students.add(student);		
		//when(studentBulkUpload.readFile(file)).thenReturn(students);

		when(processorConnector.create(Mockito.anyString(),Mockito.anyString(),Mockito.any())).thenReturn(new ResponseCreateProcessDTO());

		ReflectionTestUtils.setField(processorServiceImpl, "processorConnector", processorConnector);
		ReflectionTestUtils.setField(processorServiceImpl, "studentBulkUpload", studentBulkUpload);

	}

	@Test
	public void whenCreateIsOk() throws Exception {
		boolean hasError = false;
		try {
			processorServiceImpl.save(schoolId, multipart);
		} catch (Exception e) {
			hasError = true;
		}
		assertThat(hasError).isFalse();
	}

	@Test
	public void whenCreateIsError() throws ProcessException {
		when(processorConnector.create(Mockito.anyString(),Mockito.anyString(),Mockito.any())).thenThrow(new ProcessException(ProcessMessage.CREATE_ERROR));

		assertThatExceptionOfType(ProcessException.class).isThrownBy(() -> {
			processorServiceImpl.save(schoolId, multipart);
		}).withMessage(ProcessMessage.CREATE_ERROR.getDescription()); 
	}
	
	@Test
	public void whenGetByIdIsOk() throws ProcessException {
		processorServiceImpl.getBySchoolId(id);
		verify(processorConnector).getBySchoolId(id);
	}
	
	@Test
	public void whenFileByIdIsOk() throws ProcessException, IOException {
		processorServiceImpl.getFileById(id, processId);
		verify(processorConnector).getFileById(id, processId);
	}
}
