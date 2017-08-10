package gov.nsf.toggleservice.controller;

import gov.nsf.aspect.Loggable;
import gov.nsf.common.ember.model.EmberModel;
import gov.nsf.exception.CommonUtilException;
import gov.nsf.logging.LogLevel;
import gov.nsf.toggleservice.api.model.Toggles;
import gov.nsf.toggleservice.config.MyNSFFeatureToggles;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.PatternSyntaxException;

/**
 * ToggleController - This controller exposes toggles as a RESTful service.
 *
 * @author gtaylor
 *
 */
@RestController
public class ToggleController  {
	
	private static final Logger LOGGER = Logger.getLogger(ToggleController.class);
	
	
	/**
	 * returns a list of feature toggles the logged in user
	 *
	 * @return featureToggle object list. simple list of feature toggles
	 * @throws CommonUtilException
	 */
 	@RequestMapping(value = {"/auth/toggles/","/toggles/"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Loggable(LogLevel.DEBUG)
	public EmberModel getFeatureToggles() throws CommonUtilException,PatternSyntaxException,Exception  {

		Toggles toggles = new Toggles();

		for(MyNSFFeatureToggles f : MyNSFFeatureToggles.values()){
			toggles.getFeatures().put(f.name(), f.isActive());
		}

		EmberModel.Builder<Toggles> emberModel = new EmberModel.Builder<Toggles>(toggles);
			return emberModel.build();
	}


}
