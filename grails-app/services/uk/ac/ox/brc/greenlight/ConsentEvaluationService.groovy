package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional

@Transactional
class ConsentEvaluationService {

	/**
	 * Get the overall consent status for the passed form. *Note* that
	 * labels/restrictions aren't included in this.
	 * @param consentForm The form to evaluate
	 * @return the ConsentStatus object for the supplied form
	 */
    ConsentStatus getConsentStatus(ConsentForm consentForm) {

        //if consent doesn't exists, then run away
        if(consentForm == null){
            return ConsentStatus.NON_CONSENT
        }

        //if consent doesn't contain any responses, then run away
        if(consentForm.responses == null || consentForm.responses.isEmpty()){
            return ConsentStatus.NON_CONSENT
        }

       ConsentStatus result = ConsentStatus.FULL_CONSENT

       consentForm.responses.eachWithIndex { response, index ->
		   // If we see a non-YES value
		   if(response.answer != Response.ResponseValue.YES){

			   // If it's optional + has a label -> CONSENT_WITH_LABELS
			   // If it's optional + no label -> FULL_CONSENT
			   // If it's not optional -> NON_CONSENT
			   if (!response.question.optional) {
				   result = ConsentStatus.NON_CONSENT
			   } else if (response.question.labelIfNotYes != null) {
				   result = ConsentStatus.CONSENT_WITH_LABELS
			   }
		   }
       }
       return result
    }

	/**
	 * Get the consent labels for a given form.
	 * Note that this doesn't return the consent status.
	 * @see getConsentStatus
	 * @param consentForm the form to evaluate
	 * @return the list of labels
	 */
	List<String> getConsentLabels(ConsentForm consentForm){
		def labels = []
		consentForm.responses.eachWithIndex { response, index ->
			String label = response.question.labelIfNotYes
			// If we see a non-YES value
			if(response.answer != Response.ResponseValue.YES && label != null){
				// Only add the label if it's not there
				if(!labels.contains(label)){
					labels.push(label)
				}
			}
		}
		return labels
	}
}

enum ConsentStatus
{
    FULL_CONSENT("Full consent"),
    NON_CONSENT("No consent"),
    CONSENT_WITH_LABELS("Consent with restrictions")

	private final String label

	ConsentStatus(String label){
		this.label = label
	}
}