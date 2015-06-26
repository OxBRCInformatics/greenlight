package uk.ac.ox.brc.greenlight

class Question {

    String name
	Boolean optional = false
	Response.ResponseValue defaultResponse

	static hasMany = [validResponses: Response.ResponseValue]
	// If the response is "no", apply this label
	String labelIfNotYes

//    static auditable = true
    static belongsTo = [
            studyForm:ConsentFormTemplate]

    static constraints = {
		labelIfNotYes nullable: true
		defaultResponse nullable: true
    }
	static mapping = {
		name type: "text"

		validResponses joinTable: [name: 'question_valid_responses',
							column: 'validResponse',
							key: 'question_id']

		validResponses sort: 'value', order: 'desc'
	}

}
