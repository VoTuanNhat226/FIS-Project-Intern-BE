package com.vtn.Yame.service.camunda;

import com.vtn.Yame.entity.Category;
import com.vtn.Yame.service.CategoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("addToDatabase")
public class AddToDatabaseDelegate implements JavaDelegate {
    @Autowired
    private CategoryService categoryService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String name = (String) delegateExecution.getVariable("name");

        Category newCategory = new Category();
        newCategory.setName(name);

        categoryService.saveCategory(newCategory);

        System.out.println("Category added to database successfully: " + name);
    }
}
