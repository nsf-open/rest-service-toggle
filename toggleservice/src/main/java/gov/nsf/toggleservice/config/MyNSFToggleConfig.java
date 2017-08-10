package gov.nsf.toggleservice.config;

import gov.nsf.components.authorization.model.UserPrincipal;
import gov.nsf.components.rolemanager.model.Identity;
import gov.nsf.components.rolemanager.model.UserData;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Component;
import org.togglz.core.Feature;
import org.togglz.core.manager.TogglzConfig;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.cache.CachingStateRepository;
import org.togglz.core.repository.jdbc.JDBCStateRepository;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.user.SimpleFeatureUser;
import org.togglz.core.user.UserProvider;
import org.togglz.servlet.util.HttpServletRequestHolder;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

/**
 * Custom ToggleConfig class for MyNSF. This will back the Toggle Config with a database.
 * This is needed so we can run multiple instances of the Toggle Service in parallel, using
 * the database to keep the configuration in sync.
 *
 * Created by gareytaylor on 9/16/15.
 */
@Component
public class MyNSFToggleConfig implements TogglzConfig {

    private static final String DB_JNDI = "jdbc/toglSvc/DBODataSource";

    private static final String TABLE_NAME = "dbo.appl_fetr_rtme_cfg";

    public static final String USER_DATA_ATTR = "userData";

    public static final String TOGGLE_ROLE = "MyNSF System Administrator";

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(MyNSFToggleConfig.class);

    private DataSource dataSource;

    private StateRepository stateRepository;

    @Override
    public Class<? extends Feature> getFeatureClass() {
        return MyNSFFeatureToggles.class;
    }

    /**
     * Backs the State Repository with an internal database.
     *
     * Additionally we have wrapped the JDBCStateRepository with a LoggingStateRepository so every toggle
     * change is recorded.
     *
     * Finally, we have setup a cache on the state repository to improve performance.
     *
     * @return
     */
    public StateRepository getStateRepository() {
        if( stateRepository == null) {
            try {
                StateRepository jdbcStateRepository = new LoggingJDBCStateRepository(getDataSource(), TABLE_NAME, false);

                // cache the repo to save on db calls.
                stateRepository = new CachingStateRepository(jdbcStateRepository, 10000);

            }
            catch (Exception e) {
                throw new IllegalStateException("Error creating jdbcStateRepository", e);
            }
        }

        return stateRepository;

    }

    /**
     * Initializes the datasource if it's null. returns otherwise.
     *
     * @return
     * @throws NamingException
     */
    private DataSource getDataSource() throws NamingException {
        if(this.dataSource == null){
            InitialContext c;
            c = new InitialContext();
            dataSource = (DataSource) c.lookup(DB_JNDI);
        }
        return dataSource;
    }

    /**
     * This is where we pull the user information from the security context and map it to the
     * internal Togglz User.
     *
     * This is what allows us to target who gets what toggle.
     *
     * @return UserProvider
     */
    public UserProvider getUserProvider() {

        return new UserProvider() {

            @Override
            public FeatureUser getCurrentUser() {

                HttpServletRequest request = HttpServletRequestHolder.get();
                if(request == null) {
                    throw new IllegalStateException("Could not get request from HttpServletRequestHolder. Did you configure the TogglzFilter correctly?");
                } else {

                    UserPrincipal userPrincipal = null;
                    UserData userData = null;
                    Identity identity = null;
                    Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    SecurityContextHolderAwareRequestWrapper contextHolderAwareRequestWrapper = new SecurityContextHolderAwareRequestWrapper(request,null);

                    // pull the identity from the security context.
                    if (o instanceof UserPrincipal) {
                        userPrincipal = (UserPrincipal) (o);
                        userData = userPrincipal.getUserData();
                        identity = userData.getIdentity();
                    }
                    // determine if the user is Admin
                    if (identity!=null && StringUtils.isNotBlank(identity.getId()))  {
                        LOGGER.debug("toggle identity not null: " + userData);

                        boolean featureAdmin = contextHolderAwareRequestWrapper.isUserInRole(TOGGLE_ROLE);
                        return new SimpleFeatureUser(identity.getId(), featureAdmin).setAttribute(USER_DATA_ATTR, userData);
                    } else {
                        LOGGER.debug("WARNING: toggle identity is null: ");
                        return  null;
                    }
                }

            }
        };
    }
}
