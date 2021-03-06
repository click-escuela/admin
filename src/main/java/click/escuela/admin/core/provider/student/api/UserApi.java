package click.escuela.admin.core.provider.student.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserApi {

	private String name;
	private String surname;
	private String email;
	private String schoolId;
	private String role;
	private String userId;
	
	@JsonProperty(value = "password", required = false)
	private String password;
	
	@JsonProperty(value = "userName", required = false)
	private String userName;
}
