package gov.nsf.toggleservice.common.util;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.togglz.console.RequestEvent;
import org.togglz.console.model.FeatureModel;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.metadata.FeatureMetaData;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.spi.ActivationStrategy;
import org.togglz.core.user.FeatureUser;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by gareytaylor on 7/2/16.
 */
public class ToggleTransactionLogAspect {

    private static final Logger LOGGER = Logger.getLogger(ToggleTransactionLogAspect.class);

    public void logToggleTransaction(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        RequestEvent event = (RequestEvent) args[0];

        FeatureManager featureManager = event.getFeatureManager();
        HttpServletRequest request = event.getRequest();

        // identify the feature
        Feature feature = null;
        String featureAsString = request.getParameter("f");
        for (Feature f : featureManager.getFeatures()) {
            if (f.name().equals(featureAsString)) {
                feature = f;
            }
        }
        if (feature != null) {

            FeatureMetaData metadata = featureManager.getMetaData(feature);
            List<ActivationStrategy> impls = featureManager.getActivationStrategies();
            FeatureModel featureModel = new FeatureModel(feature, metadata, impls);

            // POST requests for this feature
            if ("POST".equals(request.getMethod())) {
                // no validation errors
                if (featureModel.isValid()) {

                    FeatureUser user = featureManager.getCurrentFeatureUser();
                    FeatureState state = featureModel.toFeatureState();

                    String logString = String.format("ToggleAdmin: User:[%s] changed toggle [%s] to state [%s].",
                            user.getName(), featureModel.getName(), (state.isEnabled() ? "enabled" : "disabled"));

                    LOGGER.info(logString);

                }
            }


        }


    }
}
