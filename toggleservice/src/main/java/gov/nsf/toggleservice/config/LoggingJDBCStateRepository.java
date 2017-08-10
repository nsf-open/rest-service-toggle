package gov.nsf.toggleservice.config;

import gov.nsf.components.authorization.model.UserPrincipal;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.jdbc.JDBCStateRepository;

import javax.sql.DataSource;

/**
 * This is a class designed to output audit information. It will output WHO made WHAT change to WHICH toggle.
 *
 * It overrides the JDBCState Repository.
 *
 * Created by gareytaylor on 7/4/16.
 */
public class LoggingJDBCStateRepository extends JDBCStateRepository {

    private static final Logger LOGGER = Logger.getLogger(LoggingJDBCStateRepository.class);

    public LoggingJDBCStateRepository(DataSource dataSource, String tableName, boolean createTable) {
        super(dataSource, tableName, createTable);
    }

    /**
     * When the feature state is set
     * @param featureState
     */
    @Override
    public void setFeatureState(FeatureState featureState) {

        super.setFeatureState(featureState);

        String logString = String.format("ToggleAdmin: User:[%s] changed toggle [%s] to state [%s].",
                getUserNameFromContext(), featureState.getFeature().name(), (featureState.isEnabled() ? "enabled" : "disabled"));

        LOGGER.info(logString);
    }

    /**
     * Pulls the user name from the security context.
     *
     * @return system user name.
     */
    private String getUserNameFromContext(){
        String sRet = "ANONOMOUS";

        Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (o instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) (o);
            sRet = userPrincipal.getUsername();

        }
        return sRet;
    }
}
