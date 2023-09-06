package org.cardano.foundation.voting.resource;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

// needed by Zalando Problem spring web
// https://www.baeldung.com/problem-spring-web
@ControllerAdvice
public class ExceptionResource implements ProblemHandling, SecurityAdviceTrait {
}
