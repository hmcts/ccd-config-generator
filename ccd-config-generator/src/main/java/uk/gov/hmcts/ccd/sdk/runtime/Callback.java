package uk.gov.hmcts.ccd.sdk.runtime;

@FunctionalInterface
public interface Callback<T, S> {

  AboutToStartOrSubmitCallbackResponse<T, S> OnFoo(CallbackRequest<T> request);

}
