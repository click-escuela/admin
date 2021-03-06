package click.escuela.admin.core.provider.student.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import click.escuela.admin.core.exception.TransactionException;
import click.escuela.admin.core.provider.processor.service.impl.SecurityServiceImpl;
import click.escuela.admin.core.provider.student.api.StudentApi;
import click.escuela.admin.core.provider.student.api.UserApi;
import click.escuela.admin.core.provider.student.connector.StudentConnector;
import click.escuela.admin.core.provider.student.dto.ParentDTO;
import click.escuela.admin.core.provider.student.dto.StudentDTO;

@Service
public class StudentServiceImpl {

	@Autowired
	private StudentConnector studentConnector;
	
	@Autowired
	private SecurityServiceImpl securityServiceImpl;
	
	@Autowired
	private EmailServiceImpl emailServiceImpl;

	public List<StudentDTO> getBySchool(String schoolId, Boolean fullDetail) throws TransactionException {
		return studentConnector.getBySchool(schoolId, fullDetail);
	}

	public StudentDTO getById(String schoolId, String studentId, Boolean fullDetail) throws TransactionException {
		return studentConnector.getById(schoolId, studentId, fullDetail);
	}

	public List<StudentDTO> getByCourse(String schoolId, String courseId, Boolean fullDetail)
			throws TransactionException {
		return studentConnector.getByCourse(schoolId, courseId, fullDetail);
	}

	public void create(String schoolId, StudentApi studentApi) throws TransactionException {
		StudentDTO studentDTO = studentConnector.create(schoolId, studentApi);
		UserApi userStudentApi = securityServiceImpl.saveUser(studentToUser(schoolId, studentDTO));
		if(!Objects.isNull(userStudentApi)) {
			emailServiceImpl.sendEmail(userStudentApi.getPassword(), userStudentApi.getUserName(), userStudentApi.getEmail(), schoolId);
		}
		UserApi userParentApi = securityServiceImpl.saveUser(parentToUser(schoolId, studentDTO.getParent()));
		if(!Objects.isNull(userParentApi)) {
			emailServiceImpl.sendEmail(userParentApi.getPassword(), userParentApi.getUserName(), userParentApi.getEmail(), schoolId);
		}
	}

	private UserApi studentToUser(String schoolId, StudentDTO studentDTO) {
		return UserApi.builder().name(studentDTO.getName()).surname(studentDTO.getSurname()).
				email(studentDTO.getEmail()).schoolId(schoolId).role("STUDENT").userId(studentDTO.getId()).build();
	}
	
	private UserApi parentToUser(String schoolId, ParentDTO parentDTO) {
		return UserApi.builder().name(parentDTO.getName()).surname(parentDTO.getSurname()).
				email(parentDTO.getEmail()).schoolId(schoolId).role("PARENT").userId(parentDTO.getId()).build();
	}

	public void update(String schoolId, StudentApi studentApi) throws TransactionException {
		studentConnector.update(schoolId, studentApi);
	}

}
