package org.studyeasy.SpringBlog.util.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class emailDetails {
    private String recipient;
    private String subject;
    private String msgbody;

    public emailDetails(@Email(message = "Invalid email") @NotEmpty(message = "Email missing") String email, String resetMessage, String resettoken, String resetPassordDemo) {
    }
}

