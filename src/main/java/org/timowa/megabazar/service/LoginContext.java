package org.timowa.megabazar.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.timowa.megabazar.database.entity.User;

@Getter
@Setter
@Component
public class LoginContext {

    private User loginUser;
}
