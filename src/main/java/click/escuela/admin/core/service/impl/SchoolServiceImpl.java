package click.escuela.admin.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import click.escuela.admin.core.connector.SchoolConnector;
import click.escuela.admin.core.exception.TransactionException;
import click.escuela.admin.core.provider.student.api.SchoolApi;

@Service
public class SchoolServiceImpl {

	@Autowired
	private SchoolConnector schoolConnector;

	public void create(SchoolApi schoolApi) throws TransactionException {
		schoolConnector.create(schoolApi);
	}
}
