package click.escuela.admin.core.provider.processor.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import click.escuela.admin.core.connector.ProcessorConnector;
import click.escuela.admin.core.exception.TransactionException;
import click.escuela.admin.core.provider.processor.dto.ProcessDTO;
import click.escuela.admin.core.provider.processor.dto.ResponseCreateProcessDTO;
import click.escuela.admin.core.provider.student.dto.FileError;
import click.escuela.admin.core.service.impl.StudentBulkUpload;

@Service
public class ProcessorServiceImpl {

	private static final String SUCESS = "SUCESS";
	private static final String ERROR = "ERROR";
	
	@Autowired
	private StudentBulkUpload studentBulkUpload;
	
	@Autowired
	private ProcessorConnector processorConnector;
	
	@Async
	public void save(String schoolId, MultipartFile excel) throws TransactionException {
		
		ResponseCreateProcessDTO response = processorConnector.create(excel.getOriginalFilename(), schoolId, excel);
		List<FileError> errors = studentBulkUpload.upload(schoolId, response.getStudents()); 
		processorConnector.update(response.getProcessId(), schoolId,
				errors, errors.isEmpty() ? SUCESS : ERROR );
	}
	
	public List<ProcessDTO> getBySchoolId(String schoolId){
		return processorConnector.getBySchoolId(schoolId);
	}
	
	public byte[] getFileById(String schoolId, String processId) throws IOException {
		return processorConnector.getFileById(schoolId, processId);
	}
	
}
