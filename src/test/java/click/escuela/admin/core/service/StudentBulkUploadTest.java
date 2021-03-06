package click.escuela.admin.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import click.escuela.admin.core.enumator.EducationLevels;
import click.escuela.admin.core.enumator.GenderType;
import click.escuela.admin.core.exception.TransactionException;
import click.escuela.admin.core.provider.student.api.StudentApiFile;
import click.escuela.admin.core.provider.student.dto.FileError;
import click.escuela.admin.core.provider.student.service.impl.StudentServiceImpl;
import click.escuela.admin.core.service.impl.StudentBulkUpload;

@RunWith(MockitoJUnitRunner.class)
public class StudentBulkUploadTest {

	private List<StudentApiFile> students =  new ArrayList<>();
	private List<FileError> errors = new ArrayList<>();
	private File file;
	private String schoolId = "1234";
	private StudentApiFile student;
	
	private StudentBulkUpload studentBulkUpload;
	
	@Mock
	private StudentServiceImpl studentService;
	
	@Before
	public void setUp() throws Exception  {
		
		studentBulkUpload = new StudentBulkUpload();
		student =StudentApiFile.builder().name("Tony").surname("Liendro").document("377758269").gender(GenderType.MALE.toString())
				.cellPhone("1523554622").division("3").grade("7").level(EducationLevels.TERCIARIO.toString())
				.email("tony@gmail.com").adressApi(null).parentApi(null).line(1)
				.build();
		students.add(student);		
		List<String> errorsList = new ArrayList<>();
		String error = "Ya existe el estudiante";
		errorsList.add(error);
		FileError fileError = FileError.builder().line(student.getLine()).errors(errorsList).build();
		errors.add(fileError);
		
		file = new File("EstudiantesTest.xlsx");
		
		ReflectionTestUtils.setField(studentBulkUpload, "studentService", studentService);
	}
	
	/*@Test
	public void whenReadFileIsOk() throws Exception {
		
		assertThat(studentBulkUpload.readFile(file)).isNotEmpty();
	}*/
	
	@Test
	public void whenUploadIsOk() throws Exception {		
		TransactionException e = mock(TransactionException.class);
		when(e.getMessage()).thenReturn("Ya existe el estudiante");
		doThrow(e).when(studentService).create(schoolId, student);
		assertThat(studentBulkUpload.upload(schoolId, students)).isNotEmpty();
	}
	/*
	@Test
	public void whenWriteErrorsIsOk() throws Exception {		
		assertThat(studentBulkUpload.writeErrors(errors, file)).isNotEmpty();
	}*/
}
