package uk.ac.ox.brc.greenlight

import grails.transaction.Transactional

@Transactional
class ConsentEvaluationService {

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

			   if(!response.question.optional){
			   	result = ConsentStatus.NON_CONSENT
			   }
		   }
       }

       return result
    }
}

enum ConsentStatus
{
    FULL_CONSENT,
    NON_CONSENT,
    CONSENT_WITH_RESTRICTIONS
}