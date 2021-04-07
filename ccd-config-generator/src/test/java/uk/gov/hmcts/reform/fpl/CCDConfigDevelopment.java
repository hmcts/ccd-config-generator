package uk.gov.hmcts.reform.fpl;

import uk.gov.hmcts.ccd.sdk.api.Webhook;
import uk.gov.hmcts.ccd.sdk.runtime.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.ccd.sdk.runtime.CallbackRequest;
import uk.gov.hmcts.reform.fpl.enums.State;
import uk.gov.hmcts.reform.fpl.model.CaseData;

public class CCDConfigDevelopment extends CCDConfig {
    @Override
    public void configure() {
        super.configure();
    }

    @Override
    protected String webhookConvention(Webhook webhook, String eventId) {
        return "localhost:5050/" + eventId + "/" + webhook;
    }

    @Override
    protected String environment() {
        return "development";
    }

    public AboutToStartOrSubmitCallbackResponse<CaseData, State> OnFoo(
        CallbackRequest<CaseData> request) {
        throw new RuntimeException();
    }

}
