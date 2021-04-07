package uk.gov.hmcts.ccd.sdk.runtime;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AboutToStartOrSubmitCallbackResponse<T, S> {
  private Map<String, Object> data;
  private S  state;
  private List<String> errors;
  private List<String> warnings;
}
