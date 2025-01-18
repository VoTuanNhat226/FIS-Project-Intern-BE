package com.vtn.Yame.service.camunda;

import com.vtn.Yame.repository.CategoryRepository;
import com.vtn.Yame.service.CategoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("inputValidationDelegate")
public class InputValidationDelegate implements JavaDelegate {
    private static final Logger logger = LoggerFactory.getLogger(InputValidationDelegate.class);

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public InputValidationDelegate(CategoryService categoryService, CategoryRepository categoryRepository) {
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String name = (String) delegateExecution.getVariable("name");

        logger.info("Executing InputValidationDelegate with name: {}", name);

        if(name == null) {
            delegateExecution.setVariable("validInput", false);
            logger.error("Name is required.");
            throw new IllegalArgumentException("Name is required.");
        }

        delegateExecution.setVariable("validInput", true);
        logger.info("Input validation passed. Valid Input is: {}", delegateExecution.getVariable("validInput"));
    }
}
