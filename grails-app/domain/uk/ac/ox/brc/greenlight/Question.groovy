package uk.ac.ox.brc.greenlight

class Question {

    String name
	Boolean optional = false

	// If the response is "no", apply this label
	String labelIfNotYes

    static auditable = true
    static belongsTo = [
            studyForm:ConsentFormTemplate]

    static constraints = {
		labelIfNotYes nullable: true
    }
	static mapping = {
		name type: "text"
	}

}
