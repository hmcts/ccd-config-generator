package uk.gov.hmcts.ccd.sdk.runtime;

import lombok.Getter;

@Getter
public class CallbackRequest<T> {
  private T caseDetails;
  private T caseDetailsBefore;
}
