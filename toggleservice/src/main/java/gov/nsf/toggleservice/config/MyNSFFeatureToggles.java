package gov.nsf.toggleservice.config;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

import gov.nsf.toggleservice.groups.MyNsfFeatureGroup;

/**
 * This is the list of viable feature toggles for MyNSF.
 *
 * Until we back this by a database you will need to update this file (and deploy)
 * every time you need to add/remove a toggle.
 *
 * Created by gareytaylor on 9/16/15.
 */
public enum MyNSFFeatureToggles implements Feature {


  @MyNsfFeatureGroup
  @Label("MyAwardActions - Enables the use of Award Actions")
  myAwardActions,
  
  // Added by MyNSF-awards team , MAM-1742- Award Actions process link
  @MyNsfFeatureGroup
  @Label("awardActionsProcessLink  - Enables the use of Process Link in Award Actions")
  awardActionsProcessLink,

  @EnabledByDefault
  @MyNsfFeatureGroup
  @Label("tooltipster - JavaScript based tool tips")
  tooltipster,

  @MyNsfFeatureGroup
  @Label("adHocReview - Show / Hide adhoc review functionality")
  adHocReview,

  @MyNsfFeatureGroup
  @Label("fundingProgramElement  - Show / Hide functionality for MPM-1129")
  fundingProgramElement,

  @MyNsfFeatureGroup
  @Label("subMeetingModeMPM2754  - Show / Hide functionality for MPM-2754")
  subMeetingModeMPM2754,

  @MyNsfFeatureGroup
  @Label("biLinkMPM896  - Show / Hide link to BI report for MPM-896")
  biLinkMPM896,

  @MyNsfFeatureGroup
  @Label("guestLinkMPM2690MPM2691  - Show / Hide link to Guest Reimbursement Links")
  guestLinkMPM2690MPM2691,

  @MyNsfFeatureGroup
  @Label("locationAlexandriaMPM2563 - Show / Hide link to Meeting Locaiton - Arlington - true is show")
  locationAlexandriaMPM2563,

  @MyNsfFeatureGroup
  @Label("notificationToggle - Enable / Disable new notification/email behavior for meeting service")
  notificationToggle;

  public boolean isActive() {
    return FeatureContext.getFeatureManager().isActive(this);
  }

}
