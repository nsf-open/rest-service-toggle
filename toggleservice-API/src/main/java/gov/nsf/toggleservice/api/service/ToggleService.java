package gov.nsf.toggleservice.api.service;


import gov.nsf.toggleservice.api.model.Toggles;

/**
 * ToggleService - Service Class to serve /awardService request
 *
 * @author Vinaya Nayak
 *
 */
public interface ToggleService {

	public Toggles getFeatureToggles();

	public Boolean toggleIsActive(String toggleName);
}
