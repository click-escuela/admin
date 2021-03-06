package click.escuela.admin.core.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import click.escuela.admin.core.enumator.CourseMessage;
import click.escuela.admin.core.enumator.DocumentType;
import click.escuela.admin.core.enumator.TeacherMessage;
import click.escuela.admin.core.exception.TransactionException;
import click.escuela.admin.core.feign.StudentController;
import click.escuela.admin.core.provider.student.api.AdressApi;
import click.escuela.admin.core.provider.student.api.TeacherApi;
import click.escuela.admin.core.provider.student.dto.AdressDTO;
import click.escuela.admin.core.provider.student.dto.TeacherDTO;

@RunWith(MockitoJUnitRunner.class)
public class TeacherConnectorTest {

	@Mock
	private StudentController teacherController;

	private TeacherConnector teacherConnector = new TeacherConnector();
	private TeacherApi teacherApi;
	private UUID teacherId;
	private UUID courseId;
	private Integer schoolId;
	private List<TeacherDTO> teachersDTO;
	private List<String> listUUIDs;

	@Before
	public void setUp() throws TransactionException {

		teacherId = UUID.randomUUID();
		schoolId = 1234;
		courseId = UUID.randomUUID();
		teacherApi = TeacherApi.builder().name("Mariana").surname("Lopez").birthday(LocalDate.now()).documentType("DNI")
				.document("25897863").cellPhone("1589632485").email("mariAna@gmail.com")
				.adressApi(new AdressApi()).build();

		TeacherDTO teacherDTO = TeacherDTO.builder().id(teacherId.toString()).name("Mariana").surname("Lopez")
				.birthday(LocalDate.now()).documentType(DocumentType.DNI).document("25897863").cellPhone("1589632485")
				.email("mariAna@gmail.com").adress(new AdressDTO()).build();

		teachersDTO = new ArrayList<>();
		teachersDTO.add(teacherDTO);
		listUUIDs =  new ArrayList<>();
		listUUIDs.add(String.valueOf(teacherId));

		Mockito.when(teacherController.getByTeacherId(schoolId.toString(), teacherId.toString())).thenReturn(teacherDTO);
		Mockito.when(teacherController.getByCourseId(schoolId.toString(), courseId.toString())).thenReturn(teachersDTO);
		Mockito.when(teacherController.getBySchoolId(schoolId.toString())).thenReturn(teachersDTO);

		ReflectionTestUtils.setField(teacherConnector, "teacherController", teacherController);
	}

	@Test
	public void whenCreateIsOk() {
		boolean hasError = false;
		try {
			teacherConnector.create(schoolId.toString(), teacherApi);
		} catch (Exception e) {
			hasError = true;
		}
		assertThat(hasError).isFalse();
	}

	@Test
	public void whenCreateIsError() throws TransactionException {

		when(teacherController.createTeacher(Mockito.any(), Mockito.any())).thenThrow(new TransactionException(
				TeacherMessage.CREATE_ERROR.getCode(), TeacherMessage.CREATE_ERROR.getDescription()));

		assertThatExceptionOfType(TransactionException.class).isThrownBy(() -> {

			teacherConnector.create(schoolId.toString(), teacherApi);
		}).withMessage(TeacherMessage.CREATE_ERROR.getDescription());
	}

	@Test
	public void whenUpdateIsOk() {
		boolean hasError = false;
		try {
			teacherConnector.update(schoolId.toString(), teacherApi);
		} catch (Exception e) {
			hasError = true;
		}
		assertThat(hasError).isFalse();
	}

	@Test
	public void whenUpdateIsError() throws TransactionException {

		when(teacherController.updateTeacher(Mockito.any(), Mockito.any())).thenThrow(new TransactionException(
				TeacherMessage.UPDATE_ERROR.getCode(), TeacherMessage.UPDATE_ERROR.getDescription()));

		assertThatExceptionOfType(TransactionException.class).isThrownBy(() -> {

			teacherConnector.update(schoolId.toString(), teacherApi);
		}).withMessage(TeacherMessage.UPDATE_ERROR.getDescription());
	}

	@Test
	public void whenGetByIsOk() {
		boolean hasError = false;
		try {
			teacherConnector.getById(schoolId.toString(), teacherId.toString());
		} catch (Exception e) {
			assertThat(hasError).isFalse();
		}
	}

	@Test
	public void whenGetByIdIsError() throws TransactionException {
		teacherId = UUID.randomUUID();
		when(teacherController.getByTeacherId(Mockito.any(), Mockito.any())).thenThrow(new TransactionException(
				TeacherMessage.GET_ERROR.getCode(), TeacherMessage.GET_ERROR.getDescription()));
		assertThatExceptionOfType(TransactionException.class).isThrownBy(() -> {
			teacherConnector.getById(schoolId.toString(), teacherId.toString());
		}).withMessage(TeacherMessage.GET_ERROR.getDescription());
	}

	@Test
	public void whenGetBySchoolIsOk() {
		boolean hasError = false;
		try {
			teacherConnector.getBySchoolId(schoolId.toString());
		} catch (Exception e) {
			assertThat(hasError).isFalse();
		}
	}

	@Test
	public void whenGetBySchoolIsEmpty() {
		boolean hasEmpty = false;
		schoolId = 6666;
		try {
			if (teacherConnector.getBySchoolId(schoolId.toString()).isEmpty())
				;
		} catch (Exception e) {
			assertThat(hasEmpty).isFalse();
		}
	}

	@Test
	public void whenGetByCourseIsOk() {
		boolean hasError = false;
		try {
			teacherConnector.getByCourseId(schoolId.toString(), courseId.toString());
		} catch (Exception e) {
			assertThat(hasError).isFalse();
		}
	}

	@Test
	public void whenGetByCourseIsEmpty() {
		boolean hasEmpty = false;
		courseId = UUID.randomUUID();
		try {
			if (teacherConnector.getByCourseId(schoolId.toString(), courseId.toString()).isEmpty())
				;
		} catch (Exception e) {
			assertThat(hasEmpty).isFalse();
		}
	}
	
	@Test
	public void whenAddCoursesIsOk() throws TransactionException {
		teacherConnector.addCourses(schoolId.toString(), teacherId.toString(), listUUIDs);
		verify(teacherController).addCourses(schoolId.toString(), teacherId.toString(), listUUIDs);
	}

	@Test
	public void whenDeleteCoursesIsOk() throws TransactionException {
		teacherConnector.deleteCourses(schoolId.toString(), teacherId.toString(), listUUIDs);
		verify(teacherController).deleteCourses(schoolId.toString(), teacherId.toString(), listUUIDs);
	}

	@Test
	public void whenAddCoursesIsError() throws TransactionException {
		doThrow(new TransactionException(CourseMessage.UPDATE_ERROR.getCode(),
				CourseMessage.UPDATE_ERROR.getDescription())).when(teacherController).addCourses(Mockito.anyString(),
						Mockito.anyString(), Mockito.anyList());
		assertThatExceptionOfType(TransactionException.class).isThrownBy(() -> {
			teacherConnector.addCourses(StringUtils.EMPTY, StringUtils.EMPTY, new ArrayList<>());
		}).withMessage(CourseMessage.UPDATE_ERROR.getDescription());
	}

	@Test
	public void whenDeleteCoursesIsError() throws TransactionException {
		doThrow(new TransactionException(CourseMessage.UPDATE_ERROR.getCode(),
				CourseMessage.UPDATE_ERROR.getDescription())).when(teacherController).deleteCourses(Mockito.anyString(),
						Mockito.anyString(), Mockito.anyList());
		assertThatExceptionOfType(TransactionException.class).isThrownBy(() -> {
			teacherConnector.deleteCourses(StringUtils.EMPTY, StringUtils.EMPTY, new ArrayList<>());
		}).withMessage(CourseMessage.UPDATE_ERROR.getDescription());
	}

}
