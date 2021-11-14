package click.escuela.admin.core.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import click.escuela.admin.core.enumator.ProcessMessage;
import click.escuela.admin.core.exception.TransactionException;
import click.escuela.admin.core.provider.processor.dto.ProcessDTO;
import click.escuela.admin.core.provider.processor.service.impl.ProcessorServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping(path = "/school/{schoolId}/processor")
public class ProcessorController {

	@Autowired
	private ProcessorServiceImpl processorService;

	@Operation(summary = "Save Excel", responses = {
			@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json")) })
	@PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<ProcessMessage> create(@PathVariable("schoolId") String schoolId,
			@RequestParam("file") MultipartFile excel)
			throws TransactionException{
		
		processorService.save(schoolId, excel);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(ProcessMessage.CREATE_OK);
	}
	
	@Operation(summary = "Get by schoolId", responses = {
			@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessDTO.class))) })
	@GetMapping(value = "", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<List<ProcessDTO>> getProcess(@Parameter(name = "School id", required = true) @PathVariable("schoolId") String schoolId) {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(processorService.getBySchoolId(schoolId));
	}
	
	@Operation(summary = "Get file by Id", responses = {
			@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcessDTO.class))) })
	@GetMapping(value = "/{processId}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<byte[]> getFileById(@Parameter(name = "School id", required = true) @PathVariable("schoolId") String schoolId,
			@PathVariable("processId") String processId) throws IOException {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(processorService.getFileById(schoolId, processId));
	}
	

}
