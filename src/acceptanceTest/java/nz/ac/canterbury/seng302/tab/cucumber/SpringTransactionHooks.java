package nz.ac.canterbury.seng302.tab.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import nz.ac.canterbury.seng302.tab.entity.UserEntity;
import nz.ac.canterbury.seng302.tab.service.UserService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class SpringTransactionHooks implements BeanFactoryAware {

    private BeanFactory beanFactory;
    private TransactionStatus transactionStatus;


    /**
     * Use this to set up the BeanFactory
     * <a href=https://stackoverflow.com/questions/68859845/rollback-transactions-after-each-cucumber-scenarios-with-spring-boot><a/>
     * @return void
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }


    /**
     * Gets the result of the h2 DB before the any tests are run
     * <a href=https://stackoverflow.com/questions/68859845/rollback-transactions-after-each-cucumber-scenarios-with-spring-boot><a/>
     * @return void
     */
    @Before(value = "@txn", order = 100)
    public void startTransaction() {
        transactionStatus = beanFactory.getBean(PlatformTransactionManager.class)
                .getTransaction(new DefaultTransactionDefinition());
    }


    /**
     * Resets the h2 DB after a test has run
     * <a href=https://stackoverflow.com/questions/68859845/rollback-transactions-after-each-cucumber-scenarios-with-spring-boot><a/>
     * @return void
     */
    @After(value = "@txn", order = 100)
    public void rollBackTransaction() {
        beanFactory.getBean(PlatformTransactionManager.class)
                .rollback(transactionStatus);
    }

}