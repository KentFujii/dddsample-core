package se.citerus.dddsample.application.web;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import se.citerus.dddsample.application.web.command.TrackCommand;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.service.TrackingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for tracking cargo. This interface sits immediately on top of the
 * domain layer, unlike the booking interface which has a a remote facade and supporting
 * DTOs in between.
 * <p/>
 * This approach represents the least amount of transfer object overhead, but is
 * also somewhat awkward when working with domain model classes in the view layer,
 * since those classes do not follow the JavaBean conventions for example.
 * <p/>
 * Note that DDD strongly urges you to keep your domain model free from user interface
 * interference and demands, so this approach should be used with caution.
 *
 * @see se.citerus.dddsample.application.web.CargoAdminController
 */
public final class CargoTrackingController extends SimpleFormController {

  private TrackingService trackingService;

  public CargoTrackingController() {
    setCommandClass(TrackCommand.class);
  }

  @Override
  protected ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response,
                                  final Object command, final BindException errors) throws Exception {

    final TrackCommand trackCommand = (TrackCommand) command;
    final String tidStr = trackCommand.getTrackingId();
    final Cargo cargo = trackingService.track(new TrackingId(tidStr));

    final Map<String, Cargo> model = new HashMap<String, Cargo>();
    if (cargo != null) {
      model.put("cargo", cargo);
    } else {
      errors.rejectValue("trackingId", "cargo.unknown_id", new Object[]{trackCommand.getTrackingId()},
        "Unknown tracking id");
    }
    return showForm(request, response, errors, model);
  }

  public void setTrackingService(TrackingService trackingService) {
    this.trackingService = trackingService;
  }
}