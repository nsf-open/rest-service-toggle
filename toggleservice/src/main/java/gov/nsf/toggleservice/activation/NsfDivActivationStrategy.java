package gov.nsf.toggleservice.activation;

import gov.nsf.components.rolemanager.model.Identity;
import gov.nsf.components.rolemanager.model.OrgAccess;
import gov.nsf.components.rolemanager.model.UserData;
import gov.nsf.toggleservice.config.MyNSFToggleConfig;
import org.togglz.core.activation.Parameter;
import org.togglz.core.activation.ParameterBuilder;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.spi.ActivationStrategy;
import org.togglz.core.user.FeatureUser;
import org.togglz.core.util.Strings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The MyNSF division activation strategy. This allows us to toggle features
 * based off the user's division.
 */
public class NsfDivActivationStrategy implements ActivationStrategy {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(NsfDivActivationStrategy.class);

    public static final String ID = "division";
    public static final String NAME = "Division strategy";

    public static final String PARAM_DIV_NAME = "code";
    public static final String PARAM_DIV_LABEL = "Division Codes";
    public static final String PARAM_DIV_DESC = "A comma separated list of user division codes for which the feature is active.";

    /**
     *
     * @return returns the Strategy ID
     */
    public String getId() {
        return ID;
    }

    /**
     *
     * @return returns the Strategy Name
     */
    public String getName() {
        return NAME;
    }

    /**
     *
     * @return returns data needed to add custom strategy to Togglz Screen
     */
    public Parameter[] getParameters() {
        return new Parameter[] {
                ParameterBuilder.create(PARAM_DIV_NAME).label(PARAM_DIV_LABEL).description(PARAM_DIV_DESC)
        };
    }

    /**
     * This is a custom activation strategy that determines if the user is part of a division that
     * has been enabled.
     *
     * @param featureState This is what has been configured in the Togglz admin screen.
     * @param user this user object has been overridden to work at NSF.
     * @return
     */
    public boolean isActive(FeatureState featureState, FeatureUser user) {

        if(user!=null && user.getAttribute(MyNSFToggleConfig.USER_DATA_ATTR) != null){
            UserData userData = (UserData)user.getAttribute(MyNSFToggleConfig.USER_DATA_ATTR);

            LOGGER.debug("Division Activation Strategy: " + userData);
            Identity identity = userData.getIdentity();
            if(identity != null) {
                Set<String> userDivisions = parseDivisions(identity);

                if (userDivisions != null && !userDivisions.isEmpty()) {
                    String divisionsAsString = featureState.getParameter(PARAM_DIV_NAME);

                    if (Strings.isNotBlank(divisionsAsString)) {

                        List<String> divList = Strings.splitAndTrim(divisionsAsString, ",");

                        for (String authority : divList) {
                            if (userDivisions.contains(authority)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;

    }

    /**
     * This code was pulled from the meeting service. Need code review to
     * determine reasoning behind logic.
     * @param identity contains NSF identity information, including division access.
     * @return Set of divisions that the user has access to.
     */
    private Set<String> parseDivisions(Identity identity){
        Set<String> setToRemoveDuplicates= new HashSet<String>();
        List<OrgAccess> orgAccess = identity.getAccess();

        for (OrgAccess tempOrgAccess: orgAccess ){
            String orgCode = tempOrgAccess.getOrgCode();
            if(orgCode != null && !"".equals(orgCode)){
                setToRemoveDuplicates.add(orgCode.substring(0, 4)+"0000");
            }
        }

        return  setToRemoveDuplicates;
    }


}
